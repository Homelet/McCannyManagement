package mccanny.visual.dialog;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.handlers.GH;
import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.FlowLayouter;
import homelet.GH.handlers.Layouter.FlowLayouter.FlowAlignment;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.handlers.Layouter.SpringLayouter;
import homelet.GH.handlers.Layouter.SpringLayouter.Position;
import homelet.GH.utils.Alignment;
import homelet.GH.utils.ToolBox;
import homelet.GH.utils.ToolBox.Orientation;
import homelet.GH.visual.swing.JInput.JInputArea;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooser;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooser.ValueProcessor;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserEvent;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserGroup;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

public class PeriodInfoDialog extends InfoDialog<CoursePeriod>{
	
	public static CoursePeriod showDialog(CoursePeriod coursePeriod){
		PeriodInfoDialog dialog = new PeriodInfoDialog(coursePeriod);
		dialog.showDialog();
		dialog.closeDialog();
		return dialog.result();
	}
	
	private final Border       DEFAULT_BORDER = BorderFactory.createLineBorder(Color.BLACK, 5, true);
	private       CoursePeriod period;
	
	public PeriodInfoDialog(CoursePeriod period){
		super(Display.getInstance(), "CoursePeriod");
		this.period = period;
		NestedPanel panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	@Override
	public CoursePeriod result(){
		return period;
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JInputArea           announcement = new JInputArea("Announcement", false);
			BasicModule          basicField   = new BasicModule();
			DateModule           dateField    = new DateModule();
			ChooseField<Teacher> teacherField = new ChooseField<>("Teacher", Teacher.teachers(), period == null ? null : period.teachers());
			ChooseField<Student> studentField = new ChooseField<>("Student", Student.students(), period == null ? null : period.students());
			announcement.setBorder(BorderFactory.createTitledBorder(DEFAULT_BORDER, "Announcement", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD));
			Layouter.SpringLayouter layouter = new SpringLayouter(this);
			layouter.put(Position.CONSTRAIN_X, basicField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, basicField, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, basicField, -10, Position.CONSTRAIN_X, announcement);
			layouter.put(Position.CONSTRAIN_Y, announcement, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, announcement, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_Y, dateField, 10, Position.CONSTRAIN_Y_HEIGHT, basicField);
			layouter.put(Position.CONSTRAIN_X, dateField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, dateField, -10, Position.CONSTRAIN_X, announcement);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, dateField, 0, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_X, teacherField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, teacherField, 10, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, teacherField, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			layouter.put(Position.CONSTRAIN_X, studentField, 10, Position.CONSTRAIN_X_WIDTH, teacherField);
			layouter.put(Position.CONSTRAIN_Y, studentField, 10, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_X_WIDTH, studentField, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, studentField, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			// 730 * 830
			layouter.put(layouter.instanceOf(this).put(Position.VALUE_WIDTH, 730).put(Position.VALUE_HEIGHT, 830));
			layouter.put(layouter.instanceOf(announcement).put(Position.VALUE_WIDTH, 200).put(Position.VALUE_HEIGHT, 350));
			layouter.put(layouter.instanceOf(teacherField).put(Position.VALUE_WIDTH, 350).put(Position.VALUE_HEIGHT, 450));
			layouter.put(layouter.instanceOf(studentField).put(Position.VALUE_WIDTH, 350).put(Position.VALUE_HEIGHT, 450));
		}
	}
	
	private class BasicModule extends JComponent{
		
		final JComboBox<Course> courseField;
		final JIndexedChooser   classroomNumberField;
		
		public BasicModule(){
			this.setBorder(BorderFactory.createTitledBorder(DEFAULT_BORDER, "Info", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD));
			JLabel course          = new JLabel("Course");
			JLabel classroomNumber = new JLabel("Classroom Number");
			courseField = new JComboBox<>(Course.courses().toArray(new Course[0]));
			classroomNumberField = new JIndexedChooser(this, 1, 1, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, null);
			courseField.setFont(Display.CLEAR_SANS_BOLD);
			course.setLabelFor(courseField);
			classroomNumber.setLabelFor(classroomNumberField);
			course.setHorizontalAlignment(JLabel.RIGHT);
			classroomNumber.setHorizontalAlignment(JLabel.RIGHT);
			course.setFont(Display.CLEAR_SANS_BOLD);
			classroomNumber.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(course, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 0));
			layouter.put(layouter.instanceOf(courseField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(classroomNumber, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(classroomNumberField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
		}
	}
	
	private class DateModule extends JComponent implements ValueProcessor, JIndexedChooserHandler{
		
		final JComboBox<Weekday> weekdayField;
		final JIndexedChooser    startField;
		final JIndexedChooser    endField;
		
		DateModule(){
			this.setBorder(BorderFactory.createTitledBorder(DEFAULT_BORDER, "Period", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD));
			JLabel start = new JLabel("Start");
			JLabel end   = new JLabel("End");
			weekdayField = new JComboBox<>(Weekday.weekdays().toArray(new Weekday[0]));
			JIndexedChooserGroup group = new JIndexedChooserGroup(this);
			startField = new JIndexedChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			endField = new JIndexedChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			group.add(startField);
			group.add(endField);
			start.setLabelFor(startField);
			end.setLabelFor(endField);
			start.setHorizontalAlignment(JLabel.CENTER);
			end.setHorizontalAlignment(JLabel.CENTER);
			weekdayField.setFont(Display.CLEAR_SANS_BOLD);
			start.setFont(Display.CLEAR_SANS_BOLD);
			end.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(weekdayField, 0, 0, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(start, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(startField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(end, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(endField, 1, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
		}
		
		@Override
		public String process(double value){
			return Utility.time(value, CoursePeriod.FORMAT_24);
		}
		
		@Override
		public void onTrigger(JIndexedChooserEvent e){
			if(e.initiator() == startField){
				if(startField.value() > endField.value()){
					endField.processValue(startField.value());
				}
			}else{
				if(startField.value() > endField.value()){
					startField.processValue(endField.value());
				}
			}
		}
	}
	
	private class ChooseField<E extends ToolTipText> extends JScrollPane{
		
		final Color  color = Color.WHITE;
		final String title;
		final Field  field;
		
		public ChooseField(String title, Collection<E> data, Collection<E> added){
			this.title = title;
			this.field = new Field(data, added);
			this.setBorder(BorderFactory.createTitledBorder(DEFAULT_BORDER, title, TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD));
			this.setViewportView(field);
			this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
			this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		}
		
		private class Field extends JPanel implements MouseListener{
			
			final ArrayList<E>          exclude;
			final ArrayList<E>          include;
			final Layouter.FlowLayouter layouter;
			final JPopupMenu            leftClickMenu;
			final StringDrawer          notify;
			
			public Field(Collection<E> data, Collection<E> added){
				notify = new StringDrawer("Double Click To add " + title);
				notify.setLinePolicy(LinePolicy.NEVER_BREAK);
				notify.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
				notify.setAlign(Alignment.CENTER);
				notify.setColor(new Color(0x999999));
				notify.setTextAlign(Alignment.TOP);
				this.layouter = new FlowLayouter(this, FlowAlignment.LEFT, 5, 5, false, false);
				if(added != null){
					this.include = new ArrayList<>();
					this.exclude = new ArrayList<>();
					for(E e : data){
						if(added.contains(e)){
							include.add(e);
							layouter.add(new Item(e));
						}else{
							exclude.add(e);
						}
					}
					this.revalidate();
				}else{
					this.include = new ArrayList<>();
					this.exclude = new ArrayList<>(data);
				}
				this.addMouseListener(this);
				JMenuItem properties = new JMenuItem();
				JMenuItem add        = new JMenuItem("Add...");
				JMenuItem remove     = new JMenuItem("Remove");
				JMenuItem clear      = new JMenuItem("Clear All");
				this.leftClickMenu = new JPopupMenu("Actions"){
					public void show(Component invoker, int x, int y){
						properties.setText(include.size() + " " + (include.size() > 1 ? title + "s" : title) + " selected");
						super.show(invoker, x, y);
					}
				};
				add.addActionListener(e->this.add());
				remove.addActionListener(e->this.remove());
				clear.addActionListener(e->this.clear());
				this.leftClickMenu.add(properties);
				this.leftClickMenu.addSeparator();
				this.leftClickMenu.add(add);
				this.leftClickMenu.add(remove);
				this.leftClickMenu.addSeparator();
				this.leftClickMenu.add(clear);
				this.setMinimumSize(new Dimension(330, 450));
				this.setMaximumSize(new Dimension(330, Integer.MAX_VALUE));
			}
			
			public void add(E e){
				exclude.remove(e);
				include.add(e);
				layouter.add(new Item(e));
				syncBestSize();
				this.revalidate();
				this.repaint();
			}
			
			public void remove(Item item){
				exclude.add(item.item);
				include.remove(item.item);
				layouter.remove(item);
				syncBestSize();
				this.revalidate();
				this.repaint();
			}
			
			private void syncBestSize(){
				this.setPreferredSize(this.getUI().getPreferredSize(this));
			}
			
			public Collection<E> chosen(){
				return include;
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2)
					add();
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				maybeShowPopup(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				maybeShowPopup(e);
			}
			
			@Override
			public void mouseEntered(MouseEvent e){}
			
			@Override
			public void mouseExited(MouseEvent e){}
			
			private void maybeShowPopup(MouseEvent e){
				if(e.isPopupTrigger())
					leftClickMenu.show(this, e.getX(), e.getY());
			}
			
			private void add(){
				Object arr = JOptionPane.showInputDialog(ChooseField.this, "Select an item to add into the field.", "Add", JOptionPane.PLAIN_MESSAGE, null, exclude.toArray((E[]) new ToolTipText[0]), null);
				if(arr == null)
					return;
				add((E) arr);
			}
			
			private void remove(){
				JOptionPane.showMessageDialog(ChooseField.this, "To Remove a " + title + " simply RIGHT click on the " + title + " that you want to delete.", "Remove", JOptionPane.INFORMATION_MESSAGE, null);
			}
			
			private void clear(){
				exclude.addAll(include);
				include.clear();
				layouter.removeAll();
				this.revalidate();
				this.repaint();
			}
			
			@Override
			public void paint(Graphics g){
				super.paint(g);
				if(include.isEmpty()){
					notify.updateGraphics(g);
					notify.setFrame(new Dimension(this.getWidth(), this.getHeight()));
					notify.validate();
					notify.draw();
				}
			}
		}
		
		private class Item extends JLabel implements MouseListener{
			
			final   E       item;
			private boolean hovering;
			
			Item(E item){
				this.item = item;
				this.setText(item.toString());
				this.setHorizontalAlignment(CENTER);
				this.setVerticalAlignment(CENTER);
				this.addMouseListener(this);
				this.hovering = false;
				this.setFont(Display.CLEAR_SANS_BOLD);
				this.setToolTipText(item.toolTip());
				Dimension dimension = this.getPreferredSize();
				dimension.setSize(dimension.width + 20, dimension.height + 20);
				ToolBox.setPreferredSize(this, dimension);
			}
			
			@Override
			public void paint(Graphics g){
				Graphics2D g2        = (Graphics2D) g;
				double     curvature = this.getHeight();
				if(hovering){
					g.setColor(color.darker());
				}else{
					g.setColor(color);
				}
				g2.fill(GH.rRectangle(false, 0, 0, this.getWidth(), this.getHeight(), curvature, curvature));
				super.paint(g);
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON3)
					field.remove(this);
			}
			
			@Override
			public void mousePressed(MouseEvent e){}
			
			@Override
			public void mouseReleased(MouseEvent e){}
			
			@Override
			public void mouseEntered(MouseEvent e){
				hovering = true;
				this.repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e){
				hovering = false;
				this.repaint();
			}
		}
	}
}
