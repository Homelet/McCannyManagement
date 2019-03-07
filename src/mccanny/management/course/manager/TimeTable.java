package mccanny.management.course.manager;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawerException;
import homelet.GH.handlers.GH;
import homelet.GH.utils.Alignment;
import homelet.GH.visual.interfaces.Renderable;
import mccanny.io.Builder;
import mccanny.io.TimeTableBuilder.TimeTableBuilder;
import mccanny.management.course.CoursePeriod;
import mccanny.util.*;
import mccanny.visual.Display;
import mccanny.visual.infoCenter.OneClickImageDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;

public class TimeTable implements Renderable, ImageRenderable{
	
	private static final TimeTableBuilder BUILDER = Builder.TIME_TABLE_V1;
	private static       JFileChooser     FILE_CHOOSER;
	
	static{
		FILE_CHOOSER = new JFileChooser();
		FILE_CHOOSER.setMultiSelectionEnabled(false);
		FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FILE_CHOOSER.setDialogType(JFileChooser.SAVE_DIALOG);
		FILE_CHOOSER.setDialogTitle("Save As");
		FILE_CHOOSER.setApproveButtonToolTipText("Click to save Timetable.");
	}
	
	//
	private static Color                            GRAY = new Color(0x999999);
	private final  OrderedUniqueArray<CoursePeriod> periods;
	private final  StringDrawer                     nameDrawer;
	private final  StringDrawer                     periodDrawer;
	private final  StringDrawer                     timeDrawer;
	private final  StringDrawer                     semiTimeDrawer;
	private final  StringDrawer                     filterDrawer;
	private        String                           name;
	private        Date                             startDate;
	private        Date                             endDate;
	private        Filter                           filter;
	private        boolean                          saved;
	private        File                             file;
	
	// for sub Timetable
	public TimeTable(TimeTable parent, Object object, Filter filter){
		this.nameDrawer = new StringDrawer();
		this.nameDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(30.0f));
		this.nameDrawer.setAlign(Alignment.TOP);
		this.nameDrawer.setTextAlign(Alignment.TOP);
		this.nameDrawer.setColor(Display.McCANNY_BLUE);
		this.periodDrawer = new StringDrawer();
		this.periodDrawer.setTextAlign(Alignment.TOP);
		this.periodDrawer.setAlign(Alignment.BOTTOM);
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(20.0f));
		this.periodDrawer.setColor(GRAY);
		this.filterDrawer = new StringDrawer(filter.toString());
		this.filterDrawer.setAlign(Alignment.BOTTOM);
		this.filterDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.filterDrawer.setTextAlign(Alignment.RIGHT);
		this.filterDrawer.setColor(GRAY);
		this.filterDrawer.setInsetsRight(5);
		this.timeDrawer = new StringDrawer();
		this.timeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.timeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.timeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.timeDrawer.setInsetsBottom(5);
		this.timeDrawer.setInsetsRight(5);
		this.timeDrawer.setColor(GRAY);
		this.semiTimeDrawer = new StringDrawer();
		this.semiTimeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.semiTimeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.semiTimeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.semiTimeDrawer.setInsetsBottom(3);
		this.semiTimeDrawer.setInsetsRight(5);
		this.semiTimeDrawer.setColor(GRAY);
		this.periods = new OrderedUniqueArray<>();
		this.name = parent.name + (object != null ? (" (" + object + ")") : "");
		name(name);
		for(CoursePeriod period : parent.periods){
			if(filter.filter(period))
				periods.add(period);
		}
		this.filter = filter;
		this.startDate = parent.startDate;
		this.endDate = parent.endDate;
		syncPeriodDrawer();
	}
	
	public TimeTable(String name, File file, Date startDate, Date endDate){
		this.nameDrawer = new StringDrawer();
		this.nameDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(30.0f));
		this.nameDrawer.setAlign(Alignment.TOP);
		this.nameDrawer.setTextAlign(Alignment.TOP);
		this.nameDrawer.setColor(Display.McCANNY_BLUE);
		this.periodDrawer = new StringDrawer();
		this.periodDrawer.setTextAlign(Alignment.TOP);
		this.periodDrawer.setAlign(Alignment.BOTTOM);
		this.periodDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(20.0f));
		this.periodDrawer.setColor(GRAY);
		this.filterDrawer = new StringDrawer();
		this.filterDrawer.setAlign(Alignment.BOTTOM);
		this.filterDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.filterDrawer.setTextAlign(Alignment.RIGHT);
		this.filterDrawer.setColor(GRAY);
		this.filterDrawer.setInsetsRight(5);
		this.timeDrawer = new StringDrawer();
		this.timeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
		this.timeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.timeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.timeDrawer.setInsetsBottom(5);
		this.timeDrawer.setInsetsRight(5);
		this.timeDrawer.setColor(GRAY);
		this.semiTimeDrawer = new StringDrawer();
		this.semiTimeDrawer.setFont(Display.CLEAR_SANS_BOLD.deriveFont(10.0f));
		this.semiTimeDrawer.setAlign(Alignment.BOTTOM_RIGHT);
		this.semiTimeDrawer.setTextAlign(Alignment.TOP_RIGHT);
		this.semiTimeDrawer.setInsetsBottom(3);
		this.semiTimeDrawer.setInsetsRight(5);
		this.semiTimeDrawer.setColor(GRAY);
		this.periods = new OrderedUniqueArray<>();
		this.name = name;
		name(name);
		applyFilter(Filter.NULL_FILTER);
		this.startDate = startDate;
		this.endDate = endDate;
		syncPeriodDrawer();
		this.file = file;
	}
	
	public void startDate(Date startDate){
		this.startDate = startDate;
		syncPeriodDrawer();
	}
	
	public Date startDate(){
		return startDate;
	}
	
	public void endDate(Date endDate){
		this.endDate = endDate;
		syncPeriodDrawer();
	}
	
	public Date endDate(){
		return endDate;
	}
	
	public String name(){
		return name;
	}
	
	public void name(String name){
		this.name = name;
		nameDrawer.initializeContents(name);
	}
	
	private void syncPeriodDrawer(){
		this.periodDrawer.initializeContents((startDate == null ? "" : startDate.dateOnly()) + (endDate == null ? "" : " ~ " + endDate.dateOnly()));
	}
	
	public void addAll(Collection<CoursePeriod> periods){
		for(CoursePeriod period : periods){
			add(period);
		}
	}
	
	public OrderedUniqueArray<CoursePeriod> periods(){
		return periods;
	}
	
	public boolean isEmpty(){
		return periods.isEmpty();
	}
	
	public void add(CoursePeriod period){
		this.periods.add(period);
		period.activate(filter.filter(period));
	}
	
	public void remove(CoursePeriod period){
		this.periods.remove(period);
	}
	
	void applyFilter(Filter filter){
//		if(this.filter != null && this.filter.equals(filter))
//			return;
		this.filter = filter;
		for(CoursePeriod period : periods)
			period.activate(filter.filter(period));
		this.filterDrawer.initializeContents(this.filter.toString());
	}
	
	@Override
	public void renderImage(Graphics2D g){
		Rectangle bound      = g.getClipBounds();
		Rectangle topPortion = new Rectangle(0, 0, bound.width, CourseManager.TOP_INSET - CourseManager.FIXED_HEADER_HEIGHT - 5);
		this.nameDrawer.updateGraphics(g);
		this.periodDrawer.updateGraphics(g);
		this.filterDrawer.updateGraphics(g);
		this.nameDrawer.setFrame(topPortion);
		this.periodDrawer.setFrame(topPortion);
		this.filterDrawer.setFrame(topPortion);
		try{
			nameDrawer.validate();
			periodDrawer.validate();
			filterDrawer.validate();
			nameDrawer.draw();
			periodDrawer.draw();
			filterDrawer.draw();
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
			drawer.initializeContents(Utility.time(timeCount, Display.FORMAT_24));
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
			int renderOffset = OneClickImageDialog.renderOffset(day);
			g.fill(GH.rectangle(false, renderOffset - 5, CourseManager.TOP_INSET - 5, 5, CourseManager.TIMETABLE_DI.height + 5));
		}
	}
	
	@Override
	public void tick(){}
	
	/**
	 * 130 - 60 - 5 = 65
	 * 65 - 5 - 5 - 10 - 10 = 35
	 */
	@Override
	public void render(Graphics2D g){
		Rectangle bound      = g.getClipBounds();
		Rectangle topPortion = new Rectangle(0, 0, bound.width, CourseManager.TOP_INSET - CourseManager.FIXED_HEADER_HEIGHT - 5);
		this.nameDrawer.updateGraphics(g);
		this.periodDrawer.updateGraphics(g);
		this.filterDrawer.updateGraphics(g);
		this.nameDrawer.setFrame(topPortion);
		this.periodDrawer.setFrame(topPortion);
		this.filterDrawer.setFrame(topPortion);
		try{
			nameDrawer.validate();
			periodDrawer.validate();
			filterDrawer.validate();
			nameDrawer.draw();
			periodDrawer.draw();
			filterDrawer.draw();
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
			drawer.initializeContents(Utility.time(timeCount, Display.FORMAT_24));
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
	
	public Filter filter(){
		return filter;
	}
	
	private String promptPath(){
		File f;
		if(file == null)
			f = new File(Utility.join("", name + ".timetable"));
		else
			f = file;
		FILE_CHOOSER.setSelectedFile(f);
		int result = FILE_CHOOSER.showDialog(Display.getInstance(), "Save As .timetable");
		switch(result){
			case JFileChooser.CANCEL_OPTION:
			case JFileChooser.ERROR_OPTION:
				return null;
			case JFileChooser.APPROVE_OPTION:
		}
		f = FILE_CHOOSER.getSelectedFile();
		String path = f.getPath();
		if(!path.endsWith(".timetable"))
			path += ".timetable";
		return path;
	}
	
	public boolean save(){
		if(!saved){
			if(file == null){
				return saveAs();
			}else{
				saved = true;
				return BUILDER.encode(this, file);
			}
		}
		return true;
	}
	
	public boolean saveAs(){
		String path = promptPath();
		if(path == null)
			return false;
		file = new File(path);
		saved = true;
		return BUILDER.encode(this, file);
	}
	
	public boolean close(){
		if(!saved){
			int result = JOptionPane.showConfirmDialog(Display.getInstance(), "There is a unsaved change.\nAre you sure to save?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
			switch(result){
				default:
				case JOptionPane.NO_OPTION:
					return false;
				case JOptionPane.YES_OPTION:
					return save();
			}
		}
		return true;
	}
	
	public void changed(){
		saved = false;
	}
}
