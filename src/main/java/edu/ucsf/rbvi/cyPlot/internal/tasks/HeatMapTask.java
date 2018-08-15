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
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;


public class HeatMapTask extends AbstractTask {
	
	final CyServiceRegistrar sr;

	@Tunable (description="Title")
	public String title;
	
	@Tunable (description="Data columns")
	public ListMultipleSelection<String> cols;
	
	@Tunable (description="Y-axis label")
	public ListSingleSelection<String> yAxis;
		
	@Tunable (description="Low color")
	public ListSingleSelection<String> lColor;
	
	@Tunable (description="Middle color")
	public ListSingleSelection<String> mColor;
	
	@Tunable (description="High color")
	public ListSingleSelection<String> hColor;
	
	@Tunable (description="Open in plot editor?")
	public ListSingleSelection<String> editorCol;
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;
	public boolean editor;
	
	public HeatMapTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		network = netView.getModel();
		table = network.getDefaultNodeTable();
		columns = table.getColumns();
		editor = true;
		
		List<String> headers = ModelUtils.getColOptions(columns, "num");

		List<String> names = ModelUtils.getColOptions(columns, "string");
		
		cols = new ListMultipleSelection<>(headers);
		
		yAxis = new ListSingleSelection<>(names);
		
		lColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		mColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		hColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		editorCol = new ListSingleSelection("Yes", "No");
	}
	
	public List<String> getColsSelection() {
		return cols.getSelectedValues();
	}
	
	/**
	 * Generate the variables necessary to create a bar chart in plotly with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		AvailableCommands ac = sr.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);	
		
		CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yAxis));
		
		
		String colNamesArray = "[";
		List<String> colNames = getColsSelection();
		for (int i = 0; i < colNames.size(); i++) {
			if (i != colNames.size() - 1) {
				colNamesArray += "\"" + colNames.get(i) + "\", ";
			}
			else {
				colNamesArray += "\"" + colNames.get(i) + "\"]";
			}	
		}
		
		String yAxisArray = ModelUtils.colToArray(yColumn);
		
		String dataArray = "[";
		for (int i = 0; i < colNames.size(); i++) {
			String colDataArray = ModelUtils.colToArray(table.getColumn(colNames.get(i)));
			if (i != colNames.size() - 1) {
				dataArray += colDataArray+ ", ";
			}
			else {
				dataArray += colDataArray + "]";
			}	
			
		}
		String lowRGB = "";
		if (ModelUtils.getTunableSelection(lColor).equals("Red")) {
			lowRGB = "rgb(255, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(lColor).equals("Yellow")) {
			lowRGB = "rgb(255, 255, 0)";
		}
		if (ModelUtils.getTunableSelection(lColor).equals("Blue")) {
			lowRGB = "rgb(0, 0, 255)";
		}
		if (ModelUtils.getTunableSelection(lColor).equals("Black")) {
			lowRGB = "rgb(0, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(lColor).equals("White")) {
			lowRGB = "rgb(255, 255, 255)";
		}
		
		String medRGB = "";
		if (ModelUtils.getTunableSelection(mColor).equals("Red")) {
			medRGB = "rgb(255, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(mColor).equals("Yellow")) {
			medRGB = "rgb(255, 255, 0)";
		}
		if (ModelUtils.getTunableSelection(mColor).equals("Blue")) {
			medRGB = "rgb(0, 0, 255)";
		}
		if (ModelUtils.getTunableSelection(mColor).equals("Black")) {
			medRGB = "rgb(0, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(mColor).equals("White")) {
			medRGB = "rgb(255, 255, 255)";
		}
		
		String highRGB = "";
		if (ModelUtils.getTunableSelection(hColor).equals("Red")) {
			highRGB = "rgb(255, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(hColor).equals("Yellow")) {
			highRGB = "rgb(255, 255, 0)";
		}
		if (ModelUtils.getTunableSelection(hColor).equals("Blue")) {
			highRGB = "rgb(0, 0, 255)";
		}
		if (ModelUtils.getTunableSelection(hColor).equals("Black")) {
			highRGB = "rgb(0, 0, 0)";
		}
		if (ModelUtils.getTunableSelection(hColor).equals("White")) {
			highRGB = "rgb(255, 255, 255)";
		}
		
		String editorSelection = ModelUtils.getTunableSelection(editorCol);
		if(editorSelection.equals("Yes")) {
			editor = true; //open the graph in the editor
		}else {
			editor = false; //don't open the graph in the editor
		}
		
		String html = JSUtils.getHeatMap(lowRGB, medRGB, highRGB, dataArray, colNamesArray, yAxisArray, title, editor);
		Map<String, Object> args = new HashMap<>();
		
		System.out.println(html);
		
		args.put("text", html);
		args.put("title", "Plot");
		args.put("id", "01");
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
}