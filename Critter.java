package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Alan Penichet-Paul
 * Ap46378
 * 16190
 * Slip days used: 2
 * Spring 2019
 */


import java.util.ArrayList;
import java.util.List;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */

public abstract class Critter {

    private int energy = 0;
    private boolean Moveable = true;
    private boolean fromFight = false;
    private boolean fromFight2 = true;
    private int x_coord;
    private int y_coord;

    private static List<Critter> population = new java.util.ArrayList<Critter>();
    private static List<Critter> babies = new java.util.ArrayList<Critter>();
    private static List<List<Critter>> world = new ArrayList<>(Params.WORLD_HEIGHT*Params.WORLD_WIDTH);
    // Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
    private static String myPackage;
    static {
    	for(int i = 0; i < Params.WORLD_HEIGHT*Params.WORLD_WIDTH; i++) {
    		world.add(new ArrayList<Critter>()); 
    	}
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static java.util.Random rand = new java.util.Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new java.util.Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the qualified name of a concrete subclass of Critter, if not,
     * an InvalidCritterException must be thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name) throws assignment4.InvalidCritterException {
    	try
    	{
			Class CritType = Class.forName(myPackage + "." + critter_class_name);
			Critter c = (Critter) CritType.newInstance();
			c.y_coord = getRandomInt(Params.WORLD_HEIGHT);
			c.x_coord = getRandomInt(Params.WORLD_WIDTH);
			c.energy = Params.START_ENERGY;
			population.add(c);
			//add to grid?
			world.get(coordSwap(c.x_coord, c.y_coord)).add(c);
		}
    	catch (Exception e)
    	{
			throw new InvalidCritterException(critter_class_name);
		} catch (NoClassDefFoundError e) {
			throw new InvalidCritterException(critter_class_name);
		}
    }
    
    /**
     * Changes (x,y) coordinate to Linear Coordinate.
     * @param x		x coordinate
     * @param y		y coordinate
     * @return Linear coordinate corresponding to (x,y)
     */
    private static int coordSwap(int x, int y) {
    	return (y*Params.WORLD_WIDTH) + x;
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name) throws assignment4.InvalidCritterException {
        List<Critter> instances = new java.util.ArrayList<Critter>();
        try {
        Class CritType = Class.forName(myPackage + "." + critter_class_name);
        
        for(Critter c: population) {
        		if(CritType.isInstance(c)) {
        			instances.add(c);
        		}
        }
        }
        catch (Exception e) {
			throw new InvalidCritterException(critter_class_name);
		} catch (Error e) {
			throw new InvalidCritterException(critter_class_name);
		}
        return instances;
    }
    /**
     * Iterates through and handles all encounters between Critters.
     */
    private static void handleEncounters() {
    	for(List<Critter> list : world) {
    		while(list.size()>1) {
    			Critter a = list.get(0);
    			Critter b = list.get(1);
    			a.fromFight = true;
    			b.fromFight = true;
    			Critter remove = CritFight(a,b);
    			a.fromFight = false;
    			a.fromFight = false;
    			if(remove != null) {
    				population.remove(remove);
    				list.remove(remove);
    			}
    			else {
    				list.remove(a);
    				list.remove(b);
    				update(a,b);
    			}
    		}
    	}
    }
    /**
     * Updates the coordinates of two Critters passed in.
     *
     * @param a: First Critter to update coordinates.
     * @param b: Second Critter to update coordinates.
     */
    private static void update(Critter a, Critter b) {
    	//fix position of two trying to fight when not in same block
    	world.get(coordSwap(a.x_coord, a.y_coord)).add(a);
    	world.get(coordSwap(b.x_coord, b.y_coord)).add(b);
    }
    /**
     * Simulates fight between two critters
     *
     * @param a: Critter to be involved in fight.
     * @param b: Critter to be involved in fight.
     * @return Critter that lost fight, to be removed. Null if no critter died. (eg coords changed)
     */
    private static Critter CritFight(Critter a, Critter b) {
			//only handle if both alive.
			if(a.energy > 0 && b.energy>0) {
				//System.out.println(a.toString() + " fighting " + b.toString());
    			boolean a_fight = a.fight(b.toString());
    			boolean b_fight = b.fight(a.toString());
    			//if both still in same spot
    			if(a.x_coord == b.x_coord && a.y_coord == b.y_coord) {
    				int PowerA = a_fight ? getRandomInt(a.energy) : 0;
    		        int PowerB = b_fight ? getRandomInt(b.energy) : 0;
    		        //a wins
    		        if(PowerA>=PowerB) {
    		        	a.energy += b.energy/2;
    		        	return b;
    		        }
    		        //b wins
    		        if(PowerA<PowerB) {
    		        	b.energy += a.energy/2;
    		        	return a;
    		        }
    			}
    			else {
    				//handle issue where in wrong list
    				return null;
    			}
			}
			// cull if dead before fight
			else if(a.energy <= 0) {
				return a;
			}
			else if(b.energy <= 0) {
				return b;
			}
			return null;
		}
    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
    	//fix for test setup
        population = new java.util.ArrayList<Critter>();
        world = new ArrayList<>(Params.WORLD_HEIGHT*Params.WORLD_WIDTH);
        babies = new java.util.ArrayList<Critter>();
        for(int i = 0; i < Params.WORLD_HEIGHT*Params.WORLD_WIDTH; i++) {
    		world.add(new ArrayList<Critter>()); 
    	}
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }
    /**
     * Primary Driver method for the Program. 
     * Simulates a time step for every Critter in population list.
     * Handles all encounters between Critters.
     * Culls dead Critters.
     * Adds Babies to population.
     * Adds Clover to population.
     */
    public static void worldTimeStep() {
        for(Critter c: population) {
        	//System.out.println("before: " + c.x_coord + ", " + c.y_coord);
        	int x = c.x_coord;
        	int y = c.y_coord;
        	c.Moveable = true;
        	c.energy -= Params.REST_ENERGY_COST;
        	c.doTimeStep();
        	if(c.x_coord!=x || c.y_coord!=y) {
        		//update coord
        		world.get(coordSwap(x,y)).remove(c);
        		world.get(coordSwap(c.x_coord,c.y_coord)).add(c);
        	}
        	//System.out.println("after: " + c.x_coord + ", " + c.y_coord);
        }
        //check for fights and handle
        handleEncounters();
        //purge dead
        //temp removal list to avoid concurrent issues
        ArrayList<Critter> toRemove = new ArrayList<Critter>();
        for(Critter c: population) {
        	if(c.energy<=0) {
        		toRemove.add(c);
        		world.get(coordSwap(c.x_coord,c.y_coord)).remove(c);
        	}
        }
        //remove from new list to avoid concurrent issues
        for(Critter c: toRemove) {
        	population.remove(c);
        }
        //add babies after fights, then dereference babies set
        for(Critter c: babies) {
        	population.add(c);
        	//add babies to grid aswell
        	world.get(coordSwap(c.x_coord,c.y_coord)).add(c);
        }
        	//dereference
        babies = new java.util.ArrayList<Critter>();
        for(int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++) {
        	try {
				Critter.createCritter("Clover");
			} catch (InvalidCritterException e) {

			}
        }
    }
    /**
     * Displays a text representation of the World.
     */
    public static void displayWorld() {
        // Top line
    	System.out.print("+");
    	for(int i = 0; i < Params.WORLD_WIDTH; i++) {
    		System.out.print("-");
    	}
    	System.out.println("+");
    	//actual grid
    	ArrayList<StringBuilder> rows = new ArrayList<StringBuilder>();
    	for(int i = 0; i < Params.WORLD_HEIGHT;i++) {
    		StringBuilder row = new StringBuilder();
    		row.append('|');
    		for(int j = 0; j< Params.WORLD_WIDTH; j++) {
    			row.append(' ');
    		}
    		row.append('|');
    		rows.add(row);
    	}
    	for(Critter c: population) {
    		//fix row
    		int x = c.x_coord;
    		int y = c.y_coord;
    		StringBuilder tempRow = rows.get(y);
    		//System.out.println(tempRow);
    		tempRow.deleteCharAt(x+1);
    		//System.out.println(tempRow);
    		tempRow.insert(x+1, c.toString());
    		//System.out.println(tempRow);
    		rows.set(y, tempRow);
    	}
    	for(StringBuilder s: rows) {
    		System.out.println(s);
    	}
    	//bottom line
    	System.out.print("+");
    	for(int i = 0; i < Params.WORLD_WIDTH; i++) {
    		System.out.print("-");
    	}
    	System.out.println("+");
    }

    /**
     * Prints out how many Critters of each type there are on the board.
     *
     * @param critters List of Critters.
     */
    public static void runStats(List<Critter> critters) {
        System.out.print("" + critters.size() + " critters as follows -- ");
        java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string, critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            System.out.print(prefix + s + ":" + critter_count.get(s));
            prefix = ", ";
        }
        System.out.println();
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String oponent);

    /* a one-character long string that visually depicts your critter in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }
    /**
     * Advances a given Critter one step in direction passed in, only when valid.
     *
     * @param direction int representing one of 8 directions.
     */
    protected final void walk(int direction) {
    	this.energy-= Params.WALK_ENERGY_COST;
    	//clear spot
    	if(Moveable) {
    		int x = this.x_coord;
	        int y = this.y_coord;
		        switch(direction) {
		        case 0: x++;  break;
		        case 1: x++; y--; break;
		        case 2: y--; break;
		        case 3: y--; x--; break;
		        case 4: x--; break;
		        case 5: x--; y++; break;
		        case 6: y++; break;
		        case 7: x++; y++; break;
		        }
		        //scroll world to avoid out of bounds
		        if(x < 0) {
		        	x += Params.WORLD_WIDTH;
		        }
		        if(y < 0) {
		        	y += Params.WORLD_HEIGHT;
		        }
		        if(x >= Params.WORLD_WIDTH) {
		        	x = x-Params.WORLD_WIDTH;
		        }
		        if(y >= Params.WORLD_HEIGHT) {
		        	y = y-Params.WORLD_HEIGHT;
		        }
		        if(fromFight || fromFight2) {
		        	//System.out.println("trying fromfight");
		        	if(!world.get(coordSwap(x,y)).isEmpty()){
		        		//if not empty return and reset flag
		        		fromFight = false;
		        		return;
		        	}//reset fromfight flag
		        	fromFight = false;
		        } 
		this.x_coord = x;
		this.y_coord = y;
    	Moveable = false;
    	}
    	
    }
    /**
     * Advances a Critter 2 spots by invoking walk twice and handling energy used.
     *
     * @param direction: int representing direction of Movement.
     */
    protected final void run(int direction) {
    	//account for energy subtraction from two walk calls
    	if(fromFight) {
    		fromFight2 = true;
    	}
    	this.energy-= Params.RUN_ENERGY_COST;
    	if(Moveable) {
	    	this.energy+= 2*Params.WALK_ENERGY_COST;
	        this.walk(direction);
	        this.walk(direction);
	        Moveable = false;
    	}
    	fromFight2 = false;
    }
    /**
     * Splits energy between potential offspring and parent. Also advances offspring by 1 spot in direction.
     *
     * @param offspring  	Critter that was produced by parent. 
     * @param direction		int representation of direction to move offspring Critter.
     */
    protected final void reproduce(Critter offspring, int direction) {
    	//System.out.println(offspring.toString() + " reproducing");
       if(this.energy<Params.MIN_REPRODUCE_ENERGY) {
    	   //not enough energy
    	   return;
       }
       else {
    	   offspring.energy = this.energy/2;
    	   this.energy = (int) Math.ceil((double)this.energy/2);
    	   offspring.x_coord = this.x_coord;
           offspring.y_coord = this.y_coord;
           offspring.walk(direction);
           babies.add(offspring);
       }
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you want to
     * create tests of your Critter model, you can create subclasses of this class
     * and then use the setter functions contained here.
     *
     * NOTE: you must make sure that the setter functions work with your implementation
     * of Critter. That means, if you're recording the positions of your critters
     * using some sort of external grid or some other data structure in addition
     * to the x_coord and y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    public static abstract class TestCritter extends Critter {
        /**
         * Sets Energy value of Critter. Removes if energy is set to 0.
         *
         * @param new_energy_value		integer value to set Energy to.
         */
        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
            if(super.energy<=0) {
            	//kill it if dead
            	world.get(coordSwap(super.x_coord,super.y_coord)).remove(this);
            	population.remove(this);
            }
        }
        /**
         * Sets X coordinate of Critter and updates World.
         *
         * @param new_x_coord 		integer X position to change to.
         */
        protected void setX_coord(int new_x_coord) {
        	//temp old coord
        	int x = super.x_coord;
        	//remove from old coord
        	world.get(coordSwap(x,super.y_coord)).remove(this);
            super.x_coord = new_x_coord;
            //add to new coord
            world.get(coordSwap(super.x_coord,super.y_coord)).add(this);
        }
        /**
         * Sets Y coordinate of Critter and updates World.
         *
         * @param new_y_coord 		integer Y position to change to.
         */
        protected void setY_coord(int new_y_coord) {
        	int y = super.y_coord;
        	//remove from old coord
        	world.get(coordSwap(super.x_coord,y)).remove(this);
            super.y_coord = new_y_coord;
            //add to new coord
            world.get(coordSwap(super.x_coord,super.y_coord)).add(this);
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you are not using the population
         * ArrayList that has been provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are not using the babies
         * ArrayList that has been provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.  Babies should be added to the general population
         * at either the beginning OR the end of every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }

    }

}
