package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.json.simple.parser.ParseException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.Plot;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class ScatterPlotTask extends AbstractTask {

	final CyServiceRegistrar sr;
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<String> yCol;

	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol;

	@Tunable (description="Open in plot editor?")
	public boolean editor;
	
	@Tunable (description="Convert X axis values to logarithmic")
	public boolean xValLog;
	@Tunable (description="Convert Y axis values to logarithmic")
	public boolean yValLog;

	// Command interface for non-network plots
	@Tunable (description="JSON formatted string of point names", context="nogui")
	public String names = null;

	@Tunable (description="JSON formatted string of x values", context="nogui")
	public String xValues = null;

	@Tunable (description="JSON formatted string of y values", context="nogui")
	public String yValues = null;

	@ContainsTunables
	public CommandTunables commandTunables = null;

	@Tunable (description="JSON formatted string of z values", context="nogui")
	public String zValues = null;

	@Tunable (description="Color scale", context="nogui", dependsOn="zValues!=null")
	public ListSingleSelection<String> colorscale;

	@Tunable (description="Color scale label", context="nogui", dependsOn="zValues!=null")
	public String scaleLabel = null;

	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;

	public ScatterPlotTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		if (netView != null) {
			network = netView.getModel();
			table = network.getDefaultNodeTable();
			columns = table.getColumns();
			editor=true;
			xValLog=false;
			yValLog=false;

			List<String> headers = ModelUtils.getColOptions(columns, "num");

			List<String> names = ModelUtils.getColOptions(columns, "string");

			xCol = new ListSingleSelection<>(headers);
			yCol = new ListSingleSelection<>(headers);
			nameCol = new ListSingleSelection<>(names);
		} else {
			xCol = null;
			yCol = null;
			nameCol = null;
		}
		commandTunables = new CommandTunables();
		colorscale = new ListSingleSelection<String>("Blues","Earth","Electric","Greens","Greys","Hot","Jet","Picnic","Portland","Reds","Viridis");
		colorscale.setSelectedValue("Viridis");
	}

	/**
	 * Generate the variables necessary to create a scatter plot in plotly with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		Map<String, String> xTraceMap;
		Map<String, String> yTraceMap;
		Map<String, String> zTraceMap = null;
		Map<String, String> nameMap;
		String idColumn = null;
		CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
		 
		CyColumn yColumn=table.getColumn(ModelUtils.getTunableSelection(yCol));
		if (xValues == null && yValues == null) {
			xTraceMap = new HashMap<>();
			yTraceMap = new HashMap<>();
			nameMap = new HashMap<>();

			 xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
			 yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));

			xTraceMap.put("trace",ModelUtils.colToArray(xColumn));

			yTraceMap.put("trace",ModelUtils.colToArray(yColumn));

			if (commandTunables.xLabel == null)
				commandTunables.xLabel = xColumn.getName();

			if (commandTunables.yLabel == null)
				commandTunables.yLabel = yColumn.getName();

			if (nameCol != null)
				idColumn = ModelUtils.getTunableSelection(nameCol);

		} else {
			try {
				// System.out.println("xValues");
				xTraceMap = JSONUtils.getMap(xValues);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'xValues' input: "+pe+": "+pe.getPosition());
				return;
			}

			try {
				// System.out.println("yValues");
				yTraceMap = JSONUtils.getMap(yValues);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'yValues' input: "+pe+": "+pe.getPosition());
				return;
			}

			if (zValues != null) {
				try {
					// System.out.println("zValues");
					zTraceMap = JSONUtils.getMap(zValues);
				} catch (ParseException pe) {
					monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'yValues' input: "+pe+": "+pe.getPosition());
					return;
				}
			}

		}

		if (names == null) {
			CyColumn nameColumn = table.getColumn(idColumn);
			String nameArray = ModelUtils.colToArray(nameColumn);
			nameMap = new LinkedHashMap<>();
			for (String key: xTraceMap.keySet()) {
				nameMap.put(key, nameArray);
			}
		} else {
			// First, determine if we've got a JSON string or not
			try {
				nameMap = JSONUtils.getMap(names);
			} catch (ParseException pe) {
				nameMap = new HashMap<>();
				// Maybe not?
				String nameArr = JSONUtils.csvToJSONArray(names);
				for (String key: xTraceMap.keySet()) {
					nameMap.put(key, nameArr);
				}
			}
		}
		
		if (xValLog) {
			commandTunables.xLabel = "Log("+commandTunables.xLabel+")";
			
				xTraceMap.put("trace",ModelUtils.colToArrayLog(xColumn));
			
			
		}

		if (yValLog) {
			
			commandTunables.yLabel = "Log("+commandTunables.yLabel+")";
			
				yTraceMap.put("trace",ModelUtils.colToArrayLog(yColumn));
			
		}
		Plot plot = new Plot("scatter", xTraceMap, yTraceMap, zTraceMap, nameMap, 
				             commandTunables.selectionString, 
				             idColumn, commandTunables.title, commandTunables.xLabel, 
				             commandTunables.yLabel, "markers", null, null, 
				             colorscale.getSelectedValue(),
				             scaleLabel, editor, commandTunables.id, 
				             commandTunables.id+":ScatterPlot");

		ModelUtils.openCyBrowser(sr, plot.getHTML(), commandTunables.title, commandTunables.id+":ScatterPlot", true);
		if (netView != null)
			ModelUtils.addPlot(netView.getModel(), plot);
		else
			ModelUtils.addPlot(null, plot); // Command?
	}
}
