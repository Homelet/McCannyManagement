package mccanny.management.teacher;

import mccanny.util.ToolTipText;

import java.util.Collection;
import java.util.HashMap;

public class Teacher implements ToolTipText{
	
	private static HashMap<String, Teacher> teachers = new HashMap<>();
	
	public static Collection<Teacher> teachers(){
		return teachers.values();
	}
	
	public static Teacher findTeacher(String MEN){
		return teachers.get(MEN);
	}
	
	public static Teacher loadTeacher(String MEN, String identity){
		// if null means no such teacher exist yet
		// else means such teacher have already been registered
		if(teachers.get(MEN) == null){
			Teacher teacher = new Teacher(MEN, identity);
			teachers.put(MEN, teacher);
			return teacher;
		}else{
			throw new IllegalArgumentException("Student with the same OEN have registered (" + teachers.get(MEN) + ")");
		}
	}
	
	private String MEN;
	private String identity;
	
	private Teacher(String MEN, String identity){
		this.identity = identity;
		this.MEN = MEN;
	}
	
	public String MEN(){
		return MEN;
	}
	
	public void MEN(String MEN){
		this.MEN = MEN;
	}
	
	public String identity(){
		return identity;
	}
	
	public void identity(String identity){
		this.identity = identity;
	}
	
	@Override
	public String toString(){
		return identity;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Teacher teacher = (Teacher) o;
		return MEN.equals(teacher.MEN);
	}
	
	@Override
	public String toolTip(){
		return identity + "(" + MEN + ")";
	}
}
