import static org.math.array.DoubleArray.increment;

import java.awt.Color;

import javax.swing.JApplet;
import net.miginfocom.swing.MigLayout;

// JMathPlot
import org.math.plot.Plot3DPanel;
import org.math.plot.canvas.PlotCanvas;
import org.math.plot.plots.GridPlot3D;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

// JMathArray
import static org.math.array.LinearAlgebra.*;
import Jama.EigenvalueDecomposition;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashSet;
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
	private JTable TransformationMatrixTable;
	private double[][] transformationMatrix;
	private JButton button_transform;
	private JTextArea textArea_info;
	private JPanel panel;
	private JComboBox comboBox_surfaces;
	private JComboBox comboBox_transform;
	private Delaunay_Triangulation delaunay;
	
	public final String[] surfaces = new String[] {"Plane", "Cube", "Sphere"};

	/**
	 * Create the applet.
	 */
	public RipMathApplet() {
		// set layout of the applet
		getContentPane().setLayout(
				new MigLayout("", "[][200.00px,grow][grow][]", "[][200.00,grow,top][200.00,grow]"));
		
		comboBox_surfaces = new JComboBox();
		comboBox_surfaces.setModel(new DefaultComboBoxModel(surfaces));
		comboBox_surfaces.setEditable(false);
		comboBox_surfaces.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = comboBox_surfaces.getSelectedIndex();
				switch(choice)
				{
				case 0:	data = generatePlane(); break;
				case 1:	data = generateCube(); break;
				case 2: data = generateSphere(); break;
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
		gbl_panel.columnWidths = new int[] { 200, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 24 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		comboBox_transform = new JComboBox();
		comboBox_transform.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = comboBox_transform.getSelectedIndex();
				if(choice == 0)
				{
					TransformationMatrixTable.setValueAt("0", 0, 3);
					TransformationMatrixTable.setValueAt("0", 1, 3);
					TransformationMatrixTable.setValueAt("0", 2, 3);
					TransformationMatrixTable.setValueAt("0", 3, 0);
					TransformationMatrixTable.setValueAt("0", 3, 1);
					TransformationMatrixTable.setValueAt("0", 3, 2);
					TransformationMatrixTable.setValueAt("1", 3, 3);
				}
			
				TransformationMatrixTable.repaint();
			}
		});
		
		comboBox_transform.setModel(new DefaultComboBoxModel(new String[] {
				"LINEAR", "AFFINE" }));
		GridBagConstraints gbc_comboBox_Transform = new GridBagConstraints();
		gbc_comboBox_Transform.anchor = GridBagConstraints.NORTHWEST;
		gbc_comboBox_Transform.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_Transform.gridx = 0;
		gbc_comboBox_Transform.gridy = 0;
		panel.add(comboBox_transform, gbc_comboBox_Transform);

		TransformationMatrixTable = new JTable(){
			public boolean isCellEditable(int row, int column)
			{
				int choice = comboBox_transform.getSelectedIndex();
				if(choice == 0)
				{
					if((row == 0 && column == 3) ||
						(row == 1 && column == 3) ||
						(row == 2 && column == 3) ||
						(row == 3 && column == 0) ||
						(row == 3 && column == 1) ||
						(row == 3 && column == 2) ||
						(row == 3 && column == 3))
					{
						return false;
					}
				}
				return true;
			}
		};
		
		TransformationMatrixTable.setCellSelectionEnabled(true);
		TransformationMatrixTable.setBorder(null);
		TransformationMatrixTable.setModel(new DefaultTableModel(
			new Object[][] {
				{"1", "0", "0", "0"},
				{"0", "1", "0", "0"},
				{"0", "0", "1", "0"},
				{"0", "0", "0", "1"},
			},
			new String[] {
				"", "", "", ""
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		TransformationMatrixTable.getColumnModel().getColumn(0).setPreferredWidth(45);
		TransformationMatrixTable.getColumnModel().getColumn(1).setPreferredWidth(45);
		TransformationMatrixTable.getColumnModel().getColumn(2).setPreferredWidth(45);
		TransformationMatrixTable.getColumnModel().getColumn(3).setPreferredWidth(45);
		
		TransformationMatrixTable.getColumnModel().getColumn(0).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(1).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(2).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(3).setCellRenderer(new ColoredCellRenderer());
		
		GridBagConstraints gbc_TransformationMatrixTable = new GridBagConstraints();
		gbc_TransformationMatrixTable.insets = new Insets(0, 0, 5, 5);
		gbc_TransformationMatrixTable.anchor = GridBagConstraints.SOUTHWEST;
		gbc_TransformationMatrixTable.gridx = 0;
		gbc_TransformationMatrixTable.gridy = 1;
		panel.add(TransformationMatrixTable, gbc_TransformationMatrixTable);
		
		// initialize gui
		button_transform = new JButton("Transform");
		GridBagConstraints gbc_button_transform = new GridBagConstraints();
		gbc_button_transform.insets = new Insets(0, 0, 0, 5);
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
				{ 1, 0, 0, 0 },
				{ 0, 1, 0, 0 }, 
				{ 0, 0, 1, 0 },
				{ 0, 0, 0, 1 }};

		// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
		inplot = new Plot3DPanel("SOUTH");
		inplot.setPreferredSize(new Dimension(300,350));
		inplot.getAxis(0).setColor(Color.BLUE);
		inplot.getAxis(1).setColor(Color.RED);
		
		outplot = new Plot3DPanel("SOUTH");
		outplot.setPreferredSize(new Dimension(300,350));
		outplot.getAxis(0).setColor(Color.BLUE);
		outplot.getAxis(1).setColor(Color.RED);
		
		delaunay = new Delaunay_Triangulation();
		
		getContentPane().add(inplot, "cell 0 1 2 1,alignx center,aligny center");
		getContentPane().add(outplot, "cell 2 1,alignx center,aligny center");
		comboBox_surfaces.setSelectedIndex(0);
	}
	
	static class ColoredCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (table.isCellEditable(row, column)) {
				setBackground(Color.WHITE);
			} else {
				setBackground(Color.GRAY);
			}
			return this;
		}
	}
	
	private double[][] generateSphere() {	
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
	
	public double[][] generatePlane()
	{
		// define your data
		double[] x = increment(-5.0, 0.2, 5.0);
		double[] y = increment(-5.0, 0.2, 5.0);
		double[][] z = f(x, y);
				
		GridPlot3D plane = new GridPlot3D("", Color.BLACK, x, y, z);
		return plane.getData();
	}
	
	public double[][] generateCube()
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
			for (int i = 0; i < 4; i++) 
			{
				for (int j = 0; j < 4; j++) 
				{
					String expression = (String) TransformationMatrixTable.getValueAt(i, j);
					double d = parseAndEvaluate(expression);
					transformationMatrix[i][j] = d;
				}
			}
			
			// double start = System.nanoTime();	// debug
			
			// append dummy column vector
			double[][] points4D = insertColumns(data, 3, one(data.length));
			
			// Get transformed points
			double[][] transformedData = transform(points4D, transformationMatrix);
			
			//double end = System.nanoTime();
			//System.out.println(end-start);
			
	
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
	
	// Actual Transformation
	public static double[][] transform(double[][] points4D, double[][] transformationMatrix) {
		double[][] result = new double[4][points4D.length];

		// matrix multiplication (batch multiplication => much faster)
		result = times(transformationMatrix, transpose(points4D));

		// remove dummy components and return only 3d points
		return deleteColumns(transpose(result), 3);
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
		//double[] zCoords = getColumnCopy(points, 2); // get the z coordinates

		double xMin = min(xCoords);
		double xMax = max(xCoords);
		double xRange = xMax - xMin;

		double yMin = min(yCoords);
		double yMax = max(yCoords);
		double yRange = yMax - yMin;
		/*
		 double zMin = min(zCoords);
		 double zMax = max(zCoords);
		 double zRange = zMax - zMin;
		 */
		for (int i = 0; i < points.length; i++) {
			int R = (int) ((255 / xRange) * (points[i][0] - xMin));
			int G = (int) ((255 / yRange) * (points[i][1] - yMin));
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
	
	public static String format(double[] array)
	{
		return print(array, " ");
	}

	// print matrix
	public static String format(double[][] matrix) {
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