package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.HashMap;
import java.util.Map;

public class Plot {
	private Map<String, String> settings;
	private Map<String, Map<String, String>> traceSettings;
	private boolean editor;
	
	public Plot(String type,
            Map<String, String> xTraceMap, Map<String, String> yTraceMap, 
            Map<String, String> zTraceMap, Map<String, String> nameTraceMap,
            String selectionString, String nameSelection,
            String title, String xLabel, String yLabel, String mode, 
            String dataExtra, String layoutExtra, String colorscale,
            String scaleLabel, boolean editor, String plotID, String tabID) {
		settings = new HashMap<>();
		traceSettings = new HashMap<>();
		settings.put("type", type);
		traceSettings.put("xTraceMap", xTraceMap);
		traceSettings.put("yTraceMap", yTraceMap);
		traceSettings.put("zTraceMap", zTraceMap);
		traceSettings.put("nameTraceMap", nameTraceMap);
		settings.put("selectionString", selectionString);
		settings.put("nameSelection", nameSelection);
		settings.put("title", title);
		settings.put("xLabel", xLabel);
		settings.put("yLabel", yLabel);
		settings.put("mode", mode);
		settings.put("dataExtra", dataExtra);
		settings.put("layoutExtra", layoutExtra);
		settings.put("colorscale", colorscale);
		settings.put("scaleLabel", scaleLabel);
		settings.put("plotID", plotID);
		settings.put("tabID", tabID);
		this.editor = editor;
	}
	
	public String getHTML() {
		return JSUtils.getXYPlot(settings.get("scatter"), traceSettings.get("xTraceMap"), 
				traceSettings.get("yTraceMap"), traceSettings.get("zTraceMap"), 
				traceSettings.get("nameTraceMap"), settings.get("selectionString"), 
				settings.get("nameSelection"), settings.get("title"), settings.get("xLabel"), 
				settings.get("yLabel"), settings.get("mode"), settings.get("dataExtra"), 
				settings.get("layoutExtra"), settings.get("colorscale"),
                settings.get("scaleLabel"), editor);
	}
	
	public void setSetting(String str, String setting) {
		settings.put(str, setting);
	}
	
	public void addExtraSetting(String str, String setting) {
		String old = settings.get(str);
		settings.put(str, (old == null ? "" : old + ", ") + setting);
	}
	
	public void setTraceSetting(String str, Map<String, String> traceSetting) {
		traceSettings.put(str, traceSetting);
	}
	
	public void setEditor(boolean editor) {
		this.editor = editor;
	}
	
	public Map<String, String> getSettings() {
		return settings;
	}
	
	public String getSetting(String setting) {
		return settings.get(setting);
	}
	
	public Map<String, Map<String, String>> getTraceSettings() {
		return traceSettings;
	}
	
	public boolean getEditor() {
		return editor;
	}
}
