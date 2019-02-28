package mccanny.visual.rendered;

import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class IconButton extends ActionsManager implements LocatableRender{
	
	private final BufferedImage icon;
	
	public IconButton(BufferedImage icon){
		this.icon = icon;
	}
	
	@Override
	public Dimension getSize(){
		return null;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return null;
	}
	
	@Override
	public void tick(){
	}
	
	@Override
	public void render(Graphics2D graphics2D){
	}
}
