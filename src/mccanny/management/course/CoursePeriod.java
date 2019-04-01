package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.management.course.manager.CourseManager;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.ImageLocateable;
import mccanny.util.ImageRenderable;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;
import mccanny.visual.dialog.PeriodInfoDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CoursePeriod extends ActionsManager implements Comparable<CoursePeriod>, LocatableRender, ImageRenderable, ImageLocateable{
	
	public static final  double                                 START_AT       = 8.0;
	public static final  double                                 END_AT         = 20.0;
	public static final  int                                    HEIGHT         = 25;
	public static final  int                                    WIDTH_PER_HOUR = 120;
	private static final HashMap<Double, RenderThresholdConfig> configs        = new HashMap<>();
	private final        Dimension                              size;
	private final        Point                                  vertex;
	// render
	private final        StringDrawer                           courseCodeDrawer;
	private final        StringDrawer                           classroomNumberDrawer;
	private final        StringDrawer                           periodDrawer;
	private final        StringDrawer                           teacherDrawer;
	private final        StringDrawer                           studentDrawer;
	// data
	private              Course                                 course;
	private              int                                    classroomNumber;
	private              double                                 start;
	private              double                                 end;
	private              double                                 length;
	private              Weekday                                weekday;
	private              int                                    lineIndex;
	private              RenderThresholdConfig                  config;
	private              boolean                                activate;
	// participant
	private              ArrayList<Teacher>                     teachers;
	private              ArrayList<Student>                     students;
	
	static{
		configs.put(0.5, new RenderThresholdConfig(false, false, 0, 0));
		configs.put(1.0, new RenderThresholdConfig(true, false, 2, 0));
		configs.put(1.5, new RenderThresholdConfig(true, false, 5, 0));
		configs.put(2.0, new RenderThresholdConfig(true, false, 2, 5));
		configs.put(2.5, new RenderThresholdConfig(true, true, 2, 6));
		configs.put(3.0, new RenderThresholdConfig(true, true, 2, 7));
		configs.put(3.5, new RenderThresholdConfig(true, true, 2, 9));
		configs.put(4.0, new RenderThresholdConfig(true, true, 3, 19));
		configs.put(4.5, new RenderThresholdConfig(true, true, 3, 12));
		configs.put(5.0, new RenderThresholdConfig(true, true, 3, 13));
		configs.put(5.5, new RenderThresholdConfig(true, true, 4, 14));
		configs.put(6.0, new RenderThresholdConfig(true, true, 5, 15));
		configs.put(6.5, new RenderThresholdConfig(true, true, 6, 17));
		configs.put(7.0, new RenderThresholdConfig(true, true, 6, 19));
		configs.put(7.5, new RenderThresholdConfig(true, true, 7, 20));
		configs.put(8.0, new RenderThresholdConfig(true, true, 8, 21));
	}
	
	public CoursePeriod(Course course, int classroomNumber, Weekday weekday, double start, double end){
		this.courseCodeDrawer = new StringDrawer();
		this.courseCodeDrawer.setFont(Display.ARIAL_BOLD.deriveFont(15.0f));
		this.courseCodeDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.courseCodeDrawer.setAlign(Alignment.CENTER);
		this.courseCodeDrawer.setTextAlign(Alignment.TOP);
		this.courseCodeDrawer.setColor(Color.WHITE);
		//
		this.classroomNumberDrawer = new StringDrawer();
		this.classroomNumberDrawer.setFont(Display.ARIAL_BOLD.deriveFont(10.0f));
		this.classroomNumberDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.classroomNumberDrawer.setAlign(Alignment.CENTER);
		this.classroomNumberDrawer.setTextAlign(Alignment.LEFT);
		this.classroomNumberDrawer.setInsetsLeft(8);
		this.classroomNumberDrawer.setColor(Color.WHITE);
		//
		this.periodDrawer = new StringDrawer();
		this.teacherDrawer = new StringDrawer();
		this.studentDrawer = new StringDrawer();
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.teacherDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.studentDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.periodDrawer.setLinePolicy(LinePolicy.BREAK_WHERE_NECESSARY);
		this.teacherDrawer.setLinePolicy(LinePolicy.BREAK_WHERE_NECESSARY);
		this.studentDrawer.setLinePolicy(LinePolicy.BREAK_WHERE_NECESSARY);
//		this.periodDrawer.setParagraphSpacing(-8);
//		this.teacherDrawer.setParagraphSpacing(-5);
//		this.studentDrawer.setParagraphSpacing(-5);
//		this.periodDrawer.setInsetsBottom(15);
//		this.teacherDrawer.setInsetsBottom(15);
//		this.periodDrawer.setAlign(Alignment.CENTER);
//		this.teacherDrawer.setAlign(Alignment.BOTTOM);
//		this.studentDrawer.setAlign(Alignment.CENTER);
//		this.periodDrawer.setTextAlign(Alignment.TOP);
//		this.teacherDrawer.setTextAlign(Alignment.TOP);
//		this.studentDrawer.setTextAlign(Alignment.TOP);
//		this.classroomNumberDrawer.setDrawFrameBorder(true);
//		this.classroomNumberDrawer.setDrawTextFrameBorder(true);
//		this.periodDrawer.setDrawFrameBorder(true);
//		this.periodDrawer.setDrawTextFrameBorder(true);
//		this.courseCodeDrawer.setDrawFrameBorder(true);
//		this.courseCodeDrawer.setDrawTextFrameBorder(true);
//		this.teacherDrawer.setDrawFrameBorder(true);
//		this.teacherDrawer.setDrawTextFrameBorder(true);
//		this.studentDrawer.setDrawFrameBorder(true);
//		this.studentDrawer.setDrawTextFrameBorder(true);
		initCourse(course);
		classroom(classroomNumber);
		this.teachers = new ArrayList<>();
		this.students = new ArrayList<>();
		this.size = new Dimension();
		this.vertex = new Point();
		this.activate = false;
		initPeriod(weekday, start, end);
	}
	
	private void initCourse(Course course){
		this.course = course;
		this.courseCodeDrawer.initializeContents(course.courseID());
	}
	
	public void course(Course course){
		if(!this.course.equals(course)){
			this.course = course;
			syncCourseCodeAppr();
			Display.getInstance().manager().applyFilter();
		}
	}
	
	public void classroom(int classroomNumber){
		this.classroomNumber = classroomNumber;
		syncClassroomAppr();
	}
	
	public void syncClassroomAppr(){
		this.classroomNumberDrawer.initializeContents(classroomInfo());
	}
	
	public String classroomInfo(){
		return this.classroomNumber == 0 ? "OC" : String.valueOf(classroomNumber);
	}
	
	private void initPeriod(Weekday weekday, double start, double end){
		if(start >= 24 || start < 0)
			throw new IllegalArgumentException("Illegal Course Start time");
		if(end >= 24 || end < 0)
			throw new IllegalArgumentException("Illegal Course End time");
		if(end < start)
			throw new IllegalArgumentException("Period End before Start");
		this.start = start;
		this.end = end;
		this.weekday = weekday;
		this.length = end - start;
		pullConfig();
		this.size.setSize((int) (CoursePeriod.WIDTH_PER_HOUR * length), CoursePeriod.HEIGHT);
//		this.periodDrawer.initializeContents(Utility.time(start, Display.FORMAT_24), "~", Utility.time(end, Display.FORMAT_24));
	}
	
	private void pullConfig(){
		syncTeacherApper();
		syncStudentApper();
	}
	
	public void syncCourseCodeAppr(){
		this.courseCodeDrawer.initializeContents(course.courseID() + " (" + Utility.join(", ", teachers) + ")");
	}
	
	public void syncTeacherApper(){
		syncCourseCodeAppr();
	}
	
	public void syncStudentApper(){
	}
	
	/**
	 * to test the order of the periodFlag
	 */
	@Override
	public int compareTo(CoursePeriod o){
		int result = Integer.compare(this.weekday.index(), o.weekday.index());
		if(result == 0){
			return comparePeriod(this, o);
		}else{
			return result;
		}
	}
	
	private static int comparePeriod(CoursePeriod p1, CoursePeriod p2){
		int result = Double.compare(p1.start, p2.start);
		if(result == 0){
			result = Double.compare(p1.end, p2.end);
			if(result == 0){
				result = Double.compare(p1.classroomNumber, p2.classroomNumber);
				if(result == 0){
					return p1.course.courseID().compareTo(p2.course.courseID());
				}else{
					return result;
				}
			}else{
				return result;
			}
		}else{
			return result;
		}
	}
	
	public void period(Weekday weekday, double start, double end){
		if(this.start == start && this.end == end && this.weekday == weekday)
			return;
		Weekday previous = this.weekday;
		initPeriod(weekday, start, end);
		Display.getInstance().manager().update(this, previous);
	}
	
	public int lineIndex(){
		return lineIndex;
	}
	
	public void lineIndex(int lineIndex){
		this.lineIndex = lineIndex;
	}
	
	public void updateLocation(){
		this.vertex.setLocation((int) (CourseManager.LEFT_INSET + CourseManager.FIXED_HEADER_WIDTH + (start - START_AT) * WIDTH_PER_HOUR), Display.getInstance().manager().renderOffset(weekday) + 5 + lineIndex * (HEIGHT + 5));
	}
	
	public Course course(){
		return course;
	}
	
	public double start(){
		return start;
	}
	
	public double end(){
		return end;
	}
	
	public int classroom(){
		return classroomNumber;
	}
	
	public Weekday weekday(){
		return weekday;
	}
	
	public void addTeacher(boolean sync, Teacher teacher){
		teachers.add(teacher);
		if(sync)
			syncTeacherApper();
	}
	
	public void replaceTeacher(Collection<Teacher> teacher){
		teachers.clear();
		teachers.addAll(teacher);
		Display.getInstance().manager().applyFilter();
		syncTeacherApper();
	}
	
	public void addStudent(boolean sync, Student student){
		students.add(student);
		if(sync)
			syncStudentApper();
	}
	
	public void replaceStudent(Collection<Student> student){
		students.clear();
		students.addAll(student);
		Display.getInstance().manager().applyFilter();
		syncStudentApper();
	}
	
	public String teacherInfo(){
		return Utility.join(",\n", teachers);
	}
	
	public String studentInfo(){
		return Utility.join(",\n", students);
	}
	
	public ArrayList<Teacher> teachers(){
		return teachers;
	}
	
	public ArrayList<Student> students(){
		return students;
	}
	
	public boolean activate(){
		// TODO
		return activate;
	}
	
	public void activate(boolean activate){
		this.activate = activate;
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
	public Point getVertex(int renderOffset, int lineIndex){
		return new Point((int) (CourseManager.LEFT_INSET + CourseManager.FIXED_HEADER_WIDTH + (start - START_AT) * WIDTH_PER_HOUR), renderOffset + 5 + lineIndex * (HEIGHT + 5));
	}
	
	@Override
	public void renderImage(Graphics2D g){
		regularRender(g);
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		regularRender(g);
	}
	
	private void compactRender(Graphics2D g){
	}
	
	/*
	 * if(isHovering()){
			g.setColor(course.color().darker());
		}else{
			g.setColor(course.color());
		}
		g.fill(GH.rRectangle(false, bounds, bounds.getHeight(), bounds.getHeight()));
		courseCodeDrawer.updateGraphics(g);
		courseCodeDrawer.setFrame(bounds);
		courseCodeDrawer.validate();
		classroomNumberDrawer.updateGraphics(g);
		classroomNumberDrawer.setFrame(bounds);
		classroomNumberDrawer.validate();
		int classNumberWidth = classroomNumberDrawer.approximateStringWidth();
		g.setColor(Color.BLACK);
		g.fill(GH.rRectangle(false, 0, 0, classNumberWidth + 15, bounds.height, bounds.getHeight(), bounds.getHeight()));
		courseCodeDrawer.draw();
		classroomNumberDrawer.draw();
	 */
	private synchronized void regularRender(Graphics2D g){
		Rectangle bounds = g.getClipBounds();
		classroomNumberDrawer.updateGraphics(g);
		courseCodeDrawer.updateGraphics(g);
		int       offset = classroomNumberDrawer.approximateStringWidth() + 15;
		Rectangle first  = new Rectangle(0, 0, offset, bounds.height);
		Rectangle second = new Rectangle(offset, 0, bounds.width - offset, bounds.height);
		classroomNumberDrawer.setFrame(first);
		courseCodeDrawer.setFrame(second);
		classroomNumberDrawer.validate();
		courseCodeDrawer.validate();
		g.setColor(course.color());
		g.fill(second);
		courseCodeDrawer.draw();
		g.setColor(Color.BLACK);
		g.fill(first);
		classroomNumberDrawer.draw();
	}
	
	@Override
	public boolean isTicking(){
		return activate();
	}
	
	@Override
	public boolean isRendering(){
		return activate();
	}
	
	/**
	 * use for passive synchronising
	 */
	public void syncAll(){
		this.courseCodeDrawer.initializeContents(course.courseID());
		this.periodDrawer.initializeContents(Utility.time(start, Display.FORMAT_24), "~", Utility.time(end, Display.FORMAT_24));
		syncClassroomAppr();
		syncStudentApper();
		syncTeacherApper();
	}
	
	@Override
	public void onMouseClick(MouseEvent e){
		if(Display.getInstance().locking() || !activate)
			return;
		if(e.getButton() == MouseEvent.BUTTON1){
			PeriodInfoDialog.showInfoDialog(this);
		}else if(e.getButton() == MouseEvent.BUTTON3){
			int result = JOptionPane.showConfirmDialog(Display.getInstance(), "Are you sure to delete this Course Period?\n" + this.toString(), "Delete Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			switch(result){
				case JOptionPane.CANCEL_OPTION:
					return;
				case JOptionPane.OK_OPTION:
			}
			Display.getInstance().manager().remove(this);
		}
	}
	
	@Override
	public void onMouseEnter(Point p){
		if(Display.getInstance().locking() || !activate)
			return;
		Display.getInstance().manager().ruler().highlight(start, end);
	}
	
	@Override
	public void onMouseExit(Point p){
		if(Display.getInstance().locking() || !activate)
			return;
		Display.getInstance().manager().ruler().discardHighlight();
	}
	
	@Override
	public String toString(){
		return course + ", Room " + classroomNumber +
				", " + weekday + " " + Utility.time(start, Display.FORMAT_24) + "~" + Utility.time(end, Display.FORMAT_24) + " (" + length() +
				"h), Teachers:[" + Utility.toString(teachers, 10) + "], Students:[" + Utility.toString(students, 10) + "]";
	}
	
	public double length(){
		return length;
	}
	
	// TODO FUTURE IMPLEMENTATION
	@Override
	public void onMousePress(MouseEvent e){}
	
	static class RenderThresholdConfig{
		
		final boolean roomNumberFlag;
		final boolean periodFlag;
		final int     maxTeacher;
		final int     maxStudent;
		
		RenderThresholdConfig(boolean roomNumberFlag, boolean periodFlag, int maxTeacher, int maxStudent){
			this.roomNumberFlag = roomNumberFlag;
			this.periodFlag = periodFlag;
			this.maxTeacher = maxTeacher;
			this.maxStudent = maxStudent;
		}
	}
}
