package edu.ucsf.rbvi.cyPlot.internal.tasks;

import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;
import org.cytoscape.service.util.CyServiceRegistrar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppScatters extends Application 
{
    public static void main(final String[] args) {    Application.launch(args);    }

    @Override public  void start(Stage stage) throws Exception 
    {
    	me = this;
//	    FXMLLoader fxmlLoader = new FXMLLoader();
//	    URL url = getClass().getResource("ScatterChart.fxml");
//	    fxmlLoader.setLocation(url);
//	    AnchorPane appPane = fxmlLoader.load();
    	StackPane pane = getStackPane(null, null);
    	Scene scene = new Scene(pane, 520, 550);
	    stage.setScene(scene);
	    stage.show();
   }

    public static StackPane getStackPane(CyServiceRegistrar registrar, CyChartManager mgr)
    {
	    StackPane pane = new StackPane();
	    pane.setPrefWidth(520);
	    pane.setPrefHeight(550);
	    new ScatterChartController(pane, registrar, mgr);
	    return pane;
    }  
    
    
 static public AppScatters getInstance()	{ return me;	}
 static private AppScatters me;
 static private Stage theStage;
 static public Stage getStage() { return theStage;  }
     
}