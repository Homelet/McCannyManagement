package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import mccanny.management.student.Student;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.util.Collection;

public class StudentSelectionDialog extends InfoDialog<Collection<Student>>{
	
	public StudentSelectionDialog(){
		super("Select Student");
		NestedPanel nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	@Override
	public Collection<Student> result(){
		return null;
	}
	
	class NestedPanel extends JBasePanel{
		
		public NestedPanel(){
			SelectionTable           table    = new SelectionTable();
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(table, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100));
			ToolBox.setPreferredSize(this, 300, 400);
		}
	}
	
	class SelectionTable extends JScrollPane{
		
		final TableModule tableModule;
		
		public SelectionTable(){
			tableModule = new TableModule();
			JTable table = new JTable(tableModule);
			table.setAutoCreateRowSorter(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnModel model = table.getColumnModel();
			model.getColumn(0).setResizable(false);
			model.getColumn(1).setPreferredWidth(140);
			model.getColumn(2).setPreferredWidth(140);
			this.setViewportView(table);
		}
	}
	
	class TableModule extends AbstractTableModel{
		
		final String[] columnName = new String[]{ null, "Identity", "OEN" };
		// row, col
		Object[][] data;
		
		public TableModule(){
			initializeData();
		}
		
		synchronized void initializeData(){
			Collection<Student> students = Student.students();
			data = new Object[students.size()][columnName.length];
			int row = 0;
			for(Student student : students){
				data[row][0] = Boolean.FALSE;
				data[row][1] = student.identity();
				data[row][2] = student.OEN();
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
			return columnIndex == 0;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex){
			return data[rowIndex][columnIndex];
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			if(columnIndex == 0){
				data[rowIndex][columnIndex] = aValue;
			}
		}
	}
}
