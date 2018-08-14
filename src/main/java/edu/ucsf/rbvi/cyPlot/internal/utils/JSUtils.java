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
	
	/**
	 * Generate the string necessary to load the needed plotly and react files. 
	 *
	 * @param builder the StringBuilder we'll write the javascript into
	 * @param editor the boolean which determines whether the graph will
	 * utilize the plotly graph editor
	 */
	
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
	
	/**
	 * Generate the string necessary to generate the base plotly
	 * graph editor. 
	 *
	 * @param data the String representation of user-selected column data
	 * from cytoscape
	 */
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
	
	/**
	 * Generate the string necessary to load a js file into the selected
	 * plot as a seperate script within the HTML.
	 *
	 * @param builder the StringBuilder we'll write the javascript into
	 * @param js the path to and name of the javascript file we want to load
	 */
	public static void loadWithScript(StringBuilder builder, String js) {
		builder.append("<script>\n");
		loadJS(builder, js);
		builder.append("\n");
		builder.append("</script>\n");
	}
	
	/**
	 * Generate the string necessary to complete the code for a plotly graph,
	 * implement Plotly.react() and closing off outstanding tags.
	 * 
	 */
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
	
	/**
	 * Generate the string necessary to generate a basic scatter plot or
	 * line graph in plotly, either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param mode the String which determines the form the data will take ("markers"
	 * for scatter or "lines" for line)
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
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
			getResizeCode(builder);
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
	
	/**
	 * Generate the string necessary to generate a filled area plot in plotly, 
	 * either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param mode the String which determines the form the data will take ("markers"
	 * for scatter or "lines" for line)
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
	public static String getFilledAreaPlot(String x, String y, String mode, String nameSelection, 
        String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		if(!editor) {
			builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
			builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", fill: 'tonexty', type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};");
			builder.append("var data = [trace1];");
			builder.append(getLabelCode(xLabel, yLabel));
			builder.append("Plotly.newPlot('CyPlot', data, layout);");
			builder.append("var myPlot = document.getElementById('CyPlot');");
			getResizeCode(builder);
			if(nameSelection != null && nameArray != null) {
				getClickCode(builder, "myPlot", nameSelection, false);
				getLassoCode(builder, "myPlot", nameSelection, false);
			}
			builder.append(getPlotly());
		}else {
			builder.append("<body>");
			builder.append("<div id=\"CyPlot\"></div>\n");
			// builder.append("<a style=\"position:absolute; left: 100px; top: 30\" onclick='hideControls()'>Hide Controls</a>\n");
			builder.append("<script type=\"text/javascript\" >\n");
			builder.append("var dataSources = {" + xLabel + ": "+x+", "+yLabel +": "+y+"};\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", fill: 'tonexty', type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};\n");
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
	
	/**
	 * Generate the string necessary to generate a volcano plot in plotly, either 
	 * with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
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
		getResizeCode(builder);
		getClickCode(builder, "myPlot", nameSelection, false);
		getLassoCode(builder, "myPlot", nameSelection, false);
		builder.append(getPlotly());
		
		return builder.toString();
	}
	
	/**
	 * Generate the string necessary to generate a bar chart in plotly, 
	 * either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
	public static String getBarChart(String x, String y, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var data = [{ x: " + x + ", y: " + y + ", type: 'bar'}];");
		builder.append(getLabelCode(xLabel, yLabel));
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		getResizeCode(builder);
		builder.append(getPlotly());
		return builder.toString();
	}
	
	//getHeatMap javadoc by Liam, don't actually know if this is accurate (Lilly can you check?)
	/**
	 * Generate the string necessary to generate a heat map in plotly, 
	 * either with or without the plotly graph editor.
	 *
	 * @param lowRGB the lower-bounded color for the color gradient
	 * @param medRGB the middle-bounded color for the color gradient
	 * @param highRGB the high-bounded color for the color gradient
	 * @param dataArray the data plotted
	 * @param colNames the list of column names for data plotted
	 * @param yAxisArray the data for the y-axis
	 * @param title the title of the map
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
	public static String getHeatMap(String lowRGB, String medRGB, String highRGB, String dataArray, 
	                                String colNames, String yAxisArray, String title, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor);
		builder.append("<body><div id=\"CyPlot\" style=\"width:600px;height:600px;\"></div>");
		builder.append("<script> var colorscaleValue = [[0, '" + lowRGB + "'], [.5, '" + medRGB + "'], [1, '" + highRGB + "']]; var data = [{z: " + dataArray + ", x: " + colNames + ", y: " + yAxisArray + ", type: \"heatmap\", transpose: true, colorscale: colorscaleValue}];");
		builder.append("var layout = {title: '" + title + "'};");
		builder.append("Plotly.newPlot('CyPlot', data, layout);");
		getResizeCode(builder);
		getClickCode(builder, "myPlot", yAxisArray, false);
		getLassoCode(builder, "myPlot", yAxisArray, false);
		builder.append(getPlotly());
		return builder.toString();
	}

	/**
	 * Generate the string necessary to generate a violin plot 
	 * in plotly, either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param nameArray the list of node names
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
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
		getResizeCode(builder);
		builder.append(getPlotly());

		return builder.toString();
	}
	
	/**
	 * Generate the string necessary to generate a dot plot or
	 * in plotly, either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param nameArray the list of node names
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor 
	 */
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
		getResizeCode(builder);
		builder.append(getPlotly());

		return builder.toString();	
	}
	
	
	/**
	 * Generate the string necessary to support integrating the plotly
	 * graph resizing function. This is not in an independent
	 * javascript file because we need to construct the script from
	 * our current java objects.
	 *
	 * @param builder the StringBuilder we'll write the javascript into
	 */
	public static void getResizeCode(StringBuilder builder) {

		builder.append("(function() { \n");
		builder.append("    var d3 = Plotly.d3;\n");
		builder.append("    var WIDTH_IN_PERCENT_OF_PARENT = 94;\n");
		builder.append("    var HEIGHT_IN_PERCENT_OF_PARENT = 95;\n");
		builder.append("    var gd3 = d3.select(\"div[id='CyPlot']\")\n");
		builder.append("        .style({ width: WIDTH_IN_PERCENT_OF_PARENT + '%',\n");
		builder.append("        '    margin-left': (100 - WIDTH_IN_PERCENT_OF_PARENT) / 2 + '%',\n");
		builder.append("\n");
		builder.append("        height: HEIGHT_IN_PERCENT_OF_PARENT + 'vh',\n");
		builder.append("        'margin-top': (100 - HEIGHT_IN_PERCENT_OF_PARENT) / 2 + 'vh'\n");
		builder.append("    });\n");
		builder.append("    var gd = gd3.node();");
		builder.append("    window.onresize = function() {Plotly.Plots.resize(gd);};\n");
		builder.append("})();");
	}
	
	/**
	 * Generate the string necessary to support integrating the plotly
	 * click selection with Cytoscape.  This is not in an independent
	 * javascript file because we need to construct the script from
	 * our current java objects.
	 *
	 * @param builder the StringBuilder we'll write the javascript into
	 * @param plot the variable that points to the div containing the
	 * plotly plot.
	 * @param nameSelection the column we used to show the names of points
	 * @param isEditor if <b>true</b> we'll be integrating this into the
	 * PlotlyEditor.  This means we have to find the appropriate div by
	 * searching for a particular CSS class.
	 */
	public static void getClickCode(StringBuilder builder, String plot, 
	                                String nameSelection, boolean isEditor) {
		if (isEditor) {
			builder.append("var plots = "+plot+".getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    plplot.on('plotly_click', function (data) { \n");
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

	/**
	 * Generate the string necessary to support integrating the plotly
	 * Lasso selection with Cytoscape.  This is not in an independent
	 * javascript file because we need to construct the script from
	 * our current java objects.
	 *
	 * @param builder the StringBuilder we'll write the javascript into
	 * @param plot the variable that points to the div containing the
	 * plotly plot.
	 * @param nameSelection the column we used to show the names of points
	 * @param isEditor if <b>true</b> we'll be integrating this into the
	 * PlotlyEditor.  This means we have to find the appropriate div by
	 * searching for a particular CSS class.
	 */
	public static void getLassoCode(StringBuilder builder, 
	                                String plot, String nameSelection, 
	                                boolean isEditor) {
		if (isEditor) {
			builder.append("var plots = "+plot+".getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
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
	
	/**
	 * FIXME:  Note that since this overwrites layout, we probably don't
	 * want to do it this way.  I think we should assemble the layout object
	 * more incrementally.  Perhaps we should create a PlotlyLayout class
	 * so we can add features to it over time?  The ".toString()" method of
	 * the class would return the constructed layout string.
	 *
	 * This method creates the layout object that we'll pass to plotly.  It
	 * adds support for title, axes and legend.
	 *
	 * @param xLabel the label to show for the X axis
	 * @param yLabel the label to show for the Y axis
	 * @return the assembled label code
	 *
	 */
	public static String getLabelCode(String xLabel, String yLabel) {
		return "var layout = {showlegend: true, legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "'}, yaxis: { title:'" + yLabel + "'}, title: '" + xLabel + " vs " + yLabel + "'};";
	}

	/**
	 * Load the code that adds a link to hide and show the controls
	 * panel in the editor view.
	 *
	 * @param builder the StringBuilder that we'll write the code into
	 */
	public static void addHideControlsCode(StringBuilder builder) {
		loadWithScript(builder, "/js/hideControls.js");
	}
	
	
	/**
	 * This method loads data from the resources in the CyPlot
	 * jar file.  While the title is loadJS, we also use it for
	 * css and other files we don't want to inject directly via
	 * strings.
	 *
	 * @param builder This is the {@link StringBuilder} that we use
	 * to write the embedded content into.
	 * @param js The name of the file, including the prefix that 
	 * we're going to look for in the resources
	 */
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
