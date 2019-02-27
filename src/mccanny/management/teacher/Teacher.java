package mccanny.management.teacher;

import mccanny.util.Distinguishable;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;

import java.util.Collection;
import java.util.HashMap;

public class Teacher implements ToolTipText, Distinguishable{
	
	private static HashMap<String, Teacher> teachers = new HashMap<>();
	
	public static Collection<Teacher> teachers(){
		return teachers.values();
	}
	
	public static Teacher findTeacher(String MEN){
		return teachers.get(MEN);
	}
	
	public static Teacher findTeacherByUID(String UID){
		for(Teacher teacher : teachers()){
			if(teacher.UID().equals(UID))
				return teacher;
		}
		return null;
	}
	
	public static Teacher loadTeacher(String UID, String MEN, String identity){
		// if null means no such teacher exist yet
		// else means such teacher have already been registered
		if(teachers.get(MEN) == null){
			Teacher teacher = new Teacher(UID, MEN, identity);
			teachers.put(MEN, teacher);
			return teacher;
		}else{
			throw new IllegalArgumentException("Teacher with the same MEN have registered (" + teachers.get(MEN) + ")");
		}
	}
	
	public static Teacher newTeacher(String MEN, String identity){
		return loadTeacher(Utility.fetchUUID32(), MEN, identity);
	}
	
	public static boolean removeTeacher(Teacher teacher){
		return teachers.remove(teacher.MEN(), teacher);
	}
	
	private final String UID;
	private       String MEN;
	private       String identity;
	
	private Teacher(String UID, String MEN, String identity){
		this.UID = UID;
		this.identity = identity;
		this.MEN = MEN;
	}
	
	public String MEN(){
		return MEN;
	}
	
	public boolean MEN(String MEN){
		if(this.MEN.equals(MEN))
			return false;
		if(teachers.get(MEN) == null){
			teachers.put(MEN, teachers.remove(this.MEN));
			this.MEN = MEN;
			return true;
		}else
			throw new IllegalArgumentException("Teacher with the same MEN have registered (" + teachers.get(MEN) + ")");
	}
	
	public String identity(){
		return identity;
	}
	
	public boolean identity(String identity){
		if(this.identity.equals(identity))
			return false;
		this.identity = identity;
		return true;
	}
	
	@Override
	public String toString(){
		return identity;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Teacher teacher = (Teacher) o;
		return MEN.equals(teacher.MEN);
	}
	
	@Override
	public String toolTip(){
		return identity + "(" + MEN + ")";
	}
	
	@Override
	public String UID(){
		return UID;
	}
}
