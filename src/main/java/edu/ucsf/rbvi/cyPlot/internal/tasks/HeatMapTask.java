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
	
	@Tunable (description="Columns")
	public ListMultipleSelection<String> cols;
		
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
		
		cols = new ListMultipleSelection<>(headers);
		
		lColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		mColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
		hColor = new ListSingleSelection("Red", "Yellow", "Blue", "Black", "White");
	}
	
	public List<String> getColsSelection() {
		return cols.getSelectedValues();
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
		
		String dataArray = "[";
		for (int i = 0; i < colNames.size(); i++) {
			String colDataArray = ModelUtils.colToArray(table.getColumn(colNames.get(i)), "num");
			if (i != colNames.size() - 1) {
				dataArray += colDataArray+ ", ";
			}
			else {
				dataArray += colDataArray + "]";
			}	
			
		}
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
		
			String html = JSUtils.getHeatMap(lowRGB, medRGB, highRGB, dataArray, colNamesArray, title);
			Map<String, Object> args = new HashMap<>();
			
			System.out.println(html);
			
			args.put("text", html);
			args.put("title", "Plot");
			args.put("id", "01");
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
		sTM.execute(ti);
	}
}