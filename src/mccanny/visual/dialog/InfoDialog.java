package mccanny.visual.dialog;

import homelet.GH.utils.Alignment;
import mccanny.visual.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class InfoDialog<E> extends JDialog{
	
	static final int FIXED_LABEL_WIDTH    = 0;
	static final int FIXED_FIELD_WIDTH    = 150;
	static final int FIXED_HEIGHT         = 0;
	static final int FIXED_CONFIRM_HEIGHT = 10;
	static final int FIXED_CONFIRM_WIDTH  = 30;
	
	public InfoDialog(String title){
		super(Display.getInstance(), true);
		setTitle(title);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				closeDialog();
				super.windowClosing(e);
			}
		});
	}
	
	public void closeDialog(){
		this.setVisible(false);
	}
	
	public void showDialog(){
		Point vertex = Alignment.CENTER.getVertex(false, Display.getInstance().getBounds(), this.getBounds());
		if(vertex.x < 0)
			vertex.x = 0;
		if(vertex.y < 0)
			vertex.y = 0;
		if(vertex.x + this.getWidth() > Display.SCREEN_DIMENSION.width)
			vertex.x = Display.SCREEN_DIMENSION.width - this.getWidth();
		if(vertex.y + this.getHeight() > Display.SCREEN_DIMENSION.height)
			vertex.y = Display.SCREEN_DIMENSION.height - this.getHeight();
		this.setLocation(vertex);
		this.setVisible(true);
	}
	
	public void removeDialog(){
		this.dispose();
	}
	
	@Override
	public void setTitle(String templateTitle){
		super.setTitle("InfoDialog - " + templateTitle);
	}
	
	public abstract E result();
}
