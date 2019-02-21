package mccanny.management.exception;

import mccanny.management.course.CoursePeriod;
import mccanny.management.student.Student;

import java.util.Arrays;

/**
 * student collusion is thrown when two period with different course but exist a same student
 */
public class StudentCollusion extends CourseCollusion{
	
	private final Student[] students;
	
	public StudentCollusion(String message, Student[] students, CoursePeriod... period){
		super(message, period);
		this.students = students;
	}
	
	@Override
	public void showWarning(){
	}
	
	@Override
	public String toString(){
		return "StudentCollusion{" +
				"students=" + Arrays.toString(students) +
				", period=" + Arrays.toString(period) +
				'}';
	}
}
