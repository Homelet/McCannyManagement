package mccanny.management.course;

import java.awt.*;

/**
 * the blue print for every course object
 */
public class Course{
	
	private String courseID;
	private double courseHour;
	private Color  color;
	
	public Course(String courseID, Color color){
		this(courseID, 110.0, color);
	}
	
	public Course(String courseID, double courseHour, Color color){
		this.courseID = courseID;
		this.courseHour = courseHour;
		this.color = color;
	}
	
	public String courseID(){
		return courseID;
	}
	
	public double courseHour(){
		return courseHour;
	}
	
	public void courseID(String courseID){
		this.courseID = courseID;
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
		return courseID + "{courseHour=" + courseHour + '}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		return courseID.equals(course.courseID);
	}
}

