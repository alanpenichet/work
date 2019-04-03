package assignment4;
/* CRITTERS Critter1.java
 * EE422C Project 4 submission by
 * Alan Penichet-Paul
 * Ap46378
 * 16190
 * Slip days used: 2
 * Spring 2019
 *
 * This critter will not move at all unless low on health
 * and fight anybody that comes into it's path.
 * It will reproduce rapidly like a virus to try to take over the map early on.
 */
public class Critter1 extends Critter {

	public String toString() { return "1"; }
	
	
	
    /**
     * Reproduces if it has enough energy. Only moves when low on energy.
     */
	@Override
	public void doTimeStep() {
		if (getEnergy() > Params.MIN_REPRODUCE_ENERGY) {
			Critter1 child = new Critter1();
			reproduce(child, Critter.getRandomInt(8));
		}
		else{
			walk(Critter.getRandomInt(8));
		}
	}

    /**
     * Chooses to fight on every occasion.
     * @param opponent String representation of opponent.
     * @return true
     */
	@Override
	public boolean fight(String opponent) {
		return true;
	}

}
