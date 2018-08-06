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
		//creating menu for CyPlot
		Properties props = new Properties();
		
		//adding in the various types of plots
		Properties props1 = new Properties();
		props1.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props1.put(ServiceProperties.TITLE, "Volcano plot");
		props1.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props2 = new Properties();
		props2.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props2.put(ServiceProperties.TITLE, "Scatter plot");
		props2.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props3 = new Properties();
		props3.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props3.put(ServiceProperties.TITLE, "Heat map");
		props3.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props4 = new Properties();
		props4.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props4.put(ServiceProperties.TITLE, "Violin plot");
		props4.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props5 = new Properties();
		props5.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props5.put(ServiceProperties.TITLE, "Line graph");
		props5.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props6 = new Properties();
		props6.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props6.put(ServiceProperties.TITLE, "Bar chart");
		props6.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props7 = new Properties();
		props7.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props7.put(ServiceProperties.TITLE, "Filled area plot");
		props7.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props8 = new Properties();
		props8.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props8.put(ServiceProperties.TITLE, "Dot plot");
		props8.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		Properties props9 = new Properties();
		props1.put(ServiceProperties.PREFERRED_MENU, "Apps.CyPlot");
		props1.put(ServiceProperties.TITLE, "Graph editor");
		props1.setProperty(ServiceProperties.IN_MENU_BAR, "true");
		
		//adding CyPlot commands
		props1.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props1.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props1.setProperty(ServiceProperties.COMMAND, "volcano");
		props1.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a volcano plot from node or edge table data");
		props1.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props1.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props1.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props2.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props2.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props2.setProperty(ServiceProperties.COMMAND, "scatter");
		props2.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a scatter plot from node or edge table data");
		props2.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props2.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props2.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props3.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props3.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props3.setProperty(ServiceProperties.COMMAND, "heat");
		props3.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a heat map from node or edge table data");
		props3.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props3.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props3.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props4.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props4.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props4.setProperty(ServiceProperties.COMMAND, "violin");
		props4.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a violin plot from node or edge table data");
		props4.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props4.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props4.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props5.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props5.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props5.setProperty(ServiceProperties.COMMAND, "line");
		props5.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a line graph from node or edge table data");
		props5.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props5.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props5.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props6.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props6.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props6.setProperty(ServiceProperties.COMMAND, "bar");
		props6.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a bar chart from node or edge table data");
		props6.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props6.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props6.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props7.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props7.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props7.setProperty(ServiceProperties.COMMAND, "filled");
		props7.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a filled area plot from node or edge table data");
		props7.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props7.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props7.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");

		props8.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props8.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props8.setProperty(ServiceProperties.COMMAND, "dot");
		props8.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Create a dot area plot from node or edge table data");
		props8.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props8.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props8.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		props9.setProperty(ServiceProperties.ENABLE_FOR, "networkAndView");
		props9.setProperty(ServiceProperties.COMMAND_NAMESPACE, "cyplot");
		props9.setProperty(ServiceProperties.COMMAND, "editor");
		props9.setProperty(ServiceProperties.COMMAND_DESCRIPTION, "Access the graph editor");
		props9.setProperty(ServiceProperties.COMMAND_LONG_DESCRIPTION, "TODO");
		props9.setProperty(ServiceProperties.COMMAND_SUPPORTS_JSON, "true");
		props9.setProperty(ServiceProperties.COMMAND_EXAMPLE_JSON, "{}");
		
		//registering all services
		CyServiceRegistrar sr = getService(context, CyServiceRegistrar.class);
		
		TaskFactory vtf = new VolcanoPlotTaskFactory(sr);
		registerService(context, vtf, TaskFactory.class, props1);
		
		TaskFactory stf = new ScatterPlotTaskFactory(sr);
		registerService(context, stf, TaskFactory.class, props2);
		
		TaskFactory htf = new HeatMapTaskFactory(sr);
		registerService(context, htf, TaskFactory.class, props3);
		
		TaskFactory vlnTF = new ViolinPlotTaskFactory(sr);
		registerService(context, vlnTF, TaskFactory.class, props4);
		
		TaskFactory ltf = new LineGraphTaskFactory(sr);
		registerService(context, ltf, TaskFactory.class, props5);
		
		TaskFactory btf = new BarChartTaskFactory(sr);
		registerService(context, btf, TaskFactory.class, props6);
		
		TaskFactory ftf = new FilledAreaTaskFactory(sr);
		registerService(context, ftf, TaskFactory.class, props7);
		
		TaskFactory dtf = new DotPlotTaskFactory(sr);
		registerService(context, dtf, TaskFactory.class, props8);
	
		TaskFactory getf = new GraphEditorTaskFactory(sr);
		registerService(context, getf, TaskFactory.class, props9);
	}
}