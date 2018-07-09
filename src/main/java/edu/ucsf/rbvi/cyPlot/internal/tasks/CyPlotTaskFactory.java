package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CyPlotTaskFactory extends AbstractTaskFactory {
	final CyServiceRegistrar registrar;
	public CyPlotTaskFactory(final CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	} 

	public TaskIterator createTaskIterator () {
		return new TaskIterator(new CyPlotTask(registrar));
	}

	public boolean isReady() { 
		return true; 
	}
}  