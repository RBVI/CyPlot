package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {
	static ContainerFactory factory = new MyContainerFactory();

	public static Map<String, String> getMap(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		Map<String, Object> obj = (Map)parser.parse(data, factory);
		Map<String, String> returnMap = new LinkedHashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			returnMap.put(strKey, arrayToString(obj.get(key)));
		}
		return returnMap;
	}

	public static Map<String, String> getMapNegLog(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		Map<String, Object> obj = (Map)parser.parse(data, factory);
		Map<String, String> returnMap = new LinkedHashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			returnMap.put(strKey, arrayToStringNegLog(obj.get(key)));
		}
		return returnMap;
	}
	public static Map<String, String> getMapLog(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		Map<String, Object> obj = (Map)parser.parse(data, factory);
		Map<String, String> returnMap = new LinkedHashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			returnMap.put(strKey, arrayToStringLog(obj.get(key)));
		}
		return returnMap;
	}

	public static Map<String, List<?>> getListMap(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		Map<String, Object> obj = (Map)parser.parse(data, factory);
		Map<String, List<?>> returnMap = new LinkedHashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			returnMap.put(strKey, stringToList((JSONArray)obj.get(key)));
		}
		return returnMap;
	}

	public static List<String> csvToList(String str) {
		// Handle quotes
		String[] splitString = str.split("\t",-1);
		if (splitString.length == 1)
			splitString = str.split(",",-1);

		return Arrays.asList(splitString);
	}

	public static String csvToJSONArray(String str) {
		// already JSON string?
		if (str.startsWith("["))
			return str;

		// Handle quotes
		String[] splitString = str.split("\t",-1);
		if (splitString.length == 1)
			splitString = str.split(",",-1);

		String jsonStr = "[";
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (String s: splitString) {
			builder.append("'"+s+"',");
		}
		return builder.substring(0,builder.length()-1).toString()+"]";
	}

	public static String arrayToStringNegLog(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			StringBuilder builder = new StringBuilder();
		 	builder.append("[");
			for (Object v: arr) {
				double value;
				if (v instanceof Double)
					value = (Double)v;
				else
					value = Double.parseDouble(v.toString());

				if (value != 0.0)
					value = -Math.log10(value);
				else
					value = Double.NaN;

				builder.append("'"+String.valueOf(value)+"',");
			}
			return builder.substring(0,builder.length()-1).toString()+"]";
		} else {
			return obj.toString();
		}
	}
	public static String arrayToStringLog(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			StringBuilder builder = new StringBuilder();
		 	builder.append("[");
			for (Object v: arr) {
				double value;
				if (v instanceof Double)
					value = (Double)v;
				else
					value = Double.parseDouble(v.toString());

				if (value != 0.0)
					value = Math.log10(value);
				else
					value = Double.NaN;

				builder.append("'"+String.valueOf(value)+"',");
			}
			return builder.substring(0,builder.length()-1).toString()+"]";
		} else {
			return obj.toString();
		}
	}

	public static String arrayToString(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (Object v: arr) {
				builder.append("'"+v.toString()+"',");
			}
			return builder.substring(0,builder.length()-1).toString()+"]";
		} else {
			return obj.toString();
		}
	}

	public static String indicesToString(List<?> strList, int nth) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < strList.size()-1; i += nth) {
			builder.append(i+",");
		}
		builder.append(strList.size()-1+"]");
		return builder.toString();
	}

	public static String listToString(List<?> strList) {
		return listToString(strList,1);
	}

	public static String listToString(List<?> strList, int nth) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < strList.size()-1; i += nth) {
			if (strList.get(i) == null)
				builder.append("'',");
			else
				builder.append("'"+strList.get(i).toString()+"',");
		}
		return builder.toString()+"'"+strList.get(strList.size()-1)+"']";
	}

	public static List<String> stringToList(JSONArray arr) {
		List<String> list = new ArrayList<>();
		for (Object v: arr) {
			list.add(v.toString());
		}
		return list;
	}

	public static List<String> stringToList(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(str);
		if (obj instanceof JSONArray) {
			return stringToList((JSONArray)obj);
		}
		return null;
	}

	public static boolean haveTabID(String jsonResult, String tabID) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(jsonResult);
			if (obj instanceof JSONArray) {
				JSONArray browserList = (JSONArray) obj;
				for (Object browserObj: browserList) {
					JSONObject browser = (JSONObject) browserObj;
					if (browser.containsKey("id") && browser.get("id").toString().equals(tabID))
						return true;
				}
			}
			return false;
		} catch (ParseException pe) {
			System.out.println("Unable to parse return from cyBrowser: "+pe.toString());
			return false;
		}
	}

	public static List<String> makeUnique(List<String> list) {
		// Create a map to keep track of the number of times we've seen a term
		Map<String, Integer> termMap = new HashMap<>();
		List<String> newTerms = new ArrayList<>();
		for (String term: list) {
			if (termMap.containsKey(term)) {
				int inserts = termMap.get(term);
				String newTerm = "";
				for (int i = 0; i < inserts; i++)
					newTerm = " "+newTerm;
				newTerms.add(newTerm+term);
				termMap.put(term, inserts+1);
			} else {
				termMap.put(term, 1);
				newTerms.add(term);
			}
		}
		return newTerms;
	}

	static class MyContainerFactory implements ContainerFactory {
		/**
		 * @return A Map instance to store JSON object, or null if you want to use org.json.simple.JSONObject.
		 */
		public Map createObjectContainer() {
			return new LinkedHashMap();
		}

		/**
		 * @return A List instance to store JSON array, or null if you want to use org.json.simple.JSONArray. 
		 */
		public List creatArrayContainer() {
			return null;
		}
	}

}
