package mccanny.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Date implements Comparable<Date>{
	
	private static Date today(){
		Calendar calendar = new GregorianCalendar();
		return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) - 1);
	}
	private final Weekday weekday;
	private final Month   month;
	private final int     year, day;
	/**
	 * if you wish to use the normal way use this constructor
	 */
	public Date(int year, double month, double day){
		this(year, (int) (month - 1), (int) (day - 1));
	}
	
	/**
	 * month start at 0 and caped at 11,
	 * day start at 0 and caped at either 27, 28, 29 or 30
	 */
	public Date(int year, int month, int day){
		this.year = year;
		this.month = Month.index(month);
		if(day < this.month.days(year)){
			this.day = day;
		}else{
			throw new IllegalArgumentException("illegal day number");
		}
		this.weekday = Utility.getWeekday(year, this.month, day);
	}
	
	@Override
	public int compareTo(Date o){
		int result = Integer.compare(this.year, o.year);
		if(result == 0){
			result = Integer.compare(this.month.index(), o.month.index());
			if(result == 0){
				return Integer.compare(this.day, o.day);
			}else{
				return result;
			}
		}else{
			return result;
		}
	}
	
	public Date floorNextWeek(){
		return this.plus(7 - weekday.index());
	}
	
	public Date plus(double week){
		return plus((int) week * 7);
	}
	
	public Date plus(int days){
		return plusSequence(new int[]{ year, month.index(), day }, days);
	}
	
	private Date plusSequence(int[] date, int dayBuffer){
		// month start at 0 and caped at 11,
		// day start at 0 and caped at either 27, 28, 29 or 30
		int dayToNextMonth = (Month.index(date[1]).days(date[0])) - date[2];
		if(dayBuffer > dayToNextMonth){
			dayBuffer -= dayToNextMonth;
			date[1] += 1;
			date[2] = 0;
		}else{
			date[2] = dayBuffer;
			dayBuffer = 0;
		}
		if(date[1] > 11){
			date[0] += 1;
			date[1] = 0;
		}
		if(dayBuffer > 0){
			return plusSequence(date, dayBuffer);
		}else{
			return new Date(date[0], date[1], date[2]);
		}
	}
	
	public Date minus(double week){
		return minus((int) week * 7);
	}
	
	public Date minus(int days){
		return minusSequence(new int[]{ year, month.index(), day }, days);
	}
	
	private Date minusSequence(int[] date, int dayBuffer){
		// month start at 0 and caped at 11,
		// day start at 0 and caped at either 27, 28, 29 or 30
		int dayleftThisMonth = date[2];
		if(dayBuffer > dayleftThisMonth){
			dayBuffer -= dayleftThisMonth;
			date[1] -= 1;
			date[2] = Month.index(date[1]).days(date[0]);
		}else{
			date[2] = dayleftThisMonth - dayBuffer;
			dayBuffer = 0;
		}
		if(date[1] < 0){
			date[0] -= 1;
		}
		if(dayBuffer > 0){
			return minusSequence(date, dayBuffer);
		}else{
			return new Date(date[0], date[1], date[2]);
		}
	}
	
	@Override
	public String toString(){
		return visibleYear() + "/" + visibleMonth() + "/" + visibleDay() + " (" + weekday() + ")";
	}
	
	public int visibleYear(){
		return year;
	}
	
	public Month visibleMonth(){
		return month;
	}
	
	public int visibleDay(){
		return day + 1;
	}
	
	public Weekday weekday(){
		return weekday;
	}
}
