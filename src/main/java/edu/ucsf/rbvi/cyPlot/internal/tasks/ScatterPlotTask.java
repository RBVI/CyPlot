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
	public ListSingleSelection<Callable<CyColumn>> xCol;

	@Tunable (description="Y-axis column")
	public ListSingleSelection<Callable<CyColumn>> yCol;
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	
	public ScatterPlotTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		CyNetwork network = netView.getModel();
		CyTable table = network.getDefaultNodeTable();
		Collection<CyColumn> columns = table.getColumns();
		
		List<String> headers = new ArrayList();
		for(CyColumn each : columns) {
			String header = each.getName();
			headers.add(header);
		}
		
		xCol = new ListSingleSelection(headers);
		yCol = new ListSingleSelection(headers);
	}
	
	public Callable<CyColumn> getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public Callable<CyColumn> getYSelection() {
		return yCol.getSelectedValue();
	}

	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
		String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		String html4 = "<body><div id=\"scattertest\" style=\"width:600px;height:600px;\"></div>";
		String html5 = "<script> var trace1 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [8, 7, 6, 5, 4, 3, 2, 1, 0], type: 'scatter'};";
		String html6 = "var trace2 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [0, 1, 2, 3, 4, 5, 6, 7, 8], type:'scatter'};";
		String html7 = "var data = [trace1, trace2];";
		String html8 = "var layout = { xaxis: {range: [2,5]}, yaxis: {range: [2,5]}};";
		String html9 = "Plotly.react('myDiv', data).then(function(){ Plotly.react('myDiv', data, layout); })";
		String html10 = "</script></body></html>";
		
        String html = html1 + html2 + html3 + html4 + html5 + html6 + html7 + html8 + html9 + html10;
		//String html = "<!DOCTYPE HTML><html><body><a href=\"https://www.google.com/\"></a></body></html>";
		Map<String, Object> args = new HashMap<>();
		
		args.put("text", html);
		args.put("title", "Plot");
		args.put("url", "https://unpkg.com/react@16.2.0/umd/react.production.min.js");
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