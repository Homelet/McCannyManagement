package mccanny.io;

import mccanny.management.course.Course;
import mccanny.management.student.Student;
import mccanny.management.teacher.Teacher;
import org.w3c.dom.*;
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
import java.io.File;
import java.io.IOException;

public class Builder{
	
	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	private Document parse(String filePath){
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			return builder.parse(new File(filePath));
		}catch(ParserConfigurationException | SAXException | IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private Document newDocument(){
		try{
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			return documentBuilder.newDocument();
		}catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void write(Document DOM, String path){
		try{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer        transformer        = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource    domSource    = new DOMSource(DOM);
			StreamResult streamResult = new StreamResult(new File(path));
			transformer.transform(domSource, streamResult);
		}catch(TransformerException e){
			e.printStackTrace();
		}
	}
	
	public static void parseTest(){
		Builder  parser   = new Builder();
		Document document = parser.parse("data\\book.xml");
		if(document == null)
			return;
		//get root element
		Element rootElement = document.getDocumentElement();
		//traverse child elements
		NodeList nodes = rootElement.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element child = (Element) node;
				//process child element
				System.out.println(child.getElementsByTagName("title").item(0).getTextContent());
				System.out.println(child.getElementsByTagName("author").item(0).getTextContent());
				System.out.println("================");
			}
		}
		NodeList nodeList = rootElement.getElementsByTagName("book");
		if(nodeList != null){
			for(int i = 0; i < nodeList.getLength(); i++){
				Element element = (Element) nodeList.item(i);
				String  id      = element.getAttribute("id");
				System.out.println("Id : " + id);
			}
		}
	}
	
	public static void writerTest(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return;
		document.setStrictErrorChecking(true);
		// root element
		Element root = document.createElement("company");
		document.appendChild(root);
		// employee element
		Element employee = document.createElement("employee");
		root.appendChild(employee);
		// set an attribute to staff element
		Attr attr = document.createAttribute("id");
		attr.setValue("10");
		employee.setAttributeNode(attr);
		//you can also use staff.setAttribute("id", "1") for this
		// firstname element
		Element firstName = document.createElement("firstname");
		firstName.appendChild(document.createTextNode("James"));
		employee.appendChild(firstName);
		// lastname element
		Element lastname = document.createElement("lastname");
		lastname.appendChild(document.createTextNode("Harley"));
		employee.appendChild(lastname);
		// email element
		Element email = document.createElement("email");
		email.appendChild(document.createTextNode("james@example.org"));
		employee.appendChild(email);
		// department elements
		Element department = document.createElement("department");
		department.appendChild(document.createTextNode("Human Resources"));
		employee.appendChild(department);
		builder.write(document, "data\\doc.xml");
	}
	
	public static void writeStudents(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return;
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
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(student.UID()));
			stdElement.appendChild(UID);
		}
		builder.write(document, "data\\students.xml");
	}
	
	public static void writeTeachers(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return;
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
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(teacher.UID()));
			teaElement.appendChild(UID);
		}
		builder.write(document, "data\\teachers.xml");
	}
	
	public static void writeCourses(){
		Builder  builder  = new Builder();
		Document document = builder.newDocument();
		if(document == null)
			return;
		// root element
		Element root = document.createElement("courses");
		document.appendChild(root);
		for(Course course : Course.courses()){
			// student element
			Element courElement = document.createElement("course");
			root.appendChild(courElement);
			// courseID element
			Element identity = document.createElement("courseID");
			identity.appendChild(document.createTextNode(course.courseID()));
			courElement.appendChild(identity);
			// courseHour element
			Element MEN = document.createElement("courseHour");
			MEN.appendChild(document.createTextNode(String.valueOf(course.courseHour())));
			courElement.appendChild(MEN);
			// UID element
			Element UID = document.createElement("UID");
			UID.appendChild(document.createTextNode(course.UID()));
			courElement.appendChild(UID);
		}
		builder.write(document, "data\\courses.xml");
	}
}
