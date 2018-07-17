package edu.ucsf.rbvi.cyPlot.internal.tasks;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class VolcanoPlotScreen extends JPanel implements ActionListener {

	public JButton color, thickness;
	
	public VolcanoPlotScreen() {
		setFocusable(true);
		
		color = new JButton("Choose color");
		color.setBounds(500,250,100,30);
		color.addActionListener(this);
		this.add(color);
		color.setVisible(true);
		
		thickness = new JButton("Set point thickness");
		thickness.setBounds(500,300,100,30);
		thickness.addActionListener(this);
		this.add(thickness);
		thickness.setVisible(true);
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