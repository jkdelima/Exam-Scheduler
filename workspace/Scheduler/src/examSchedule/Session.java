package examSchedule;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima
 * This class creates the Session object for our project.
 */
public class Session {
	
	private String name;
	private String day;
	private Long time;
	private Long length;
	private Room room;
	private Course course;
	private Lecture lecture;
	private Long remainingCapacity;
	
	/**
	 * Session constructor.
	 * @param name is the name of the session
	 */
	public Session(String name) {
		this.name = name;
		course = new Course("");
		lecture = new Lecture("");
		day = "";
		time = new Long(0);
		room = new Room("");
		remainingCapacity = new Long(0);
	}
	
    public Session(Session session) {
        this.name = new String(session.getName());
        this.setDay(new String(session.getDay()));
        this.setTime(new Long(session.getTime()));
        this.setLength(new Long(session.getLength()));
        this.setRoom(new Room(session.getRoom()));
        this.setCourse(new Course(session.getCourse()));
        this.setLecture(new Lecture(session.getLecture()));
        this.setRemainingCapacity(session.getRemainingCapacity());
    }

    /**
	 * The following are get methods for session characteristics.
	 * @return name, day, time, room, exam length, course, and lecture of session
	 */
	
	public String getName() {
		return name;
	}

	public String getDay() {
		return day;
	}

	public Long getTime() {
		return time;
	}

	public Room getRoom() {
		return room;
	}

	public Long getLength() {
		return length;
	}

	public Course getCourse() {
		return course;
	}

	public Lecture getLecture() {
		return lecture;
	}
	
    /**
	 * The following are set methods for session characteristics.
	 * @param name, day, time, room, exam length, course, and lecture of session
	 */

	public void setDay(String day) {
		this.day = day;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void setRoom(Room room) {
		this.room = room;
		this.remainingCapacity = room.getCapacity();
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
	}
	
	/**
	 * Method providing object's string output.
	 * @return the string output
	 */
	public String toString() {
		return "session(" + name + ")";
	}
	
	public String toStringTwo() {
		return "session(" + name + ", " + room.getName() + ", " + day + ", " + time + ", " + length + ")";
	}

	public String toAt() {
		return "at(" + name + ", " + day + ", " + time + ", " + length + ")";
	}
	
	//check session.room != null before call
	public String toRoomAssign() {
		return "roomAssign(" + name + ", " + room.getName() + ")";
	}

	//check session.day != null before call
	public String toDayAssign() {
		return "dayAssign(" + name + ", " + day + ")";
	}
	
	//check session.time != null before call
	public String toTime() {
		return "time(" + name + ", " + time + ")";
	}
	
	/**
	 * Method providing object's string assignment.
	 * @return the string output assignment
	 */
	public String assign() {
		return "assign(" + course.getName() + ", " + lecture.getName() + ", " + name + ")";
	}
	
	public boolean equals(Session session) {
	    return this.name.equals(session.getName());
	}

    public Long getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Long remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
    
    public void subtractRemainingCapacity(Long subtract) {
        this.setRemainingCapacity(this.getRemainingCapacity() - subtract);
    }

    
    public void unsubtractRemainingCapacity(Long unsubtract) {
        this.setRemainingCapacity(this.getRemainingCapacity() + unsubtract);
    }
    
    public void resetRemainingCapacity() {
        remainingCapacity = room.getCapacity();
    }
}
