package mccanny.visual.dialog;

import mccanny.management.course.Filter;
import mccanny.visual.swing.JBasePanel;

import java.awt.*;

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
	}
}
