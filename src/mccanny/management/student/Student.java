package mccanny.management.student;

import mccanny.management.course.Course;
import mccanny.util.Distinguishable;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;

import java.util.Collection;
import java.util.HashMap;

public class Student implements ToolTipText, Distinguishable{
	
	private static HashMap<String, Student> students = new HashMap<>();
	
	public static Collection<Student> students(){
		return students.values();
	}
	
	public static Student findStudent(String OEN){
		return students.get(OEN);
	}
	
	public static Student findStudentByUID(String UID){
		for(Student student : students()){
			if(student.UID().equals(UID))
				return student;
		}
		return null;
	}
	
	public static Student loadStudent(String UID, String OEN, String identity){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(students.get(OEN) == null){
			Student student = new Student(UID, OEN, identity);
			students.put(OEN, student);
			return student;
		}else{
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN) + ")");
		}
	}
	
	public static Student newStudent(String OEN, String identity){
		return loadStudent(Utility.fetchUUID32(), OEN, identity);
	}
	
	public static boolean removeStudent(Student student){
		return students.remove(student.OEN(), student);
	}
	
	private final HashMap<Course, StudentCourseDetail> studentCourseDetailMap;
	private final String                               UID;
	private       String                               OEN;
	private       String                               identity;
	
	private Student(String UID, String OEN, String identity){
		this.UID = UID;
		this.OEN = OEN;
		this.identity = identity;
		this.studentCourseDetailMap = new HashMap<>();
	}
	
	public String OEN(){
		return OEN;
	}
	
	public boolean OEN(String OEN){
		if(this.OEN.equals(OEN))
			return false;
		if(students.get(OEN) == null){
			students.put(OEN, students.remove(this.OEN));
			this.OEN = OEN;
			return true;
		}else
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN) + ")");
	}
	
	public String identity(){
		return identity;
	}
	
	public boolean identity(String identity){
		if(this.identity.equals(identity))
			return false;
		this.identity = identity;
		return true;
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
	public String UID(){
		return UID;
	}
}
