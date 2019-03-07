package mccanny.io.TimeTableBuilder;

import mccanny.io.Builder;
import mccanny.management.course.manager.TimeTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public abstract class TimeTableBuilder{
	
	final String VERSION;
	
	TimeTableBuilder(String VERSION){
		this.VERSION = VERSION;
	}
	
	public static TimeTable decode(String path){
		Builder  builder  = new Builder();
		File     file     = new File(path);
		Document document = builder.parse(file);
		if(document == null)
			return null;
		String encodeVersion = ((Element) (document.getElementsByTagName("encoder").item(0))).getAttribute("version");
		switch(encodeVersion){
			case "v1":
				return Builder.TIME_TABLE_V1.decode(document, file);
			default:
				return null;
		}
	}
	
	public abstract boolean encode(TimeTable timeTable, File file);
	
	public abstract TimeTable decode(Document document, File file);
}
