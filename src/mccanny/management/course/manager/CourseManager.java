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

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

//@SuppressWarnings("all")
public class CourseManager implements Renderable{
	
	public static final int                   MIN_COUNT          = 2;
	public static final int                   TOP_INSET          = 60;
	public static final int                   LEFT_INSET         = 0;
	public static final int                   RIGHT_INSET        = 0;
	public static final int                   BOTTOM_INSET       = 0;
	public static final int                   FIXED_HEADER_WIDTH = 200;
	public static final Dimension             TIMETABLE_DI       = new Dimension((int) ((CoursePeriod.END_AT - CoursePeriod.START_AT) * CoursePeriod.WIDTH_PER_HOUR), 0);
	private             TimeTable             timeTable;
	private final       IconButtonManager     iconButtonManager;
	private final       TimeRuler             ruler;
	private final       HashMap<Weekday, Day> days;
	private final       CanvasThread          thread;
	private             boolean               dialogLock         = false;
	
	public synchronized void lock(){
		SwingUtilities.invokeLater(()->dialogLock = true);
	}
	
	public synchronized void unlock(){
		SwingUtilities.invokeLater(()->dialogLock = false);
	}
	
	public boolean locking(){
		return dialogLock;
	}
	
	public void printError(){
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			System.out.println(weekday + ": " + print(day.errors));
		}
	}
	
	private String print(ArrayList arrayList){
		StringBuilder builder = new StringBuilder("[");
		for(Object o : arrayList){
			if(o instanceof ClassroomCollusion)
				continue;
			builder.append(o).append("\n");
		}
		return builder.append("]").toString();
	}
	
	public CourseManager(CanvasThread thread){
		this.thread = thread;
		this.days = new HashMap<>();
		this.ruler = new TimeRuler();
		thread.getRenderManager().addPreTargets(this);
		for(Weekday weekday : Weekday.weekdays()){
			Day day = new Day(weekday);
			this.days.put(weekday, day);
			thread.getRenderManager().addPreTargets(day);
		}
		thread.getRenderManager().addPreTargets(ruler);
		this.iconButtonManager = new IconButtonManager(thread);
	}
	
	public TimeRuler ruler(){
		return ruler;
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
//	public void bestFit(){
//		int target = Display.SCREEN_DIMENSION.height - 100;
//		int accum  = TOP_INSET + TimeRuler.DEFAULT_RULER_HEIGHT;
//		for(Weekday weekday : Weekday.weekdays()){
//			Day day = days.get(weekday);
//			accum += day.height() + 5;
////			day.active(accum <= target);
//		}
//		analyze();
//		updateOffsets();
//		syncAllLocation();
//	}
	
	public void active(Weekday weekday, boolean active){
//		days.get(weekday).active(active);
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
//		if(!day.active())
//			return;
		day.errors.clear();
		int          maxCount = 0;
		PeriodBuffer buffer   = new PeriodBuffer();
		for(PeriodEvent event : day.events){
			if(!event.period.activate())
				continue;
			if(event.status == PeriodEvent.START){
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
		g.setColor(Display.NORMAL_BACKGROUND);
		g.fill(g.getClipBounds());
	}
	
	public TimeTable timeTable(){
		return timeTable;
	}
	
	private void updateOffsets(){
		int accum = TOP_INSET + TimeRuler.DEFAULT_RULER_HEIGHT;
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
//			if(!day.active())
//				return;
			day.renderOffset(accum);
			accum += day.height() + 5;
		}
		updateDimension(accum - TOP_INSET - TimeRuler.DEFAULT_RULER_HEIGHT - 5);
	}
	
	private void updateDimension(int newHeight){
		if(TIMETABLE_DI.height != newHeight){
			TIMETABLE_DI.height = newHeight;
			ruler.syncHeight();
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
	
	public void applyFilter(){
//		if(this.timeTable.filter().equals(filter))
//			return;
		timeTable.applyFilter();
		analyze();
		updateOffsets();
		syncAllLocation();
	}
	
	public void applyFilter(Filter filter){
//		if(this.timeTable.filter().equals(filter))
//			return;
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
	
	public Day day(Weekday weekday){
		return days.get(weekday);
	}
}
