package mccanny.io.TimeTableBuilder;

import mccanny.io.Builder;
import mccanny.io.BuilderException;
import mccanny.management.course.Course;
import mccanny.management.course.CoursePeriod;
import mccanny.management.course.manager.TimeTable;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.util.Weekday;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

public class V1 extends TimeTableBuilder{
	
	public V1(){
		super("v1");
	}
	
	@Override
	public boolean encode(TimeTable timeTable, File file){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return false;
		Element root = document.createElement("timetable");
		document.appendChild(root);
		// encoder element
		Element encoder = document.createElement("encoder");
		encoder.setAttribute("version", VERSION);
		root.appendChild(encoder);
		// config element
		Element config = document.createElement("config");
		root.appendChild(config);
		// date element
		Element date = document.createElement("date");
		config.appendChild(date);
		{
			// start element
			Element start = document.createElement("start");
			start.setAttribute("year", timeTable.startDate() != null ? String.valueOf(timeTable.startDate().year()) : "");
			start.setAttribute("month", timeTable.startDate() != null ? String.valueOf(timeTable.startDate().month().index()) : "");
			start.setAttribute("day", timeTable.startDate() != null ? String.valueOf(timeTable.startDate().day()) : "");
			date.appendChild(start);
			// end element
			Element end = document.createElement("end");
			end.setAttribute("year", timeTable.endDate() != null ? String.valueOf(timeTable.endDate().year()) : "");
			end.setAttribute("month", timeTable.endDate() != null ? String.valueOf(timeTable.endDate().month().index()) : "");
			end.setAttribute("day", timeTable.endDate() != null ? String.valueOf(timeTable.endDate().day()) : "");
			date.appendChild(end);
		}
		// name element
		Element name = document.createElement("name");
		name.setAttribute("str", timeTable.name());
		config.appendChild(name);
		// periods element
		Element periods = document.createElement("periods");
		root.appendChild(periods);
		for(CoursePeriod coursePeriod : timeTable.periods()){
			// course element
			Element periodElement = document.createElement("period");
			periods.appendChild(periodElement);
			Element course = document.createElement("course");
			{
				course.setAttribute("UID", coursePeriod.course().UID());
				course.setAttribute("courseID", coursePeriod.course().courseID());
			}
			periodElement.appendChild(course);
			// course element
			Element classRoomNumber = document.createElement("classRoom");
			{
				classRoomNumber.setAttribute("number", String.valueOf(coursePeriod.classroom()));
			}
			periodElement.appendChild(classRoomNumber);
			// period element
			Element time = document.createElement("time");
			{
				time.setAttribute("weekday", String.valueOf(coursePeriod.weekday().offset()));
				time.setAttribute("start", String.valueOf(coursePeriod.start()));
				time.setAttribute("end", String.valueOf(coursePeriod.end()));
			}
			periodElement.appendChild(time);
			// teachers element
			Element teachers = document.createElement("teachers");
			for(Teacher teacher : coursePeriod.teachers()){
				Element teacherEle = document.createElement("teacher");
				teacherEle.setAttribute("UID", teacher.UID());
				teacherEle.setAttribute("MEN", teacher.MEN());
				teachers.appendChild(teacherEle);
			}
			periodElement.appendChild(teachers);
			// teachers element
			Element students = document.createElement("students");
			for(Student student : coursePeriod.students()){
				Element studentEle = document.createElement("student");
				studentEle.setAttribute("UID", student.UID());
				studentEle.setAttribute("OEN", student.OEN());
				students.appendChild(studentEle);
			}
			periodElement.appendChild(students);
		}
		return builder.write(document, file);
	}
	
	@Override
	public TimeTable decode(Document document, File file){
		//get root element
		Element rootElement = document.getDocumentElement();
		Element config      = (Element) rootElement.getElementsByTagName("config").item(0);
		// date
		Element   date      = (Element) config.getElementsByTagName("date").item(0);
		Date      startDate = loadDate((Element) date.getElementsByTagName("start").item(0));
		Date      endDate   = loadDate((Element) date.getElementsByTagName("end").item(0));
		String    name      = ((Element) config.getElementsByTagName("name").item(0)).getAttribute("str");
		TimeTable timeTable = new TimeTable(name, file, startDate, endDate);
		NodeList  periods   = ((Element) rootElement.getElementsByTagName("periods").item(0)).getElementsByTagName("period");
		if(periods != null){
			for(int index = 0; index < periods.getLength(); index++){
				Element element = (Element) periods.item(index);
				// course
				Course course = loadCourse((Element) element.getElementsByTagName("course").item(0));
				// classroom
				int classroom = loadClassroom((Element) element.getElementsByTagName("classRoom").item(0));
				// period
				Element period  = (Element) element.getElementsByTagName("time").item(0);
				Weekday weekday = Weekday.offset(Integer.valueOf(period.getAttribute("weekday")));
				double  start   = Double.valueOf(period.getAttribute("start"));
				double  end     = Double.valueOf(period.getAttribute("end"));
				// course Period
				CoursePeriod coursePeriod = new CoursePeriod(course, classroom, weekday, start, end);
				// teacher
				NodeList teachers = ((Element) element.getElementsByTagName("teachers").item(0)).getElementsByTagName("teacher");
				for(int i = 0; i < teachers.getLength(); i++){
					Teacher teacher = loadTeacher((Element) teachers.item(i));
					if(teacher == null){
						throw new BuilderException("Error Loading teacher");
					}
					coursePeriod.addTeacher(false, teacher);
				}
				coursePeriod.syncTeacherApper();
				// student
				NodeList students = ((Element) element.getElementsByTagName("students").item(0)).getElementsByTagName("student");
				for(int i = 0; i < students.getLength(); i++){
					Student student = loadStudent((Element) students.item(i));
					if(student == null){
						throw new BuilderException("Error Loading Student");
					}
					coursePeriod.addStudent(false, student);
				}
				coursePeriod.syncStudentApper();
				timeTable.add(coursePeriod);
			}
		}
		return timeTable;
	}
	
	public static Date loadDate(Element date){
		String str_year  = date.getAttribute("year");
		String str_month = date.getAttribute("month");
		String str_day   = date.getAttribute("day");
		if(str_day.length() == 0 || str_month.length() == 0 || str_year.length() == 0)
			return null;
		return new Date(Integer.valueOf(str_year), Integer.valueOf(str_month), Integer.valueOf(str_day));
	}
	
	public static int loadClassroom(Element classroom){
		String number = classroom.getAttribute("number");
		return Integer.valueOf(number);
	}
	
	public static Course loadCourse(Element course){
		String UID      = course.getAttribute("UID");
		String courseID = course.getAttribute("courseID");
		Course c        = Course.findCourseByUID(UID);
		if(c == null)
			return Course.findCourse(courseID);
		return c;
	}
	
	public static Student loadStudent(Element student){
		String  UID = student.getAttribute("UID");
		String  OEN = student.getAttribute("OEN");
		Student s   = Student.findStudentByUID(UID);
		if(s == null)
			return Student.findStudent(OEN);
		return s;
	}
	
	public static Teacher loadTeacher(Element teacher){
		String  UID = teacher.getAttribute("UID");
		String  MEN = teacher.getAttribute("MEN");
		Teacher s   = Teacher.findTeacherByUID(UID);
		if(s == null)
			return Teacher.findTeacher(MEN);
		return s;
	}
}