import java.awt.Color;
import java.util.Iterator;

import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;
import static org.math.array.DoubleArray.increment;

public class CubePlot3D_jme extends Plot {
	public boolean draw_lines = true;
	public boolean fill_shape = false;
	public boolean draw_dots = false;
	
	public double[][] points;
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	
	public CubePlot3D_jme(String n, Color c) {
		super(n, c);
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
	
	private void generateCube()
	{
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
	    
	    int count = 0;
	    for(double X = -1; X < 1; X = X+0.2)
	    {
	    	
	    }
	    indices = new int[] {2, 44, 42};
		//indices = new int[40 * 20 * 2 * 6];
		
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
		int cindex = 0;
		for(int i = 0; i < indices.length / 3; i++)
		{
			double[] p1 = points[indices[cindex++]];
			cindex += 2;
			
			// color mapping is only for unit sphere
			int R = (int) ((255/2) * (p1[0] + 1));
			int G = (int) ((255/2) * (p1[1] + 1));
			colorMap_Triangles[i] = new Color(0,0,128);
		}
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
			for(int i = 0; i < points.length; i++)
			{
				draw.setColor(colorMap_Points[i]);
				draw.drawDot(points[i]);
			}
		}
		
		if(fill_shape)
		{
			int index = 0;
			for(int i = 0; i < indices.length / 3; i++)
			{	
				double[] p1 = points[indices[index++]];
				double[] p2 = points[indices[index++]];
				double[] p3 = points[indices[index++]];
				
				draw.setColor(colorMap_Triangles[i]);
				draw.fillPolygon(1.0f, p1, p2, p3);
			}
		}
		
		if(draw_lines)
		{
			int index = 0;
			for(int i = 0; i < indices.length / 3; i++)
			{	
				double[] p1 = points[indices[index++]];
				double[] p2 = points[indices[index++]];
				double[] p3 = points[indices[index++]];
				
				draw.setColor(colorMap_Triangles[i]);
				draw.drawPolygon(p1, p2, p3);
			}
		}
	}
}