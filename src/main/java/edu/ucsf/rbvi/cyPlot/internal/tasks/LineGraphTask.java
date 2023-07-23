package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.json.simple.parser.ParseException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class LineGraphTask extends AbstractTask {

	final CyServiceRegistrar sr;
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<String> yCol;

	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol;

	@Tunable (description="Open in plot editor?")
	public boolean editor;

	// Command interface for non-network plots
	@Tunable (description="JSON formatted string of point names", context="nogui")
	public String names = null;

	@Tunable (description="JSON formatted string of x values", context="nogui")
	public String xValues = null;

	@Tunable (description="JSON formatted string of y values", context="nogui")
	public String yValues = null;

	@Tunable (description="Selection string", context="nogui")
	public String selectionString = null;

	@Tunable (description="Plot title", context="nogui")
	public String title = null;

	@Tunable (description="X Axis Label", context="nogui")
	public String xLabel = null;

	@Tunable (description="Y Axis Label", context="nogui")
	public String yLabel = null;
	

	@Tunable (description="Convert X axis values to logarithmic")
	public boolean xValLog;
	@Tunable (description="Convert Y axis values to logarithmic")
	public boolean yValLog;


	// @ContainsTunables
	// public CommandTunables commandTunables = null;

	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;

	public LineGraphTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		if (netView != null) {
			network = netView.getModel();
			table = network.getDefaultNodeTable();
			columns = table.getColumns();
			editor = true;

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
	}

	/**
	 * Generate the variables necessary to create a scatter plot in plotly with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);

		Map<String, String> xTraceMap;
		Map<String, String> yTraceMap;
		Map<String, String> nameMap;
		String idColumn = null;
		CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
		CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
		if (xCol != null && yCol != null) {
			xTraceMap = new HashMap<>();
			yTraceMap = new HashMap<>();
			nameMap = new HashMap<>();

//			CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
//			CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
			CyColumn nameColumn = table.getColumn(ModelUtils.getTunableSelection(nameCol));

			xTraceMap.put("trace",ModelUtils.colToArray(xColumn));
			
			yTraceMap.put("trace",ModelUtils.colToArray(yColumn));

			if (xLabel == null)
				xLabel = xColumn.getName();

			if (yLabel == null)
				yLabel = yColumn.getName();

			if (nameCol != null)
				idColumn = ModelUtils.getTunableSelection(nameCol);

		} else {
			try {
				xTraceMap = JSONUtils.getMap(xValues);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'xValues' input: "+pe+": "+pe.getPosition());
				return;
			}

			try {
				yTraceMap = JSONUtils.getMap(yValues);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'yValues' input: "+pe+": "+pe.getPosition());
				return;
			}
		}

		if (names == null) {
			CyColumn nameColumn = table.getColumn("shared name");
			String nameArray = ModelUtils.colToArray(nameColumn);
			nameMap = new HashMap<>();
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
			xLabel = "Log("+xLabel+")";
			
				xTraceMap.put("trace",ModelUtils.colToArrayLog(xColumn));
			
			
		}

		if (yValLog) {
			
			yLabel = "Log("+yLabel+")";
			
				yTraceMap.put("trace",ModelUtils.colToArrayLog(yColumn));
			
		}
		

		String html = JSUtils.getXYPlot("scatter", xTraceMap, yTraceMap, null, nameMap, 
		                                selectionString, idColumn, 
		                                title, xLabel, yLabel, "lines", null, null, null, null, editor);

		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", title);

		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
}
