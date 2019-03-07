package mccanny.visual.rendered;

import homelet.GH.visual.CanvasThread;
import mccanny.util.Picture;
import mccanny.visual.dialog.FilterDialog;
import mccanny.visual.dialog.PeriodInfoDialog;
import mccanny.visual.infoCenter.InformationCenter;
import mccanny.visual.infoCenter.OneClickImageDialog;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class IconButtonManager{
	
	public static final int          MAX_BUTTON = 5;
	private final       IconButton[] buttons;
	private final       CanvasThread thread;
	
	public IconButtonManager(CanvasThread thread){
		this.buttons = new IconButton[MAX_BUTTON];
		this.thread = thread;
		this.set(0, Picture.pic().MENU, new IconButtonAction(){
			@Override
			public void onLeftClick(MouseEvent e){
			}
			
			@Override
			public void onRightClick(MouseEvent e){
			}
		});
		this.set(1, Picture.pic().INFO, new IconButtonAction(){
			@Override
			public void onLeftClick(MouseEvent e){
				InformationCenter.showInformationCenter();
			}
			
			@Override
			public void onRightClick(MouseEvent e){
				InformationCenter.showInformationCenter();
			}
		});
		this.set(2, Picture.pic().FILTER, new IconButtonAction(){
			@Override
			public void onLeftClick(MouseEvent e){
				FilterDialog.showInfoDialog();
			}
			
			@Override
			public void onRightClick(MouseEvent e){
				FilterDialog.showInfoDialog();
			}
		});
		this.set(3, Picture.pic().WARNING, new IconButtonAction(){
			@Override
			public void onLeftClick(MouseEvent e){
				OneClickImageDialog.showOneClickDialog();
			}
			
			@Override
			public void onRightClick(MouseEvent e){
			}
		});
		this.set(4, Picture.pic().NEW, new IconButtonAction(){
			@Override
			public void onLeftClick(MouseEvent e){
				PeriodInfoDialog.showInfoDialog(null);
			}
			
			@Override
			public void onRightClick(MouseEvent e){
				do{
					if(PeriodInfoDialog.showInfoDialog(null) == null)
						break;
				}while(true);
			}
		});
	}
	
	public void set(int index, BufferedImage icon, IconButtonAction action){
		IconButton button = new IconButton(this, icon, action);
		button.index(index);
		buttons[index] = button;
		thread.getRenderManager().addPostTargets(button);
	}
}
