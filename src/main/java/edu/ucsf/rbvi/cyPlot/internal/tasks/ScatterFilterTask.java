package edu.ucsf.rbvi.cyPlot.internal.tasks;


import edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterFilterDialog;
import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;
import edu.ucsf.rbvi.cyPlot.internal.tasks.AbstractEmptyObservableTask;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class ScatterFilterTask extends AbstractEmptyObservableTask {
	public String title = null;

	@Tunable(description = "X Axis Parameter", context= Tunable.NOGUI_CONTEXT)
	public String x = "Degree";
	@Tunable(description = "Y Axis Parameter", context= Tunable.NOGUI_CONTEXT)
	public String y = "Betweeenness";

	@ProvidesTitle
	public String getTitle() {		return "Starting Cytoscape Scatter Plot";	}

	@Override	public <R> R getResults(Class<? extends R> type) {		return getIDResults(type, null);	}

	final CyChartManager manager;
	CyColumn column = null;
	CyColumn yColumn = null;

	//----------------------------------------------------
	public ScatterFilterTask(CyChartManager mgr) {
		manager = mgr;
		column = mgr.getXColumn();
		yColumn = mgr.getYColumn();
//		CyNetwork net = manager.getCurrentNetwork();
	}

	public ScatterFilterTask(CyChartManager mgr, CyColumn col, CyColumn ycol) {
		manager = mgr;
		mgr.setXColumn(col);
//		if (ycol == null) 
//			ycol = getFirstNumericColumnExcept(col);
		mgr.setYColumn(ycol);
	}

//	private CyColumn getFirstNumericColumnExcept(CyColumn col) {
//		manager.getCurrentNetwork()
//		return null;
//	}

	public void run(TaskMonitor monitor) 
	{
//		if (monitor instanceof TFExecutor)
//		{
//			TFExecutor exec = (TFExecutor) monitor;
//			exec.interceptor.
			
			CyColumn manCol = manager.getXColumn();
			manager.setXColumnName(manCol == null ? x : manCol.getName());
			manager.setYColumnName(y);
			ScatterFilterDialog	chart = new ScatterFilterDialog(manager, title);
			chart.setVisible(true);
//		}
				
	}

}
