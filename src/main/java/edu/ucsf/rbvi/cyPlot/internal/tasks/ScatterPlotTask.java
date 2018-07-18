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

import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

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
	
	public ModelUtils mUtils;
	
	public ScatterPlotTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		
		List<String> headers = mUtils.getColOptions(columns, "num");
		
		List<String> names = mUtils.getColOptions(columns, "string");

		
		xCol = new ListSingleSelection<>(headers);
		yCol = new ListSingleSelection<>(headers);
		nameCol = new ListSingleSelection<>(names);
		mode = new ListSingleSelection<>("lines", "markers");
	}
	
	public String getXSelection() {
		return xCol.getSelectedValue();
	}
	
	public String getYSelection() {
		return yCol.getSelectedValue();
	}
	
	public String getNameSelection() {
		return nameCol.getSelectedValue();
	}
	
	public String getModeSelection() {
		return mode.getSelectedValue();
	}

	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		CyColumn xColumn = table.getColumn(getXSelection());
		CyColumn yColumn = table.getColumn(getYSelection());
		CyColumn nameColumn = table.getColumn(getNameSelection());
		
		List<Object> list1 = xColumn.getValues(xColumn.getType());
		String xArray = mUtils.colToArray(list1, "num");
		
		List<Object> list2 = yColumn.getValues(yColumn.getType());
		String yArray = mUtils.colToArray(list2, "num");
		
		List<Object> nameList = nameColumn.getValues(nameColumn.getType());
		String nameArray = mUtils.colToArray(nameList, "string");

		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		String html2 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
		String html3 = "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		String html4 = "<body><div id=\"scatterplot\" style=\"width:600px;height:600px;\"></div>";
		String html5 = "<script> var trace1 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter', mode: '" + this.getModeSelection() + "', text: " + nameArray + "};";
		String html6 = "var trace2 = { x: " + xArray + ", y: " + yArray + ", type: 'scatter'};";
		String html7 = "var data = [trace1];";
		//String html8 = "var layout = { xaxis: {range: [0,5]}, yaxis: {range: [0,5]}};"; (code for manually setting the range, if I decide to do that.)
		String html8 = "var layout = {hovermode: 'closest', title: 'Scatter Plot'};";
		String html9 = "Plotly.newPlot('scatterplot', data, layout);";
		String html10 = "var myPlot = document.getElementById('scatterplot');";
		
		//on click stuff
		String html11 = "myPlot.on('plotly_click', function(data){ \n ;";
		//String html12 = "alert('You clicked me!, point = '+data.points[0].text);";
		//String html12 = "cybrowser.executeCyCommand('network select nodeList = ' +data.points[0].text);});";
		String html12 = "cybrowser.executeCyCommand('network select nodeList = \"" + this.getNameSelection() + ":' +data.points[0].text+'\"');});";
		
		//lasso stuff
		String html13 = "myPlot.on('plotly_selected', function(data) { \n ;";
		String html14 = "var nodelist = ''; for(var i = 0; i<data.points.length; i++) { nodelist+= (', "+this.getNameSelection()+ ":' +data.points[i].text);};";
		String html15 = "cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');});";
		//important: always keep at bottom of HTML
		String html16 = "Plotly.react();";
		String html17 = "</script></body></html>";
		
        String html = html1 + html2 + html3 + html4 + html5 + html6 + html7 + html8 + html9 + html10 + html11 + html12 + html13 + html14 + html15 + html16 + html17;
		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", "Plot");
		
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