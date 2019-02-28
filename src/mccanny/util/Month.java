package mccanny.util;

public enum Month{
	JANUARY(0, "January"),
	FEBRUARY(1, "February"),
	MARCH(2, "March"),
	APRIL(3, "April"),
	MAY(4, "May"),
	JUNE(5, "June"),
	JULY(6, "July"),
	AUGUST(7, "August"),
	SEPTEMBER(8, "September"),
	OCTOBER(9, "October"),
	NOVEMBER(10, "November"),
	DECEMBER(11, "December");
	private final int    index;
	private final String rep;
	
	Month(int index, String rep){
		this.index = index;
		this.rep = rep;
	}
	
	public int index(){
		return index;
	}
	
	public Month next(){
		return index(index + 1);
	}
	
	public static Month index(int index){
		if(index < 0 || index > 11){
			throw new IllegalArgumentException("Don't have month " + index);
		}
		return Month.values()[index];
	}
	
	public int days(int year){
		switch(this.index){
			case 1:
				if(((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){
					return 29;
				}else{
					return 28;
				}
			case 0:
			case 2:
			case 4:
			case 6:
			case 7:
			case 9:
			case 11:
				return 31;
			case 3:
			case 5:
			case 8:
			case 10:
				return 30;
			default:
				return -1;
		}
	}
	
	@Override
	public String toString(){
		return rep;
	}
}
