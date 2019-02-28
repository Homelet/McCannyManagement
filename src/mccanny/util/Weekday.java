package mccanny.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public enum Weekday{
	MONDAY(0, "Monday"),
	TUESDAY(1, "Tuesday"),
	WEDNESDAY(2, "Wednesday"),
	THURSDAY(3, "Thursday"),
	FRIDAY(4, "Friday"),
	SATURDAY(5, "Saturday"),
	SUNDAY(6, "Sunday");
	public static Weekday[] weekdays(Weekday from){
		Weekday[] weekdays = new Weekday[7];
		int       cursor   = from.index;
		for(int index = 0; index < 7; index++){
			weekdays[index] = index(cursor);
			cursor++;
			if(cursor > 6)
				cursor = 0;
		}
		return weekdays;
	}

	public static Weekday index(int index){
		return weekdays().get(index);
	}

	public static ArrayList<Weekday> weekdays(){
		if(!isValidated){
			validate();
		}
		return weekdays;
	}

	public static void validate(){
		for(Weekday weekday : Weekday.values()){
			int offset = weekday.offset - Weekday.FIRST_DAY_OF_WEEK.offset;
			weekday.index = offset >= 0 ? offset : 7 + offset;
		}
		weekdays.sort(Comparator.comparingInt(o->o.index));
		isValidated = true;
	}

	public static Weekday phrase(int index){
		switch(index){
			case Calendar.MONDAY:
				return Weekday.MONDAY;
			case Calendar.TUESDAY:
				return Weekday.TUESDAY;
			case Calendar.WEDNESDAY:
				return Weekday.WEDNESDAY;
			case Calendar.THURSDAY:
				return Weekday.THURSDAY;
			case Calendar.FRIDAY:
				return Weekday.FRIDAY;
			case Calendar.SUNDAY:
				return Weekday.SUNDAY;
			case Calendar.SATURDAY:
				return Weekday.SATURDAY;
			default:
				return null;
		}
	}

	public static void firstDayOfWeek(Weekday firstDayOfWeek){
		FIRST_DAY_OF_WEEK = firstDayOfWeek;
		validate();
	}
	public static  Weekday            FIRST_DAY_OF_WEEK = Weekday.MONDAY;
	private static boolean            isValidated       = false;
	private static ArrayList<Weekday> weekdays;
	private final  int                offset;
	private final  String             rep;
	private        int                index;
	
	Weekday(int offset, String rep){
		this.offset = offset;
		this.rep = rep;
		add();
	}
	
	public void add(){
		if(weekdays == null)
			weekdays = new ArrayList<>();
		weekdays.add(this);
	}
	
	public int phrase(){
		switch(this){
			case MONDAY:
				return Calendar.MONDAY;
			case TUESDAY:
				return Calendar.TUESDAY;
			case WEDNESDAY:
				return Calendar.WEDNESDAY;
			case THURSDAY:
				return Calendar.THURSDAY;
			case FRIDAY:
				return Calendar.FRIDAY;
			case SATURDAY:
				return Calendar.SATURDAY;
			case SUNDAY:
				return Calendar.SUNDAY;
			default:
				return -1;
		}
	}
	
	public int index(){
		if(!isValidated()){
			validate();
		}
		return index;
	}
	
	public static boolean isValidated(){
		return isValidated;
	}
	
	@Override
	public String toString(){
		return rep;
	}
}
