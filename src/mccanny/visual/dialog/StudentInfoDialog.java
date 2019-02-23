package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox.Orientation;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;

public class StudentInfoDialog extends InfoDialog<Student>{
	
	private Student student;
	
	public StudentInfoDialog(){
		this(null);
	}
	
	public StudentInfoDialog(Student student){
		super(Display.getInstance(), "Student");
		this.student = student;
		NestedPanel nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	@Override
	public Student result(){
		return student;
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JLabel      studentIdentity      = new JLabel("Student Identity");
			JLabel      studentOEN           = new JLabel("OEN");
			JInputField studentIdentityField = new JInputField("Ex: Homelet", true);
			JInputField studentOENField      = new JInputField("Ex: XXXXXXXXX", true);
			if(student != null){
				studentIdentityField.setContent(student.identity());
				studentOENField.setContent(student.OEN());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				if(student == null){
					try{
						student = Student.loadStudent(studentOENField.getContent(), studentIdentityField.getContent());
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Student", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					student.OEN(studentOENField.getContent());
					student.identity(studentIdentityField.getContent());
					closeDialog();
				}
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			studentIdentity.setLabelFor(studentIdentityField);
			studentOEN.setLabelFor(studentOENField);
			studentIdentity.setHorizontalAlignment(JLabel.RIGHT);
			studentOEN.setHorizontalAlignment(JLabel.RIGHT);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(studentIdentity, 0, 0).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(studentIdentityField, 1, 0).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(studentOEN, 0, 1).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(studentOENField, 1, 1).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 2).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 2).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
		}
	}
}
