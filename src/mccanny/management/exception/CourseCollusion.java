package mccanny.management.exception;

import mccanny.management.course.CoursePeriod;

public abstract class CourseCollusion{
	
	protected final CoursePeriod[] period;
	protected final String         message;
	
	public CourseCollusion(String message, CoursePeriod... period){
		this.message = message;
		this.period = period;
	}
	
	public CoursePeriod[] periods(){
		return period;
	}
	
	public String message(){
		return message;
	}
	
	public abstract void showWarning();
}
