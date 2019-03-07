package mccanny.visual.swing;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox.Orientation;
import mccanny.util.Date;
import mccanny.util.Month;
import mccanny.util.Utility;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooser;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserEvent;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserGroup;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooserHandler;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDateSelector extends JComponent implements JIndexedChooserHandler, MouseListener{
	
	public static final int             REGULAR  = 0;
	public static final int             BIRTHDAY = 1;
	private final       JIndexedChooser year;
	private final       JIndexedChooser month;
	private final       JIndexedChooser day;
	private final       int             mode;
	private final       Date            referenceDate;
	
	public JDateSelector(Date referenceDate, int mode){
		this.referenceDate = referenceDate;
		this.mode = mode;
		JIndexedChooserGroup group = new JIndexedChooserGroup(this);
		switch(mode){
			default:
			case REGULAR:
				year = new JIndexedChooser(this, 1, referenceDate.year(), 2100, 1900, Orientation.VERTICAL, value->String.valueOf((int) value));
				month = new JIndexedChooser(this, 1, referenceDate.month().index(), 11, 0, Orientation.VERTICAL, value->Month.index((int) value).toString());
				day = new JIndexedChooser(this, 1, referenceDate.day(), referenceDate.month().days(referenceDate.year()), 0, Orientation.VERTICAL, value->String.valueOf((int) value + 1));
				break;
			case BIRTHDAY:
				year = new JIndexedChooser(this, 1, referenceDate.year(), referenceDate.year(), 1900, Orientation.VERTICAL, value->String.valueOf((int) value));
				month = new JIndexedChooser(this, 1, referenceDate.month().index(), referenceDate.month().index(), 0, Orientation.VERTICAL, value->Month.index((int) value).toString());
				day = new JIndexedChooser(this, 1, referenceDate.day(), referenceDate.day(), 0, Orientation.VERTICAL, value->String.valueOf((int) value + 1));
				break;
		}
		group.add(year);
		group.add(month);
		group.add(day);
		year.setButtonText("+", "-");
		month.setButtonText("+", "-");
		day.setButtonText("+", "-");
		year.setButtonToolTipText("Next Year", "Last Year");
		month.setButtonToolTipText("Next Month", "Last Month");
		day.setButtonToolTipText("Tomorrow", "Yesterday");
		Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
		layouter.put(layouter.instanceOf(year, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(40, 100).setInsets(0, 0, 0, 10));
		layouter.put(layouter.instanceOf(month, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(30, 100).setInsets(0, 0, 0, 10));
		layouter.put(layouter.instanceOf(day, 2, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(30, 100).setInsets(0, 0, 0, 0));
	}
	
	@Override
	public void onTrigger(JIndexedChooserEvent e){
		if(e.initiator() == year){
			if(mode == BIRTHDAY){
				if((int) year.value() == referenceDate.year()){
					month.range(0, referenceDate.month().index());
					syncDay(true);
				}else{
					month.range(0, 11);
					syncDay(false);
				}
			}
		}else if(e.initiator() == month){
			syncDay(mode == BIRTHDAY && (int) year.value() == referenceDate.year());
		}
	}
	
	public Date value(){
		return new Date((int) year.value(), (int) month.value(), (int) day.value());
	}
	
	private void syncDay(boolean yearMatch){
		if(yearMatch && (int) month.value() == referenceDate.month().index())
			day.range(0, referenceDate.day());
		else
			day.range(0, Month.index((int) month.value()).days((int) year.value()) - 1);
	}
	
	public void applyDate(Date date){
		applyDate(date.year(), date.month().index(), date.day());
	}
	
	public void applyDate(int year, int month, int day){
		this.year.processValue(year, true);
		this.month.processValue(month, true);
		this.day.processValue(day, true);
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount() == 2){
			String result = (String) JOptionPane.showInputDialog(this, "Please Enter the date in format of (year-month-day)", "Quick Date Prompt", JOptionPane.PLAIN_MESSAGE, null, null, ((int) year.value()) + "-" + ((int) month.value() + 1) + "-" + ((int) day.value() + 1));
			if(result == null)
				return;
			result = Utility.discardSpace(result);
			if(result.length() == 0)
				return;
			Matcher matcher = Pattern.compile("([\\d]{4})[\\-/]([\\d]{1,2})[\\-/]([\\d]{1,2})").matcher(result);
			if(matcher.find()){
				int year  = Integer.valueOf(matcher.group(1));
				int month = Integer.valueOf(matcher.group(2)) - 1;
				int day   = Integer.valueOf(matcher.group(3)) - 1;
				applyDate(year, month, day);
			}
		}
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
