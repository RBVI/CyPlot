package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.HashMap;
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

	public static String csvToJSONArray(String str) {
		// already JSON string?
		if (str.startsWith("["))
			return str;

		// Handle quotes
		String[] splitString = str.split("\t",-1);
		if (splitString.length == 1)
			splitString = str.split(",",-1);

		String jsonStr = "[";
		for (String s: splitString) {
			jsonStr += "'"+s+"',";
		}
		return jsonStr.substring(0,jsonStr.length()-1)+"]";
	}

	public static String arrayToStringNegLog(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			String s = "[";
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

				s += "'"+String.valueOf(value)+"',";
			}
			return s.substring(0,s.length()-1)+"]";
		} else {
			return obj.toString();
		}
	}

	public static String arrayToString(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray arr = (JSONArray) obj;
			String s = "[";
			for (Object v: arr) {
				s += "'"+v.toString()+"',";
			}
			return s.substring(0,s.length()-1)+"]";
		} else {
			return obj.toString();
		}
	}
}
