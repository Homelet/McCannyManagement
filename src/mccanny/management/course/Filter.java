package mccanny.management.course;

import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;

import java.util.List;

public class Filter{
	
	/**
	 * a filter that won't filter anything
	 */
	public static final Filter    NULL_FILTER = new Filter(false, null, null, null);
	/** a positive polar means show all the contexts that contains any one of the info inside the filter */
	public static final boolean   POSITIVE    = true;
	/** a negative polar means show all the contexts that do not contains any of the info inside the filter */
	public static final boolean   NEGATIVE    = false;
	// a positive polar means show all the contexts that contains any one of the info inside the filter
	// a negative polar means show all the contexts that do not contains any of the info inside the filter
	private final       boolean   polar;
	private final       Student[] students;
	private final       Teacher[] teachers;
	private final       Course[]  courses;
	
	public Filter(boolean polar, Course[] courses, Student[] students, Teacher[] teachers){
		this.polar = polar;
		this.students = students;
		this.teachers = teachers;
		this.courses = courses;
	}
	
	boolean filter(CoursePeriod period){
		if(polar)
			return checkCourse(period.course()) || checkStudent(period.students()) || checkTeacher(period.teachers());
		else
			return !checkCourse(period.course()) && !checkStudent(period.students()) && !checkTeacher(period.teachers());
	}
	
	private boolean checkCourse(Course c){
		if(courses == null)
			return false;
		for(Course course : courses){
			if(c.equals(course))
				return true;
		}
		return false;
	}
	
	private boolean checkStudent(List<Student> s){
		if(students == null)
			return false;
		for(Student student : students){
			if(s.contains(student))
				return true;
		}
		return false;
	}
	
	private boolean checkTeacher(List<Teacher> t){
		if(teachers == null)
			return false;
		for(Teacher teacher : teachers){
			if(t.contains(teacher))
				return true;
		}
		return false;
	}
}