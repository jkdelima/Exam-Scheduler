package examSchedule.parser;

public abstract class Solution extends Entity implements SolutionInterface {

	public Solution(String filename) {
		
	}

	public abstract boolean isSolved();

	public abstract boolean isComplete();

	public abstract boolean hasViolations();

	public abstract int getGoodness();

	public abstract String toString();

}
