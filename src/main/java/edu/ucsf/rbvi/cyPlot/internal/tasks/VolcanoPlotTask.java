package edu.ucsf.rbvi.cyPlot.internal.tasks;

import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.simple.parser.ParseException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class VolcanoPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	@Tunable (description="Fold change column")
	public ListSingleSelection<String> fcCol = null;

	@Tunable (description="p-value column")
	public ListSingleSelection<String> pValueCol = null;
	
	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol = null;
	
	@Tunable (description="Open in plot editor?")
	public boolean editor;

	// Command interface for non-network plots
	@Tunable (description="JSON formatted string of point names", context="nogui")
	public String names = null;

	@Tunable (description="JSON formatted string of fold changes", context="nogui")
	public String fcData = null;

	@Tunable (description="JSON formatted string of pValues", context="nogui")
	public String pValueData = null;

	@Tunable (description="Selection string", context="nogui")
	public String selectionString = null;

	@Tunable (description="Plot title", context="nogui")
	public String title = null;

	@Tunable (description="X Axis Label", context="nogui")
	public String xLabel = null;

	@Tunable (description="Y Axis Label", context="nogui")
	public String yLabel = null;

	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;
	
	public VolcanoPlotTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		if (netView != null) {
			network = netView.getModel();
			table = network.getDefaultNodeTable();
			columns = table.getColumns();
			List<String> headers = ModelUtils.getColOptions(columns, "num");
			List<String> names = ModelUtils.getColOptions(columns, "string");
			fcCol = new ListSingleSelection<>(headers);
			pValueCol = new ListSingleSelection<>(headers);
			nameCol = new ListSingleSelection<>(names);
		}

		editor = true;
	}

	/**
	 * Generate the variables necessary to create a volcano plot in plotly with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);

		Map<String, String> fcTraceMap;
		Map<String, String> pvTraceMap;
		Map<String, String> nameMap;
		String idColumn = null;

		// First, we need to determine where we're getting our data
		if (fcData == null) {
			fcTraceMap = new HashMap<>();
			pvTraceMap = new HashMap<>();
			nameMap = new HashMap<>();

			CyColumn fcColumn = table.getColumn(ModelUtils.getTunableSelection(fcCol));
			CyColumn pvColumn = table.getColumn(ModelUtils.getTunableSelection(pValueCol));

			fcTraceMap.put("trace",ModelUtils.colToArray(fcColumn));

			pvTraceMap.put("trace",ModelUtils.colToArrayNegLog(pvColumn));

			if (xLabel == null)
				xLabel = fcColumn.getName();

			if (yLabel == null) {
				yLabel = pvColumn.getName();
				yLabel = "-Log("+yLabel+")";
			}

			if (title == null)
				title = "Volcano Plot for "+xLabel+" vs. "+yLabel;

			if (nameCol != null)
				idColumn = ModelUtils.getTunableSelection(nameCol);

		} else {
			try {
				fcTraceMap = JSONUtils.getMap(fcData);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'fcData' input: "+pe+": "+pe.getPosition());
				return;
			}

			try {
				pvTraceMap = JSONUtils.getMapNegLog(pValueData);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'pValueData' input: "+pe+": "+pe.getPosition());
				return;
			}
		}

		if (names == null) {
			CyColumn nameColumn = table.getColumn("shared name");
			String nameArray = ModelUtils.colToArray(nameColumn);
			nameMap = new HashMap<>();
			for (String key: fcTraceMap.keySet()) {
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
				for (String key: fcTraceMap.keySet()) {
					nameMap.put(key, nameArr);
				}
			}
		}

		if (title == null)
			title = "Cytoscape Volcano Plot";

		String html = JSUtils.getXYPlot("scatter", fcTraceMap, pvTraceMap, null, 
		                                nameMap, selectionString, idColumn, 
		                                title, xLabel, yLabel, "markers", null, null, null, null, editor,false,false);

		Map<String, Object> args = new HashMap();
		args.put("text", html);
		args.put("title", title);

		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
}
