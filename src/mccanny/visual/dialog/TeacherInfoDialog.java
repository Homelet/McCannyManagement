package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.teacher.Teacher;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;

public class TeacherInfoDialog extends InfoDialog<Teacher>{
	
	public static Teacher showDialog(Teacher teacher){
		TeacherInfoDialog dialog = new TeacherInfoDialog(teacher);
		dialog.showDialog();
		dialog.closeDialog();
		return dialog.result();
	}
	
	private Teacher teacher;
	
	public TeacherInfoDialog(){
		this(null);
	}
	
	public TeacherInfoDialog(Teacher teacher){
		super("Teacher");
		this.teacher = teacher;
		NestedPanel nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	@Override
	public Teacher result(){
		return teacher;
	}
	
	private class NestedPanel extends JBasePanel{
		
		NestedPanel(){
			JLabel      teacherIdentity      = new JLabel("Teacher Identity");
			JLabel      teacherMEN           = new JLabel("MEN");
			JInputField teacherIdentityField = new JInputField("Ex: Alireza Rafiee", true);
			JInputField teacherMENField      = new JInputField("Ex: XXXXXXXXX", true);
			if(teacher != null){
				teacherIdentityField.setContent(teacher.identity());
				teacherMENField.setContent(teacher.MEN());
			}
			JButton confirm = new JButton("Confirm");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				String identity = teacherIdentityField.getContent().trim();
				String MEN      = teacherMENField.getContent().trim();
				if(MEN.length() != 9){
					JOptionPane.showMessageDialog(TeacherInfoDialog.this, "MEN needs to contain exactly 9 digit!", "MEN Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}else if(identity.length() == 0){
					JOptionPane.showMessageDialog(TeacherInfoDialog.this, "Teacher Identity requires at least an Non-space character!", "Identity Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				if(teacher == null){
					try{
						teacher = Teacher.loadTeacher(MEN, identity);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Teacher", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						teacher.MEN(MEN);
						teacher.identity(identity);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Changing TeacherInfo", JOptionPane.ERROR_MESSAGE, null);
					}
				}
			});
			cancel.addActionListener((action)->{
				closeDialog();
			});
			teacherIdentity.setLabelFor(teacherIdentityField);
			teacherMEN.setLabelFor(teacherMENField);
			teacherIdentity.setHorizontalAlignment(JLabel.RIGHT);
			teacherMEN.setHorizontalAlignment(JLabel.RIGHT);
			teacherIdentity.setFont(Display.CLEAR_SANS_BOLD);
			teacherMEN.setFont(Display.CLEAR_SANS_BOLD);
			teacherIdentityField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			teacherMENField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			teacherIdentityField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			teacherMENField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(teacherIdentity, 0, 0).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherIdentityField, 1, 0).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(teacherMEN, 0, 1).setInnerPad(FIXED_LABEL_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherMENField, 1, 1).setInnerPad(FIXED_FIELD_WIDTH, FIXED_HEIGHT).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 2).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 2).setInnerPad(FIXED_CONFIRM_WIDTH, FIXED_CONFIRM_HEIGHT).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
		}
	}
}
