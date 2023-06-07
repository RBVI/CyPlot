package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.List;
import java.util.Map;

import  edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;
import  edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterFilterTask;
import org.cytoscape.task.AbstractTableColumnTaskFactory;
import org.cytoscape.model.CyColumn;

import org.cytoscape.work.TaskIterator;

public class CyPlotColumnTaskFactory extends AbstractTableColumnTaskFactory {
	final CyChartManager manager;
	
	public CyPlotColumnTaskFactory(CyChartManager mgr) {	manager = mgr;	}
	
	public boolean isReady() {		return true;	}
	
	@Override
	public TaskIterator createTaskIterator(CyColumn column)
	{ 
		return new TaskIterator(new ScatterFilterTask(manager, column, null)); 	
	}
}