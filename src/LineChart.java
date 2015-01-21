
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart  { 
	
	final static XYSeries xySeries = new XYSeries("Iloœc pieniêdzy");
	
	public static boolean SCALABLE = false;
	private static JFreeChart lineChart; 
	private ChartPanel chartPanel;
	private static int g_i = 0;
	
	private static LogarithmicAxis newZeroBasedLogAxis() {
	        LogarithmicAxis axis = new LogarithmicAxis(null /* maybe title */) {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public Range getRange() {
	                Range sRange = super.getRange();
	                // ensure lowerBound < upperBound to prevent exception
	                return new Range(
	                        Math.max(0, sRange.getLowerBound()),
	                        Math.max(1e-8, sRange.getUpperBound()));
	            }
	        };
	        axis.setAllowNegativesFlag(true);
	        // ... init as you wish ...
	        return axis;
	    }
	
	public LineChart(){

        //dxySeries.add(0, 0);
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(xySeries);
        
        
        
    	lineChart = ChartFactory.createXYLineChart(
            "Stan konta",      					// chart title
            "",                     	// x axis label
            "",                      	// y axis label
            dataset,            		// data
            PlotOrientation.VERTICAL,
            false,                     	// include legend
            false,                     	// tooltips
            false                     	// urls
        );
    	
    	lineChart.setTitle(new org.jfree.chart.title.TextTitle("Aktualny stan konta - 0 z³",
			       new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
	));
    	// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
    	lineChart.setBackgroundPaint(Color.white);
        
        // get a reference to the plot for further customisation...
        XYPlot plot = lineChart.getXYPlot();
        /*plot.setBackgroundPaint(Color.lightGray);
        
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);*/
        //plot.setDomainAxis(newZeroBasedLogAxis());
        plot.setRangeAxis(newZeroBasedLogAxis());  
        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        

        
        
        ValueAxis domain = plot.getDomainAxis();
        domain.setRange(0, 50);
    	
        //NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();  
        //domainAxis.setTickUnit(new NumberTickUnit(1));
  
        
    	chartPanel = new ChartPanel(lineChart);
    	chartPanel.setMouseWheelEnabled(true);
    	
	}
	

	  
    public ChartPanel getChartPanel(){
    	return chartPanel;
    }
  
 
    public static void updateDataSet (int amount){
    	if (g_i > 50)
    		if (!LineChart.SCALABLE)
    			lineChart.getXYPlot().getDomainAxis().setRange(g_i-50, g_i); else
    				lineChart.getXYPlot().getDomainAxis().setAutoRange(true);
    	
    	lineChart.setTitle("Aktualny stan konta - "+Integer.toString(amount)+" z³");
    	xySeries.add(g_i++, amount);
    }
    
  

}