package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.service.util.CyServiceRegistrar;


public class NodeSelectedListener implements SelectedNodesAndEdgesListener {
	final CyServiceRegistrar registrar;
	
	public NodeSelectedListener(final CyServiceRegistrar registrar) {
		this.registrar = registrar;
	}
	
	@Override
	public void handleEvent(SelectedNodesAndEdgesEvent event) {
		CyNetwork network = event.getNetwork();
		List<Plot> plots = ModelUtils.getPlots(network);
		if (plots == null || plots.isEmpty()) return;
		
		for (Plot plot: plots) {
			int[] selectedpoints = ModelUtils.getNodeIndices(plot, event.getSelectedNodes());		
			if (selectedpoints == null || selectedpoints.length == 0) return;
			StringBuilder selectedstr = new StringBuilder("[");
			for (int selected: selectedpoints) 
				selectedstr.append(selected + ",");
			if (selectedpoints.length != 0)
				selectedstr.deleteCharAt(selectedstr.length() - 1);
			selectedstr.append("]");
			plot.setSetting("layoutExtra", "selectedpoints: " + selectedstr.toString());
			// System.out.println(plot.getSetting("layoutExtra"));
			
			ModelUtils.listCyBrowsers(registrar, new CheckCyBrowser(registrar, plot));
			
		}
	}

	class CheckCyBrowser implements TaskObserver {
		final CyServiceRegistrar registrar;
		final Plot plot;

		CheckCyBrowser(final CyServiceRegistrar registrar, final Plot plot) {
			this.registrar = registrar;
			this.plot = plot;
		}

		@Override
		public void allFinished(FinishStatus status) {}

		@Override
		public void taskFinished(ObservableTask task) {
			JSONResult results = task.getResults(JSONResult.class);
			if (JSONUtils.haveTabID(results.getJSON(), plot.getSetting("tabID"))) {

				ModelUtils.openCyBrowser(registrar, plot.getHTML(), plot.getSetting("title"), 
				                         plot.getSetting("tabID"), false);

			}
		}
	}

}
