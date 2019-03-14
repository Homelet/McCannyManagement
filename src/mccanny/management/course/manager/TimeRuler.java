package mccanny.management.course.manager;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.management.course.CoursePeriod;
import mccanny.util.ImageRenderable;
import mccanny.util.Utility;
import mccanny.visual.Display;

import java.awt.*;

public class TimeRuler implements LocatableRender, ImageRenderable{
	
	public static final  int          DEFAULT_RULER_HEIGHT  = 40;
	private static final Color        GRAY                  = new Color(0x999999);
	private static final Color        HIGHLIGHT_COLOR       = new Color(0xFF6633);
	private static final Color        HIGHLIGHT_COLOR_TRANS = new Color(205, 102, 51, 125);
	private final        StringDrawer timeDrawer;
	private final        StringDrawer periodDrawer;
	private final        Dimension    size;
	private final        Point        vertex;
	private              double       highlightStart;
	private              double       highlightEnd;
	
	public TimeRuler(){
		this.size = new Dimension(CourseManager.TIMETABLE_DI.width, 0);
		this.vertex = new Point(CourseManager.FIXED_HEADER_WIDTH, CourseManager.TOP_INSET);
		this.timeDrawer = new StringDrawer();
		this.timeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(13.0f));
		this.timeDrawer.setInsetsLeft(7);
		this.timeDrawer.setAlign(Alignment.TOP_LEFT);
		this.timeDrawer.setTextAlign(Alignment.TOP_LEFT);
		this.periodDrawer = new StringDrawer();
		this.periodDrawer.setTextWidth(StringDrawer.FRAME_WIDTH, StringDrawer.FRAME_WIDTH);
		this.periodDrawer.setLinePolicy(LinePolicy.BREAK_BY_WORD);
		this.periodDrawer.setLineSpaceing(-10);
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.periodDrawer.setAlign(Alignment.CENTER);
		this.periodDrawer.setTextAlign(Alignment.TOP);
		this.periodDrawer.setColor(Color.BLACK);
		syncHeight();
		discardHighlight();
	}
	
	public void syncHeight(){
		this.size.height = DEFAULT_RULER_HEIGHT + CourseManager.TIMETABLE_DI.height;
	}
	
	public void highlight(double start, double end){
		this.highlightStart = start;
		this.highlightEnd = end;
		syncPeriod();
	}
	
	public void discardHighlight(){
		this.highlightStart = -1;
		this.highlightEnd = -1;
		syncPeriod();
	}
	
	private void syncPeriod(){
		if(highlightStart >= 0 && highlightEnd >= 0)
			periodDrawer.initializeContents(Utility.time(highlightStart, Display.FORMAT_24) + "~" + Utility.time(highlightEnd, Display.FORMAT_24) + " (" + (highlightEnd - highlightStart) + "h)");
		else
			periodDrawer.clearAllContents();
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
	public void renderImage(Graphics2D g){
		render(g);
	}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		timeDrawer.updateGraphics(g);
		int widthOffset = 0;
		int drawingFlag;
		{
			double reminder = CoursePeriod.START_AT - Math.floor(CoursePeriod.START_AT);
			if(reminder == 0){
				drawingFlag = 0; // 0 flag
			}else if(reminder == 0.25){
				drawingFlag = 1; // 15 flag
			}else if(reminder == 0.5){
				drawingFlag = 2; // 30 flag
			}else if(reminder == 0.75){
				drawingFlag = 3; // 45 flag
			}else{
				return;
			}
		}
		for(double timeCount = CoursePeriod.START_AT; timeCount <= CoursePeriod.END_AT; timeCount += 0.25, widthOffset += CoursePeriod.WIDTH_PER_HOUR / 4){
			if(timeCount == highlightStart || timeCount == highlightEnd){
				g.setColor(HIGHLIGHT_COLOR);
				timeDrawer.setColor(HIGHLIGHT_COLOR);
			}else{
				g.setColor(GRAY);
				timeDrawer.setColor(GRAY);
			}
			Rectangle rect;
			switch(drawingFlag){
				case 0:
					timeDrawer.setFrame(new Rectangle(widthOffset, 0, bound.width - widthOffset, DEFAULT_RULER_HEIGHT));
					timeDrawer.initializeContents(Utility.time(timeCount, Display.FORMAT_24));
					timeDrawer.validate();
					timeDrawer.draw();
					rect = new Rectangle(widthOffset, 0, 5, DEFAULT_RULER_HEIGHT);
					break;
				case 2:
					rect = new Rectangle(widthOffset, DEFAULT_RULER_HEIGHT - 20, 4, 20);
					break;
				default:
				case 3:
				case 1:
					rect = new Rectangle(widthOffset, DEFAULT_RULER_HEIGHT - 10, 2, 10);
					break;
			}
			g.fill(rect);
			if(drawingFlag == 3){
				drawingFlag = 0;
			}else{
				drawingFlag++;
			}
		}
		if(highlightStart >= 0 && highlightEnd >= 0){
			double    hour        = highlightEnd - highlightStart;
			Rectangle periodBound = new Rectangle((int) ((highlightStart - CoursePeriod.START_AT) * CoursePeriod.WIDTH_PER_HOUR), 0, (int) (CoursePeriod.WIDTH_PER_HOUR * hour), DEFAULT_RULER_HEIGHT);
			g.setColor(HIGHLIGHT_COLOR_TRANS);
			g.fill(periodBound);
			periodDrawer.updateGraphics(g);
			periodDrawer.setFrame(periodBound);
			periodDrawer.validate();
			periodDrawer.draw();
		}
	}
}
