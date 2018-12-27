package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

public class ModelUtils {
	
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
		String dataArray = "";
		for(int i = 0; i<columns.size(); i++) {
			String oneCol = "'" + columns.get(i).getName() + "' : " + colToArray(columns.get(i));
			if (i != columns.size()-1) {
				dataArray += oneCol + ",";
			}
			else {	
			dataArray += oneCol;
			}
		}
		return dataArray;
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
		String array = "[";
		for(int i = 0; i<list.size(); i++) {
			if(i != list.size()-1) {
				array += ("'" + list.get(i) + "', ");
			}else {
				array += ("'" + list.get(i) + "']");
			}
		}
		return array;
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
}
