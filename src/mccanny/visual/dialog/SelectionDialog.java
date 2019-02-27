package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;

public class SelectionDialog<E> extends InfoDialog<Collection<E>>{
	
	private static final String[]        STUDENT_COLUMN_HEADER = new String[]{ null, "Identity", "OEN" };
	private static final String[]        TEACHER_COLUMN_HEADER = new String[]{ null, "Identity", "MEN" };
	private static final String[]        COURSE_COLUMN_HEADER  = new String[]{ null, "CourseID", "CourseHour" };
	private              ArrayList<E>    include;
	private              ArrayList<E>    exclude;
	private              int             flag;
	private              String          title;
	private              boolean         shiftDown             = false;
	private              NewItemListener newItemListener       = null;
	private              boolean         acceptResult          = false;
	
	private SelectionDialog(Frame frameOwner, String[] columnHeader, Collection<E> include, Collection<E> exclude, int flag, NewItemListener itemListener){
		super(frameOwner, "Select " + Utility.flag(flag));
		init(Utility.flag(flag), columnHeader, include, exclude, flag, itemListener);
	}
	
	private void init(String title, String[] columnHeader, Collection<E> include, Collection<E> exclude, int flag, NewItemListener itemListener){
		this.title = title;
		this.flag = flag;
		this.newItemListener = itemListener;
		this.include = new ArrayList<>(include);
		this.exclude = new ArrayList<>(exclude);
		NestedPanel nestedPanel = new NestedPanel(columnHeader);
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	private SelectionDialog(Dialog frameOwner, String[] columnHeader, Collection<E> include, Collection<E> exclude, int flag, NewItemListener itemListener){
		super(frameOwner, "Select " + Utility.flag(flag));
		init(Utility.flag(flag), columnHeader, include, exclude, flag, itemListener);
	}
	
	public static SelectionDialog<Student> showStudentDialog(Collection<Student> include, Collection<Student> exclude, NewItemListener itemListener){
		return showStudentDialog(Display.getInstance(), include, exclude, itemListener);
	}
	
	public static SelectionDialog<Student> showStudentDialog(Frame frameOwner, Collection<Student> include, Collection<Student> exclude, NewItemListener itemListener){
		SelectionDialog<Student> studentSelectionDialog = new SelectionDialog<>(frameOwner, STUDENT_COLUMN_HEADER, include, exclude, Utility.STUDENT_FLAG, itemListener);
		studentSelectionDialog.showDialog();
		studentSelectionDialog.removeDialog();
		return studentSelectionDialog;
	}
	
	public static SelectionDialog<Student> showStudentDialog(Dialog frameOwner, Collection<Student> include, Collection<Student> exclude, NewItemListener itemListener){
		SelectionDialog<Student> studentSelectionDialog = new SelectionDialog<>(frameOwner, STUDENT_COLUMN_HEADER, include, exclude, Utility.STUDENT_FLAG, itemListener);
		studentSelectionDialog.showDialog();
		studentSelectionDialog.removeDialog();
		return studentSelectionDialog;
	}
	
	public static SelectionDialog<Teacher> showTeacherDialog(Collection<Teacher> include, Collection<Teacher> exclude, NewItemListener itemListener){
		return showTeacherDialog(Display.getInstance(), include, exclude, itemListener);
	}
	
	public static SelectionDialog<Teacher> showTeacherDialog(Frame frameOwner, Collection<Teacher> include, Collection<Teacher> exclude, NewItemListener itemListener){
		SelectionDialog<Teacher> teacherSelectionDialog = new SelectionDialog<>(frameOwner, TEACHER_COLUMN_HEADER, include, exclude, Utility.TEACHER_FLAG, itemListener);
		teacherSelectionDialog.showDialog();
		teacherSelectionDialog.removeDialog();
		return teacherSelectionDialog;
	}
	
	public static SelectionDialog<Teacher> showTeacherDialog(Dialog frameOwner, Collection<Teacher> include, Collection<Teacher> exclude, NewItemListener itemListener){
		SelectionDialog<Teacher> teacherSelectionDialog = new SelectionDialog<>(frameOwner, TEACHER_COLUMN_HEADER, include, exclude, Utility.TEACHER_FLAG, itemListener);
		teacherSelectionDialog.showDialog();
		teacherSelectionDialog.removeDialog();
		return teacherSelectionDialog;
	}
	
	public static SelectionDialog<Course> showCourseDialog(Collection<Course> include, Collection<Course> exclude, NewItemListener itemListener){
		return showCourseDialog(Display.getInstance(), include, exclude, itemListener);
	}
	
	public static SelectionDialog<Course> showCourseDialog(Frame frameOwner, Collection<Course> include, Collection<Course> exclude, NewItemListener itemListener){
		SelectionDialog<Course> courseSelectionDialog = new SelectionDialog<>(frameOwner, COURSE_COLUMN_HEADER, include, exclude, Utility.COURSE_FLAG, itemListener);
		courseSelectionDialog.showDialog();
		courseSelectionDialog.removeDialog();
		return courseSelectionDialog;
	}
	
	public static SelectionDialog<Course> showCourseDialog(Dialog frameOwner, Collection<Course> include, Collection<Course> exclude, NewItemListener itemListener){
		SelectionDialog<Course> courseSelectionDialog = new SelectionDialog<>(frameOwner, COURSE_COLUMN_HEADER, include, exclude, Utility.COURSE_FLAG, itemListener);
		courseSelectionDialog.showDialog();
		courseSelectionDialog.removeDialog();
		return courseSelectionDialog;
	}
	
	@Override
	public Collection<E> result(){
		return include();
	}
	
	public Collection<E> include(){
		return include;
	}
	
	public Collection<E> exclude(){
		return exclude;
	}
	
	public boolean acceptResult(){
		return acceptResult;
	}
	
	public interface NewItemListener{
		
		void addNewItem(Object item);
	}
	
	class NestedPanel extends JBasePanel{
		
		public NestedPanel(String[] columnHeader){
			SelectionTable table          = new SelectionTable(columnHeader);
			JLabel         description    = new JLabel("Check the " + title + " that you want to Select.");
			JButton        newItem        = new JButton("New " + title);
			JButton        selectAll      = new JButton("Select All");
			JButton        clearSelection = new JButton("Clear Selection");
			JButton        confirm        = new JButton("Confirm");
			JButton        cancel         = new JButton("Cancel");
			confirm.addActionListener((action)->{
				acceptResult = true;
				closeDialog();
			});
			cancel.addActionListener((action)->{
				acceptResult = false;
				closeDialog();
			});
			description.setFont(Display.CLEAR_SANS_BOLD);
			newItem.setFont(Display.CLEAR_SANS_BOLD);
			newItem.setToolTipText("Click to create an new " + title + ".");
			selectAll.setFont(Display.CLEAR_SANS_BOLD);
			selectAll.setToolTipText("Click to select all " + title + ".");
			clearSelection.setFont(Display.CLEAR_SANS_BOLD);
			clearSelection.setToolTipText("Click to clear all selection.");
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			newItem.addActionListener((e)->table.newItem());
			selectAll.addActionListener((e)->table.selectAll());
			clearSelection.addActionListener((e)->table.clearSelection());
			GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(description, 0, 0, 6, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(newItem, 0, 1, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(selectAll, 2, 1, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(clearSelection, 4, 1, 2, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(table, 0, 2, 6, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 3, 3, 1).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 3, 3, 3, 1).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(newItem, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(selectAll, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(clearSelection, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(confirm, FIXED_BUTTON_DIMENSION);
		}
	}
	
	class SelectionTable extends JScrollPane implements MouseListener, KeyListener, FocusListener{
		
		final JTable   table;
		final String[] columnHeader;
		TableModule tableModule;
		
		SelectionTable(String[] columnHeader){
			this.columnHeader = columnHeader;
			table = new JTable();
			syncTableModule();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			table.setDragEnabled(false);
			table.setRowHeight(20);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(this);
			table.addKeyListener(this);
			table.addFocusListener(this);
			this.setViewportView(table);
		}
		
		void syncTableModule(){
			tableModule = new TableModule(columnHeader);
			table.setModel(tableModule);
			table.setRowSorter(new TableRowSorter<>(tableModule));
			table.getRowSorter().toggleSortOrder(1);
			TableColumnModel model = table.getColumnModel();
			model.getColumn(0).setResizable(false);
			model.getColumn(1).setResizable(false);
			model.getColumn(2).setResizable(false);
			model.getColumn(0).setMinWidth(20);
			model.getColumn(0).setMaxWidth(20);
			model.getColumn(0).setPreferredWidth(20);
		}
		
		@Override
		public void focusGained(FocusEvent e){
		}
		
		@Override
		public void focusLost(FocusEvent e){
			shiftDown = false;
		}
		
		@Override
		public void mouseClicked(MouseEvent e){}
		
		/*
		 *
		 */
		@Override
		public void mousePressed(MouseEvent e){
			if(shiftDown){
				Object value = tableModule.data[table.convertRowIndexToModel(table.getSelectedRow())][0];
				switch(flag){
					case Utility.TEACHER_FLAG:
						TeacherInfoDialog.showInfoDialog(SelectionDialog.this, (Teacher) value);
						break;
					case Utility.STUDENT_FLAG:
						StudentInfoDialog.showInfoDialog(SelectionDialog.this, (Student) value);
						break;
					case Utility.COURSE_FLAG:
						CourseInfoDialog.showInfoDialog(SelectionDialog.this, (Course) value);
						break;
				}
				syncTableModule();
			}else if(e.getButton() == MouseEvent.BUTTON1){
				toggleSelected();
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e){}
		
		@Override
		public void mouseEntered(MouseEvent e){}
		
		@Override
		public void mouseExited(MouseEvent e){}
		
		void toggleSelected(){
			table.setValueAt(!(boolean) table.getValueAt(table.getSelectedRow(), 0), table.getSelectedRow(), 0);
			table.revalidate();
			table.repaint();
		}
		
		@Override
		public void keyTyped(KeyEvent e){
		}
		
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				toggleSelected();
			}
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				shiftDown = true;
		}
		
		@Override
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				shiftDown = false;
		}
		
		void selectAll(){
			for(int index = 0; index < table.getRowCount(); index++)
				table.setValueAt(Boolean.TRUE, index, 1);
			table.revalidate();
			table.repaint();
		}
		
		void clearSelection(){
			for(int index = 0; index < table.getRowCount(); index++)
				table.setValueAt(Boolean.FALSE, index, 1);
			table.revalidate();
			table.repaint();
		}
		
		void newItem(){
			E newItem = null;
			switch(flag){
				case Utility.TEACHER_FLAG:
					newItem = (E) TeacherInfoDialog.showInfoDialog(SelectionDialog.this, null);
					break;
				case Utility.STUDENT_FLAG:
					newItem = (E) StudentInfoDialog.showInfoDialog(SelectionDialog.this, null);
					break;
				case Utility.COURSE_FLAG:
					newItem = (E) CourseInfoDialog.showInfoDialog(SelectionDialog.this, null);
					break;
			}
			if(newItem == null)
				return;
			if(newItemListener != null)
				newItemListener.addNewItem(newItem);
			exclude.add(newItem);
			syncTableModule();
		}
	}
	
	class TableModule extends AbstractTableModel{
		
		private final String[]   columnName;
		// row, col
		private       Object[][] data;
		
		TableModule(String[] columnName){
			this.columnName = columnName;
			initializeData();
		}
		
		synchronized void initializeData(){
			data = new Object[include.size() + exclude.size()][columnName.length + 1];
			int row = 0;
			switch(flag){
				case Utility.STUDENT_FLAG:
					for(E e : include){
						Student item = (Student) e;
						data[row][0] = item;
						data[row][1] = Boolean.TRUE;
						data[row][2] = item.identity();
						data[row][3] = item.OEN();
						row++;
					}
					for(E e : exclude){
						Student item = (Student) e;
						data[row][0] = item;
						data[row][1] = Boolean.FALSE;
						data[row][2] = item.identity();
						data[row][3] = item.OEN();
						row++;
					}
					break;
				case Utility.TEACHER_FLAG:
					for(E e : include){
						Teacher item = (Teacher) e;
						data[row][0] = item;
						data[row][1] = Boolean.TRUE;
						data[row][2] = item.identity();
						data[row][3] = item.MEN();
						row++;
					}
					for(E e : exclude){
						Teacher item = (Teacher) e;
						data[row][0] = item;
						data[row][1] = Boolean.FALSE;
						data[row][2] = item.identity();
						data[row][3] = item.MEN();
						row++;
					}
					break;
				case Utility.COURSE_FLAG:
					for(E e : include){
						Course item = (Course) e;
						data[row][0] = item;
						data[row][1] = Boolean.TRUE;
						data[row][2] = item.courseID();
						data[row][3] = item.courseHour();
						row++;
					}
					for(E e : exclude){
						Course item = (Course) e;
						data[row][0] = item;
						data[row][1] = Boolean.FALSE;
						data[row][2] = item.courseID();
						data[row][3] = item.courseHour();
						row++;
					}
					break;
			}
		}
		
		@Override
		public int getRowCount(){
			return data.length;
		}
		
		@Override
		public int getColumnCount(){
			return columnName.length;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex){
			return data[rowIndex][columnIndex + 1];
		}
		
		@Override
		public String getColumnName(int columnIndex){
			return columnName[columnIndex];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex){
			return data[0][columnIndex + 1].getClass();
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
			return false;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			boolean oldValue = (boolean) data[rowIndex][1];
			boolean newValue = (boolean) aValue;
			// if a value has changed, it also need to synchronize it corresponding buffer
			if(oldValue){
				if(!newValue){
					if(include.remove(data[rowIndex][0])){
						exclude.add((E) data[rowIndex][0]);
					}else{
						throw new IllegalArgumentException("Unexpected exception");
					}
				}
			}else{
				if(newValue){
					if(exclude.remove(data[rowIndex][0])){
						include.add((E) data[rowIndex][0]);
					}else{
						throw new IllegalArgumentException("Unexpected exception");
					}
				}
			}
			data[rowIndex][1] = newValue;
		}
	}
}
