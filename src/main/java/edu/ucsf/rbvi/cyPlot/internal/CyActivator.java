package edu.ucsf.rbvi.cyPlot.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.cyPlot.internal.tasks.VolcanoPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ScatterPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.ViolinPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.BarChartTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.DotPlotTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.FilledAreaTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.GraphEditorTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.HeatMapTaskFactory;
import edu.ucsf.rbvi.cyPlot.internal.tasks.LineGraphTaskFactory;

public class CyActivator extends AbstractCyActivator {
  
	@Override
	public void start(BundleContext context) throws Exception {

		//registering all services
		CyServiceRegistrar sr = getService(context, CyServiceRegistrar.class);

		//adding in the various types of plots
		// Volcano
		{
			Properties props = new Properties();
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Volcano plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Scatter plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Heat map");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Violin plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Line graph");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Bar chart");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Filled area plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Dot plot");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
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
			props.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
			props.put(ServiceProperties.TITLE, "Graph editor");
			props.setProperty(ServiceProperties.IN_MENU_BAR, "true");
			props.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
			props.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
			props.setProperty(ServiceProperties.COMMAND, "editor");
			props.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Access the graph editor");
			props.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

			TaskFactory getf = new GraphEditorTaskFactory(sr);
			registerService(context, getf, TaskFactory.class, props);
		}
	}
}
