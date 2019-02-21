package mccanny.management.student;

import mccanny.management.course.Course;

import java.util.HashMap;

public class Student{
	
	private static HashMap<String, Student> students = new HashMap<>();
	
	public static Student findStudent(String OEN){
		return students.get(OEN);
	}
	
	public static void loadStudent(String OEN, String identity, HashMap<Course, StudentCourseDetail> studentCourseDetailMap){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(students.get(OEN) == null){
			Student student = new Student(OEN, identity);
			students.put(student.OEN, student);
		}else{
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN).toString() + ")");
		}
	}
	
	private final HashMap<Course, StudentCourseDetail> studentCourseDetailMap;
	private       String                               OEN;
	private       String                               identity;
	
	public Student(String OEN, String identity){
		this.OEN = OEN;
		this.identity = identity;
		this.studentCourseDetailMap = new HashMap<>();
	}
	
	public String OEN(){
		return OEN;
	}
	
	public void OEN(String OEN){
		this.OEN = OEN;
	}
	
	public String identity(){
		return identity;
	}
	
	public void identity(String identity){
		this.identity = identity;
	}
	
	@Override
	public String toString(){
		return identity + "{OEN='" + OEN + "\'}";
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return OEN.equals(student.OEN);
	}
}
