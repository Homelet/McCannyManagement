package mccanny.visual.dialog;

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
	
	public InfoDialog(Frame frameOwner, String title){
		super(frameOwner, true);
		setTitle(title);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(true);
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
