package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;

public class GraphEditorTask {
	String html = 

	Map<String, Object> args = new HashMap<>();		
	args.put("text", html);
	args.put("title", "Scatter Plot");

	TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "show", args, null);
	sTM.execute(ti);
}
