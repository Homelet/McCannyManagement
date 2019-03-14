package mccanny.management.course.manager;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.ActionsManager;
import homelet.GH.visual.interfaces.LocatableRender;
import mccanny.management.course.CoursePeriod;
import mccanny.management.exception.CourseCollusion;
import mccanny.util.ImageRenderable;
import mccanny.util.OrderedUniqueArray;
import mccanny.util.Weekday;
import mccanny.visual.Display;

import java.awt.*;
import java.util.ArrayList;

public class Day extends ActionsManager implements LocatableRender, ImageRenderable{
	
	private final Color                           NORMAL_BACKGROUND            = new Color(0xCCCCCC);
	private final Color                           HIDE_COLOR                   = new Color(0xEEEEEE);
	private final Color                           HIGHLIGHT_BACKGROUND         = new Color(0xFF0066);
	private final Color                           BLENDED_HIGHLIGHT_BACKGROUND = new Color(206, 169, 184);
	final         Weekday                         weekday;
	final         ArrayList<CourseCollusion>      errors;
	final         OrderedUniqueArray<PeriodEvent> events;
	private       int                             renderOffset;
	private       int                             maxCount;
	private       boolean                         active;
	private final Dimension                       size;
	private final Point                           vertex;
	private final StringDrawer                    drawer;
	
	public boolean active(){
		return active;
	}
	
	public void active(boolean active){
		this.active = active;
	}
	
	public Day(Weekday weekday){
		this.weekday = weekday;
		errors = new ArrayList<>();
		events = new OrderedUniqueArray<>();
		renderOffset = 0;
		maxCount = 0;
		active = true;
		this.size = new Dimension(CourseManager.FIXED_HEADER_WIDTH + CourseManager.TIMETABLE_DI.width, 0);
		this.vertex = new Point(0, 0);
		this.drawer = new StringDrawer(weekday.toString());
		this.drawer.setAlign(Alignment.CENTER);
		this.drawer.setTextAlign(Alignment.LEFT);
		this.drawer.setInsetsLeft(20);
		this.drawer.setColor(Color.WHITE);
		this.drawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(20.0f));
	}
	
	public int renderOffset(){
		return renderOffset;
	}
	
	public void renderOffset(int renderOffset){
		this.renderOffset = renderOffset;
		this.vertex.y = renderOffset;
	}
	
	public int maxCount(){
		return maxCount;
	}
	
	public void maxCount(int maxCount){
		this.maxCount = Math.max(CourseManager.MIN_COUNT, maxCount);
		this.size.height = 5 + this.maxCount * (CoursePeriod.HEIGHT + 5) + 5; // addition 5 for div line
	}
	
	public int height(){
		return this.size.height - 5;
	}
	
	@Override
	public Dimension getSize(){
		return size;
	}
	
	@Override
	public Point getVertex(Rectangle rectangle){
		return vertex;
	}
	
	public ArrayList<CourseCollusion> errors(){
		return errors;
	}
	
	public OrderedUniqueArray<PeriodEvent> events(){
		return events;
	}
	
	@Override
	public void renderImage(Graphics2D g){
		if(!active)
			return;
		Rectangle bound = g.getClipBounds();
		renderComponent(g, bound);
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		Rectangle bound = g.getClipBounds();
		renderComponent(g, bound);
		renderInteraction(g, bound);
	}
	
	@Override
	public boolean isTicking(){
		return active;
	}
	
	@Override
	public boolean isRendering(){
		return active;
	}
	
	private void renderComponent(Graphics2D g, Rectangle bound){
		if(weekday == Weekday.SATURDAY || weekday == Weekday.SUNDAY){
			g.setColor(BLENDED_HIGHLIGHT_BACKGROUND);
			g.fill(GH.rectangle(false, CourseManager.FIXED_HEADER_WIDTH, 0, bound.width - CourseManager.FIXED_HEADER_WIDTH, bound.height));
			g.setColor(HIGHLIGHT_BACKGROUND);
		}else{
			g.setColor(NORMAL_BACKGROUND);
			g.fill(GH.rectangle(false, CourseManager.FIXED_HEADER_WIDTH, 0, bound.width - CourseManager.FIXED_HEADER_WIDTH, bound.height));
			g.setColor(Display.McCANNY_BLUE);
		}
		Rectangle header = new Rectangle(0, 0, CourseManager.FIXED_HEADER_WIDTH, bound.height);
		g.fill(header);
		drawer.updateGraphics(g);
		drawer.setFrame(header);
		drawer.validate();
		drawer.draw();
	}
	
	private void renderInteraction(Graphics2D g, Rectangle bound){
		if(isHovering()){
			g.setColor(HIDE_COLOR);
			g.fill(GH.rectangle(false, 0, 0, 5, bound.height));
		}
	}
}

