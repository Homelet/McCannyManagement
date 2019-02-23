package mccanny.management.course;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * the blue print for every course object
 */
public class Course{
	
	private static HashMap<String, Course> courses = new HashMap<>();
	
	public static Collection<Course> courses(){
		return courses.values();
	}
	
	public static Course findCourse(String courseID){
		return courses.get(courseID);
	}
	
	public static Course loadCourse(String courseID, double courseHour, Color color){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(courses.get(courseID) == null){
			Course course = new Course(courseID, courseHour, color);
			courses.put(courseID, course);
			return course;
		}else{
			throw new IllegalArgumentException("Course with the same Course Name have registered (" + courses.get(courseID) + ")");
		}
	}
	
	private String courseID;
	private double courseHour;
	private Color  color;
	
	private Course(String courseID, double courseHour, Color color){
		this.courseID = courseID;
		this.courseHour = courseHour;
		this.color = color;
	}
	
	public String courseID(){
		return courseID;
	}
	
	public void courseID(String courseID){
		if(courses.get(courseID) == null){
			courses.put(courseID, courses.remove(this.courseID));
			this.courseID = courseID;
		}else
			throw new IllegalArgumentException("Course with the same Course Name have registered (" + courses.get(courseID) + ")");
	}
	
	public double courseHour(){
		return courseHour;
	}
	
	public void courseHour(double courseHour){
		this.courseHour = courseHour;
	}
	
	public Color color(){
		return color;
	}
	
	public void color(Color color){
		this.color = color;
	}
	
	@Override
	public String toString(){
		return courseID;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		return courseID.equals(course.courseID);
	}
}

