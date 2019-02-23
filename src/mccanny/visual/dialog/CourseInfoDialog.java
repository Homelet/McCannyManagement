package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ColorBank;
import homelet.GH.utils.ToolBox.Orientation;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.course.Course;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JColorChooser;
import mccanny.visual.swing.JNumberChooser;

import javax.swing.*;

public class CourseInfoDialog extends InfoDialog<Course>{
	
	private static ColorBank colorBank = new ColorBank();
	private        Course    course;
	
	public CourseInfoDialog(){
		this(null);
	}
	
	public CourseInfoDialog(Course course){
		super(Display.getInstance(), "Course");
		this.course = course;
		NestedPanel panel = new NestedPanel();
		this.setContentPane(panel);
		this.pack();
	}
	
	@Override
	public Course result(){
		return course;
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JLabel         courseID         = new JLabel("Course Name");
			JLabel         courseHour       = new JLabel("Course Required Hour");
			JLabel         courseColor      = new JLabel("Course Color");
			JInputField    courseIDField    = new JInputField("Ex: MHF4U", true);
			JNumberChooser courseHourField  = new JNumberChooser(this, 5, course != null ? course.courseHour() : 110, Integer.MAX_VALUE, 0, Orientation.HORIZONTAL, (value->value + "h"));
			JColorChooser  courseColorField = new JColorChooser(this, course != null ? course.color() : colorBank.pollColor());
			if(course != null){
				courseIDField.setContent(course.courseID());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				if(course == null){
					try{
						course = Course.loadCourse(courseIDField.getContent(), courseHourField.value(), courseColorField.getColor());
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Course", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					course.courseID(courseIDField.getContent());
					course.courseHour(courseHourField.value());
					course.color(courseColorField.getColor());
					closeDialog();
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
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(courseID, 0, 0).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseIDField, 1, 0).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(courseHour, 0, 1).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseHourField, 1, 1).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(courseColor, 0, 2).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(courseColorField, 1, 2).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 3).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 3).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
		}
	}
}
