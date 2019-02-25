package mccanny.visual;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.JCanvas;
import mccanny.management.course.Course;
import mccanny.management.course.CourseManager;
import mccanny.management.course.TimeTable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.visual.dialog.CourseInfoDialog;
import mccanny.visual.dialog.PeriodInfoDialog;
import mccanny.visual.dialog.StudentInfoDialog;
import mccanny.visual.dialog.TeacherInfoDialog;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

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
	public static       Dimension     SCREEN_DIMENSION;
	public static final Color         McCANNY_BLUE = new Color(0x00205E);
	public static final String        VERSION      = "V0.1";
	public static       boolean       FORMAT_24    = false;
	private final       JCanvas       canvas;
	private final       CourseManager manager;
	
	static{
		try{
			CLEAR_SANS_BOLD = Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/Clear Sans Bold.ttf")).deriveFont(15.0f);
		}catch(FontFormatException | IOException e){
			e.printStackTrace();
		}
		SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	private Display() throws HeadlessException{
		super("McCanny TimeTable " + VERSION);
		setName("2048");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.canvas = new JCanvas("TimeTable");
		this.canvas.getCanvasThread().setPrintNoticeInConsole(true);
		this.canvas.getCanvasThread().setFPS(30);
		this.manager = new CourseManager(canvas.getCanvasThread());
		JBasePanel               panel    = new JBasePanel();
		Layouter.GridBagLayouter layouter = new GridBagLayouter(panel);
		layouter.put(layouter.instanceOf(canvas, 0, 0).setWeight(100, 100).setFill(Fill.BOTH).setAnchor(Anchor.CENTER));
		this.setContentPane(panel);
	}
	
	private void construct(){
		this.manager.initializeTimeTable(new TimeTable(new Date(2019, 02.f, 10.f)));
		updateDimension();
		Course.loadCourse("MHF4U", 110, Color.ORANGE);
		Course.loadCourse("MDM4U", 110, Color.ORANGE);
		Course.loadCourse("ENG4U", 110, Color.ORANGE);
		for(int index = 0; index < 101; index++){
			Teacher.loadTeacher(String.valueOf(index), String.valueOf(index * index));
			Student.loadStudent(String.valueOf(index), String.valueOf(index * index * index * index));
		}
		Teacher.loadTeacher("000", "Patric");
		Student.loadStudent("111", "Homelet");
		Student.loadStudent("112", "Harry");
		Student.loadStudent("113", "Penny");
		Student.loadStudent("114", "Ethan");
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
		SwingUtilities.invokeLater(()->{
			StudentInfoDialog.showDialog(null);
			TeacherInfoDialog.showDialog(null);
			CourseInfoDialog.showDialog(null);
			PeriodInfoDialog.showDialog(null);
		});
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
			System.out.println("Exiting!");
		super.processWindowEvent(e);
	}
}
