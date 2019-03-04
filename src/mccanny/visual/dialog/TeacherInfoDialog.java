package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.teacher.Teacher;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;

import javax.swing.*;
import java.awt.*;

public class TeacherInfoDialog extends InfoDialog<Teacher>{
	
	public static Teacher showInfoDialog(Teacher teacher){
		return showInfoDialog(Display.getInstance(), teacher);
	}
	
	public static Teacher showInfoDialog(Frame owner, Teacher teacher){
		TeacherInfoDialog dialog = new TeacherInfoDialog(owner, teacher);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	@Override
	public Teacher result(){
		return teacher;
	}
	
	public static Teacher showInfoDialog(Dialog owner, Teacher teacher){
		TeacherInfoDialog dialog = new TeacherInfoDialog(owner, teacher);
		dialog.showDialog();
		dialog.removeDialog();
		return dialog.result();
	}
	
	private Teacher     teacher;
	private NestedPanel nestedPanel;
	
	private TeacherInfoDialog(Frame owner, Teacher teacher){
		super(owner, "Teacher Info");
		init(teacher);
	}
	
	private void init(Teacher teacher){
		this.teacher = teacher;
		nestedPanel = new NestedPanel();
		this.setContentPane(nestedPanel);
		this.pack();
	}
	
	private TeacherInfoDialog(Dialog owner, Teacher teacher){
		super(owner, "Teacher Info");
		init(teacher);
	}
	
	private class NestedPanel extends JBasePanel{
		
		final JInputField teacherIdentityField;
		
		NestedPanel(){
			JLabel teacherIdentity = new JLabel("Teacher Identity");
			JLabel teacherMEN      = new JLabel("MEN");
			teacherIdentityField = new JInputField("Ex: Alireza Rafiee", true);
			JInputField teacherMENField = new JInputField("Ex: XXXXXXXXX", true);
			teacherIdentityField.getTextComponent().setToolTipText("The Identity for a teacher, typically the teacher's name.");
			teacherMENField.getTextComponent().setToolTipText("A Ministry Educator Number (MEN) is a unique identifier which is assigned to all educators in the province.");
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
						teacher = Teacher.newTeacher(MEN, identity);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Teacher", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						if(teacher.MEN(MEN) | teacher.identity(identity))
							Display.getInstance().manager().syncAll();
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
			layouter.put(layouter.instanceOf(teacherIdentity, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherIdentityField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(teacherMEN, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherMENField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 2).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 2).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(100, 100).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(teacherIdentityField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(teacherMENField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(confirm, FIXED_BUTTON_DIMENSION);
		}
	}
	
	@Override
	protected Component firstFocus(){
		return nestedPanel.teacherIdentityField.getTextComponent();
	}
}
