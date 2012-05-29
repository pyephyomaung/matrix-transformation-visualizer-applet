import java.awt.Color;
import static org.math.array.DoubleArray.increment;

import org.math.array.DoubleArray;
import org.math.plot.plots.GridPlot3D;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;

public class PlanePlot3D extends Plot {
	public boolean draw_lines = false;
	public boolean fill_shape = false;
	public boolean draw_dots = true;
	
	public int numPoints = 1000;
	public double[][] points;
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	
	public PlanePlot3D() {
		super("", Color.black);
		generatePlane();
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
	 * @return scattered points of a plane defined in f
	 */
	public void generatePlane()
	{
		// define your data
		double[] x = increment(-5.0, 0.2, 5.0);
		double[] y = increment(-5.0, 0.2, 5.0);
		double[][] z = f(x, y);
				
		GridPlot3D tmpPlane = new GridPlot3D("", Color.BLACK, x, y, z);
		points = tmpPlane.getData();

		colorMap_Points = new Color[points.length];
		double[] xCoords = DoubleArray.getColumnCopy(points, 0); // get the x coordinates
		double[] yCoords = DoubleArray.getColumnCopy(points, 1); // get the y coordinates
		//double[] zCoords = getColumnCopy(points, 2); // get the z coordinates

		double xMin = DoubleArray.min(xCoords);
		double xMax = DoubleArray.max(xCoords);
		double xRange = xMax - xMin;

		double yMin = DoubleArray.min(yCoords);
		double yMax = DoubleArray.max(yCoords);
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
			colorMap_Points[i] = new Color(R, G, 128);
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