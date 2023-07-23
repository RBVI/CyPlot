package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

public class ModelUtils {
	public static Map<CyNetwork, List<Plot>> plots; // Maps the Network SUID to the list of corresponding tabIDs 
	public static Map<Plot, Map<CyNode, Integer>> nodeIndexLookup;
	/**
	 * Get the user selection for a tunable.
	 *
	 * @param tunable the list of options for the user to choose from
	 * @return the value from the list selected by the user
	 */
	public static String getTunableSelection(ListSingleSelection<String> tunable) {
		return tunable.getSelectedValue();
	}
	
	/**
	 * Get the user selection for a tunable.
	 *
	 * @param tunable the list of options for the user to choose from
	 * @return the value from the list selected by the user
	 */
	public static List<String> getTunableSelections(ListMultipleSelection<String> tunable) {
		return tunable.getSelectedValues();
	}
	
	/**
	 * Get a JSON-formatted String of column data from a List of CyColumns. This is generally passed in as the 
	 * dataSources var within JSUtils methods.
	 *
	 * @param columns the List of CyColumns
	 * @return the String of column data formatted as JSON
	 */
	public static String colsToDataSourcesArray(List<CyColumn> columns) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i<columns.size(); i++) {
			String oneCol = "'" + columns.get(i).getName() + "' : " + colToArray(columns.get(i));
			if (i != columns.size()-1) {
				builder.append(oneCol + ",");
			}
			else {	
				builder.append(oneCol);
			}
		}
		return builder.toString();
	}
	
	//Returns an array-formatted String of column data depending on the specified data type.
	/**
	 * Get a JSON-formatted String of column data from a single CyColumn.
	 *
	 * @param column the CyColumn in question
	 * @return the String of column data formatted as JSON
	 */
	public static String colToArray(CyColumn column) {
		List<Object> list = column.getValues(column.getType());
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(int i = 0; i<list.size(); i++) {
			if(i != list.size()-1) {
				builder.append("'" + list.get(i) + "', ");
			}else {
				builder.append("'" + list.get(i) + "']");
			}
		}
		return builder.toString();
	}

	//Returns an array-formatted String of column data depending on the specified data type.
	/**
	 * Get a JSON-formatted String of the negative log of column data from a single CyColumn.
	 *
	 * @param column the CyColumn in question
	 * @return the String of column data formatted as JSON
	 */
	public static String colToArrayNegLog(CyColumn column) {
		List<Object> list = column.getValues(column.getType());
		String array = "[";
		for(int i = 0; i<list.size(); i++) {
			String value = "";
			if (list.get(i) != null) {
				double v = (Double) list.get(i);
				if (v != 0.0)
					value = String.valueOf(-Math.log10(v));
			}
			if(i != list.size()-1) {
				array += ("'" + value + "', ");
			}else {
				array += ("'" + value + "']");
			}
		}
		return array;
	}
	public static String colToArrayLog(CyColumn column) {
		List<Object> list = column.getValues(column.getType());
		String array = "[";
		for(int i = 0; i<list.size(); i++) {
			String value = "";
			if (list.get(i) != null) {
				double v = (Double) list.get(i);
				if (v != 0.0)
					value = String.valueOf(Math.log10(v));
			}
			if(i != list.size()-1) {
				array += ("'" + value + "', ");
			}else {
				array += ("'" + value + "']");
			}
		}
		return array;
	}
	
	/**
	 * Get a list of names of either numeric or string-based columns, or a list of all column names in a collection.
	 *
	 * @param columns the Collection of CyColumns
	 * @param type the type of data we want the column to contain, either "string," "num," or "all" (if we do not want
	 * to discriminate)
	 * @return a list of the names of columns with a certain type of value (or a list of all column names within the collection)
	 */
	public static List<String> getColOptions(Collection<CyColumn> columns, String type) {
		if(type.equals("num")) {
			List<String> headers = new ArrayList<>();
			for(CyColumn each : columns) {
				if(!each.getType().isAssignableFrom(String.class) && 
						!each.getType().isAssignableFrom(Boolean.class) &&
						!each.getType().isAssignableFrom(List.class) &&
						!each.getName().equals(CyNetwork.SUID) && 
						!each.getName().equals(CyNetwork.SELECTED)) {
					String header = each.getName();
					headers.add(header);
				}
			}
			return headers;
		} else if(type.equals("string")) {
			List<String> strings = new ArrayList<>();
			for(CyColumn each : columns) {
				if(each.getType().isAssignableFrom(String.class)) {
					String header = each.getName();
					strings.add(header);
				}
			}
			return strings;
		}else if(type.equals("all")) {
			List<String> strings = new ArrayList<>();
			for(CyColumn each : columns) {
				String header = each.getName();
				strings.add(header);
			}
			return strings;
		}else {
			return null;
		}
	}

	public static void listCyBrowsers(CyServiceRegistrar sr, TaskObserver observer) {
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);	
		Map<String, Object> args = new HashMap<>();
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "list", args, observer);
		sTM.execute(ti);
	}
	public static void openCyBrowser(CyServiceRegistrar sr, String html, String title, String tabID, boolean newTab) {
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);	

		Map<String, Object> args = new HashMap<>();
		args.put("text", html);
		args.put("title", title);
		args.put("id", tabID);
		args.put("newTab", newTab);
		
		TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
		sTM.execute(ti);
	}
	
	public static void addPlot(CyNetwork network, Plot plot) {
		if (plots == null) plots = new HashMap<>();
		if (network == null)
			return; // There is no network, so we're not going to be selecting
		if (!plots.containsKey(network)) {
			plots.put(network, new ArrayList<>());
			if (nodeIndexLookup == null) nodeIndexLookup = new HashMap<>();
			if (!nodeIndexLookup.containsKey(plot)) {
				Map<CyNode, Integer> lookup = new HashMap<>();
				List<CyNode> nodes = network.getNodeList();
				if (nodes != null && !nodes.isEmpty()) {
					int ind = 0;
					for (CyNode node: network.getNodeList())
						lookup.put(node, ind++);
				}
				nodeIndexLookup.put(plot, lookup);
			}
		}
		plots.get(network).add(plot);
	}
	
	public static int[] getNodeIndices(Plot plot, Collection<CyNode> selectedNodes) {
		if (nodeIndexLookup == null) return null;
		Map<CyNode, Integer> lookup = nodeIndexLookup.get(plot);
		if (lookup == null) return null;
		
		int[] selectedIndices = new int[selectedNodes.size()];
		int ind = 0;
		for (CyNode selected: selectedNodes) {
			if (!lookup.containsKey(selected)) continue; // ignore nodes which were not there when plot was created
			if (ind == selectedIndices.length) break;
			selectedIndices[ind++] = lookup.get(selected);
		}
		return selectedIndices;
	}
	
	public static List<Plot> getPlots(CyNetworkView netView) {
		return getPlots(netView.getModel());
	}
	
	public static List<Plot> getPlots(CyNetwork network) {
		if (plots != null && plots.containsKey(network)) 
			return plots.get(network);
		return null;
	}
}
