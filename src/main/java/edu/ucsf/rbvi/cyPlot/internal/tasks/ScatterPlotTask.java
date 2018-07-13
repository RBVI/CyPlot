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
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class ScatterPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<String> yCol;
	
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
		
		List<String> headers = new ArrayList<>();
		for(CyColumn each : columns) {
			if(!each.getType().isAssignableFrom(String.class) && 
					!each.getType().isAssignableFrom(Boolean.class) &&
					!each.getName().equals(CyNetwork.SUID) && 
					!each.getName().equals(CyNetwork.SELECTED)) {
				String header = each.getName();
				headers.add(header);
			}
		}
		
		xCol = new ListSingleSelection<>(headers);
		yCol = new ListSingleSelection<>(headers);
	}
	
	public String getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public String getYSelection() {
		return yCol.getSelectedValue();
	}

	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(getXSelection());
		CyColumn yColumn = table.getColumn(getYSelection());
		
		String xArray = "[";
		List<Object> list1 = xColumn.getValues(xColumn.getType());
		for(int i = 0; i<list1.size(); i++) {
			xArray += (""+list1.get(i));
			if(i != list1.size()-1) {
				xArray += ", ";
			}else {
				xArray += "]"; 
			}
		}
		
		String yArray = "[";
		List<Object> list2 = yColumn.getValues(yColumn.getType());
		for(int i = 0; i<list2.size(); i++) {
			yArray += (""+list2.get(i));
			if(i != list2.size()-1) {
				yArray += ", ";
			}else {
				yArray += "]"; 
			}
		}
		

		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
		String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		String html4 = "<body><div id=\"scattertest\" style=\"width:600px;height:600px;\"></div>";
		String html5 = "<script> var trace1 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter'};";
		String html6 = "var trace2 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter'};";
		//String html5 = "<script> var trace1 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [8, 7, 6, 5, 4, 3, 2, 1, 0], type: 'scatter'};";
		//String html6 = "var trace2 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [0, 1, 2, 3, 4, 5, 6, 7, 8], type:'scatter'};";
		String html7 = "var data = [trace1];";
		//String html8 = "var layout = { xaxis: {range: [0,5]}, yaxis: {range: [0,5]}};";
		String html8 = "var layout = {};";
		String html9 = "Plotly.react('scattertest', data, layout);";
		String html10 = "</script></body></html>";
		
        String html = html1 + html2 + html3 + html4 + html5 + html6 + html7 + html8 + html9 + html10;
		Map<String, Object> args = new HashMap<>();
		
		args.put("text", html);
		args.put("title", "Plot");
		args.put("id", "01");
		
		//JFrame
		JFrame fr = new JFrame("Scatter plot interface");
        ScatterPlotScreen sc = new ScatterPlotScreen();
        fr.add(sc);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.pack();
        fr.setVisible(true);
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}