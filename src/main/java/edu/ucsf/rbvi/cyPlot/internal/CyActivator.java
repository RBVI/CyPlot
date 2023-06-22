package edu.ucsf.rbvi.cyPlot.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.TITLE;
//import org.cytoscape.browser.internal.util.IconUtil;

import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.TableColumnTaskFactory;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;




import edu.ucsf.rbvi.cyPlot.internal.tasks.VolcanoPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.utils.NodeSelectedListener;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ViolinPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.BarChartTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.DotPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.FilledAreaTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.GraphEditorTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.HeatMapTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.LineGraphTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.HistogramPlotTaskFactory;


import edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterPlotColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.BarChartColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.DotPlotColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.FilledAreaColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.GraphEditorColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.HeatMapColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.LineGraphColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ViolinPlotColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.VolcanoPlotColumnTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.HistogramPlotColumnTaskFactory;

import edu.ucsf.rbvi.cyPlot.internal.tasks.CyPlotAction;

//for cyplot icon 
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.TextIcon;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.task.TableTaskFactory;

//import edu.ucsf.rbvi.cyPlot.internal.columnTasks.Column2DFilterTaskFactory;


public class CyActivator extends AbstractCyActivator {
	
	@Override
	public void start(BundleContext context) throws Exception {

		//registering all services
		CyServiceRegistrar sr = getService(context, CyServiceRegistrar.class);
	    CyApplicationManager applicationManager = getService(context,CyApplicationManager.class);


		NodeSelectedListener nodeSelectedListener = new NodeSelectedListener(sr);
		registerService(context, nodeSelectedListener, SelectedNodesAndEdgesListener.class, new Properties());
		var iconManager = sr.getService(IconManager.class);
		var iconFont = iconManager.getIconFont("cytoscape-3", 18.0f);
		{
			
			var icon = new TextIcon("+", iconFont, 10, 10);
			var iconId = "cy::Table::Cyplot_tools";
			iconManager.addIcon(iconId, icon);
			var action = new CyPlotAction(applicationManager,icon,(float) 0.045,sr);
			
			var props = new Properties();
			props.setProperty(TITLE, "Cyplot");
			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Tools...");

			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "0.5");
			
			registerService(context, action, CyAction.class, props);
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			ScatterPlotColumnTaskFactory sctf = new ScatterPlotColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager.Cyplot");
			props.put(ServiceProperties.TITLE, "Cyplot Scatter plot");
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "scatter");
			registerService(context, sctf, TableColumnTaskFactory.class, props);
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			HistogramPlotColumnTaskFactory hpctf = new HistogramPlotColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager.Cyplot");
			props.put(ServiceProperties.TITLE, "Cyplot Histogram plot");
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "histogram");
			registerService(context, hpctf, TableColumnTaskFactory.class, props);
		}
		
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			VolcanoPlotColumnTaskFactory vctf = new VolcanoPlotColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Volcano plot");
			registerService(context, vctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			BarChartColumnTaskFactory bctf = new BarChartColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Bar chart ");
			registerService(context, bctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			DotPlotColumnTaskFactory dctf = new DotPlotColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Dot plot");
			registerService(context, dctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			FilledAreaColumnTaskFactory fctf = new FilledAreaColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Filled Area plot");
			registerService(context, fctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			GraphEditorColumnTaskFactory gctf = new GraphEditorColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Graph Editor ");
			registerService(context, gctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			HeatMapColumnTaskFactory hctf = new HeatMapColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Heat Map");
			registerService(context,hctf, TableColumnTaskFactory.class, props);
		
		}
		
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			LineGraphColumnTaskFactory lctf = new LineGraphColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Line graph");
			registerService(context,lctf, TableColumnTaskFactory.class, props);
		
		}
		{
//			CyChartManager manager = new CyChartManager(sr);
//			CyPlotColumnTaskFactory cyPlotColumnTaskFactory = new CyPlotColumnTaskFactory(manager);
			ViolinPlotColumnTaskFactory vlnctf = new ViolinPlotColumnTaskFactory(sr);
			Properties props = new Properties();
			props.setProperty("preferredTaskManager", "table.column.task.manager");
			props.put(ServiceProperties.TITLE, "Cyplot Violin plot");
			registerService(context,vlnctf, TableColumnTaskFactory.class, props);
		
		}
		
		//adding in the various types of plots
		//histogram
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Histogram plot");
//			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.IN_NETWORK_PANEL_CONTEXT_MENU, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_histogram";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot histogram plot...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "volcano");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a volcano plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
//			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
//			props.put(ServiceProperties.TITLE, "Cyplot histogram");
//			props.setProperty(TITLE, "Plot Histogram...");
//			props.setProperty(IN_MENU_BAR, "true");
//			props.setProperty(COMMAND_NAMESPACE, "cyplot");
//			props.setProperty(COMMAND, "histogram");
//			props.setProperty(COMMAND_DESCRIPTION, "Launch a Histogram chart in a separate window");
//			props.setProperty(COMMAND_LONG_DESCRIPTION,  "Launch a Cytoscap CyChart in a separate window.  "       );
//			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
//			props.setProperty(COMMAND_EXAMPLE_JSON, "{\"id\":\"my window\"}");
//			registerService(bc, histoChart2, TableColumnTaskFactory.class, props);
			TaskFactory htp = new HistogramPlotTaskFactory(sr);
			registerService(context, htp, TaskFactory.class, props);
		}
		// Volcano
		{
			
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Volcano plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_volcano";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot volcano plot...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "volcano");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a volcano plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory vtf = new VolcanoPlotTaskFactory(sr);
			registerService(context, vtf, TaskFactory.class, props);
		}

		// Scatter
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Scatter plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_scatter";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Scatter plot...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "scatter");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a scatter plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory stf = new ScatterPlotTaskFactory(sr);
			registerService(context, stf, TaskFactory.class, props);
		}

		// Heat maps
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Heat map");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_heatmap";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Heat map ...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "heat");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a heat map from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory htf = new HeatMapTaskFactory(sr);
			registerService(context, htf, TaskFactory.class, props);
		}

		// Violin plots
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Violin plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
//			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Violin";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Violin plot ...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "violin");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a violin plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory vlnTF = new ViolinPlotTaskFactory(sr);
			registerService(context, vlnTF, TaskFactory.class, props);
		}

		// Line graphs
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Line graph");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Line";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Line graph ...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "line");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a line graph from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory ltf = new LineGraphTaskFactory(sr);
			registerService(context, ltf, TaskFactory.class, props);
		}

		// Bar charts
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Bar chart");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Bar";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Bar chart ...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "bar");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a bar chart from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory btf = new BarChartTaskFactory(sr);
			registerService(context, btf, TaskFactory.class, props);
		}

		// Filled area plot
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Filled area plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Filled_area_plot";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Filled area plot...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "filled");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a filled area plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory ftf = new FilledAreaTaskFactory(sr);
			registerService(context, ftf, TaskFactory.class, props);
		}

		// Dot plot
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Dot plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Dot_plot";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Dot plot...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "dot");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a dot area plot from node or edge table data");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory dtf = new DotPlotTaskFactory(sr);
			registerService(context, dtf, TaskFactory.class, props);
		}

		// Graph editor
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Tools.CyPlot");
			props.put(ServiceProperties.TITLE, "Graph editor");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			
//			var icon = new TextIcon("/", iconFont, 10, 10); // "#" is the node table icon in the cytoscape-3 font
//			var iconId = "CyPlot_Graph_Editor";
//			iconManager.addIcon(iconId, icon);
//			props.setProperty(ServiceProperties.LARGE_ICON_ID, iconId);
//			props.setProperty(ServiceProperties.TOOLTIP, "CyPlot Graph Editor...");
//			props.setProperty("inNodeTableToolBar", "true");
//			props.setProperty("inEdgeTableToolBar", "true");
//			props.setProperty("inNetworkTableToolBar", "true");
//			props.setProperty("inUnassignedTableToolBar", "true");
//			props.setProperty(ServiceProperties.TOOL_BAR_GRAVITY, "5");
			
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "editor");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Access the graph editor");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory getf = new GraphEditorTaskFactory(sr);
			registerService(context, getf, TaskFactory.class, props);
		}
		//adding cyplot tool options as is the case for cyChart
//		ServiceReference ref = context.getServiceReference(CySwingApplication.class.getName());
//		if (ref == null)  	return;
//		CyChartManager manager = new CyChartManager(sr);
//		String version = context.getBundle().getVersion().toString();
//		manager.setVersion(version);

//		{TaskFactory stf = new ScatterPlotTaskFactory(sr);
//		Properties props = new Properties();
//		props.setProperty(ServiceProperties.TITLE, "Plot cytoScatter...");
//		props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
//		props.setProperty(ServiceProperties.COMMAND, "column");
//		props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Launch a Scatter chart in a separate window");
//		props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION,  "Launch a Cytoscape CyPlot in a separate window.  "       );
//		props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "false");
//		registerService(context, stf, TableColumnTaskFactory.class, props);}
////		
	}
}
