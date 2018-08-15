package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class BarChartTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public BarChartTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a task iterator which is used to generate the BarChartTask,
	 *which in turn generates a bar chart.
	 *
	 *@return the TaskIterator for a BarChartTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new BarChartTask(serviceRegistrar));
	}
	
	/**
	 *Checks to see if there are any networks. If so, returns true.
	 *
	 *@return a boolean signifying if there are existing networks
	 */
	public boolean isReady() { 
		return true; 
	}
}
