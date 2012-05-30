import java.awt.Color;
import static org.math.array.DoubleArray.increment;
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
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	
	public CubePlot3D() {
		super("", Color.black);
		generateCube();
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
	 * @return scattered points of a cube
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
						colorMap_Points[index] = Color.MAGENTA;
						index++;
						points[index] = new double[] {x[i], tmpZ_side2, y[j]};
						colorMap_Points[index] = Color.ORANGE;
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
	
	/*
	private void generatePlane()
	{
		com.jme3.scene.shape.Quad plane = new com.jme3.scene.shape.Quad(10, 5);
		points = JMEHelper.getVertices(plane);
		indices = JMEHelper.getIndices(plane);
		
		// color mapping for points
		colorMap_Points = new Color[points.length];
		for(int i = 0; i < points.length; i++)
		{
			// color mapping is only for unit sphere
			int R = (int) ((255/2) * (points[i][0] + 1));
			int G = (int) ((255/2) * (points[i][1] + 1));
			colorMap_Points[i] = new Color(0,0,128);
		}
		
		// color mapping for triangles
		colorMap_Triangles = new Color[indices.length / 3];
		int index = 0;
		for(int i = 0; i < indices.length / 3; i++)
		{
			double[] p1 = points[indices[index++]];
			index += 2;
			
			// color mapping is only for unit sphere
			int R = (int) ((255/2) * (p1[0] + 1));
			int G = (int) ((255/2) * (p1[1] + 1));
			colorMap_Triangles[i] = new Color(0,0,128);
		}
	}*/
	
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
			for(int i = 0; i < points.length; i++)
			{
				draw.setColor(colorMap_Points[i]);
				draw.drawDot(points[i]);
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