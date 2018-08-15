package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class FilledAreaTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public FilledAreaTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate the FilledAreaTask,
	 *which in turn generates a filled area plot.
	 *
	 *@return the TaskIterator for a FilledAreaTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new FilledAreaTask(serviceRegistrar));
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
