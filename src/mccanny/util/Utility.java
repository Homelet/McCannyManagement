package mccanny.util;

import mccanny.management.course.Course;
import mccanny.management.exception.CourseCollusion;

import java.lang.reflect.Array;
import java.util.*;

public class Utility{
	
	/**
	 * Pad string with a certain padder from start or end.
	 *
	 * @param i             the
	 * @param desiredLength the desired length
	 * @param padder        the padder
	 * @param fromStart     from start or not
	 * @return the string
	 */
	public static String padString(String i, int desiredLength, char padder, boolean fromStart){
		if(i.length() >= desiredLength)
			return i;
		StringBuilder builder = new StringBuilder();
		for(int adder = 0; adder < desiredLength - i.length(); adder++){
			builder.append(padder);
		}
		return (fromStart ? builder.insert(0, i) : builder.append(i)).toString();
	}
	
	/**
	 * Random double.
	 *
	 * @param min the min
	 * @param max the max
	 * @return the double
	 */
	public static double random(double min, double max){
		if(max < min)
			throw new IllegalArgumentException("Max is smaller than min");
		return Math.random() * (max - min) + min;
	}
	
	/**
	 * Random int.
	 *
	 * @param min the min
	 * @param max the max
	 * @return the int
	 */
	public static int randomInt(int min, int max){
		if(max < min)
			throw new IllegalArgumentException("Max is smaller than min");
		return (int) (Math.random() * (max - min) + min);
	}
	
	public static Weekday getWeekday(int year, Month month, int day){
		Calendar calendar = new GregorianCalendar(year, month.index(), day + 1);
		calendar.setFirstDayOfWeek(Weekday.FIRST_DAY_OF_WEEK.phrase());
		return Weekday.phrase(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	public static <E> void compareAll(ArrayList<CourseCollusion> errors, E[] arr, PeriodComparator<E> comparator){
		int   size    = arr.length;
		int[] indexes = new int[]{ 0, 1 };
		do{
			if(arr[indexes[0]] == null || arr[indexes[1]] == null)
				continue;
			comparator.compare(arr[indexes[0]], arr[indexes[1]], errors);
		}while(move_to_next_index(size, 1, indexes));
	}
	
	private static boolean move_to_next_index(int limit, int checkingIndex, int[] indexes){
		int value = indexes[checkingIndex];
		if(value + 1 < limit){
			indexes[checkingIndex] += 1;
			return true;
		}else{
			if(checkingIndex == 0)
				return false;
			indexes[checkingIndex] = indexes[0] + 2;
			return move_to_next_index(limit, checkingIndex - 1, indexes);
		}
	}
	
	public static <E> ArrayList<E> compare(List<E> list_1, List<E> list_2){
		ArrayList<E> e = new ArrayList<>();
		for(E e_1 : list_1){
			for(E e_2 : list_2){
				if(e_1 == e_2)
					e.add(e_1);
			}
		}
		return e;
	}
	
	public static String time(double time, boolean format24){
		int hour   = (int) Math.floor(time);
		int minute = (int) ((time - hour) * 60);
		if(format24){
			return String.valueOf(hour) + ':' + padString(String.valueOf(minute), 2, '0', true);
		}else{
			return (hour > 12 ? String.valueOf(hour - 12) : hour) + ":" + padString(String.valueOf(minute), 2, '0', true) + (hour > 12 ? " PM" : " AM");
		}
	}
	
	public interface PeriodComparator<E>{
		
		void compare(E o1, E o2, ArrayList<CourseCollusion> errors);
	}
}
