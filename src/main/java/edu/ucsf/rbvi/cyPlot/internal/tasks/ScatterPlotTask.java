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
		
		String html = JSUtils.getScatterPlot(xArray, yArray, ModelUtils.getTunableSelection(mode), ModelUtils.getTunableSelection(nameCol), nameArray);
		Map<String, Object> args = new HashMap<>();		
		args.put("text", html);
		args.put("title", "Scatter Plot");
		
		//failsafe code that works w/o JSUtils
//		String html = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
//		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
//		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
//		html += "<body><div id=\"scatterplot\" style=\"width:600px;height:600px;\"></div>";
//		html +=  "<script> var trace1 = { x: " + items.get("x") + ", y: " + items.get("y") + ", type: 'scatter', mode: '" + items.get("mode") + "', text: " + items.get("nameArray") + "};";
//		html += "var data = [trace1];";
//		html += "var layout = {hovermode: 'closest', title: 'Scatter Plot'};";
//		html += "Plotly.newPlot('scatterplot', data, layout);";
//		html += "var myPlot = document.getElementById('scatterplot');";
//		//on click 
//		html += "myPlot.on('plotly_click', function(data){ \n ;";
//		html += "cybrowser.executeCyCommand('network select nodeList = \"" + items.get("nameSelection") + ":' +data.points[0].text+'\"');});";
//		//lasso and box 
//		html += "myPlot.on('plotly_selected', function(data) { \n ;";
//		html += "var nodelist = ''; for(var i = 0; i<data.points.length; i++) { nodelist+= (', "+items.get("nameSelection")+ ":' +data.points[i].text);};";
//		html += "cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');});";
//		//important: always keep at bottom of HTML
//		html += "Plotly.react();";
//		html += "</script></body></html>";
		
		//JFrame if we want to add deeper customization options
//		JFrame fr = new JFrame("Scatter plot control panel");
//        ScatterPlotScreen sc = new ScatterPlotScreen();
//        fr.add(sc);
//        fr.pack();
//        fr.setVisible(true);
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}