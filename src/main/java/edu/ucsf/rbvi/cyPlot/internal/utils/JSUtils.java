package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.Map;

public class JSUtils {
	public static String getHTML(Map<String, String> items) {
		String html = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
		if(items.get("type").equals("scatter")) {
			html += "<body><div id=\"scatterplot\" style=\"width:600px;height:600px;\"></div>";
			html +=  "<script> var trace1 = { x: " + items.get("x") + ", y: " + items.get("y") + ", type: 'scatter', mode: '" + items.get("mode") + "', text: " + items.get("nameArray") + "};";
			html += "var data = [trace1];";
			html += "var layout = {hovermode: 'closest', title: 'Scatter Plot'};";
			html += "Plotly.newPlot('scatterplot', data, layout);";
			html += "var myPlot = document.getElementById('scatterplot');";
			//on click stuff
			html += "myPlot.on('plotly_click', function(data){ \n ;";
			html += "cybrowser.executeCyCommand('network select nodeList = \"" + items.get("nameSelection") + ":' +data.points[0].text+'\"');});";
			//lasso and box stuff
			html += "myPlot.on('plotly_selected', function(data) { \n ;";
			html += "var nodelist = ''; for(var i = 0; i<data.points.length; i++) { nodelist+= (', "+items.get("nameSelection")+ ":' +data.points[i].text);};";
			html += "cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');});";
		}
		//important: always keep at bottom of HTML
		html += "Plotly.react();";
		html += "</script></body></html>";
		
		
		//backup code
		//known to work for scatter:
//		String html = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>";
//		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react@16.2.0/umd/react.production.min.js\"></script>";
//		html += "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
//		html += "<body><div id=\"scatterplot\" style=\"width:600px;height:600px;\"></div>";
//		html +=  "<script> var trace1 = { x: " + items.get("x") + ", y: " + items.get("y") + ", type: 'scatter', mode: '" + items.get("mode") + "', text: " + items.get("nameArray") + "};";
//		html += "var data = [trace1];";
//		html += "var layout = {hovermode: 'closest', title: 'Scatter Plot'};";
//		html += "Plotly.newPlot('scatterplot', data, layout);";
//		html += "var myPlot = document.getElementById('scatterplot');";
//		//on click stuff
//		html += "myPlot.on('plotly_click', function(data){ \n ;";
//		html += "cybrowser.executeCyCommand('network select nodeList = \"" + items.get("nameSelection") + ":' +data.points[0].text+'\"');});";
//		//lasso and box stuff
//		html += "myPlot.on('plotly_selected', function(data) { \n ;";
//		html += "var nodelist = ''; for(var i = 0; i<data.points.length; i++) { nodelist+= (', "+items.get("nameSelection")+ ":' +data.points[i].text);};";
//		html += "cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');});";
//		//important: always keep at bottom of HTML
//		html += "Plotly.react();";
//		html += "</script></body></html>";
		
		return html;
	}
}
