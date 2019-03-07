package mccanny.visual.dialog;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox;
import homelet.GH.visual.swing.JInput.JInputField;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.visual.Display;
import mccanny.visual.swing.JBasePanel;
import mccanny.visual.swing.JDateSelector;

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
			JLabel teacherEmail    = new JLabel("E-Mail");
			JLabel teacherBirthday = new JLabel("Birthday");
			//
			teacherIdentityField = new JInputField("Ex: Alireza Rafiee", true);
			JInputField   teacherMENField      = new JInputField("Ex: XXXXXXXXX", true);
			JInputField   teacherEmailField    = new JInputField("Ex: Ex: XXXXXX@XXX.XXX", true);
			JDateSelector teacherBirthdayField = new JDateSelector(Date.today(), JDateSelector.BIRTHDAY);
			//
			teacherIdentityField.getTextComponent().setToolTipText("The Identity for a teacher, typically the teacher's name.");
			teacherMENField.getTextComponent().setToolTipText("A Ministry Educator Number (MEN) is a unique identifier which is assigned to all educators in the province.");
			teacherEmailField.getTextComponent().setToolTipText("the Teacher's email.");
			teacherBirthday.setToolTipText("Double Click for Quick Date Prompt.");
			if(teacher != null){
				teacherIdentityField.setContent(teacher.identity());
				teacherMENField.setContent(teacher.MEN());
				teacherEmailField.setContent(teacher.email());
				teacherBirthdayField.applyDate(teacher.birthday());
			}
			JButton confirm = new JButton(teacher != null ? "Apply Changes" : "Create Teacher");
			JButton cancel  = new JButton("Cancel");
			confirm.addActionListener((action)->{
				String identity = teacherIdentityField.getContent().trim();
				String MEN      = teacherMENField.getContent().trim();
				String email    = teacherEmailField.getContent().trim();
				Date   birthday = teacherBirthdayField.value();
				if(MEN.length() != 9){
					JOptionPane.showMessageDialog(TeacherInfoDialog.this, "MEN needs to contain exactly 9 digit!", "MEN Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}else if(identity.length() == 0){
					JOptionPane.showMessageDialog(TeacherInfoDialog.this, "Teacher Identity requires at least an Non-space character!", "Identity Format Exception", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				if(teacher == null){
					try{
						teacher = Teacher.newTeacher(MEN, identity, birthday, email);
						closeDialog();
					}catch(IllegalArgumentException e){
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error Adding Teacher", JOptionPane.ERROR_MESSAGE, null);
					}
				}else{
					try{
						teacher.MEN(MEN);
						teacher.email(email);
						teacher.birthday(birthday);
						if(teacher.identity(identity))
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
			teacherEmail.setLabelFor(teacherEmailField);
			teacherBirthday.setLabelFor(teacherBirthdayField);
			teacherIdentity.setHorizontalAlignment(JLabel.RIGHT);
			teacherMEN.setHorizontalAlignment(JLabel.RIGHT);
			teacherEmail.setHorizontalAlignment(JLabel.RIGHT);
			teacherBirthday.setHorizontalAlignment(JLabel.RIGHT);
			teacherIdentity.setFont(Display.CLEAR_SANS_BOLD);
			teacherMEN.setFont(Display.CLEAR_SANS_BOLD);
			teacherEmail.setFont(Display.CLEAR_SANS_BOLD);
			teacherBirthday.setFont(Display.CLEAR_SANS_BOLD);
			teacherIdentityField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			teacherMENField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			teacherEmailField.getTextComponent().setFont(Display.CLEAR_SANS_BOLD);
			teacherIdentityField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			teacherMENField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			teacherEmailField.getDrawer().setFont(Display.CLEAR_SANS_BOLD);
			confirm.setFont(Display.CLEAR_SANS_BOLD);
			cancel.setFont(Display.CLEAR_SANS_BOLD);
			teacherBirthday.addMouseListener(teacherBirthdayField);
			Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
			layouter.put(layouter.instanceOf(teacherIdentity, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 15).setInsets(10, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherIdentityField, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 15).setInsets(10, 10, 0, 10));
			layouter.put(layouter.instanceOf(teacherMEN, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 15).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherMENField, 1, 1).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 15).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(teacherEmail, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 15).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherEmailField, 1, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 15).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(teacherBirthday, 0, 3).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(34, 45).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(teacherBirthdayField, 1, 3).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(66, 45).setInsets(0, 10, 0, 10));
			layouter.put(layouter.instanceOf(cancel, 0, 4).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(34, 15).setInsets(0, 10, 10, 10));
			layouter.put(layouter.instanceOf(confirm, 1, 4).setAnchor(Anchor.RIGHT).setFill(Fill.BOTH).setWeight(66, 15).setInsets(0, 10, 0, 10));
			ToolBox.setPreferredSize(teacherIdentityField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(teacherMENField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(teacherEmailField, FIXED_FIELD_DIMENSION);
			ToolBox.setPreferredSize(teacherBirthdayField, FIXED_FIELD_DIMENSION.width, FIXED_FIELD_DIMENSION.height * 3);
			ToolBox.setPreferredSize(cancel, FIXED_BUTTON_DIMENSION);
			ToolBox.setPreferredSize(confirm, FIXED_BUTTON_DIMENSION);
		}
	}
	
	@Override
	protected Component firstFocus(){
		return nestedPanel.teacherIdentityField.getTextComponent();
	}
}
