package edu.ucsf.rbvi.cyPlot.internal.utils;

import java.util.List;

import org.cytoscape.model.CyColumn;


/**
 *  The {@code LinearRegression} class performs a simple linear regression
 *  on an set of <em>n</em> data points (<em>y<sub>i</sub></em>, <em>x<sub>i</sub></em>).
 *  That is, it fits a straight line <em>y</em> = &alpha; + &beta; <em>x</em>,
 *  (where <em>y</em> is the response variable, <em>x</em> is the predictor variable,
 *  &alpha; is the <em>y-intercept</em>, and &beta; is the <em>slope</em>)
 *  that minimizes the sum of squared residuals of the linear regression model.
 *  It also computes associated statistics, including the coefficient of
 *  determination <em>R</em><sup>2</sup> and the standard deviation of the
 *  estimates for the slope and <em>y</em>-intercept.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class LinearRegression {
    private final double intercept, slope;
    private final double r2;
    private final double svar0, svar1;

   /**
     * Performs a linear regression on the data points {@code (y[i], x[i])}.
     *
     * @param  x the values of the predictor variable
     * @param  y the corresponding values of the response variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public LinearRegression(CyColumn xColumn, CyColumn yColumn,boolean yValLog,boolean xValLog) {
        // Get the x and y values from the CyColumns objects
        List<Object> xValues = xColumn.getValues(xColumn.getType());
        List<Object> yValues = yColumn.getValues(yColumn.getType());

        // Convert List<Object> to arrays of doubles
        double[] y;
        double[] x;
        if(xValLog) {
        	y = convertToDoubleLogArray(yValues);
        }else {
        	y = convertToDoubleArray(yValues);
        }
        
        
if(yValLog) {
	x= convertToDoubleLogArray(yValues);
        }else {
        	x = convertToDoubleArray(xValues);
        }
        
        

        if (x.length != y.length) {
            throw new IllegalArgumentException("Array lengths are not equal");
        }

        int n = x.length;

        // Convert List<Object> to arrays of doubles
//        for (int i = 0; i < n; i++) {
//        	if(isNumeric(xValues.get(i)) && isNumeric(yValues.get(i)))
//        	{    x[i] = convertToDouble(xValues.get(i));
//	            y[i] = convertToDouble(yValues.get(i));
//        	}
//        }

       
        if (x.length != y.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }
        

        // first pass
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < n; i++) {
            sumx  += x[i];
            sumx2 += x[i]*x[i];
            sumy  += y[i];
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        slope  = xybar / xxbar;
        intercept = ybar - slope * xbar;

        // more statistical analysis
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = slope*x[i] + intercept;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }

        int degreesOfFreedom = n-2;
        r2    = ssr / yybar;
        double svar  = rss / degreesOfFreedom;
        svar1 = svar / xxbar;
        svar0 = svar/n + xbar*xbar*svar1;
    }

   private double[] convertToDoubleLogArray(List<Object> yvalues) {
	   double[] result = new double[yvalues.size()];
       for (int i = 0; i < yvalues.size(); i++) {
       	if (yvalues.get(i) instanceof Number) {
           result[i] = Math.log(convertToDouble(yvalues.get(i)));
       	}else {
       		result[i]=result[i-1];
       		continue;
       	}
       }
       return result;
}

/**
     * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
     */
    public double intercept() {
        return intercept;
    }

   /**
     * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
     */
    public double slope() {
        return slope;
    }
    public static boolean isNumeric(Object obj) {
        return obj instanceof Number;
    }
    
    private double convertToDouble(Object obj) {
        
            return ((Number) obj).doubleValue();

    }
   
    private double[] convertToDoubleArray(List<Object> values) {
        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
        	if (values.get(i) instanceof Number) {
            result[i] = convertToDouble(values.get(i));
        	}else {
        		result[i]=result[i-1];
        		continue;
        	}
        }
        return result;
    }

}