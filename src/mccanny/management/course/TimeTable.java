package mccanny.management.course;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.interfaces.Renderable;
import mccanny.util.Date;
import mccanny.util.OrderedUniqueArray;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;

import java.awt.*;
import java.util.Collection;

public class TimeTable implements Renderable{
	
	private static Color                            GRAY = new Color(0x999999);
	private        Date                             startDate;
	private        Date                             endDate;
	private final  OrderedUniqueArray<CoursePeriod> periods;
	private        Filter                           filter;
	private final  StringDrawer                     nameDrawer;
	private final  StringDrawer                     periodDrawer;
	private final  StringDrawer                     timeDrawer;
	private final  StringDrawer                     semiTimeDrawer;
	
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
		this.periods = new OrderedUniqueArray<>();
		this.filter = Filter.NULL_FILTER;
		startDate(startDate);
	}
	
	public Date startDate(){
		return startDate;
	}
	
	public void startDate(Date startDate){
		this.startDate = startDate;
		this.periodDrawer.initializeContents(startDate.visibleMonth() + ", " + startDate.visibleYear());
	}
	
	public void addAll(Collection<CoursePeriod> periods){
		for(CoursePeriod period : periods){
			this.periods.add(period);
			period.activate(filter.filter(period));
		}
	}
	
	public OrderedUniqueArray<CoursePeriod> periods(){
		return periods;
	}
	
	void add(CoursePeriod period){
		this.periods.add(period);
		period.activate(filter.filter(period));
	}
	
	void remove(CoursePeriod period){
		this.periods.remove(period);
	}
	
	void applyFilter(Filter filter){
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
}
