import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.math.array.DoubleArray.increment;

import java.awt.Color;

import javax.swing.JApplet;
import net.miginfocom.swing.MigLayout;

// JMathPlot
import org.math.plot.Plot3DPanel;
import org.math.plot.plots.BoxPlot3D;
import org.math.plot.plots.GridPlot3D;
import org.math.plot.plots.Plot;
import org.math.plot.plots.ScatterPlot;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

// JMathArray
import static org.math.array.LinearAlgebra.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.TrayIcon.MessageType;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Pye Phyo
 * 
 */
public class RipMathApplet extends JApplet {
	private static final double[][] XYdX = null;
	private Plot3DPanel inplot;
	private Plot3DPanel outplot;
	private CustomizedScatteredPlot scatterPlot_in;
	private CustomizedScatteredPlot scatterPlot_out;
	private double[][] data;
	private Color[] colorMap;
	private static Parser psr = new Parser();
	private JTable LinearTransformationMatrix;
	private double[][] transformationMatrix;
	private JButton button_transform;
	private JTextArea textArea_info;
	private JPanel panel;
	private JComboBox comboBox_surfaces;
	
	public String[] surfaces = new String[] {"Plane", "Cube", "Sphere"};

	/**
	 * Create the applet.
	 */
	public RipMathApplet() {
		// set layout of the applet
		getContentPane().setLayout(
				new MigLayout("", "[][200.00px,grow][grow]", "[][200.00,grow,top][200.00]"));
		
		comboBox_surfaces = new JComboBox();
		comboBox_surfaces.setModel(new DefaultComboBoxModel(surfaces));
		comboBox_surfaces.setEditable(false);
		comboBox_surfaces.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = comboBox_surfaces.getSelectedIndex();
				switch(choice)
				{
				case 0:	data = getPlane(); break;
				case 1:	data = getCube(); break;
				case 2: data = getSphere(); break;
				}
				
				colorMap = mapColor(data);
				inplot.removeAllPlots();
				outplot.removeAllPlots();
				scatterPlot_in = new CustomizedScatteredPlot("original", colorMap, data);
				inplot.addPlot(scatterPlot_in);
			}
		});
		getContentPane().add(comboBox_surfaces, "cell 1 0");

		panel = new JPanel();
		getContentPane().add(panel, "cell 1 2 2 1,grow");
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 200, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 24 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JComboBox comboBox_Transform = new JComboBox();
		comboBox_Transform.setModel(new DefaultComboBoxModel(new String[] {
				"LINEAR", "AFFINE" }));
		GridBagConstraints gbc_comboBox_Transform = new GridBagConstraints();
		gbc_comboBox_Transform.anchor = GridBagConstraints.NORTHWEST;
		gbc_comboBox_Transform.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_Transform.gridx = 0;
		gbc_comboBox_Transform.gridy = 0;
		panel.add(comboBox_Transform, gbc_comboBox_Transform);

		LinearTransformationMatrix = new JTable();
		LinearTransformationMatrix.setBorder(null);
		LinearTransformationMatrix.setModel(new DefaultTableModel(
				new Object[][] { { "1", "0", "0" }, { "0", "1", "0" },
						{ "0", "0", "1" }, }, new String[] { "", "", "" }) {
			Class[] columnTypes = new Class[] { String.class, String.class,
					String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		GridBagConstraints gbc_LinearTransformationMatrix = new GridBagConstraints();
		gbc_LinearTransformationMatrix.insets = new Insets(0, 0, 5, 5);
		gbc_LinearTransformationMatrix.anchor = GridBagConstraints.SOUTHWEST;
		gbc_LinearTransformationMatrix.gridx = 0;
		gbc_LinearTransformationMatrix.gridy = 1;
		panel.add(LinearTransformationMatrix, gbc_LinearTransformationMatrix);
		// initialize gui
		button_transform = new JButton("Transform");
		GridBagConstraints gbc_button_transform = new GridBagConstraints();
		gbc_button_transform.insets = new Insets(0, 0, 5, 5);
		gbc_button_transform.gridx = 0;
		gbc_button_transform.gridy = 2;
		panel.add(button_transform, gbc_button_transform);
		button_transform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				transform();
			}
		});

		textArea_info = new JTextArea();
		textArea_info.setEditable(false);
		GridBagConstraints gbc_textArea_info = new GridBagConstraints();
		gbc_textArea_info.fill = GridBagConstraints.BOTH;
		gbc_textArea_info.anchor = GridBagConstraints.NORTHWEST;
		gbc_textArea_info.insets = new Insets(0, 0, 5, 5);
		gbc_textArea_info.gridx = 1;
		gbc_textArea_info.gridy = 1;
		panel.add(textArea_info, gbc_textArea_info);

		// testing with 45 degree rotation
		transformationMatrix = new double[][] { 
				{ 1, 0, 0 },
				{ 0, 1, 0 }, 
				{ 0, 0, 1 } };

		// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
		inplot = new Plot3DPanel("SOUTH");
		outplot = new Plot3DPanel("SOUTH");
		
		getContentPane().add(inplot, "cell 0 1 2 1,grow");
		getContentPane().add(outplot, "cell 2 1,grow");
		comboBox_surfaces.setSelectedIndex(0);
	}
	
	private double[][] getSphere() {
		int numPoints = 5000;
	    double[][] points = new double[numPoints][3];
	    double inc = Math.PI * (3 - Math.sqrt(5));
	    double off = (double) 2 / numPoints;
	    double x, y, z, r, phi;
	 
	    for (int k = 0; k < numPoints; k++){
	        y = k * off - 1 + (off /2);
	        r = Math.sqrt(1 - y * y);
	        phi = k * inc;
	        x = Math.cos(phi) * r;
	        z = Math.sin(phi) * r;
	 
	        points[k] = new double[] {x, y, z};
	    }
	 
	    return points;
	}
	
	public double[][] getPlane()
	{
		// define your data
		double[] x = increment(-10.0, 0.5, 10.0);
		double[] y = increment(-10.0, 0.5, 10.0);
		double[][] z = f(x, y);
				
		GridPlot3D plane = new GridPlot3D("", Color.BLACK, x, y, z);
		return plane.getData();
	}
	
	public double[][] getCube()
	{
		double[] x = increment(-0.5, 0.05, 0.5);
	    double[] y = increment(-0.5, 0.05, 0.5);
	    int sideSize = x.length * y.length;
	    double[][] points = new double[sideSize * 2 * 6][3];
	    
	    int index = 0;
	    double a, b, c, d;
	    
	    a = 0; b = 0; c = 1; d = 0.5;
	    for(int axis = 0; axis < 3; axis++)
	    {
		    for (int i = 0; i < x.length; i++)
		    {
				for (int j = 0; j < y.length; j++)
				{
					double tmpZ_side1 = -(d + a*x[i] + b*y[j])/c;
					double tmpZ_side2 = -(d * -1 + a*x[i] + b*y[j])/c;
					
					switch(axis)
					{
					case 0:
						points[index] = new double[] {x[i], y[j], tmpZ_side1};
						index++;
						points[index] = new double[] {x[i], y[j], tmpZ_side2};
						index++;
					case 1:
						points[index] = new double[] {x[i], tmpZ_side1, y[j]};
						index++;
						points[index] = new double[] {x[i], tmpZ_side2, y[j]};
						index++;
					case 2:
						points[index] = new double[] {tmpZ_side1, x[i], y[j]};
						index++;
						points[index] = new double[] {tmpZ_side2, x[i], y[j]};
						index++;
					}
				}
			}
	    }
	    
	    return points;
	}
	
	public double[] findEigenValue(double[][] matrix)
	{
		EigenvalueDecomposition eigenDecomposition = eigen(transformationMatrix);
		double[][] D = eigenDecomposition.getD().getArray();
		Set<Double> tmpSet = new HashSet<Double>(); 
		for(int i = 0; i < D.length; i++)
		{
			tmpSet.add(D[i][i]);
		}
		
		Double[] tmpSet_array = tmpSet.toArray(new Double[tmpSet.size()]);
		double[] result = new double[tmpSet_array.length];
		for(int k = 0; k < tmpSet_array.length; k++)
		{
			result[k] = tmpSet_array[k];
		}
		return result;
	}

	public void transform() {
		try
		{
			// Update transformation matrix
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					String expression = (String) LinearTransformationMatrix
							.getValueAt(i, j);
					
						double d = parseAndEvaluate(expression);
						transformationMatrix[i][j] = d;
				}
			}
			
			// Get transformed points
			double[][] transformedData = linearTransform(data, transformationMatrix);

			// plot the transformed (output) data
			outplot.removeAllPlots();	// remove the current output plot
			scatterPlot_out = new CustomizedScatteredPlot("transformed", colorMap,
					transformedData);
			outplot.addPlot(scatterPlot_out);
			
			// update the information of the transformation matrix
			updateInfo();
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid expression!");
		}
	}

	/**
	 * Update information on determinant, eigen value and rank Called on clicked
	 * of button_Transform
	 */
	public void updateInfo() {
		double determinant = det(transformationMatrix);
		double[] eigenValues = findEigenValue(transformationMatrix);
		double rank = rank(transformationMatrix);
		textArea_info.setText("Determinant:\t" + determinant + "\n"
				+ "Eigen Value:\t" + print(eigenValues, ", ") + "\n" + "Rank:\t" + rank);
	}

	public static double parseAndEvaluate(String expression) {
		String strd = psr.parse(expression);
		StringTokenizer tokenizer = new StringTokenizer(strd);
		tokenizer.nextToken();
		tokenizer.nextToken();
		Double d = Double.valueOf(tokenizer.nextToken());
		return d;
	}

	/**
	 * Map colors to points
	 * 
	 * @param points
	 * @return an array color mapping to each point
	 */
	public static Color[] mapColor(double[][] points) {
		Color[] colorMap = new Color[points.length];

		double[] xCoords = getColumnCopy(points, 0); // get the x coordinates
		double[] yCoords = getColumnCopy(points, 1); // get the y coordinates
		double[] zCoords = getColumnCopy(points, 2); // get the z coordinates

		double xMin = min(xCoords);
		double xMax = max(xCoords);
		double xRange = xMax - xMin;

		double yMin = min(yCoords);
		double yMax = max(yCoords);
		double yRange = yMax - yMin;

		// double zMin = min(zCoords);
		// double zMax = max(zCoords);
		// double zRange = zMax - zMin;

		for (int i = 0; i < points.length; i++) {
			int R = (int) ((255 / xRange) * (points[i][0] - xMin));
			int G = (int) ((255 / yRange) * (points[i][1] - yMin));
			// int B = (int) ((255/zRange) * (points[i][2] - zMin));
			colorMap[i] = new Color(R, G, 128);
		}

		return colorMap;
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
	public static double[][] linearTransform(double[][] points,
			double[][] transformationMatrix) {
		double[][] result = new double[points.length][3];

		for (int i = 0; i < points.length; i++) {
			result[i] = times(transformationMatrix, points[i]);
		}

		return result;
	}
	
	// print array
	public static String print(double[] array, String separator)
	{
		String s = "";
		for(int i = 0; i < array.length; i++)
		{
			if(i == array.length-1) s += array[i];
			else s += array[i] + separator;
		}
		return s;
	}
	
	public static String print(double[] array)
	{
		return print(array, " ");
	}

	// print matrix
	public static String print(double[][] matrix) {
		String s = "";
		for (double[] row : matrix) 
		{
			for (double data : row) 
			{
				s += data + " ";
			}
			s += "\n";
		}
		return s;
	}

}