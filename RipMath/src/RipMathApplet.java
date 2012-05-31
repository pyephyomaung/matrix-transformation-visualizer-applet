import java.awt.Color;
import javax.swing.JApplet;
import net.miginfocom.swing.MigLayout;

// Parser

// JMathPlot
import org.math.plot.Plot3DPanel;
import org.math.plot.plotObjects.BaseLabel;
import org.math.plot.plots.LinePlot;
import org.math.plot.plots.Plot;
import org.math.plot.plots.ScatterPlot;

import parser.*;

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
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.JTabbedPane;

/**
 * @author Pye Phyo Maung
 * 
 */
public class RipMathApplet extends JApplet {
	private Plot3DPanel inplotPanel;
	private Plot3DPanel outplotPanel;
	private Parser psr = new Parser();
	private JTable TransformationMatrixTable;
	private double[][] transformationMatrix;
	private JButton button_transform;
	private JTextArea textArea_info;
	private JPanel panel;
	private JComboBox comboBox_surfaces;
	private JComboBox comboBox_transform;
	private QRDecomposition3x3 qrdecomposition;
	
	private Plot xAxis;
	private Plot yAxis;
	private Plot zAxis;
	private Plot inplot;
	private Plot outplot;
	private Plot qPlot;
	private Plot rPlot;
	
	private static final DecimalFormat fourDecimal = new DecimalFormat("##.0000");
	private double[] inMinBounds;	// min bounds of the input plot box
	private double[] inMaxBounds;	// max bounds of the input plot box
	private double[] outMinBounds;  // min bounds of the output plot box
	private double[] outMaxBounds;	// max bounds of the output plot box
	
	// default minimum bounds of the axes
	private double[] axesMinBounds = new double[] {-10, -10, -10};
	
	// default maximum bounds of the axes
	private double[] axesMaxBounds = new double[] {10, 10, 10};
	
	public final String[] surfaces = new String[] {"Plane", "Cube", "Sphere"};
	private JPanel panel_QR;
	private JTable table_Q;
	private JLabel label_Q;
	private JLabel label_R;
	private JTable table_R;
	private JPanel panel_Q;
	private JPanel panel_R;
	private JCheckBox checkBox_Q;
	private JCheckBox checkBox_R;
	private JPanel panel_transformOption;
	private JCheckBox checkBox_transformPlot;
	private JPanel panel_surfaceOption;
	private JLabel lblChooseASurface;
	
	/**
	 * Enumeration of predefined surfaces
	 */
	public enum Surface
	{
		PLANE, CUBE, SPHERE 
	}
	
	/**
	 * Enumeration of transform modes
	 */
	public enum TransformMode
	{
		LINEAR, AFFINE
	}
	
	public static void main(String[] args)
	{
		RipMathApplet app = new RipMathApplet();
		app.start();
	}

	/**
	 * Constructor for the applet
	 */
	public RipMathApplet() {
		// Initialize inner representation of transformation matrix
		transformationMatrix = new double[][] { 
				{ 1, 0, 0, 0 },
				{ 0, 1, 0, 0 }, 
				{ 0, 0, 1, 0 },
				{ 0, 0, 0, 1 }};
		
		// set layout of the applet
		getContentPane().setLayout(
				new MigLayout("", "[320.00px][320.00px]", "[][320.00px][]"));

		// Initialize GUI cocmponents of plots
		initGUI_plots();
		
		// Initialize the main panel that contains GUIs for transform and QR matrices
		panel = new JPanel();
		JScrollPane panelScroller = new JScrollPane(panel);
		getContentPane().add(panelScroller, "cell 0 2 2 1,grow");
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 200, 200, 0 };
		gbl_panel.rowHeights = new int[] { 100 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 1.0 };
		panel.setLayout(gbl_panel);
		
		// Initialize GUI components of transformation
		initGUI_transform();
		
		// Initialize GUI components of QR
		initGUI_TabbedPanel();

		comboBox_surfaces.setSelectedIndex(0);
		textArea_info.setText("Press \"Transform\" to generate information");
		tabbedPane.setEnabled(false);
	}
	
	private void initGUI_plots()
	{
		// Initialize the combo box for selecting a surface
		panel_surfaceOption = new JPanel();
		getContentPane().add(panel_surfaceOption, "cell 0 0 2 1,grow");
		panel_surfaceOption.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		lblChooseASurface = new JLabel("Choose a surface: ");
		panel_surfaceOption.add(lblChooseASurface);

		comboBox_surfaces = new JComboBox();
		comboBox_surfaces.setModel(new DefaultComboBoxModel(Surface.values()));
		comboBox_surfaces.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Surface choice = (Surface) comboBox_surfaces.getSelectedItem();
				
				/*/ debug
				if(comboBox_surfaces.getSelectedIndex() == 3)
				{
					inplotPanel.removeAllPlots();
					//Plot sphere = new SpherePlot3D("", Color.BLUE);
					Plot plane = new CubePlot3DJME("", Color.BLUE);
					inplotPanel.addPlot(plane);
					return;
				}
				*/
				
				switch(choice)
				{
				case PLANE:	
					inplot = new PlanePlot3D();
					break;
				case CUBE:	
					inplot = new CubePlot3D();
					break;
				case SPHERE: 
					inplot = new SpherePlot3D();
					break;
				}
				
				outplotPanel.removeAllPlots();
				outplotPanel.removeAllPlotables();
				createinplotPanel();
			}
		});
		panel_surfaceOption.add(comboBox_surfaces);
				
		// create your PlotPanel (you can use it as a JPanel)
		initinplotPanel();
		initoutplotPanel();
	}
	
	private void initGUI_transform()
	{	
		panel_transform = new JPanel();
		GridBagConstraints gbc_panel_transform = new GridBagConstraints();
		gbc_panel_transform.insets = new Insets(0, 0, 0, 5);
		gbc_panel_transform.gridx = 0;
		gbc_panel_transform.gridy = 0;
		panel.add(panel_transform, gbc_panel_transform);
		panel_transform.setLayout(new BorderLayout(0, 0));
		
		// Initialize table to display and edit transformation matrix
		TransformationMatrixTable = new JTable(){
			public boolean isCellEditable(int row, int column)
			{
				TransformMode choice = (TransformMode) comboBox_transform.getSelectedItem();
				if(choice.equals(TransformMode.LINEAR))
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
		configTransformationMatrixTable();
		
		panel_transform.add(TransformationMatrixTable, BorderLayout.CENTER);
		// Initialze that contains comboBox_transform and checkBox_useVariables
		panel_transformOption = new JPanel();
		panel_transform.add(panel_transformOption, BorderLayout.SOUTH);
		panel_transformOption.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Initialize check box to use variables in transformation matrix
		checkBox_transformPlot = new JCheckBox("");
		checkBox_transformPlot.setToolTipText("Show/Hide Transformed Plot");
		checkBox_transformPlot.setSelected(true);
		checkBox_transformPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkBox_transformPlot.isSelected())
				{
					try{outplotPanel.removePlot(outplot);} catch(Exception e){}
					try
					{
						outplotPanel.addPlot(outplot);
						syncAxesBounds();
					}
					catch(Exception e){}
				}
				else
				{
					try
					{
						outplotPanel.removePlot(outplot);
						syncAxesBounds();
					}
					catch(Exception e){}
				}
			}
		});
		panel_transformOption.add(checkBox_transformPlot);
		
		// Initialize combo box for selecting transform mode (Linear or Affine)
		comboBox_transform = new JComboBox();
		comboBox_transform.setToolTipText("Select Transform Mode");
		panel_transformOption.add(comboBox_transform);
		comboBox_transform.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TransformMode choice = (TransformMode) comboBox_transform.getSelectedItem();
				if(choice.equals(TransformMode.LINEAR))
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
		
		comboBox_transform.setModel(new DefaultComboBoxModel(TransformMode.values()));
						
		// Initialize button for triggering the transformation
		button_transform = new JButton("Transform");
		panel_transformOption.add(button_transform);
		button_transform.setHorizontalAlignment(SwingConstants.LEFT);
		button_transform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				transform();
				syncAxesBounds();

				// QR decomposition
				double[][] matrix = new double[3][3];
				for(int i = 0; i < 3; i++)
				{
					for(int j = 0; j < 3 ; j++)
					{
						matrix[i][j] = transformationMatrix[i][j];
					}
				}
				qrdecomposition = new QRDecomposition3x3(matrix);				
				updateInfo();
			}
		});
	}
	
	private void initGUI_TabbedPanel()
	{
		// tabbed pane for displaying information about the transformation matrix
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_tabbedPane.insets = new Insets(0, 0, 0, 5);
		gbc_tabbedPane.gridx = 1;
		gbc_tabbedPane.gridy = 0;
		panel.add(tabbedPane, gbc_tabbedPane);
		
		// disable tabbed pane for displaying information
		tabbedPane.setEnabled(false);

		// add a tab for displaying info
		JPanel panel_info = new JPanel();
		tabbedPane.addTab("Info", null, panel_info, null);

		// Initialize text area for displaying the transform matrix info
		textArea_info = new JTextArea();
		panel_info.add(textArea_info);
		textArea_info.setOpaque(false);
		textArea_info.setEditable(false);
		
		// inside QR tab, initialize QR panel that contains panel_Q and panel_R
		panel_QR = new JPanel();
		tabbedPane.addTab("QR Decomposition", null, panel_QR, null);
		tabbedPane.setEnabledAt(1, true);
		panel_QR.setLayout(new GridLayout(3, 1, 0, 0));
		
		// Initialize panel for Q
		panel_Q = new JPanel();
		panel_Q.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
		panel_QR.add(panel_Q);
		
		// Check box for drawing the transformation with Q
		checkBox_Q = new JCheckBox("");
		checkBox_Q.setToolTipText("Show/Hide Plot Transformed by Q");
		checkBox_Q.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkBox_Q.isSelected())
				{
					createQPlot();
				}
				else
				{
					if(qPlot != null) 
					{
						outplotPanel.removePlot(qPlot);
						syncAxesBounds();
					}
				}
			}
		});
		panel_Q.add(checkBox_Q);
		label_Q = new JLabel("Q = ");
		panel_Q.add(label_Q);

		// Table to display Q matrix
		table_Q = new JTable();
		table_Q.setBorder(null);
		panel_Q.add(table_Q);
		table_Q.setModel(new DefaultTableModel(
				new Object[][] {
						{null, null, null},
						{null, null, null},
						{null, null, null},
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

		checkBox_QColor = new JCheckBox("Color");
		checkBox_QColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(qPlot != null && checkBox_QColor.isSelected()) 
				{
					outplotPanel.removePlot(qPlot);
					syncAxesBounds();
				}
				createQPlot();
			}
		});
		panel_Q.add(checkBox_QColor);
		table_Q.getColumnModel().getColumn(0).setPreferredWidth(45);
		table_Q.getColumnModel().getColumn(1).setPreferredWidth(45);
		table_Q.getColumnModel().getColumn(2).setPreferredWidth(45);

		// Initialize panel for R
		panel_R = new JPanel();
		panel_R.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
		panel_QR.add(panel_R);

		// Check box for drawing the transformation with R
		checkBox_R = new JCheckBox("");
		checkBox_R.setToolTipText("Show/Hide Plot Transformed by R");
		checkBox_R.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(checkBox_R.isSelected())
				{
					createRPlot();
				}
				else
				{
					if(rPlot != null) 
					{
						outplotPanel.removePlot(rPlot);
						syncAxesBounds();
					}
				}
			}
		});
		panel_R.add(checkBox_R);

		label_R = new JLabel("R = ");
		panel_R.add(label_R);

		// Table to display R matrix
		table_R = new JTable();
		table_R.setBorder(null);
		table_R.setModel(new DefaultTableModel(
				new Object[][] {
						{null, null, null},
						{null, null, null},
						{null, null, null},
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
		table_R.getColumnModel().getColumn(0).setPreferredWidth(45);
		table_R.getColumnModel().getColumn(1).setPreferredWidth(45);
		table_R.getColumnModel().getColumn(2).setPreferredWidth(45);
		panel_R.add(table_R);

		checkBox_RColor = new JCheckBox("Color");
		checkBox_RColor.setToolTipText("");
		checkBox_RColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rPlot != null && checkBox_RColor.isSelected()) 
				{
					outplotPanel.removePlot(rPlot);
					syncAxesBounds();
				}
				createRPlot();
			}
		});
		panel_R.add(checkBox_RColor);
	}
	
	private void configTransformationMatrixTable()
	{
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
	
		// Add custom renderer to the transformation matrix table for locking the cell in linear mode
		TransformationMatrixTable.getColumnModel().getColumn(0).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(1).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(2).setCellRenderer(new ColoredCellRenderer());
		TransformationMatrixTable.getColumnModel().getColumn(3).setCellRenderer(new ColoredCellRenderer());
	}
	
	/**
	 * Initialize outplotPanel
	 */
	private void initoutplotPanel()
	{
		outplotPanel = new Plot3DPanel();
		outplotPanel.setPreferredSize(new Dimension(300, 320));
		outplotPanel.plotCanvas.getGrid().setVisible(false);
		outplotPanel.removeLegend();
		outplotPanel.plotToolBar.setPreferredSize(new Dimension(300, 24));
		getContentPane().add(outplotPanel, "cell 1 1,alignx center,aligny center");
	}
	
	
	/**
	 * Initialize inplotPanel
	 */
	private void initinplotPanel()
	{
		inplotPanel = new Plot3DPanel();
		inplotPanel.setPreferredSize(new Dimension(300, 320));
		inplotPanel.plotCanvas.getGrid().setVisible(false);
		inplotPanel.removeLegend();
	
		getContentPane().add(inplotPanel, "cell 0 1,alignx center,aligny center");
		inplotPanel.plotToolBar.setPreferredSize(new Dimension(200, 24));
	}
	
	/**
	 * Create inplotPanel and add axes
	 */
	private void createinplotPanel()
	{
		inplotPanel.removeAllPlots();
		inplotPanel.removeAllPlotables();
		inplotPanel.addPlot(inplot);
		
		/*/ Debug
		CubePlot3D testPlot = new CubePlot3D("", Color.RED, 
				CubePlot3D.ORIGINAL, 2, 2, 2);
		data = testPlot.getData();
		inplotPanel.addPlot(testPlot);
		*/
		
		// create axes
		inMinBounds = copy(inplotPanel.plotCanvas.base.getMinBounds());
		inMaxBounds = copy(inplotPanel.plotCanvas.base.getMaxBounds());
		// zoom in a little bit
		for(int i = 0; i < inMinBounds.length; i++)
		{
			inMinBounds[i] /= 1.2;
			inMaxBounds[i] /= 1.2;
		}
		xAxis = new LinePlot("", Color.BLUE, new double[][] {{axesMinBounds[0],0,0},{axesMaxBounds[0],0,0}});
		yAxis = new LinePlot("", Color.RED,new double[][] {{0,axesMinBounds[1],0},{0,axesMaxBounds[1],0}});
		zAxis = new LinePlot("", Color.BLACK,new double[][] {{0,0,axesMinBounds[2]},{0,0,axesMaxBounds[2]}}); 
		addAxes(inplotPanel);
		
		inplotPanel.setFixedBounds(inMinBounds, inMaxBounds);
	}
	
	private void createQPlot()
	{
		removeAxes(outplotPanel);
		if(qPlot != null) outplotPanel.removePlot(qPlot);
		double[][] qTransformData = transpose(times(qrdecomposition.Q, transpose(inplot.getData())));
		
		if(checkBox_QColor.isSelected())
		{
			Surface choice = (Surface) comboBox_surfaces.getSelectedItem();
			switch(choice)
			{
			case PLANE:
				PlanePlot3D tmpPlanePlot = (PlanePlot3D) inplot;
				qPlot = new CustomPlot3D("", qTransformData, tmpPlanePlot.getColorMap_Points());
				break;
			case CUBE:
				CubePlot3D tmpCubePlot = (CubePlot3D) inplot;
				// special case
				double[][] qTransformEdgeData = transpose(times(qrdecomposition.Q, transpose(tmpCubePlot.getEdgePoints())));
				qPlot = new CubePlot3D("", qTransformData, qTransformEdgeData, tmpCubePlot.getColorMap_Points());
				break;
			case SPHERE:
				SpherePlot3D tmpSpherePlot = (SpherePlot3D) inplot;;
				qPlot = new CustomPlot3D("", 
						qTransformData, tmpSpherePlot.getIndices(), 
						tmpSpherePlot.getColorMap_Points(), tmpSpherePlot.getColorMap_Triangles());
				break;
			}
		}
		else
		{
			qPlot = new ScatterPlot("", Color.lightGray, qTransformData);
		}
		
		if(checkBox_Q.isSelected())
		{
			outplotPanel.addPlot(qPlot);
		}
		
		syncAxesBounds();
	}
	
	private void createRPlot()
	{
		removeAxes(outplotPanel);
		if(rPlot != null) outplotPanel.removePlot(rPlot);
		double[][] rTransformData = transpose(times(qrdecomposition.R, transpose(inplot.getData())));
		
		if(checkBox_RColor.isSelected())
		{
			Surface choice = (Surface) comboBox_surfaces.getSelectedItem();
			switch(choice)
			{
			case PLANE:
				PlanePlot3D tmpPlanePlot = (PlanePlot3D) inplot;
				rPlot = new CustomPlot3D("", rTransformData, tmpPlanePlot.getColorMap_Points());
				break;
			case CUBE:
				CubePlot3D tmpCubePlot = (CubePlot3D) inplot;
				// special case
				double[][] rTransformEdgeData = transpose(times(qrdecomposition.R, transpose(tmpCubePlot.getEdgePoints())));
				rPlot = new CubePlot3D("", rTransformData, rTransformEdgeData, tmpCubePlot.getColorMap_Points());
				break;
			case SPHERE:
				SpherePlot3D tmpSpherePlot = (SpherePlot3D) inplot;;
				rPlot = new CustomPlot3D("", 
						rTransformData, tmpSpherePlot.getIndices(), 
						tmpSpherePlot.getColorMap_Points(), tmpSpherePlot.getColorMap_Triangles());
				break;
			}
		}
		else
		{
			rPlot = new ScatterPlot("", Color.CYAN, rTransformData);
		}
		
		if(checkBox_R.isSelected())
		{
			outplotPanel.addPlot(rPlot);
		}
		syncAxesBounds();
	}
	
	private org.math.plot.plotObjects.Label xAxisLabel;
	private org.math.plot.plotObjects.Label yAxisLabel;
	private org.math.plot.plotObjects.Label zAxisLabel;
	private JPanel panel_transform;
	private JTabbedPane tabbedPane;
	private JCheckBox checkBox_QColor;
	private JCheckBox checkBox_RColor;
	private void addAxes(Plot3DPanel plot3DPanel)
	{
		try
		{
			removeAxes(plot3DPanel);
		}
		catch(Exception e){}
		
		plot3DPanel.addPlot(xAxis);
		plot3DPanel.addPlot(yAxis);
		plot3DPanel.addPlot(zAxis);
		
		// add label to axes
		xAxisLabel = new org.math.plot.plotObjects.Label("X", Color.BLUE, 2, 0, 0);
		xAxisLabel.setFont(new java.awt.Font("Courier", java.awt.Font.BOLD, 12));
		yAxisLabel = new org.math.plot.plotObjects.Label("Y", Color.RED, 0, 2, 0);
		yAxisLabel.setFont(new java.awt.Font("Courier", java.awt.Font.BOLD, 12));
		zAxisLabel = new org.math.plot.plotObjects.Label("Z", Color.BLACK, 0, 0, 2);
		zAxisLabel.setFont(new java.awt.Font("Courier", java.awt.Font.BOLD, 12));
	
		try
		{
			plot3DPanel.removePlotable(xAxisLabel);
			plot3DPanel.removePlotable(yAxisLabel);
			plot3DPanel.removePlotable(zAxisLabel);
		}
		catch(Exception e){}
		plot3DPanel.addPlotable(xAxisLabel);
		plot3DPanel.addPlotable(yAxisLabel);
		plot3DPanel.addPlotable(zAxisLabel);
	}
	
	private void removeAxes(Plot3DPanel plot3DPanel)
	{
		plot3DPanel.removePlot(xAxis);
		plot3DPanel.removePlot(yAxis);
		plot3DPanel.removePlot(zAxis);
		
		// remove axes labels
		plot3DPanel.removePlotable(xAxisLabel);
		plot3DPanel.removePlotable(yAxisLabel);
		plot3DPanel.removePlotable(zAxisLabel);
	}
	
	/**
	 * Synchronize Axes of inplotPanel and outplotPanel
	 */
	public void syncAxesBounds()
	{			
		// draw axes for outplotPanel
		outMinBounds = outplotPanel.plotCanvas.base.getMinBounds(); 
		outMaxBounds = outplotPanel.plotCanvas.base.getMaxBounds();
		
		try
		{
			inplotPanel.removePlot(xAxis);
			inplotPanel.removePlot(yAxis);
			inplotPanel.removePlot(zAxis);
		}
		catch(Exception e)
		{
			//System.out.println(e.toString());
		}
		
		for(int i = 0; i < 3; i++)
		{
			double min = axesMinBounds[i];
			double max = axesMaxBounds[i];
			
			// extend axes if necessary
			if(outMinBounds[i] < min) min = outMinBounds[i] - 5;
			if(outMaxBounds[i] > max) max = outMaxBounds[i] + 5;
			
			switch(i)
			{
			case 0:
				xAxis = new LinePlot("", Color.BLUE,new double[][] {{min,0,0},{max,0,0}});
				break;
			case 1:
				yAxis = new LinePlot("", Color.RED,new double[][] {{0,min,0},{0,max,0}});
				break;
			case 2:
				zAxis = new LinePlot("", Color.BLACK,new double[][] {{0,0,min},{0,0,max}});
				break;
			}
		}
		
		addAxes(inplotPanel);
		addAxes(outplotPanel);
		
		// lock the bounds of inplotPanel and outplotPanel so the views are synchronized
		inplotPanel.setFixedBounds(inMinBounds, inMaxBounds);
		outplotPanel.setFixedBounds(inMinBounds, inMaxBounds);
	}
	
	/**
	 * Custom renderer for cells in table
	 * Uneditable cells are blacked out.
	 * @author pye
	 *
	 */
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
	
	/**
	 * Find eigen values of a matrix
	 * @param matrix a two-dimensional array representing a matrix
	 * @return the eigen values for the input matrix
	 */
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

	/**
	 * A general function for transformation defined by TransformationMatrixTable
	 * The transformation can be either linear or affine
	 */
	public void transform() {
		// Enable tabbed pane for displaying information
		tabbedPane.setEnabled(true);
		
		boolean hasVariable = false;
		for (int i = 0; i < 4; i++) 
		{
			for (int j = 0; j < 4; j++) 
			{
				String expression = (String) TransformationMatrixTable.getValueAt(i, j);
				if(expression.toLowerCase().contains("x") ||
						expression.toLowerCase().contains("y") ||
						expression.toLowerCase().contains("z"))
				{
					hasVariable = true;
				}
			}
		}
		
		try
		{	
			// double start = System.nanoTime();	// debug
			
			// append dummy column vector
			double[][] points4D = insertColumns(inplot.getData(), 3, one(inplot.getData().length));
			
			// Get transformed points
			double[][] transformedData;
			if(hasVariable)
			{
				psr.useCustomVariables = true;
				transformedData = transformWithVariable(points4D, transformationMatrix);
			}
			else
			{
				psr.useCustomVariables = false;
				transformedData = transform(points4D, transformationMatrix);
			}
			
			//double end = System.nanoTime();
			//System.out.println(end-start);
				
			// plot the transformed (output) data
			
			Surface choice = (Surface) comboBox_surfaces.getSelectedItem();
			switch(choice)
			{
			case PLANE:
				PlanePlot3D tmpPlanePlot = (PlanePlot3D) inplot;
				outplot = new CustomPlot3D("", transformedData, tmpPlanePlot.getColorMap_Points());
				break;
			case CUBE:
				CubePlot3D tmpCubePlot = (CubePlot3D) inplot;
				// special case
				double[][] edgePoints4D = insertColumns(tmpCubePlot.getEdgePoints(), 3, one(tmpCubePlot.getEdgePoints().length));
				// Get transformed edge points
				double[][] transformedEdgeData;
				if(hasVariable)
				{
					psr.useCustomVariables = true;
					transformedEdgeData = transformWithVariable(edgePoints4D, transformationMatrix);
				}
				else
				{
					psr.useCustomVariables = false;
					transformedEdgeData = transform(edgePoints4D, transformationMatrix);
				}
				outplot = new CubePlot3D("", transformedData, transformedEdgeData, tmpCubePlot.getColorMap_Points());
				break;
			case SPHERE:
				SpherePlot3D tmpSpherePlot = (SpherePlot3D) inplot;;
				outplot = new CustomPlot3D("", 
						transformedData, tmpSpherePlot.getIndices(), 
						tmpSpherePlot.getColorMap_Points(), tmpSpherePlot.getColorMap_Triangles());
				break;
			}
			
			outplotPanel.removeAllPlots();	// remove the current output plot
			if(checkBox_transformPlot.isSelected())
			{
				outplotPanel.addPlot(outplot);
			}
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "Invalid expression!");
		}
	}
	
	/**
	 * Actual Transformation
	 * @param points4D
	 * @param transformationMatrix
	 * @return transformed points in 3D
	 */
	public double[][] transform(double[][] points4D, double[][] transformationMatrix) {
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
		
		double[][] result = new double[4][points4D.length];

		// matrix multiplication (batch multiplication => much faster)
		result = times(transformationMatrix, transpose(points4D));

		// remove dummy components and return only 3d points
		return deleteColumns(transpose(result), 3);
	}
	
	public double[][] transformWithVariable(double [][] points4D, double[][] transformationMatrix)
	{
		double[][] result = new double[points4D.length][4];
		
		for(int count = 0; count < points4D.length; count++)
		{
			double[] tmpPt = points4D[count];
			psr.setXYZ(tmpPt[0], tmpPt[1], tmpPt[2]);
			
			for (int i = 0; i < 4; i++) 
			{
				for (int j = 0; j < 4; j++) 
				{
					String expression = (String) TransformationMatrixTable.getValueAt(i, j);
					double d = parseAndEvaluate(expression);
					transformationMatrix[i][j] = d;
				}
			}
			
			result[count] = times(transformationMatrix, tmpPt);
		}
		
		return deleteColumns(result, 3);
	}
	
	/**
	 * Update information on determinant, eigen value, rank and QR matrices 
	 * Called on clicked of button_Transform
	 */
	public void updateInfo() {
		double[][] tmpMatrix;
		
		TransformMode choice = (TransformMode) comboBox_transform.getSelectedItem();
		// linear
		if(choice.equals(TransformMode.LINEAR))
		{
			tmpMatrix = new double[3][3];
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					tmpMatrix[i][j] = transformationMatrix[i][j];
				}
			}
		}
		// affine
		else
		{
			tmpMatrix = transformationMatrix;
		}
		
		double determinant = det(tmpMatrix);
		double[] eigenValues = findEigenValue(tmpMatrix);
		double rank = rank(tmpMatrix);
		textArea_info.setText("Determinant:\t" + fourDecimal.format(determinant) + "\n"
				+ "Eigen Value:\t" + format(eigenValues, ", ") + "\n" 
				+ "Rank:\t" + rank);
		
		
		try {
			// update Q and R matrices
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					table_Q.setValueAt(qrdecomposition.Q[i][j], i, j);
					table_R.setValueAt(qrdecomposition.R[i][j], i, j);
				}
			}
			
			// update Q and R transform plots if the check boxes are selected
			if(checkBox_Q.isSelected())
			{
				createQPlot();
			}
			
			if(checkBox_R.isSelected())
			{
				createRPlot();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Parse the expression and then evaluate it
	 * @param expression
	 * @return a double integer resulted from the evaluation of the expession
	 */
	public double parseAndEvaluate(String expression) throws NumberFormatException{
		String strd = psr.parse(expression);
		
		/*/ Debug parser with variable
		psr.setXYZ(2, 0, 0);
		System.out.print(psr.parse("2*x + 3*y"));
		*/
		
		StringTokenizer tokenizer = new StringTokenizer(strd);
		tokenizer.nextToken();
		tokenizer.nextToken();
		Double d = Double.valueOf(tokenizer.nextToken());
		return d;
	}
	
	/**
	 * Format an array defined by a separator
	 * @param array
	 * @param separator
	 * @return a formatted string of the input array
	 */
	public static String format(double[] array, String separator)
	{
		String s = "";
		for(int i = 0; i < array.length; i++)
		{
			if(i == array.length-1) s += array[i];
			else s += fourDecimal.format(array[i]) + separator;
		}
		return s;
	}
	
	
	/**
	 * Format an array defined by a default separator
	 * @param array
	 * @return a formatted string of the input array separated by a white space
	 */
	public static String format(double[] array)
	{
		return format(array, " ");
	}

	/**
	 * Format a matrix
	 * @param matrix
	 * @return a formattetd string of the input matrix
	 */
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