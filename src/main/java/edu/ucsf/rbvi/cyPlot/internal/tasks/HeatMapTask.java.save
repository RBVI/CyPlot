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

public class HeatMapTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	
	@Tunable (description="X-axis column")
	public ListSingleSelection<String> xCol;
	
	@Tunable (description="Number of rows")
	public ListSingleSelection numRows;
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;
	
	public HeatMapTask(final CyServiceRegistrar sr) {
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
		numRows = new ListSingleSelection(1, 2, 3, 4);
	}
	
	public String getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public int getNumRowsSelection() {
		return (int) numRows.getSelectedValue();
	}
	
	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(getXSelection());
		
		System.out.println("runmethod");
		
		String dataArray = "[";
		List<Object> list1 = xColumn.getValues(xColumn.getType());
		int numRows = getNumRowsSelection();
		int numCols = list1.size()/numRows;
		int dataArrInd = 0;
		for(int r = 0; r<numRows; r++) {
			dataArray+="[";
			for(int c = 0; c<numCols; c++) {
				dataArray+=list1.get(dataArrInd);
				if(c!=numCols-1) {
					dataArrInd++;
					dataArray+=", ";
				}else {
					dataArray += "]";
				}
			}
			if (dataArrInd!=list1.size()-1) {
				dataArray+=", ";
			}
			else {
				dataArray+="]";
		}
			
			String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
			String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
			String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script>";
			String html4 = "<body><div id=\"heatmap\" style=\"width:600px;height:600px;\"></div>";
			String html5 = "<script> var colorscaleValue = [[-3, 'rgb(166,206,227)'], [-2, 'rgb(31,120,180)'], [-1, 'rgb(178,223,138)'], [1, 'rgb(51,160,44)'], [2, 'rgb(251,154,153)'], [3, 'rgb(227,26,28)']]; var i = 0; var j = 0; var z = []; for (i = 0; i < 5; i += 1) {z[i] = []; for (j = 0; j < 5; j += 1) {z[i].push(Math.random());}}var data = [{z: z,type: \"heatmap\",colorscale: colorscaleValue}];";
					//"Plotly.newPlot(\"plot1\", data);";
			//String html6 = "var trace2 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter'};";
			//String html7 = "var data = [trace1];";
			//String html8 = "var layout = { xaxis: {range: [0,5]}, yaxis: {range: [0,5]}};"; (code for manually setting the range, if I decide to do that.)
			//String html8 = "var layout = {hovermode: 'closest', title: 'Heat Map'};";
			
//			String html9 = "var myPlot = document.getElementById('scatterplot');";
//			String html10 = "myPlot.on('plotly_click', function(data){ var pts = '';";
//			String html11 = "for(var i=0; i<data.points.length; i++) {";
//			String html12 = "pts = 'x= ' +data.points[i].x + '\ny = ' + data.points[i].y.toPrecision(4) + '\n\n';}";
//			String html13 = "alert('Closest point clicked:\n\n'+pts);});";
			
			String html9 = "Plotly.react('heatmap', data);";
			String html10 = "</script></body></html>";
			
	        String html = html1 + html2 + html3 + html4 + html5 +  html9 + html10;
			Map<String, Object> args = new HashMap<>();
			
			System.out.println(html);
			
			args.put("text", html);
			args.put("title", "Plot");
			args.put("id", "01");
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}
}