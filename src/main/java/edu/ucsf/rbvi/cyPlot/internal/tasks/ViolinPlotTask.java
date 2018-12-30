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
	import org.cytoscape.work.util.ListMultipleSelection;

	import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
	import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
	import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

	import org.cytoscape.model.CyColumn;
	import org.cytoscape.model.CyNetwork;
	import org.cytoscape.model.CyTable;

	public class ViolinPlotTask extends AbstractTask {

		final CyServiceRegistrar sr;

		@Tunable (description="Columns to include")
		public ListMultipleSelection<String> cols = null;

    @Tunable (description="Name selection column")
    public ListSingleSelection<String> nameCol = null;

		@Tunable (description="Open in plot editor?")
		public ListSingleSelection<String> editorCol;

		// Command interface for non-network plots
		@Tunable (description="JSON formatted string of point names", context="nogui")
		public String names = null;

		@Tunable (description="JSON formatted data string", context="nogui")
		public String data = null;

		@Tunable (description="Selection string", context="nogui")
		public String selectionString = null;

		@Tunable (description="Plot title", context="nogui")
		public String title = null;

		@Tunable (description="X Axis Label", context="nogui")
		public String xlabel = null;

		@Tunable (description="Y Axis Label", context="nogui")
		public String ylabel = null;

		public CyApplicationManager appManager;
		public CyNetworkView netView;
		public CyNetwork network;
		public CyTable table;
		public Collection<CyColumn> columns;
		public boolean editor;

		public ViolinPlotTask(final CyServiceRegistrar sr) {
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

				cols = new ListMultipleSelection<>(headers);
				nameCol = new ListSingleSelection(names);
			}
			editor = true;
			editorCol = new ListSingleSelection("Yes", "No");
		}

		/**
		 * Generate the variables necessary to create a violin plot in plotly with the cytoscape 
		 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
		 *
		 * @param monitor the TaskMonitor required for this method by the parent 
		 * AbstractTask class
		 */
		public void run(TaskMonitor monitor) { 
			TaskManager sTM = sr.getService(TaskManager.class);
		    //AvailableCommands ac = sr.getService(AvailableCommands.class);
			CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);

			Map<String, String> traceMap;
			Map<String, String> nameMap;
			// First, we need to determine where we're getting our data
			if (data == null) {
				traceMap = new HashMap<>();
				for (String col: ModelUtils.getTunableSelections(cols)) {
					String array = ModelUtils.colToArray(table.getColumn(col));
					traceMap.put(col, array);
				}
			} else {
				try {
					traceMap = JSONUtils.getMap(data);
				} catch (ParseException pe) {
					monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to parse 'data' input: "+pe+": "+pe.getPosition());
					return;
				}
			}

			String nameArray;
			if (names == null) {
				CyColumn nameColumn = table.getColumn("shared name");
				nameArray = ModelUtils.colToArray(nameColumn);
				nameMap = new HashMap<>();
				for (String key: traceMap.keySet()) {
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
					for (String key: traceMap.keySet()) {
						nameMap.put(key, nameArr);
					}
				}
			}

			String editorSelection = ModelUtils.getTunableSelection(editorCol);

			if(editorSelection.equals("Yes")) {
				editor = true; //open the graph in the editor
			}else {
				editor = false; //don't open the graph in the editor
			}

			String idColumn = null;
			if (nameCol != null)
				idColumn = ModelUtils.getTunableSelection(nameCol);

			String html = JSUtils.getViolinPlot(traceMap, selectionString, idColumn, nameMap, 
			                                    title, xlabel, ylabel, editor);
			Map<String, Object> args = new HashMap();
			args.put("text", html);
			args.put("title", title);

			TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
			sTM.execute(ti);
		}
}
