package mccanny.visual.dialog;

import mccanny.management.course.Filter;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FilterDialog extends InfoDialog<Filter>{
	
	private FilterDialog(Frame frameOwner){
		super(frameOwner, "Filter");
	}
	
	private FilterDialog(Dialog frameOwner){
		super(frameOwner, "Filter");
	}
	
	private Filter filter;
	
	@Override
	public Filter result(){
		return filter;
	}
	
	class NestedPanel extends JBasePanel{
		// Showing all CoursePeriod [includes] [students], [teachers], [course]
		
		NestedPanel(){
			JLabel showing_all_coursePeriod_ = new JLabel("Showing All CoursePeriod ");
//			JLabel includes                  = new JLabel(includes_exclude[0]);
			JLabel students = new JLabel();
			JLabel teachers = new JLabel();
			JLabel course   = new JLabel();
//			includes.addMouseListener(new MouseAdapter(){
//				@Override
//				public void mousePressed(MouseEvent e){
//				}
//			});
		}
		
		class PolarToggle extends JLabel implements MouseListener{
			
			final   String[] includes_exclude = new String[]{ "Include", "Exclude" };
			private boolean  polar;
			
			public PolarToggle(boolean polar){
				polar(polar);
			}
			
			private void toggle(){
				polar(!polar);
			}
			
			private boolean polar(){
				return polar;
			}
			
			private void polar(boolean polar){
				this.polar = polar;
				this.setName(polar == Filter.POSITIVE ? includes_exclude[0] : includes_exclude[1]);
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
				toggle();
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
		
		class SelectionLabel<E> extends JLabel implements MouseListener{
			
			private final int flag;
			
			public SelectionLabel(int flag){
				this.flag = flag;
			}
			
			void syncName(){
			}
			
			@Override
			public void mouseClicked(MouseEvent e){
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
