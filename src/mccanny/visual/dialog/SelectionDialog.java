package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import mccanny.management.student.Student;
import mccanny.util.Listable;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

public class SelectionDialog<E extends Listable> extends InfoDialog<Collection<E>>{
	
	public static SelectionDialog<Student> showStudentDialog(Frame frameOwner, Collection<Student> include, Collection<Student> exclude){
		SelectionDialog<Student> studentSelectionDialog = new SelectionDialog<>(frameOwner, "Student", STUDENT_COLUMN_HEADER, include, exclude);
		studentSelectionDialog.showDialog(frameOwner);
		studentSelectionDialog.closeDialog();
		return studentSelectionDialog;
	}
	
	public static final String[]      STUDENT_COLUMN_HEADER = new String[]{ null, "Identity", "OEN" };
	public static final String[]      TEACHER_COLUMN_HEADER = new String[]{ null, "Identity", "MEN" };
	public static final String[]      COURSE_COLUMN_HEADER  = new String[]{ null, "CourseID", "Course Hour(h)" };
	private             Collection<E> include;
	private             Collection<E> exclude;
	
	private SelectionDialog(Frame frameOwner, String title, String[] columnHeader, Collection<E> include, Collection<E> exclude){
		super(frameOwner, "Select " + title);
		this.include = include;
		this.exclude = exclude;
		NestedPanel nestedPanel = new NestedPanel(columnHeader);
		this.setContentPane(nestedPanel);
		this.pack();
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
	
	class NestedPanel extends JBasePanel{
		
		public NestedPanel(String[] columnHeader){
			SelectionTable  table    = new SelectionTable(columnHeader);
			GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(table, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100));
			ToolBox.setPreferredSize(this, 300, 400);
		}
	}
	
	class SelectionTable extends JScrollPane implements MouseListener, KeyListener{
		
		final TableModule tableModule;
		final JTable      table;
		
		SelectionTable(String[] columnHeader){
			tableModule = new TableModule(columnHeader);
			table = new JTable(tableModule);
			table.setAutoCreateRowSorter(true);
			table.setRowSorter(new TableRowSorter<>(tableModule));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setDragEnabled(false);
			table.setRowHeight(20);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(this);
			table.addKeyListener(this);
			TableColumnModel model = table.getColumnModel();
			model.getColumn(0).setResizable(false);
			model.getColumn(1).setResizable(false);
			model.getColumn(2).setResizable(false);
			model.getColumn(0).setPreferredWidth(20);
			model.getColumn(1).setPreferredWidth(130);
			model.getColumn(2).setPreferredWidth(130);
			this.setViewportView(table);
		}
		
		@Override
		public void mouseClicked(MouseEvent e){}
		
		@Override
		public void mousePressed(MouseEvent e){
			if(e.getButton() == MouseEvent.BUTTON1){
				toggleSelected();
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e){}
		
		@Override
		public void mouseEntered(MouseEvent e){}
		
		@Override
		public void mouseExited(MouseEvent e){}
		
		@Override
		public void keyTyped(KeyEvent e){
		}
		
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				toggleSelected();
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e){
		}
		
		void toggleSelected(){
			table.setValueAt(!(boolean) table.getValueAt(table.getSelectedRow(), 0), table.getSelectedRow(), 0);
			table.revalidate();
			table.repaint();
		}
	}
	
	class TableModule extends AbstractTableModel{
		
		// row, col
		private       Object[][] data;
		private final String[]   columnName;
		
		TableModule(String[] columnName){
			this.columnName = columnName;
			initializeData();
		}
		
		synchronized void initializeData(){
			data = new Object[include.size() + exclude.size()][columnName.length];
			int row = 0;
			for(E item : include){
				data[row][0] = Boolean.TRUE;
				data[row][1] = item.identity();
				data[row][2] = item.info();
				row++;
			}
			for(E item : exclude){
				data[row][0] = Boolean.FALSE;
				data[row][1] = item.identity();
				data[row][2] = item.info();
				row++;
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
		public String getColumnName(int columnIndex){
			return columnName[columnIndex];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex){
			return data[0][columnIndex].getClass();
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex){
			return false;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex){
			return data[rowIndex][columnIndex];
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			data[rowIndex][columnIndex] = aValue;
		}
	}
}
