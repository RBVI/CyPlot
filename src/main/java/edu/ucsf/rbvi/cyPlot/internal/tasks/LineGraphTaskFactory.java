package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class LineGraphTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public LineGraphTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate a LineGraphTask,
	 *which in turn generates a line graph.
	 *
	 *@return the TaskIterator for a LineGraphTask
	 */
	public TaskIterator createTaskIterator() { 
		// This should do something like:
		return new TaskIterator(new LineGraphTask(serviceRegistrar));
		//return null;
	}

	/**
	 *Checks to see if there are any networks. If so, returns true.
	 *
	 *@return a boolean signifying if there are existing networks
	 */
	public boolean isReady() { 
		// Check to see if there are any networks
		// If so, return true
		return true; 
	}
}
