package ac.uk.napier.onemax;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MyChartPanel extends JFrame {

	public ChartPanel chartPanel;
	public JFreeChart chart;
	public ArrayList<Point> chartData = new ArrayList<>();
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyChartPanel chart = new MyChartPanel();
					chart.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MyChartPanel() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(700, 100, 600, 800);
		getContentPane().setLayout(new BorderLayout());
		chart = ChartFactory.createXYLineChart("Improvement in Fitness", "Ticks", "Fitness Improvement", null, PlotOrientation.VERTICAL, false, false, false);
		chartPanel = new ChartPanel(chart);

		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		chartPanel.setMouseZoomable(true, false);
		this.getContentPane().add(chartPanel, BorderLayout.CENTER);
		this.setFocusable(true);
		
	}
	
	private XYDataset createDataset(ArrayList<Point> data) {
		final XYSeries series = new XYSeries("Best Individual Fitness");
		for (Point p : data) {
			try {				
				series.add(p.x, p.y);				
			} catch (SeriesException e) {
				System.err.println("Error adding to series");
			}
		}
		return new XYSeriesCollection(series);
	}

	public void addDataPoint(Individual individual, Algorithm alg) {
		NumberAxis yAxis = (NumberAxis) chartPanel.getChart().getXYPlot().getRangeAxis();
	    yAxis.setLowerBound(alg.startingFitness);
	    yAxis.setUpperBound(individual.getFitness() * 1.01);	   
		Point p = new Point(Individual.getEvaluations(), individual.getFitness());
		chartData.add(p);
		chartPanel.getChart().getXYPlot().setDataset(createDataset(chartData));
		chartPanel.repaint();
	}

}
