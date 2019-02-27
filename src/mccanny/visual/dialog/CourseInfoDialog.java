package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ColorBank;
import homelet.GH.utils.ToolBox;
import homelet.GH.utils.ToolBox.Orientation;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.course.Course;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JColorChooser;
import mccanny.visual.swing.JIndexedChooser.JIndexedChooser;

import javax.swing.*;
import java.awt.*;

public class CourseInfoDialog extends InfoDialog<Course>{
	
	private static ColorBank colorBank = new ColorBank();
	private        Course    course;
	
	private CourseInfoDialog(Dialog owner, Course course){
		super(owner, "Course Info");
		init(course);
	}
	
	private void init(Course course){
		this.course = course;
		NestedPanel panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	private CourseInfoDialog(Frame owner, Course course){
		super(owner, "Course Info");
		init(course);
	}
	
	public static Course showInfoDialog(Course course){
		return showInfoDialog(Display.getInstance(), course);
	}
	
	public static Course showInfoDialog(Frame owner, Course course){
		CourseInfoDialog dialog = new CourseInfoDialog(owner, course);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	@Override
	public Course result(){
		return course;
	}
	
	public static Course showInfoDialog(Dialog owner, Course course){
		CourseInfoDialog dialog = new CourseInfoDialog(owner, course);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JLabel      courseID      = new JLabel("Course Name");
			JLabel      courseHour    = new JLabel("Course Required Hour");
			JLabel      courseColor   = new JLabel("Course Color");
			JInputField courseIDField = new JInputField("Ex: MHF4U", true);
			courseIDField.getTextComponent().setToolTipText("The Course Code for a Course.\nTypically 5 digit long.");
			JIndexedChooser courseHourField = new JIndexedChooser(this, 5, course != null ? course.courseHour() : 110, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, (value->value + "h"));
			courseHourField.setButtonToolTipText("+ 5 hour", "- 5 hour");
			courseHourField.setButtonText("+ 5h", "- 5h");
			JColorChooser courseColorField = new JColorChooser(this, course != null ? course.color() : colorBank.pollRandomColor());
			if(course != null){
				courseIDField.setContent(course.courseID());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				String courseIDValue    = courseIDField.getContent().trim();
				int    courseHourValue  = (int) Math.floor(courseHourField.value());
				Color  courseColorColor = courseColorField.getColor();
				if(courseIDValue.length() == 0){
					JOptionPane.showMessageDialog(CourseInfoDialog.this, "CourseID requires at least an Non-space character!", "CourseID Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}else if(courseIDValue.length() != 5){
					int result = JOptionPane.showConfirmDialog(CourseInfoDialog.this, "CourseID is typically 5 digit long.\nAre you sure to proceed?", "CourseID Format Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
					switch(result){
						case JOptionPane.CANCEL_OPTION:
							return;
						default:
						case JOptionPane.OK_OPTION:
					}
				}
				if(course == null){
					try{
						course = Course.newCourse(courseIDValue, courseHourValue, courseColorColor);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Course", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						if(course.courseID(courseIDValue) | course.courseHour(courseHourValue) | course.color(courseColorColor))
							Display.getInstance().manager().syncAll();
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Changing CourseInfo", JOptionPane.ERROR_MESSAGE, null);
					}
				}
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			courseID.setLabelFor(courseIDField);
			courseHour.setLabelFor(courseHourField);
			courseColor.setLabelFor(courseColorField);
			courseID.setHorizontalAlignment(JLabel.RIGHT);
			courseHour.setHorizontalAlignment(JLabel.RIGHT);
			courseColor.setHorizontalAlignment(JLabel.RIGHT);
			courseID.setFont(Display.CLEAR_SANS_BOLD);
			courseHour.setFont(Display.CLEAR_SANS_BOLD);
			courseColor.setFont(Display.CLEAR_SANS_BOLD);
			courseIDField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			courseIDField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(courseID, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseIDField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(courseHour, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseHourField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(courseColor, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseColorField, 1, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 3).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 3).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(courseIDField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(courseHourField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(courseColorField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(confirm, FIXED_BUTTON_DIMENSION);
			courseIDField.getTextComponent().requestFocusInWindow();
		}
	}
}
