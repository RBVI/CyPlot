package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ScatterPlotTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public ScatterPlotTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate a ScatterPlotTask,
	 *which in turn generates a scatter plot.
	 *
	 *@return the TaskIterator for a ScatterPlotTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new ScatterPlotTask(serviceRegistrar));
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
