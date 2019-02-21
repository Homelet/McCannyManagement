package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.CanvasThread;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.management.exception.ClassroomCollusion;
import mccanny.management.exception.CourseCollusion;
import mccanny.management.exception.StudentCollusion;
import mccanny.management.exception.TeacherCollusion;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//@SuppressWarnings("all")
public class CourseManager{
	
	public static       Dimension             TIMETABLE_DI        = new Dimension(0, (int) ((CoursePeriod.END_AT - CoursePeriod.START_AT) * CoursePeriod.HEIGHT_PER_HOUR));
	public static final int                   MIN_COUNT           = 4;
	public static final int                   TOP_INSET           = 130;
	public static final int                   LEFT_INSET          = 80;
	public static final int                   RIGHT_INSET         = 0;
	public static final int                   BOTTOM_INSET        = 0;
	public static final int                   FIXED_HEADER_HEIGHT = 60;
	private             TimeTable             timeTable;
	private final       HashMap<Weekday, Day> days;
	private final       CanvasThread          thread;
	
	public CourseManager(CanvasThread thread){
		this.thread = thread;
		this.days = new HashMap<>();
		this.days.put(Weekday.MONDAY, new Day(Weekday.MONDAY));
		this.days.put(Weekday.TUESDAY, new Day(Weekday.TUESDAY));
		this.days.put(Weekday.WEDNESDAY, new Day(Weekday.WEDNESDAY));
		this.days.put(Weekday.THURSDAY, new Day(Weekday.THURSDAY));
		this.days.put(Weekday.FRIDAY, new Day(Weekday.FRIDAY));
		this.days.put(Weekday.SATURDAY, new Day(Weekday.SATURDAY));
		this.days.put(Weekday.SUNDAY, new Day(Weekday.SUNDAY));
		thread.getRenderManager().addPreTargets(this.days.values().toArray(new LocatableRender[0]));
	}
	
	public TimeTable timeTable(){
		return timeTable;
	}
	
	public void initializeTimeTable(TimeTable timeTable){
		this.timeTable = timeTable;
		thread.getRenderManager().addPreTargets(timeTable);
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.errors.clear();
			day.events.clear();
		}
		for(CoursePeriod period : timeTable.periods){
			thread.getRenderManager().addTargets(period);
			ArrayList<Event> day = days.get(period.weekday()).events;
			day.add(new Event(period, period.start(), Event.START));
			day.add(new Event(period, period.end(), Event.END));
		}
		for(Weekday weekday : Weekday.weekdays())
			days.get(weekday).events.sort(null);
		analyze();
		for(CoursePeriod period : timeTable.periods){
			period.updateLocation();
		}
	}
	
	public void analyze(){
		for(Weekday weekday : Weekday.weekdays())
			analyze(weekday, false);
		updateOffsets();
	}
	
	/**
	 * @param updateOffset pass in true if single calling this, false otherwise
	 */
	public void analyze(Weekday weekday, boolean updateOffset){
		Day day = days.get(weekday);
		day.errors.clear();
		int          maxCount = 0;
		PeriodBuffer buffer   = new PeriodBuffer();
		for(Event event : day.events){
			if(event.status){
				check(event.period, buffer, day.errors);
				event.period.lineIndex(buffer.join(event.period), false);
				maxCount = Math.max(buffer.size, maxCount);
			}else{
				buffer.remove(event.period.lineIndex());
			}
		}
		day.maxCount(maxCount);
		if(updateOffset)
			updateOffsets();
	}
	
	private void updateOffsets(){
		int accum = LEFT_INSET;
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.renderOffset(accum);
			accum += day.width();
		}
		TIMETABLE_DI.width = accum - LEFT_INSET;
	}
	
	private void check(CoursePeriod period1, PeriodBuffer buffer, ArrayList<CourseCollusion> errors){
		for(CoursePeriod period2 : buffer.arr){
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
	
	public void update(CoursePeriod period){
		ArrayList<Event> day = days.get(period.weekday()).events;
		day.removeIf(event->event.period == period);
		day.add(new Event(period, period.start(), Event.START));
		day.add(new Event(period, period.end(), Event.END));
		day.sort(null);
	}
	
	public void add(CoursePeriod period){
		timeTable.periods.add(period);
		timeTable.periods.sort(null);
		thread.getRenderManager().addTargets(period);
		ArrayList<Event> day = days.get(period.weekday()).events;
		day.add(new Event(period, period.start(), Event.START));
		day.add(new Event(period, period.end(), Event.END));
		day.sort(null);
	}
	
	public void remove(CoursePeriod period){
		timeTable.periods.remove(period);
		thread.getRenderManager().removeTargets(period);
		ArrayList<Event> day = days.get(period.weekday()).events;
		day.removeIf(event->event.period == period);
	}
	
	class Event implements Comparable<Event>{
		
		@Override
		public int compareTo(Event o){
			return Double.compare(time, o.time);
		}
		
		final static boolean      START = true;
		final static boolean      END   = false;
		final        CoursePeriod period;
		final        double       time;
		final        boolean      status;
		
		Event(CoursePeriod period, double time, boolean status){
			this.period = period;
			this.time = time;
			this.status = status;
		}
	}
	
	class Day extends ActionsManager implements LocatableRender{
		
		private final Color                      NORMAL_BACKGROUND            = new Color(0xCCCCCC);
		private final Color                      HIDE_COLOR                   = new Color(0xEEEEEE);
		private final Color                      HIGHLIGHT_BACKGROUND         = new Color(0xFF0066);
		private final Color                      BLENDED_HIGHLIGHT_BACKGROUND = new Color(206, 169, 184);
		private final int                        FIXED_Y_OFFSET               = 65;
		final         Weekday                    weekday;
		final         ArrayList<CourseCollusion> errors;
		final         ArrayList<Event>           events;
		private       int                        renderOffset;
		private       int                        maxCount;
		private final Dimension                  size;
		private final Point                      vertex;
		private final StringDrawer               drawer;
		
		Day(Weekday weekday){
			this.weekday = weekday;
			errors = new ArrayList<>();
			events = new ArrayList<>();
			renderOffset = 0;
			maxCount = 0;
			this.size = new Dimension(0, FIXED_HEADER_HEIGHT + TIMETABLE_DI.height);
			this.vertex = new Point(0, FIXED_Y_OFFSET);
			this.drawer = new StringDrawer(weekday.toString());
			this.drawer.setAlign(Alignment.CENTER);
			this.drawer.setTextAlign(Alignment.TOP);
			this.drawer.setColor(new Color(0xFFFFFF));
			this.drawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(20.0f));
		}
		
		public int renderOffset(){
			return renderOffset;
		}
		
		public void renderOffset(int renderOffset){
			this.renderOffset = renderOffset;
			this.vertex.x = renderOffset;
		}
		
		public int maxCount(){
			return maxCount;
		}
		
		public void maxCount(int maxCount){
			this.maxCount = Math.max(MIN_COUNT, maxCount);
			this.size.width = this.maxCount * CoursePeriod.WIDTH;
		}
		
		int width(){
			return this.size.width;
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
			if(weekday == Weekday.SATURDAY || weekday == Weekday.SUNDAY){
				g.setColor(BLENDED_HIGHLIGHT_BACKGROUND);
				g.fill(GH.rectangle(false, 0, FIXED_HEADER_HEIGHT, bound.width, bound.height - FIXED_HEADER_HEIGHT));
				g.setColor(HIGHLIGHT_BACKGROUND);
			}else{
				g.setColor(NORMAL_BACKGROUND);
				g.fill(GH.rectangle(false, 0, FIXED_HEADER_HEIGHT, bound.width, bound.height - FIXED_HEADER_HEIGHT));
				g.setColor(Display.McCANNY_BLUE);
			}
			Rectangle header = new Rectangle(0, 0, bound.width, FIXED_HEADER_HEIGHT);
			g.fill(header);
			drawer.updateGraphics(g);
			drawer.setFrame(header);
			try{
				drawer.validate();
				drawer.draw();
			}catch(StringDrawerException e){
				e.printStackTrace();
			}
			if(isHovering()){
				g.setColor(HIDE_COLOR);
				g.fill(GH.rectangle(false, 0, 0, bound.width, 5));
			}
		}
	}
	
	class PeriodBuffer{
		
		CoursePeriod[] arr;
		int            size;
		
		PeriodBuffer(){
			this.arr = new CoursePeriod[8];
			this.size = 0;
		}
		
		private void expand(){
			this.arr = Arrays.copyOf(arr, (int) Math.ceil(this.arr.length * 2));
		}
		
		int join(CoursePeriod period){
			for(int i = 0; i < arr.length; i++){
				if(arr[i] == null){
					arr[i] = period;
					size++;
					return i;
				}
			}
			expand();
			return join(period);
		}
		
		void remove(int index){
			arr[index] = null;
			size--;
		}
	}
}
