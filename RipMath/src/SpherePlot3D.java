import java.awt.Color;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;

public class SpherePlot3D extends Plot {
	public boolean draw_lines = true;
	public boolean fill_shape = false;
	public boolean draw_dots = false;
	
	public int numPoints = 1000;
	public double[][] points;
	public int[] indices;
	public Color[] colorMap_Points;
	public Color[] colorMap_Triangles;
	
	public SpherePlot3D() {
		super("", Color.black);
		generateSphere_jme();
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
	
	private void generateSphere_jme()
	{
		// unit sphere
		com.jme3.scene.shape.Sphere sphere = new com.jme3.scene.shape.Sphere(32, 32, 1);
		points = JMEHelper.getVertices(sphere);
		indices = JMEHelper.getIndices(sphere);
		
		// color mapping for points
		colorMap_Points = new Color[points.length];
		for(int i = 0; i < points.length; i++)
		{
			// color mapping is only for unit sphere
			int R = (int) ((255/2) * (points[i][0] + 1));
			int G = (int) ((255/2) * (points[i][1] + 1));
			colorMap_Points[i] = new Color(R,G,128);
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
			colorMap_Triangles[i] = new Color(R,G,128);
		}
	}
	
	/* Old
	private double[][] generateSphere() {	
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
	*/

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
			/*
			Iterator<Triangle_dt> it = triangulator.trianglesIterator();
			while(it.hasNext())
			{
				Triangle_dt tri = it.next();
				try
				{
					
						System.out.printf("%f, %f, %f--%f, %f, %f--%f, %f, %f\n", 
								tri.p1().x(), tri.p1().y(), tri.p1().z(),
								tri.p2().x(), tri.p2().y(), tri.p2().z(),
								tri.p3().x(), tri.p3().y(), tri.p3().z());
					
						
						draw.fillPolygon(1.0f,
								new double[] {tri.p1().x(), tri.p1().y(), tri.p1().z()},
								new double[] {tri.p2().x(), tri.p2().y(), tri.p2().z()},
								new double[] {tri.p3().x(), tri.p3().y(), tri.p3().z()});
						draw.setColor(Color.BLUE);
				}
				catch(Exception e) {}
			}
			*/
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