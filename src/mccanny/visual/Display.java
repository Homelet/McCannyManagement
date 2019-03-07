package mccanny.visual;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.visual.JCanvas;
import mccanny.io.Builder;
import mccanny.io.TimeTableBuilder.TimeTableBuilder;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.course.manager.CourseManager;
import mccanny.management.course.manager.TimeTable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.dialog.*;
import mccanny.visual.infoCenter.InformationCenter;
import mccanny.visual.infoCenter.OneClickImageDialog;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Display extends JFrame{
	
	public static Display createDisplay(){
		display = new Display();
		display.construct();
		return display;
	}
	
	private void construct(){
		if(!Builder.loadCourses() | !Builder.loadStudents() | !Builder.loadTeachers())
			JOptionPane.showMessageDialog(this, "Error Loading Data Base!\nPlease Contact Administer!", "Error", JOptionPane.ERROR_MESSAGE, null);
		TimeTable timeTable = TimeTableBuilder.decode(Utility.join("data", "timetable.timetable"));
		this.manager.initializeTimeTable(timeTable);
//		this.manager.initializeTimeTable(new TimeTable("McCanny TimeTable", new Date(2019, 3.0, 1), new Date(2019, 3.0, 29)));
		updateDimension();
//		Course ESL_GLS1O         = Course.newCourse("ESL", 110, Color.RED);
//		Course ENG4U_3U          = Course.newCourse("ENG", 110, Color.BLUE);
//		Course ASM4M_3M          = Course.newCourse("ASM", 110, Color.GREEN);
//		Course SBI4U_3U          = Course.newCourse("SBI", 110, Color.CYAN);
//		Course SBI4U_SNC2D       = Course.newCourse("SBI,SNC", 110, Color.ORANGE);
//		Course CHC2D_GLC20_CLS1O = Course.newCourse("CHC,GLC,CLS", 110, McCANNY_BLUE);
//		Course ART_PORTFOLIO     = Course.newCourse("Portfolio", 110, new Color(0xE06666));
//		Course SPH4U_SNC2D       = Course.newCourse("SPH,SNC", 110, new Color(0xFFE598));
//		Course MDM4U             = Course.newCourse("MDM4U", 110, new Color(0xA4C2F4));
//		Course IDC4U             = Course.newCourse("IDC4U", 110, new Color(0xC27BA0));
//		Course LKMDU             = Course.newCourse("LKMDU", 110, new Color(0xD9EAD3));
//		Course TDJ4M_3M          = Course.newCourse("TDJ", 110, new Color(0xCCCCCC));
//		Course AMV4M_3M          = Course.newCourse("AMV", 110, new Color(0xF4CCCC));
//		Course ENG4U_WRITING     = Course.newCourse("ENG4U", 110, new Color(0x562390));
//		Course MCR3U_MPM2D       = Course.newCourse("MCR,MPM", 110, new Color(0x231266));
//		Course SPH4U_3U          = Course.newCourse("SPH", 110, new Color(0x654321));
//		Course MHF4U_MCV4U       = Course.newCourse("MHF,MCV", 110, new Color(0x235511));
//		Course FRENCH            = Course.newCourse("French", 110, new Color(0xFFF2CC));
//		Course TEJ4M_3M_ICS3U_4U = Course.newCourse("TEJ,ICS", 110, new Color(0xB6D7A8));
//		Course MPM2D_MCR3U       = Course.newCourse("MPM,MCR", 110, new Color(0xe04056));
//		Course ENG4U_EPS3O       = Course.newCourse("ENG,EPS", 110, new Color(0x09f123));
//		Course IELTS_COPE        = Course.newCourse("IELTS", 110, new Color(0x812e41));
//		Course MCR3U_AMC8        = Course.newCourse("MCR,AMC", 110, new Color(0x3412c4));
//		Course MUSIC_PORTFOLIO   = Course.newCourse("Portfolios", 110, new Color(0x124599));
//		Course MODEL_3D          = Course.newCourse("3D Model", 110, new Color(0xc12496e));
//		Date   date              = Date.today();
//		String email             = "homeltwei@gmail.com";
//		//
//		Teacher Alice    = Teacher.newTeacher("000", "Alice", date, email);
//		Teacher Amanda   = Teacher.newTeacher("001", "Amanda", date, email);
//		Teacher Naiyelli = Teacher.newTeacher("002", "Naiyelli", date, email);
//		Teacher Dr_Golam = Teacher.newTeacher("003", "Dr.Golam", date, email);
//		Teacher Liang    = Teacher.newTeacher("004", "Liang", date, email);
//		Teacher Alberto  = Teacher.newTeacher("005", "Alberto", date, email);
//		Teacher Eleanor  = Teacher.newTeacher("006", "Eleanor", date, email);
//		Teacher Elaine   = Teacher.newTeacher("007", "Elaine", date, email);
//		Teacher Michelle = Teacher.newTeacher("008", "Michelle", date, email);
//		Teacher Stein    = Teacher.newTeacher("009", "Stein", date, email);
//		Teacher Wenxiang = Teacher.newTeacher("010", "Wenxiang", date, email);
//		Teacher Jack     = Teacher.newTeacher("011", "Jack", date, email);
//		Teacher Patrick  = Teacher.newTeacher("012", "Patrick", date, email);
//		Teacher Dr_Ali   = Teacher.newTeacher("013", "Dr.Ali", date, email);
//		Teacher Bill     = Teacher.newTeacher("014", "Bill", date, email);
//		Teacher Irene    = Teacher.newTeacher("015", "Irene", date, email);
//		//
//		Student Owen         = Student.newStudent("111", "Owen", date, email);
//		Student Kevin        = Student.newStudent("112", "Kevin", date, email);
//		Student Cytheria     = Student.newStudent("113", "Cytheria", date, email);
//		Student Mandy        = Student.newStudent("114", "Mandy", date, email);
//		Student Emma         = Student.newStudent("115", "Emma", date, email);
//		Student Yuki         = Student.newStudent("116", "Yuki", date, email);
//		Student Gloria       = Student.newStudent("117", "Gloria", date, email);
//		Student Harry        = Student.newStudent("118", "Harry", date, email);
//		Student Ethan        = Student.newStudent("119", "Ethan", date, email);
//		Student Andy         = Student.newStudent("121", "Andy", date, email);
//		Student Oliver       = Student.newStudent("122", "Oliver", date, email);
//		Student Steve        = Student.newStudent("123", "Steve", date, email);
//		Student William      = Student.newStudent("124", "William", date, email);
//		Student Olivia       = Student.newStudent("125", "Olivia", date, email);
//		Student Rachel       = Student.newStudent("126", "Rachel", date, email);
//		Student Chloe        = Student.newStudent("127", "Chloe", date, email);
//		Student Shizhuo      = Student.newStudent("128", "Shizhuo", date, email);
//		Student Finnick      = Student.newStudent("129", "Finnick", date, email);
//		Student Lily         = Student.newStudent("130", "Lily", date, email);
//		Student Felix        = Student.newStudent("131", "Felix", date, email);
//		Student Gavin        = Student.newStudent("132", "Gavin", date, email);
//		Student Kailey       = Student.newStudent("133", "Kailey", date, email);
//		Student Karen        = Student.newStudent("134", "Karen", date, email);
//		Student Yi           = Student.newStudent("135", "Yi", date, email);
//		Student Grantarie    = Student.newStudent("136", "Grantarie", date, email);
//		Student Tony         = Student.newStudent("137", "Tony", date, email);
//		Student Eric         = Student.newStudent("138", "Eric", date, email);
//		Student Jack_student = Student.newStudent("139", "Jack", date, email);
//		Student Homelet      = Student.newStudent("140", "Homelet", date, email);
//		Student Lanhui       = Student.newStudent("141", "Lanhui", date, email);
//		Student Jo           = Student.newStudent("142", "Jo", date, email);
//		Student Brendon      = Student.newStudent("143", "Brendon", date, email);
//		Student Rain         = Student.newStudent("144", "Rain", date, email);
//		Student Ryan         = Student.newStudent("145", "Ryan", date, email);
//		Student Joe          = Student.newStudent("146", "Joe", date, email);
//		Student Joel         = Student.newStudent("147", "Joel", date, email);
//		Student Kris         = Student.newStudent("148", "Kris", date, email);
//		Student Vincent      = Student.newStudent("149", "Vincent", date, email);
//		Student Toney        = Student.newStudent("150", "Toney", date, email);
//		Student Bruce        = Student.newStudent("151", "Bruce", date, email);
//		Student Doris        = Student.newStudent("152", "Doris", date, email);
//		Student Bob          = Student.newStudent("153", "Bob", date, email);
//		Student Ken          = Student.newStudent("154", "Ken", date, email);
//		Student Hank         = Student.newStudent("155", "Hank", date, email);
//		Student Oscar        = Student.newStudent("156", "Oscar", date, email);
//		Student Alex         = Student.newStudent("157", "Alex", date, email);
//		Student Alimee       = Student.newStudent("158", "Alimee", date, email);
//		Student Krystal      = Student.newStudent("159", "Krystal", date, email);
//		Student Lina         = Student.newStudent("160", "Lina", date, email);
//		Student Penny        = Student.newStudent("161", "Penny", date, email);
//		Student Harley       = Student.newStudent("162", "Harley", date, email);
//		Student Jason        = Student.newStudent("163", "Jason", date, email);
//		Student Duncan       = Student.newStudent("164", "Duncan", date, email);
//		Student Zhifei       = Student.newStudent("165", "Zhifei", date, email);
//		Student Rosalia      = Student.newStudent("166", "Rosalia", date, email);
//		Student Kaylina      = Student.newStudent("167", "Kaylina", date, email);
//		create(ESL_GLS1O, Weekday.MONDAY, 9.25, 12.25, new Teacher[]{ Alice }, Owen, Kevin, Cytheria, Mandy, Emma, Yuki);
//		create(ENG4U_3U, Weekday.MONDAY, 9.25, 12.25, new Teacher[]{ Amanda }, Gloria, Harry, Ethan, Andy, Oliver, Steve, William, Olivia, Rachel);
//		create(ASM4M_3M, Weekday.MONDAY, 12.75, 15.75, new Teacher[]{ Naiyelli }, Chloe, Shizhuo, Gloria, Finnick, Olivia, Rachel, Cytheria, Mandy, Lily);
//		create(SBI4U_3U, Weekday.MONDAY, 12.75, 15.75, new Teacher[]{ Dr_Golam }, Harry, Emma);
//		create(CHC2D_GLC20_CLS1O, Weekday.MONDAY, 12.75, 15.75, new Teacher[]{ Alice }, Felix, Gavin, Owen, Kevin, Yuki);
//		create(ART_PORTFOLIO, Weekday.MONDAY, 16.00, 18.00, new Teacher[]{ Liang }, Kailey, Karen, Emma, Yi);
//		create(AMV4M_3M, Weekday.MONDAY, 16.00, 19.00, new Teacher[]{ Alberto }, Grantarie, Andy, Tony, Eric, Jack_student);
//		create(SPH4U_SNC2D, Weekday.MONDAY, 16.00, 19.00, new Teacher[]{ Dr_Golam }, Homelet, Harry, Lanhui, Owen, Kevin);
//		create(MDM4U, Weekday.MONDAY, 17.50, 19.00, new Teacher[]{ Eleanor }, Homelet, Gloria, Jo, Brendon, William);
//		create(ESL_GLS1O, Weekday.TUESDAY, 9.25, 12.25, new Teacher[]{ Alice }, Owen, Kevin, Cytheria, Mandy, Emma, Yuki);
//		create(IDC4U, Weekday.TUESDAY, 9.25, 12.25, new Teacher[]{ Elaine }, Felix, Finnick, Steve, Olivia, Rachel);
//		create(ASM4M_3M, Weekday.TUESDAY, 12.75, 15.75, new Teacher[]{ Naiyelli }, Chloe, Shizhuo, Gloria, Finnick, Olivia, Rachel, Cytheria, Mandy, Lily);
//		create(CHC2D_GLC20_CLS1O, Weekday.TUESDAY, 12.75, 15.75, new Teacher[]{ Alice }, Felix, Gavin, Owen, Kevin, Yuki);
//		create(CHC2D_GLC20_CLS1O, Weekday.WEDNESDAY, 9.25, 12.25, new Teacher[]{ Alice }, Felix, Gavin, Owen, Kevin, Yuki);
//		create(LKMDU, Weekday.WEDNESDAY, 9.25, 12.25, new Teacher[]{ Michelle }, Shizhuo, Rachel);
//		create(ENG4U_3U, Weekday.WEDNESDAY, 12.75, 15.75, new Teacher[]{ Amanda }, Gloria, Harry, Ethan, Andy, Oliver, Steve, William, Olivia, Rachel);
//		create(TDJ4M_3M, Weekday.WEDNESDAY, 14.0, 18.0, new Teacher[]{ Stein, Wenxiang }, Felix, Gloria, Rachel, Kaylina, Emma, Cytheria, Mandy, Chloe, Owen, Kevin);
//		create(ART_PORTFOLIO, Weekday.WEDNESDAY, 16.00, 18.00, new Teacher[]{ Jack }, Kailey, Karen, Emma, Yi);
//		create(AMV4M_3M, Weekday.WEDNESDAY, 16.00, 19.00, new Teacher[]{ Alberto }, Grantarie, Andy, Tony, Eric, Jack_student);
//		create(MHF4U_MCV4U, Weekday.WEDNESDAY, 16.00, 19.00, new Teacher[]{ Patrick }, Rain, Ryan, Gloria, Joe, William, Gavin, Kris, Chloe, Steve, Rosalia, Joel, Vincent);
//		create(MCR3U_MPM2D, Weekday.THURSDAY, 9.25, 12.25, new Teacher[]{ Patrick }, Mandy, Cytheria, Owen, Kevin, Emma);
//		create(LKMDU, Weekday.THURSDAY, 9.25, 12.25, new Teacher[]{ Michelle });
//		create(ASM4M_3M, Weekday.THURSDAY, 12.75, 15.75, new Teacher[]{ Naiyelli }, Chloe, Shizhuo, Gloria, Finnick, Oliver, Rachel, Cytheria, Mandy, Lily);
//		create(SBI4U_SNC2D, Weekday.THURSDAY, 12.75, 15.25, new Teacher[]{ Naiyelli }, Harry, Owen, Kevin);
//		create(LKMDU, Weekday.THURSDAY, 15.75, 19.0, new Teacher[]{ Michelle }, Toney, Bruce);
//		create(SPH4U_3U, Weekday.THURSDAY, 16.00, 19.0, new Teacher[]{ Dr_Golam }, Homelet, Harry, Lanhui, Emma);
//		create(MHF4U_MCV4U, Weekday.THURSDAY, 16.00, 19.00, new Teacher[]{ Patrick }, Rain, Ryan, Gloria, Joe, William, Gavin, Kris, Chloe, Steve, Rosalia, Joel, Vincent);
//		create(ESL_GLS1O, Weekday.FRIDAY, 9.25, 12.25, new Teacher[]{ Alice }, Owen, Kevin, Cytheria, Mandy, Emma, Yuki);
//		create(ENG4U_3U, Weekday.FRIDAY, 9.25, 12.25, new Teacher[]{ Amanda }, Gloria, Harry, Ethan, Andy, Oliver, Steve, William, Olivia, Rachel);
//		create(IDC4U, Weekday.FRIDAY, 12.75, 15.25, new Teacher[]{ Elaine }, Felix, Finnick, Steve, Olivia, Rachel);
//		create(MCR3U_MPM2D, Weekday.FRIDAY, 12.75, 15.25, new Teacher[]{ Patrick }, Mandy, Cytheria, Owen, Kevin, Emma);
//		create(SPH4U_3U, Weekday.FRIDAY, 15.5, 19.00, new Teacher[]{ Dr_Ali }, Homelet, Harry, Lanhui, Emma);
//		create(ART_PORTFOLIO, Weekday.SATURDAY, 13.00, 18.00, new Teacher[]{ Liang }, Hank, Oscar, Kailey, Karen, Yi, Lily);
//		create(FRENCH, Weekday.SATURDAY, 13.00, 15.50, new Teacher[]{ Amanda }, Alex, Alimee, Oscar);
//		create(MHF4U_MCV4U, Weekday.SATURDAY, 12.75, 15.75, new Teacher[]{ Patrick }, Rain, Ryan, Gloria, Joe, William, Gavin, Kris, Chloe, Steve, Rosalia, Joel, Vincent);
//		create(TDJ4M_3M, Weekday.SATURDAY, 13.0, 18.00, new Teacher[]{ Stein, Wenxiang }, Felix, Gloria, Rachel, Kaylina, Emma, Cytheria, Mandy, Chloe, Owen, Kevin);
//		create(MDM4U, Weekday.SATURDAY, 16.0, 18.00, new Teacher[]{ Eleanor }, Homelet, Gloria, Jo, Brendon, William);
//		create(ENG4U_EPS3O, Weekday.SATURDAY, 16.0, 18.00, new Teacher[]{ Amanda }, Doris, Bob, Ken, Jo, Krystal, Lina);
//		create(TEJ4M_3M_ICS3U_4U, Weekday.SATURDAY, 16.0, 18.00, new Teacher[]{ Bill }, Homelet, Oliver, Penny, Ethan, Joel);
//		create(MDM4U, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Eleanor }, Homelet, Gloria, Jo, Brendon, William);
//		create(SBI4U_SNC2D, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Dr_Golam }, Harry, Owen, Kevin);
//		create(IELTS_COPE, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Irene }, Harley, Jason, Finnick);
//		create(LKMDU, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Michelle }, Toney, Bruce, Doris);
//		create(LKMDU, Weekday.SUNDAY, 12.75, 15.75, new Teacher[]{ Michelle }, Toney, Bruce, Doris);
//		create(MCR3U_AMC8, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Patrick }, Mandy, Duncan);
//		create(MUSIC_PORTFOLIO, Weekday.SUNDAY, 10.50, 12.50, new Teacher[]{ Alberto }, Zhifei);
//		create(ART_PORTFOLIO, Weekday.SUNDAY, 13.00, 15.00, new Teacher[]{ Elaine }, Hank, Oscar, Lily, Emma);
//		create(MODEL_3D, Weekday.SUNDAY, 15.00, 18.00, new Teacher[]{ Wenxiang }, Hank, Oscar, Lily, Emma);
//		create(ENG4U_WRITING, Weekday.SUNDAY, 12.75, 15.75, new Teacher[]{ Amanda }, Doris, Bob, Ken, Jo, Harley, Jason);
//		create(MPM2D_MCR3U, Weekday.SUNDAY, 12.75, 15.75, new Teacher[]{ Patrick }, Cytheria, Owen, Kevin, Emma, Mandy);
//		create(AMV4M_3M, Weekday.SUNDAY, 12.75, 15.75, new Teacher[]{ Alberto }, Grantarie, Andy, Toney, Eric, Jack_student);
//		create(TEJ4M_3M_ICS3U_4U, Weekday.SUNDAY, 12.75, 15.75, new Teacher[]{ Bill }, Homelet, Oliver, Penny, Ethan, Joel);
//		create(TEJ4M_3M_ICS3U_4U, Weekday.SUNDAY, 16.00, 18.00, new Teacher[]{ Bill }, Homelet, Oliver, Penny, Ethan, Joel);
		manager.printError();
	}
	
	private void create(Course course, Weekday weekday, double start, double end, Teacher[] teacher, Student... student){
		CoursePeriod p = new CoursePeriod(course, 1, weekday, start, end);
		p.replaceTeacher(Arrays.asList(teacher));
		p.replaceStudent(Arrays.asList(student));
		manager.add(p);
	}
	
	/**
	 * 39 for windows
	 * 21 for mac
	 */
	public void updateDimension(){
		DISPLAY_DIMENSION.setSize(CourseManager.TIMETABLE_DI.width + CourseManager.LEFT_INSET + CourseManager.RIGHT_INSET, CourseManager.TIMETABLE_DI.height + CourseManager.BOTTOM_INSET + CourseManager.TOP_INSET + 39 + 23);
		this.setSize(DISPLAY_DIMENSION);
		this.revalidate();
	}
	
	public static Display getInstance(){
		return display;
	}
	
	public static final Color         McCANNY_BLUE      = new Color(0x00205E);
	public static final String        VERSION           = "V1.0 BETA";
	public static final Dimension     DISPLAY_DIMENSION = new Dimension();
	public static       Font          CLEAR_SANS_BOLD;
	public static       Dimension     SCREEN_DIMENSION;
	public static       boolean       FORMAT_24         = false;
	private static      Display       display;
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
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		this.canvas = new JCanvas("TimeTable");
		this.canvas.getCanvasThread().setPrintNoticeInConsole(false);
		this.canvas.getCanvasThread().setFPS(30);
		this.manager = new CourseManager(canvas.getCanvasThread());
		JBasePanel panel = new JBasePanel();
		createMenu();
		Layouter.GridBagLayouter layouter = new GridBagLayouter(panel);
		layouter.put(layouter.instanceOf(canvas, 0, 0).setWeight(100, 100).setFill(Fill.BOTH).setAnchor(Anchor.CENTER));
		this.setContentPane(panel);
	}
	
	private void createMenu(){
		JMenuBar menuBar = new JMenuBar();
		// File
		JMenu     file         = new JMenu("File");
		JMenuItem newTimeTable = new JMenuItem("New");
		JMenuItem openRecent   = new JMenuItem("Recent");
		JMenuItem saveAs       = new JMenuItem("Save as...");
		JMenuItem save         = new JMenuItem("Save");
		JMenuItem setting      = new JMenuItem("Setting");
		JMenuItem exit         = new JMenuItem("Exit");
		file.add(newTimeTable);
		file.add(openRecent);
		file.addSeparator();
		file.add(save);
		file.add(saveAs);
		file.addSeparator();
		file.add(setting);
		file.addSeparator();
		file.add(exit);
		newTimeTable.addActionListener(action->{
		});
		save.addActionListener(action->{
			manager.timeTable().save();
		});
		saveAs.addActionListener(action->{
			manager.timeTable().saveAs();
		});
		openRecent.addActionListener(action->{
		});
		setting.addActionListener(action->{
		});
		exit.addActionListener(action->{
		});
		// Edit
		JMenu             info            = new JMenu("Info");
		JCheckBoxMenuItem massProduction  = new JCheckBoxMenuItem("Mass Production Mode", false);
		JMenuItem         newStudent      = new JMenuItem("New Student");
		JMenuItem         newTeacher      = new JMenuItem("New Teacher");
		JMenuItem         newCourse       = new JMenuItem("New Course");
		JMenuItem         newCoursePeriod = new JMenuItem("New CoursePeriod");
		JMenuItem         Info            = new JMenuItem("Info Center");
		info.add(massProduction);
		info.addSeparator();
		info.add(newStudent);
		info.add(newTeacher);
		info.add(newCourse);
		info.addSeparator();
		info.add(newCoursePeriod);
		info.addSeparator();
		info.add(Info);
		newStudent.addActionListener(action->{
			do{
				if(StudentInfoDialog.showInfoDialog(null) == null)
					break;
			}while(massProduction.isSelected());
		});
		newTeacher.addActionListener(action->{
			do{
				if(TeacherInfoDialog.showInfoDialog(null) == null)
					break;
			}while(massProduction.isSelected());
		});
		newCourse.addActionListener(action->{
			do{
				if(CourseInfoDialog.showInfoDialog(null) == null)
					break;
			}while(massProduction.isSelected());
		});
		newCoursePeriod.addActionListener(action->{
			do{
				if(PeriodInfoDialog.showInfoDialog(null) == null)
					break;
			}while(massProduction.isSelected());
		});
		Info.addActionListener(action->{
			InformationCenter.showInformationCenter();
		});
		// Filter
		JMenu     filter      = new JMenu("Filter");
		JMenuItem applyFilter = new JMenuItem("Apply Filter");
		applyFilter.addActionListener(action->{
			FilterDialog.showInfoDialog();
		});
		filter.add(applyFilter);
		// Utility
		JMenu     utility           = new JMenu("Utility");
		JMenuItem oneClickGenerator = new JMenuItem("One Click Generator");
		oneClickGenerator.addActionListener(action->{
			OneClickImageDialog.showOneClickDialog();
		});
		utility.add(oneClickGenerator);
		//
		menuBar.add(file);
		menuBar.add(info);
		menuBar.add(filter);
		menuBar.add(utility);
		this.setJMenuBar(menuBar);
	}
	
	public JCanvas canvas(){
		return canvas;
	}
	
	public CourseManager manager(){
		return manager;
	}
	
	public void showDisplay(){
		this.setLocation(Utility.frameVertex(new Rectangle(SCREEN_DIMENSION), this.getBounds()));
		this.setVisible(true);
		this.canvas.startRendering();
		SwingUtilities.invokeLater(()->{
			TimeTableInfoDialog.showInfoDialog();
		});
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e){
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			if(!onExit())
				return;
		}
		super.processWindowEvent(e);
	}
	
	private boolean onExit(){
		if(Builder.writeStudents() & Builder.writeTeachers() & Builder.writeCourses() & manager.timeTable().close()){
			System.out.println("Exiting!");
			return true;
		}else{
			JOptionPane.showMessageDialog(this, "Error shutting Down!\nPlease Contact Administer!", "Error", JOptionPane.ERROR_MESSAGE, null);
			System.err.println("Error shutting Down!");
			return false;
		}
	}
}
