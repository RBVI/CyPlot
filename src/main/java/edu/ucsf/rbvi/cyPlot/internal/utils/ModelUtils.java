package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;

public class ModelUtils {
	
	//Returns an array-formatted String of column data depending on the specified data type.
	public static String colToArray(List<Object> list, String type) {
		if(type.equals("string")) {
			String array = "[";
			for(int i = 0; i<list.size(); i++) {
				if(i != list.size()-1) {
					array += ("'" + list.get(i) + "', ");
				}else {
					array += ("'" + list.get(i) + "']");
				}
			}
			return array;
		}else if(type.equals("num")) {
			String array = "[";
			for(int i = 0; i<list.size(); i++) {
				array += (""+list.get(i));
				if(i != list.size()-1) {
					array += ", ";
				}else {
					array += "]"; 
				}
			}
			return array;
		}else {
			return null;
		}
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
		}else {
			return null;
		}
	}
}
