package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
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
import org.cytoscape.model.subnetwork.CyRootNetwork;

public class VolcanoPlotTask extends AbstractTask {
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	final CyServiceRegistrar sr;
	
	@Tunable (description="Fold-change column")
    public ListSingleSelection<Callable<CyColumn>> foldChangeCol;

     @Tunable (description="P-Value column")
	public ListSingleSelection<Callable<CyColumn>> pValCol;
	
	public VolcanoPlotTask(final CyServiceRegistrar sr) {
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
		
		foldChangeCol = new ListSingleSelection(headers);
		pValCol = new ListSingleSelection(headers);
	}
	
    public Callable<CyColumn> getFoldValueSelection() {
		return foldChangeCol.getSelectedValue();
	}
	
	public Callable<CyColumn> getPValueSelection() {
		return pValCol.getSelectedValue();
	} 

	public void run(TaskMonitor monitor) throws Exception { 	
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);
		
		String html1 = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		String html4 = "<body><div id=\"myDiv\"></div>";
		String html5 = "<script>function makeplot(){ Plotly.d3.csv(\"https://raw.githubusercontent.com/plotly/datasets/master/2014_apple_stock.csv\", function(data){ processData(data){);}";
		String html6 = "function processData(allRows){ var x = [], y = [], std = [];";
		String html7 = "for (var i = 0; i < allRows.length; i++) { row = allRows[i];";
		String html8 = "x.push(row['AAPL_x']);";
		String html9 = "y.push(row['AAPL_y']);}";
		String html10 = "makePlotly(x,y,std);}";
		String html11 = "function makePlotly(x,y,std){";
		String html12 = "var plotDiv = document.getElementById(\"plot\");";
		String html13 = "var traces = [{x: x, y: y}];";
		String html14 = "var layout = {yaxis: {fixedrange: true}, xaxis: {fixedrange: true}, title: 'my graph'} Plotly.newPlot('myDiv', traces,layout);}; makeplot();";
		String html15 = "</script></body></html>";
        String html = html1 + html4 + html5 + html6 + html7 + html8 + html9 + html10 + html11 + html12 + html13 + html14 + html15;
	//	String html = "<!DOCTYPE HTML><html><body><a href=\"https://www.google.com/\"></a></body></html>";
		Map<String, Object> args = new HashMap<>();	
		args.put("text", html);

		//JFrame
				JFrame fr = new JFrame("volcano plot interface");
		        VolcanoPlotScreen sc = new VolcanoPlotScreen();
		        fr.add(sc);
		        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        fr.pack();
		        fr.setVisible(true);
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
	
	
}