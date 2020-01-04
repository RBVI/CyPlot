package edu.ucsf.rbvi.cyPlot.internal.tasks;

import javax.swing.JFrame;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.simple.parser.ParseException;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.color.BrewerType;
import org.cytoscape.util.color.Palette;
import org.cytoscape.util.color.PaletteProvider;
import org.cytoscape.util.color.PaletteProviderManager;
import org.cytoscape.util.color.PaletteType;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;


public class HeatMapTask extends AbstractTask {
	
	final CyServiceRegistrar sr;

	@Tunable (description="Data columns")
	public ListMultipleSelection<String> cols;
	
	@Tunable (description="Row labels")
	public ListSingleSelection<String> idColumn;
		
	@Tunable (description="Color palette to use")
	public ListSingleSelection<String> palette;
	
	@Tunable (description="Open in plot editor?")
	public boolean editor;

	// Command interface for non-network plots
	@Tunable (description="JSON formatted string of data points (in column order)", context="nogui")
	public String data = null;

	@Tunable (description="JSON formatted string of column headers", context="nogui")
	public String columnLabels = null;

	@Tunable (description="JSON formatted string of row headers", context="nogui")
	public String rowLabels = null;

	@ContainsTunables
	public CommandTunables commandTunables = new CommandTunables();
	
	public CyApplicationManager appManager;
	public CyNetworkView netView;
	public CyNetwork network;
	public CyTable table;
	public Collection<CyColumn> columns;

	private Map<String, Palette> paletteMap;
	
	public HeatMapTask(final CyServiceRegistrar sr) {
		super();
		this.sr = sr; 
		appManager = sr.getService(CyApplicationManager.class);
		netView = appManager.getCurrentNetworkView();
		if (netView != null) {
			network = netView.getModel();
			table = network.getDefaultNodeTable();
			columns = table.getColumns();
			editor = true;

			List<String> headers = ModelUtils.getColOptions(columns, "num");

			List<String> names = ModelUtils.getColOptions(columns, "string");

			cols = new ListMultipleSelection<>(headers);

			idColumn = new ListSingleSelection<>(names);
		}

		// Get the list of palette providers
		paletteMap = new HashMap<>();
		addPalettes(paletteMap, BrewerType.DIVERGING);
		addPalettes(paletteMap, BrewerType.SEQUENTIAL);

		List<String> paletteList = new ArrayList<>(paletteMap.keySet());
		Collections.sort(paletteList);

		palette = new ListSingleSelection<>(paletteList);

		// Set a reasonable default
		palette.setSelectedValue("ColorBrewer Diverging Red-Blue");

		commandTunables = new CommandTunables();
	}
	
	public List<String> getColsSelection() {
		return cols.getSelectedValues();
	}
	
	/**
	 * Generate the variables necessary to create a bar chart in plotly with the cytoscape 
	 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
	 *
	 * @param monitor the TaskMonitor required for this method by the parent 
	 * AbstractTask class
	 */
	public void run(TaskMonitor monitor) { 
		//SynchronousTaskManager sTM = sr.getService(SynchronousTaskManager.class);
		TaskManager sTM = sr.getService(TaskManager.class);
		CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);	

		List<String> rowHeaders;
		List<String> columnHeaders;
		Map<String, List<?>> colData;

		double zmin = Double.MAX_VALUE;
		double zmax = Double.MIN_VALUE;

		if (data == null) {
			CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(idColumn));

			columnHeaders = getColsSelection();
			rowHeaders = yColumn.getValues(String.class);
			colData = new HashMap<>();
			for (String colName: columnHeaders) {
				CyColumn column = table.getColumn(colName);
				List<Object> list = column.getValues(column.getType());

				double[] minmax = getMinMax(list);
				if (minmax[0] < zmin)
					zmin = minmax[0];
				if (minmax[1] > zmax)
					zmax = minmax[1];

				colData.put(colName, list);
			}

		} else {
			// FIXME: there is a bug (undocumented feature) in plotly.js that silently drops
			// rows if the row header is a duplicate.  This patently sucks.  The only workaround
			// is to force the labels to be unique (ugh)
			rowHeaders = getHeaders(rowLabels, true);

			columnHeaders = getHeaders(columnLabels, false);
			// System.out.println("rowHeaders.size = "+rowHeaders.size());

			try {
				colData = JSONUtils.getListMap(data);
			} catch (ParseException pe) {
				monitor.showMessage(TaskMonitor.Level.ERROR, "Parse error in 'data': "+pe.toString());
				// System.out.println(data);
				return;
			}

			// for (String col: colData.keySet())
			// 	System.out.println("colData.get("+col+").size() = "+colData.get(col).size());

			// Get our min/max
			for (String colName: columnHeaders) {
				List<?> list = colData.get(colName);
				double[] minmax = getMinMax(list);
				if (minmax[0] < zmin)
					zmin = minmax[0];
				if (minmax[1] > zmax)
					zmax = minmax[1];
			}
		}

		if (Math.abs(zmin) > zmax)
			zmax = Math.abs(zmin);
		else
			zmin = Math.copySign(zmax, zmin);

		if (commandTunables.xLabel == null)
			commandTunables.xLabel = "";

		if (commandTunables.yLabel == null)
			commandTunables.yLabel = "";

		String ids;
		if (idColumn == null)
			ids = null;
		else
			ids = idColumn.getSelectedValue();

		// String rowHeaders = "{'':"+ModelUtils.colToArray(yColumn)+"}";

		if (palette.getSelectedValue() == null || palette.getSelectedValue().length() == 0) {
			palette.setSelectedValue("ColorBrewer Diverging Red-Blue");
		}

		Palette colorPalette = paletteMap.get(palette.getSelectedValue());

		String dataExtra = "zmin: '"+zmin+"', zmax: '"+zmax+"', ygap: '.2', xgap: '2'";

		// getClusteredHeatMap(List<String> rowOrder, List<String> columnOrder, 
		//                     Map<String, List<String>> rowGroupMap, Map<String, List<String>> colGroupMap,
		//                     Map<String, double[]> rowData
		//                     xLabel, yLabel, title, editor);
		//
		// data, colorPalette, xLabel, yLabel, title, editor);
		// rowHeaders = "{ 'group1':[val1, val2, val3, ...], 'group2':...}"
		// colHeaders = "{ 'group1':[val1, val2, val3, ...], 'group2':...}"
		// zValues = "{'rowHeader1':[[z1,z2,z3, ...],[z5,z6,z7,...]],'rowHeader2': }"
		/*
	public static String getHeatMap(List<String> rowHeaders, List<String> columnHeaders, 
	                                Map<String, String> colData,
	                                String selectionString, String nameSelection,
	                                Object colorPalette, String xLabel, String yLabel,
	                                String title, boolean editor) {
		*/
		String html = JSUtils.getHeatMap(rowHeaders, columnHeaders, colData,
	                                   commandTunables.selectionString, ids, 
		                                 colorPalette, commandTunables.xLabel, commandTunables.yLabel, commandTunables.title, 
		                                 dataExtra, null, editor);

		ModelUtils.openCyBrowser(sr, html, commandTunables.title, commandTunables.id+":HeatMap", true);
	}

	private void addPalettes(Map<String, Palette> map, PaletteType type) {
		List<PaletteProvider> providers = 
						sr.getService(PaletteProviderManager.class).getPaletteProviders(type, false);

		for (PaletteProvider provider: providers) {
			for (Object paletteID: provider.listPaletteIdentifiers(type, false)) {
				Palette p = provider.getPalette(paletteID, 9);
				String name = provider.getProviderName()+" ";
				if (!name.contains("Divergent"))
					name += type+" ";
				name += p.getName();
				map.put(name, p);
			}
		}
	}

	private String getColorString(Palette palette, int index) {
		Color clr = palette.getColors()[index];
		return "rgb("+clr.getRed()+","+clr.getGreen()+","+clr.getBlue()+")";
	}

	private List<String> getHeaders(String input, boolean makeUnique) {
		List<String> headers;
		// First, see if we've got a JSON-formatted list
		try {
			headers = JSONUtils.stringToList(input);
		} catch (ParseException pe) {
			// OK, probably a csv
			headers = JSONUtils.csvToList(input);
		}
		if (makeUnique)
			return JSONUtils.makeUnique(headers);
		return headers;
	}

	private double[] getMinMax(List<?> list) {
		double[] minmax = new double[2];
		minmax[0] = Double.MAX_VALUE;
		minmax[1] = Double.MIN_VALUE;

		for (Object v: list) {
			double value = 0.0;
			if (v instanceof Double) {
				value = ((Double)v).doubleValue();
			} else if (v instanceof Integer) {
				value = ((Integer)v).doubleValue();
			} else if (v instanceof Long) {
				value = ((Long)v).doubleValue();
			} else if (v instanceof Float) {
				value = ((Float)v).doubleValue();
			} else if (v instanceof String) {
				value = Double.parseDouble((String)v);
			}
			if (value < minmax[0])
				minmax[0] = value;
			if (value > minmax[1])
				minmax[1] = value;
		}
		return minmax;
	}
}
