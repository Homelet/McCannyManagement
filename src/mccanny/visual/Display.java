package mccanny.visual;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.JCanvas;
import mccanny.management.course.Course;
import mccanny.management.course.CourseManager;
import mccanny.management.course.CoursePeriod;
import mccanny.management.course.TimeTable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.util.Weekday;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Display extends JFrame{
	
	private static Display display;
	
	public static Display createDisplay(){
		display = new Display();
		display.construct();
		return display;
	}
	
	public static Display getInstance(){
		return display;
	}
	
	public static       Font          CLEAR_SANS_BOLD;
	public static final Color         McCANNY_BLUE = new Color(0x00205E);
	public static final String        VERSION      = "0.1";
	private final       JCanvas       canvas;
	private final       CourseManager manager;
	
	static{
		try{
			CLEAR_SANS_BOLD = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/Clear Sans Bold.ttf")).deriveFont(15.0f);
		}catch(FontFormatException | IOException e){
			e.printStackTrace();
		}
	}
	
	private Display() throws HeadlessException{
		super("McCanny TimeTable");
		setName("2048");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.canvas = new JCanvas("TimeTable");
		this.canvas.getCanvasThread().setPrintNoticeInConsole(true);
		this.canvas.getCanvasThread().setFPS(-1);
		this.manager = new CourseManager(canvas.getCanvasThread());
		JPanel                   panel    = new JPanel();
		Layouter.GridBagLayouter layouter = new GridBagLayouter(panel);
		layouter.put(layouter.instanceOf(canvas, 0, 0).setWeight(100, 100).setFill(Fill.BOTH).setAnchor(Anchor.CENTER));
		this.setContentPane(panel);
	}
	
	private void construct(){
		Course                  course   = new Course("MHF4U", new Color(0xFF6600));
		Teacher                 teacher  = new Teacher("0", "Patric");
		Teacher                 teacher1 = new Teacher("0", "Eleanor");
		Student                 student  = new Student("0", "Homelet");
		Student                 student1 = new Student("0", "Harry");
		Student                 student2 = new Student("0", "Penny");
		Student                 student3 = new Student("0", "Emma");
		Student                 student4 = new Student("0", "Brendon");
		Student                 student5 = new Student("0", "Ethan");
		Student                 student6 = new Student("0", "Olivia");
		ArrayList<CoursePeriod> periods  = new ArrayList<>();
		//
		CoursePeriod period2 = new CoursePeriod(course, 10, Weekday.MONDAY, 8, 8.5);
		period2.addTeacher(true, Arrays.asList(teacher, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1));
		//
		CoursePeriod period = new CoursePeriod(course, 10, Weekday.MONDAY, 8, 10);
		period.addTeacher(true, Arrays.asList(teacher, teacher1, teacher1, teacher1, teacher1, teacher1));
		//
		CoursePeriod period3 = new CoursePeriod(course, 10, Weekday.MONDAY, 8, 9.5);
		period3.addTeacher(true, Arrays.asList(teacher, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1));
		//
		CoursePeriod period4 = new CoursePeriod(course, 10, Weekday.MONDAY, 8, 12);
		period4.addTeacher(true, Arrays.asList(teacher1, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1, teacher1));
//		CoursePeriod period5 = new CoursePeriod(course, 10, Weekday.MONDAY, 11.5, 12.5);
//		period5.addStudent(true, Arrays.asList(student));
//		period5.addTeacher(true, Arrays.asList(teacher));
		periods.add(period2);
		periods.add(period);
		periods.add(period3);
		periods.add(period4);
//		periods.add(period5);
		this.manager.initializeTimeTable(new TimeTable(new Date(2019, 02.f, 10.f), periods));
		updateDimension();
	}
	
	public JCanvas canvas(){
		return canvas;
	}
	
	public CourseManager manager(){
		return manager;
	}
	
	public void showDisplay(){
		this.setVisible(true);
		this.canvas.startRendering();
	}
	
	/**
	 * 39 for windows
	 * 21 for mac
	 */
	public void updateDimension(){
		Dimension dimension = new Dimension(CourseManager.TIMETABLE_DI.width + CourseManager.LEFT_INSET + CourseManager.RIGHT_INSET, CourseManager.TIMETABLE_DI.height + CourseManager.BOTTOM_INSET + CourseManager.TOP_INSET + 21);
		System.out.println(dimension);
		ToolBox.setPreferredSize(this, dimension);
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING)
			;
		super.processWindowEvent(e);
	}
}
