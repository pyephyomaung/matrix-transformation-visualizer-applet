import java.awt.Color;
import org.math.plot.plots.Plot;
import org.math.plot.render.AbstractDrawer;

public class CubePlot3D_corner extends Plot {
	public boolean draw_lines = true;
	public boolean fill_shape = true;
	public boolean draw_dots = false;
	public static final double[] ORIGINAL = new double[] {0,0,0};

	private double[] center = ORIGINAL; // default at origin
	private double xWidth = 2;
	private double yWidth = 2;
	private double zWidth = 2;
	private double[][] cornerPoints;

	public CubePlot3D_corner(String n, Color c, double[][] _cornerPoints) {
		super(n, c);
		cornerPoints = _cornerPoints;
	}
	
	public CubePlot3D_corner(String n, Color c, double[] _center, double _xWidth,
			double _yWidth, double _zWidth) {
		super(n, c);
		
		center = _center;
		xWidth = _xWidth;
		yWidth = _yWidth;
		zWidth = _zWidth;
		generateCornerPoints();
	}
		
	private void generateCornerPoints()
	{
		double x = center[0];
		double y = center[1];
		double z = center[2];
		double xOff = xWidth / 2;
		double yOff = yWidth / 2;
		double zOff = zWidth / 2;
		
		cornerPoints = new double[8][3]; // cube has 8 corner points
		
		/*    3 ---- 2
		 *   /|     /|
		 *  0 ---- 1 |
		 *  | |    | |
		 *  |/7 ---|/ 6
		 *  4 ---- 5
		 */
		
		cornerPoints[0] = new double[] {x-xOff, y+yOff, z+zOff};
		cornerPoints[1] = new double[] {x+xOff, y+yOff, z+zOff};
		cornerPoints[2] = new double[] {x+xOff, y-yOff, z+zOff};
		cornerPoints[3] = new double[] {x-xOff, y-yOff, z+zOff};
		
		cornerPoints[4] = new double[] {x-xOff, y+yOff, z-zOff};
		cornerPoints[5] = new double[] {x+xOff, y+yOff, z-zOff};
		cornerPoints[6] = new double[] {x+xOff, y-yOff, z-zOff};
		cornerPoints[7] = new double[] {x-xOff, y-yOff, z-zOff};
	}

	@Override
	public void setData(double[][] _cornerPoints) {
		cornerPoints = _cornerPoints;
	}

	@Override
	public double[][] getData() {
		return cornerPoints;
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
			draw.setColor(c);
			draw.setDotType(AbstractDrawer.ROUND_DOT);
			draw.setDotRadius(AbstractDrawer.DEFAULT_DOT_RADIUS);
			for(int i = 0; i < cornerPoints.length; i++)
				draw.drawDot(cornerPoints[i]);
		}
		
		if(fill_shape)
		{
			// down
			draw.setColor(Color.GREEN);
			draw.fillPolygon(1f, cornerPoints[4], cornerPoints[5], cornerPoints[6], cornerPoints[7]);
			
			// back
			draw.setColor(Color.ORANGE);
			draw.fillPolygon(1f, cornerPoints[3], cornerPoints[2], cornerPoints[6], cornerPoints[7]);
			
			// left
			draw.setColor(Color.BLACK);
			draw.fillPolygon(1f, cornerPoints[0], cornerPoints[3], cornerPoints[7], cornerPoints[4]);
			
			// up
			draw.setColor(Color.GREEN);
			draw.fillPolygon(1f, cornerPoints[0], cornerPoints[1], cornerPoints[2], cornerPoints[3]);
			
			// front
			draw.setColor(Color.ORANGE);
			draw.fillPolygon(1f, cornerPoints[0], cornerPoints[1], cornerPoints[5], cornerPoints[4]);
			
			// right
			draw.setColor(Color.BLACK);
			draw.fillPolygon(1f, cornerPoints[1], cornerPoints[2], cornerPoints[6], cornerPoints[5]);
		}
		
		if(draw_lines)
		{
			draw.setColor(Color.BLACK);
			draw.drawLine(cornerPoints[0], cornerPoints[1]);
			draw.drawLine(cornerPoints[1], cornerPoints[2]);
			draw.drawLine(cornerPoints[2], cornerPoints[3]);
			draw.drawLine(cornerPoints[3], cornerPoints[0]);
			
			draw.drawLine(cornerPoints[4], cornerPoints[5]);
			draw.drawLine(cornerPoints[5], cornerPoints[6]);
			draw.drawLine(cornerPoints[6], cornerPoints[7]);
			draw.drawLine(cornerPoints[7], cornerPoints[4]);
		
			draw.drawLine(cornerPoints[0], cornerPoints[4]);
			draw.drawLine(cornerPoints[1], cornerPoints[5]);
			draw.drawLine(cornerPoints[2], cornerPoints[6]);
			draw.drawLine(cornerPoints[3], cornerPoints[7]);
		}
	}
}