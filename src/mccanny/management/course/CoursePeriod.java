package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * a course periodFlag is
 */
public class CoursePeriod extends ActionsManager implements Comparable<CoursePeriod>, LocatableRender{
	
	public static final  double                                 START_AT        = 8.0;
	public static final  double                                 END_AT          = 20.0;
	public static final  int                                    WIDTH           = 50;
	public static final  int                                    HEIGHT_PER_HOUR = 50;
	public static        boolean                                FORMAT_24       = false;
	private static final HashMap<Double, RenderThresholdConfig> configs         = new HashMap<>();
	
	static{
		configs.put(0.5, new RenderThresholdConfig(false, false, 0, 0));
		configs.put(1.0, new RenderThresholdConfig(true, false, 2, 0));
		configs.put(1.5, new RenderThresholdConfig(true, false, 4, 0));
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
	
	/**
	 * to test the order of the periodFlag
	 */
	@Override
	public int compareTo(CoursePeriod o){
		int result = Integer.compare(this.weekday.index(), o.weekday.index());
		if(result == 0){
			int period = comparePeriod(this, o);
			return Integer.compare(period, 0);
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
			return Double.compare(p2.end, p1.end);
		}
	}
	
	private       Course                course;
	private       int                   classroomNumber;
	private       double                start;
	private       double                end;
	private       double                length;
	private       Weekday               weekday;
	private       int                   lineIndex;
	private final Dimension             size;
	private final Point                 vertex;
	private       RenderThresholdConfig config;
	// participant
	private       ArrayList<Teacher>    teachers;
	private       ArrayList<Student>    students;
	// render
	private final StringDrawer          classroomNumberDrawer;
	private final StringDrawer          periodDrawer;
	private final StringDrawer          courseCodeDrawer;
	private final StringDrawer          teacherDrawer;
	private final StringDrawer          studentDrawer;
	
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
		this.teacherDrawer.setLinePolicy(LinePolicy.BREAK_BY_WORD);
		this.studentDrawer.setLinePolicy(LinePolicy.BREAK_BY_WORD);
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
		this.weekday = weekday;
		classroom(classroomNumber);
		this.teachers = new ArrayList<>();
		this.students = new ArrayList<>();
		this.size = new Dimension();
		this.vertex = new Point();
		period(start, end);
	}
	
	public double length(){
		return length;
	}
	
	public void period(double start, double end){
		if(start >= 24 || start < 0)
			throw new IllegalArgumentException("Illegal Course Start time");
		if(end >= 24 || end < 0)
			throw new IllegalArgumentException("Illegal Course End time");
		if(end < start)
			throw new IllegalArgumentException("Period End before Start");
		this.start = start;
		this.end = end;
		this.length = end - start;
		pullConfig();
		this.size.setSize(CoursePeriod.WIDTH, (int) (CoursePeriod.HEIGHT_PER_HOUR * length));
		this.periodDrawer.initializeContents(Utility.time(start, FORMAT_24), "~", Utility.time(end, FORMAT_24));
	}
	
	private void pullConfig(){
		double hour   = Math.floor(length);
		double minute = length - hour;
		if(hour >= 8){
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
	
	public int lineIndex(){
		return lineIndex;
	}
	
	public void lineIndex(int lineIndex, boolean updateLocation){
		this.lineIndex = lineIndex;
		if(updateLocation)
			updateLocation();
	}
	
	public void updateLocation(){
		this.vertex.setLocation(Display.getInstance().manager().renderOffset(weekday) + lineIndex * WIDTH, CourseManager.TOP_INSET + (start - START_AT) * HEIGHT_PER_HOUR);
	}
	
	public void course(Course course){
		this.course = course;
		this.courseCodeDrawer.initializeContents(course.courseID());
	}
	
	public void classroom(int classroomNumber){
		this.classroomNumber = classroomNumber;
		this.classroomNumberDrawer.initializeContents("Room " + classroomNumber);
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
	
	public void weekday(Weekday weekday){
		this.weekday = weekday;
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
	
	public ArrayList<Teacher> teachers(){
		return teachers;
	}
	
	public ArrayList<Student> students(){
		return students;
	}
	
	@Override
	public String toString(){
		return "CoursePeriod{" +
				"course=" + course +
				", classroomNumber=" + classroomNumber +
				", periodFlag=(" + start + "~" + end + ", " + length() + "h)" +
				", weekday=" + weekday +
				", teachers=" + teachers +
				", students=" + students +
				'}';
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
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		if(isHovering()){
			g.setColor(Color.BLACK);
			g.fill(bound);
			g.setColor(course.color());
			g.fill(GH.rectangle(false, 2, 2, bound.width - 4, bound.height - 4));
		}else{
			g.setColor(course.color());
			g.fill(bound);
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
	
	// TODO FUTURE IMPLEMENTATION
	@Override
	public void onMousePress(MouseEvent e){}
}
