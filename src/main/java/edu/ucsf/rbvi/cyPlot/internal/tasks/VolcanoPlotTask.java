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

import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class VolcanoPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	@Tunable (description="Fold change column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="P-val column")
	public ListSingleSelection<String> yCol;
	
	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol;
	
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
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		
		List<String> headers = ModelUtils.getColOptions(columns, "num");
		List<String> names = ModelUtils.getColOptions(columns, "string");
		
		xCol = new ListSingleSelection<>(headers);
		yCol = new ListSingleSelection<>(headers);
		nameCol = new ListSingleSelection<>(names);
	}
	
	public String getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public String getYSelection() {
		return yCol.getSelectedValue();
	}

	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(getXSelection());
		CyColumn yColumn = table.getColumn(getYSelection());
		CyColumn nameColumn = table.getColumn("shared name");
		
        String xArray = ModelUtils.colToArray(xColumn, "num");
        String yArray = ModelUtils.colToArray(yColumn, "num");
        String nameArray = ModelUtils.colToArray(nameColumn, "string");
        
        String xLabel = xColumn.getName();
        String yLabel = yColumn.getName();
        
        String html = JSUtils.getVolcanoPlot(xArray, yArray,  ModelUtils.getTunableSelection(nameCol), nameArray, xLabel, yLabel);
		Map<String, Object> args = new HashMap();        
        args.put("text", html);
		args.put("title", "Plot");
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}