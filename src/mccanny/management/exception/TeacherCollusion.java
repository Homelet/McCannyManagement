package mccanny.management.exception;

import mccanny.management.course.CoursePeriod;
import mccanny.management.teacher.Teacher;
import mccanny.util.Utility;

import java.util.Arrays;

/**
 * teacher collusion is thrown when two period with different course but exist a same teacher
 */
public class TeacherCollusion extends CourseCollusion{
	
	private final Teacher[] teachers;
	
	public TeacherCollusion(String message, Teacher[] teachers, CoursePeriod... period){
		super(message, period);
		this.teachers = teachers;
	}
	
	@Override
	public void showWarning(){
	}
	
	@Override
	public String toString(){
		return "TeacherCollusion{" +
				"teachers=" + Utility.join(", ", Arrays.asList(teachers), Teacher::identity) +
				", period=" + Utility.join(", ", Arrays.asList(period), period1->period1.course().courseID()) +
				'}';
	}
}
