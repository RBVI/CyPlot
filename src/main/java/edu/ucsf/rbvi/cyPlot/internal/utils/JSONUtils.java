package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtils {
	public static Map<String, String> getMap(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(data);
		Map<String, String> returnMap = new HashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			// System.out.println("strKey = "+strKey);
			returnMap.put(strKey, arrayToString(obj.get(key)));
		}
		return returnMap;
	}

	public static Map<String, String> getMapNegLog(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(data);
		Map<String, String> returnMap = new HashMap<>();
		for (Object key: obj.keySet()) {
			String strKey = key.toString();
			returnMap.put(strKey, arrayToStringNegLog(obj.get(key)));
		}
		return returnMap;
	}

	public static Map<String, List<?>> getListMap(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(data);
		Map<String, List<?>> returnMap = new HashMap<>();
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
}
