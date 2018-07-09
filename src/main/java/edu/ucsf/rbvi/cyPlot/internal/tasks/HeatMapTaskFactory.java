package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class HeatMapTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public HeatMapTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	public TaskIterator createTaskIterator() { 
		// This should do something like:
		return new TaskIterator(new HeatMapTask(serviceRegistrar));
		//return null;
	}

	public boolean isReady() { 
		// Check to see if there are any networks
		// If so, return true
		return true; 
	}
}
