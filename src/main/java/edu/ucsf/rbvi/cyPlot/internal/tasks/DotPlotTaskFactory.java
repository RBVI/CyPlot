package edu.ucsf.rbvi.cyPlot.internal.tasks; 

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class DotPlotTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public DotPlotTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate the DotPlotTask,
	 *which in turn generates a dot plot.
	 *
	 *@return the TaskIterator for a DotPlotTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new DotPlotTask(serviceRegistrar));
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
