package mccanny.util;

import mccanny.management.course.CoursePeriod;

import java.util.Arrays;

public class PeriodBuffer{
	
	CoursePeriod[] arr;
	int            size;
	
	public PeriodBuffer(){
		this.arr = new CoursePeriod[8];
		this.size = 0;
	}
	
	public int size(){
		return size;
	}
	
	public CoursePeriod[] arr(){
		return arr;
	}
	
	private void expand(){
		this.arr = Arrays.copyOf(arr, (int) Math.ceil(this.arr.length * 2));
	}
	
	public synchronized int join(CoursePeriod period){
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == null){
				arr[i] = period;
				size++;
				return i;
			}
		}
		expand();
		return join(period);
	}
	
	public synchronized void remove(int index){
		arr[index] = null;
		size--;
	}
}
