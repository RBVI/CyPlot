package edu.ucsf.rbvi.cyPlot.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;

import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.ucsf.rbvi.cyPlot.internal.tasks.VolcanoPlotTaskFactory;

// TODO: [Optional] Improve non-gui mode
public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {

		// See if we have a graphics console or not
		boolean haveGUI = true;
		ServiceReference ref = bc.getServiceReference(CySwingApplication.class.getName());

		if (ref == null) {
			haveGUI = false;
			// Issue error and return
		}

		// Get a handle on the CyServiceRegistrar
		CyServiceRegistrar registrar = getService(bc, CyServiceRegistrar.class);

		{
			VolcanoPlotTaskFactory volcanoPlot = new VolcanoPlotTaskFactory(registrar);
			Properties props = new Properties();
			props.setProperty(PREFERRED_MENU, "Apps.CyPlot");
			props.setProperty(TITLE, "Volcano");
			props.setProperty(IN_MENU_BAR, "true");
			props.setProperty(ENABLE_FOR, "networkAndView");
			props.setProperty(COMMAND_NAMESPACE, "cyplot");
			props.setProperty(COMMAND, "volcano");
			props.setProperty(COMMAND_DESCRIPTION, 
										    "Create a volcano plot from node or edge table data");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "TODO");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
    	props.setProperty(COMMAND_EXAMPLE_JSON, "{}");

			registerService(bc, volcanoPlot, TaskFactory.class, props);
		}

	}

}
