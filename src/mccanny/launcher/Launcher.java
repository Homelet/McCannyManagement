package mccanny.launcher;

import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.visual.dialog.PeriodInfoDialog;

import java.awt.*;

public class Launcher{
	
	public static void main(String[] args){
		EventQueue.invokeLater(()->{
//			Display.createDisplay().showDisplay();
			Course.loadCourse("MHF4U", 110, Color.ORANGE);
			for(int index = 0; index < 101; index++){
				Teacher.loadTeacher(String.valueOf(index), String.valueOf(index * index));
				Student.loadStudent(String.valueOf(index), String.valueOf(index * index * index * index));
			}
			Teacher.loadTeacher("000", "Patric");
			Student.loadStudent("111", "Homelet");
			Student.loadStudent("112", "Harry");
			Student.loadStudent("113", "Penny");
			Student.loadStudent("114", "Ethan");
			PeriodInfoDialog.showDialog(null);
		});
	}
}
