package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.utils.Alignment;
import homelet.GH.utils.Border;
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
	
	public static final  double                                 START_AT        = 8.0;
	public static final  double                                 END_AT          = 20.0;
	public static final  int                                    WIDTH           = 50;
	public static final  int                                    HEIGHT_PER_HOUR = 50;
	private static final HashMap<Double, RenderThresholdConfig> configs         = new HashMap<>();
	private final        Dimension                              size;
	private final        Point                                  vertex;
	// render
	private final        StringDrawer                           classroomNumberDrawer;
	private final        StringDrawer                           periodDrawer;
	private final        StringDrawer                           courseCodeDrawer;
	private final        StringDrawer                           teacherDrawer;
	private final        StringDrawer                           studentDrawer;
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
		configs.put(2.5, new RenderThresholdConfig(true, true, 1, 6));
		configs.put(3.0, new RenderThresholdConfig(true, true, 1, 7));
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
		this.classroomNumberDrawer = new StringDrawer();
		this.periodDrawer = new StringDrawer();
		this.courseCodeDrawer = new StringDrawer();
		this.teacherDrawer = new StringDrawer();
		this.studentDrawer = new StringDrawer();
		this.classroomNumberDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.courseCodeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(13.0f));
		this.teacherDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.studentDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.classroomNumberDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.periodDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.courseCodeDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.teacherDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.studentDrawer.setLinePolicy(LinePolicy.NEVER_BREAK);
		this.periodDrawer.setParagraphSpacing(-8);
		this.teacherDrawer.setParagraphSpacing(-5);
		this.studentDrawer.setParagraphSpacing(-5);
		this.periodDrawer.setInsetsBottom(15);
		this.teacherDrawer.setInsetsBottom(15);
		this.classroomNumberDrawer.setAlign(Alignment.TOP);
		this.periodDrawer.setAlign(Alignment.CENTER);
		this.courseCodeDrawer.setAlign(Alignment.BOTTOM);
		this.teacherDrawer.setAlign(Alignment.BOTTOM);
		this.studentDrawer.setAlign(Alignment.CENTER);
		this.classroomNumberDrawer.setTextAlign(Alignment.TOP);
		this.periodDrawer.setTextAlign(Alignment.TOP);
		this.courseCodeDrawer.setTextAlign(Alignment.TOP);
		this.teacherDrawer.setTextAlign(Alignment.TOP);
		this.studentDrawer.setTextAlign(Alignment.TOP);
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
		course(course);
		classroom(classroomNumber);
		this.teachers = new ArrayList<>();
		this.students = new ArrayList<>();
		this.size = new Dimension();
		this.vertex = new Point();
		this.activate = false;
		initPeriod(weekday, start, end);
	}
	
	public void course(Course course){
		if(this.course == null || !this.course.equals(course)){
			this.course = course;
			this.courseCodeDrawer.initializeContents(course.courseID());
		}
	}
	
	public void classroom(int classroomNumber){
		if(this.classroomNumber == classroomNumber)
			return;
		this.classroomNumber = classroomNumber;
		this.classroomNumberDrawer.initializeContents("Room " + classroomNumber);
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
		this.size.setSize(CoursePeriod.WIDTH, (int) (CoursePeriod.HEIGHT_PER_HOUR * length));
		this.periodDrawer.initializeContents(Utility.time(start, Display.FORMAT_24), "~", Utility.time(end, Display.FORMAT_24));
	}
	
	private void pullConfig(){
		double hour   = Math.floor(length);
		double minute = length - hour;
		if(hour <= 0){
			this.config = configs.get(0.5);
		}else if(hour >= 8){
			this.config = configs.get(8.0);
		}else if(minute < 0.5){
			this.config = configs.get(hour);
		}else{
			this.config = configs.get(hour + 0.5);
		}
		if(config.roomNumberFlag){
			courseCodeDrawer.setAlign(Alignment.BOTTOM);
		}else{
			courseCodeDrawer.setAlign(Alignment.CENTER);
		}
		syncTeacherApper();
		syncStudentApper();
	}
	
	public void syncTeacherApper(){
		teacherDrawer.clearAllContents();
		for(int i = 0; i < teachers.size(); i++){
			if(i < config.maxTeacher - 1){
				teacherDrawer.addContent(teachers.get(i).identity());
			}else if(i == config.maxTeacher){
				if(teachers.size() - i > 0){
					teacherDrawer.addContent("...");
				}else{
					teacherDrawer.addContent(teachers.get(i).identity());
				}
				break;
			}
		}
	}
	
	public void syncStudentApper(){
		studentDrawer.clearAllContents();
		for(int i = 0; i < students.size(); i++){
			if(i < config.maxStudent - 1){
				studentDrawer.addContent(students.get(i).identity());
			}else if(i == config.maxStudent){
				if(students.size() - i > 0){
					studentDrawer.addContent("...");
				}else{
					studentDrawer.addContent(students.get(i).identity());
				}
				break;
			}
		}
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
		if(p1.start > p2.start){
			return 1;
		}else if(p1.start < p2.start){
			return -1;
		}else{
			return Double.compare(p1.end, p2.end);
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
		this.vertex.setLocation(Display.getInstance().manager().renderOffset(weekday) + lineIndex * WIDTH, CourseManager.TOP_INSET + (start - START_AT) * HEIGHT_PER_HOUR);
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
	
	public void addTeacher(boolean sync, Collection<Teacher> teacher){
		teachers.addAll(teacher);
		if(sync)
			syncTeacherApper();
	}
	
	public void removeTeacher(boolean sync, Collection<Teacher> teacher){
		teachers.removeAll(teacher);
		if(sync)
			syncTeacherApper();
	}
	
	public void replaceTeacher(Collection<Teacher> teacher){
		teachers.clear();
		teachers.addAll(teacher);
		syncTeacherApper();
	}
	
	public void addStudent(boolean sync, Collection<Student> student){
		students.addAll(student);
		if(sync)
			syncStudentApper();
	}
	
	public void removeStudent(boolean sync, Collection<Student> student){
		students.removeAll(student);
		if(sync)
			syncStudentApper();
	}
	
	public void replaceStudent(Collection<Student> student){
		students.clear();
		students.addAll(student);
		syncStudentApper();
	}
	
	public ArrayList<Teacher> teachers(){
		return teachers;
	}
	
	public ArrayList<Student> students(){
		return students;
	}
	
	public boolean activate(){
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
		return new Point(renderOffset + lineIndex * WIDTH, (int) (CourseManager.TOP_INSET + (start - START_AT) * HEIGHT_PER_HOUR));
	}
	
	@Override
	public void renderImage(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		g.setColor(course.color());
		g.fill(bound);
		try{
			Rectangle up;
			Rectangle down;
			if(config.maxStudent == 0){
				up = bound;
			}else{
				up = new Rectangle(0, 0, bound.width, bound.height / 2);
				down = new Rectangle(0, bound.height / 2, bound.width, bound.height / 2);
				studentDrawer.updateGraphics(g);
				studentDrawer.setFrame(down);
				studentDrawer.validate();
				studentDrawer.draw();
			}
			if(config.roomNumberFlag){
				classroomNumberDrawer.updateGraphics(g);
				classroomNumberDrawer.setFrame(up);
				classroomNumberDrawer.validate();
				classroomNumberDrawer.draw();
			}
			if(config.periodFlag){
				periodDrawer.updateGraphics(g);
				periodDrawer.setFrame(up);
				periodDrawer.validate();
				periodDrawer.draw();
			}
			if(config.maxTeacher != 0){
				teacherDrawer.updateGraphics(g);
				teacherDrawer.setFrame(up);
				teacherDrawer.validate();
				teacherDrawer.draw();
			}
			courseCodeDrawer.updateGraphics(g);
			courseCodeDrawer.setFrame(up);
			courseCodeDrawer.validate();
			courseCodeDrawer.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		g.setColor(course.color());
		g.fill(bound);
		if(isHovering()){
			Border.drawBorder(g, Border.RECTANGULAR, bound, Color.BLACK, 2, 0);
		}
		try{
			Rectangle up;
			Rectangle down;
			if(config.maxStudent == 0){
				up = bound;
			}else{
				up = new Rectangle(0, 0, bound.width, bound.height / 2);
				down = new Rectangle(0, bound.height / 2, bound.width, bound.height / 2);
				studentDrawer.updateGraphics(g);
				studentDrawer.setFrame(down);
				studentDrawer.validate();
				studentDrawer.draw();
			}
			if(config.roomNumberFlag){
				classroomNumberDrawer.updateGraphics(g);
				classroomNumberDrawer.setFrame(up);
				classroomNumberDrawer.validate();
				classroomNumberDrawer.draw();
			}
			if(config.periodFlag){
				periodDrawer.updateGraphics(g);
				periodDrawer.setFrame(up);
				periodDrawer.validate();
				periodDrawer.draw();
			}
			if(config.maxTeacher != 0){
				teacherDrawer.updateGraphics(g);
				teacherDrawer.setFrame(up);
				teacherDrawer.validate();
				teacherDrawer.draw();
			}
			courseCodeDrawer.updateGraphics(g);
			courseCodeDrawer.setFrame(up);
			courseCodeDrawer.validate();
			courseCodeDrawer.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isTicking(){
		return activate;
	}
	
	@Override
	public boolean isRendering(){
		return activate;
	}
	
	/**
	 * use for passive synchronising
	 */
	public void syncAll(){
		this.courseCodeDrawer.initializeContents(course.courseID());
		this.classroomNumberDrawer.initializeContents("Room " + classroomNumber);
		this.periodDrawer.initializeContents(Utility.time(start, Display.FORMAT_24), "~", Utility.time(end, Display.FORMAT_24));
		syncStudentApper();
		syncTeacherApper();
	}
	
	@Override
	public void onMouseClick(MouseEvent e){
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
	public String toString(){
//		return +
//				"course=" + course +
//				", classroomNumber=Room " + classroomNumber +
//				", teachers=" + teachers +
//				", students=" + students +
//				'}';
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
