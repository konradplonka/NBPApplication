import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import java.util.Map;

public class PlotDrawer extends JFrame {

    public PlotDrawer(String windowTitle, String plotTitle, String xLabel, String yLabel, Map<String, Double> data, boolean averageValue){
        super(windowTitle);

        // Create dataset
        XYDataset dataset = createDataset(data, averageValue);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(plotTitle, xLabel, yLabel, dataset);

        // Customize plot appereance
        customizePlotApperance(chart, Color.WHITE, new Color(192,192,192));

        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private XYDataset createDataset(Map<String, Double> data, boolean averageValue) {
        /*
         * This method creates dataset
         * @param data is a map which contains data and price value
         */

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        TimeSeries series1 = new TimeSeries("Price");

        // Add data to dataset
        for(Map.Entry<String, Double> entry: data.entrySet()){
            String [] splitDate = entry.getKey().split("-");

            int year = Integer.parseInt(splitDate[0]);
            int month = Integer.parseInt(splitDate[1]);
            int day = Integer.parseInt(splitDate[2]);

            series1.add(new Day(day, month, year), entry.getValue());
        }
        if(averageValue){
            TimeSeries series2 = new TimeSeries("Average value");
            double average = 0;
            double sum = 0;

            for(Map.Entry<String, Double> entry: data.entrySet()){
                sum += entry.getValue();
            }
            average = sum/data.size();

            for(Map.Entry<String, Double> entry: data.entrySet()) {
                String[] splitDate = entry.getKey().split("-");

                int year = Integer.parseInt(splitDate[0]);
                int month = Integer.parseInt(splitDate[1]);
                int day = Integer.parseInt(splitDate[2]);
                series2.add(new Day(day, month, year), average);
            }
            dataset.addSeries(series2);
        }

        dataset.addSeries(series1);


        return dataset;
    }

    private void customizePlotApperance(JFreeChart chart, Color backgroundColor, Color gridColor){
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(backgroundColor);
        plot.setDomainGridlinePaint(gridColor);
        plot.setRangeGridlinePaint(gridColor);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        Ellipse2D ellipse2D = new Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);


        renderer.setSeriesPaint(0,new Color(79,195,247));
        renderer.setSeriesPaint(1,new Color(255,81,49));
        renderer.setSeriesShape(0,ellipse2D);
        renderer.setSeriesShape(1,ellipse2D);
        renderer.setDefaultShapesVisible(true);

        String dateformat = "dd-MM-YYYY";
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setVerticalTickLabels(true);
        axis.setDateFormatOverride(new SimpleDateFormat(dateformat));


    }
}
