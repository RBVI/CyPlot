package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.List;
import java.util.Map;


//import edu.ucsf.rbvi.cyPlot.internal.columnTasks.ScatterFilterTask;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterPlotTask;
import org.cytoscape.task.AbstractTableColumnTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;

public class VolcanoPlotColumnTaskFactory extends AbstractTableColumnTaskFactory {
//	final CyChartManager manager;
	final CyServiceRegistrar cs_registrar;
	
	public VolcanoPlotColumnTaskFactory(CyServiceRegistrar sr) {	cs_registrar = sr;	}
	
	public boolean isReady() {		return true;	}
	
	@Override
	public TaskIterator createTaskIterator(CyColumn column)
	{ 
		return new TaskIterator(new VolcanoPlotTask(cs_registrar)); 	
	}
}