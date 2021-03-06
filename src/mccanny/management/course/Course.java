package mccanny.management.course;

import mccanny.util.Distinguishable;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * the blue print for every course object
 */
public class Course implements ToolTipText, Distinguishable, Comparable<Course>{
	
	public static Course findCourse(String courseID){
		return courses.get(courseID);
	}
	
	public static Course findCourseByUID(String UID){
		for(Course course : courses()){
			if(course.UID().equals(UID))
				return course;
		}
		return null;
	}
	
	public static Collection<Course> courses(){
		return courses.values();
	}
	
	@Override
	public String UID(){
		return UID;
	}
	
	public static Course newCourse(String courseID, double courseHour, Color color){
		return loadCourse(Utility.fetchUUID32(), courseID, courseHour, color);
	}
	
	public static Course loadCourse(String UID, String courseID, double courseHour, Color color){
		// if null means no such student exist yet
		// else means such student have already been registered
		if(courses.get(courseID) == null){
			Course course = new Course(UID, courseID, courseHour, color);
			courses.put(courseID, course);
			return course;
		}else{
			throw new IllegalArgumentException("Course with the same Course Name have registered (" + courses.get(courseID) + ")");
		}
	}
	
	public static boolean removeCourse(Course course){
		return courses.remove(course.courseID(), course);
	}
	
	public String courseID(){
		return courseID;
	}
	
	private static HashMap<String, Course> courses = new HashMap<>();
	private final  String                  UID;
	private        String                  courseID;
	private        double                  courseHour;
	private        Color                   color;
	
	@Override
	public int compareTo(Course o){
		int result = courseID.compareTo(o.courseID);
		if(result == 0){
			return Double.compare(courseHour, o.courseHour);
		}else{
			return result;
		}
	}
	
	private Course(String UID, String courseID, double courseHour, Color color){
		this.UID = UID;
		this.courseID = courseID;
		this.courseHour = courseHour;
		this.color = color;
	}
	
	public boolean courseID(String courseID){
		if(this.courseID.equals(courseID))
			return false;
		if(courses.get(courseID) == null){
			courses.put(courseID, courses.remove(this.courseID));
			this.courseID = courseID;
			return true;
		}else
			throw new IllegalArgumentException("Course with the same CourseID have registered (" + courses.get(courseID) + ")");
	}
	
	public double courseHour(){
		return courseHour;
	}
	
	public boolean courseHour(double courseHour){
		if(this.courseHour == courseHour)
			return false;
		this.courseHour = courseHour;
		return true;
	}
	
	public Color color(){
		return color;
	}
	
	public boolean color(Color color){
		if(this.color.equals(color))
			return false;
		this.color = color;
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		return courseID.equals(course.courseID);
	}
	
	@Override
	public String toString(){
		return courseID;
	}
	
	@Override
	public String toolTip(){
		return courseID + "(" + courseHour + ")";
	}
}

