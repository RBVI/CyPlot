package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

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
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class BarChartTask extends AbstractTask {
	
	final CyServiceRegistrar sr;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<String> yCol;
	
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;
	
	@Tunable (description="Open in plot editor?")
	public ListSingleSelection<String> editorCol;
	
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;
	public boolean editor;
		
	public BarChartTask(final CyServiceRegistrar sr) {
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

		
		yCol = new ListSingleSelection<>(headers);
		xCol = new ListSingleSelection<>(names);
		editorCol = new ListSingleSelection("Yes", "No");
	}

	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
		CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
		
		String xArray = ModelUtils.colToArray(xColumn);
		
		String yArray = ModelUtils.colToArray(yColumn);
				
		String xLabel = xColumn.getName();
        String yLabel = yColumn.getName();
        
        String editorSelection = ModelUtils.getTunableSelection(editorCol);
		if(editorSelection.equals("Yes")) {
			editor = true; //open the graph in the editor
		}else {
			editor = false; //don't open the graph in the editor
		}
        
		String html = JSUtils.getBarChart(xArray, yArray, xLabel, yLabel, editor);
		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", "Bar Chart");

		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
}