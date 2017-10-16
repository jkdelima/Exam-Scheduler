package examSchedule.parser;

import java.util.Vector;

import examSchedule.Course;
import examSchedule.Environment;
import examSchedule.Instructor;
import examSchedule.Lecture;
import examSchedule.Room;
import examSchedule.Session;
import examSchedule.Student;

public class OrBasedSolution extends Solution {

    public Environment env;
    private int goodness = 0;

    private Vector<Session> assignments = new Vector<Session>();

    public OrBasedSolution(String filename) {
        super(filename);
        // TODO Auto-generated constructor stub
    }

    public void setGoodness(int goodness) {
        this.goodness = goodness;
    }

    @Override
    public boolean isSolved() {
        if (isComplete() && !hasViolations()) {
            return true;
        } else {
            return false;
        }
    }

    // here we check hard constraint 1
    @Override
    public boolean isComplete() {
        boolean complete = true;

        Vector<Lecture> lectures = new Vector<Lecture>();

        for (Session session : assignments) {
            lectures.add(session.getLecture());
        }

        // H1 - Every lecture is assigned an exam session.
        for (Course course : env.courses) {
            if (!course.allLecturesAssigned()) 
                complete = false;
//            for (Lecture lecture : course.getLectures()) {
//                if (!lectures.contains(lecture)) {
//                    complete = false;
//                }
//            }
        }

        return complete;
    }

    // here we check hard constraints 2 through 4, although most of them have been built into the search algorithm.
    @Override
    public boolean hasViolations() {
        boolean violated = false;

        // H2 - no lecture is assigned more than one exam session.
        // this constraint is built into the search heuristic, no lecture may be assigned without
        // removing it from the list of available lectures to be assigned.
        for (Course course : env.courses) {
            for (Lecture lecture : course.getLectures()) {
                int listed = 0;
                for (Session session : assignments) {
                    Lecture temp = session.getLecture();
                    if (temp == lecture)
                        listed++;
                    if (listed > 1)
                        violated = true;
                    break;
                }
            }
        }
        // H3 - the number of students writing an exam in a particular exam session may not exceed the capacity of the room.
        // this constraint is built into the search heuristic, no session can be assigned a lecture if
        // it does not have the capacity left to hold the students of the lecture.
        for (Session session : assignments) {
            Lecture lecture = session.getLecture();
            Long baseStudents = lecture.getNumStudents();
            Room room = session.getRoom();
            Long capacity = room.getCapacity();
            for (Session sessionCheck : env.sessions) {
                if (session == sessionCheck) {
                    Lecture sameSession = sessionCheck.getLecture();
                    Long addStudents = sameSession.getNumStudents();
                    baseStudents += addStudents;
                }
            }
            if (baseStudents > capacity) {
                violated = true;
                break;
            }
        }

        // H4 - every lecture's required time must be less than the session length.
        // this constraint is built into the search heuristic, no lecture even has the opportunity to
        // be assigned to a session without the appropriate length.
        for (Session session : assignments) {
            if (session.getLength() < session.getLecture().getExamLength()) {
                violated = true;
                break;
            }
        }

        return violated;
    }

    // here we check soft constraints to judge the over-all quality of the solution we have generated.
    @Override
    public int getGoodness() {

        // S1 - no student writes more than one exam in a timeslot.
        // -100 goodness for every conflict.
        Vector<Session> studentExams = new Vector<Session>();
        for (Student student : env.students) {

            for (Pair studentPair : student.getClasses()) {
                Lecture studentLecture = (Lecture) studentPair.getValue();

                for (Session session : assignments) {
                    if (studentLecture == session.getLecture()) {
                        studentExams.add(session);
                    }
                }

                // S1
                for (Session sessOne : studentExams) {
                    for (Session sessTwo : studentExams) {
                        if (sessOne != sessTwo && sessOne.getDay() == sessTwo.getDay() && sessOne.getTime() == sessTwo.getTime()) {
                            goodness -= 100;
                        }
                    }
                }
            }

            // studentExams still holds a list of sessions that the student is to write in... we'll use this to do S4 & S5

            // S4 - no student writes for longer than 5 hours in a single day.
            // -50 goodness for every conflict.
            for (String day : env.days) {
                long hours = 0;

                for (Session sessOne : studentExams) {
                    if (day == sessOne.getDay()) {
                        hours += sessOne.getLength();
                    }

                    // S5 - no student should write exams with no break between them.
                    // -50 goodness for every conflict.
                    for (Session sessTwo : studentExams) {
                        if (sessOne != sessTwo) {
                            long finish = sessOne.getTime() + sessOne.getLength();

                            if (finish == sessTwo.getTime()) {
                                goodness -= 50;
                            }
                        }
                    }
                }

                if (hours > 5) {
                    goodness -= 50;
                }
            }

            studentExams.removeAllElements();
        }

        // S2 - no instructor invigulates in more than one room at the same time.
        // -20 goodness for every conflict.
        Vector<Session> instructorExams = new Vector<Session>();
        for (Instructor instructor : env.instructors) {

            for (Lecture instructorLecture : instructor.getLectures()) {

                for (Session session : assignments) {
                    if (instructorLecture == session.getLecture()) {
                        instructorExams.add(session);
                    }
                }

                for (Session sessOne : instructorExams) {
                    for (Session sessTwo : instructorExams) {
                        if (sessOne != sessTwo && sessOne.getDay() == sessTwo.getDay() && sessOne.getTime() == sessTwo.getTime()) {
                            goodness -= 20;
                        }
                    }
                }
            }

            instructorExams.removeAllElements();
        }

        // S3 - every lecture for the same course should have the same exam timeslot.
        // -50 goodness for every conflict.
        Vector<Session> courseExams = new Vector<Session>();
        for (Course course : env.courses) {

            for (Lecture lecture : course.getLectures()) {
                for (Session session : assignments) {
                    if (lecture == session.getLecture()) {
                        courseExams.add(session);
                    }
                }
            }

            for (Session sessOne : courseExams) {
                for (Session sessTwo : courseExams) {
                    if (sessOne != sessTwo && (sessOne.getTime() != sessTwo.getTime() || sessOne.getDay() != sessTwo.getDay())) {
                        goodness -= 50;
                    }
                }
            }

            courseExams.removeAllElements();
        }

        // S6 - all the exams taking place in a particular session should have the same length.
        // -20 goodness for every conflict.
        Vector<Lecture> sessionExams = new Vector<Lecture>();
        for (Session envSession : env.sessions) {

            for (Session session : assignments) {
                if (session == envSession) {
                    sessionExams.add(session.getLecture());
                }
            }

            for (Lecture lectOne : sessionExams) {
                for (Lecture lectTwo : sessionExams) {
                    if (lectOne.getExamLength() != lectTwo.getExamLength()) {
                        goodness -= 20;
                    }
                }
            }

            sessionExams.removeAllElements();
        }

        // S7 - every exam in a session should take up the full time of the session.
        // -5 goodness for every conflict.
        for (Session session : assignments) {

            if ((session.getLength() - (session.getLecture().getExamLength())) > 0) {
                goodness -= 5;
            }
        }

        return goodness;
    }

    // need a loop to print ASSIGN(COURSE, LECTURE, SESSION)
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    public Vector<Session> getAssignments() {
        return assignments;
    }

    public void setAssignments(Vector<Session> assignments) {
        this.assignments = assignments;
    }

}
