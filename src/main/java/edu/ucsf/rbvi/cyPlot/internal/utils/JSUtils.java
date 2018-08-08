package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

 
public class JSUtils {
	
	
/*	static String preamble = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head>"+
									 "<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\"></script></head>";*/
		
	public static String getPreamble() { 
			StringBuilder builder = new StringBuilder();
			builder.append("<html><head>");
			builder.append("<script>");
			//loadJS(builder, "/js/plotly.min.js");
			//loadJS(builder, "/js/react-dom.production.min.js");
			//loadJS(builder, "/js/main.e236ec29.js");
			loadJS(builder, "/js/vendors~app~index.bundle.js");
			loadJS(builder, "/js/app-index.bundle.js");
			loadJS(builder, "app.bundle.js");
			builder.append("</script>");
			builder.append("<style>");
			//loadJS(builder, "/css/main.cf05ff36.css");
			loadJS(builder, "/css/main.752d5eb7.css");
			builder.append("</style>");
			//builder.append("<script type=\"text/javascript\" src=\"https://unpkg.com/react-dom@16.2.0/umd/react-dom.production.min.js\">>");
			builder.append("</script></head>");

			return builder.toString(); 
	}
	
	
//	public static String getChartEditor() {
//		StringBuilder builder = new StringBuilder();
//		builder.append(getPreamble());
//		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
//		builder.append("<script> const dataSources = {cy1: [1, 2, 3], cy2: [4, 3, 2], cy3: [17, 13, 9]}");
//		builder.append("var _react = require('react');");
//		builder.append("var _react2 = _interopRequireDefault(_react);");
//		builder.append("var _reactDom = require('react-dom');");
//		builder.append("var _reactDom2 = _interopRequireDefault(_reactDom);");
//		builder.append("require('./index.css');");
//		builder.append("var _App = require('./App');");
//		builder.append("var _App2 = _interopRequireDefault(_App);");
//		builder.append("function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }");
//		builder.append("_reactDom2.default.render(_react2.default.createElement(_App2.default, {dataSources: this.dataSources}), document.getElementById('root'));");
//		builder.append(getPlotly());
//
//		writeDebugFile(builder.toString(), "getChartEditor.html");
//
//		return builder.toString();
//	}

	public static String getChartEditor() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<meta charset=\"utf-8\"/>");
		builder.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,shrink-to-fit=no\"/>");
		builder.append("<meta name=\"theme-color\" content=\"#000000\"/>");
		builder.append("<title>Simple App</title>");
		builder.append("<body>");
		builder.append("<noscript>You need to enable JavaScript to run this app.</noscript>");
		builder.append("<div id=\"root\"></div>");
		builder.append("<script type=\"text/javascript\" >");
		builder.append("alert(\"app: \"+app.App.default.toSource());");
		builder.append("var dataSources = {col1: [1, 2, 3], col2: [4, 3, 2], col3: [17, 13, 9] };");
		builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources }), document.getElementById('root'));");
		builder.append("</script></body></html>");
		builder.append(getPlotly());
		writeDebugFile(builder.toString(), "getIndex.html");
		return builder.toString();
				//could not find where/how to load these, unsure of importance(?)
				//<link rel="manifest" href="file:///tmp/react-chart-editor/manifest.json"/>
				//<link rel="shortcut icon" href="file:///tmp/react-chart-editor/favicon.ico"/>
			 // <script src="https://unpkg.com/react@16/umd/react.development.js" crossorigin></script>
		  	//<script src="https://unpkg.com/react-dom@16/umd/react-dom.development.js" crossorigin></script>
	}
	


	public static void writeDebugFile(String string, String name) {
		
		File file = null;
		FileOutputStream fos = null;
		
		try {
			//naturally, this next line needs to be modified for individual users.
			file = new File("/Users/liammagee/Desktop/" + name);
			//file = new File("C:\\Users\\Lilly\\Desktop\\" + name);
			fos = new FileOutputStream(file);
			if(!file.exists()) {
				file.createNewFile();
			}
			byte[] bytesArray = string.getBytes();
			fos.write(bytesArray);
			fos.flush();
			System.out.println("Write successful.");
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(fos != null) {
					fos.close();
				}
			}catch (IOException e) {
				System.out.println("Error...");
			}
		}
	}
		
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
	
	public static String getVolcanoPlot(String x, String y, String nameSelection, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var xArr = " + x + ";");
	//	builder.append("for(var i = 0; i < xArr.length; i++) { ");
	//	builder.append("xArr[i] = ((Math.log10(xArr[i])) / (Math.log(2))); } " );
		builder.append("var yArr = " + y + ";");
	//	builder.append("for(var i = 0; i < yArr.length; i++) {");
	//	builder.append("yArr[i] = Math.log10(yArr[i]) * -1; }");		
		builder.append("var trace1 = { x: xArr , y:  yArr , type: 'scatter', mode: 'markers', name: 'trace', text: " + nameArray + "};");
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
	
	public static String getHeatMap(String lowRGB, String medRGB, String highRGB, String dataArray, String colNames, String yAxisArray, String title) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", x: " + colNames + ", y: " + yAxisArray + ", type: \"heatmap\", transpose: true, colorscale: colorscaleValue}];");
		builder.append("var layout = {title: '" + title + "'};");
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append(getResizeCode());
		//builder.append(getClickCode("myPlot", yAxisArray));
		//builder.append(getLassoCode("myPlot", yAxisArray));
		builder.append(getPlotly());
		return builder.toString();
	}

	
	public static String getViolinPlot(String x, String y, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");		
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
		builder.append("Plotly.plot('CyPlot', {data: data, layout: layout});");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		//attempting resize
		builder.append(getResizeCode());
		builder.append(getPlotly());

		return builder.toString();
	}
	
	public static String getDotPlot(String x, String y, String nameArray, String xLabel, String yLabel) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPreamble());
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var myPlot = document.getElementById(\"CyPlot\");");
		builder.append( "var trace1 = {");
		builder.append("type:\"scatter\",");
		builder.append("mode:\"markers\",");
		builder.append("x: " + x + ",");
		builder.append("x: " + y + ",");
		builder.append("name: 'Highest Marks',");
		builder.append("marker: {");
		builder.append("color: 'rgba(156, 165, 196, 0.5)',");
		builder.append("line: {");
		builder.append("  color: 'rgba(156, 165, 196, 1)',");
		builder.append("  width: 1,");
		builder.append("},");
		builder.append("symbol: 'circle',");
		builder.append("size: 20");
		builder.append("},");
		builder.append("hoverlabel: {");
		builder.append("bgcolor: 'black',");
		builder.append("}");
		builder.append("};");		
		builder.append("var data = [trace1];");
		builder.append("var layout = { title: 'dot plot', xaxis: { showgrid: false, showline: true, linecolor: 'rgb(200,0,0)', ticks:'inside', tickcolor:'rgb(200,0,0)', tickwidth:4}, legend: { bgcolor: 'white', borderwidth:1, bordercolor:'black', orientation:'h', xanchor: 'center', x: 0.5, font: {size:12}}, paper_bgcolor: 'rgb(255,230,255)', plot_bgcolor:'rgb(255,230,255)'};");
		builder.append("Plotly.plot(myPlot, data, layout);");
		builder.append(getResizeCode());
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
	
	private static void loadJS(StringBuilder builder, String js) {
		URL plotly = JSUtils.class.getClassLoader().getResource(js);
		try (Stream<String> stream = new BufferedReader(new InputStreamReader(plotly.openConnection().getInputStream())).lines()) {
			stream.forEach((s) -> {
				builder.append(s+"\n");
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}