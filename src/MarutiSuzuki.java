
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.File;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class MarutiSuzuki 
{
    public static void main(String[] args) 
    {
        String filePath = "resources/maruti_sales.csv"; 

       
        List<Double> months = new ArrayList<>();
        List<Double> sales = new ArrayList<>();

        try 
        {
            
            Scanner sc = new Scanner(new File(filePath));
            sc.nextLine(); 

            
            while (sc.hasNextLine()) 
            {
                String line = sc.nextLine();
                String[] fields = line.split(",");
                months.add(Double.parseDouble(fields[0]));
                sales.add(Double.parseDouble(fields[1]));
            }

            sc.close();
        } 
        
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
           
        }

       //apache commons math only works on arrays
        
        double[] monthArray = months.stream().mapToDouble(Double::doubleValue).toArray();
        double[] salesArray = sales.stream().mapToDouble(Double::doubleValue).toArray();

        
        SimpleRegression regression = new SimpleRegression();
        
        for (int i = 0; i < monthArray.length; i++) 
        {
            regression.addData(monthArray[i], salesArray[i]);
        }

        
        double nextMonth = monthArray.length + 1;
        double forecast = regression.predict(nextMonth);

       
        System.out.println("Forecasted sales for month " + (int) nextMonth + "(July 2024): " + forecast);

        // Visualizing the data using Jfreechart
        
        Window.createChart(months, sales, regression);
    }
}


class Window 
{
    public static void createChart(List<Double> months, List<Double> sales, SimpleRegression regression) 
    {
        XYSeries series = new XYSeries("Historical Sales");
        for (int i = 0; i < months.size(); i++) 
        {
            series.add(months.get(i), sales.get(i));
        }

        XYSeries predictedSeries = new XYSeries("Predicted Sales");
        for (int i = 0; i <= months.size(); i++) 
        {
            predictedSeries.add((double) i + 1, regression.predict((double) i + 1));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(predictedSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Sales Forecast",
                "Month",
                "Sales",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
