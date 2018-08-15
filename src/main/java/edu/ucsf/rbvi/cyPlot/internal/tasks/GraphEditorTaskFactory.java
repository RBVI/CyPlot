package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class GraphEditorTaskFactory implements TaskFactory {
	CyServiceRegistrar serviceRegistrar;

	public GraphEditorTaskFactory(CyServiceRegistrar reg) {
		this.serviceRegistrar = reg;
	}

	/**
	 *Creates a TaskIterator which is used to generate the GraphEditorTask,
	 *which in turn generates the plotly graph editor in a cybrowser window.
	 *
	 *@return the TaskIterator for a GraphEditorTask
	 */
	public TaskIterator createTaskIterator() { 
		return new TaskIterator(new GraphEditorTask(serviceRegistrar));
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
