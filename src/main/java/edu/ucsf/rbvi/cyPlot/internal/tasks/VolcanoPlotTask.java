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

public class VolcanoPlotTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	@Tunable (description="Fold change column")
	public ListSingleSelection<String> xCol;

	@Tunable (description="P-val column")
	public ListSingleSelection<String> yCol;
	
	@Tunable (description="Markers?")
	public ListSingleSelection<String> mode;
	
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
		mode = new ListSingleSelection<>("lines", "markers");
	}
	
	public String getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public String getYSelection() {
		return yCol.getSelectedValue();
	}
	
	public String getModeSelection() {
		return mode.getSelectedValue();
	}

	public void run(TaskMonitor monitor) { 
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(getXSelection());
		CyColumn yColumn = table.getColumn(getYSelection());
		CyColumn nameColumn = table.getColumn("shared name");
		
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
		
		String nameArray = "[";
		List<Object> nameList = nameColumn.getValues(nameColumn.getType());
		for(int i = 0; i<nameList.size(); i++) {
			if(i != nameList.size()-1) {
				nameArray += ("'" + nameList.get(i) + "', ");
			}else {
				nameArray += ("'" + nameList.get(i) + "']");
			}
		}
		
		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
		String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		String html4 = "<body><div id=\"scatterplot\" style=\"width:600px;height:600px;\"></div>";
		
		String html5 = "<script> var xArr = " + xArray + ";";
		String html6 = "for (var i = 0; i < xArr.length; i++ ) { ";
	    String html7 = "xArr[i] = ((Math.log10(xArr[i])) / (Math.log(2))); }";
	    
	    String html8 = "var yArr = " + yArray + ";";
		String html9 = "for (var i = 0; i < yArr.length; i++ ) { ";
	    String html10 = "yArr[i] = Math.log10(yArr[i]) * -1; } ";
	    
		String html11 = "var trace1 = { x: xArr , y:  yArr , type: 'scatter', mode: '" + this.getModeSelection() + "', text: " + nameArray + "};";
		String htmll2 = "var trace2 = { x: xArr , y:  yArr , type: 'scatter'};";
		String html13 = "var data = [trace1];";
		//String html8 = "var layout = { xaxis: {range: [0,5]}, yaxis: {range: [0,5]}};"; (code for manually setting the range, if I decide to do that.)
		String html14 = "var layout = {hovermode: 'closest', title: 'Volcano Plot'};";
		
//		String html9 = "var myPlot = document.getElementById('scatterplot');";
//		String html10 = "myPlot.on('plotly_click', function(data){ var pts = '';";
//		String html11 = "for(var i=0; i<data.points.length; i++) {";
//		String html12 = "pts = 'x= ' + data.points[i].x + '\ny = ' + data.points[i].y.toPrecision(4) + '\n\n';}";
//		String html13 = "alert('Closest point clicked:\n\n'+pts);});";
		
		String html15 = "Plotly.react('scatterplot', data, layout);";
		String html16 = "</script></body></html>";
		
        String html = html1 + html2 + html3 + html4 + html5 + html6 + html7 + html8 + html9 + html10 + html11 + htmll2 + html13 + html14 + html15 + html16;
		Map<String, Object> args = new HashMap<>();
		
		args.put("text", html);
		args.put("title", "Plot");
		args.put("id", "01");
		
		//JFrame
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