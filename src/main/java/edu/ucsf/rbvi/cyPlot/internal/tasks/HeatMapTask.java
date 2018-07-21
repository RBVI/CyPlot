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

import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;


public class HeatMapTask extends AbstractTask {
	
	final CyServiceRegistrar sr;
	
	@Tunable (description="Column 1")
	public ListSingleSelection<String> col1;
	
	@Tunable (description="Column 2")
	public ListSingleSelection<String> col2;
	
	@Tunable (description="Column 3")
	public ListSingleSelection<String> col3;
	
	@Tunable (description="Low color")
	public ListSingleSelection<String> lColor;
	
	@Tunable (description="Middle color")
	public ListSingleSelection<String> mColor;
	
	@Tunable (description="High color")
	public ListSingleSelection<String> hColor;
	
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
		
		List<String> headers = ModelUtils.getColOptions(columns, "num");
		
//		List<String> headers = new ArrayList<>();
//		for(CyColumn each : columns) {
//			if(!each.getType().isAssignableFrom(String.class) && 
//					!each.getType().isAssignableFrom(Boolean.class) &&
//					!each.getName().equals(CyNetwork.SUID) && 
//					!each.getName().equals(CyNetwork.SELECTED)) {
//				String header = each.getName();
//				headers.add(header);
//			}
//		}
		
		col1 = new ListSingleSelection<>(headers);
		col2 = new ListSingleSelection<>(headers);
		col3 = new ListSingleSelection<>(headers);
		lColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		mColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		hColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
	}
	
	public String getCol1Selection() {
		return col1.getSelectedValue();
	}
	
	public String getCol2Selection() {
		return col2.getSelectedValue();
	}
	
	public String getCol3Selection() {
		return col3.getSelectedValue();
	}
	
	public String getLowSelection() {
		return lColor.getSelectedValue();
	}
	
	public String getMedSelection() {
		return mColor.getSelectedValue();
	}
	
	public String getHighSelection() {
		return hColor.getSelectedValue();
	}
	
	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn column1 = table.getColumn(getCol1Selection());
		CyColumn column2 = table.getColumn(getCol2Selection());
		CyColumn column3 = table.getColumn(getCol3Selection());
		String colNamesArray = "[\"" + getCol1Selection() + "\", \"" + getCol1Selection() + "\", \"" + getCol3Selection() + "\"]";
		
		//List<Object> list1 = column1.getValues(column1.getType());
		String col1Array = ModelUtils.colToArray(column1, "num");
//		String col1Array = "[";
//		List<Object> list1 = column1.getValues(column1.getType());
//		for(int i = 0; i<list1.size(); i++) {
//			col1Array += (""+list1.get(i));
//			if(i != list1.size()-1) {
//				col1Array += ", ";
//			}else {
//				col1Array += "]"; 
//			}
//		}
		
		//List<Object> list2 = column2.getValues(column2.getType());
		String col2Array = ModelUtils.colToArray(column2, "num");
//		String col2Array = "[";
//		List<Object> list2 = column2.getValues(column2.getType());
//		for(int i = 0; i<list2.size(); i++) {
//			col2Array += (""+list2.get(i));
//			if(i != list2.size()-1) {
//				col2Array += ", ";
//			}else {
//				col2Array += "]"; 
//			}
//		}
		
		//List<Object> list3 = column3.getValues(column3.getType());
		String col3Array = ModelUtils.colToArray(column3, "num");
//		String col3Array = "[";
//		List<Object> list3 = column3.getValues(column3.getType());
//		for(int i = 0; i<list3.size(); i++) {
//			col3Array += (""+list3.get(i));
//			if(i != list3.size()-1) {
//				col3Array += ", ";
//			}else {
//				col3Array += "]"; 
//			}
//		}
		
		String dataArray = "[" + col1Array + "," + col2Array + "," + col3Array + "]";
				
		String lowRGB = "";
		if (getLowSelection().equals("Red")) {
			lowRGB = "rgb(255, 0, 0)";
		}
		if (getLowSelection().equals("Yellow")) {
			lowRGB = "rgb(255, 255, 0)";
		}
		if (getLowSelection().equals("Blue")) {
			lowRGB = "rgb(0, 0, 255)";
		}
		if (getLowSelection().equals("Black")) {
			lowRGB = "rgb(0, 0, 0)";
		}
		if (getLowSelection().equals("White")) {
			lowRGB = "rgb(255, 255, 255)";
		}
		
		String medRGB = "";
		if (getMedSelection().equals("Red")) {
			medRGB = "rgb(255, 0, 0)";
		}
		if (getMedSelection().equals("Yellow")) {
			medRGB = "rgb(255, 255, 0)";
		}
		if (getMedSelection().equals("Blue")) {
			medRGB = "rgb(0, 0, 255)";
		}
		if (getMedSelection().equals("Black")) {
			medRGB = "rgb(0, 0, 0)";
		}
		if (getMedSelection().equals("White")) {
			medRGB = "rgb(255, 255, 255)";
		}
		
		String highRGB = "";
		if (getHighSelection().equals("Red")) {
			highRGB = "rgb(255, 0, 0)";
		}
		if (getHighSelection().equals("Yellow")) {
			highRGB = "rgb(255, 255, 0)";
		}
		if (getHighSelection().equals("Blue")) {
			highRGB = "rgb(0, 0, 255)";
		}
		if (getHighSelection().equals("Black")) {
			highRGB = "rgb(0, 0, 0)";
		}
		if (getHighSelection().equals("White")) {
			highRGB = "rgb(255, 255, 255)";
		}
		
//			String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
//			String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
//			String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script>";
//			String html4 = "<body><div id=\"heatmap\" style=\"width:600px;height:600px;\"></div>";
//			String html5 = "<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", type: \"heatmap\",colorscale: colorscaleValue}];";
//			//String html6 = "Plotly.newPlot(\"plot1\", data);";
//			//String html6 = "var trace2 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter'};";
//			//String html7 = "var data = [trace1];";
//			//String html8 = "var layout = { xaxis: {range: [0,5]}, yaxis: {range: [0,5]}};"; (code for manually setting the range, if I decide to do that.)
//			//String html8 = "var layout = {hovermode: 'closest', title: 'Heat Map'};";
//			
////			String html9 = "var myPlot = document.getElementById('scatterplot');";
////			String html10 = "myPlot.on('plotly_click', function(data){ var pts = '';";
////			String html11 = "for(var i=0; i<data.points.length; i++) {";
////			String html12 = "pts = 'x= ' +data.points[i].x + '\ny = ' + data.points[i].y.toPrecision(4) + '\n\n';}";
////			String html13 = "alert('Closest point clicked:\n\n'+pts);});";
//			
//			String html9 = "Plotly.react('heatmap', data);";
//			String html10 = "</script></body></html>";
//			
//	        String html = html1 + html2 + html3 + html4 + html5 + html9 + html10;
		
			String html = JSUtils.getHeatMap(lowRGB, medRGB, highRGB, dataArray, colNamesArray);
			Map<String, Object> args = new HashMap<>();
			
			System.out.println(html);
			
			args.put("text", html);
			args.put("title", "Plot");
			args.put("id", "01");
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}