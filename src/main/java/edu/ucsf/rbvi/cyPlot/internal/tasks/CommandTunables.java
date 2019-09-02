package edu.ucsf.rbvi.cyPlot.internal.tasks;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class CommandTunables {
	
	@Tunable (description="Selection string", context="nogui")
	public String selectionString = null;

	@Tunable (description="Plot title", context="nogui")
	public String title = null;

	@Tunable (description="CyBrowser id", context="nogui")
	public String id = null;

	@Tunable (description="CyBrowser tab id", context="nogui")
	public String tabID = null;

	@Tunable (description="X Axis Label", context="nogui")
	public String xLabel = null;

	@Tunable (description="Y Axis Label", context="nogui")
	public String yLabel = null;

}
