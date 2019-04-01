package mccanny.management.exception;

import mccanny.management.course.CoursePeriod;
import mccanny.util.Utility;

import java.util.Arrays;

/**
 * classroom collusion is thrown when two period with different course but exist a same classroom
 */
public class ClassroomCollusion extends CourseCollusion{
	
	public ClassroomCollusion(String message, CoursePeriod... period){
		super(message, period);
	}
	
	@Override
	public void showWarning(){
	}
	
	@Override
	public String toString(){
		return "ClassroomCollusion{" +
				"period=" + Utility.join(", ", Arrays.asList(period), period1->period1.course().courseID()) +
				'}';
	}
}
