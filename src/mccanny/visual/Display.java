package mccanny.visual;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.visual.JCanvas;
import mccanny.management.course.Course;
import mccanny.management.course.manager.CourseManager;
import mccanny.management.course.manager.OneClickImageRenderer;
import mccanny.management.course.manager.TimeTable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.util.Utility;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Display extends JFrame{
	
	public static Display createDisplay(){
		display = new Display();
		display.construct();
		return display;
	}
	
	private void construct(){
		this.manager.initializeTimeTable(new TimeTable(new Date(2019, 02.f, 10.f)));
		updateDimension();
		Course.newCourse("MHF4U", 110, Color.ORANGE);
		Course.newCourse("MDM4U", 110, Color.RED);
		Course.newCourse("ENG4U", 110, Color.CYAN);
		Teacher.newTeacher("000", "Patric");
		Student.newStudent("111", "Homelet");
		Student.newStudent("112", "Harry");
		Student.newStudent("113", "Penny");
		Student.newStudent("114", "Ethan");
	}
	
	/**
	 * 39 for windows
	 * 21 for mac
	 */
	public void updateDimension(){
		DISPLAY_DIMENSION.setSize(CourseManager.TIMETABLE_DI.width + CourseManager.LEFT_INSET + CourseManager.RIGHT_INSET, CourseManager.TIMETABLE_DI.height + CourseManager.BOTTOM_INSET + CourseManager.TOP_INSET + 39);
		this.setSize(DISPLAY_DIMENSION);
		this.revalidate();
	}
	
	public static Display getInstance(){
		return display;
	}
	
	public static final Color                 McCANNY_BLUE                = new Color(0x00205E);
	public static final String                VERSION                     = "V0.1";
	public static final Dimension             DISPLAY_DIMENSION           = new Dimension();
	public static       String                TIME_TABLE_OUTPUT_DIRECTORY = "/Users/homeletwei/Workspaces/IntelliJ IDEA/McCannyManagement/timeTable";
	public static       Font                  CLEAR_SANS_BOLD;
	public static       Dimension             SCREEN_DIMENSION;
	public static       boolean               FORMAT_24                   = false;
	private static      Display               display;
	private final       JCanvas               canvas;
	private final       CourseManager         manager;
	private final       OneClickImageRenderer renderer;
	
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
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.canvas = new JCanvas("TimeTable");
		this.canvas.getCanvasThread().setPrintNoticeInConsole(false);
		this.canvas.getCanvasThread().setFPS(30);
		this.manager = new CourseManager(canvas.getCanvasThread());
		this.renderer = new OneClickImageRenderer();
		JBasePanel               panel    = new JBasePanel();
		Layouter.GridBagLayouter layouter = new GridBagLayouter(panel);
		layouter.put(layouter.instanceOf(canvas, 0, 0).setWeight(100, 100).setFill(Fill.BOTH).setAnchor(Anchor.CENTER));
		this.setContentPane(panel);
	}
	
	public JCanvas canvas(){
		return canvas;
	}
	
	public CourseManager manager(){
		return manager;
	}
	
	public OneClickImageRenderer renderer(){
		return renderer;
	}
	
	public void showDisplay(){
		this.setLocation(Utility.frameVertex(new Rectangle(SCREEN_DIMENSION), this.getBounds()));
		this.setVisible(true);
		this.canvas.startRendering();
		SwingUtilities.invokeLater(()->{
//			StudentInfoDialog.showDialog(null);
//			TeacherInfoDialog.showDialog(null);
//			CourseInfoDialog.showDialog(null);
//			FilterDialog.showInfoDialog();
//			SelectionDialog.showCourseDialog(this, Collections.emptyList(), Course.courses(), null);
//			InformationCenter.showInformationCenter();
		});
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING)
			System.out.println("Exiting!");
		super.processWindowEvent(e);
	}
}
