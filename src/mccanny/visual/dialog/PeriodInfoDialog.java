package mccanny.visual.dialog;

import homelet.GH.handlers.GH;
import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.FlowLayouter;
import homelet.GH.handlers.Layouter.FlowLayouter.FlowAlignment;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ColorBank;
import homelet.GH.utils.ToolBox;
import homelet.GH.utils.ToolBox.Orientation;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.util.Weekday;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JNumberChooser;
import mccanny.visual.swing.JNumberChooser.ValueProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

public class PeriodInfoDialog extends InfoDialog<CoursePeriod>{
	
	private       CoursePeriod period;
	private final NestedPanel  panel;
	
	public PeriodInfoDialog(){
		this(null);
	}
	
	public PeriodInfoDialog(CoursePeriod period){
		super(Display.getInstance(), "CoursePeriod");
		this.period = period;
		this.panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	@Override
	public CoursePeriod result(){
		return period;
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JLabel               course               = new JLabel("Course");
			JLabel               classroomNumber      = new JLabel("Classroom Number");
			JLabel               date                 = new JLabel("Period");
			JLabel               teacher              = new JLabel("Teacher");
			JLabel               student              = new JLabel("Student");
			JComboBox<Course>    courseField          = new JComboBox<>(Course.courses().toArray(new Course[0]));
			JNumberChooser       classroomNumberField = new JNumberChooser(this, 1, 1, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, null);
			DateModule           dateField            = new DateModule();
			ChooseField<Teacher> teacherField         = new ChooseField<>(Teacher.teachers());
			ChooseField<Student> studentField         = new ChooseField<>(Student.students());
			course.setLabelFor(courseField);
			classroomNumber.setLabelFor(classroomNumberField);
			date.setLabelFor(dateField);
			teacher.setLabelFor(teacherField);
			student.setLabelFor(studentField);
			course.setHorizontalAlignment(JLabel.RIGHT);
			classroomNumber.setHorizontalAlignment(JLabel.RIGHT);
			date.setHorizontalAlignment(JLabel.RIGHT);
			teacher.setHorizontalAlignment(JLabel.RIGHT);
			student.setHorizontalAlignment(JLabel.RIGHT);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(course, 0, 0).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 0));
			layouter.put(layouter.instanceOf(courseField, 1, 0).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(classroomNumber, 0, 1).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(classroomNumberField, 1, 1).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(date, 0, 2).setInnerPad(FIXED_LABEL_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(dateField, 1, 2).setInnerPad(FIXED_FIELD_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacher, 0, 3).setInnerPad(FIXED_LABEL_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(teacherField, 1, 3).setInnerPad(FIXED_FIELD_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(student, 0, 4).setInnerPad(FIXED_LABEL_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(studentField, 1, 4).setInnerPad(FIXED_FIELD_WIDTH, DateModule.HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
		}
	}
	
	private class DateModule extends JComponent implements ValueProcessor{
		
		static final int FIXED_MINI_HEIGHT      = 10;
		static final int FIXED_MINI_LABEL_WIDTH = 40;
		static final int FIXED_MINI_FIELD_WIDTH = FIXED_FIELD_WIDTH - 10 - FIXED_MINI_LABEL_WIDTH;
		JComboBox<Weekday> weekdayField;
		JNumberChooser     startField;
		JNumberChooser     endField;
		static final int HEIGHT = FIXED_MINI_HEIGHT * 3 + 10 * 2;
		
		DateModule(){
			JLabel start = new JLabel("Start");
			JLabel end   = new JLabel("End");
			weekdayField = new JComboBox<>(Weekday.weekdays().toArray(new Weekday[0]));
			startField = new JNumberChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			endField = new JNumberChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			start.setLabelFor(startField);
			end.setLabelFor(endField);
			start.setHorizontalAlignment(JLabel.CENTER);
			end.setHorizontalAlignment(JLabel.CENTER);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(weekdayField, 0, 0, 2, 1).setInnerPad(FIXED_FIELD_WIDTH, FIXED_MINI_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 0));
			layouter.put(layouter.instanceOf(start, 0, 1).setInnerPad(FIXED_MINI_LABEL_WIDTH, FIXED_MINI_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 0, 0));
			layouter.put(layouter.instanceOf(startField, 1, 1).setInnerPad(FIXED_MINI_FIELD_WIDTH, FIXED_MINI_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(end, 0, 2).setInnerPad(FIXED_MINI_LABEL_WIDTH, FIXED_MINI_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 0, 0, 0));
			layouter.put(layouter.instanceOf(endField, 1, 2).setInnerPad(FIXED_MINI_FIELD_WIDTH, FIXED_MINI_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 0, 10, 0));
		}
		
		@Override
		public String process(double value){
			return Utility.time(value, CoursePeriod.FORMAT_24);
		}
	}
	
	private class ChooseField<E> extends JComponent{
		
		final Field field;
		
		public ChooseField(Collection<E> data){
			this.field = new Field(data);
			JButton plus = new JButton("+");
			plus.addActionListener((event)->{
				Object arr = JOptionPane.showInputDialog(ChooseField.this, "Select an item to add into the field.", "Add", JOptionPane.PLAIN_MESSAGE, null, field.exclude.toArray((E[]) new Object[0]), null);
				if(arr == null)
					return;
				field.add((E) arr);
			});
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(field, 0, 0).setInnerPad(FIXED_FIELD_WIDTH, 300).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 0));
			layouter.put(layouter.instanceOf(plus, 1, 0).setInnerPad(10, 10).setAnchor(Anchor.TOP).setFill(Fill.NONE).setWeight(0, 100).setInsets(10, 10, 0, 10));
		}
		
		public Collection<E> chosen(){
			return field.include;
		}
		
		private class Field extends JPanel{
			
			final ArrayList<E>          exclude;
			final ArrayList<E>          include;
			final Layouter.FlowLayouter layouter;
			final ColorBank             bank;
			
			public Field(Collection<E> data){
				this.include = new ArrayList<>();
				this.exclude = new ArrayList<>(data);
				this.layouter = new FlowLayouter(this, FlowAlignment.LEFT, 10, 10, false, false);
				this.bank = new ColorBank();
			}
			
			public void add(E e){
				Item item = new Item(e, bank.pollRandomColor());
				exclude.remove(e);
				include.add(e);
				layouter.add(item);
				this.revalidate();
				this.repaint();
			}
			
			public void remove(Item item){
				exclude.add(item.item);
				include.remove(item.item);
				layouter.remove(item);
				this.revalidate();
				this.repaint();
			}
		}
		
		private class Item extends JLabel implements MouseListener{
			
			final   E       item;
			final   Color   color;
			private boolean hovering;
			
			public Item(E item, Color color){
				this.item = item;
				this.color = color;
				this.setText(item.toString());
				this.setHorizontalAlignment(CENTER);
				this.setVerticalAlignment(CENTER);
				this.addMouseListener(this);
				this.hovering = false;
				Dimension dimension = this.getSize();
				dimension.setSize(dimension.width + 50, dimension.height + 30);
//				this.setMinimumSize(new Dimension(20, 150));
//				this.setMaximumSize(new Dimension(1000, 150));
				ToolBox.setPreferredSize(this, dimension);
			}
			
			@Override
			public void paint(Graphics g){
				Graphics2D g2        = (Graphics2D) g;
				double     curvature = this.getHeight() / 2.0d;
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
