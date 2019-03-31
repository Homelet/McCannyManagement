package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.util.Utility;
import mccanny.visual.Display;

import java.awt.*;

public class CoursePeriodDropDown implements LocatableRender{
	
	private static final int          FIXED_WIDTH         = 220;
	private static final int          FIXED_HEADER_HEIGHT = 50;
	private final        Dimension    size;
	private final        Point        vertex;
	private final        StringDrawer coursePeriod;
	private final        StringDrawer courseDetail;
	private final        StringDrawer courseNameTitle;
	private final        StringDrawer courseName;
	private final        StringDrawer instructorTitle;
	private final        StringDrawer instructor;
	private final        StringDrawer studentsTitle;
	private final        StringDrawer students;
	
	public CoursePeriodDropDown(){
		this.vertex = new Point();
		this.size = new Dimension(FIXED_WIDTH, 0);
		this.coursePeriod = new StringDrawer();
		this.coursePeriod.setColor(Color.WHITE);
		this.coursePeriod.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.coursePeriod.setTextAlign(Alignment.LEFT);
		this.coursePeriod.setFont(Display.ARIAL_BOLD.deriveFont(20.0f).deriveFont(Font.BOLD));
		//
		this.courseDetail = new StringDrawer();
		this.courseDetail.setColor(Color.WHITE);
		this.courseDetail.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.courseDetail.setTextAlign(Alignment.LEFT);
		this.courseDetail.setFont(Display.ARIAL_BOLD.deriveFont(12.0f));
		this.courseDetail.setFont(Display.ARIAL_BOLD);
		//
		this.courseNameTitle = new StringDrawer("Course Name:");
		this.courseNameTitle.setColor(Color.BLACK);
		this.courseNameTitle.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.courseNameTitle.setTextAlign(Alignment.LEFT);
		this.courseNameTitle.setFont(Display.ARIAL_BOLD.deriveFont(15.0f).deriveFont(Font.BOLD));
		//
		this.courseName = new StringDrawer();
		this.courseName.setColor(Color.BLACK);
		this.courseName.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.courseName.setTextAlign(Alignment.LEFT);
		this.courseName.setFont(Display.ARIAL_BOLD.deriveFont(10.0f));
		//
		this.instructorTitle = new StringDrawer("Instructor:");
		this.instructorTitle.setColor(Color.BLACK);
		this.instructorTitle.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.instructorTitle.setTextAlign(Alignment.LEFT);
		this.instructorTitle.setFont(Display.ARIAL_BOLD.deriveFont(15.0f).deriveFont(Font.BOLD));
		//
		this.instructor = new StringDrawer();
		this.instructor.setColor(Color.BLACK);
		this.instructor.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.instructor.setTextAlign(Alignment.LEFT);
		this.instructor.setFont(Display.ARIAL_BOLD.deriveFont(10.0f));
		//
		this.studentsTitle = new StringDrawer("Students:");
		this.studentsTitle.setColor(Color.BLACK);
		this.studentsTitle.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.studentsTitle.setTextAlign(Alignment.LEFT);
		this.studentsTitle.setFont(Display.ARIAL_BOLD.deriveFont(15.0f));
		this.studentsTitle.setFont(Display.ARIAL_BOLD.deriveFont(15.0f).deriveFont(Font.BOLD));
		//
		this.students = new StringDrawer();
		this.students.setColor(Color.BLACK);
		this.students.setAlign(Alignment.KEEP_X_ON_LEFT);
		this.students.setTextAlign(Alignment.LEFT);
		this.students.setFont(Display.ARIAL_BOLD.deriveFont(10.0f));
		this.students.setFont(Display.ARIAL_BOLD.deriveFont(Font.BOLD));
	}
	
	public void init(CoursePeriod period){
		Point     p           = period.getVertex(null);
		Dimension s           = period.getSize();
		int       widthOffset = p.x + s.width + 10;
		int       widthFinal  = widthOffset + FIXED_WIDTH;
		if(widthFinal < Display.SCREEN_DIMENSION.width){
			// if not fit
			widthOffset = p.x - s.width - 10;
		}
		this.vertex.setLocation(widthOffset, p.y);
		this.coursePeriod.initializeContents(Utility.time(period.start(), Display.FORMAT_24) + " - " + Utility.time(period.end(), Display.FORMAT_24));
		this.courseDetail.initializeContents(period.classroomInfo());
		this.courseName.initializeContents(period.course().courseID());
		this.instructor.initializeContents(period.teacherInfo());
		this.students.initializeContents(period.studentInfo());
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return vertex;
	}
	
	@Override
	public void tick(){
	}
	
	@Override
	public void render(Graphics2D g){
	}
	
	@Override
	public boolean isTicking(){
		return true;
	}
	
	@Override
	public boolean isRendering(){
		return true;
	}
}
