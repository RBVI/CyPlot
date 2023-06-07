package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.json.JSONResult;

abstract public class AbstractEmptyObservableTask extends AbstractTask implements ObservableTask {
	@Override
	abstract public void run(TaskMonitor monitor);

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) 
			return (R)"";
		else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> { return "{}"; };
			return (R)res;
		}
		return null;
	}

	@Override
	public List<Class<?>> getResultClasses() {
		return Arrays.asList(String.class, JSONResult.class);
	}

	@SuppressWarnings("unchecked")
	public <R> R getIDResults(Class<? extends R> type, String ID) {
		if (type.equals(String.class)) {
			if (ID != null)
				return (R)ID;
			return (R)"";
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> { 
				if (ID != null) {
					return "{\"id\":\""+ID+"\"}";
				} else {
					return "{\"id\":\"\"}"; 
				}
			};
			return (R)res;
		}
		return null;
	}
}
