package edu.ucsf.rbvi.cyPlot.internal.tasks;
import javax.swing.JPanel;

public interface CyChart {
	public String getTitle(String id);
	public JPanel getPanel(String id);
}
