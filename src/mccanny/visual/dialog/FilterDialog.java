package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.SpringLayouter;
import homelet.GH.handlers.Layouter.SpringLayouter.Position;
import homelet.GH.utils.ToolBox;
import mccanny.management.course.Course;
import mccanny.management.course.manager.Filter;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.visual.Display;
import mccanny.visual.dialog.SelectionDialog.NewItemListener;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FilterDialog extends InfoDialog<Filter>{
	
	public static Filter showInfoDialog(){
		return showInfoDialog(Display.getInstance());
	}
	
	public static Filter showInfoDialog(Frame owner){
		FilterDialog dialog = new FilterDialog(owner, Display.getInstance().manager().timeTable().filter());
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	public static Filter showInfoDialog(Dialog owner){
		FilterDialog dialog = new FilterDialog(owner, Display.getInstance().manager().timeTable().filter());
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private Filter      filter;
	private NestedPanel panel;
	
	private FilterDialog(Frame frameOwner, Filter filter){
		super(frameOwner, "Filter");
		init(filter);
	}
	
	private void init(Filter filter){
		this.filter = filter;
		panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	private FilterDialog(Dialog frameOwner, Filter filter){
		super(frameOwner, "Filter");
		init(filter);
	}
	
	@Override
	public Filter result(){
		return filter;
	}
	
	@Override
	protected Component firstFocus(){
		return panel.confirm;
	}
	
	class NestedPanel extends JBasePanel{
		
		final JButton confirm;
		
		NestedPanel(){
			JLabel                  showing_all_coursePeriod_ = new JLabel("Showing All CoursePeriod Who ");
			PolarLabel              polar                     = new PolarLabel(filter != null ? filter.polar : Filter.POSITIVE);
			SelectionLabel<Student> students                  = new SelectionLabel<>(Utility.STUDENT_FLAG, filter != null ? filter.students : null);
			SelectionLabel<Teacher> teachers                  = new SelectionLabel<>(Utility.TEACHER_FLAG, filter != null ? filter.teachers : null);
			SelectionLabel<Course>  course                    = new SelectionLabel<>(Utility.COURSE_FLAG, filter != null ? filter.courses : null);
			confirm = new JButton("Apply Filter");
			JButton cancel = new JButton("Cancel");
			confirm.addActionListener((action)->{
				filter = Filter.createFilter(polar.polar(), course.include, students.include, teachers.include);
				Display.getInstance().manager().applyFilter(filter);
				closeDialog();
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			showing_all_coursePeriod_.setFont(Display.CLEAR_SANS_BOLD.deriveFont(25.0f));
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.SpringLayouter layouter = new SpringLayouter(this);
			layouter.put(Position.CONSTRAIN_X, showing_all_coursePeriod_, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, showing_all_coursePeriod_, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X, polar, 0, Position.CONSTRAIN_X_WIDTH, showing_all_coursePeriod_);
			layouter.put(Position.CONSTRAIN_Y, polar, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, polar, 0, Position.CONSTRAIN_Y_HEIGHT, showing_all_coursePeriod_);
			layouter.put(Position.CONSTRAIN_X_WIDTH, polar, -10, Position.CONSTRAIN_X_WIDTH, this);
			//
			layouter.put(Position.CONSTRAIN_X, students, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, students, 10, Position.CONSTRAIN_Y_HEIGHT, showing_all_coursePeriod_);
			layouter.put(Position.CONSTRAIN_X_WIDTH, students, -10, Position.CONSTRAIN_X_WIDTH, this);
			//
			layouter.put(Position.CONSTRAIN_X, teachers, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, teachers, 10, Position.CONSTRAIN_Y_HEIGHT, students);
			layouter.put(Position.CONSTRAIN_X_WIDTH, teachers, -10, Position.CONSTRAIN_X_WIDTH, this);
			//
			layouter.put(Position.CONSTRAIN_X, course, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, course, 10, Position.CONSTRAIN_Y_HEIGHT, teachers);
			layouter.put(Position.CONSTRAIN_X_WIDTH, course, -10, Position.CONSTRAIN_X_WIDTH, this);
			//
			layouter.put(Position.CONSTRAIN_X, cancel, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, cancel, 10, Position.CONSTRAIN_Y_HEIGHT, course);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, cancel, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, cancel, -10, Position.CONSTRAIN_X, confirm);
			layouter.put(Position.CONSTRAIN_Y, confirm, 10, Position.CONSTRAIN_Y_HEIGHT, course);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, confirm, -10, Position.CONSTRAIN_Y_HEIGHT, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, confirm, -10, Position.CONSTRAIN_X_WIDTH, this);
			// 560 * 190
			ToolBox.setPreferredSize(this, 560, 190);
			layouter.put(layouter.instanceOf(cancel).put(Position.VALUE_HEIGHT, FIXED_BUTTON_DIMENSION.height));
			layouter.put(layouter.instanceOf(confirm).put(Position.CONSTRAIN_X, 560 / 3).put(Position.VALUE_HEIGHT, FIXED_BUTTON_DIMENSION.height));
		}
		
		class PolarLabel extends JLabel implements MouseListener{
			
			final   String[] includes_exclude = new String[]{ "Contains", "Don't Contains" };
			final   Color[]  colors           = new Color[]{ Display.McCANNY_BLUE, Color.RED };
			private boolean  polar;
			
			public PolarLabel(boolean polar){
				this.addMouseListener(this);
				this.setFont(Display.CLEAR_SANS_BOLD.deriveFont(25.0f));
				polar(polar);
			}
			
			private void polar(boolean polar){
				this.polar = polar;
				this.setText(polar ? includes_exclude[0] : includes_exclude[1]);
				this.setForeground(polar ? colors[0] : colors[1]);
			}
			
			private boolean polar(){
				return polar;
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				toggle();
			}
			
			private void toggle(){
				polar(!polar);
			}
			
			@Override
			public void mousePressed(MouseEvent e){}
			
			@Override
			public void mouseReleased(MouseEvent e){}
			
			@Override
			public void mouseEntered(MouseEvent e){}
			
			@Override
			public void mouseExited(MouseEvent e){}
		}
		
		class SelectionLabel<E> extends JLabel implements MouseListener, NewItemListener{
			
			private final int          flag;
			private final ArrayList<E> include;
			private final ArrayList<E> exclude;
			private final String       emptyMessage;
			
			public SelectionLabel(int flag, E[] include){
				this.addMouseListener(this);
				this.emptyMessage = "Click here to add " + Utility.flag(flag);
				this.flag = flag;
				if(include != null){
					this.include = new ArrayList<>(Arrays.asList(include));
				}else{
					this.include = new ArrayList<>();
				}
				this.exclude = new ArrayList<>();
				switch(flag){
					case Utility.STUDENT_FLAG:
						for(Student student : Student.students()){
							if(!this.include.contains(student)){
								exclude.add((E) student);
							}
						}
						break;
					case Utility.TEACHER_FLAG:
						for(Teacher teacher : Teacher.teachers()){
							if(!this.include.contains(teacher)){
								exclude.add((E) teacher);
							}
						}
						break;
					case Utility.COURSE_FLAG:
						for(Course course : Course.courses()){
							if(!this.include.contains(course)){
								exclude.add((E) course);
							}
						}
						break;
				}
				this.setFont(Display.CLEAR_SANS_BOLD);
				update();
			}
			
			void update(){
				this.setText(Utility.flag(flag) + ": " + (include.isEmpty() ? emptyMessage : Utility.toString(include, 5)));
			}
			
			@Override
			public void addNewItem(Object item){
				exclude.add((E) item);
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				addItem();
			}
			
			void addItem(){
				SelectionDialog dialog = null;
				switch(flag){
					case Utility.STUDENT_FLAG:
						dialog = SelectionDialog.showStudentDialog(FilterDialog.this, (Collection<Student>) include, (Collection<Student>) exclude, this);
						break;
					case Utility.TEACHER_FLAG:
						dialog = SelectionDialog.showTeacherDialog(FilterDialog.this, (Collection<Teacher>) include, (Collection<Teacher>) exclude, this);
						break;
					case Utility.COURSE_FLAG:
						dialog = SelectionDialog.showCourseDialog(FilterDialog.this, (Collection<Course>) include, (Collection<Course>) exclude, this);
						break;
				}
				if(dialog == null)
					return;
				if(dialog.acceptResult())
					sync(dialog.include(), dialog.exclude());
			}
			
			void sync(Collection include, Collection exclude){
				this.exclude.clear();
				this.include.clear();
				this.exclude.addAll(exclude);
				this.include.addAll(include);
				update();
			}
			
			@Override
			public void mousePressed(MouseEvent e){
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
			}
			
			@Override
			public void mouseEntered(MouseEvent e){
			}
			
			@Override
			public void mouseExited(MouseEvent e){
			}
		}
	}
}
