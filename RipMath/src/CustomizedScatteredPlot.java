import java.awt.Color;
import org.math.plot.plots.ScatterPlot;
import org.math.plot.render.AbstractDrawer;

public class CustomizedScatteredPlot extends ScatterPlot {
	private double XY[][];
	private Color colorMap[];
	
	public CustomizedScatteredPlot(String n, Color[] c, double[][] _XY) {
		super(n, Color.BLACK, _XY);
		XY = _XY;
		colorMap = c;
	}
	
	@Override
	public void plot(AbstractDrawer draw, Color c) {
        if (!visible) {
            return;
        }

        for (int i = 0; i < XY.length; i++) {
        	draw.setColor(colorMap[i]);
            draw.drawDot(XY[i]);
        }
    }
}
