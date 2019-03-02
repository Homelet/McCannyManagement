package mccanny.management.course.manager;

import mccanny.management.course.CoursePeriod;

public class PeriodEvent implements Comparable<PeriodEvent>{
	
	@Override
	public int compareTo(PeriodEvent o){
		int result = Double.compare(time, o.time);
		if(result == 0){
			return this.period.compareTo(o.period);
		}else{
			return result;
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PeriodEvent event = (PeriodEvent) o;
		if(status != event.status) return false;
		return period.equals(event.period);
	}
	
	public final static boolean      START = true;
	public final static boolean      END   = false;
	public final        CoursePeriod period;
	public final        double       time;
	public final        boolean      status;
	
	public PeriodEvent(CoursePeriod period, double time, boolean status){
		this.period = period;
		this.time = time;
		this.status = status;
	}
}