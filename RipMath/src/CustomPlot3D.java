import java.awt.Color;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;

public class CustomPlot3D extends Plot {
	public boolean draw_lines = true;
	public boolean fill_shape = true;
	public boolean draw_dots = false;
	
	public double[][] points;
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	
	public CustomPlot3D(String label, double[][] _points, Color[] _colorMap_Points)
	{
		super(label, Color.black);
		points = _points;
		colorMap_Points = _colorMap_Points;
		draw_lines = false;
		fill_shape = false;
		draw_dots = true;
	}
	
	public CustomPlot3D(String label, double[][] _points, int[] _indices, Color[] _colorMap_Points, Color[] _colorMap_Triangles) {
		super(label, Color.black);
		points = _points;
		indices = _indices;
		colorMap_Points = _colorMap_Points;
		colorMap_Triangles = _colorMap_Triangles;
		draw_lines = true;
		fill_shape = false;
		draw_dots = true;
	}
		
	@Override
	public void setData(double[][] _points) {
		points = _points;
	}

	@Override
	public double[][] getData() {
		return points;
	}
	
	public void setIndices(int[] _indices)
	{
		indices = _indices;
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