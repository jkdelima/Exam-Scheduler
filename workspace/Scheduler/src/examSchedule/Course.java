package examSchedule;

import java.util.Vector;
import java.util.Iterator;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima This class creates the Course object for our project.
 */
public class Course {

    private Vector<Lecture> lectures;
    private String name;

    /**
     * Course constructor.
     * 
     * @param name
     *            is the name of the course
     */
    public Course(String name) {
        this.name = name;
        lectures = new Vector<Lecture>();
    }

    public Course(Course course) {
        this.lectures = new Vector<Lecture>(course.getLectures());
        this.name = new String(course.getName());
    }

    /**
     * Adds lecture to object.
     * 
     * @param lecture
     *            is the name of the lecture
     */
    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
    }

    /**
     * Checks if object has the lecture.
     * 
     * @param lecture
     *            is the name of the lecture
     * @return true if it has the lecture, false otherwise
     */
    public boolean hasLecture(String lecture) {
        Iterator<Lecture> iter = lectures.iterator();
        while (iter.hasNext()) {
            if (iter.next().getName().equals(lecture)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to find the lecture.
     * 
     * @param lecture
     *            is the name of the lecture
     * @return the lecture name, null otherwise.
     */
    public Lecture findLecture(String lecture) {
        Iterator<Lecture> iter = lectures.iterator();
        while (iter.hasNext()) {
            Lecture temp = iter.next();
            if (temp.getName().equals(lecture)) {
                return temp;
            }
        }
        return null;
    }

    public void removeLecture(Lecture lecture) {
        this.lectures.remove(lecture);
    }

    public boolean allLecturesAssigned() {
        for (Lecture lecture : lectures) {
            if (!lecture.assigned) 
                return false;
        }
        return true;
    }

    public Vector<Lecture> getLectures() {
        return lectures;
    }

    /**
     * Get name method.
     * 
     * @return name of course
     */
    public String getName() {
        return name;
    }

    /**
     * Method providing object's string output.
     * 
     * @return the string output
     */
    public String toString() {
        String course = "course(" + name + ")";

        return course;
    }

    public boolean equals(Course course) {
        return this.name.equals(course.getName());
    }
}
