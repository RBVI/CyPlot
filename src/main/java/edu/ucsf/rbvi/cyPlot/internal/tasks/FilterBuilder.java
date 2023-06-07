package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.command.CommandExecutorTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.Range;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 *  A utility class to create the filters via the task manager
 *  execFilterCommand is the method to create the tasks, using CyServiceRegister
 *  The X and Y axes are each a filter, with a ComposeFilter combining (AND-ing) them
 *  Most of the code is constructing the string to send to that command.
 * 
 * @author adamtreister
 *
 */
public class FilterBuilder {
	String xColumnName;
	Range xRange;
	String yColumnName;
	Range yRange;

	public FilterBuilder(String columnName, Range r)
	{
		xColumnName = columnName;
		xRange = r;
	}
	public FilterBuilder(String xcolumn, Range xRange, String yCol, Range inYRange)
	{
		this(xcolumn, xRange);
		yColumnName = yCol;
		yRange = inYRange;
	}
	//---------------------------------------------------------------
	public void makeCompositeFilter(CyServiceRegistrar registrar)
	{
		CommandExecutorTaskFactory commandTF = registrar.getService(CommandExecutorTaskFactory.class);
		TaskManager<?,?> taskManager = registrar.getService(TaskManager.class);
		if (commandTF != null && taskManager != null)
			execFilterCommand(taskManager, commandTF, makeComposite());
		else System.err.println("CommandExecutorTaskFactory or TaskManager is null");
		
	}
	
	private String makeComposite() {
		
		String filter1 = makeString(true);
		String filter2 = makeString(false);
		StringBuilder compos = new StringBuilder();
		compos.append("{\n\"id\" : \"CompositeFilter\",\n");
		compos.append("\"parameters\" : {\n \"type\" : \"ALL\"\n},\n");
		compos.append("\"transformers\" : [ \n");
		compos.append(filter1 + ", \n" + filter2);
		compos.append("] \n}\n");		
		return compos.toString();
	}	
	//---------------------------------------------------------------
	public void makeSingleFilter(CyServiceRegistrar registrar)
	{
		CommandExecutorTaskFactory commandTF = registrar.getService(CommandExecutorTaskFactory.class);
		TaskManager<?,?> taskManager = registrar.getService(TaskManager.class);
		if (commandTF != null && taskManager != null)
			execFilterCommand(taskManager, commandTF, makeString(true));
		else System.err.println("CommandExecutorTaskFactory or TaskManager is null");
		
	}
	//---------------------------------------------------------------
	private void execFilterCommand(TaskManager<?,?> taskManager, CommandExecutorTaskFactory commandTF, String json)
	{
//		System.out.println(json);
		Map<String, Object> args = new HashMap<>();
		args.put("name","cychart filter");
		args.put("json",json);
		TaskIterator ti = commandTF.createTaskIterator("filter","create", args, null);
		taskManager.execute(ti);
	}
	//---------------------------------------------------------------
	/*
	 *  put together all the default values to build the filter for either X or Y
	 */
	public String makeString(boolean isX)
	{
		String criterion, columnName;
		if (isX)
		{
			criterion = String.format("[ %.4f, %.4f ]", xRange.min(), xRange.max());
			columnName = xColumnName;
		}
		else
		{
			criterion = String.format("[ %.4f, %.4f ]", yRange.min(), yRange.max());
			columnName = yColumnName;
		}
		StringBuilder buildr = new StringBuilder();
		buildr.append("{\n");
		buildr.append("\"id\" : \"ColumnFilter\", \n" );
		buildr.append("\"parameters\" : {\n");
		addLine(buildr,"predicate", "\"BETWEEN\"", true );
		addLine(buildr,"criterion", criterion,true );
		addLine(buildr,"caseSensitive", "false",true );
		addLine(buildr,"type", "\"nodes\"",true );
		addLine(buildr,"anyMatch", "true",true );
		addLine(buildr,"columnName", inQuotes(columnName), false );
		buildr.append("\n }\n}");
		return buildr.toString();
	 }
			
	private String inQuotes(String a) {		return '"' + a + '"';	}

		
	void addLine(StringBuilder b, String attr, String value, boolean addComma)
	{
		b.append(inQuotes(attr) + " : " + value  );
		if (addComma)  b.append(",\n");
	}
		
}
