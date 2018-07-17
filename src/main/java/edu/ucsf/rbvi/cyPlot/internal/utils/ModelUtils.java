package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;

public class ModelUtils {
	
	public static String numColtoArray(List<Object> list) {
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
	}
	
	public static String stringColtoArray(List<Object> list) {
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
	
	public static List<String> getNumericOptions(Collection<CyColumn> columns) {
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
	}
	
	public static List<String> getStringOptions(Collection<CyColumn> columns) {
		List<String> strings = new ArrayList<>();
		for(CyColumn each : columns) {
			if(each.getType().isAssignableFrom(String.class)) {
				String header = each.getName();
				strings.add(header);
			}
		}
		return strings;
	}
}
