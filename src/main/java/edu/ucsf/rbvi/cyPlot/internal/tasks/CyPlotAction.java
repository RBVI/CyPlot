package edu.ucsf.rbvi.cyPlot.internal.tasks;
import javax.swing.*;  

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;


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
/*
 * #%L
 * Cytoscape Table Browser Impl (table-browser-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2021 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

@SuppressWarnings("serial")
public class CyPlotAction extends AbstractCyAction {

	private static String TITLE = "Cyplot Tools...";
	
    
    
    private final CyApplicationManager applicationManager;
    
	private final CyServiceRegistrar serviceRegistrar;

	public CyPlotAction(CyApplicationManager applicationManager,

            
            
			Icon icon,
			float toolbargravity,
			CyServiceRegistrar serviceRegistrar
	) {
		super(TITLE);
		
        this.applicationManager = applicationManager;
        
        
        
		this.serviceRegistrar = serviceRegistrar;
		
		putValue(SHORT_DESCRIPTION, TITLE);
		putValue(LARGE_ICON_KEY, icon);

		setIsInNodeTableToolBar(true);
		setIsInEdgeTableToolBar(true);
		setIsInNetworkTableToolBar(false);
		setIsInUnassignedTableToolBar(false);
		
		setToolbarGravity(toolbarGravity);
		

		insertSeparatorBefore = true;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		var source = evt.getSource();
		
		if (source instanceof Component)
			showCreateColumnPopup((Component) source);
	}
	
	private void showCreateColumnPopup(Component invoker) {
		var popup = new JPopupMenu();
			
		var columnRegular = new JMenu("Cyplot tools");
		

		columnRegular.add(getJMenuItemScatter(false));
		columnRegular.add(getJMenuItemHistogram(false));
		columnRegular.add(getJMenuItemVolcano(false));
		columnRegular.add(getJMenuItemViolin(false));
		columnRegular.add(getJMenuItemDot(false));
		columnRegular.add(getJMenuItemBar(false));
		columnRegular.add(getJMenuItemFilledArea(false));
		columnRegular.add(getJMenuItemGraphEditor(false));
		columnRegular.add(getJMenuItemHeatMap(false));
//		
		
		popup.add(columnRegular);
		
		
		popup.pack();
		popup.show(invoker, 0, invoker.getHeight());
	}

//	
	private JMenuItem getJMenuItemScatter(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Scatter Plot");
		mi.addActionListener(e -> createNewAttribute("Scatter Plot", isShared));

		return mi;
	}

	private JMenuItem getJMenuItemHistogram(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Histogram Plot");
		mi.addActionListener(e -> createNewAttribute("Histogram Plot", isShared));

		return mi;
	}

	private JMenuItem getJMenuItemVolcano(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Volcano Plot");
		mi.addActionListener(e -> createNewAttribute("Volcano Plot", isShared));
		
		return mi;
	}
	private JMenuItem getJMenuItemHeatMap(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Heat Map");
		mi.addActionListener(e -> createNewAttribute("Heat Map", isShared));

		return mi;
	}

	private JMenuItem getJMenuItemBar(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Bar Chart");
		mi.addActionListener(e -> createNewAttribute("Bar Chart", isShared));

		return mi;
	}

	private JMenuItem getJMenuItemViolin(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Violin Plot");
		mi.addActionListener(e -> createNewAttribute("Violin Plot", isShared));
		
		return mi;
	}
	private JMenuItem getJMenuItemDot(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Dot Plot");
		mi.addActionListener(e -> createNewAttribute("Dot Plot", isShared));
		
		return mi;
	}
	private JMenuItem getJMenuItemFilledArea(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Filled Area Plot");
		mi.addActionListener(e -> createNewAttribute("Filled Area Plot", isShared));
		
		return mi;
	}
	private JMenuItem getJMenuItemGraphEditor(boolean isShared) {
		var mi = new JMenuItem();
		mi.setText("Graph Editor");
		mi.addActionListener(e -> createNewAttribute("Graph Editor", isShared));
		
		return mi;
	}

//
//	
	private void createNewAttribute(String type, boolean isShared) {
		try {
//			
//			String newAttribName = null;
		

	    // Get the currently selected network
			
		    CyNetwork currentNetwork = applicationManager.getCurrentNetwork();
	
		        // Create a new task to plot the graph using the TaskFactory
		    TaskIterator taskIterator;
			    if(type.equals("Bar Chart")) {
			    		BarChartTaskFactory btf=new BarChartTaskFactory(serviceRegistrar);
			    		taskIterator = btf.createTaskIterator();
			    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
			    		taskManager.execute(taskIterator);
			        
			    }else if(type.equals("Scatter Plot")) {
			    	ScatterPlotTaskFactory sptf=new ScatterPlotTaskFactory(serviceRegistrar);
		    		taskIterator = sptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Histogram Plot")) {
			    	HistogramPlotTaskFactory hptf=new HistogramPlotTaskFactory(serviceRegistrar);
		    		taskIterator = hptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Volcano Plot")) {
			    	VolcanoPlotTaskFactory vptf=new VolcanoPlotTaskFactory(serviceRegistrar);
		    		taskIterator = vptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Graph Editor")) {
			    	GraphEditorTaskFactory gtf=new GraphEditorTaskFactory(serviceRegistrar);
		    		taskIterator = gtf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Violin Plot")) {
			    	ViolinPlotTaskFactory vlnptf=new ViolinPlotTaskFactory(serviceRegistrar);
		    		taskIterator = vlnptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Heat Map")) {
			    	HeatMapTaskFactory hmtf=new HeatMapTaskFactory(serviceRegistrar);
		    		taskIterator = hmtf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Line Graph")) {
			    	LineGraphTaskFactory lgtf=new LineGraphTaskFactory(serviceRegistrar);
		    		taskIterator = lgtf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Dot Plot")) {
			    	DotPlotTaskFactory dptf=new DotPlotTaskFactory(serviceRegistrar);
		    		taskIterator = dptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }else if(type.equals("Filled Area Plot")) {
			    	FilledAreaTaskFactory faptf=new FilledAreaTaskFactory(serviceRegistrar);
		    		taskIterator = faptf.createTaskIterator();
		    		TaskManager taskManager = serviceRegistrar.getService(TaskManager.class);
		    		taskManager.execute(taskIterator);
			    	
			    }
			
			else {
				throw new IllegalArgumentException("unknown column type \"" + type + "\".");
			}
		}catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
//	
//			CyTable attrs = null;
//			
//			
		
//				if (type.equals("Scatter"))
//					
//				else if (type.equals("Histogram"))
//					attrs.createColumn(newAttribName, Double.class, false);
//				else if (type.equals("Volcano"))
//					attrs.createColumn(newAttribName, Integer.class, false);
//				else if (type.equals("Long Integer"))
//					attrs.createColumn(newAttribName, Long.class, false);
//				else if (type.equals("Boolean"))
//					attrs.createColumn(newAttribName, Boolean.class, false);
//				else if (type.equals("String List"))
//					attrs.createListColumn(newAttribName, String.class, false);
//				else if (type.equals("Floating Point List"))
//					attrs.createListColumn(newAttribName, Double.class, false);
//				else if (type.equals("Integer List"))
//					attrs.createListColumn(newAttribName, Integer.class, false);
//				else if (type.equals("Long Integer List"))
//					attrs.createListColumn(newAttribName, Long.class, false);
//				else if (type.equals("Boolean List"))
//					attrs.createListColumn(newAttribName, Boolean.class, false);
//				else
//					throw new IllegalArgumentException("unknown column type \"" + type + "\".");
//			}
//		} catch (IllegalArgumentException e) {
//			JOptionPane.showMessageDialog(null, "good", JOptionPane.ERROR_MESSAGE);
				 

		
//	}
//		}
	}
}	
