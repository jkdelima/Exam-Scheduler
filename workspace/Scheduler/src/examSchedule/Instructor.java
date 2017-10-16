package examSchedule;

import java.util.Vector;
import java.util.Iterator;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima
 * This class creates the Instructor object for our project.
 */
public class Instructor {
	private String name;
	private Vector<Lecture> lectures;

	/**
	 * Instructor constructor.
	 * @param name is the name of the instructor
	 */
	public Instructor(String name) {
		this.name = name;
		lectures = new Vector<Lecture>();
	}

	public Instructor(Instructor instructor) {
	    this.name = new String(instructor.getName());
	    this.lectures = new Vector<Lecture>(instructor.getLectures());
	}

    /**
	 * Adds lecture to object.
	 * @param lecture is the name of the lecture
	 */
	public void addLecture(Lecture lecture) {
		lectures.add(lecture);
		if (!lecture.getInstructor().getName().equals(this.name)) {
			lecture.setInstructor(this);
		}
	}

	/**
	 * Checks if object has the lecture to instruct.
	 * @param course is the name of the course
	 * @param lecture is the name of the lecture
	 * @return true if it instructs the lecture, false otherwise
	 */
	public boolean instructs(String course, String lecture) {
		Iterator<Lecture> iter = lectures.iterator();
		while (iter.hasNext()) {
			if (iter.next().getName().equals(lecture)
					&& iter.next().getCourse().equals(course)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get name method.
	 * @return name of instructor
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Method providing object's string output.
	 * @return the string output
	 */
	public String toString() {
		return "instructor(" + name + ")";
	}

	/**
	 * Instructs string method for the instructor.
	 * @return string output for instructs
	 */
	public String instructs(Lecture lecture) {
			return "instructs(" + name + ", " + lecture.getCourse().getName() + ", " + lecture.getName() + ")";
		}
	
	public Vector<Lecture> getLectures() {
		return lectures;
	}

	/**
	 * Get lecture size method.
	 * @return size of the class
	 */
	public int getLecturesSize() {
		return lectures.size();
	}

}
