package mccanny.visual.infoCenter;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.SpringLayouter;
import homelet.GH.handlers.Layouter.SpringLayouter.Position;
import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;
import mccanny.visual.Display;
import mccanny.visual.dialog.CourseInfoDialog;
import mccanny.visual.dialog.StudentInfoDialog;
import mccanny.visual.dialog.TeacherInfoDialog;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;

public class InformationCenter extends JDialog{
	
	public static void showInformationCenter(){
		informationCenter.showDialog();
	}
	
	private void showDialog(){
		Display.getInstance().manager().lock();
		syncAll();
		this.setLocation(Utility.frameVertex(Display.getInstance().getBounds(), this.getBounds()));
		this.setVisible(true);
		Display.getInstance().manager().unlock();
	}
	
	private void syncAll(){
		panel.studentPanel.syncTableModule();
		panel.teacherPanel.syncTableModule();
		panel.coursePanel.syncTableModule();
	}
	
	private static final String[]          STUDENT_HEADER    = new String[]{ "Identity", "OEN", "Birthday", "Email" };
	private static final String[]          TEACHER_HEADER    = new String[]{ "Identity", "MEN", "Birthday", "Email" };
	private static final String[]          COURSE_HEADER     = new String[]{ "Course ID", "Course Hour" };
	private static final InformationCenter informationCenter = new InformationCenter();
	private final        NestedPanel       panel;
	
	private InformationCenter(){
		super(Display.getInstance(), "Information Center", true);
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
		panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	private void closeDialog(){
		this.setVisible(false);
	}
	
	class NestedPanel extends JBasePanel{
		
		final JTabbedPane      tabbedPane;
		final InformationPanel studentPanel;
		final InformationPanel teacherPanel;
		final InformationPanel coursePanel;
		
		NestedPanel(){
			JLabel    title       = new JLabel("Information Center");
			JLabel    description = new JLabel("This is a place for all information stored in the system.");
			JButton   newItem     = new JButton("New");
			JCheckBox checkBox    = new JCheckBox("Mass Production Mode");
			description.setToolTipText("<html>SHIFT + right click on any item to adjust it's properties.<br>BACKSPACE or DELETE removes the item.</html>");
			newItem.setToolTipText("Click to create an new Item.");
			checkBox.setToolTipText("<html>Mass Production mode is helpful when adding multiple item<br>By checking this mode, the new Item promote is continuously bring up and until an cancel is pressed.</html>");
			title.setFont(Display.CLEAR_SANS_BOLD.deriveFont(30.0f));
			description.setFont(Display.CLEAR_SANS_BOLD);
			newItem.setFont(Display.CLEAR_SANS_BOLD);
			checkBox.setFont(Display.CLEAR_SANS_BOLD.deriveFont(15.0f));
			newItem.addActionListener(e->newItem(checkBox.isSelected()));
			tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
			studentPanel = new InformationPanel(Utility.STUDENT_FLAG);
			teacherPanel = new InformationPanel(Utility.TEACHER_FLAG);
			coursePanel = new InformationPanel(Utility.COURSE_FLAG);
			tabbedPane.addTab("Student", studentPanel);
			tabbedPane.addTab("Teacher", teacherPanel);
			tabbedPane.addTab("Course", coursePanel);
			Layouter.SpringLayouter layouter = new SpringLayouter(this);
			layouter.put(Position.CONSTRAIN_X, title, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, title, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X, description, 10, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, description, 5, Position.CONSTRAIN_Y_HEIGHT, title);
			layouter.put(Position.CONSTRAIN_X_WIDTH, description, 0, Position.CONSTRAIN_X_WIDTH, title);
			layouter.put(Position.CONSTRAIN_X_WIDTH, title, -10, Position.CONSTRAIN_X, checkBox);
			layouter.put(Position.CONSTRAIN_Y, checkBox, 10, Position.CONSTRAIN_Y, this);
			layouter.put(Position.CONSTRAIN_X_WIDTH, checkBox, -10, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_X, newItem, 0, Position.CONSTRAIN_X, checkBox);
			layouter.put(Position.CONSTRAIN_Y, newItem, 0, Position.CONSTRAIN_Y_HEIGHT, checkBox);
			layouter.put(Position.CONSTRAIN_X_WIDTH, newItem, 0, Position.CONSTRAIN_X_WIDTH, checkBox);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, newItem, 0, Position.CONSTRAIN_Y_HEIGHT, description);
			layouter.put(Position.CONSTRAIN_X, tabbedPane, 0, Position.CONSTRAIN_X, this);
			layouter.put(Position.CONSTRAIN_Y, tabbedPane, 10, Position.CONSTRAIN_Y_HEIGHT, description);
			layouter.put(Position.CONSTRAIN_X_WIDTH, tabbedPane, 0, Position.CONSTRAIN_X_WIDTH, this);
			layouter.put(Position.CONSTRAIN_Y_HEIGHT, tabbedPane, 0, Position.CONSTRAIN_Y_HEIGHT, this);
			//
			layouter.put(layouter.instanceOf(this).put(Position.VALUE_WIDTH, 730).put(Position.VALUE_HEIGHT, 500));
			layouter.put(layouter.instanceOf(title).put(Position.VALUE_WIDTH, 500).put(Position.VALUE_HEIGHT, 40));
			layouter.put(layouter.instanceOf(checkBox).put(Position.VALUE_WIDTH, 200).put(Position.VALUE_HEIGHT, 30));
		}
		
		void newItem(boolean massProduction){
			do{
				if(((InformationPanel) tabbedPane.getSelectedComponent()).newItem() == null)
					break;
			}while(massProduction);
		}
	}
	
	class InformationPanel extends JScrollPane implements MouseListener, KeyListener, FocusListener{
		
		final JTable   table;
		final String[] columnHeader;
		final int      flag;
		TableModule tableModule;
		private boolean shiftDown = false;
		
		InformationPanel(int flag){
			this.flag = flag;
			switch(flag){
				default:
				case Utility.STUDENT_FLAG:
					this.columnHeader = STUDENT_HEADER;
					break;
				case Utility.TEACHER_FLAG:
					this.columnHeader = TEACHER_HEADER;
					break;
				case Utility.COURSE_FLAG:
					this.columnHeader = COURSE_HEADER;
					break;
			}
			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.setRowHeight(20);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(this);
			table.addKeyListener(this);
			table.addFocusListener(this);
			this.setFocusable(true);
			this.setViewportView(table);
		}
		
		@Override
		public void focusGained(FocusEvent e){
		}
		
		@Override
		public void focusLost(FocusEvent e){
			shiftDown = false;
		}
		
		@Override
		public void keyTyped(KeyEvent e){
		}
		
		@Override
		public void keyPressed(KeyEvent e){
			switch(e.getKeyCode()){
				case KeyEvent.VK_SHIFT:
					shiftDown = true;
					break;
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					int selectedRow = table.getSelectedRow();
					if(selectedRow != -1){
						Object value  = tableModule.data[table.convertRowIndexToModel(selectedRow)][0];
						int    result = JOptionPane.showConfirmDialog(Display.getInstance(), "Are you sure to delete this " + Utility.flag(flag) + "?\n" + value, "Delete Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
						switch(result){
							case JOptionPane.CANCEL_OPTION:
								return;
							case JOptionPane.OK_OPTION:
						}
						switch(flag){
							case Utility.STUDENT_FLAG:
								Student.removeStudent((Student) value);
								break;
							case Utility.TEACHER_FLAG:
								Teacher.removeTeacher((Teacher) value);
								break;
							case Utility.COURSE_FLAG:
								Course.removeCourse((Course) value);
								break;
						}
						syncTableModule();
					}
			}
		}
		
		void syncTableModule(){
			tableModule = new TableModule(columnHeader);
			table.setRowSorter(null);
			table.setModel(tableModule);
			if(tableModule.data.length != 0){
				table.setRowSorter(new TableRowSorter<>(tableModule));
				table.getRowSorter().toggleSortOrder(0);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				shiftDown = false;
		}
		
		@Override
		public void mouseClicked(MouseEvent e){}
		
		@Override
		public void mousePressed(MouseEvent e){
			if(shiftDown){
				Object value = tableModule.data[table.convertRowIndexToModel(table.getSelectedRow())][0];
				switch(flag){
					case Utility.TEACHER_FLAG:
						TeacherInfoDialog.showInfoDialog(InformationCenter.this, (Teacher) value);
						break;
					case Utility.STUDENT_FLAG:
						StudentInfoDialog.showInfoDialog(InformationCenter.this, (Student) value);
						break;
					case Utility.COURSE_FLAG:
						CourseInfoDialog.showInfoDialog(InformationCenter.this, (Course) value);
						break;
				}
				syncTableModule();
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e){}
		
		@Override
		public void mouseEntered(MouseEvent e){}
		
		@Override
		public void mouseExited(MouseEvent e){}
		
		Object newItem(){
			Object newItem = null;
			switch(flag){
				case Utility.TEACHER_FLAG:
					newItem = TeacherInfoDialog.showInfoDialog(InformationCenter.this, null);
					break;
				case Utility.STUDENT_FLAG:
					newItem = StudentInfoDialog.showInfoDialog(InformationCenter.this, null);
					break;
				case Utility.COURSE_FLAG:
					newItem = CourseInfoDialog.showInfoDialog(InformationCenter.this, null);
					break;
			}
			if(newItem == null)
				return null;
			syncTableModule();
			return newItem;
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
				switch(flag){
					default:
					case Utility.STUDENT_FLAG:{
						data = new Object[Student.students().size()][columnName.length + 1];
						int row = 0;
						for(Student item : Student.students()){
							data[row][0] = item;
							data[row][1] = item.identity();
							data[row][2] = item.OEN();
							data[row][3] = item.birthday();
							data[row][4] = item.email();
							row++;
						}
						break;
					}
					case Utility.TEACHER_FLAG:{
						data = new Object[Teacher.teachers().size()][columnName.length + 1];
						int row = 0;
						for(Teacher item : Teacher.teachers()){
							data[row][0] = item;
							data[row][1] = item.identity();
							data[row][2] = item.MEN();
							data[row][3] = item.birthday();
							data[row][4] = item.email();
							row++;
						}
						break;
					}
					case Utility.COURSE_FLAG:{
						data = new Object[Course.courses().size()][columnName.length + 1];
						int row = 0;
						for(Course item : Course.courses()){
							data[row][0] = item;
							data[row][1] = item.courseID();
							data[row][2] = String.valueOf(item.courseHour());
							row++;
						}
						break;
					}
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
			public void setValueAt(Object aValue, int rowIndex, int columnIndex){}
		}
	}
}
