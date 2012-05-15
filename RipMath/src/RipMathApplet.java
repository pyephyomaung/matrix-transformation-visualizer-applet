import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.math.array.DoubleArray.increment;

import java.awt.Color;

import javax.swing.JApplet;
import net.miginfocom.swing.MigLayout;

// JMathPlot
import org.math.plot.Plot3DPanel;
import org.math.plot.plots.Plot;

// JMathArray
import static org.math.array.LinearAlgebra.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class RipMathApplet extends JApplet {
	private JTable LinearTransformationMatrix;

	/**
	 * Create the applet.
	 */
	public RipMathApplet() {
		
		// set layout of the applet
		getContentPane().setLayout(new MigLayout("", "[][117.00,grow][grow]", "[300.00,grow,top][200.00]"));
		
		// define your data
		double[] x = increment(-10.0, 1, 10.0); 
		double[] y = increment(-10.0, 1, 10.0);
		double[][] z1 = f(x, y);
		
		// testing with 45 degree rotation
		double[][] transformationMatrix = new double[][] {
				{cos(45), -sin(45), 0},
				{sin(45), cos(45), 0},
				{0, 0, 1}
		};	
		
		// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
		Plot3DPanel inplot = new Plot3DPanel("SOUTH");
		//Plot3DPanel outplot = new Plot3DPanel("SOUTH");
		
		// add grid plot to the PlotPanel
		inplot.addGridPlot("original", x, y, z1);
		
		// get data from plot and transform
		Plot plot = inplot.getPlot(0);
		
		// plot the transformed data
		double[][] data = plot.getData();
		double[][] transformedData = linearTransform(data, transformationMatrix);	
		inplot.addScatterPlot("transform", Color.RED, transformedData);
		
		getContentPane().add(inplot, "cell 0 0 3 1,grow");
		JComboBox comboBox_Transform = new JComboBox();
		comboBox_Transform.setModel(new DefaultComboBoxModel(new String[] {"LINEAR", "AFFINE"}));
		getContentPane().add(comboBox_Transform, "cell 1 1,growx,aligny center");
		
		LinearTransformationMatrix = new JTable();
		LinearTransformationMatrix.setModel(new DefaultTableModel(
			new Object[][] {
				{new Double(1.0), new Double(0.0), new Double(0.0)},
				{new Double(0.0), new Double(1.0), new Double(0.0)},
				{new Double(0.0), new Double(0.0), new Double(1.0)},
			},
			new String[] {
				"", "", ""
			}
		) {
			Class[] columnTypes = new Class[] {
				Double.class, Double.class, Double.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		getContentPane().add(LinearTransformationMatrix, "cell 2 1,alignx center,aligny center");
		//getContentPane().add(outplot, "cell 1 0,grow");
	}

	// function definition
	public static double f1(double x, double y) {
		double z = x;
		return z;
	}

	// grid version of the function
	public static double[][] f(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f1(x[i], y[j]);
		return z;
	}
	
	// linear transformation
	public static double[][] linearTransform(double[][] points, double[][] transformationMatrix)
	{
		double[][] result = new double[points.length][3];
		
		for(int i = 0; i < points.length; i++)
		{
			result[i] = times(transformationMatrix, points[i]);
		}
		
		return result;
	}
	
	// print matrix
	public static void print(double[][] matrix)
	{
		for(double[] row : matrix)
		{
			for(double data : row)
			{
				System.out.print(data + " ");
			}
			System.out.println();
		}
	}
}