package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

public class GraphEditorTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;
	public List<CyColumn> selectedColumnsList;
	
	@Tunable (description="Columns")
	public ListMultipleSelection<String> cols;
	
	public GraphEditorTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		
		//List<String> headers = ModelUtils.getColOptions(columns, "all");
		//Looks like the editor can only take in numeric data under dataSources! Previous line broke the program.
		List<String> headers = ModelUtils.getColOptions(columns, "all");
		cols = new ListMultipleSelection<>(headers);
		
		
		selectedColumnsList = new ArrayList<>();
	}
	
	/**
	 * Generate the variables necessary to open the plotly graph editor with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		// Get the selected columns
		for (String col: cols.getSelectedValues()) {
			selectedColumnsList.add(table.getColumn(col));
		}

		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		String dataSourcesArray = ModelUtils.colsToDataSourcesArray(selectedColumnsList);
		
		String html = JSUtils.getChartEditor(dataSourcesArray);
		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", "Graph Editor");

		//args.put("debug", true);
		
		// System.out.println(html);

	
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
	@ProvidesTitle
    public String getTitle() {
        return "Graph Editor";
    }
}
