package mccanny.management.course.manager;

import homelet.GH.visual.interfaces.Locatable;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.ImageRenderable;
import mccanny.util.PeriodBuffer;
import mccanny.util.Weekday;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class OneClickImageRenderer implements ImageRenderable{
	
	private static final String                         ROOT          = "timeTables";
	private static final String                         STUDENTS_ROOT = "timeTables/student";
	private static final String                         TEACHER_ROOT  = "timeTables/teacher";
	private static final String                         COURSES_ROOT  = "timeTables/course";
	// render list
	private final        List<ImageRenderable>          preRenderList;
	private final        List<ImageRenderable>          renderList;
	private final        List<ImageRenderable>          postRenderList;
	private final        HashMap<Weekday, Day>          days;
	private final        HashMap<CoursePeriod, Integer> lineIndex;
	
	public OneClickImageRenderer(){
		this.days = new HashMap<>();
		this.lineIndex = new HashMap<>();
		this.preRenderList = Collections.synchronizedList(new ArrayList<>());
		this.renderList = Collections.synchronizedList(new ArrayList<>());
		this.postRenderList = Collections.synchronizedList(new ArrayList<>());
		for(Weekday weekday : Weekday.weekdays()){
			Day day = new Day(weekday);
			this.days.put(weekday, day);
			this.preRenderList.add(day);
		}
	}
	
	public int renderOffset(Weekday weekday){
		return days.get(weekday).renderOffset();
	}
	
	public void start(TimeTable timeTable){
		createRenderedImage(timeTable, null, Filter.NULL_FILTER, ROOT);
		createTimeTable(timeTable, Student.students(), STUDENTS_ROOT);
		createTimeTable(timeTable, Teacher.teachers(), TEACHER_ROOT);
		createTimeTable(timeTable, Course.courses(), COURSES_ROOT);
	}
	
	private void createTimeTable(TimeTable parent, Collection collection, String path){
		for(Object o : collection){
			createRenderedImage(parent, o, Filter.createFilter(Filter.POSITIVE, o), path);
		}
	}
	
	private void createRenderedImage(TimeTable parent, Object o, Filter filter, String path){
		TimeTable     timeTable = new TimeTable(parent, o, filter);
		BufferedImage image     = renderImage(timeTable);
		try{
			String filename = o != null ? o.toString().trim().replace("/", "_").replace("\\", "_") + ".png" : "timetable.png";
			System.out.println(filename);
			Iterator<ImageWriter> writers     = ImageIO.getImageWritersByFormatName("png");
			ImageWriter           imageWriter = writers.next();
			ImageWriteParam       pngparams   = imageWriter.getDefaultWriteParam();
			pngparams.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
//			pngparams.setCompressionQuality(1.0F);
			pngparams.setProgressiveMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
			pngparams.setDestinationType(new ImageTypeSpecifier(image.getColorModel(), image.getSampleModel()));
			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new FileOutputStream(new File(path + "/" + filename)));
			imageWriter.setOutput(imageOutputStream);
			imageWriter.write(null, new IIOImage(image, null, null), pngparams);
			imageOutputStream.close();
			imageWriter.dispose();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private BufferedImage renderImage(TimeTable timeTable){
		Dimension     size  = initTimeTable(timeTable);
		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		renderImage((Graphics2D) image.createGraphics().create(0, 0, size.width, size.height));
		image.flush();
		return image;
	}
	
	private Dimension initTimeTable(TimeTable timeTable){
		this.renderList.clear();
		this.postRenderList.clear();
		this.lineIndex.clear();
		this.renderList.add(timeTable);
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.errors.clear();
			day.events.clear();
		}
		for(CoursePeriod period : timeTable.periods()){
			this.renderList.add(period);
			Day day = days.get(period.weekday());
			day.events.add(new PeriodEvent(period, period.start(), PeriodEvent.START));
			day.events.add(new PeriodEvent(period, period.end(), PeriodEvent.END));
		}
		return analyze();
	}
	
	private Dimension analyze(){
		for(Weekday weekday : Weekday.weekdays())
			analyze(weekday);
		return updateOffsets();
	}
	
	private void analyze(Weekday weekday){
		Day          day      = days.get(weekday);
		int          maxCount = 0;
		PeriodBuffer buffer   = new PeriodBuffer();
		for(PeriodEvent event : day.events){
			if(event.status){
				lineIndex.put(event.period, buffer.join(event.period));
				maxCount = Math.max(buffer.size(), maxCount);
			}else{
				buffer.remove(lineIndex.get(event.period));
			}
		}
		day.maxCount(maxCount);
	}
	
	private Dimension updateOffsets(){
		int accum = CourseManager.LEFT_INSET;
		for(Weekday weekday : Weekday.weekdays()){
			Day day = days.get(weekday);
			day.renderOffset(accum);
			accum += day.width();
		}
		return new Dimension(accum + CourseManager.RIGHT_INSET, (int) ((CoursePeriod.END_AT - CoursePeriod.START_AT) * CoursePeriod.HEIGHT_PER_HOUR) + CourseManager.TOP_INSET + CourseManager.BOTTOM_INSET);
	}
	
	@Override
	public void renderImage(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fill(g.getClipBounds());
		preRender(g);
		doRender(g);
		postRender(g);
	}
	
	private void doRender(Graphics2D g){
		synchronized(renderList){
			doRenderProcess(g, renderList);
		}
	}
	
	private void preRender(Graphics2D g){
		synchronized(preRenderList){
			doRenderProcess(g, preRenderList);
		}
	}
	
	private void postRender(Graphics2D g){
		synchronized(postRenderList){
			doRenderProcess(g, postRenderList);
		}
	}
	
	private void doRenderProcess(Graphics2D g, List<? extends ImageRenderable> renderList){
		if(renderList.isEmpty())
			return;
		for(ImageRenderable renderable : renderList){
			renderProcess(g, renderable);
		}
	}
	
	private void renderProcess(Graphics2D g, ImageRenderable renderable){
		Rectangle clipBounds = g.getClipBounds();
		Dimension size       = null;
		Point     vertex     = null;
		if(renderable instanceof CoursePeriod){
			CoursePeriod period = ((CoursePeriod) renderable);
			size = period.getSize();
			vertex = period.getVertex(days.get(period.weekday()).renderOffset(), lineIndex.get(period));
		}else if(renderable instanceof Locatable){
			Locatable locatableRender = ((Locatable) renderable);
			size = locatableRender.getSize();
			vertex = locatableRender.getVertex(clipBounds);
		}
		if(size == null)
			size = clipBounds.getSize();
		if(vertex == null)
			vertex = new Point(0, 0);
		Rectangle  bounds = clipBounds.intersection(new Rectangle(vertex, size));
		Graphics2D g2     = (Graphics2D) g.create(bounds.x, bounds.y, bounds.width, bounds.height);
		renderable.renderImage(g2);
	}
}
