package mccanny.visual.dialog;

import homelet.GH.StringDrawer.StringDrawer.StringDrawer;
import homelet.GH.StringDrawer.StringDrawer.StringDrawer.LinePolicy;
import homelet.GH.handlers.GH;
import homelet.GH.handlers.Layouter;
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
import mccanny.util.MyFlowLayout;
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
import java.awt.event.*;
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
		super("CoursePeriod");
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
			PeriodModule         periodField  = new PeriodModule();
			ChooseField<Teacher> teacherField = new ChooseField<>("Teacher", Teacher.teachers(), period == null ? null : period.teachers());
			ChooseField<Student> studentField = new ChooseField<>("Student", Student.students(), period == null ? null : period.students());
			if(period != null){
				basicField.courseField.setSelectedItem(period.course());
				basicField.classroomNumberField.processValue(period.classroom());
				periodField.weekdayField.setSelectedItem(period.weekday());
				periodField.startField.processValue(period.start());
				periodField.endField.processValue(period.end());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				Course              courseValue          = (Course) basicField.courseField.getSelectedItem();
				int                 classRoomNumberValue = (int) basicField.classroomNumberField.value();
				Weekday             weekdayValue         = (Weekday) periodField.weekdayField.getSelectedItem();
				double              startValue           = periodField.startField.value();
				double              endValue             = periodField.endField.value();
				Collection<Teacher> teacherValue         = teacherField.chosen();
				Collection<Student> studentValue         = studentField.chosen();
				if(endValue - startValue <= 0){
					JOptionPane.showMessageDialog(PeriodInfoDialog.this, "Period requires at least 0.1 h!", "Period Too Short Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				if(teacherValue.size() == 0){
					int result = JOptionPane.showConfirmDialog(PeriodInfoDialog.this, "There is no teacher Associated.\nAre you sure to proceed?", "No Teacher Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
					switch(result){
						case JOptionPane.CANCEL_OPTION:
							return;
						default:
						case JOptionPane.OK_OPTION:
					}
				}
				if(period == null){
					try{
						period = new CoursePeriod(courseValue, classRoomNumberValue, weekdayValue, startValue, endValue);
						period.addTeacher(true, teacherValue);
						period.addStudent(true, studentValue);
						Display.getInstance().manager().add(period);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Creating CoursePeriod", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						period.course(courseValue);
						period.classroom(classRoomNumberValue);
						period.weekday(weekdayValue);
						period.period(startValue, endValue);
						period.replaceTeacher(teacherField.chosen());
						period.replaceStudent(studentField.chosen());
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Changing CoursePeriodInfo", JOptionPane.ERROR_MESSAGE, null);
					}
				}
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			announcement.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			announcement.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			announcement.setBorder(BorderFactory.createTitledBorder(DEFAULT_BORDER, "Announcement", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD));
			Layouter.SpringLayouter layouter = new SpringLayouter(this);
			layouter.put(Position.CONSTRAIN_X, basicField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, basicField, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, basicField, -10, Position.CONSTRAIN_X, announcement);
			layouter.put(Position.CONSTRAIN_Y, announcement, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, announcement, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_X, periodField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, periodField, 10, Position.CONSTRAIN_Y_HEIGHT, basicField);
			layouter.put(Position.CONSTRAIN_X_WIDTH, periodField, 0, Position.CONSTRAIN_X_WIDTH, basicField);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, periodField, 0, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_X, teacherField, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, teacherField, 10, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_X, studentField, 10, Position.CONSTRAIN_X_WIDTH, teacherField);
			layouter.put(Position.CONSTRAIN_Y, studentField, 10, Position.CONSTRAIN_Y_HEIGHT, announcement);
			layouter.put(Position.CONSTRAIN_X_WIDTH, studentField, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, teacherField, 0, Position.CONSTRAIN_Y_HEIGHT, studentField);
			layouter.put(Position.CONSTRAIN_Y, cancel, 10, Position.CONSTRAIN_Y_HEIGHT, teacherField);
			layouter.put(Position.CONSTRAIN_Y, confirm, 10, Position.CONSTRAIN_Y_HEIGHT, teacherField);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, cancel, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, confirm, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			// 730 * 830
			layouter.put(layouter.instanceOf(this).put(Position.VALUE_WIDTH, 730).put(Position.VALUE_HEIGHT, 880));
			layouter.put(layouter.instanceOf(announcement).put(Position.VALUE_WIDTH, 200).put(Position.VALUE_HEIGHT, 350));
			layouter.put(layouter.instanceOf(cancel).put(Position.CONSTRAIN_X, 235).put(Position.VALUE_WIDTH, 125).put(Position.VALUE_HEIGHT, 40));
			layouter.put(layouter.instanceOf(confirm).put(Position.CONSTRAIN_X, 370).put(Position.VALUE_WIDTH, 125).put(Position.VALUE_HEIGHT, 40));
		}
	}
	
	private class BasicModule extends JComponent implements JIndexedChooserHandler, ActionListener, ValueProcessor{
		
		final JComboBox<Course> courseField;
		final JIndexedChooser   classroomNumberField;
		final TitledBorder      border;
		
		public BasicModule(){
			this.border = BorderFactory.createTitledBorder(DEFAULT_BORDER, "", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD);
			this.setBorder(border);
			JLabel course          = new JLabel("Course");
			JLabel classroomNumber = new JLabel("Classroom Number");
			courseField = new JComboBox<>(Course.courses().toArray(new Course[0]));
			JIndexedChooserGroup group = new JIndexedChooserGroup(this);
			classroomNumberField = new JIndexedChooser(this, 1, 1, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, this);
			courseField.addActionListener(this);
			group.add(classroomNumberField);
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
			updateTitle();
		}
		
		@Override
		public String process(double value){
			return String.valueOf((int) Math.floor(value));
		}
		
		private void updateTitle(){
			this.border.setTitle(courseField.getSelectedItem() + " - Room " + process(classroomNumberField.value()));
			this.revalidate();
			this.repaint();
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			updateTitle();
		}
		
		@Override
		public void onTrigger(JIndexedChooserEvent e){
			updateTitle();
		}
	}
	
	private class PeriodModule extends JComponent implements ValueProcessor, JIndexedChooserHandler, ActionListener{
		
		final JComboBox<Weekday> weekdayField;
		final JIndexedChooser    startField;
		final JIndexedChooser    endField;
		final TitledBorder       border;
		
		PeriodModule(){
			this.border = BorderFactory.createTitledBorder(DEFAULT_BORDER, "Period", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD);
			this.setBorder(border);
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
			weekdayField.addActionListener(this);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(weekdayField, 0, 0, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(start, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(startField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(end, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 0));
			layouter.put(layouter.instanceOf(endField, 1, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			updateTitle();
		}
		
		@Override
		public String process(double value){
			return Utility.time(value, Display.FORMAT_24);
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
			updateTitle();
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			updateTitle();
		}
		
		private void updateTitle(){
			border.setTitle("Period - " + weekdayField.getSelectedItem() + ", " + Utility.time(startField.value(), Display.FORMAT_24) + "~" + Utility.time(endField.value(), Display.FORMAT_24) + " (" + (endField.value() - startField.value()) + "h)");
			this.revalidate();
			this.repaint();
		}
	}
	
	private class ChooseField<E extends ToolTipText> extends JScrollPane implements MouseMotionListener{
		
		final Color        color = Color.WHITE;
		final String       title;
		final Field        field;
		final TitledBorder border;
		
		public ChooseField(String title, Collection<E> data, Collection<E> added){
			this.title = title;
			ToolBox.setPreferredSize(this, 350, 450);
			this.field = new Field(data, added);
			this.border = BorderFactory.createTitledBorder(DEFAULT_BORDER, "", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD);
			this.setBorder(this.border);
			this.setViewportView(field);
			this.getVerticalScrollBar().setBlockIncrement(40);
			this.getVerticalScrollBar().setUnitIncrement(40);
			this.setAutoscrolls(true);
			this.addMouseMotionListener(this);
			this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
			this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
			updateTitle();
		}
		
		@Override
		public void mouseDragged(MouseEvent e){
			Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
			((JPanel) e.getSource()).scrollRectToVisible(r);
		}
		
		@Override
		public void mouseMoved(MouseEvent e){}
		
		void scrollToBottom(){
			SwingUtilities.invokeLater(()->{
				this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMaximum());
			});
		}
		
		void updateTitle(){
			this.border.setTitle(title + " - " + field.include.size() + " selected");
			this.revalidate();
			this.repaint();
		}
		
		public Collection<E> chosen(){
			return field.include;
		}
		
		private class Field extends JPanel implements MouseListener{
			
			final ArrayList<E> exclude;
			final ArrayList<E> include;
			final JPopupMenu   leftClickMenu;
			final StringDrawer notify;
			final MyFlowLayout layout;
			
			public Field(Collection<E> data, Collection<E> added){
				notify = new StringDrawer("Double Click To Add " + title);
				notify.setLinePolicy(LinePolicy.NEVER_BREAK);
				notify.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
				notify.setAlign(Alignment.CENTER);
				notify.setColor(new Color(0x999999));
				notify.setTextAlign(Alignment.TOP);
				layout = new MyFlowLayout(300, 5, 5);
				this.setLayout(layout);
				if(added != null){
					this.include = new ArrayList<>();
					this.exclude = new ArrayList<>();
					for(E e : data){
						if(added.contains(e)){
							include.add(e);
							this.add(new Item(e));
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
				add.addActionListener(e->this.addItem());
				remove.addActionListener(e->this.remove());
				clear.addActionListener(e->this.clear());
				this.leftClickMenu.add(properties);
				this.leftClickMenu.addSeparator();
				this.leftClickMenu.add(add);
				this.leftClickMenu.add(remove);
				this.leftClickMenu.addSeparator();
				this.leftClickMenu.add(clear);
			}
			
			public void addItem(E e){
				exclude.remove(e);
				include.add(e);
				this.add(new Item(e));
				updateTitle();
				syncBestSize();
				scrollToBottom();
			}
			
			public void removeItem(Item item){
				exclude.add(item.item);
				include.remove(item.item);
				this.remove(item);
				updateTitle();
				syncBestSize();
			}
			
			private void syncBestSize(){
				this.setSize(layout.prefDimension());
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2)
					addItem();
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
			
			private void addItem(){
				Object arr = JOptionPane.showInputDialog(ChooseField.this, "Select an item to addItem into the field.", "Add", JOptionPane.PLAIN_MESSAGE, null, exclude.toArray((E[]) new ToolTipText[0]), null);
				if(arr == null)
					return;
				addItem((E) arr);
			}
			
			private void remove(){
				JOptionPane.showMessageDialog(ChooseField.this, "To Remove a " + title + " simply RIGHT click on the " + title + " that you want to delete.", "Remove", JOptionPane.INFORMATION_MESSAGE, null);
			}
			
			private void clear(){
				exclude.addAll(include);
				include.clear();
				this.removeAll();
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
				syncBestSize();
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
					field.removeItem(this);
				else if(e.getButton() == MouseEvent.BUTTON1){
					if(this.item instanceof Student)
						StudentInfoDialog.showDialog((Student) item);
					else
						TeacherInfoDialog.showDialog((Teacher) item);
					this.setText(item.toString());
					this.setToolTipText(item.toolTip());
					syncBestSize();
					this.revalidate();
					this.repaint();
				}
			}
			
			void syncBestSize(){
				Dimension dimension = this.getUI().getPreferredSize(this);
				dimension.setSize(dimension.width + 20, dimension.height + 20);
				ToolBox.setPreferredSize(this, dimension);
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
