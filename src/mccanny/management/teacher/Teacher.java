package mccanny.management.teacher;

import java.util.HashMap;

public class Teacher{
	
	private static HashMap<String, Teacher> teachers = new HashMap<>();
	
	public static Teacher findTeacher(String MEN){
		return teachers.get(MEN);
	}
	
	public static void loadTeacher(String MEN, String identity){
		// if null means no such teacher exist yet
		// else means such teacher have already been registered
		if(teachers.get(MEN) == null){
			Teacher teacher = new Teacher(MEN, identity);
			teachers.put(teacher.MEN, teacher);
		}else{
			throw new IllegalArgumentException("Teacher with the same teacher ID have registered");
		}
	}
	
	private String MEN;
	private String identity;
	
	public Teacher(String MEN, String identity){
		this.identity = identity;
		this.MEN = MEN;
	}
	
	public String identity(){
		return identity;
	}
	
	public String MEN(){
		return MEN;
	}
	
	@Override
	public String toString(){
		return identity + "{MEN='" + MEN + "\'}";
	}
}
