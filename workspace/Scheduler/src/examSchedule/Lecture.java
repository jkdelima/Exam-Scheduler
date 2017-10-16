package examSchedule;

import java.util.Vector;
import java.util.Iterator;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima
 * This class creates the Lecture object for our project.
 */
public class Lecture {

	private Course course;
	private String name;
	private Instructor instructor;
	private Long examLength;
	private Vector<Student> students;
	public boolean assigned;
	
	/**
	 * Lecture constructor.
	 * @param name is the name of the lecture
	 */
	public Lecture(String name){
		this.name = name;
		course = new Course("");
	    instructor = new Instructor("");
	    examLength = new Long(0);
	    students = new Vector<Student>();
	    assigned = false;
	}
	
	public boolean equals(Lecture lecture) {
	    return this.name.equals(lecture.getName());
	}
	
	/**
	 * Lecture constructor.
	 * @param course is the name of the course
	 * @param name is the name of the lecture
	 */
	public Lecture(Course course, String name) {
	    this.course = course;
	    this.name = name;
	    instructor = new Instructor("");
	    examLength = new Long(0);
	    students = new Vector<Student>();
	    assigned = false;
	    
	}
	
	public Lecture(Lecture lecture) {
	    this.course = new Course(lecture.getCourse());
	    this.name = new String(lecture.getName());
	    this.instructor = new Instructor(lecture.getInstructor());
	    this.examLength = new Long(lecture.getExamLength());
	    this.students = new Vector<Student>(lecture.getStudents());
	    assigned = lecture.assigned;
	}

    /**
	 * Checks if object has the student.
	 * @param student is the name of the student
	 * @return true if it has the student, false otherwise
	 */
	public boolean hasStudent(String student) {
        Iterator<Student> iter = students.iterator();
        while (iter.hasNext()) {
            if (iter.next().getName().equals(student)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Method to find the student.
	 * @param student is the name of the student
	 * @return the student name, null otherwise.
	 */
    public Student findStudent(String student) {
        Iterator<Student> iter = students.iterator();
        while (iter.hasNext()) {
            if (iter.next().getName().equals(student)) {
                return iter.next();
            }
        }
        return null;
    }
    
    public Vector<Student> getStudents() {
        return students;
    }

	/**
	 * Adds student to object.
	 * @param student is the name of the student
	 */    
    public void addStudent(Student student) {
        students.add(student);
        student.addLecture(this);
    }

    /**
	 * The following are get methods for lecture characteristics.
	 * @return name, course, instructor, and exam length of lecture
	 */
    
    public String getName() {
	    return name;
	}

	public Course getCourse() {
	    return course;
	}

	public Instructor getInstructor() {
	    return this.instructor;
	}

	public Long getExamLength() {
	    return examLength;
	}
	
	public Long getNumStudents() {
		return new Long(students.size());
	}

	/**
	 * Method that sets instructor of lecture
	 * @param instructor is the name of the instructor
	 */
	public void setInstructor(Instructor instructor){
		this.instructor = instructor;
		instructor.addLecture(this);
	}

	/**
	 * Method that sets exam length of lecture
	 * @param length is the length of the exam
	 */	
	public void setExamLength(Long length){
		this.examLength = length;
	}
	
	/**
	 * Method providing object's string output.
	 * @return the string output
	 */
	public String toString() {
		//return "lecture(" + course + ", " + name + ")";
		return "lecture(" + course + ", " + name + ", " + instructor + ", " + examLength + ")";
	}
	
	public String toStringTwo() {
		return "lecture(" + course + ", " + name + ")";
	}
	
	public String toExamLength() {
		return "examLength(" + course + ", " + name + ", " + examLength + ")";
	}
}
