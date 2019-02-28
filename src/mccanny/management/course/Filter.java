package mccanny.management.course;

import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;

import java.util.Collection;
import java.util.List;

public class Filter{
	
	public static Filter createFilter(boolean polar, Collection courses, Collection students, Collection teachers){
		return new Filter(polar, (Course[]) courses.toArray(new Course[0]), (Student[]) students.toArray(new Student[0]), (Teacher[]) teachers.toArray(new Teacher[0]));
	}
	
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
	public final        boolean   polar;
	public final        Student[] students;
	public final        Teacher[] teachers;
	public final        Course[]  courses;
	private final       String    rep;
	
	public Filter(boolean polar, Course[] courses, Student[] students, Teacher[] teachers){
		this.polar = polar;
		this.students = students;
		this.teachers = teachers;
		this.courses = courses;
		this.rep = rep();
	}
	
	boolean filter(CoursePeriod period){
		if(polar)
			return checkCourse(period.course()) || checkStudent(period.students()) || checkTeacher(period.teachers());
		else
			return !checkCourse(period.course()) && !checkStudent(period.students()) && !checkTeacher(period.teachers());
	}
	
	private boolean checkCourse(Course c){
		if(courses == null || courses.length == 0)
			return false;
		for(Course course : courses){
			if(c.equals(course))
				return true;
		}
		return false;
	}
	
	private boolean checkStudent(List<Student> s){
		if(students == null || students.length == 0)
			return false;
		for(Student student : students){
			if(s.contains(student))
				return true;
		}
		return false;
	}
	
	private boolean checkTeacher(List<Teacher> t){
		if(teachers == null || teachers.length == 0)
			return false;
		for(Teacher teacher : teachers){
			if(t.contains(teacher))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Filter filter = (Filter) o;
		return polar == filter.polar && Utility.identical(courses, filter.courses) && Utility.identical(students, filter.students) && Utility.identical(teachers, filter.teachers);
	}
	
	@Override
	public String toString(){
		return rep;
	}
	
	private String rep(){
		StringBuilder builder    = new StringBuilder();
		int           totalIndex = (students == null ? 0 : students.length) + (teachers == null ? 0 : teachers.length) + (courses == null ? 0 : courses.length);
		if(totalIndex == 0){
			builder.append(polar ? "Exclude All Periods" : "Include All Periods");
			return builder.toString();
		}else{
			builder.append(polar ? "Period Contains" : "Period Don't Contains");
		}
		int        globeIndex = 0;
		int        limit      = 5;
		Object[][] objects    = new Object[][]{ students, teachers, courses };
		for(Object[] inner : objects){
			if(inner != null && inner.length != 0){
				for(int index = 0; index < inner.length; index++, globeIndex++){
					if(globeIndex >= limit){
						builder.append(" ...").append(totalIndex - limit).append(" more");
						return builder.toString();
					}else{
						builder.append(" ").append(inner[index]);
						if(globeIndex + 1 < totalIndex)
							builder.append(",");
					}
				}
			}
		}
		return builder.toString();
	}
}