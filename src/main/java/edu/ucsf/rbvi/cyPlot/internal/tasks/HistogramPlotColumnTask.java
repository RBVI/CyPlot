package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.cyPlot.internal.utils.ModelUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSONUtils;
import edu.ucsf.rbvi.cyPlot.internal.utils.JSUtils;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

public class HistogramPlotColumnTask extends AbstractTask {
	
		
		final CyServiceRegistrar sr;

		
		
		public CyColumn xCol;

		@Tunable (description="Name selection column")
		public ListSingleSelection<String> nameCol;
		
		@Tunable (description="Open in plot editor?",context="nogui")
		public boolean editor;

		// Command interface for non-network plots
		@Tunable (description="JSON formatted string of point names", context="nogui")
		public String names = null;

		@Tunable (description="JSON formatted string of x values", context="nogui")
		public String xValues = null;

		@Tunable (description="JSON formatted string of y values", context="nogui")
		public String yValues = null;

		@Tunable (description="Selection string", context="nogui")
		public String selectionString = null;

		@Tunable (description="Plot title", context="nogui")
		public String title = null;

		@Tunable (description="X Axis Label", context="nogui")
		public String xLabel = null;

		@Tunable (description="Y Axis Label", context="nogui")
		public String yLabel = null;

		
		
		public CyApplicationManager appManager;
		public CyNetworkView netView;
		public CyNetwork network;
		public CyTable table;
		public CyColumn column;
			
		public HistogramPlotColumnTask(final CyServiceRegistrar sr,CyColumn dataCol) {
			super();
			this.sr = sr; 
			appManager = sr.getService(CyApplicationManager.class);
			netView = appManager.getCurrentNetworkView();
			
				
				
				
					network = netView.getModel();
					table = network.getDefaultNodeTable();
					column = dataCol;
					editor = false;
		
					
//		
//					List<String> names = ModelUtils.getColOptions(columns, "string");
					xCol=column;
					
				
					
		
				
		}

		/**
		 * Generate the variables necessary to create a bar chart in plotly with the cytoscape 
		 * task. Creates and executes a TaskIterator which opens the plot within a cybrowser window. 
		 *
		 * @param monitor the TaskMonitor required for this method by the parent 
		 * AbstractTask class
		 */
		public void run(TaskMonitor monitor) { 
			TaskManager sTM = sr.getService(TaskManager.class);
			CommandExecutorTaskFactory taskFactory = sr.getService(CommandExecutorTaskFactory.class);

			String xArray;
			
			
			String idColumn = null;

//			if (xCol != null) {
//				CyColumn yColumn = table.getColumn(ModelUtils.getTunableSelection(yCol));
				CyColumn xColumn = xCol;
//				CyColumn nameColumn = table.getColumn(ModelUtils.getTunableSelection(nameCol));
			
				xArray = ModelUtils.colToArray(xColumn);
			
//				yArray = ModelUtils.colToArray(yColumn);

//				nameArray = ModelUtils.colToArray(nameColumn);
					
				xLabel = xColumn.getName();
				
				yLabel ="Frequency";

				
				xArray = ModelUtils.colToArray(xColumn);
				
			

			
//		}
			String html = JSUtils.getHistogramColumnPlot(xArray,null,  selectionString, idColumn, 
                    title, xLabel, yLabel, editor);
			Map<String, Object> args = new HashMap<>();		
			args.put("text", html);
			args.put("title", "Histogram Plot");
			
			TaskIterator ti = taskFactory.createTaskIterator("cybrowser", "dialog", args, null);
			sTM.execute(ti);
		}
}
