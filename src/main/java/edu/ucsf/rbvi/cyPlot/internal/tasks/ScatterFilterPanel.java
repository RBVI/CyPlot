package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.CountDownLatch;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;
import org.cytoscape.service.util.CyServiceRegistrar;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class ScatterFilterPanel extends JPanel {
 
	private static final long serialVersionUID = 1L;

	protected JFXPanel jfxPanel;
 
	public static final String EVENT_TYPE_CLICK = "click";
	public static final String EVENT_TYPE_CONTEXT_MENU = "contextmenu";

	private String title = null;
	private CyServiceRegistrar registrar;
	private CyChartManager manager; 
	final Logger logger = Logger.getLogger(CyUserLog.NAME);
 
	public ScatterFilterPanel(CyChartManager mgr, ScatterFilterDialog parentDialog) {
		super(new BorderLayout());
//		System.out.println("ScatterFilterPanel");
		setPreferredSize(new Dimension(520, 500));
		manager = mgr;
		registrar = manager.getRegistrar();
		initComponents();
//		Platform.setImplicitExit(false);
	}

	public String getTitle() 	{		return title;	}

	public String execute(final String script) {
		final String[] returnVal = new String[1]; 
		final CountDownLatch doneLatch = new CountDownLatch(1);
		try {
			doneLatch.await();
		} catch (InterruptedException e) {
		}
		return returnVal[0];
	}

	private void initComponents() {
		jfxPanel = new JFXPanel();
		add(jfxPanel, BorderLayout.CENTER);
		StackPane appPane = AppScatters.getStackPane(registrar, manager);
		if (appPane != null) 
		
		{
			Scene scene = new Scene(appPane);
				Platform.runLater(() -> { jfxPanel.setScene(scene); });
			//https://stackoverflow.com/questions/52832861/jfxpanel-freezing-awt-thread-the-second-time
//			System.out.println("scene created");
		}
//		else System.out.println("appPane came back null");
	}

}