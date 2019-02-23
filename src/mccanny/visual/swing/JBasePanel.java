package mccanny.visual.swing;

import homelet.GH.handlers.GH;
import homelet.GH.utils.Border;
import homelet.GH.utils.ColorBank;

import javax.swing.*;
import java.awt.*;

public class JBasePanel extends JPanel{
	
	private static boolean   showGrid = true;
	private        ColorBank colorBank;
	
	public JBasePanel(){
		this.colorBank = new ColorBank();
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(showGrid)
			paintInner((Graphics2D) g, this, new Point(0, 0), 0);
	}
	
	private Point sumPoint(Point vertex, Point thePoint){
		return new Point(vertex.x + thePoint.x, vertex.y + thePoint.y);
	}
	
	private void paint(Graphics2D g, Point vertex, Component c, int layerIndex){
		Point     innerVertex = sumPoint(vertex, c.getLocation());
		Dimension size        = c.getSize();
		Border.drawBorder(g, Border.RECTANGULAR, GH.rectangle(false, innerVertex, size), colorBank.pollColor(layerIndex), 1, 0);
//		g.draw(GH.line(false, innerVertex.x, innerVertex.y, size.width, size.height));
//		g.draw(GH.line(false, innerVertex.x + size.width, innerVertex.y, -size.width, size.height));
	}
	
	private void paintInner(Graphics2D g, Container c, Point vertex, int layerIndex){
		paint(g, vertex, c, layerIndex);
		layerIndex++;
		synchronized(c.getTreeLock()){
			for(Component comp : c.getComponents()){
				if(!comp.isVisible())
					continue;
				if(comp instanceof Container)
					paintInner(g, ((Container) comp), sumPoint(vertex, c.getLocation()), layerIndex);
				else
					paint(g, vertex, c, layerIndex);
			}
		}
	}
}
