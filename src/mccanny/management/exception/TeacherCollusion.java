package mccanny.management.exception;

import mccanny.management.teacher.Teacher;
import mccanny.management.course.CoursePeriod;

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
				"teachers=" + Arrays.toString(teachers) +
				", period=" + Arrays.toString(period) +
				'}';
	}
}
