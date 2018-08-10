package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.util.ListSingleSelection;

public class ModelUtils {
	
	//Returns the selection for a String tunable.
	public static String getTunableSelection(ListSingleSelection<String> tunable) {
		return tunable.getSelectedValue();
	}
	
	//Returns an array-formatted String of column data depending for the dataSources var.
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
		
		public static String colToDataArray(CyColumn column) {
			String dataArray = column.getName() + ": " + colToArray(column);
			return dataArray;
		}
	
	//Returns an array-formatted String of column data depending on the specified data type.
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
	
	//Returns a list of the names of columns with a certain type of value (currently only Strings or nums)
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
