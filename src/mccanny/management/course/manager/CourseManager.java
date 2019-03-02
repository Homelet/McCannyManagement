package mccanny.management.course.manager;

import homelet.GH.visual.CanvasThread;
import homelet.GH.visual.interfaces.Renderable;
import mccanny.management.course.CoursePeriod;
import mccanny.management.exception.ClassroomCollusion;
import mccanny.management.exception.CourseCollusion;
import mccanny.management.exception.StudentCollusion;
import mccanny.management.exception.TeacherCollusion;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.OrderedUniqueArray;
import mccanny.util.PeriodBuffer;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;
import mccanny.visual.rendered.IconButtonManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

//@SuppressWarnings("all")
public class CourseManager implements Renderable{
	
	public static       Dimension             TIMETABLE_DI        = new Dimension(0, (int) ((CoursePeriod.END_AT - CoursePeriod.START_AT) * CoursePeriod.HEIGHT_PER_HOUR));
	public static final int                   MIN_COUNT           = 4;
	public static final int                   TOP_INSET           = 130;
	public static final int                   LEFT_INSET          = 85;
	public static final int                   RIGHT_INSET         = 0;
	public static final int                   BOTTOM_INSET        = 0;
	public static final int                   FIXED_HEADER_HEIGHT = 60;
	private             TimeTable             timeTable;
	private final       IconButtonManager     iconButtonManager;
	private final       HashMap<Weekday, Day> days;
	private final       CanvasThread          thread;
	
	public CourseManager(CanvasThread thread){
		this.thread = thread;
		this.days = new HashMap<>();
		thread.getRenderManager().addPreTargets(this);
		for(Weekday weekday : Weekday.weekdays()){
			Day day = new Day(weekday);
			this.days.put(weekday, day);
			thread.getRenderManager().addPreTargets(day);
		}
		this.iconButtonManager = new IconButtonManager(thread);
	}
	
	public void initializeTimeTable(TimeTable timeTable){
		this.timeTable = timeTable;
		thread.getRenderManager().addPreTargets(timeTable);
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.errors.clear();
			day.events.clear();
		}
		for(CoursePeriod period : timeTable.periods()){
			thread.getRenderManager().addTargets(period);
			if(!period.activate())
				continue;
			Day day = days.get(period.weekday());
			day.events.add(new PeriodEvent(period, period.start(), PeriodEvent.START));
			day.events.add(new PeriodEvent(period, period.end(), PeriodEvent.END));
		}
		analyze();
		updateOffsets();
		syncAllLocation();
	}
	
	public void analyze(){
		for(Weekday weekday : Weekday.weekdays())
			analyze(weekday);
	}
	
	public void analyze(Weekday weekday){
		Day day = days.get(weekday);
		day.errors.clear();
		int          maxCount = 0;
		PeriodBuffer buffer   = new PeriodBuffer();
		for(PeriodEvent event : day.events){
			if(event.status){
				check(event.period, buffer, day.errors);
				event.period.lineIndex(buffer.join(event.period));
				maxCount = Math.max(buffer.size(), maxCount);
			}else{
				buffer.remove(event.period.lineIndex());
			}
		}
		day.maxCount(maxCount);
	}
	
	public void analyze(Weekday weekday, int start, int end){
		Day                     day               = days.get(weekday);
		ArrayList<CoursePeriod> buffer            = new ArrayList<>();
		ArrayList<CoursePeriod> potentialConflict = new ArrayList<>();
		for(PeriodEvent event : day.events){
			if(event.status){
				buffer.add(event.period);
			}else{
				buffer.remove(event.period.lineIndex());
			}
		}
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	public TimeTable timeTable(){
		return timeTable;
	}
	
	private void updateOffsets(){
		int accum = LEFT_INSET;
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.renderOffset(accum);
			accum += day.width();
		}
		updateDimension(accum - LEFT_INSET);
	}
	
	private void updateDimension(int newWidth){
		if(TIMETABLE_DI.width != newWidth){
			TIMETABLE_DI.width = newWidth;
			Display.getInstance().updateDimension();
		}
	}
	
	private void check(CoursePeriod period1, PeriodBuffer buffer, ArrayList<CourseCollusion> errors){
		for(CoursePeriod period2 : buffer.arr()){
			if(period2 == null)
				continue;
			if(period1.classroom() == period2.classroom())
				errors.add(new ClassroomCollusion("Classroom Collusion", period1, period2));
			ArrayList<Student> students = Utility.compare(period1.students(), period2.students());
			if(!students.isEmpty())
				errors.add(new StudentCollusion("Student Collusion", students.toArray(new Student[0]), period1, period2));
			ArrayList<Teacher> teachers = Utility.compare(period1.teachers(), period2.teachers());
			if(!teachers.isEmpty())
				errors.add(new TeacherCollusion("Teacher Collusion", teachers.toArray(new Teacher[0]), period1, period2));
		}
	}
	
	public int renderOffset(Weekday weekday){
		return days.get(weekday).renderOffset();
	}
	
	public void applyFilter(Filter filter){
		if(this.timeTable.filter().equals(filter))
			return;
		timeTable.applyFilter(filter);
		analyze();
		updateOffsets();
		syncAllLocation();
	}
	
	/**
	 * call this method if a coursePeriod has changes it's start or finish or weekday
	 */
	public void update(CoursePeriod period, Weekday previousDay){
		Day previous = days.get(previousDay);
		Day current  = days.get(period.weekday());
		// first delete the previous event
		previous.events.removeIf(event->event.period == period);
		// add the current event
		OrderedUniqueArray<PeriodEvent> eventArray = current.events;
		eventArray.add(new PeriodEvent(period, period.start(), PeriodEvent.START));
		eventArray.add(new PeriodEvent(period, period.end(), PeriodEvent.END));
		if(previousDay != period.weekday()){
			analyze(previousDay);
		}
		analyze(period.weekday());
		updateOffsets();
		syncAllLocation();
	}
	
	public void addAll(Collection<CoursePeriod> periods){
		if(periods.size() == 0)
			return;
		HashSet<Weekday> weekdays = new HashSet<>();
		for(CoursePeriod period : periods){
			timeTable.add(period);
			thread.getRenderManager().addTargets(period);
			Day day = days.get(period.weekday());
			day.events.add(new PeriodEvent(period, period.start(), PeriodEvent.START));
			day.events.add(new PeriodEvent(period, period.end(), PeriodEvent.END));
			weekdays.add(period.weekday());
		}
		for(Weekday weekday : weekdays){
			analyze(weekday);
		}
		updateOffsets();
		syncAllLocation();
	}
	
	public void add(CoursePeriod period){
		timeTable.add(period);
		thread.getRenderManager().addTargets(period);
		Day day = days.get(period.weekday());
		day.events.add(new PeriodEvent(period, period.start(), PeriodEvent.START));
		day.events.add(new PeriodEvent(period, period.end(), PeriodEvent.END));
		analyze(period.weekday());
		updateOffsets();
		syncAllLocation();
	}
	
	public void remove(CoursePeriod period){
		timeTable.remove(period);
		thread.getRenderManager().removeTargets(period);
		Day day = days.get(period.weekday());
		day.events.removeIf(event->event.period == period);
		analyze(period.weekday());
		updateOffsets();
		syncAllLocation();
	}
	
	private void syncAllLocation(){
		for(CoursePeriod period : timeTable.periods())
			period.updateLocation();
	}
	
	/**
	 * for passive syncing
	 */
	public void syncAll(){
		for(CoursePeriod period : timeTable.periods())
			period.syncAll();
	}
}
