package examSchedule.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;

import examSchedule.*;

/**
 * This class can function as the main() for your assignment program. Use it in conjunction with the PredicateReader class. You will have to define an environment, which here is represented by the
 * class Environment (you will have to define your own Environment class). Usually, it would be a subclass of PredicateReader.
 * 
 * <p>
 * Copyright: Copyright (c) 2003-2007, Department of Computer Science, University of Calgary. Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and that both that copyright notice and this permission notice appear in supporting documentation. The
 * Department of Computer Science makes no representations about the suitability of this software for any purpose. It is provided "as is" without express or implied warranty.
 * </p>
 * 
 * @author <a href="http://www.cpsc.ucalgary.ca/~kremer/">Rob Kremer</a>
 * 
 */
public class ExamSchedule {

    /**
     * Interpret the command line, and either run in command mode, perform a search, or evaluate a solution. There are four acceptable forms of the command line:
     * <p>
     * <ol>
     * <li>&lt;no params&gt;<blockquote>operates in " {@link #commandMode(EnvironmentInterface) command mode}"</blockquote>
     * <li><em>problemFilename</em> <blockquote>reads problemFilename as a data file and then enters "{@link #commandMode(EnvironmentInterface) command mode}"</blockquote>
     * <li><em>problemFilename long</em> <blockquote>reads problemFilename and then attempts to perform a {@link #doSearch(EnvironmentInterface, String, long) search} for up to <em>long</em>
     * milliseconds</blockquote>
     * <li><em>problemFilename solutionFilename</em> <blockquote>reads problemFilename and solutionFilename and prints out an evaluation of the solution</blockquote>
     * </ol>
     * Form 3 is the one need to use for the assessment of the assignment. The other forms are usually useful for development and testing.
     * <p>
     * The specified input file (in forms 2-4) is read by calling {@link EnvironmentInterface#fromFile(String)}.
     * 
     * @param args
     *            the arguments from the command line
     */
    public static void main(String[] args) {

        final EnvironmentInterface env = Environment.get();

        // long startTime = System.currentTimeMillis();

        String fromFile = null;

        // intialize the the environment from the filename in the first argument
        // on the command line if it's there
        if (args.length > 0) {
            fromFile = args[0];
            env.fromFile(fromFile);
        } else {
            System.out.println("Synopsis: ExamSchedule <env-file> [<solution-file>|<time-in-ms>]");
        }
        // if there's a second argument on the command line, it's either
        // a solution file name or a time in milliseconds to limit our run to...
        if (args.length > 1) {
            // let's assume it's a time in milliseconds: we'll do a search on
            // it.

            try {
                long timeLimit = new Long(args[1]).longValue();

                doSearch(env, fromFile + ".out", timeLimit);
            }
            // not a time, so it must be a filename to read a solution to
            // evaluate from...
            catch (NumberFormatException ex) {
                // env.setCurrentSolution(new Solution(args[1]));
            }

            // if we did something usefull above, print the results...
            if (env.getCurrentSolution() != null) {
                // //System.out.println(currentSolution.toString());
                System.out.println(((Solution) env.getCurrentSolution()).getName() + ": isSolved()    -> " + env.getCurrentSolution().isSolved());
                System.out.println(((Solution) env.getCurrentSolution()).getName() + ": getGoodness() -> " + env.getCurrentSolution().getGoodness());
            }

        }
        // The command line had either no arguments or just a input data file
        // (from which we've
        // already initialized (or attempted), so we'll enter command mode...
        else {
            commandMode(env);
        }
    }

    /**
     * Do a search given the current environment (containing the problem data), writing it out to <em>outFileName</em>, and limiting ourselves to <em>timeLimit</em> milliseconds of search. This method
     * uses the arguably clever ploy of using a shutdown hook to save the results if we terminate abnormally or are killed before we get around to shutting down ourselves.
     * 
     * @param env2
     *            The environment object that contains all the info about the current situation
     * @param outFileName
     *            The name of the file to output the solution to.
     * @param timeLimit
     *            The number of milliseconds to limit he search to.
     */
    public static void doSearch(final EnvironmentInterface env, final String outFileName, final long timeLimit) {

        // here we cast the environment interface passed to us to a true environment.
        Environment env2 = (Environment) env;

        // we set up an old solution to be our base case (nothing has been solved)
        OrBasedSolution oldSolution = new OrBasedSolution(outFileName);
        oldSolution.env = env2; // THIS LINE WAS CHANGED TO SET THE ENVIRONMENT IN THE OLD SOLUTION.

        // we start our time limit with the time passed to us...
        long searchStartTime = System.currentTimeMillis();
        // and start counting.
        long currentTime = 0;

        // loop until we have just enough time to output our solution
        while (timeLimit >= currentTime - searchStartTime) {
            //System.out.println("\n=================\n STARTING SEARCH\n=================\n");

            // we don't want to override our environment variables so we make duplicates to work with.
            Vector<Course> courseCopy = new Vector<Course>(env2.courses);
            Vector<Session> sessionCopy = new Vector<Session>(env2.sessions);
            /*
             * NICK THINKS: FOR THE ABOVE: SMALL CHANGES: courseCopy and sessionCopy declarations can both be moved outside this while loop as we can assign them a value from within the while loop and
             * avoid constantly creating new variables that will never be reused.
             */

            // we assure that every lecture has been flagged as unassigned...
            for (Course crs : courseCopy) {
                for (Lecture lec : crs.getLectures())
                    lec.assigned = false;
            }

            // and that every session's capacity is set to it's original capacity.
            for (Session ss : sessionCopy) {
                ss.resetRemainingCapacity();
            }

            // we generate a new solution to apply our assignments in...
            OrBasedSolution newSolution = new OrBasedSolution(outFileName);
            newSolution.env = env2; // THIS LINE WAS CHANGED TO SET THE ENVIRONMENT IN THE NEW SOLUTION.

            // and then call our recursive search function to generate a set of assignments.
            newSolution.setAssignments(recursiveTreeSearch(new Vector<Session>(), courseCopy, sessionCopy));
            currentTime = System.currentTimeMillis();

            if (oldSolution.getGoodness() == 0)
                oldSolution.setGoodness(Integer.MIN_VALUE);

            // if the solution we generate is better than the one we previously had, we update our old solution.
            if (oldSolution.getGoodness() < newSolution.getGoodness()) {
                oldSolution = newSolution;
                env2.setCurrentSolution(newSolution); // THIS LINE WAS ALTERED TO CHANGE THE CURRENT SOLUTION IN THE ENVIRONMENT.
            }

            // Check against hard constraints. if violated, try search again without writing to file
            if (!oldSolution.isSolved()) {
                //System.out.println("HARD CONSTRAINTS WERE VIOLATED. START OVER."); // Debug purposes
                continue;
            }

        }

        // after the time is up, we send an alert to the console...
        //System.out.println("\n======================================");
        //System.out.println("SEARCH COMPLETE (took " + (currentTime - searchStartTime) + " milliseconds)");

        // and then print our results to the output filename we were given.
        PrintWriter toFile;
        try {
            toFile = new PrintWriter(outFileName, "UTF-8");

            for (Session session : oldSolution.getAssignments()) {
                toFile.println("assign(" + session.getCourse().getName() + ", " + session.getLecture().getName() + ", " + session.getName() + ")");
            }

            toFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*
     * NICK THINKS: FOR RECURSIVESEARCH: PROBLEM: we have a huge issue that I cannot believe we didn't notice, but we have no way to force the recursive search (even if it DOES back-up all the way to
     * the original call) to go to the next course in the set of courses we have given it, I've included some pseudo-code below in an attempt to show what we might need to do, ctrl-f "FORLOOP?"
     */
    // recursive search function - returns a vector of sessions,
    // each session has an assigned lecture,
    // each lecture holds the name of it's associated course,
    // this is how we print our results for Professor Kremer's program to analyze.
    public static Vector<Session> recursiveTreeSearch(Vector<Session> assignments, Vector<Course> courses, Vector<Session> sessions) {

        // first we compare the size of our session and course vectors.
        // if the number of sessions available is 0 and the number of courses left to assign is more than 0
        // we must abort (and return null) as the current assignment vector we are building will violate
        // hard constraint number 1 (all lectures must be assigned a session).
        boolean allCoursesAssigned = true;
        for (Course crs : courses) {
            if (!crs.allLecturesAssigned()) {
                allCoursesAssigned = false;
                break;
            }
        }

        if (sessions.size() == 0 && courses.size() > 0) {
            //System.out.println("\n***********************\n*********ERROR*********\n***********************\n");
            return null;
        }

        // if we've passed the first check, we know there are sessions still left to assign our courses to
        // check to make sure we haven't assigned all courses now, for if we have, then we know we've found
        // some solution.

        // else if (courses.size() == 0) { // all courses assigned. Solution found.
        else if (allCoursesAssigned) {
            //System.out.println("\n================\n SOLUTION FOUND\n================\n");
            return assignments;
        }

        // if we've passed both initial checks, we know we still have sessions and lectures to assign.
        // we create a new vector, called 'possibilities', which we will populate with all sessions available
        // that can house the number of students in a yet-to-be-determined lecture.
        Vector<Session> possibilities = new Vector<Session>();


        Course course = new Course("");
        // iterate over all courses that are not all fully assigned yet
        for (Course crs : courses) {
            if (!crs.allLecturesAssigned()) {
                course = crs;
                
                Lecture lecture = new Lecture("");
                //iterate over all lectures that are not assigned yet
                for (Lecture lec : course.getLectures()) {
                    if (!lec.assigned) {
                        lecture = lec;
                        
                        // iterate over all available sessions for this lecture
                        for (Session session : sessions) {
                            // if there is room in the session for all students in the lecture, add to possibilities
                            if (session.getRemainingCapacity() - lecture.getNumStudents() >= 0 && session.getLength() >= lecture.getExamLength()) {
                                possibilities.add(session);
                            }
                        }
                        
                        if (possibilities.size() == 0) {
                            //System.out.println("\n***********************\nERROR: No valid sessions\n***********************\n");
                            return null;
                        }
                        
                        for (Session session : possibilities) {


                            // send a message to the console, alerting us as to what we are assigning. Simultaneously,
                            // set the current session in possibilities' course and lecture to the current course/lecture
                            // and remove the number of students in that lecture from the remaining capacity of that session.
                            //System.out.println("Assigning: " + course.getName() + " " + lecture.getName());
                            session.setCourse(course);
                            session.setLecture(lecture);
                            session.subtractRemainingCapacity(lecture.getNumStudents());
                            assignments.add(new Session(session));

                            // flag the current lecture as assigned...
                            lecture.assigned = true;

                            // then step through the current courses lectures and check their flags, if they are all set
                            // to assigned, remove the course and set the courseRemoved flag to true.
                            // if (course.allLecturesAssigned()) {
                            // courseRemoved = true;
                            // courses.remove(course);
                            // }
                            /*
                             * NICK THINKS: FOR THE ABOVE: could we make a variable in course, the same as in lecture, to denote that all lectures have been assigned, rather than removing it from the list of courses,
                             * this way we don't need to track a boolean and a copy of the course, simply set its flag to false and reiterate.
                             * 
                             * mh: actually, since course has a allLecturesAssigned method, we could use that instead. I'll try it
                             * 
                             */

                            // now call this function again, with the lecture flagged as assigned and the course either
                            // removed or modified to reflect the state of it's assignments.
                            Vector<Session> tempAssignments = recursiveTreeSearch(assignments, courses, sessions);

                            // if we run into an error in the above call it will return null, at this point we know that
                            // something we have done in this call (or a previous one if this is a shell itself) is wrong
                            // and we must go back and try another pattern of assignments...
                            if (tempAssignments == null) {
                                // to do so, we must first revert our changes to the course, lecture and session.
                                session.unsubtractRemainingCapacity(lecture.getNumStudents());
                                course.findLecture(lecture.getName()).assigned = false;

                                // if (courseRemoved) {
                                // courses.add(backupCourse);
                                // } else {
                                // course.findLecture(backupLecture.getName()).assigned = false;
                                // }
                                //System.out.println("Terminating branch. Reassigning " + course.getName() + " " + lecture.getName() + ".");
                                continue;
                                // if we do NOT run into an error in the above call it will return an assignment vector, at
                                // this point we know we're getting a good result and that we can carry it back up the recursion
                                // chain to the origin and compare it to our old solution.
                            } else if (!course.allLecturesAssigned()) {
                                break;
                            } else {
                                //System.out.println("Returning...");
                                assignments = tempAssignments;
                                return assignments;
                            }
                        }
                    }
                }
            }
        }
        
//        Course course = new Course("");
//        for (Course crs : courses) {
//            if (!crs.allLecturesAssigned()) {
//                course = crs;
//                break;
//            }
//        }
//
//        Lecture lecture = new Lecture("");
//        /*
//         * NICK THINKS: FORLOOP?: If we encased this course/lecture choice within a double for loop and called recursiveSearch from within that, we could reset ourselves (as we do already) before the
//         * for loop takes us to the next course/lecture choice, this would solve our problem of not iterating over all possible courses as a first choice.
//         * 
//         * Maybe something like...
//         * 
//         * if(sessions == 0 & courses > 0) return null; else if(courses == 0) return assignments;
//         * 
//         * for(course : courses){ //if(course.assigned = false) ***** this is only if we take my suggestion for a course-flag below. for(lecture : course.lectures){ if(lecture.assigned = false){
//         * possibilities = new vector<session>
//         * 
//         * for(session : sessions){ if(session.getRemainingCapacity - lecture.getNumStudents >= 0){ possibilities.add(session) } }
//         * 
//         * if(possibilties.size > 0){ do our stuff, return assignments } else{ return null? } } } }
//         * 
//         * I probably missed quite a bit, but I think that should cause the program to at least step through all possible courses at each level. This won't cause us to ever iterate through in a
//         * different pattern, but it will force iteration until it finds SOME solution or time runs out and the program is killed.
//         * 
//         * mh: will attempt implementation. if it doesn't appear below, it's cause I couldn't get it to work
//         */
//
//        // we step through the lectures in the given course and pick the first unassigned lecture we come across.
//        for (Lecture lec : course.getLectures()) {
//            if (!lec.assigned) {
//                lecture = lec;
//                break;
//            }
//        }
//        /*
//         * NICK THINKS: FOR THE ABOVE: Are we copying our chosen lecture here? If so that might be how we've ended up with 4/3 and 6/3 lectures assigned on our tests. Though I may just be sleep
//         * deprived.
//         * 
//         * mh: it's only a copy of the reference. Whatever we changes we make to lecture will be reflected in lec, and thus the lecture that is in course's lecture vector
//         */
//
//        // now we populate the possibilities vector with all valid sessions from the sessions vector.
//        for (Session session : sessions) {
//            // if there is room in the session for all students in the lecture, add to possibilities
//            if (session.getRemainingCapacity() - lecture.getNumStudents() >= 0) {
//                possibilities.add(session);
//            }
//        }
//
//        // now we must check to ascertain whether or not we can proceed.
//        // if the number of possible sessions to assign the currently selected lecture to is 0, we will
//        // violate the first hard constraint by proceeding, so we must return null.
//        if (possibilities.size() == 0) {
//            //System.out.println("\n***********************\nERROR: No valid sessions\n***********************\n");
//            return null;
//        }
//
//        // otherwise, we step through the possibilities we have generated and see what happens when we
//        // assign the current lecture to any one of them.
//
//        for (Session session : possibilities) {
//
//            // first, we create new variables to save the course and lecture that we are about to apply
//            // to a session, in case we run into an error and need to revert the course/lecture to before
//            // the application.
//
//            // send a message to the console, alerting us as to what we are assigning. Simultaneously,
//            // set the current session in possibilities' course and lecture to the current course/lecture
//            // and remove the number of students in that lecture from the remaining capacity of that session.
//            //System.out.println("Assigning: " + course.getName() + " " + lecture.getName());
//            session.setCourse(course);
//            session.setLecture(lecture);
//            session.subtractRemainingCapacity(lecture.getNumStudents());
//            assignments.add(new Session(session));
//
//            // flag the current lecture as assigned...
//            lecture.assigned = true;
//
//            // then step through the current courses lectures and check their flags, if they are all set
//            // to assigned, remove the course and set the courseRemoved flag to true.
//            // if (course.allLecturesAssigned()) {
//            // courseRemoved = true;
//            // courses.remove(course);
//            // }
//            /*
//             * NICK THINKS: FOR THE ABOVE: could we make a variable in course, the same as in lecture, to denote that all lectures have been assigned, rather than removing it from the list of courses,
//             * this way we don't need to track a boolean and a copy of the course, simply set its flag to false and reiterate.
//             * 
//             * mh: actually, since course has a allLecturesAssigned method, we could use that instead. I'll try it
//             * 
//             */
//
//            // now call this function again, with the lecture flagged as assigned and the course either
//            // removed or modified to reflect the state of it's assignments.
//            Vector<Session> tempAssignments = recursiveTreeSearch(assignments, courses, sessions);
//
//            // if we run into an error in the above call it will return null, at this point we know that
//            // something we have done in this call (or a previous one if this is a shell itself) is wrong
//            // and we must go back and try another pattern of assignments...
//            if (tempAssignments == null) {
//                // to do so, we must first revert our changes to the course, lecture and session.
//                session.unsubtractRemainingCapacity(lecture.getNumStudents());
//                course.findLecture(lecture.getName()).assigned = false;
//
//                // if (courseRemoved) {
//                // courses.add(backupCourse);
//                // } else {
//                // course.findLecture(backupLecture.getName()).assigned = false;
//                // }
//                //System.out.println("Terminating branch. Reassigning " + course.getName() + " " + lecture.getName() + ".");
//                continue;
//                // if we do NOT run into an error in the above call it will return an assignment vector, at
//                // this point we know we're getting a good result and that we can carry it back up the recursion
//                // chain to the origin and compare it to our old solution.
//            } else if (!course.allLecturesAssigned()) {
//                for (Lecture lec : course.getLectures()) {
//                    if (!lec.assigned) {
//                        lecture = lec;
//                        break;
//                    }
//                }
//                continue;
//            } else {
//                //System.out.println("Returning...");
//                assignments = tempAssignments;
//                return assignments;
//            }
//        }
        //System.out.println("All possible sessions iterated. Returning to last call.");
        return assignments;
    }

    public static void outputDemo(final Environment env2, final String outFileName) {

        // START of writer toFile
        // toFile will create a file of the form inputfile.ext.out as fed by the
        // PredicateReader that created env.
        // writing to toFile O*V*E*R*W*R*I*T*E*S already existent files (of the
        // same name) with the newly created file.
        // toFile should write the the working directory of .jar executed to run
        // this writer.

        PrintWriter toFile;
        try {

            toFile = new PrintWriter(outFileName, "UTF-8");

            for (String day : env2.days) {
                toFile.println("day(" + day + ")");
            }

            for (Course course : env2.courses) {
                toFile.println(course.toString());
            }

            for (Instructor instructor : env2.instructors) {
                toFile.println(instructor.toString());

                for (Lecture lecture : instructor.getLectures()) {
                    toFile.println(instructor.instructs(lecture));
                }
            }

            for (Student student : env2.students) {
                toFile.println(student.toString());
                toFile.println(student.toEnrolledList());

                for (Pair<Course, Lecture> courseLecturePair : student.getClasses()) {
                    toFile.println(student.toEnrolled(courseLecturePair.getValue()));
                }
            }

            for (Course course : env2.courses) {
                for (Lecture lecture : course.getLectures()) {

                    toFile.println(lecture.toStringTwo());
                    toFile.println(lecture.toString());
                    toFile.println(lecture.toExamLength());
                }
            }

            for (Room room : env2.rooms) {
                toFile.println(room.toString());
                toFile.println(room.toCapacity());
            }

            for (Session session : env2.sessions) {
                toFile.println(session.toString());
                toFile.println(session.toStringTwo());

                if (session.getDay() != null && session.getTime() != null && session.getLength() != null) {
                    toFile.println(session.toAt());
                }

                if (session.getRoom() != null) {
                    toFile.println(session.toRoomAssign());
                }

                if (session.getDay() != null) {
                    toFile.println(session.toDayAssign());
                }

                if (session.getTime() != null) {
                    toFile.println(session.toTime());
                }
            }
            toFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run in "command mode" -- continuously reading predicates from the command line. If a predicate begins with a "!", then we will attempt to execute it with a {@link PredicateReader#eval(String)},
     * otherwise with a {@link PredicateReader#assert_(String)}. Special tokens handled anomalously are:
     * <ul>
     * <li>exit // exits the program
     * <li>? // executes the prediate "!help()"
     * <li>help // executes the prediate "!help()"
     * </ul>
     * 
     * @param env
     */
    public static void commandMode(final EnvironmentInterface env) {
        final int maxBuf = 200;
        byte[] buf = new byte[maxBuf];
        int length;
        try {
            System.out.print("\nTAallocation: query using predicates, assert using \"!\" prefixing predicates;\n !exit() to quit; !help() for help.\n\n> ");
            while ((length = System.in.read(buf)) != -1) {
                String s = new String(buf, 0, length);
                s = s.trim();
                if (s.equals("exit"))
                    break;
                if (s.equals("?") || s.equals("help")) {
                    s = "!help()";
                    System.out.println("> !help()");
                }
                if (s.length() > 0) {
                    if (s.charAt(0) == '!')
                        env.assert_(s.substring(1));
                    else
                        System.out.print(" --> " + env.eval(s));
                }
                System.out.print("\n> ");
            }
        } catch (Exception e) {
            System.err.println("exiting: " + e.toString());
        }
    }

}
