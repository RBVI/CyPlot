package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class ScatterPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<String> yCol;
	
	@Tunable (description="Name selection column")
	public ListSingleSelection<String> nameCol;
	
	@Tunable (description="Lines or Markers?")
	public ListSingleSelection<String> mode;
	
	
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
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		
		List<String> headers = ModelUtils.getColOptions(columns, "num");
		
		List<String> names = ModelUtils.getColOptions(columns, "string");

		
		xCol = new ListSingleSelection<>(headers);
		yCol = new ListSingleSelection<>(headers);
		nameCol = new ListSingleSelection<>(names);
		mode = new ListSingleSelection<>("lines", "markers");
	}

	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(ModelUtils.getTunableSelection(xCol));
		CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
		CyColumn nameColumn = table.getColumn(ModelUtils.getTunableSelection(nameCol));
		
		List<Object> list1 = xColumn.getValues(xColumn.getType());
		String xArray = ModelUtils.colToArray(list1, "num");
		
		List<Object> list2 = yColumn.getValues(yColumn.getType());
		String yArray = ModelUtils.colToArray(list2, "num");
		
		List<Object> nameList = nameColumn.getValues(nameColumn.getType());
		String nameArray = ModelUtils.colToArray(nameList, "string");

		Map<String, String> items = new HashMap<>();
		items.put("type", "scatter");
		items.put("x",xArray);
		items.put("y",yArray);
		items.put("mode",ModelUtils.getTunableSelection(mode));
		items.put("nameSelection",ModelUtils.getTunableSelection(nameCol));
		items.put("nameArray", nameArray);
		
        String html = JSUtils.getHTML(items);
		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", "Plot");
		
		//JFrame if we want to add deeper customization options
//		JFrame fr = new JFrame("Scatter plot interface");
//        ScatterPlotScreen sc = new ScatterPlotScreen();
//        fr.add(sc);
//        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        fr.pack();
//        fr.setVisible(true);
		//
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}