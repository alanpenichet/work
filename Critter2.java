package assignment4;
/* CRITTERS Critter1.java
 * EE422C Project 4 submission by
 * Alan Penichet-Paul
 * Ap46378
 * 16190
 * Slip days used: 2
 * Spring 2019
 *
 * This critter hates numbers with a passion. If any critter tries to fight it,
 * it will only retaliate if it has a number representing it. It will also
 * try to gravitate to the top of the screen as it only runs up and walks down
 */

public class Critter2 extends Critter {

	public String toString() { return "2"; }
	
	
    /**
     * Runs in up direction or walks in down direction.
     */
	@Override
	public void doTimeStep() {
		int direction = Critter.getRandomInt(8);
		if(direction<5) {
			run(direction);
		}
		else {
			walk(direction);
		}
	}
	
    /**
     * Chooses to fight if opponent is represented by an integer.
     *
     * @param opponent	String representation of opponent.
     * @return True if opponent is an integer.
     */
	@Override
	public boolean fight(String opponent) {
		try {  
		    Double.parseDouble(opponent);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
	}

}
