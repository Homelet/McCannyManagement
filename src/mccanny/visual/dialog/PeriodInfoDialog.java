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
import mccanny.visual.dialog.SelectionDialog.NewItemListener;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

public class PeriodInfoDialog extends InfoDialog<CoursePeriod>{
	
	public static CoursePeriod showInfoDialog(CoursePeriod coursePeriod){
		return showInfoDialog(Display.getInstance(), coursePeriod);
	}
	
	public static CoursePeriod showInfoDialog(Frame owner, CoursePeriod coursePeriod){
		PeriodInfoDialog dialog = new PeriodInfoDialog(owner, coursePeriod);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	@Override
	public CoursePeriod result(){
		return period;
	}
	
	public static CoursePeriod showInfoDialog(Dialog owner, CoursePeriod coursePeriod){
		PeriodInfoDialog dialog = new PeriodInfoDialog(owner, coursePeriod);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private final Border       DEFAULT_BORDER = BorderFactory.createLineBorder(Color.BLACK, 5, true);
	private       CoursePeriod period;
	private       NestedPanel  panel;
	
	private PeriodInfoDialog(Frame owner, CoursePeriod period){
		super(owner, "Course Period");
		init(period);
	}
	
	private void init(CoursePeriod period){
		this.period = period;
		panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	private PeriodInfoDialog(Dialog owner, CoursePeriod period){
		super(owner, "Course Period");
		init(period);
	}
	
	@Override
	protected Component firstFocus(){
		return panel.basicField.courseField;
	}
	
	private class NestedPanel extends JBasePanel{
		
		final BasicModule basicField;
		
		NestedPanel(){
			JInputArea announcement = new JInputArea("No Announcement Yet", false);
			basicField = new BasicModule();
			PeriodModule         periodField  = new PeriodModule();
			ChooseField<Teacher> teacherField = new ChooseField<>(Teacher.teachers(), period == null ? null : period.teachers(), Utility.TEACHER_FLAG);
			ChooseField<Student> studentField = new ChooseField<>(Student.students(), period == null ? null : period.students(), Utility.STUDENT_FLAG);
			if(period != null){
				basicField.courseField.setSelectedItem(period.course());
				basicField.classroomNumberField.processValue(period.classroom(), false);
				periodField.weekdayField.setSelectedItem(period.weekday());
				periodField.startField.processValue(period.start(), false);
				periodField.endField.processValue(period.end(), false);
			}
			JButton confirm = new JButton(period != null ? "Apply Changes" : "Create CoursePeriod");
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
						period.replaceTeacher(teacherValue);
						period.replaceStudent(studentValue);
						Display.getInstance().manager().add(period);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Creating CoursePeriod", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						period.course(courseValue);
						period.classroom(classRoomNumberValue);
						period.period(weekdayValue, startValue, endValue);
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
			layouter.put(Position.CONSTRAIN_X, cancel, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, cancel, 10, Position.CONSTRAIN_Y_HEIGHT, teacherField);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, cancel, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, cancel, -10, Position.CONSTRAIN_X, confirm);
			layouter.put(Position.CONSTRAIN_Y, confirm, 10, Position.CONSTRAIN_Y_HEIGHT, teacherField);
			layouter.put(Position.CONSTRAIN_X_WIDTH, confirm, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, confirm, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			// 726 * 814
			layouter.put(layouter.instanceOf(this).put(Position.VALUE_WIDTH, 726).put(Position.VALUE_HEIGHT, 824));
			layouter.put(layouter.instanceOf(announcement).put(Position.VALUE_WIDTH, 200).put(Position.VALUE_HEIGHT, 308));
			layouter.put(layouter.instanceOf(cancel).put(Position.VALUE_HEIGHT, FIXED_BUTTON_DIMENSION.height));
			layouter.put(layouter.instanceOf(confirm).put(Position.CONSTRAIN_X, 726 / 3).put(Position.VALUE_HEIGHT, FIXED_BUTTON_DIMENSION.height));
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
			JLabel classroomNumber = new JLabel("Classroom #");
			courseField = new JComboBox<>(Course.courses().toArray(new Course[0]));
			courseField.setToolTipText("Course for this Period.");
			JButton adjustCourse = new JButton("Adjust");
			JButton newCourse    = new JButton("New");
			adjustCourse.setToolTipText("Click To adjust Course Detail.");
			newCourse.setToolTipText("Click To add new Course.");
			classroomNumberField = new JIndexedChooser(this, 1, 1, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, this);
			courseField.addActionListener(this);
			adjustCourse.addActionListener(e->{
				CourseInfoDialog.showInfoDialog(PeriodInfoDialog.this, (Course) courseField.getSelectedItem());
				updateTitle();
			});
			newCourse.addActionListener(e->{
				CourseInfoDialog.showInfoDialog(PeriodInfoDialog.this, null);
				Object selected = courseField.getSelectedItem();
				courseField.setModel(new DefaultComboBoxModel<>(Course.courses().toArray(new Course[0])));
				courseField.setSelectedItem(selected);
			});
			classroomNumberField.setButtonText("Next Room", "Last Room");
			classroomNumberField.setButtonToolTipText("Switch to next Classroom.", "Switch to last Classroom.");
			courseField.setFont(Display.CLEAR_SANS_BOLD);
			adjustCourse.setFont(Display.CLEAR_SANS_BOLD);
			newCourse.setFont(Display.CLEAR_SANS_BOLD);
			course.setLabelFor(courseField);
			classroomNumber.setLabelFor(classroomNumberField);
			course.setHorizontalAlignment(JLabel.RIGHT);
			classroomNumber.setHorizontalAlignment(JLabel.RIGHT);
			course.setFont(Display.CLEAR_SANS_BOLD);
			classroomNumber.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(course, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(10, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(50, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(adjustCourse, 2, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(20, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(newCourse, 3, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(20, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(classroomNumber, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(10, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(classroomNumberField, 1, 1, 3, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(90, 100).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(courseField, 10, FIXED_FIELD_DIMENSION.height);
			ToolBox.setPreferredSize(adjustCourse, 25, FIXED_SQUARE_BUTTON_DIMENSION.height);
			ToolBox.setPreferredSize(newCourse, 25, FIXED_SQUARE_BUTTON_DIMENSION.height);
			ToolBox.setPreferredSize(classroomNumberField, FIXED_FIELD_DIMENSION);
			updateTitle();
		}
		
		private void updateTitle(){
			this.border.setTitle(courseField.getSelectedItem() + " - Room " + process(classroomNumberField.value()));
			this.revalidate();
			this.repaint();
		}
		
		@Override
		public String process(double value){
			return "Room " + (int) Math.floor(value);
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
			weekdayField.setToolTipText("Day of the week for this Period.");
			JIndexedChooserGroup group = new JIndexedChooserGroup(this);
			startField = new JIndexedChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			endField = new JIndexedChooser(this, 0.25, CoursePeriod.START_AT, CoursePeriod.END_AT, CoursePeriod.START_AT, Orientation.HORIZONTAL, this);
			startField.setButtonText("+ 15 min", "- 15 min");
			startField.setButtonToolTipText("Plus 15 minutes", "Minus 15 minutes");
			endField.setButtonText("+ 15 min", "- 15 min");
			endField.setButtonToolTipText("Plus 15 minutes", "Minus 15 minutes");
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
			ToolBox.setPreferredSize(weekdayField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(startField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(endField, FIXED_FIELD_DIMENSION);
			updateTitle();
		}
		
		private void updateTitle(){
			border.setTitle("Period - " + weekdayField.getSelectedItem() + ", " + Utility.time(startField.value(), Display.FORMAT_24) + "~" + Utility.time(endField.value(), Display.FORMAT_24) + " (" + (endField.value() - startField.value()) + "h)");
			this.revalidate();
			this.repaint();
		}
		
		@Override
		public String process(double value){
			return Utility.time(value, Display.FORMAT_24);
		}
		
		@Override
		public void onTrigger(JIndexedChooserEvent e){
			if(e.initiator() == startField){
				if(startField.value() > endField.value()){
					endField.processValue(startField.value(), false);
				}
			}else{
				if(startField.value() > endField.value()){
					startField.processValue(endField.value(), false);
				}
			}
			updateTitle();
		}
		
		@Override
		public void actionPerformed(ActionEvent e){
			updateTitle();
		}
	}
	
	private class ChooseField<E extends ToolTipText> extends JScrollPane{
		
		final Color        color = Color.WHITE;
		final String       title;
		final Field        field;
		final TitledBorder border;
		final int          flag;
		
		public ChooseField(Collection<E> data, Collection<E> added, int flag){
			this.title = Utility.flag(flag);
			this.flag = flag;
			ToolBox.setPreferredSize(this, 350, 450);
			this.field = new Field(data, added);
			this.border = BorderFactory.createTitledBorder(DEFAULT_BORDER, "", TitledBorder.LEADING, TitledBorder.BELOW_TOP, Display.CLEAR_SANS_BOLD);
			this.setBorder(this.border);
			this.setViewportView(field);
			this.getVerticalScrollBar().setUnitIncrement(30);
			this.getVerticalScrollBar().setBlockIncrement(30);
			this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
			this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
			this.setFocusable(true);
			updateTitle();
		}
		
		void updateTitle(){
			this.border.setTitle(title + " - " + field.include.size() + " selected");
			this.revalidate();
			this.repaint();
		}
		
		void scrollToBottom(){
			SwingUtilities.invokeLater(()->{
				this.getVerticalScrollBar().setValue(this.getVerticalScrollBar().getMaximum());
			});
		}
		
		public Collection<E> chosen(){
			return field.include;
		}
		
		private class Field extends JPanel implements MouseListener, NewItemListener{
			
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
				this.setToolTipText("Double Click to Add " + title + ", Left Click on any " + title + " to Adjust, Right click on any " + title + " removes them.");
			}
			
			private void addItem(){
				SelectionDialog selectionDialog = null;
				switch(flag){
					case Utility.STUDENT_FLAG:
						selectionDialog = SelectionDialog.showStudentDialog(PeriodInfoDialog.this, (Collection<Student>) include, (Collection<Student>) exclude, this);
						break;
					case Utility.TEACHER_FLAG:
						selectionDialog = SelectionDialog.showTeacherDialog(PeriodInfoDialog.this, (Collection<Teacher>) include, (Collection<Teacher>) exclude, this);
						break;
				}
				if(selectionDialog == null)
					return;
				if(selectionDialog.acceptResult())
					syncItem(selectionDialog.include(), selectionDialog.exclude());
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
				updateTitle();
				syncBestSize();
			}
			
			void syncItem(Collection include, Collection exclude){
				this.exclude.clear();
				this.include.clear();
				this.removeAll();
				this.exclude.addAll(exclude);
				this.include.addAll(include);
				for(E item : this.include){
					this.add(new Item(item));
				}
				updateTitle();
				syncBestSize();
				scrollToBottom();
			}
			
			private void syncBestSize(){
				this.setSize(layout.prefDimension());
			}
			
			public void removeItem(Item item){
				exclude.add(item.item);
				include.remove(item.item);
				this.remove(item);
				updateTitle();
				syncBestSize();
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
			
			@Override
			public void addNewItem(Object item){
				exclude.add((E) item);
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
				this.setFocusable(true);
				syncBestSize();
			}
			
			void syncBestSize(){
				Dimension dimension = this.getUI().getPreferredSize(this);
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
					field.removeItem(this);
				else if(e.getButton() == MouseEvent.BUTTON1){
					if(this.item instanceof Student)
						StudentInfoDialog.showInfoDialog(PeriodInfoDialog.this, (Student) item);
					else
						TeacherInfoDialog.showInfoDialog(PeriodInfoDialog.this, (Teacher) item);
					this.setText(item.toString());
					this.setToolTipText(item.toolTip());
					syncBestSize();
					this.revalidate();
					this.repaint();
				}
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
