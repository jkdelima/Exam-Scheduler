package examSchedule;

import java.util.Iterator;
import java.util.Vector;

import examSchedule.parser.Pair;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima
 * This class creates the Student object for our project.
 */
public class Student {
	
	private String name;
	private Vector<Pair<Course, Lecture>> classes;
	
	/**
	 * Student constructor.
	 * @param nm is the name of the student
	 */
	public Student(String nm){
	    name = nm;
	    classes = new Vector<Pair<Course, Lecture>>();
	};
	
	/**
	 * Adds lecture to object.
	 * @param lecture is the name of the lecture
	 */
	public void addLecture(Lecture lecture) {
		Course courseName = lecture.getCourse();
		classes.add(new Pair<Course, Lecture>(courseName, lecture));
		if (!lecture.hasStudent(this.name)) {
		    lecture.addStudent(this);
		}
	};
	
	/**
	 * Checks if object has the lecture.
	 * @param course is the name of the course
	 * @param lecture is the name of the lecture
	 * @return true if it has the lecture, false otherwise
	 */
	public boolean hasClass(Course course, String lecture) {
		Iterator<Pair<Course, Lecture>> iter = classes.iterator();
		while(iter.hasNext()){
			if(iter.next().getValue().getName().equals(lecture) && iter.next().getKey().equals(course)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get name method.
	 * @return name of student
	 */
	public String getName() {
	    return name;
	}
	
	public Vector<Pair<Course, Lecture>> getClasses() {
		return classes;
	}
	
	/**
	 * Method converting object to string output.
	 * @return the string output
	 */
	public String toString() {
		return "student(" + name + ")";
	}
	
	/**
	 * Enrolled string method for the student.
	 * @return string for enrollment
	 */
	public String toEnrolled(Lecture lecture) {
		return "enrolled(" + name + ", " + lecture.getCourse().getName() + ", " + lecture.getName() + ")";
	}

	public String toEnrolledList() {
		String studentString = "enrolled(" + name + ", [";
		
		for (Pair<Course, Lecture> pair : classes) {
			studentString += pair.getKey().getName() + ", " + pair.getValue().getName() + ", ";
		}
		
		//remove the last ", "
		String enrolledList = studentString.substring(0, studentString.length()-1);

		return enrolledList + "])";
}
	
	/**
	 * Get lecture size method.
	 * @return size of the class
	 */
	public int getLecturesSize() {
		return classes.size();
	}
}
