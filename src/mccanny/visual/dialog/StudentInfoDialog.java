package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.student.Student;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;

public class StudentInfoDialog extends InfoDialog<Student>{
	
	public static Student showInfoDialog(Student student){
		return showInfoDialog(Display.getInstance(), student);
	}
	
	public static Student showInfoDialog(Frame owner, Student student){
		StudentInfoDialog dialog = new StudentInfoDialog(owner, student);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	@Override
	public Student result(){
		return student;
	}
	
	public static Student showInfoDialog(Dialog owner, Student student){
		StudentInfoDialog dialog = new StudentInfoDialog(owner, student);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private Student     student;
	private NestedPanel nestedPanel;
	
	private StudentInfoDialog(Frame owner, Student student){
		super(owner, "Student Info");
		init(student);
	}
	
	private void init(Student student){
		this.student = student;
		nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	private StudentInfoDialog(Dialog owner, Student student){
		super(owner, "Student Info");
		init(student);
	}
	
	@Override
	protected Component firstFocus(){
		return nestedPanel.studentIdentityField.getTextComponent();
	}
	
	private class NestedPanel extends JBasePanel{
		
		final JInputField studentIdentityField;
		
		NestedPanel(){
			JLabel studentIdentity = new JLabel("Student Identity");
			JLabel studentOEN      = new JLabel("OEN");
			studentIdentityField = new JInputField("Ex: Homelet", true);
			JInputField studentOENField = new JInputField("Ex: XXXXXXXXX", true);
			studentIdentityField.getTextComponent().setToolTipText("The Identity for a student, typically the student's name.");
			studentOENField.getTextComponent().setToolTipText("The OEN is a student identification number that is assigned by the Ministry of Education");
			if(student != null){
				studentIdentityField.setContent(student.identity());
				studentOENField.setContent(student.OEN());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				String identity = studentIdentityField.getContent().trim();
				String OEN      = studentOENField.getContent().trim();
				if(OEN.length() != 9){
					JOptionPane.showMessageDialog(StudentInfoDialog.this, "OEN needs to contain exactly 9 digit!", "OEN Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}else if(identity.trim().length() == 0){
					JOptionPane.showMessageDialog(StudentInfoDialog.this, "Student Identity requires at least an Non-space character!", "Identity Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				if(student == null){
					try{
						student = Student.newStudent(OEN, identity);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Student", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						if(student.OEN(OEN) | student.identity(identity))
							Display.getInstance().manager().syncAll();
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Changing StudentInfo", JOptionPane.ERROR_MESSAGE, null);
					}
				}
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			studentIdentity.setLabelFor(studentIdentityField);
			studentOEN.setLabelFor(studentOENField);
			studentIdentity.setHorizontalAlignment(JLabel.RIGHT);
			studentOEN.setHorizontalAlignment(JLabel.RIGHT);
			studentIdentity.setFont(Display.CLEAR_SANS_BOLD);
			studentOEN.setFont(Display.CLEAR_SANS_BOLD);
			studentIdentityField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			studentOENField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			studentIdentityField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			studentOENField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(studentIdentity, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(studentIdentityField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(studentOEN, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(studentOENField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 2).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 2).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(studentIdentityField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(studentOENField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(confirm, FIXED_BUTTON_DIMENSION);
		}
	}
}
