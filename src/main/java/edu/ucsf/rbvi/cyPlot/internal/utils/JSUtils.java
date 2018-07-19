package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.Map;

public class JSUtils {
	
	static String preamble = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>"+
            "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>"+
									 "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
	public static String getPreamble() { return preamble; }

	public static String getScatterPlot(String x, String y, String mode, String nameSelection, String nameArray) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', mode: '" + mode + "', text: " + nameArray + "};");
		builder.append("var data = [trace1];");
		builder.append("var layout = {hovermode: 'closest', title: 'Scatter Plot'};");
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		builder.append(getClickCode("myPlot", nameSelection));
		builder.append(getLassoCode("myPlot", nameSelection));
		builder.append(getPlotly());

		return builder.toString();
	}

	public static String getHeatMap(String lowRGB, String medRGB, String highRGB, String dataArray, String colNames) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", x: " + colNames + ", type: \"heatmap\", transpose: true, colorscale: colorscaleValue}];");
		//builder.append("var data = [trace1];");
		//builder.append("var layout = {autosize: true};");
		builder.append("Plotly.newPlot('CyPlot', data);");
		//builder.append("var myPlot = document.getElementById('CyPlot');");
		//builder.append(getClickCode("myPlot", nameSelection));
		//builder.append(getLassoCode("myPlot", nameSelection));
		builder.append(getPlotly());

		return builder.toString();
	}
	
	public static String getClickCode(String plot, String nameSelection) {
		return plot+".on('plotly_click', function(data){ \n ;" +
		       "cybrowser.executeCyCommand('network select nodeList = \"" + nameSelection + ":' +data.points[0].text+'\"');});";
	}

	public static String getLassoCode(String plot, String nameSelection) {
		return	plot+".on('plotly_selected', function(data) { \n ;"+
		        "var nodelist = ''; for(var i = 0; i<data.points.length; i++) { nodelist+= (', "+nameSelection+ ":' +data.points[i].text);};" +
		        "cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');});";
	}

	public static String getPlotly() {
		return "Plotly.react();"+
		       "</script></body></html>";
	}
}