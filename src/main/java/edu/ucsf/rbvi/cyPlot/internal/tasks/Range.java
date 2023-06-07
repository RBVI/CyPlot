package edu.ucsf.rbvi.cyPlot.internal.tasks;

import edu.ucsf.rbvi.cyPlot.internal.tasks.Range;

public class Range
{
	public static Range EMPTY = new Range(0,0);
	double min;
	double max;
	public double min() { return min;	}
	public double max() { return max;	}
	public double width() { return max-min;	}
	public void union(Range other) 
	{ 
		if (min > other.min) min = other.min;
		if (max < other.max) max = other.max;
	}
	
	public void intersect(Range other) 
	{ 
		if (min < other.min) min = other.min;
		if (max > other.max) max = other.max;
	}
	
	public double product() { return max / min;	}
	public double normalize(double x)	{ return (x - min) / width();	}
	public double normalize(double x, boolean isLog)	
	{ 
		if (isLog) return (x / min) / product(); 
		return normalize(x);	
	}
	public void set(double mini, double maxi)	{ 	min = mini; max = maxi;	 }
	
	public Range()							{		this(0,1);	}
	public Range(float mini, float maxi)	{		min = mini; max = maxi;	}
	public Range(double mini, double maxi)	{		min = mini; max = maxi;	}
	public Range(int mini, int maxi)		{		min = mini; max = maxi;	}
	public String toString()				{ 		return "[" + min + " - " + max + "]"; }
	public boolean contains(double d)		{ 		return min <= d && max > d; }
	
}
