package mccanny.management.teacher;

import mccanny.util.Date;
import mccanny.util.Distinguishable;
import mccanny.util.ToolTipText;
import mccanny.util.Utility;

import java.util.Collection;
import java.util.HashMap;

public class Teacher implements ToolTipText, Distinguishable, Comparable<Teacher>{
	
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
	
	public static Collection<Teacher> teachers(){
		return teachers.values();
	}
	
	@Override
	public String UID(){
		return UID;
	}
	
	public static Teacher newTeacher(String MEN, String identity, Date birthday, String email){
		return loadTeacher(Utility.fetchUUID32(), MEN, identity, birthday, email);
	}
	
	public static Teacher loadTeacher(String UID, String MEN, String identity, Date birthday, String email){
		// if null means no such teacher exist yet
		// else means such teacher have already been registered
		if(teachers.get(MEN) == null){
			Teacher teacher = new Teacher(UID, MEN, identity, birthday, email);
			teachers.put(MEN, teacher);
			return teacher;
		}else{
			throw new IllegalArgumentException("Teacher with the same MEN have registered (" + teachers.get(MEN) + ")");
		}
	}
	
	public static boolean removeTeacher(Teacher teacher){
		return teachers.remove(teacher.MEN(), teacher);
	}
	
	public String MEN(){
		return MEN;
	}
	
	private static HashMap<String, Teacher> teachers = new HashMap<>();
	private final  String                   UID;
	private        String                   MEN;
	private        String                   identity;
	private        Date                     birthday;
	private        String                   email;
	
	@Override
	public int compareTo(Teacher o){
		int result = identity.compareTo(o.identity);
		if(result == 0){
			return birthday.compareTo(o.birthday);
		}else{
			return result;
		}
	}
	
	private Teacher(String UID, String MEN, String identity, Date birthday, String email){
		this.UID = UID;
		this.identity = identity;
		this.MEN = MEN;
		this.birthday = birthday;
		this.email = email;
	}
	
	public void MEN(String MEN){
		if(this.MEN.equals(MEN))
			return;
		if(teachers.get(MEN) == null){
			teachers.put(MEN, teachers.remove(this.MEN));
			this.MEN = MEN;
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
	
	public Date birthday(){
		return birthday;
	}
	
	public void birthday(Date birthday){
		this.birthday = birthday;
	}
	
	public String email(){
		return email;
	}
	
	public void email(String email){
		this.email = email;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Teacher teacher = (Teacher) o;
		return MEN.equals(teacher.MEN);
	}
	
	@Override
	public String toString(){
		return identity;
	}
	
	@Override
	public String toolTip(){
		return identity + "(" + MEN + ")";
	}
}
