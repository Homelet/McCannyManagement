package mccanny.io;

import mccanny.io.TimeTableBuilder.TimeTableBuilder;
import mccanny.io.TimeTableBuilder.V1;
import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import mccanny.util.Date;
import mccanny.util.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Builder{
	
	public static final TimeTableBuilder       TIME_TABLE_V1  = new V1();
	private static      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	public Document parse(File file){
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			return builder.parse(file);
		}catch(ParserConfigurationException | SAXException | IOException e){
			return null;
		}
	}
	
	public Document newDocument(){
		try{
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			return documentBuilder.newDocument();
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean write(Document DOM, File file){
		try{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer        transformer        = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource    domSource    = new DOMSource(DOM);
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(domSource, streamResult);
			return true;
		}catch(TransformerException e){
			return false;
		}
	}
	
	public static boolean loadStudents(){
		Builder  builder  = new Builder();
		Document document = builder.parse(new File(Utility.join("data", "students.xml")));
		if(document == null)
			return false;
		//get root element
		Element  rootElement = document.getDocumentElement();
		NodeList nodeList    = rootElement.getElementsByTagName("student");
		if(nodeList != null){
			for(int i = 0; i < nodeList.getLength(); i++){
				Element element  = (Element) nodeList.item(i);
				String  identity = element.getElementsByTagName("identity").item(0).getTextContent();
				String  OEN      = element.getElementsByTagName("OEN").item(0).getTextContent();
				String  email    = element.getElementsByTagName("email").item(0).getTextContent();
				Date    birthday = V1.loadDate((Element) element.getElementsByTagName("birthday").item(0));
				String  UID      = element.getElementsByTagName("UID").item(0).getTextContent();
				Student.loadStudent(UID, OEN, identity, birthday, email);
			}
		}
		return true;
	}
	
	public static boolean loadTeachers(){
		Builder  builder  = new Builder();
		Document document = builder.parse(new File(Utility.join("data", "teachers.xml")));
		if(document == null)
			return false;
		//get root element
		Element  rootElement = document.getDocumentElement();
		NodeList nodeList    = rootElement.getElementsByTagName("teacher");
		if(nodeList != null){
			for(int i = 0; i < nodeList.getLength(); i++){
				Element element  = (Element) nodeList.item(i);
				String  identity = element.getElementsByTagName("identity").item(0).getTextContent();
				String  MEN      = element.getElementsByTagName("MEN").item(0).getTextContent();
				String  email    = element.getElementsByTagName("email").item(0).getTextContent();
				Date    birthday = V1.loadDate((Element) element.getElementsByTagName("birthday").item(0));
				String  UID      = element.getElementsByTagName("UID").item(0).getTextContent();
				Teacher.loadTeacher(UID, MEN, identity, birthday, email);
			}
		}
		return true;
	}
	
	public static boolean loadCourses(){
		Builder  builder  = new Builder();
		Document document = builder.parse(new File(Utility.join("data", "courses.xml")));
		if(document == null)
			return false;
		//get root element
		Element  rootElement = document.getDocumentElement();
		NodeList nodeList    = rootElement.getElementsByTagName("course");
		if(nodeList != null){
			for(int i = 0; i < nodeList.getLength(); i++){
				Element element    = (Element) nodeList.item(i);
				String  courseID   = element.getElementsByTagName("courseID").item(0).getTextContent();
				String  courseHour = element.getElementsByTagName("courseHour").item(0).getTextContent();
				String  color      = element.getElementsByTagName("color").item(0).getTextContent();
				String  UID        = element.getElementsByTagName("UID").item(0).getTextContent();
				Course.loadCourse(UID, courseID, Double.valueOf(courseHour), new Color(Integer.valueOf(color, 16)));
			}
		}
		return true;
	}
	
	public static boolean writeStudents(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return false;
		// root element
		Element root = document.createElement("students");
		document.appendChild(root);
		for(Student student : Student.students()){
			// student element
			Element stdElement = document.createElement("student");
			root.appendChild(stdElement);
			// identity element
			Element identity = document.createElement("identity");
			identity.appendChild(document.createTextNode(student.identity()));
			stdElement.appendChild(identity);
			// OEN element
			Element OEN = document.createElement("OEN");
			OEN.appendChild(document.createTextNode(student.OEN()));
			stdElement.appendChild(OEN);
			// email element
			Element email = document.createElement("email");
			email.appendChild(document.createTextNode(student.email()));
			stdElement.appendChild(email);
			// birthday element
			Element birthday = document.createElement("birthday");
			Date    date     = student.birthday();
			birthday.setAttribute("year", date != null ? String.valueOf(date.year()) : "");
			birthday.setAttribute("month", date != null ? String.valueOf(date.month().index()) : "");
			birthday.setAttribute("day", date != null ? String.valueOf(date.day()) : "");
			stdElement.appendChild(birthday);
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(student.UID()));
			stdElement.appendChild(UID);
		}
		return builder.write(document, new File(Utility.join("data", "students.xml")));
	}
	
	public static boolean writeTeachers(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return false;
		// root element
		Element root = document.createElement("teachers");
		document.appendChild(root);
		for(Teacher teacher : Teacher.teachers()){
			// teacher element
			Element teaElement = document.createElement("teacher");
			root.appendChild(teaElement);
			// identity element
			Element identity = document.createElement("identity");
			identity.appendChild(document.createTextNode(teacher.identity()));
			teaElement.appendChild(identity);
			// MEN element
			Element MEN = document.createElement("MEN");
			MEN.appendChild(document.createTextNode(teacher.MEN()));
			teaElement.appendChild(MEN);
			// email element
			Element email = document.createElement("email");
			email.appendChild(document.createTextNode(teacher.email()));
			teaElement.appendChild(email);
			// birthday element
			Element birthday = document.createElement("birthday");
			Date    date     = teacher.birthday();
			birthday.setAttribute("year", date != null ? String.valueOf(date.year()) : "");
			birthday.setAttribute("month", date != null ? String.valueOf(date.month().index()) : "");
			birthday.setAttribute("day", date != null ? String.valueOf(date.day()) : "");
			teaElement.appendChild(birthday);
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(teacher.UID()));
			teaElement.appendChild(UID);
		}
		return builder.write(document, new File(Utility.join("data", "teachers.xml")));
	}
	
	public static boolean writeCourses(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return false;
		// root element
		Element root = document.createElement("courses");
		document.appendChild(root);
		for(Course course : Course.courses()){
			// course element
			Element courElement = document.createElement("course");
			root.appendChild(courElement);
			// courseID element
			Element courseID = document.createElement("courseID");
			courseID.appendChild(document.createTextNode(course.courseID()));
			courElement.appendChild(courseID);
			// courseHour element
			Element courseHour = document.createElement("courseHour");
			courseHour.appendChild(document.createTextNode(String.valueOf(course.courseHour())));
			courElement.appendChild(courseHour);
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(course.UID()));
			courElement.appendChild(UID);
			// Color element
			Element color = document.createElement("color");
			color.appendChild(document.createTextNode(Integer.toString(course.color().getRGB(), 16)));
			courElement.appendChild(color);
		}
		return builder.write(document, new File(Utility.join("data", "courses.xml")));
	}
}
