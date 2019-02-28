package mccanny.util;

public class TimeStamp implements Comparable<TimeStamp>{
	
	public final static boolean timeFormat24 = false;
	private final       Date    date;
	private final       double  time;
	private final       String  rep;
	public TimeStamp(Date date, double time){
		this.date = date;
		this.time = time;
		rep = rep();
	}
	
	private String rep(){
		return date.weekday() + "," + date.visibleMonth() + " " + date.visibleDay() + "," + date.visibleYear() + " " + time();
	}
	
	private String time(){
		int hour   = (int) Math.floor(time);
		int minute = (int) ((time - hour) * 60);
		if(TimeStamp.timeFormat24){
			return String.valueOf(hour) + ':' + minute;
		}else{
			return hour + ":" + minute + (hour > 12 ? " PM" : " AM");
		}
	}
	
	@Override
	public int compareTo(TimeStamp o){
		int result = this.date.compareTo(o.date);
		if(result != 0){
			return Double.compare(this.time, o.time);
		}else{
			return result;
		}
	}
	
	@Override
	public String toString(){
		return rep;
	}
}
