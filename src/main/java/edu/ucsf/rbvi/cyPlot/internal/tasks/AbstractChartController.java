package edu.ucsf.rbvi.cyPlot.internal.tasks;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import edu.ucsf.rbvi.cyPlot.internal.tasks.FilterBuilder;
import edu.ucsf.rbvi.cyPlot.internal.tasks.CyChartManager;
import edu.ucsf.rbvi.cyPlot.internal.tasks.Range;
import edu.ucsf.rbvi.cyPlot.internal.tasks.Borders;
import edu.ucsf.rbvi.cyPlot.internal.tasks.NumberField;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

/*
 * This is the parent class of the histogram and scatter controllers.
 * It builds the panel, with header / chart / footer
 */
abstract public class AbstractChartController implements SetCurrentNetworkListener {		// implements Initializable

	protected CyApplicationManager applicationManager = null;
	protected CyServiceRegistrar registrar;
	protected CyNetwork myNetwork = null;		// remember the network that created us, and only enable when it is active
	protected CyChartManager manager;

	protected CyTable table;
	protected NumberField xMin, xMax;
	protected NumberField yMin, yMax;
	protected Label statusLabel = new Label();
	protected CyColumn xColumn, yColumn;

	protected AnchorPane chartBox;
	protected StackPane chartContainer;
	protected ChoiceBox<String> xAxisChoices;
	protected CheckBox logXTransform;
	protected ChoiceBox<String> yAxisChoices;
	protected CheckBox logYTransform;
	protected CheckBox interactive;
	public boolean isInteractive() {			return interactive.isSelected();	}

	protected ValueAxis<Number> xAxis;
	protected ValueAxis<Number> yAxis;
	protected XYChart<Number,Number> theChart;
	public void setChart(XYChart<Number,Number> c) { theChart = c; }
	protected XYChart<Number,Number> getChart() 	{ return theChart; }
	public Node getPlotAreaNode() 		{		return theChart == null ? null : theChart.lookup(".chart-plot-background");	}
	public Bounds getPlotBounds()		{		return theChart == null ? null : getPlotAreaNode().getBoundsInParent();		}
	public int getDataSize()
	{
		if (theChart == null)	return 0;

		ObservableList<Series<Number,Number>> data = theChart.getData();
		if (data.size() == 0)  return 0;
		 Series<Number,Number> series = data.get(0);
		if (series == null) return 0;
		return series.getData().size();
	}

//	public Rectangle getPlotBoundsRect()
//	{
//		Bounds b= getPlotBounds();
//		return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
//
//	}
	protected HBox header1, footer1, footer2, footer3;

	static Font numberFont = new Font("SansSerif", 10);
	abstract public void setParameters();
	//-------------------------------------------------------------
	// use this if you don't use FXML to define the chart
	/*
	 * Constructor
	 *
	 * @param StackPane parent The JavaFX Node that contains us
	 * @param CyServiceRegistrar reg Access to Cytoscape's registered services
	 * @param boolean is2D	Both histogram and scatter charts come thru here
	 * @param CyChartManager mgr  The app's central intelligence
	 */

	public AbstractChartController(StackPane parent, CyServiceRegistrar reg, boolean is2D, CyChartManager mgr) {
//		System.out.println("AbstractChartController");
		chartContainer = parent;
		manager = mgr;
//		chartContainer.setBorder(Borders.etchedBorder);
		if (reg == null)
		{
			applicationManager = null;
			table = null;
		}
		else
		{	registrar = reg;
			applicationManager = registrar.getService(CyApplicationManager.class);
			table = manager.getCurrentTable();
//			table = manager.getCurrentNetwork().getDefaultNodeTable();
			myNetwork = applicationManager.getCurrentNetwork();
		}

		HBox top = makeHeader(is2D);
		chartBox = new AnchorPane();			//  the placeholder to contain the chart
		anchor(chartBox);
		VBox bottom = makeFooter(is2D);

		BorderPane page = new BorderPane();
		page.setTop(top);
		page.setCenter(chartBox);
		page.setBottom(bottom);
		bottom.setPadding(new Insets(2,10,2,10));

		parent.getChildren().add(page);
		anchor(parent);
		xColumn = manager.getXColumn();
		yColumn = manager.getYColumn();
		initialize();
		parentStackPane = parent;
		parentStackPane.getChildren().add(tip);
		tip.setVisible(false);
//
	}
	Label tip = new Label("");
	StackPane parentStackPane;
	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		CyNetwork currentNet = e.getSource().getCurrentNetwork();
		boolean active;
		if (currentNet == null) {
			active = false;
		} else {
				active = currentNet == myNetwork;
		}
		parentStackPane.setDisable(!active);
		parentStackPane.setOpacity(active ? 1 : 0.5);
		Platform.runLater(() ->
		{
			String s = "This window is disabled \nbecause no network is active.";
			if (currentNet != null)
			{
				CyTable networkTable = myNetwork.getDefaultNetworkTable();
				String title =  (networkTable == null) ? "this" : currentNet.getDefaultNetworkTable().getTitle();
				s = "This window is disabled because " + title + "\nis not the active network.";
			}
			tip.setText(s);
			tip.setVisible(!active);
		});
	}

	//-------------------------------------------------------------
	public String getTitle()
	{
		String title = "Untitled";
		CyTable table = manager.getCurrentTable();
		if (table != null)
			title = table.getTitle();
		else if (myNetwork != null)
		{
			CyTable networkTable = myNetwork.getDefaultNetworkTable();
			if (networkTable != null)
				title =  myNetwork.getDefaultNetworkTable().getTitle();
		}
		return title;
	}
	//-------------------------------------------------------------
//  watch out: fxml support is in a bundle not included in cytoscape
//	@Override public void initialize(URL uri, ResourceBundle rb)
//	{
//		initialize();
//	}

	public void initialize()
	{
	    assert( chartContainer != null);
		ChangeListener<Number> xListener = new ChangeListener<Number>() 		{
			@Override public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV)
			{   setXParameters(newV);   }
		};
		ChangeListener<Number> yListener = new ChangeListener<Number>() 		{
			@Override public void changed(ObservableValue<? extends Number> obs, Number oldV, Number newV)
			{   setYParameters(newV);   }
		};
		populateColumnChoices();
		int xIndex = 0;
		SingleSelectionModel<String> xSelector = xAxisChoices.getSelectionModel();
		SingleSelectionModel<String> ySelector = yAxisChoices.getSelectionModel();
		if (xColumn != null)
			xSelector.select(xColumn.getName());
		else 	xSelector.select(xIndex);

		if (yColumn == null)
		{
			ySelector.select(1);
			manager.setYColumnName(ySelector.getSelectedItem());
		}
		else	ySelector.select(yColumn.getName());


		setParameters();
		xSelector.selectedIndexProperty().addListener(xListener);
		ySelector.selectedIndexProperty().addListener(yListener);

		registrar.registerService(this, SetCurrentNetworkListener.class, new Properties());
	}
	/*
	 * Unregister the service when we are inactive
	 */

	public void unregister()
	{
//		System.out.println("unregister");
		registrar.unregisterService(this, SetCurrentNetworkListener.class);
	}
	// ------------
	/*
	 * Shared code to make the container of buttons and checkboxes
	 */
	private HBox makeHeader(boolean is2D) {
		makeFilter = new Button("Create Filter");
		makeFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {	makeFilter();	}
		});
		copyImage = new Button("Export Image...");
		copyImage.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent event) {	snapshot();	}
		});
		interactive = new CheckBox("Interactive");
		interactive.setAlignment(Pos.CENTER);
		interactive.setTranslateY(4);

		curveFit = new CheckBox("Regression");
		ChangeListener<? super Boolean> regressCheckChange = new ChangeListener<Boolean>() {
		    @Override public void changed(ObservableValue<? extends Boolean> ov,
		        Boolean old_val, Boolean val) { regression(val);  }};
		curveFit.selectedProperty().addListener(regressCheckChange);
		curveFit.setTranslateY(4);

		header1 = new HBox(8, makeFilter, copyImage, interactive, curveFit);
		header1.setMinHeight(30);
		header1.setMaxHeight(30);
		header1.setBorder(Borders.emptyBorder);
		AnchorPane.setLeftAnchor(header1, 12.0);
		return header1;
	}
	// ------------
	/*
	 * Shared code to make the container of axis ChoiceBox popup with log and min-max fields
	 */
	private VBox makeFooter(boolean is2D) {
		// -make the x axis choice box and log check box
		xAxisChoices = new ChoiceBox<String>();
		ChangeListener<Boolean> logXChange = new ChangeListener<Boolean>() {
		    @Override public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
		        setLogXDistribution(new_val);
//		        System.out.println("log changed");}
		    }  };

		logXTransform = new CheckBox("Log");
		logXTransform.selectedProperty().addListener(logXChange);
		logXTransform.setAlignment(Pos.CENTER);
		logXTransform.setDisable(false);
		logXTransform.setVisible(true);
		xMin = makeNumberField("xMin");
		xMax = makeNumberField("xMax");

		// ------------   we always create the Y axis line, but only include it if (is2D)
		yAxisChoices = new ChoiceBox<String>();
		ChangeListener<Boolean> logYChange = new ChangeListener<Boolean>() {
			    @Override
			    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
			        setLogYDistribution(new_val);
			    }
			};

		logYTransform = new CheckBox("Log");
		logYTransform.selectedProperty().addListener(logYChange);
		logYTransform.setAlignment(Pos.CENTER);
		logYTransform.setDisable(false);
		logYTransform.setVisible(true);
		yMin = makeNumberField("yMin");
		yMax = makeNumberField("yMax");

		// -------------- layout
		footer1 = makeNumberRangeLine(xAxisChoices, logXTransform, xMin, xMax);
		footer2 = makeNumberRangeLine(yAxisChoices, logYTransform, yMin, yMax);
		footer3 = new HBox(statusLabel);
		VBox bottom = new VBox();
		List<Node> children = bottom.getChildren();
		children.add(footer1);
		if (is2D) 		children.add(footer2);
		children.add(footer3);
		return bottom;
	}
	// ------------
	private HBox makeNumberRangeLine(ChoiceBox<?> choices, CheckBox log, NumberField minFld, NumberField maxFld)		// 6 controls
	{
		Label min = new Label("Min:");
		Label max = new Label("Max:");
		min.setFont(numberFont);	min.setAlignment(Pos.BASELINE_RIGHT);		min.setTranslateY(4);
		max.setFont(numberFont);	max.setAlignment(Pos.BASELINE_RIGHT);		max.setTranslateY(4);
		return new HBox(8, choices, log, min, minFld, max, maxFld);
	}
	// ------------
	private NumberField makeNumberField(String id) {
		NumberField fld = new NumberField(this);
		fld.setId(id);
		fld.setAlignment(Pos.BASELINE_RIGHT);
		fld.setFont(numberFont);
		fld.setMaxWidth(55);
//		fld.textProperty().addListener( (obs, oldVal, newVal) -> fieldChanged(newVal, fld.getId()));
		fld.focusedProperty().addListener((obs, old, nVal) -> { if (!nVal.booleanValue()) fieldChanged(fld.getText(), fld.getId());  });
		return fld;
	}
	// ------------  respond to user edits of range values.
	public void fieldChanged(String newValue, String fieldId) {

		BigDecimal newXmin = xMin == null ? new BigDecimal(0) : xMin.getNumber();
		BigDecimal newXmax = xMax == null ? new BigDecimal(0) : xMax.getNumber();
		BigDecimal newYmin = yMin == null ? new BigDecimal(0) : yMin.getNumber();
		BigDecimal newYmax = yMax == null ? new BigDecimal(0) : yMax.getNumber();

		double val = 0;
		try {
			val = Double.parseDouble(newValue);
		}
		catch (NumberFormatException e)
		{
			return;
		}
		if ("xMin".equals(fieldId))	setXRange(new Range(val, newXmax.doubleValue()));
		if ("xMax".equals(fieldId))	setXRange(new Range(newXmin.doubleValue(), val));
		if ("yMin".equals(fieldId))	setYRange(new Range(val, newYmax.doubleValue()));
		if ("yMax".equals(fieldId))	setYRange(new Range(newYmin.doubleValue(), val));


//		resizeRangeFields();
//		System.out.println(fieldId + " = " + val);
}
	public abstract void resizeRangeFields();
	abstract public void resized();
	//-------------------------------------------------------------
	protected Button makeFilter;
	protected Button copyImage;
	protected CheckBox curveFit;

	protected void makeFilter() {
		if (registrar == null) {		System.err.println("No registrar found");  return; 	}
		String x = xAxisChoices.getSelectionModel().getSelectedItem();
		String y = yAxisChoices.getSelectionModel().getSelectedItem();
	    FilterBuilder builder = new FilterBuilder(x, new Range(startX, endX), y, new Range(startY, endY));
	    builder.makeCompositeFilter(registrar);
	    selectFilterPanel();
	 }

	protected void clearRegression()
	{
		curveFit.setSelected(false);
	}
	abstract protected void regression(boolean vis);
	//-------------------------------------------------------------
	/*
	 * After creating a filter, bring that panel up in the tabs
	 */
	protected void selectFilterPanel() {

	CySwingApplication desktopApp = registrar.getService(CySwingApplication.class);
		if (desktopApp == null) return;
		CytoPanel cytoPanelWest = desktopApp.getCytoPanel(CytoPanelName.WEST);
		if (cytoPanelWest == null) return;
		int index = cytoPanelWest.indexOfComponent("org.cytoscape.Filter");
		if (index >= 0)
			cytoPanelWest.setSelectedIndex(index);
	}

	//-------------------------------------------------------------
	/*
	 * Take a snapshot of the window and save it as a PNG
	 */
	private void snapshot() {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

	    //Prompt user to select a file
	    fileChooser.setInitialFileName(getTitle());
	    File file = fileChooser.showSaveDialog(null);		//manager.getOwner()
	    if(file != null){
	        try {
	    		header1.setVisible(false);
	    		footer1.setVisible(false);
	    		footer2.setVisible(false);
	    		footer3.setVisible(true);
	            //Pad the capture area
	    		int width = (int)chartContainer.getWidth() + 20;
	    		int height = (int)chartContainer.getHeight() + 20;
	            WritableImage writableImage = new WritableImage(width, height);
	            chartContainer.snapshot(null, writableImage);
	            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
	            ImageIO.write(renderedImage, "png", file);		   //Write the snapshot to the chosen file
	        } catch (IOException ex) { ex.printStackTrace(); }
	    }
		header1.setVisible(true);
		footer1.setVisible(true);
		footer2.setVisible(true);
		footer3.setVisible(true);
	}

	//-------------------------------------------------------------
	 protected boolean isXLog = false;
	 protected boolean isYLog = false;

	 public boolean isXLog()	{ return isXLog;	}
	 public boolean isYLog()	{ return isYLog;	}

		/*
		 * Go thru the table columns and collect the numerics
		 */

	protected void populateColumnChoices() {
		if (table != null && !table.getColumns().isEmpty()) {
			for (CyColumn col : table.getColumns()) {
				if (!isNumericColumn(col))
					continue;
				xAxisChoices.getItems().add(col.getName());
				yAxisChoices.getItems().add(col.getName());
			}
			xAxisChoices.getSelectionModel().select(0);
		}
	}
	/*
	 * Search the active table for a column by name
	 *
	 * @param name The column name
	 */

	protected CyColumn findColumn(String name) {
		if (name == null) return null;
		if (table == null) return null;
		for (CyColumn column : table.getColumns())
			if (name.equals(column.getName()))
				return column;
		return null;
	}
	// -------------------------------------------------------------
	public void setXParameters(Number val) {
		xAxisChoices.getSelectionModel().select(val.intValue());
		setParameters();
	}

	protected void setLogXDistribution(Boolean new_val) {
		isXLog = new_val;
		setXParameters(xAxisChoices.getSelectionModel().getSelectedIndex());
	}
	// -------------------------------------------------------------
	public void setYParameters(Number val)
	{
		yAxisChoices.getSelectionModel().select(val.intValue());
		setParameters();
	}

	protected void setLogYDistribution(Boolean new_val) {
		isYLog = new_val;
		setYParameters(yAxisChoices.getSelectionModel().getSelectedIndex());
	}
	// -------------------------------------------------------------
	public void setStatus(String s)	{
		if ( statusLabel != null)  statusLabel.setText(s);
	}

	public void setStatus(String s, Range xRange, Range yRange)	{
		if ( statusLabel != null)  statusLabel.setText(s);
		setXRange(xRange);
		setYRange(yRange);
	}

	public void setXmin(double d)	{ if (!Double.isNaN(d))	xMin.setNumber(d);	}
	public void setXmax(double d)	{ xMax.setNumber(d);	}
	public void setYmin(double d)	{ yMin.setNumber(d);	}
	public void setYmax(double d)	{ yMax.setNumber(d);	}

	public void setXRange(Range r) {
		if (r == null) return;
		setXmin(startX = r.min());
		setXmax(endX = r.max());
	}
	public void setYRange(Range r) {
		if (r == null) return;
		setYmin(startY = r.min());
		setYmax(endY = r.max());
	}

	// ------------------------------------------------------
	public int getDataSize(XYChart<Number, Number> chart)
	{
		List<XYChart.Series<Number, Number>> dataList = chart.getData();
		if (dataList == null || dataList.isEmpty()) return 0;
		XYChart.Series<Number, Number> data = dataList.get(0);
		return data.getData().size();
	}

	// ------------------------------------------------------
	protected boolean isNumericColumn(CyColumn col) {
		return col.getType() == Double.class || col.getType() == Integer.class;
	}

	protected List<Double> getColumnValues(CyColumn col)
	{
		if (col.getType() == Double.class)
			return col.getValues(Double.class);
		if (col.getType() == Integer.class)
		{
			List<Integer> intvalues = col.getValues(Integer.class);
			List<Double> dubvalues = new ArrayList<Double>();
			for (Integer i : intvalues)
				if (i != null)
					dubvalues.add(new Double(i));
			return dubvalues;
		}
		return null;
	}

	// ------------------------------------------------------
/*
 * Return the log if it's defined, otherwise 0
 * This is a hack to handle values when plotting on a log axis
 *
 * @return the log(d) or 0
 */
	protected double safelog(double d) {			//  BAD STATS!
		if (d <= 0) 			return 0;
		if (Double.isNaN(d)) 	return 0;
		return Math.log(d);
	}
	// ------------------------------------------------------
	protected double startX, endX, startY, endY;			// these are values in the charts data space
	public double getSelectionStart()	{ 	return startX;	}
	public double getSelectionEnd()		{	return endX;	}
	public double getSelectionTop()		{ 	return startY;	}
	public double getSelectionBottom()	{ 	return endY;	}

	// ------------------------------------------------------
	public Range getXRange()
	{
		xAxis = (ValueAxis<Number>) theChart.getXAxis();  //return Range.EMPTY;
		return new Range(xAxis.getLowerBound(), xAxis.getUpperBound());
	}
	// ------------------------------------------------------
	public Range getYRange()
	{
		if (yAxis == null) yAxis = (ValueAxis<Number>) theChart.getYAxis();
		return new Range(yAxis.getLowerBound(), yAxis.getUpperBound());
	}
	// ------------------------------------------------------
	public void setRangeValues(Range r) {
		if (r == null) return;
		setRangeValues(r.min(), r.max());
	}
	public void setRangeValues(double selStart, double selEnd) {
		if (Double.isNaN(selStart) || Double.isNaN(selEnd)) return;
		startX = Math.max(xAxis.getLowerBound(), Math.min(selStart, selEnd));
		endX = Math.min(xAxis.getUpperBound(), Math.max(selStart, selEnd));
//		System.out.println(String.format("setRangeValues abs %.2f - %.2f", startX, endX));
	}

	public void setRangeValues(double selStart, double selEnd, double selStartY, double selEndY) {
		if (Double.isNaN(selStart) || Double.isNaN(selEnd)) return;
		startX = Math.max(xAxis.getLowerBound(), Math.min(selStart, selEnd));
		endX = Math.min(xAxis.getUpperBound(), Math.max(selStart, selEnd));

		startY = Math.max(yAxis.getLowerBound(), Math.min(selStartY, selEndY));
		endY = Math.min(yAxis.getUpperBound(), Math.max(selStartY, selEndY));
	}

	// ------------------------------------------------------
	public void anchor(Node n)					{	anchor(n,0);	}
	private void anchor(Node n, double margin)	{	anchor(n, margin, margin, margin, margin);	}
	private void anchor(Node n, double top, double left, double bottom, double right )
	{
		AnchorPane.setTopAnchor(n, top);
		AnchorPane.setLeftAnchor(n, left);
		AnchorPane.setBottomAnchor(n, bottom);
		AnchorPane.setRightAnchor(n, right);
	}
	// ------------------------------------------------------
	/*
	 * Rather than each field getting its own lambda expr
	 * @param fldId ID of the field
	 * @param newValue The value it was set to
	 */
	public void fieldEdited(String fldId, BigDecimal newValue) {

		double v = newValue.doubleValue();
		if ("xMin".contentEquals(fldId))	setXmin(v);
		if ("xMax".contentEquals(fldId))	setXmax(v);
		if ("yMin".contentEquals(fldId))	setYmin(v);
		if ("yMax".contentEquals(fldId))	setYmax(v);
	}
}
