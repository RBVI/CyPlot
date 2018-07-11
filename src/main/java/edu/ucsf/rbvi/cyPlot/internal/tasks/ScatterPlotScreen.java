package edu.ucsf.rbvi.cyPlot.internal.tasks;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class ScatterPlotScreen extends JPanel implements ActionListener {

	public ScatterPlotScreen() {
		setFocusable(true);
	}
	
	public Dimension getPreferredSize() {
        Dimension windowSize = new Dimension(800,600);
        return windowSize;
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	

}
