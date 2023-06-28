package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.awt.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.cytoscape.util.color.Palette;


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
	public static void getPreamble(StringBuilder builder, boolean editor, String title) {
			builder.append("<html><head>");
			if (title != null)
				builder.append("<title>"+title+"</title>\n");
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
				// loadWithScript(builder, "/js/plotly.min.js");
				loadWithScript(builder, "/js/plotly.js");
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
	 * @return the assembled graph editor code
	 */
	public static String getChartEditor(String data) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, true, null);
		builder.append("<body>\n");
    builder.append("<div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
		builder.append("<script type=\"text/javascript\">\n");
		// builder.append("alert(\"app: \"+app.App.default.toSource());");
		builder.append("var dataSources = {" + data + "};\n");
		builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources }), document.getElementById('CyPlot'));\n");
		builder.append("</script>\n");
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
	 * @return the assembled completion code
	 */
	public static String getPlotly() {
		return "</body></html>";
	}

	/**
	 * Generate an HTML file representation of the code being fed to cybrowser. File
	 * path can be customized by altering "user.home" to suit individual needs. Useful for
	 * opening the code in external browsers for debugging purposes, but not essential to the
	 * functionality of the app.
	 *
	 * @param string the Java string of HTML that will be written into the file
	 * @param the desired name of the HTML file generated
	 */
	public static void writeDebugFile(String string, String name) {

		File file = null;
		FileOutputStream fos = null;

		try {
			String home = System.getProperty("user.home");
			file = new File(home+"/" + name);
			fos = new FileOutputStream(file);
			if(!file.exists()) {
				file.createNewFile();
			}
			byte[] bytesArray = string.getBytes();
			fos.write(bytesArray);
			fos.flush();
			// System.out.println("Write successful.");
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
	 * Generate the string necessary to generate a volcano plot in plotly, either
	 * with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param mode the String which determines the form the data will take ("markers"
	 * for scatter or "lines" for line)
	 * @param selectionString a cytoscape command callback for selection
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param title the plot title
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor
	 * @return the assembled scatter plot code
	 */
	public static String getScatterPlot(Map<String,String> xTraceMap, Map<String, String> yTraceMap,
	                                    Map<String, String> nameTraceMap,
	                                    String selectionString, String nameSelection,
	                                    String title, String xLabel, String yLabel,
	                                    String dataExtra, String layoutExtra, String mode, boolean editor) {
		String html = getXYPlot("scatter", xTraceMap, yTraceMap, null, nameTraceMap,
		                        selectionString, nameSelection, title, xLabel, yLabel,
		                        dataExtra, layoutExtra,
		                        mode, null, null, editor);
		writeDebugFile(html, "ScatterPlot.html");
		return html;
	}

	/**
	 * Generate the string necessary to generate a filled area plot in plotly,
	 * either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param mode the String which determines the form the data will take ("markers"
	 * for scatter or "lines" for line)
	 * @param selectionString a cytoscape command callback for selection
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param title the plot title
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor
	 * @return the assembled filled area plot code
	 */

	// FIXME: this really isn't right....
	public static String getFilledAreaPlot(String x, String y, String mode, String selectionString, String nameSelection,
                                         String nameArray, String title, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, title);
		if(!editor) {
			builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			builder.append("<script> var trace1 = { x: " + x + ", y: " + y + ", fill: 'tonexty', type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, false));
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
			getResizeCode(builder);
			builder.append("</script>\n");
			if(nameSelection != null && nameArray != null) {
				getClickCode(builder, "CyPlot", null, nameSelection, false);
				getLassoCode(builder, "CyPlot", null, nameSelection, false);
			}
			builder.append(getPlotly());
		}else {
			builder.append("<body>");
			builder.append("<div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			// builder.append("<a style=\"position:absolute; left: 100px; top: 30\" onclick='hideControls()'>Hide Controls</a>\n");
			builder.append("<script type=\"text/javascript\" >\n");
			builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", fill: 'tonexty', type: 'scatter', name: 'trace', mode: '" + mode + "', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, false));
			builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
			builder.append("</script>\n");
			if (nameSelection != null && nameArray != null) {
				getClickCode(builder, "CyPlot", null, nameSelection, true);
				getLassoCode(builder, "CyPlot", null, nameSelection, true);
			}
			addHideControlsCode(builder);
			builder.append("</body></html>");
			writeDebugFile(builder.toString(), "FilledAreaPlot.html");
		}

		return builder.toString();
	}

	/**
	 * Generate the string necessary to generate a volcano plot in plotly, either
	 * with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data (Fold Change)
	 * @param y the user-selected y-column data (-Log(pValues))
	 * @param selectionString a cytoscape command callback for selection
	 * @param nameSelection the cytoscape data column we use to show the names of points
	 * @param nameArray the list of node names
	 * @param title the plot title
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor
	 * @return the assembled volcano plot code
	 */
	public static String getVolcanoPlot(String x, String y, String selectionString, String nameSelection, String nameArray,
	                                    String title, String xLabel, String yLabel, boolean editor) {

		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, title);
		if(!editor) {
      builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			builder.append("<script>\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', mode: 'markers', name: 'trace', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, true));
			builder.append("Plotly.newPlot('CyPlot', data, layout);\n");
			getResizeCode(builder);
			builder.append("</script>\n");
			if (selectionString != null || nameSelection != null) {
				getClickCode(builder, "CyPlot", selectionString, nameSelection, false);
				getLassoCode(builder, "CyPlot", selectionString, nameSelection, false);
			}
			builder.append(getPlotly());
			writeDebugFile(builder.toString(), "VolcanoPlot.html");
		}else {
			builder.append("<body>");
			builder.append("<div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			// builder.append("<a style=\"position:absolute; left: 100px; top: 30\" onclick='hideControls()'>Hide Controls</a>\n");
			builder.append("<script type=\"text/javascript\" >\n");
			builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'scatter', mode: 'markers', name: 'trace', text: " + nameArray + "};\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, true));
			builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));");
			builder.append("</script>\n");
			if (selectionString != null || nameSelection != null) {
				getClickCode(builder, "CyPlot", selectionString, nameSelection, true);
				getLassoCode(builder, "CyPlot", selectionString, nameSelection, true);
			}
			addHideControlsCode(builder);
			builder.append("</body></html>");
			writeDebugFile(builder.toString(), "VolcanoEditor.html");
		}


		return builder.toString();
	}

	/**
	 * Generate the string necessary to generate a bar chart in plotly,
	 * either with or without the plotly graph editor.
	 *
	 * @param x the user-selected x-column data
	 * @param y the user-selected y-column data
	 * @param selectionString a cytoscape command callback for selection
	 * @param title the plot title
	 * @param xLabel the label for the x-axis
	 * @param yLabel the label for the y-axis
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor
	 * @return the assembled bar chart code
	 */

	public static String getBarChart(String x, String y, String selectionString, String nameSelection,
									                 String nameArray, String title, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, title);
		if(!editor) {
      builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			builder.append("<script>\n");
		 	builder.append("var data = [{ x: " + x + ", y: " + y + ", type: 'bar'}];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, false));
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
			getResizeCode(builder);
			if (selectionString != null || nameSelection != null) {
				getClickCodeBarChart(builder, "CyPlot", selectionString, nameSelection, false);
				getLassoCodeBarChart(builder, "CyPlot", selectionString, nameSelection, false);
			}
			builder.append("</script>\n");
			builder.append(getPlotly());
		}else {
      builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
			builder.append("<script type=\"text/javascript\" >\n");
			builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
			builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'bar'}\n");
			builder.append("var data = [trace1];\n");
			builder.append(getLabelCode(xLabel, yLabel, title, false));
			// builder.append("var config = {'responsive':true};\n"); // Will this work?  Can we pass config using the editor?
			builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
			// builder.append("var myPlot = document.getElementById('CyPlot');\n");
			builder.append("</script>\n");
			if (selectionString != null || nameSelection != null) {
				getClickCode(builder, "CyPlot", selectionString, nameSelection, true);
				getLassoCode(builder, "CyPlot", selectionString, nameSelection, true);
			}
			addHideControlsCode(builder);
			builder.append("</body></html>");
		}
		writeDebugFile(builder.toString(), "barchart.html");

		return builder.toString();
	}

	//getHeatMap javadoc by Liam, don't actually know if this is accurate (Lilly can you check?)
	/**
	 * Generate the string necessary to generate a heat map in plotly,
	 * either with or without the plotly graph editor.
	 *
		String html = JSUtils.getHeatMap(rowHeaders, columnHeaders, colData, colorPalette,
		                                 xLabel, yLabel, title, editor);
	 * @param rowHeaders the list of row headers.  Also defines the row order.
	 * @param columnHeaders the list of column headers.  Also defines the column order.
	 * @param colData a map of the data, organized by the column name as the key and a
	 *                JSON-formatted array with the column data
	 * @param selectionString the command we use to indicate selected nodes
	 * @param nameSelection the column we used to show the names of points
	 * @param colorPalette a color palette.  Could be either a String, in which case it's assumed
	 *                     to be a Plotly palette, or a Palette, and we'll provide RGB values.
	 * @param zmin the minimum value for the scale
	 * @param zmax the maximum value for the scale
	 * @param xLabel the label for the x axis
	 * @param yLabel the label for the y axis
	 * @param title the title of the chart
	 * @param editor the boolean that controls whether the graph will open in the plotly
	 * graph editor
	 * @return the assembled heat map code
	 */
	public static String getHeatMap(List<String> rowHeaders, List<String> columnHeaders,
	                                Map<String, List<?>> colData,
	                                String selectionString, String nameSelection,
	                                Object colorPalette, String xLabel, String yLabel,
	                                String title, String dataExtra, String layoutExtra, boolean editor) {
		// Get our color string
		String colors = "var colorScaleValue = "+getColorString(colorPalette)+"\n";

		StringBuilder dataArrayBuilder = new StringBuilder();
		dataArrayBuilder.append("[");
		for (String col: columnHeaders) {
			dataArrayBuilder.append(JSONUtils.listToString(colData.get(col))+",");
		}
		dataArrayBuilder.setCharAt(dataArrayBuilder.length()-1, ']');

		// Build our text data

		String dataArray = dataArrayBuilder.toString();
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, title);
    builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
		builder.append("<script>\n");
 		builder.append(colors);
	 	builder.append("var data = [{z: " + dataArray + ", x: " + JSONUtils.listToString(columnHeaders));
		builder.append(", y: " + JSONUtils.listToString(rowHeaders) + ", type: \"heatmap\", transpose: true,");

		// This doesn't actually work to pass the data right now
		// builder.append(" text: "+ JSONUtils.listToString(rowHeaders)+",");

		if (dataExtra != null)
			builder.append(dataExtra+",");
		builder.append(" colorscale: colorScaleValue}];\n");
		// We can't use getLayoutCode because of the need to use ticktext :-(
		// builder.append(getLayoutCode(xLabel, yLabel, title, layoutExtra, true));
		{
			if (title == null) title = xLabel+" vs "+yLabel;
			builder.append("var layout = ");
			builder.append("{hovermode: 'closest',");
			builder.append("yaxis: { title: '"+yLabel+"', automargin: true,");
			// builder.append(" ticktext: "+JSONUtils.listToString(rowHeaders, 4) + ",");
			// builder.append(" tickvals: "+JSONUtils.indicesToString(rowHeaders, 4) + ",");
			builder.append(" tickfont: {size: 9},");
	 		builder.append("},");
			builder.append("xaxis: { title: '"+xLabel+"', automargin: true },");
			builder.append("title: '"+title+"'");
			if (layoutExtra != null)
				builder.append(","+layoutExtra);
			builder.append("}\n");
		}

		if (editor) {
			String dataSources = "var dataSources = {";
			for (String col: colData.keySet()) {
				dataSources += "'"+col+"':"+colData.get(col)+",";
			}
			builder.append(dataSources.substring(0, dataSources.length()-1)+"};\n");
			builder.append("ReactDOM.render(React.createElement(app.App.default, ");
			builder.append("{ dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
		} else {
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
			getResizeCode(builder);
		}
		builder.append("</script>\n");
		if (selectionString != null || nameSelection != null) {
			getClickCode(builder, "CyPlot", selectionString, nameSelection, "y", editor);
			getLassoCode(builder, "CyPlot", selectionString, nameSelection, "y", editor);
		}
		if (editor)
      addHideControlsCode(builder);
		builder.append(getPlotly());
		writeDebugFile(builder.toString(), "HeatMap.html");
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
	 * @return the assembled violin plot code
	 */
	public static String getViolinPlot(Map<String, String> traceMap, String selectionString,
	                                   String nameSelection, Map<String,String> traceNamesMap,
	                                   List<String> traceOrder,
																		 String title, String xlabel, String ylabel,
																		 String dataExtra, String layoutExtra,
                                     boolean editor) {

		if (title == null)
			title = "Cytoscape Violin Plot";

		if (traceNamesMap == null || traceNamesMap.size() == 0) {
			traceNamesMap = new HashMap<>();
			for (String tr: traceMap.keySet()) {
				traceNamesMap.put(tr, "[]");
			}
		}

		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, title);
    builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
		builder.append("<script type=\"text/javascript\">\n");

		String traceStr = "var data = [";
		int traceNumber = 1;
		String dataSources = "var dataSources = {";
		for (String trace: traceOrder) {
			builder.append("var trace"+traceNumber+" = ");
			builder.append("{ legendgroup: '"+trace+"', name: '"+trace+"'");
			builder.append(", scalegroup: 'Yes', x0: '"+trace+"', ");
			builder.append("y: "+traceMap.get(trace)+", ");
			builder.append("type:'violin', ");
			builder.append("text: " + traceNamesMap.get(trace));
			if (dataExtra != null)
				builder.append(","+dataExtra);
		 	builder.append("};\n");
			traceStr += "trace"+traceNumber+",";
			traceNumber++;
			dataSources += "'"+trace+"': "+traceMap.get(trace)+",";
		}
		builder.append(traceStr.substring(0, traceStr.length()-1)+"];\n");

		if (editor)
			builder.append(dataSources.substring(0, dataSources.length()-1)+"};\n");

		if (xlabel == null)
			xlabel = "";
		if (ylabel == null)
			ylabel = "";

		if (layoutExtra == null)
			layoutExtra = "";
		layoutExtra += "violinmode: 'overlay', violingap: 0, violingroupgap: 0";
		builder.append(getLayoutCode(xlabel, ylabel, title, layoutExtra, true));

		/*
		builder.append("var layout = { hovermode: 'closest', showlegend: true, title: '"+title+"',");
		builder.append("xaxis: { title:'"+xlabel+"'}, yaxis: {title:'"+ylabel+"'},");
		if (layoutExtra)
			builder.append(layoutExtra);
		builder.append("legend: { tracegroupgap: 0 }, violingap: 0, violingroupgap: 0, violinmode: 'overlay' };\n");
		*/

		if (!editor) {
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
			getResizeCode(builder);
		} else {
			builder.append("ReactDOM.render(React.createElement(app.App.default, ");
			builder.append("{ dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
		}
		builder.append("</script>\n");
		if (selectionString != null || nameSelection != null) {
			getClickCode(builder, "CyPlot", selectionString, nameSelection, editor);
			getLassoCode(builder, "CyPlot", selectionString, nameSelection, editor);
		}
		builder.append(getPlotly());
		writeDebugFile(builder.toString(), "ViolinPlot.html");
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
	 * @return the assembled dot plot code
	 */
	public static String getDotPlot(String x, String y, String nameArray, String xLabel, String yLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		getPreamble(builder, editor, null);
    builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
		builder.append("<script>\n");
		builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
		builder.append( "var trace1 = {\n");
		builder.append("type:\"scatter\",\n");
		builder.append("mode:\"markers\",\n");
		builder.append("x: " + x + ",\n");
		builder.append("y: " + y + ",\n");
		builder.append("name: 'Highest Marks',\n");
		builder.append("marker: {\n");
		builder.append("color: 'rgba(156, 165, 196, 0.5)',\n");
		builder.append("line: {\n");
		builder.append("  color: 'rgba(156, 165, 196, 1)',\n");
		builder.append("  width: 1,\n");
		builder.append("},\n");
		builder.append("symbol: 'circle',\n");
		builder.append("size: 20\n");
		builder.append("},\n");
		builder.append("hoverlabel: {\n");
		builder.append("bgcolor: 'black',\n");
		builder.append("}\n");
		builder.append("};\n");
		builder.append("var data = [trace1];\n");
		builder.append("var layout = { title: 'dot plot'};\n");
		if (!editor) {
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
      getResizeCode(builder);
		} else {
			builder.append("ReactDOM.render(React.createElement(app.App.default, ");
			builder.append("{ dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
		}
		builder.append("</script>\n");
		builder.append(getPlotly());
		writeDebugFile(builder.toString(), "DotPlot.html");
		return builder.toString();
	}

	public static void getClickCode(StringBuilder builder, String plot, String selectionString,
	                                String nameSelection, boolean isEditor) {
		getClickCode(builder, plot, selectionString, nameSelection, null, isEditor);
	}

  public static void getClickCodeBarChart(StringBuilder builder, String plot, String selectionString,
                                  String nameSelection, boolean isEditor) {
    getClickCodeBarChart(builder, plot, selectionString, nameSelection, null, isEditor);
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
	 * @param selectionString the command we use to indicate selected nodes
	 * @param nameSelection the column we used to show the names of points
	 * @param dataElement the data element to use for the selection name
	 * @param isEditor if <b>true</b> we'll be integrating this into the
	 * PlotlyEditor.  This means we have to find the appropriate div by
	 * searching for a particular CSS class.
	 */
	public static void getClickCode(StringBuilder builder, String plot, String selectionString,
	                                String nameSelection, String dataElement, boolean isEditor) {
		if (selectionString == null)
			selectionString = "network select nodeList = \""+nameSelection+":%s\"";
		if (dataElement == null)
			dataElement = "text";

		builder.append("<script>\n");
		builder.append("var myPlot = document.getElementById('"+plot+"');\n");
		if (isEditor) {
			builder.append("var plots = myPlot.getElementsByClassName('"+PLOT_CLASS+"');\n");
			// builder.append("alert('plots = '+plots[0]);\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    plplot.on('plotly_click', function (data) { \n");
			String selString = String.format(selectionString, "'+data.points[0]."+dataElement+"+'");
			builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			builder.append("    });\n");
			builder.append("}\n");
		} else {
			builder.append("\nmyPlot.on('plotly_click', function(data){ \n");
			String selString = String.format(selectionString, "'+data.points[0]."+dataElement+"+'");
			builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			System.out.println("selString = "+selString);
			System.out.println("command = "+"cybrowser.executeCyCommand('"+selString+"');");
			builder.append("});\n");
		}
		builder.append("</script>\n");
	}


  public static void getClickCodeBarChart(StringBuilder builder, String plot, String selectionString,
	                                String nameSelection, String dataElement, boolean isEditor) {
		if (selectionString == null)
			selectionString = "network select nodeList = \""+nameSelection+":%s\"";
		if (dataElement == null)
			dataElement = "text";

		builder.append("var myPlot = document.getElementById('"+plot+"');\n");
		if (isEditor) {
			builder.append("var plots = myPlot.getElementsByClassName('"+PLOT_CLASS+"');\n");
			// builder.append("alert('plots = '+plots[0]);\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    plplot.on('plotly_click', function (data) { \n");
			String selString = String.format(selectionString, "'+data.points[0]."+dataElement+"+'");
			builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			builder.append("    });\n");
			builder.append("}\n");
		} else {
			builder.append("\nmyPlot.on('plotly_click', function(data){ \n");
			String selString = String.format(selectionString, "'+data.points[0]."+dataElement+"+'");
			builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			System.out.println("selString = "+selString);
			System.out.println("command = "+"cybrowser.executeCyCommand('"+selString+"');");
			builder.append("});\n");
		}
	}

	public static void getLassoCode(StringBuilder builder, String plot, String selectionString,
	                                String nameSelection, boolean isEditor) {
		getLassoCode(builder, plot, selectionString, nameSelection, null, isEditor);
	}

  public static void getLassoCodeBarChart(StringBuilder builder, String plot, String selectionString,
                                  String nameSelection, boolean isEditor) {
    getLassoCodeBarChart(builder, plot, selectionString, nameSelection, null, isEditor);
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
	 * @param dataElement the data element to use for the selection name
	 * @param isEditor if <b>true</b> we'll be integrating this into the
	 * PlotlyEditor.  This means we have to find the appropriate div by
	 * searching for a particular CSS class.
	 */
	public static void getLassoCode(StringBuilder builder,
	                                String plot, String selectionString, String nameSelection,
	                                String dataElement,
	                                boolean isEditor) {
		if (dataElement == null)
			dataElement = "text";

		builder.append("<script>\n");
		builder.append("var myPlot = document.getElementById('"+plot+"');\n");
		if (isEditor) {
			builder.append("var plots = myPlot.getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    plplot.on('plotly_selected', function (data) { \n");
			builder.append("        var nodelist = ''; \n");
			builder.append("        for(var i = 0; i<data.points.length; i++) { \n");
			if (selectionString == null)
				builder.append("            nodelist+= (',"+nameSelection+ ":' +data.points[i]."+dataElement+");\n");
			else
				builder.append("            nodelist+= (','+data.points[i]."+dataElement+");\n");
			builder.append("        };\n");
			if (selectionString == null)
				builder.append("        cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');\n");
			else {
				String selString = String.format(selectionString, "'+nodelist+'");
				builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			}
			builder.append("    });\n");
			builder.append("}\n");
		} else {
			builder.append("myPlot.on('plotly_selected', function(data){ \n");
		  builder.append("    var nodelist = ''; \n");
			builder.append("    for(var i = 0; i<data.points.length; i++) { \n");
			if (selectionString == null)
				builder.append("        nodelist+= (',"+nameSelection+ ":'+data.points[i]."+dataElement+");\n");
			else
				builder.append("            nodelist+= (','+data.points[i]."+dataElement+");\n");
			builder.append("    };\n");
			if (selectionString == null)
				builder.append("        cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');\n");
			else {
				String selString = String.format(selectionString, "'+nodelist+'");
				builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			}
			builder.append("});\n");
		}
		builder.append("</script>\n");
	}

  public static void getLassoCodeBarChart(StringBuilder builder,
	                                String plot, String selectionString, String nameSelection,
	                                String dataElement,
	                                boolean isEditor) {
		if (dataElement == null)
			dataElement = "text";

		builder.append("var myPlot = document.getElementById('"+plot+"');\n");
		if (isEditor) {
			builder.append("var plots = myPlot.getElementsByClassName('"+PLOT_CLASS+"');\n");
			builder.append("for (var i = 0; i < plots.length; i++) {\n");
			builder.append("    var plplot = plots[i];\n");
			builder.append("    plplot.on('plotly_selected', function (data) { \n");
			builder.append("        var nodelist = ''; \n");
			builder.append("        for(var i = 0; i<data.points.length; i++) { \n");
			if (selectionString == null)
				builder.append("            nodelist+= (',"+nameSelection+ ":' +data.points[i]."+dataElement+");\n");
			else
				builder.append("            nodelist+= (','+data.points[i]."+dataElement+");\n");
			builder.append("        };\n");
			if (selectionString == null)
				builder.append("        cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');\n");
			else {
				String selString = String.format(selectionString, "'+nodelist+'");
				builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			}
			builder.append("    });\n");
			builder.append("}\n");
		} else {
			builder.append("myPlot.on('plotly_selected', function(data){ \n");
		  builder.append("    var nodelist = ''; \n");
			builder.append("    for(var i = 0; i<data.points.length; i++) { \n");
			if (selectionString == null)
				builder.append("        nodelist+= (',"+nameSelection+ ":'+data.points[i]."+dataElement+");\n");
			else
				builder.append("            nodelist+= (','+data.points[i]."+dataElement+");\n");
			builder.append("    };\n");
			if (selectionString == null)
				builder.append("        cybrowser.executeCyCommand('network select nodeList = \"'+nodelist+'\"');\n");
			else {
				String selString = String.format(selectionString, "'+nodelist+'");
				builder.append("        cybrowser.executeCyCommand('"+selString+"');\n");
			}
			builder.append("});\n");
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
	public static String getLayoutCode(String xLabel, String yLabel, String title,
	                                   String layoutExtra, boolean showLegend) {

		if (title == null) title = xLabel+" vs "+yLabel;
		String layout = "var layout = ";
		layout += "{showLegend: "+showLegend+", legend: { x:1, y: 0.5 },";
		layout += "hovermode: 'closest',";
		layout += "xaxis: { title: '"+xLabel+"', automargin: true },";
		layout += "yaxis: { title: '"+yLabel+"', automargin: true },";
		layout += "title: '"+title+"'";
		if (layoutExtra != null)
			layout += ","+layoutExtra;
		layout += "};\n";
		return layout;
	}

	public static String getLabelCode(String xLabel, String yLabel, String title, boolean showLegend) {
		if (title == null) title = xLabel+" vs "+yLabel;
		return "var layout = {showlegend: "+showLegend+", legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "', automargin: true}, yaxis: { title:'" + yLabel + "', automargin: true}, title: '" + title + "'};";
	}
	public static String getHistogramLabelCode(String xLabel, String yLabel, String title, boolean showLegend) {
		if (title == null) title = "Frequency of "+xLabel;
		return "var layout = {showlegend: "+showLegend+", legend: { x: 1, y: 0.5 }, hovermode: 'closest', xaxis: { title:'" + xLabel + "', automargin: true}, yaxis: { title:'" + yLabel + "', automargin: true}, title: '" + title + "'};";
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
    builder.append("window.onresize = function() { \n");
    builder.append("    Plotly.relayout('CyPlot', { 'xaxis.autorange': true, 'yaxis.autorange': true});\n");
    builder.append("};\n");
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


	// Common interface for all XY Plot routines, including:
	// 	Volcano, Scatter, Bar, Line
	public static String getXYPlot(String type,
	                               Map<String, String> xTraceMap, Map<String, String> yTraceMap,
	                               Map<String, String> zTraceMap, Map<String, String> nameTraceMap,
	                               String selectionString, String nameSelection,
	                               String title, String xLabel, String yLabel, String mode,
	                               String dataExtra, String layoutExtra, String colorscale,
	                               String scaleLabel, boolean editor) {
		StringBuilder builder = new StringBuilder();
		boolean showLegend = false;
		if (xTraceMap.keySet().size() > 1)
			showLegend = true;
		getPreamble(builder, editor, title);

    builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
		builder.append("<script>\n");
		String traceStr = "var data = [";
		String dataSources = "var dataSources = {";
		int traceNumber = 1;
		for (String trace: xTraceMap.keySet()) {
			builder.append("var trace"+traceNumber+" = ");
			builder.append("{ legendgroup: '"+trace+"', name: '"+trace+"', mode:'"+mode+"', scalegroup: 'Yes',");
			if (zTraceMap != null) {
				builder.append("marker: { color: "+zTraceMap.get(trace));
				if (colorscale == null)
					colorscale = "Viridis";

				builder.append(",colorscale:'"+colorscale+"', showscale:true");
				if (scaleLabel != null)
					builder.append(", colorbar: {title:'"+scaleLabel+"'}");
				builder.append("},");
			}
			builder.append("x:"+xTraceMap.get(trace)+", y: "+yTraceMap.get(trace));
			builder.append(", type:'"+type+"', text: " + nameTraceMap.get(trace));
			if (dataExtra != null)
				builder.append(","+dataExtra);
			builder.append("};\n");

			traceStr += "trace"+traceNumber+",";
			traceNumber++;
			dataSources += "'"+trace+"X': "+xTraceMap.get(trace)+",";
			dataSources += "'"+trace+"Y': "+yTraceMap.get(trace)+",";
		}
		builder.append(traceStr.substring(0, traceStr.length()-1)+"];\n");
		builder.append(getLayoutCode(xLabel, yLabel, title, layoutExtra, showLegend));
		if (editor) {
			builder.append(dataSources.substring(0, dataSources.length()-1)+"};\n");
			builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
		} else {
			builder.append("var config = {'responsive':true};\n");
			builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
			getResizeCode(builder);
		}
		builder.append("</script>\n");
		if (selectionString != null || nameSelection != null) {
			getClickCode(builder, "CyPlot", selectionString, nameSelection, editor);
			getLassoCode(builder, "CyPlot", selectionString, nameSelection, editor);
		}
		if (editor)
			addHideControlsCode(builder);
		builder.append(getPlotly());

		writeDebugFile(builder.toString(), type+"Plot.html");
		return builder.toString();
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
		try (Stream<String> stream = new BufferedReader(new InputStreamReader(plotly.openConnection().getInputStream(), StandardCharsets.UTF_8)).lines()) {
			stream.forEach((s) -> {
				builder.append(s+"\n");
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getColorString(Object palette) {
		if (palette instanceof Palette) {
			// Get a color span and return the ends and the middle
			Color[] colors = ((Palette)palette).getColors(9);
			String colorstr = "["+getStopColor(colors[8], 0.0)+","+getStopColor(colors[4], 0.5)+
			                  ","+getStopColor(colors[0], 1.0)+"]";
			return colorstr;
		} else {
			return "'"+palette.toString()+"'";
		}
	}

	private static String getStopColor(Color clr, double stop) {
		return "["+stop+", '"+getRGBColor(clr)+"']";
	}

	private static String getRGBColor(Color clr) {
		return "rgb("+clr.getRed()+","+clr.getGreen()+","+clr.getBlue()+")";
	}
	
	//functions for histogram plot(similar to bar chart)
	public static String getHistogramPlot(String x, String y, String selectionString, String nameSelection,
            String nameArray, String title, String xLabel, String yLabel, boolean editor) {
StringBuilder builder = new StringBuilder();
getPreamble(builder, editor, title);
if(!editor) {
builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
builder.append("<script>\n");
builder.append("var data = [{ x: " + x + ", type: 'histogram'}];\n");
builder.append(getHistogramLabelCode(xLabel, yLabel, title, false));
builder.append("var config = {'responsive':true};\n");
builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
getResizeCode(builder);
if (selectionString != null || nameSelection != null) {
getClickCodeBarChart(builder, "CyPlot", selectionString, nameSelection, false);
getLassoCodeBarChart(builder, "CyPlot", selectionString, nameSelection, false);
}
builder.append("</script>\n");
builder.append(getPlotly());
}else {
builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
builder.append("<script type=\"text/javascript\" >\n");
builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'histogram'}\n");
builder.append("var data = [trace1];\n");
builder.append(getHistogramLabelCode(xLabel, yLabel, title, false));
// builder.append("var config = {'responsive':true};\n"); // Will this work?  Can we pass config using the editor?
builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
// builder.append("var myPlot = document.getElementById('CyPlot');\n");
builder.append("</script>\n");
if (selectionString != null || nameSelection != null) {
getClickCode(builder, "CyPlot", selectionString, nameSelection, true);
getLassoCode(builder, "CyPlot", selectionString, nameSelection, true);
}
addHideControlsCode(builder);
builder.append("</body></html>");
}
writeDebugFile(builder.toString(), "histogram.html");

return builder.toString();
}
	public static String getHistogramColumnPlot(String x, String y, String selectionString, 
            String nameArray, String title, String xLabel, String yLabel, boolean editor) {
StringBuilder builder = new StringBuilder();
getPreamble(builder, editor, title);
if(!editor) {
builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
builder.append("<script>\n");
builder.append("var data = [{ x: " + x + ", type: 'histogram'}];\n");
builder.append(getHistogramLabelCode(xLabel, yLabel, title, false));
builder.append("var config = {'responsive':true};\n");
builder.append("Plotly.newPlot('CyPlot', data, layout, config);");
getResizeCode(builder);

builder.append("</script>\n");
builder.append(getPlotly());
}else {
builder.append("<body><div id=\"CyPlot\" style=\"width: 100%; height: 100%\"></div>\n");
builder.append("<script type=\"text/javascript\" >\n");
builder.append("var dataSources = {'" + xLabel + "': "+x+", '"+yLabel +"': "+y+"};\n");
builder.append("var trace1 = { x: " + x + ", y: " + y + ", type: 'histogram'}\n");
builder.append("var data = [trace1];\n");
builder.append(getHistogramLabelCode(xLabel, yLabel, title, false));
// builder.append("var config = {'responsive':true};\n"); // Will this work?  Can we pass config using the editor?
builder.append("ReactDOM.render(React.createElement(app.App.default, { dataSources: dataSources, data: data, layout: layout }), document.getElementById('CyPlot'));\n");
// builder.append("var myPlot = document.getElementById('CyPlot');\n");
builder.append("</script>\n");

addHideControlsCode(builder);
builder.append("</body></html>");
}
writeDebugFile(builder.toString(), "histogram.html");

return builder.toString();
}

}
