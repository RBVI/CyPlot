package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.Map;

public class JSUtils {
	
	static String preamble = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>"+
									 "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";
	public static String getPreamble() { return preamble; }

	public static String getScatterPlot(String x, String y, String mode, String nameSelection, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};");
		builder.append("var data = [trace1];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		builder.append(getResizeCode());

		builder.append(getClickCode("myPlot", nameSelection));
		builder.append(getLassoCode("myPlot", nameSelection));
		builder.append(getPlotly());

		return builder.toString();
	}
	
	public static String getFilledAreaPlot(String x, String y, String mode, String nameSelection, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", fill: 'tonexty', type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};");
		builder.append("var data = [trace1];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		builder.append(getResizeCode());
		builder.append(getPlotly());
		return builder.toString();
	}
	
	public static String getBarChart(String xArray, String yArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var data = [{ x: " + xArray + ", y: " + yArray + ", type: 'bar'}];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append(getResizeCode());
		builder.append(getPlotly());
		return builder.toString();
	}
	
	public static String getVolcanoPlot(String x, String y, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var xArr = " + x + ";");
		builder.append("for(var i = 0; i < xArr.length; i++) { ");
		builder.append("xArr[i] = ((Math.log10(xArr[i])) / (Math.log(2))); } " );
		builder.append("var yArr = " + y + ";");
		builder.append("for(var i = 0; i < yArr.length; i++) {");
		builder.append("yArr[i] = Math.log10(yArr[i]) * -1; }");		
		builder.append("var trace1 = { x: xArr , y:  yArr , type: 'scatter', mode: 'markers', name: 'trace', text: " + nameArray + "};");
		builder.append("var data = [trace1];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		//attempting resize
		builder.append(getResizeCode());

	//	builder.append(getClickCode("myPlot", nameSelection));
	//	builder.append(getLassoCode("myPlot", nameSelection));
		builder.append(getPlotly());

		return builder.toString();
	}
	
	public static String getHeatMap(String lowRGB, String medRGB, String highRGB, String dataArray, String colNames, String title) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", x: " + colNames + ", type: \"heatmap\", transpose: true, colorscale: colorscaleValue}];");
		builder.append("var layout = {title: '" + title + "'};");
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append(getPlotly());

		return builder.toString();
	}

	
	public static String getViolinPlot(String x, String y, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
	//	builder.append("<script> var xArr = [-0700363748634, -0.0763134101281, -0.0828627975443];");
	//	builder.append("var yArr = [-2.56204950828, -2.5190692685, -2.47608902871];");
	//	builder.append("var xOpp = [0.0700363748634,0.0763134101281,0.0828627975443];");
	//	builder.append("var yOpp = [2.56204950828, 2.5190692685, 2.47608902871];");
		
		builder.append("<script> var xArr = " + x + ";");
		builder.append("var xOpp = [];");
		builder.append("var yArr = " + y + ";");
		builder.append("var yOpp = [];");
		builder.append("for (var i = 0; i < xArr.length; i++){ ");
		builder.append("xOpp[i] = xArr[i] * -1; }");
		builder.append("for (var i = 0; i < yArr.length; i++){ ");
		builder.append("yOpp[i] = yArr[i] * -1; }");
		builder.append("var trace1 = { x: xArr, y: yArr, fill: 'tonextx', fillcolor: '#604d9e', line: { color: 'rgb(50,50,50)', shape:'spline', width:0.5}, mode: 'lines', name:'', opacity:0.5, type:'scatter', xaxis:'x1', yaxis:'y1'};");
		builder.append("var trace2 = { x: xOpp, y: yOpp, fill: 'tonextx', fillcolor: '#604d9e', line: { color: 'rgb(50,50,50)', shape:'spline', width:0.5}, mode: 'lines', name:'', opacity:0.5, type:'scatter', xaxis:'x1', yaxis:'y1'};");
		builder.append("var data = [trace1, trace2];");
		builder.append("var layout = { font: { family: 'Georgia, serif'}, height: 500, hovermode: 'closest', margin: { r: 65, t: 150, b: 85, l: 65}, showlegend: false, title: 'violin plot', xaxis1: { anchor: 'y1', domain: [0.0, 0.18], mirror: false, range: [-0.469636175369, 0.408030146141], showgrid: false, showline: false, showticklabels: false, ticks:'', title: 'group 1', zeroline: false}, yaxis1: { anchor: 'x1', autorange: true, domain: [0.0, 1.0], mirror: false, showgrid: false, showline: true, showticklabels: true, ticklen: 4, title: 'y', zeroline: false}};");
	//	builder.append("yaxis1: { anchor: 'x1', autorange: true, showgrid: false, showline: true, showticklabels: true, ticklen: 4, title: 'y', zeroline: false}};");
		
	//	builder.append("<script>Plotly.d3.csv(\"https://raw.githubusercontent.com/plotly/datasets/master/violin_data.csv\", function(err, rows){" );
	//	builder.append("function unpack(rows, key) {");
	//	builder.append("return rows.map(function(row) { return row[key]; });}" );
	//	builder.append("var data = [{ type: 'violin', x: unpack(rows, 'total_bill'), points: 'none', box: { visible: true}, boxpoints: false, line: { color:'black'}, fillcolor: '#8dd3c7', opacity: 0.6, meanline: { visible: true }, y0:\"total bill\" }];");
	//	builder.append("var layout = { title:\"violin plot\", xaxis: { zeroline: false }};");
	//	builder.append("var data = [{ type: 'violin', x: unpack(rows, '" + x + "'), y: unpack(rows,'" + y +  "')}];");
	//	builder.append("var layout = { title:\"" + xLabel + " vs " + yLabel +  "\"};");
	//	builder.append("Plotly.newPlot('CyPlot', data, layout)});");
		builder.append("Plotly.plot('CyPlot', {data: data, layout: layout});");
	//	builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", type: 'violin', name: 'trace', , text:" + nameArray + "};");
	//	builder.append("var data = [trace1];");
	//	builder.append("var layout = {hovermode: 'closest'};");
	//	builder.append("var layout = {showlegend: true, legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "'}, yaxis: { title:'" + yLabel + "'}, title: '" + xLabel + " vs " + yLabel + "'};");
	//	builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		//attempting resize
		builder.append(getResizeCode());

	//	builder.append(getClickCode("myPlot", nameSelection));
	//	builder.append(getLassoCode("myPlot", nameSelection));
		builder.append(getPlotly());

		return builder.toString();
	}
	
	public static String getResizeCode() {
		StringBuilder builder = new StringBuilder();

		builder.append("(function() { ");
		builder.append("var d3 = Plotly.d3;");
		builder.append("var WIDTH_IN_PERCENT_OF_PARENT = 94;");
		builder.append("var HEIGHT_IN_PERCENT_OF_PARENT = 95;");
		builder.append("var gd3 = d3.select(\"div[id='CyPlot']\")");
		//builder.append("var gd3 = d3.select('body').append('CyPlot')");
		builder.append(".style({ width: WIDTH_IN_PERCENT_OF_PARENT + '%',\n" +
				"        'margin-left': (100 - WIDTH_IN_PERCENT_OF_PARENT) / 2 + '%',\n" +
				"\n" +
				"        height: HEIGHT_IN_PERCENT_OF_PARENT + 'vh',\n" +
				"        'margin-top': (100 - HEIGHT_IN_PERCENT_OF_PARENT) / 2 + 'vh'\n" +
				"    });");
		builder.append("var gd = gd3.node();");
		builder.append("window.onresize = function() {Plotly.Plots.resize(gd);};");
		builder.append("})();");

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
	
	public static String getLabelCode(String xLabel, String yLabel) {
		return "var layout = {showlegend: true, legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "'}, yaxis: { title:'" + yLabel + "'}, title: '" + xLabel + " vs " + yLabel + "'};";
	}

	public static String getPlotly() {
		return "Plotly.react();"+
		       "</script></body></html>";
	}
}