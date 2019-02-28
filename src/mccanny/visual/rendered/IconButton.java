package mccanny.visual.rendered;

import homelet.GH.handlers.GH;
import homelet.GH.utils.Border;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.util.Picture;
import mccanny.visual.Display;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class IconButton extends ActionsManager implements LocatableRender{
	
	private static final Dimension         ICON_DIMENSION = new Dimension(Picture.DEFAULT_ICON_WIDTH + 20, Picture.DEFAULT_ICON_HEIGHT + 20);
	private static final Point             RENDER_OFFSET  = new Point(10, 10);
	private final        BufferedImage     icon;
	private final        Point             vertex;
	private final        Dimension         size;
	private final        IconButtonManager manager;
	private final        IconButtonAction  action;
	private              boolean           active         = false;
	private              boolean           pressing;
	
	IconButton(IconButtonManager manager, BufferedImage icon, IconButtonAction action){
		this.icon = icon;
		this.action = action;
		this.vertex = new Point(0, 5);
		this.size = ICON_DIMENSION;
		this.manager = manager;
	}
	
	void index(int index){
		if(index >= 0 && index < IconButtonManager.MAX_BUTTON){
			active = true;
			vertex.x = 10 + index * (10 + ICON_DIMENSION.width);
		}else{
			active = false;
		}
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return vertex;
	}
	
	@Override
	public boolean isTicking(){
		return active;
	}
	
	@Override
	public boolean isRendering(){
		return active;
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		if(pressing){
			g.setColor(Color.WHITE);
			g.fill(bound);
			Border.drawBorder(g, Border.RECTANGULAR, bound, Display.McCANNY_BLUE, 5, 5);
		}else if(isHovering()){
			g.setColor(Display.McCANNY_BLUE);
			g.fill(bound);
		}
		GH.draw(g, icon, RENDER_OFFSET, null);
	}
	
	@Override
	public void onMousePress(MouseEvent e){
		pressing = true;
	}
	
	@Override
	public void onMouseRelease(MouseEvent e){
		pressing = false;
		if(e.getButton() == MouseEvent.BUTTON1){
			if(action != null)
				action.onLeftClick(e);
		}else if(e.getButton() == MouseEvent.BUTTON3){
			if(action != null)
				action.onRightClick(e);
		}
	}
	
	@Override
	public void onMouseExit(Point p){
		pressing = false;
	}
}
