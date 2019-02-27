package mccanny.visual.dialog;

import mccanny.util.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class InfoDialog<E> extends JDialog{
	
	static final Dimension FIXED_BUTTON_DIMENSION        = new Dimension(100, 30);
	static final Dimension FIXED_SQUARE_BUTTON_DIMENSION = new Dimension(30, 30);
	static final Dimension FIXED_FIELD_DIMENSION         = new Dimension(200, 30);
	
	InfoDialog(Frame frameOwner, String title){
		super(frameOwner, title, true);
		init();
	}
	
	private void init(){
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				closeDialog();
				super.windowClosing(e);
			}
		});
		this.setFocusable(true);
	}
	
	public void closeDialog(){
		this.setVisible(false);
	}
	
	InfoDialog(Dialog frameOwner, String title){
		super(frameOwner, title, true);
		init();
	}
	
	public void showDialog(){
		this.setLocation(Utility.frameVertex(this.getOwner().getBounds(), this.getBounds()));
		this.setVisible(true);
	}
	
	public void removeDialog(){
		this.dispose();
	}
	
	public abstract E result();
}
