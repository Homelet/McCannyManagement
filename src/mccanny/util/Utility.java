package mccanny.util;

import homelet.GH.utils.Alignment;
import mccanny.management.exception.CourseCollusion;
import mccanny.visual.Display;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

public class Utility{
	
	public static final String FILE_SEPARATOR = File.separator;
	
	public static String flag(int flag){
		switch(flag){
			case STUDENT_FLAG:
				return "Student";
			case TEACHER_FLAG:
				return "Teacher";
			case COURSE_FLAG:
				return "Course";
		}
		return null;
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
		return (fromStart ? builder.append(i) : builder.insert(0, i)).toString();
	}
	
	public static Color translateColor(String context){
		context = context.toLowerCase().trim();
		if(context.startsWith("#")){
			return Color.decode(context);
		}else{
			context = discardSpace(context);
			String[] groups = context.split(",");
			switch(groups.length){
				case 3:
					return new Color(Integer.valueOf(groups[0]), Integer.valueOf(groups[1]), Integer.valueOf(groups[2]));
				case 4:
					return new Color(Integer.valueOf(groups[0]), Integer.valueOf(groups[1]), Integer.valueOf(groups[2], Integer.valueOf(groups[3])));
				default:
					return null;
			}
		}
	}
	
	public static String discardSpace(String string){
		StringBuilder builder = new StringBuilder();
		for(char c : string.toCharArray()){
			if(c == ' ')
				continue;
			builder.append(c);
		}
		return builder.toString();
	}
	
	/**
	 * Determine the Position of the value which between two peaks
	 * <br>
	 * if know the two Comparable's large, use {@link Utility#betweenPeaks(Comparable, Comparable, Comparable)}
	 *
	 * @param <E>     the type parameter
	 * @param value   value that needs to be compared
	 * @param peakOne one value
	 * @param peakTwo two value
	 * @return <ul> <li>-2 value is smaller than minimum</li> <li>-1 value is equals than minimum</li> <li>0 value in range</li> <li>1 value is equals than max</li> <li>2 value is bigger than max</li> </ul>
	 * @author HomeletWei
	 */
	public static <E extends Comparable<E>> int between(E value, E peakOne, E peakTwo){
		return betweenPeaks(value, peakOne.compareTo(peakTwo) >= 0 ? peakOne : peakTwo, peakTwo.compareTo(peakOne) <= 0 ? peakTwo : peakOne);
	}
	
	/**
	 * Determine the Position of the value which between two peaks
	 *
	 * @param <E>     the type parameter
	 * @param value   value that needs to be compared
	 * @param maxPeak max peak
	 * @param minPeak minimum peak
	 * @return <ul> <li>-2 value is smaller than minimum peak</li> <li>-1 value is equals than minimum peak</li> <li>0 value in range</li> <li>1 value is equals than max peak</li> <li>2 value is bigger than max peak</li> </ul>
	 * @author HomeletWei
	 */
	public static <E extends Comparable<E>> int betweenPeaks(E value, E maxPeak, E minPeak){
		if(value.compareTo(minPeak) < 0){
			return -2;
		}else if(value.compareTo(minPeak) == 0){
			return -1;
		}else{
			if(value.compareTo(maxPeak) > 0){
				return 2;
			}else if(value.compareTo(maxPeak) == 0){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	public static Point frameVertex(Rectangle frame, Rectangle objectFrame){
		Point vertex = Alignment.CENTER.getVertex(false, frame, objectFrame);
		if(vertex.x < 0)
			vertex.x = 0;
		if(vertex.y < 0)
			vertex.y = 0;
		if(vertex.x + objectFrame.width > Display.SCREEN_DIMENSION.width)
			vertex.x = Display.SCREEN_DIMENSION.width - objectFrame.width;
		if(vertex.y + objectFrame.height > Display.SCREEN_DIMENSION.height)
			vertex.y = Display.SCREEN_DIMENSION.height - objectFrame.height;
		return vertex;
	}
	
	public static String toString(List objects, int limit){
		StringBuilder builder = new StringBuilder();
		for(int index = 0; index < objects.size(); index++){
			if(index >= limit){
				builder.append(" ...").append(objects.size() - limit).append(" more");
				break;
			}else{
				builder.append(objects.get(index));
				if(index + 1 < objects.size())
					builder.append(", ");
			}
		}
		return builder.toString();
	}
	
	public static String fetchUUID32(){
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
	
	public static final int STUDENT_FLAG = 0;
	public static final int TEACHER_FLAG = 1;
	public static final int COURSE_FLAG  = 2;
	
	public static <E> boolean identical(Collection<E> collection1, Collection<E> collection2){
		if(collection1.size() != collection2.size())
			return false;
		for(E item : collection1){
			if(!collection2.contains(item)){
				return false;
			}
		}
		return true;
	}
	
	public static <E> boolean identical(E[] arr1, E[] arr2){
		if(arr1 == arr2)
			return true;
		if(arr1 == null || arr2 == null)
			return false;
		if(arr1.length != arr2.length)
			return false;
		for(E e : arr1){
			if(Arrays.binarySearch(arr2, e) < 0){
				return false;
			}
		}
		return true;
	}
	
	public static String join(String root, String... layers){
		StringBuilder builder = new StringBuilder(root);
		for(String s : layers)
			builder.append(FILE_SEPARATOR).append(s);
		return builder.toString();
	}
	
	public interface PeriodComparator<E>{
		
		void compare(E o1, E o2, ArrayList<CourseCollusion> errors);
	}
}
