package mccanny.visual.rendered;

import homelet.GH.visual.CanvasThread;

import java.awt.image.BufferedImage;

public class IconButtonManager{
	
	public static final int          MAX_BUTTON = 5;
	private final       IconButton[] buttons;
	private final       CanvasThread thread;
	
	public IconButtonManager(CanvasThread thread){
		this.buttons = new IconButton[MAX_BUTTON];
		this.thread = thread;
	}
	
	public void set(int index, BufferedImage icon, IconButtonAction action){
		IconButton button = new IconButton(this, icon, action);
		button.index(index);
		buttons[index] = button;
		thread.getRenderManager().addPostTargets(button);
	}
}
