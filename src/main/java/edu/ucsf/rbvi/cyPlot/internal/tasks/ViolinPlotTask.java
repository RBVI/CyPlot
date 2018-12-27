package edu.ucsf.rbvi.cyPlot.internal.tasks;

	import javax.swing.JFrame;

	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.concurrent.Callable;

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

	import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
	import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

	import org.cytoscape.model.CyColumn;
	import org.cytoscape.model.CyNetwork;
	import org.cytoscape.model.CyTable;

	public class ViolinPlotTask extends AbstractTask {
		
		final CyServiceRegistrar sr;

		@Tunable (description="Columns to include")
		public ListMultipleSelection<String> cols;

    @Tunable (description="Name selection column")
    public ListSingleSelection<String> nameCol;

		@Tunable (description="Open in plot editor?")
		public ListSingleSelection<String> editorCol;
		
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
			network = netView.getModel();
			table = network.getDefaultNodeTable();
			columns = table.getColumns();
			editor = true;

			List<String> headers = ModelUtils.getColOptions(columns, "num");
			List<String> names = ModelUtils.getColOptions(columns, "string");

			cols = new ListMultipleSelection<>(headers);
			nameCol = new ListSingleSelection(names);
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
			
			Map<String, String> traceMap = new HashMap<>();
			for (String col: ModelUtils.getTunableSelections(cols)) {
				String array = ModelUtils.colToArray(table.getColumn(col));
				traceMap.put(col, array);
			}
			CyColumn nameColumn = table.getColumn("shared name");
			
			String nameArray = ModelUtils.colToArray(nameColumn);

			String editorSelection = ModelUtils.getTunableSelection(editorCol);

			if(editorSelection.equals("Yes")) {
				editor = true; //open the graph in the editor
			}else {
				editor = false; //don't open the graph in the editor
			}

			String html = JSUtils.getViolinPlot(traceMap, ModelUtils.getTunableSelection(nameCol), nameArray, editor);
			Map<String, Object> args = new HashMap();
			args.put("text", html);
			args.put("title", "Plot");
			
			TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
			sTM.execute(ti);
		}
}
