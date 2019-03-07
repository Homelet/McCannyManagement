package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.course.manager.TimeTable;
import mccanny.util.Date;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JDateSelector;

import javax.swing.*;
import java.awt.*;

public class TimeTableInfoDialog extends InfoDialog<TimeTable>{
	
	public static TimeTable showInfoDialog(){
		TimeTableInfoDialog dialog = new TimeTableInfoDialog(Display.getInstance(), null);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private NestedPanel nestedPanel;
	private TimeTable   timeTable;
	
	public TimeTableInfoDialog(Frame frameOwner, TimeTable timeTable){
		super(frameOwner, "TimeTable");
		this.timeTable = timeTable;
		nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	@Override
	public TimeTable result(){
		return timeTable;
	}
	
	@Override
	protected Component firstFocus(){
		return null;
	}
	
	class NestedPanel extends JBasePanel{
		
		public NestedPanel(){
			JLabel        name           = new JLabel("Name");
			JLabel        startDate      = new JLabel("Start Date");
			JLabel        endDate        = new JLabel("End Date");
			JInputField   nameField      = new JInputField("Ex: McCanny TimeTable", true);
			Date          date           = Date.today();
			JDateSelector startDateField = new JDateSelector(timeTable == null ? date : timeTable.startDate(), JDateSelector.REGULAR);
			JDateSelector endDateField   = new JDateSelector(timeTable == null ? date : timeTable.startDate(), JDateSelector.REGULAR);
			if(timeTable != null){
				nameField.setContent(timeTable.name());
			}
			name.setLabelFor(nameField);
			startDate.setLabelFor(startDateField);
			endDate.setLabelFor(endDateField);
			startDate.addMouseListener(startDateField);
			endDate.addMouseListener(endDateField);
			JButton confirm = new JButton(timeTable != null ? "Apply Changes" : "Create TimeTable");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				String    nameValue      = nameField.getContent();
				Date      startDateValue = startDateField.value();
				Date      endDateValue   = endDateField.value();
				TimeTable current        = Display.getInstance().manager().timeTable();
				if(current != null)
					current.save();
				Display.getInstance().manager().initializeTimeTable(timeTable);
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			name.setHorizontalAlignment(JLabel.RIGHT);
			startDate.setHorizontalAlignment(JLabel.RIGHT);
			endDate.setHorizontalAlignment(JLabel.RIGHT);
			name.setFont(Display.CLEAR_SANS_BOLD);
			startDate.setFont(Display.CLEAR_SANS_BOLD);
			endDate.setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			nameField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			nameField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(name, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 13).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(nameField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 13).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(startDate, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 37).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(startDateField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 37).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(endDate, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 37).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(endDateField, 1, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 37).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 3).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 13).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 3).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 13).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(nameField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(startDateField, FIXED_FIELD_DIMENSION.width, FIXED_FIELD_DIMENSION.height * 3);
			ToolBox.setPreferredSize(endDateField, FIXED_FIELD_DIMENSION.width, FIXED_FIELD_DIMENSION.height * 3);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
		}
	}
}
