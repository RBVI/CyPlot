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
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskIterator;
	import org.cytoscape.work.TaskManager;
	import org.cytoscape.work.TaskMonitor;
	import org.cytoscape.work.Tunable;
	import org.cytoscape.work.util.ListSingleSelection;

	import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
	import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

	import org.cytoscape.model.CyColumn;
	import org.cytoscape.model.CyNetwork;
	import org.cytoscape.model.CyTable;

	public class DotPlotTask extends AbstractTask {
		
		final CyServiceRegistrar sr;
		@Tunable (description="X-axis column")
		public ListSingleSelection<String> xCol;

		@Tunable (description="Y-axis column")
		public ListSingleSelection<String> yCol;
		
		@Tunable (description="Name selection column")
		public ListSingleSelection<String> nameCol;
		
		@Tunable (description="Open in plot editor?")
		public boolean editor;
		
		public CyApplicationManager appManager;
		public CyNetworkView netView;
		public CyNetwork network;
		public CyTable table;
		public Collection<CyColumn> columns;
		
		public DotPlotTask(final CyServiceRegistrar sr) {
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

			xCol = new ListSingleSelection<>(headers);
			yCol = new ListSingleSelection<>(headers);
			nameCol = new ListSingleSelection<>(names);
		}

		/**
		 * Generate the variables necessary to create a dot plot in plotly as a cytoscape 
		 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
		 *
		 * @param monitor the TaskMonitor required for this method by the parent 
		 * AbstractTask class
		 */
		public void run(TaskMonitor monitor) { 
			TaskManager sTM = sr.getService(TaskManager.class);
		    //AvailableCommands ac = sr.getService(AvailableCommands.class);
			CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);

			CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
			CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
			CyColumn nameColumn = table.getColumn(ModelUtils.getTunableSelection(nameCol));

			String xArray = ModelUtils.colToArray(xColumn);
			String yArray = ModelUtils.colToArray(yColumn);
			String nameArray = ModelUtils.colToArray(nameColumn);

			String xLabel = xColumn.getName();
			String yLabel = yColumn.getName();

			String html = JSUtils.getDotPlot(xArray, yArray, nameArray, xLabel, yLabel, editor);
			Map<String, Object> args = new HashMap();        
			args.put("text", html);
			args.put("title", "Dot Plot");

			TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
			sTM.execute(ti);
		}
	@ProvidesTitle
    public String getTitle() {
        return "Dot Plot";
    }
}
