package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
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
			plot.addExtraSetting("layoutExtra", "selectedpoints=" + selectedpoints // HOW TO INPUT INDICES ARRAY?
					+ ", selected=dict(marker=dict(color='red'))," 
					+ "unselected=dict(marker=dict(color='rgb(200,200, 200)', opacity=0.9))");
			
			ModelUtils.openCyBrowser(registrar, plot.getHTML(), plot.getSetting("title"), 
									 plot.getSetting("plotID"), plot.getSetting("tabID"));
		}
	}
}