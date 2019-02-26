package mccanny.management.student;

import mccanny.management.course.Course;
import mccanny.util.Listable;
import mccanny.util.ToolTipText;

import java.util.Collection;
import java.util.HashMap;

public class Student implements ToolTipText, Listable{
	
	private static HashMap<String, Student> students = new HashMap<>();
	
	public static Collection<Student> students(){
		return students.values();
	}
	
	public static Student findStudent(String OEN){
		return students.get(OEN);
	}
	
	public static Student loadStudent(String OEN, String identity){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(students.get(OEN) == null){
			Student student = new Student(OEN, identity);
			students.put(OEN, student);
			return student;
		}else{
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN) + ")");
		}
	}
	
	private final HashMap<Course, StudentCourseDetail> studentCourseDetailMap;
	private       String                               OEN;
	private       String                               identity;
	
	private Student(String OEN, String identity){
		this.OEN = OEN;
		this.identity = identity;
		this.studentCourseDetailMap = new HashMap<>();
	}
	
	public String OEN(){
		return OEN;
	}
	
	public void OEN(String OEN){
		if(this.OEN.equals(OEN))
			return;
		if(students.get(OEN) == null){
			students.put(OEN, students.remove(this.OEN));
			this.OEN = OEN;
		}else
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN) + ")");
	}
	
	public String identity(){
		return identity;
	}
	
	public void identity(String identity){
		if(this.identity.equals(identity))
			return;
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
		Student student = (Student) o;
		return OEN.equals(student.OEN);
	}
	
	@Override
	public String toolTip(){
		return identity + "(" + OEN + ")";
	}
	
	@Override
	public String info(){
		return OEN;
	}
}
