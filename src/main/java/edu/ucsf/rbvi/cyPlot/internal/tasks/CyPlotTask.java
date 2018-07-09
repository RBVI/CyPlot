package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

public class CyPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	
	public CyPlotTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
	}

	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
	//	String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
	//	String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		String html4 = "<body><div id=\"scattertest\" style=\"width:600px;height:600px;\"></div>";
		String html5 = "<script> var trace1 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [8, 7, 6, 5, 4, 3, 2, 1, 0], type: 'scatter'};";
		String html6 = "var trace2 = { x: [0, 1, 2, 3, 4, 5, 6, 7, 8], y: [0, 1, 2, 3, 4, 5, 6, 7, 8], type:'scatter'};";
		String html7 = "var data = [trace1, trace2];";
		String html8 = "var layout = { xaxis: {range: [2,5]}, yaxis: {range: [2,5]}};";
		String html9 = "Plotly.react('myDiv', data).then(function(){ Plotly.react('myDiv', data, layout); })";
		String html10 = "</script></body></html>";
		
        String html = html1 + html4 + html5 + html6 + html7 + html8 + html9 + html10;
	//	String html = "<!DOCTYPE HTML><html><body><a href=\"https://www.google.com/\"></a></body></html>";
		Map<String, Object> args = new HashMap<>();
		
		args.put("text", html);
	/*	args.put("shape", "ellipse");
		args.put("colorScaleLow", "0");
		args.put("colorScaleHigh", "555"); */
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}