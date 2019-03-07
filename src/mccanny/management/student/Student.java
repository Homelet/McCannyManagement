package mccanny.management.student;

import mccanny.management.course.Course;
import mccanny.util.Date;
import mccanny.util.Distinguishable;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;

import java.util.Collection;
import java.util.HashMap;

public class Student implements ToolTipText, Distinguishable, Comparable<Student>{
	
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
	
	public static Collection<Student> students(){
		return students.values();
	}
	
	@Override
	public String UID(){
		return UID;
	}
	
	public static Student newStudent(String OEN, String identity, Date birthday, String email){
		return loadStudent(Utility.fetchUUID32(), OEN, identity, birthday, email);
	}
	
	public static Student loadStudent(String UID, String OEN, String identity, Date birthday, String email){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(students.get(OEN) == null){
			Student student = new Student(UID, OEN, identity, birthday, email);
			students.put(OEN, student);
			return student;
		}else{
			throw new IllegalArgumentException("Student with the same OEN have registered (" + students.get(OEN) + ")");
		}
	}
	
	public static boolean removeStudent(Student student){
		return students.remove(student.OEN(), student);
	}
	
	@Override
	public int compareTo(Student o){
		int result = identity.compareTo(o.identity);
		if(result == 0){
			return birthday.compareTo(o.birthday);
		}else{
			return result;
		}
	}
	
	public String OEN(){
		return OEN;
	}
	
	private static HashMap<String, Student>             students = new HashMap<>();
	private final  HashMap<Course, StudentCourseDetail> studentCourseDetailMap;
	private final  String                               UID;
	private        String                               OEN;
	private        String                               identity;
	// TODO FUTURE IMPLEMENTATION
	private        Date                                 birthday;
	private        String                               email;
	
	private Student(String UID, String OEN, String identity, Date birthday, String email){
		this.UID = UID;
		this.OEN = OEN;
		this.identity = identity;
		this.studentCourseDetailMap = new HashMap<>();
		this.birthday = birthday;
		this.email = email;
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
	
	public boolean identity(String identity){
		if(this.identity.equals(identity))
			return false;
		this.identity = identity;
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return OEN.equals(student.OEN);
	}
	
	public Date birthday(){
		return birthday;
	}
	
	public void birthday(Date birthday){
		this.birthday = birthday;
	}
	
	public String email(){
		return email;
	}
	
	public void email(String email){
		this.email = email;
	}
	
	@Override
	public String toString(){
		return identity;
	}
	
	@Override
	public String toolTip(){
		return identity + "(" + OEN + ")";
	}
}
