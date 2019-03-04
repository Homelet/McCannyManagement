package mccanny.launcher;

import mccanny.io.Builder;
import mccanny.visual.Display;

import java.awt.*;

public class Launcher{
	
	public static void main(String[] args){
		EventQueue.invokeLater(()->{
			Display.createDisplay().showDisplay();
//			Builder.parseTest();
//			Builder.writerTest();
			Builder.writeStudents();
			Builder.writeTeachers();
			Builder.writeCourses();
		});
	}
}
