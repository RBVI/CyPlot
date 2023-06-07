package edu.ucsf.rbvi.cyPlot.internal.tasks;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChart;
import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;

public class ScatterFilterDialog extends JDialog implements CyChart, ChangeListener {

	private static final long serialVersionUID = 1L;
	private final CyChartManager manager;
	private ScatterFilterPanel currentPanel;
	private String initialTitle = null;

	public ScatterFilterDialog(CyChartManager mgr, String title) {
		super(mgr.getOwner());
		
		manager = mgr;
		if (title != null) {
			setTitle(title);
			initialTitle = title;
		} else  setTitle("CyChart");

		currentPanel = new ScatterFilterPanel(manager, this);
		getContentPane().add(currentPanel);
		setPreferredSize(new Dimension(520, 600));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();

	}

	public String getTitle(String id) 				{ 	return (id == null)?  initialTitle : null;	}
	public ScatterFilterPanel getPanel(String id) 	{ 	return (id == null)?  currentPanel : null; }

	@Override public void stateChanged(ChangeEvent e) {
		String ttl = currentPanel.getTitle();
		setTitle((ttl != null) ? ttl : "CyChart");
	}

}
