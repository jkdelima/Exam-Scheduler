package examSchedule;

/**
 * @author Group 4 - Mike Hung, Nick Pylypow, Jeremy Kyle Delima
 * This class creates the Room object for our project.
 */
public class Room {

	private String name;
	private Long capacity;
	
	/**
	 * Room constructor.
	 * @param name is the name of the room.
	 */
	public Room(String name){
		this.name = name;
		capacity = new Long(0);
	}
	
	public Room(Room room) {
	    this.name = new String(room.getName());
	    this.capacity = new Long(room.getCapacity());
	}

    /**
	 * Get name method.
	 * @return name of room
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get capacity method.
	 * @return capacity of room
	 */
	public Long getCapacity() {
		return capacity;
	}

	/**
	 * Set capacity method.
	 * @param cap is the capacity of the room
	 */
	public void setCapacity(Long cap) {
	    this.capacity = cap;	    
	}
	
	/**
	 * Method providing object's string output.
	 * @return the string output
	 */
	public String toString() {
		return "room(" + name + ")";
	}

	public String toCapacity() {
		return "capacity(" + name + ", " + capacity + ")";
	}
	
}