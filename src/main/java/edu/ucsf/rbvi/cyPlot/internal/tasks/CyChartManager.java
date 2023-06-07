package edu.ucsf.rbvi.cyPlot.internal.tasks;

import java.awt.Frame;
import java.util.Collection;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;

public class CyChartManager {

	private CyServiceRegistrar registrar;
//	private Map<String, CyChart> idMap = new HashMap<String, CyChart>();
	private String version = "unknown";
//	private static int chartCount = 1;

	public CyChartManager(CyServiceRegistrar reg) {
		registrar = reg;
//		CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
	}

	public CyServiceRegistrar getRegistrar() {		return registrar;	}

	public CyNetwork getCurrentNetwork() 		
	{	
		if (registrar == null) return null;
		CyApplicationManager appMgr = registrar.getService(CyApplicationManager.class);
		if (appMgr == null) return null;
		return appMgr.getCurrentNetwork();	
	}
	
	public void setVersion(String v) { this.version = v; }
	public String getVersion() { return version; }

	public Frame getOwner() {
		final CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
		return swingApplication == null ? null : swingApplication.getJFrame();
	}
	CyColumn xColumn;
	CyColumn yColumn;
	boolean isDirected;

	public void setXColumn(CyColumn c) 	{	xColumn = c;	}
	public void setYColumn(CyColumn c) 	{	yColumn = c;	}
	public CyColumn getXColumn() 		
	{
		if (xColumn == null)
			{} //setXColumnName();
		 return xColumn;
	}
	public CyColumn getYColumn()	{
//		if (yColumn != null) 
			return yColumn;	
	}

	public void setXColumnName(String x) {
//		CyNetwork net = getCurrentNetwork();
//		if (net == null) return;
		CyTable tab = getCurrentTable();
//		boolean edgeAttribute = x.startsWith("Edge") && !x.startsWith("EdgeCount");
//		CyTable tab = edgeAttribute ? net.getDefaultEdgeTable() : net.getDefaultNodeTable();
		if (tab == null) return;
		xColumn = tab.getColumn(x);
		if (xColumn == null && "Degree".equals(x))
		{
			x = "EdgeCount";
			xColumn = tab.getColumn(x);
		}
	}
	

	public void setYColumnName(String y) {
//		CyNetwork net = getCurrentNetwork();
//		if (net == null) return;
		if (isDirected && "Degree".equals(y))
			y = "EdgeCount";
		CyTable tab = getCurrentTable();
		if (tab == null) return;

		if (y == null)
		{
			Collection<CyColumn> cols = tab.getColumns();
			for (CyColumn col : cols)
				if (col.getType() == Double.class)
				{
					y = col.getName();
					yColumn = col;
				}
		}
		else 		yColumn = tab.getColumn(y);
//		boolean edgeAttribute = y.startsWith("Edge") && !y.startsWith("EdgeCount");
//		CyTable tab = edgeAttribute ? net.getDefaultEdgeTable() : net.getDefaultNodeTable();

		
		if ("Degree".equals(y))
		{
			y = "EdgeCount";
			yColumn = tab.getColumn(y);
		}
	}

	public CyTable getCurrentTable() {
		CyApplicationManager appMgr = registrar.getService(CyApplicationManager.class);
		return (appMgr == null) ? null : appMgr.getCurrentTable();
	}	
	


}
