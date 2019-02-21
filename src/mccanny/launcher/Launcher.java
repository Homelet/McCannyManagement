package mccanny.launcher;

import mccanny.visual.Display;

import java.awt.*;

public class Launcher{
	
	public static void main(String[] args){
		EventQueue.invokeLater(()->{
			Display.createDisplay().showDisplay();
		});
	}
}
