package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class HeatMapTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public HeatMapTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate a HeatMapTask,
	 *which in turn generates a heat map.
	 *
	 *@return the TaskIterator for a HeatMapTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new HeatMapTask(serviceRegistrar));
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
