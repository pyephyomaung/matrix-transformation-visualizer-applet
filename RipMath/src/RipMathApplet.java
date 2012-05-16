import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.math.array.DoubleArray.increment;

import java.awt.Color;

import javax.swing.JApplet;
import net.miginfocom.swing.MigLayout;

// JMathPlot
import org.math.plot.Plot3DPanel;
import org.math.plot.plots.GridPlot3D;
import org.math.plot.plots.Plot;
import org.math.plot.plots.ScatterPlot;

import Jama.EigenvalueDecomposition;

// JMathArray
import static org.math.array.LinearAlgebra.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author pye
 *
 */
/**
 * @author pye
 *
 */
/**
 * @author pye
 *
 */
public class RipMathApplet extends JApplet {
	private JTable LinearTransformationMatrix;
	private JButton button_transform;
	private double[][] transformationMatrix;
	private JTextArea textArea_info;
	private JPanel panel;

	/**
	 * Create the applet.
	 */
	public RipMathApplet() {
		// set layout of the applet
		getContentPane().setLayout(new MigLayout("", "[][200.00px,grow][grow]", "[200.00,grow,top][200.00]"));
		
		// define your data
		double[] x = increment(-10.0, 0.5, 10.0); 
		double[] y = increment(-10.0, 0.5, 10.0);
		double[][] z1 = f(x, y);
		
		// testing with 45 degree rotation
		transformationMatrix = new double[][] {
				{cos(45), -sin(45), 0},
				{sin(45), cos(45), 0},
				{0, 0, 1}
		};
		
		
		// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
		Plot3DPanel inplot = new Plot3DPanel("SOUTH");
		Plot3DPanel outplot = new Plot3DPanel("SOUTH");
		
		// transform data
		GridPlot3D gridPlot_in = new GridPlot3D("", Color.BLACK, x, y, z1);
		double[][] data = gridPlot_in.getData();
		double[][] transformedData = linearTransform(data, transformationMatrix);
		
		// plot the input data
		Color[] colorMap = mapColor(data);
		CustomizedScatteredPlot scatterPlot_in = new CustomizedScatteredPlot("scatter", colorMap, data);
		inplot.addPlot(scatterPlot_in);
		
		// plot the transformed (output) data
		CustomizedScatteredPlot scatterPlot_out = new CustomizedScatteredPlot("transform", colorMap, transformedData);
		outplot.addPlot(scatterPlot_out);
				
		getContentPane().add(inplot, "cell 0 0 2 1,grow");
		getContentPane().add(outplot, "cell 2 0,grow");
		
		panel = new JPanel();
		getContentPane().add(panel, "cell 1 1 2 1,grow");
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{200, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 24};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JComboBox comboBox_Transform = new JComboBox();
		comboBox_Transform.setModel(new DefaultComboBoxModel(new String[] {"LINEAR", "AFFINE"}));
		GridBagConstraints gbc_comboBox_Transform = new GridBagConstraints();
		gbc_comboBox_Transform.anchor = GridBagConstraints.NORTHWEST;
		gbc_comboBox_Transform.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_Transform.gridx = 0;
		gbc_comboBox_Transform.gridy = 0;
		panel.add(comboBox_Transform, gbc_comboBox_Transform);
		LinearTransformationMatrix = new JTable();
		LinearTransformationMatrix.setBorder(null);
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
				UpdateInfo();
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
	}
	
	
	/**
	 * Update information on determinant, eigen value and rank 
	 * Called on clicked of button_Transform
	 */
	public void UpdateInfo() {
		double determinant = det(transformationMatrix);
		double eigenValue = 0;
		double rank = rank(transformationMatrix);
		textArea_info.setText(
				"Determinant:\t" + determinant + "\n" +
				"Eigen Value:\t" + eigenValue + "\n" +
				"Rank:\t" + rank);
	}
	
	
	/** 
	 * Map colors to points
	 * @param points 
	 * @return an array color mapping to each point
	 */
	public static Color[] mapColor(double[][] points)
	{
		Color[] colorMap = new Color[points.length];
		
		double[] xCoords = getColumnCopy(points, 0);	// get the x coordinates
		double[] yCoords = getColumnCopy(points, 1);	// get the y coordinates
		double[] zCoords = getColumnCopy(points, 2);	// get the z coordinates
		
		double xMin = min(xCoords);
		double xMax = max(xCoords);
		double xRange = xMax - xMin;
		
		double yMin = min(yCoords);
		double yMax = max(yCoords);
		double yRange = yMax - yMin;
		
		//double zMin = min(zCoords);
		//double zMax = max(zCoords);
		//double zRange = zMax - zMin;
		
		for(int i = 0; i < points.length; i++)
		{
			int R = (int) ((255/xRange) * (points[i][0] - xMin));
			int G = (int) ((255/yRange) * (points[i][1] - yMin));
			//int B = (int) ((255/zRange) * (points[i][2] - zMin));
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