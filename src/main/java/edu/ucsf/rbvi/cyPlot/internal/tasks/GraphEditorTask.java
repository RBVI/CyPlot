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
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

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
	
	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol;
	
	public GraphEditorTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		
		List<String> headers = ModelUtils.getColOptions(columns, "num");
		cols = new ListMultipleSelection<>(headers);
		
		List<String> names = ModelUtils.getColOptions(columns, "string");
		nameCol = new ListSingleSelection<>(names);
		
		selectedColumnsList = new ArrayList<>();
		/*
		 * This isn't defined until the run method
		for(int i = 0; i < cols.getSelectedValues().size(); i++) {
			selectedColumnsList.add(table.getColumn(cols.getSelectedValues().get(i)));
		}
		*/
	}
	
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

	
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}
