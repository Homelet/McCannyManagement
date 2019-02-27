package mccanny.management.student;

import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.util.Date;

import java.util.ArrayList;

// TODO FUTURE IMPLEMENTATION V2.X
public class StudentCourseDetail{
	
	private final Course                  course;
	private final Student                 student;
	private final ArrayList<CoursePeriod> periods;
	private       Date                    startDate;
	private       double                  hours;
	private       Date                    lastUpdate = null;
	private       Date                    estimateFinish;
	
	public StudentCourseDetail(Course course, Student student, Date startDate){
		this.course = course;
		this.student = student;
		this.periods = new ArrayList<>();
		this.startDate = startDate;
	}
	
	public Date lastUpdate(){
		return lastUpdate;
	}
	
	public Date startDate(){
		return startDate;
	}
	
	public Date estimateFinish(){
		return estimateFinish;
	}
	
	public double hours(){
		return hours;
	}
	
	public Course course(){
		return course;
	}
	
	public Student student(){
		return student;
	}
	
	public String hoursLeft(){
		double left = hours - course.courseHour();
		return (left == 0 ? "" : (left < 0 ? "-" : "+")) + left;
	}
	
	public void setHours(double hours){
		this.hours = hours;
		this.estimateFinish = estimateCourseFinishDate();
	}
	
	public void addHours(double hours){
		this.hours += hours;
		this.estimateFinish = estimateCourseFinishDate();
	}
	
	public void update(Date today){
		this.lastUpdate = today;
		this.estimateFinish = estimateCourseFinishDate();
	}
	
	public void startDate(Date startDate){
		this.startDate = startDate;
	}
	
	public double hoursPerWeek(){
		int accum = 0;
		for(CoursePeriod period : periods){
			accum += period.length();
		}
		return accum;
	}
	
	public void join(CoursePeriod period){
		if(period.course() != course){
			throw new IllegalArgumentException("Can't join periods with different course");
		}
		this.periods.add(period);
		this.periods.sort(null);
	}
	
	public void remove(CoursePeriod period){
		this.periods.remove(period);
	}
	
	/**
	 * estimate the date that the course is going to finnish
	 */
	private Date estimateCourseFinishDate(){
		double hoursPerWeek  = hoursPerWeek();
		double hoursRequired = course.courseHour() - this.hours;
		int    weekCount     = (int) Math.floor(hoursRequired / hoursPerWeek);
		double trailingHours = hoursRequired % hoursPerWeek;
		int    trailingDay   = 0;
		for(CoursePeriod period : periods){
			trailingHours -= period.length();
			trailingDay++;
			if(trailingHours <= 0)
				break;
		}
		return startDate.plus(weekCount * 7 + trailingDay);
	}
}
