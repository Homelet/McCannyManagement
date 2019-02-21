package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.RenderManager;
import homelet.GH.visual.interfaces.Renderable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TimeTable implements Renderable{
	
	private static Color                   GRAY = new Color(0x999999);
	private        Date                    startDate;
	private        Date                    endDate;
	private final  ArrayList<CoursePeriod> periods;
	private        Filter                  filter;
	private final  StringDrawer            nameDrawer;
	private final  StringDrawer            periodDrawer;
	private final  StringDrawer            timeDrawer;
	private final  StringDrawer            semiTimeDrawer;
	
	public TimeTable(Date startDate){
		this.nameDrawer = new StringDrawer("McCanny TimeTable");
		this.periodDrawer = new StringDrawer();
		this.nameDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(30.0f));
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(20.0f));
		this.nameDrawer.setAlign(Alignment.TOP);
		this.periodDrawer.setAlign(Alignment.TOP);
		this.nameDrawer.setTextAlign(Alignment.TOP);
		this.periodDrawer.setTextAlign(Alignment.TOP);
		this.nameDrawer.setColor(Display.McCANNY_BLUE);
		this.periodDrawer.setColor(GRAY);
		this.periodDrawer.setInsetsTop(35);
		this.timeDrawer = new StringDrawer();
		this.semiTimeDrawer = new StringDrawer();
		this.timeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.semiTimeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.timeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.semiTimeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.timeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.semiTimeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.timeDrawer.setInsetsBottom(5);
		this.timeDrawer.setInsetsRight(5);
		this.semiTimeDrawer.setInsetsBottom(3);
		this.semiTimeDrawer.setInsetsRight(5);
		this.timeDrawer.setColor(GRAY);
		this.semiTimeDrawer.setColor(GRAY);
		this.periods = new ArrayList<>();
		this.filter = Filter.NULL_FILTER;
		startDate(startDate);
	}
	
	public Date startDate(){
		return startDate;
	}
	
	public void startDate(Date startDate){
		this.startDate = startDate;
		this.periodDrawer.initializeContents(startDate.visibleYear() + ", " + startDate.visibleMonth());
	}
	
	public void add(CoursePeriod period){
		this.periods.add(period);
		this.periods.sort(null);
		period.activate(filter.filter(period));
	}
	
	public void addAll(Collection<CoursePeriod> periods){
		this.periods.addAll(periods);
		this.periods.sort(null);
		for(CoursePeriod period : periods)
			period.activate(filter.filter(period));
	}
	
	public void remove(CoursePeriod period){
		this.periods.remove(period);
	}
	
	public ArrayList<CoursePeriod> periods(){
		return periods;
	}
	
	public void applyFilter(Filter filter){
		this.filter = filter;
		for(CoursePeriod period : periods)
			period.activate(filter.filter(period));
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		this.nameDrawer.updateGraphics(g);
		this.periodDrawer.updateGraphics(g);
		this.nameDrawer.setFrame(bound);
		this.periodDrawer.setFrame(bound);
		try{
			nameDrawer.validate();
			periodDrawer.validate();
			nameDrawer.draw();
			periodDrawer.draw();
		}catch(StringDrawerException e){
			e.printStackTrace();
		}
		timeDrawer.updateGraphics(g);
		semiTimeDrawer.updateGraphics(g);
		int          heightOffset = CourseManager.TOP_INSET;
		boolean      wholeFlag    = CoursePeriod.START_AT - Math.floor(CoursePeriod.START_AT) == 0;
		StringDrawer drawer;
		for(double timeCount = CoursePeriod.START_AT; timeCount <= CoursePeriod.END_AT; timeCount += 0.5, heightOffset += CoursePeriod.HEIGHT_PER_HOUR / 2){
			g.setColor(GRAY);
			if(wholeFlag){
				drawer = timeDrawer;
				g.fill(GH.rectangle(false, 0, heightOffset - 5, bound.width, 5));
				drawer.setFrame(new Rectangle(0, 0, CourseManager.LEFT_INSET, heightOffset));
			}else{
				drawer = semiTimeDrawer;
				g.fill(GH.rectangle(false, 30, heightOffset - 2, bound.width - 30, 2));
				drawer.setFrame(new Rectangle(10, 0, CourseManager.LEFT_INSET - 10, heightOffset));
			}
			drawer.initializeContents(Utility.time(timeCount, CoursePeriod.FORMAT_24));
			try{
				drawer.validate();
				drawer.draw();
			}catch(StringDrawerException e){
				e.printStackTrace();
			}
			wholeFlag = !wholeFlag;
		}
		g.setColor(Color.BLACK);
		for(Weekday day : Weekday.weekdays()){
			if(day == Weekday.FIRST_DAY_OF_WEEK)
				continue;
			int renderOffset = Display.getInstance().manager().renderOffset(day);
			g.fill(GH.rectangle(false, renderOffset - 5, CourseManager.TOP_INSET - 5, 5, CourseManager.TIMETABLE_DI.height + 5));
		}
	}
	
	public static class Filter{
		
		public static final Filter    NULL_FILTER = new Filter(false, new Course[0], new Student[0], new Teacher[0]);
		public static final boolean   POSITIVE    = true;
		public static final boolean   NEGATIVE    = true;
		// a positive polar means show all the contexts that contains any one of the info inside the filter
		// a negative polar means show all the contexts that do not contains any of the info inside the filter
		final               boolean   polar;
		final               Student[] students;
		final               Teacher[] teachers;
		final               Course[]  courses;
		
		public Filter(boolean polar, Course[] courses, Student[] students, Teacher[] teachers){
			this.polar = polar;
			this.students = students;
			this.teachers = teachers;
			this.courses = courses;
		}
		
		boolean filter(CoursePeriod period){
			if(polar)
				return checkCourse(period.course()) || checkStudent(period.students()) || checkTeacher(period.teachers());
			else
				return !checkCourse(period.course()) && !checkStudent(period.students()) && !checkTeacher(period.teachers());
		}
		
		boolean checkCourse(Course c){
			for(Course course : courses){
				if(c.equals(course))
					return true;
			}
			return false;
		}
		
		boolean checkStudent(List<Student> s){
			for(Student student : students){
				if(s.contains(student))
					return true;
			}
			return false;
		}
		
		boolean checkTeacher(List<Teacher> t){
			for(Teacher teacher : teachers){
				if(t.contains(teacher))
					return true;
			}
			return false;
		}
	}
}
