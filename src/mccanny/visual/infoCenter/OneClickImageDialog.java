package mccanny.visual.infoCenter;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.interfaces.Locatable;
import homelet.GH.visual.swing.JInput.JInputArea;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.course.manager.*;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.ImageRenderable;
import mccanny.util.PeriodBuffer;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;
import mccanny.visual.dialog.InfoDialog;
import mccanny.visual.swing.JBasePanel;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class OneClickImageDialog extends JDialog{
	
	public static void showOneClickDialog(){
		oneClickImageDialog.showDialog();
	}
	
	public static int renderOffset(Weekday weekday){
		return oneClickImageDialog.renderer.renderOffset(weekday);
	}
	
	private void showDialog(){
		Display.getInstance().manager().lock();
		syncAll();
		this.setLocation(Utility.frameVertex(Display.getInstance().getBounds(), this.getBounds()));
		this.setVisible(true);
		Display.getInstance().manager().unlock();
	}
	
	private void syncAll(){
		panel.reset();
	}
	
	private static final String                ROOT                = "timeTables";
	private static final String                STUDENTS_ROOT       = Utility.join(ROOT, "student");
	private static final String                TEACHER_ROOT        = Utility.join(ROOT, "teacher");
	private static final String                COURSES_ROOT        = Utility.join(ROOT, "course");
	private static final OneClickImageDialog   oneClickImageDialog = new OneClickImageDialog();
	private final        OneClickImageRenderer renderer;
	private final        NestedPanel           panel;
	
	private OneClickImageDialog(){
		super(Display.getInstance(), "One Click Image Renderer", true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				closeDialog();
				super.windowClosing(e);
			}
		});
		this.setFocusable(true);
		renderer = new OneClickImageRenderer();
		panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	private void closeDialog(){
		this.setVisible(false);
	}
	
	class NestedPanel extends JBasePanel implements ActionListener, Runnable{
		
		boolean finnished;
		int     progressCounter = 0;
		final StringBuffer builder;
		final JInputArea   logger;
		final JScrollBar   scrollBar;
		final JProgressBar progress;
		final JButton      start;
		final JButton      cancel;
		
		public NestedPanel(){
			builder = new StringBuffer();
			logger = new JInputArea("", false);
			progress = new JProgressBar();
			progress.setStringPainted(true);
			start = new JButton();
			start.addActionListener(this);
			cancel = new JButton();
			cancel.addActionListener(action->{
				closeDialog();
			});
			scrollBar = ((JScrollPane) this.logger.getComponent(0)).getVerticalScrollBar();
			logger.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			progress.setFont(Display.CLEAR_SANS_BOLD);
			start.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(logger, 0, 0, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 70).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(progress, 0, 1, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 10).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 2).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(34, 10).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(start, 1, 2).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(66, 10).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(logger, 400, 300);
			ToolBox.setPreferredSize(progress, 400, InfoDialog.FIXED_BUTTON_DIMENSION.height);
			ToolBox.setPreferredSize(cancel, InfoDialog.FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(start, InfoDialog.FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(this, 420, 390);
		}
		
		void reset(){
			int totalStep = Student.students().size() + Teacher.teachers().size() + Course.courses().size() + 1;
			progress.setMinimum(0);
			progress.setMaximum(totalStep);
			progress.setValue(0);
			builder.delete(0, builder.length());
			start.setText("Start Sequence");
			start.setToolTipText("Press to start the Sequence.");
			cancel.setText("Cancel Sequence");
			cancel.setToolTipText("Exit One Click Image Dialog.");
			builder.append("Preparing One Click Image Sequence...\nReady to initiate One Click Image Sequence!\n");
			finnished = false;
			progressCounter = 0;
			syncProgress("Ready To Start");
		}
		
		void syncLogger(){
			logger.setContent(builder.toString());
			SwingUtilities.invokeLater(this::scrollToBottom);
		}
		
		void syncProgress(String text){
			syncLogger();
			this.progress.setValue(progressCounter);
			this.progress.setString(text);
		}
		
		void scrollToBottom(){
			scrollBar.setValue(scrollBar.getMaximum());
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			if(!finnished){
				// start sequence
				new Thread(this, "OneClickImage Generation Thread").start();
			}else{
				// open dir sequence
				try{
					Desktop.getDesktop().open(new File(ROOT));
				}catch(IOException e1){
					e1.printStackTrace();
				}
			}
		}
		
		@Override
		public void run(){
			start();
		}
		
		public void start(){
			start.setEnabled(false);
			cancel.setEnabled(false);
			TimeTable parent = Display.getInstance().manager().timeTable();
			renderer.createRenderedImage(parent, "timetable.png", ROOT);
			builder.append("Created ").append("timetable.png").append('\n');
			progressCounter++;
			syncProgress("timetable.png");
			builder.append("\n============== Student ==============\n");
			syncLogger();
			createTimeTable(parent, Student.students(), STUDENTS_ROOT);
			builder.append("\n============== Teacher ==============\n");
			syncLogger();
			createTimeTable(parent, Teacher.teachers(), TEACHER_ROOT);
			builder.append("\n============== Course ==============\n");
			syncLogger();
			createTimeTable(parent, Course.courses(), COURSES_ROOT);
			builder.append("\n============= Finished =============\n").append("One Click Image Sequence Finished!");
			syncProgress("Finished");
			finnished = true;
			start.setText("Open Directory");
			start.setToolTipText("Press to Open the directory");
			start.setEnabled(true);
			cancel.setText("Exit");
			cancel.setToolTipText("Press to exit");
			cancel.setEnabled(true);
		}
		
		private void createTimeTable(TimeTable parent, Collection collection, String path){
			for(Object o : collection){
				TimeTable timeTable = new TimeTable(parent, o, Filter.createFilter(Filter.POSITIVE, o));
				String    filename  = Utility.checkName(o.toString()) + ".png";
				if(!timeTable.isEmpty()){
					renderer.createRenderedImage(timeTable, filename, path);
					builder.append("Created ").append(filename).append('\n');
				}else{
					builder.append("Skipped ").append(filename).append('\n');
				}
				progressCounter++;
				syncProgress(filename);
			}
		}
	}
	
	class OneClickImageRenderer implements ImageRenderable{
		
		// render list
		private final List<ImageRenderable>          preRenderList;
		private final List<ImageRenderable>          renderList;
		private final List<ImageRenderable>          postRenderList;
		private final HashMap<Weekday, Day>          days;
		private final HashMap<CoursePeriod, Integer> lineIndex;
		private final TimeRuler                      ruler;
		
		private OneClickImageRenderer(){
			this.days = new HashMap<>();
			this.lineIndex = new HashMap<>();
			this.ruler = new TimeRuler();
			this.preRenderList = Collections.synchronizedList(new ArrayList<>());
			this.renderList = Collections.synchronizedList(new ArrayList<>());
			this.postRenderList = Collections.synchronizedList(new ArrayList<>());
			for(Weekday weekday : Weekday.weekdays()){
				Day day = new Day(weekday);
				this.days.put(weekday, day);
				this.preRenderList.add(day);
			}
			preRenderList.add(ruler);
		}
		
		int renderOffset(Weekday weekday){
			return days.get(weekday).renderOffset();
		}
		
		private void createRenderedImage(TimeTable timeTable, String fileName, String path){
			BufferedImage image = renderImage(timeTable);
			try{
				Iterator<ImageWriter> writers     = ImageIO.getImageWritersByFormatName("png");
				ImageWriter           imageWriter = writers.next();
				ImageWriteParam       pngparams   = imageWriter.getDefaultWriteParam();
				pngparams.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
//		    	pngparams.setCompressionQuality(1.0F);
				pngparams.setProgressiveMode(ImageWriteParam.MODE_COPY_FROM_METADATA);
				pngparams.setDestinationType(new ImageTypeSpecifier(image.getColorModel(), image.getSampleModel()));
				ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new FileOutputStream(new File(Utility.join(path, fileName))));
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
				day.errors().clear();
				day.events().clear();
			}
			for(CoursePeriod period : timeTable.periods()){
				this.renderList.add(period);
				Day day = days.get(period.weekday());
				day.events().add(new PeriodEvent(period, period.start(), PeriodEvent.START));
				day.events().add(new PeriodEvent(period, period.end(), PeriodEvent.END));
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
			for(PeriodEvent event : day.events()){
				if(event.status == PeriodEvent.START){
					lineIndex.put(event.period, buffer.join(event.period));
					maxCount = Math.max(buffer.size(), maxCount);
				}else{
					buffer.remove(lineIndex.get(event.period));
				}
			}
			day.maxCount(maxCount);
		}
		
		private Dimension updateOffsets(){
			int accum = CourseManager.TOP_INSET + TimeRuler.DEFAULT_RULER_HEIGHT;
			for(Weekday weekday : Weekday.weekdays()){
				Day day = days.get(weekday);
				day.renderOffset(accum);
				accum += day.height() + 5;
			}
			return new Dimension((int) ((CoursePeriod.END_AT - CoursePeriod.START_AT) * CoursePeriod.WIDTH_PER_HOUR) + CourseManager.FIXED_HEADER_WIDTH + CourseManager.LEFT_INSET + CourseManager.RIGHT_INSET, accum + CourseManager.BOTTOM_INSET);
		}
		
		@Override
		public void renderImage(Graphics2D g){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Display.NORMAL_BACKGROUND);
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
}
