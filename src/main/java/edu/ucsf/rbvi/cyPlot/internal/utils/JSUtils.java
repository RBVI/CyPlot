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

 
// TODO: lots of opportunity to clean things up here
public class JSUtils {
	public static String PLOT_CLASS = "js-plotly-plot";
	
	public static void getPreamble(StringBuilder builder, boolean editor) { 
			builder.append("<html><head>");
			builder.append("<meta charset=\"utf-8\"/>");
			builder.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,shrink-to-fit=no\"/>");
			builder.append("<meta name=\"theme-color\" content=\"#000000\"/>");
			loadWithScript(builder, "/js/react.production.min.js");
			loadWithScript(builder, "/js/react-dom.production.min.js");
			if (editor) {
				loadWithScript(builder, "/js/vendors~app~index.bundle.js");
				loadWithScript(builder, "/js/app-index.bundle.js");
				loadWithScript(builder, "/js/app.bundle.js");
				builder.append("<style>");
				loadJS(builder, "/css/main.752d5eb7.css");
				builder.append("</style>");
			} else {
				loadWithScript(builder, "/js/plotly.min.js");
			}
			builder.append("</head>");

			return;
	}

	public static String getChartEditor(String data) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, true);
		builder.append("<body>");
		builder.append("<div id=\"root\"></div>");
		builder.append("<script type=\"text/javascript\" >");
		// builder.append("alert(\"app: \"+app.App.default.toSource());");
		builder.append("var dataSources = {" + data + "};");
		builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources }), document.getElementById('root'));");
		builder.append(getPlotly());
		writeDebugFile(builder.toString(), "getChartEditor.html");
		return builder.toString();
	}
	
	public static void loadWithScript(StringBuilder builder, String js) {
		builder.append("<script>\n");
		loadJS(builder, js);
		builder.append("\n");
		builder.append("</script>\n");
	}
	
	public static String getPlotly() {
		return "Plotly.react();"+
		       "</script></body></html>";
	}
	


	public static void writeDebugFile(String string, String name) {
		
		File file = null;
		FileOutputStream fos = null;
		
		try {
			//naturally, this next line needs to be modified for individual users.
			String home = System.getProperty("user.home");
			file = new File(home+"/" + name);
			//file = new File("C:/Users/Lilly/Desktop/" + name);
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
		
	public static String getScatterPlot(String x, String y, String mode, String nameSelection, 
									                    String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		if (!editor) {
			builder.append("<body><div id=\"CyPlot\"></div>\n");
			builder.append("<script>\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel));
			builder.append("Plotly.newPlot('CyPlot', data, layout);\n");
			builder.append("var myPlot = document.getElementById('CyPlot');\n");
			builder.append(getResizeCode());
			if (nameSelection != null && nameArray != null) {
				getClickCode(builder, "myPlot", nameSelection, false);
				getLassoCode(builder, "myPlot", nameSelection, false);
			}
			builder.append(getPlotly());
			writeDebugFile(builder.toString(), "ScatterPlot.html");
		} else {
			builder.append("<body>");
			builder.append("<div id=\"CyPlot\"></div>\n");
			// builder.append("<a style=\"position:absolute; left: 100px; top: 30\" onclick='hideControls()'>Hide Controls</a>\n");
			builder.append("<script type=\"text/javascript\" >\n");
			builder.append("var dataSources = {" + xLabel + ": "+x+", "+yLabel +": "+y+"};\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel));
			builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));");
			if (nameSelection != null && nameArray != null) {
				builder.append("var myPlot = document.getElementById('CyPlot');");
				getClickCode(builder, "myPlot", nameSelection, true);
				getLassoCode(builder, "myPlot", nameSelection, true);
			}
			builder.append("</script>\n");
			addHideControlsCode(builder);
			builder.append("</body></html>");
			writeDebugFile(builder.toString(), "CyPlot.html");
		}

		return builder.toString();
	}
	
	
	
	public static String getFilledAreaPlot(String x, String y, String mode, String nameSelection, 
	                                       String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
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
	
	public static String getBarChart(String xArray, String yArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var data = [{ x: " + xArray + ", y: " + yArray + ", type: 'bar'}];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append(getResizeCode());
		builder.append(getPlotly());
		return builder.toString();
	}
	
	public static String getVolcanoPlot(String x, String y, String nameSelection, String nameArray, 
	                                    String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var xArr = " + x + ";");
		builder.append("var yArr = " + y + ";");
		builder.append("var trace1 = { x: xArr , y:  yArr , type: 'scatter', mode: 'markers', name: 'trace', text: " + nameArray + "};");
		builder.append("var data = [trace1];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append("var myPlot = document.getElementById('CyPlot');");
		builder.append(getResizeCode());
		getClickCode(builder, "myPlot", nameSelection);
		getLassoCode(builder, "myPlot", nameSelection);
		builder.append(getPlotly());
		
		return builder.toString();
	}
	
	public static String getHeatMap(String lowRGB, String medRGB, String highRGB, String dataArray, 
	                                String colNames, String yAxisArray, String title, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", x: " + colNames + ", y: " + yAxisArray + ", type: \"heatmap\", transpose: true, colorscale: colorscaleValue}];");
		builder.append("var layout = {title: '" + title + "'};");
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		builder.append(getResizeCode());
		getClickCode(builder, "myPlot", yAxisArray);
		getLassoCode(builder, "myPlot", yAxisArray);
		builder.append(getPlotly());
		return builder.toString();
	}

	public static String getViolinPlot(String x, String y, String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
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
	
	public static String getDotPlot(String x, String y, String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
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
	

	public static void getClickCode(StringBuilder builder, String plot, String nameSelection) {
		getClickCode(builder, plot, nameSelection, false);
	}

	public static void getClickCode(StringBuilder builder, String plot, String nameSelection, boolean isEditor) {
		if (isEditor) {
			builder.append("var plots = "+plot+".getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("// alert('plots: '+plots);\n");
			builder.append("// alert('plots.length: '+plots.length);\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    // alert('plplot: '+plplot);\n");
			builder.append("    plplot.on('plotly_click', function (data) { \n");
			builder.append("        alert('clicked!');\n");
			builder.append("        cybrowser.executeCyCommand('network select nodeList = \"" + nameSelection + ":' +data.points[0].text+'\"');\n");
			builder.append("    });\n");
			builder.append("}\n");
			return;
		} else {
			builder.append(plot+".on('plotly_click', function(data){ \n");
			builder.append("    cybrowser.executeCyCommand('network select nodeList = \"" + nameSelection + ":' +data.points[0].text+'\"');\n");
			builder.append("});\n");
			return;
		}
	}

	public static void getLassoCode(StringBuilder builder, String plot, String nameSelection) {
		getLassoCode(builder, plot, nameSelection, false);
	}

	public static void getLassoCode(StringBuilder builder, String plot, String nameSelection, boolean isEditor) {
		if (isEditor) {
			builder.append("var plots = "+plot+".getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    // alert('plplot: '+plplot);\n");
			builder.append("    plplot.on('plotly_selected', function (data) { \n");
		  builder.append("        var nodelist = ''; \n");
			builder.append("        for(var i = 0; i<data.points.length; i++) { \n");
			builder.append("            nodelist+= (', "+nameSelection+ ":' +data.points[i].text);\n");
			builder.append("        };\n");
			builder.append("        cybrowser.executeCyCommand('network select nodeList = \"" + nameSelection + ":' +data.points[0].text+'\"');\n");
			builder.append("    });\n");
			builder.append("}\n");
			return;
		} else {
			builder.append(plot+".on('plotly_click', function(data){ \n");
		  builder.append("    var nodelist = ''; \n");
			builder.append("    for(var i = 0; i<data.points.length; i++) { \n");
			builder.append("        nodelist+= (', "+nameSelection+ ":' +data.points[i].text);\n");
			builder.append("    };\n");
			builder.append("    cybrowser.executeCyCommand('network select nodeList = \"" + nameSelection + ":' +data.points[0].text+'\"');\n");
			builder.append("});\n");
			return;
		}
	}
	
	public static String getLabelCode(String xLabel, String yLabel) {
		return "var layout = {showlegend: true, legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "'}, yaxis: { title:'" + yLabel + "'}, title: '" + xLabel + " vs " + yLabel + "'};";
	}

	public static void addHideControlsCode(StringBuilder builder) {
		loadWithScript(builder, "/js/hideControls.js");
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
