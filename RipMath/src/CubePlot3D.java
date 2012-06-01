import java.awt.Color;
import java.util.ArrayList;

import static org.math.array.DoubleArray.increment;

import org.math.array.DoubleArray;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;

/**
 * @author Pye Phyo Maung
 *
 */
public class CubePlot3D extends Plot {
	public boolean draw_lines = false;
	public boolean fill_shape = false;
	public boolean draw_dots = true;
	
	public double[][] points;
	private double[][] edgePoints; // separated from points for performance
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	public boolean useColorMap = true;
	public Color pointColor;
	public Color edgePointColor;
	
	public CubePlot3D() {
		super("", Color.black);
		generateCube();
		useColorMap = true;
	}
	
	public CubePlot3D(String label, double[][] _points, double[][] _edgePoints, Color[] _colorMap_Points)
	{
		super(label, Color.black);
		points = _points;
		edgePoints = _edgePoints;
		colorMap_Points = _colorMap_Points;
		useColorMap = true;
	}
	
	/**
	 * Constructor for cube without using color map
	 * @param label
	 * @param _points
	 * @param _pointColor
	 * @param _edgePoints
	 * @param _edgePointColor
	 */
	public CubePlot3D(String label, double[][] _points, Color _pointColor, 
			double[][] _edgePoints, Color _edgePointColor)
	{
		super(label, Color.black);
		points = _points;
		edgePoints = _edgePoints;
		useColorMap = false;
		pointColor = _pointColor;
		edgePointColor = _edgePointColor;
	}
	
	public void setEdgePoints(double[][] _edgePoints)
	{
		edgePoints = _edgePoints;
	}
	
	public double[][] getEdgePoints()
	{
		return edgePoints;
	}
	
	@Override
	public void setData(double[][] _points) {
		points = _points;
	}

	@Override
	public double[][] getData() {
		return points;
	}
	
	public int[] getIndices()
	{
		return indices;
	}
	
	public Color[] getColorMap_Points()
	{
		return colorMap_Points;
	}
	
	public Color[] getColorMap_Triangles()
	{
		return colorMap_Triangles;
	}
	
	/**
	 * Generate scattered points of a cube
	 * @return 
	 */
	public void generateCube()
	{
		// generate points
		double[] x = increment(-1.0f, 0.2, 1.0f);
	    double[] y = increment(-1.0f, 0.2, 1.0f);
	    int sideSize = x.length * y.length;
	    points = new double[sideSize * 2 * 6][3];
	    colorMap_Points = new Color[points.length];
	    
	    int index = 0;
	    double a, b, c, d;
	    
	    a = 0; b = 0; c = 1; d = 1;
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
						colorMap_Points[index] = Color.BLUE; 
						index++;
						points[index] = new double[] {x[i], y[j], tmpZ_side2};
						colorMap_Points[index] = Color.GREEN;
						index++;
					case 1:
						points[index] = new double[] {x[i], tmpZ_side1, y[j]};
						colorMap_Points[index] = Color.CYAN;
						index++;
						points[index] = new double[] {x[i], tmpZ_side2, y[j]};
						colorMap_Points[index] = Color.LIGHT_GRAY;
						index++;
					case 2:
						points[index] = new double[] {tmpZ_side1, x[i], y[j]};
						colorMap_Points[index] = Color.PINK;
						index++;
						points[index] = new double[] {tmpZ_side2, x[i], y[j]};
						colorMap_Points[index] = Color.RED;
						index++;
					}
				}
			}
	    }
	    
	    /*add edges (hard coded)
	     * 
	     * **Note to self
	     * The parametric equation for each edge can be written:p = [x y z] = v1 + e(v2-v1), 
	     * where v1 and v2 are the coordinates of the vertices at each end of the edge. 
	     * e = is a real-number which is the fractional distance along the edge: 
	     * a value: 0 <= e <= 1 means that the given point is on the edge. 
	     *
	     */
	    double[] tmpInc = increment(-1.0f, 0.1, 1.0f);
	    ArrayList<double[]> edgePointsList = new ArrayList<double[]>();
	    double[] v1;
		double[] v2;
		double e;
	    for (int i = 0; i < tmpInc.length; i++)
	    {
	    		// using x axis
				v1 = new double[] {1, 0, 0};
				v2 = new double[] {-1, 0, 0};
				e = dist(new double[] {tmpInc[i], 0, 0}, v1) / dist(v1, v2);
				
				edgePointsList.add(new double[] {v1[0] + e * (v2[0]-v1[0]), -1, 1});
				edgePointsList.add(new double[] {v1[0] + e * (v2[0]-v1[0]), 1, 1});
				edgePointsList.add(new double[] {v1[0] + e * (v2[0]-v1[0]), -1, -1});
				edgePointsList.add(new double[] {v1[0] + e * (v2[0]-v1[0]), 1, -1});
				
				// using y axis
				v1 = new double[] {0, -1, 0};
				v2 = new double[] {0, 1, 0};
				e = dist(new double[] {0, tmpInc[i], 0}, v1) / dist(v1, v2);
				
				edgePointsList.add(new double[] {-1, v1[1] + e * (v2[1]-v1[1]), 1});
				edgePointsList.add(new double[] {1, v1[1] + e * (v2[1]-v1[1]), 1});
				edgePointsList.add(new double[] {-1, v1[1] + e * (v2[1]-v1[1]), -1});
				edgePointsList.add(new double[] {1, v1[1] + e * (v2[1]-v1[1]), -1});
				
				// using z axis
				v1 = new double[] {0, 0, -1};
				v2 = new double[] {0, 0, 1};
				e = dist(new double[] {0, 0, tmpInc[i]}, v1) / dist(v1, v2);
				edgePointsList.add(new double[] {-1, 1, v1[2] + e * (v2[2]-v1[2])});
				edgePointsList.add(new double[] {1, 1, v1[2] + e * (v2[2]-v1[2])});
				edgePointsList.add(new double[] {-1, -1, v1[2] + e * (v2[2]-v1[2])});
				edgePointsList.add(new double[] {1, -1, v1[2] + e * (v2[2]-v1[2])});
	    }
	    
	    edgePoints = edgePointsList.toArray(new double[edgePointsList.size()][]);
	}
	
	/**
	 * Calculate the Euclidean distance between two vectors
	 * @param v1 vector
	 * @param v2 vector
	 * @return the Euclidean distance between two vectors
	 */
	public double dist(double[] v1, double[] v2)
	{
		assert(v1.length == v2.length);
		double sum = 0;
		for(int i = 0; i < v1.length; i++)
		{
			sum += (v2[i]-v1[i]) * (v2[i]-v1[i]);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Function definition
	 * @param x
	 * @param y
	 * @return a double integer
	 */
	public static double f(double x, double y) {
		double z = x;
		return z;
	}
	
	/**
	 * Grid version of the function f to be used for grid plot
	 * @param x
	 * @param y
	 * @return
	 */
	public static double[][] f(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f(x[i], y[j]);
		return z;
	}
	
	@Override
	public double[] isSelected(int[] screenCoordTest, AbstractDrawer draw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void plot(AbstractDrawer draw, Color c) {
		if (!visible)
			return;
		
		if(draw_dots)
		{
			if(useColorMap)
			{
				// draw surface
				for(int i = 0; i < points.length; i++)
				{
					draw.setColor(colorMap_Points[i]);
					draw.drawDot(points[i]);
				}
				
				// draw edge
				draw.setColor(Color.BLACK);
				for(int i = 0; i < edgePoints.length; i++)
				{
					draw.drawDot(edgePoints[i]);
				}
			}
			else  // without color map
			{
				// draw surface with mono color
				draw.setColor(pointColor);
				for(int i = 0; i < points.length; i++)
				{
					draw.drawDot(points[i]);
				}
				
				// draw edge
				draw.setColor(edgePointColor);
				for(int i = 0; i < edgePoints.length; i++)
				{
					draw.drawDot(edgePoints[i]);
				}
			}
		}
		
		if(fill_shape)
		{
			
		}
		
		if(draw_lines)
		{
			
		}
	}
}