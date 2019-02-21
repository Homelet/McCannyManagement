package mccanny.management;

import java.util.ArrayList;

public class Loger{
	
	private static Loger loger = new Loger();
	
	private static void log(Log log){
		loger.logs.add(log);
	}
	
	final ArrayList<Log> logs;
	
	private Loger(){
		logs = new ArrayList<>();
	}
}
