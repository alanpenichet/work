package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Fall 2016
 */


import java.util.ArrayList;
import java.util.List;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */


/*
 * Massive TODO WALL
 * array index out of bounds when height 20 and width 50
 * figure out fighting -- maybe good
 * figure out spawning on same block -- maybe good
 * make 2 new classes
 * find out which ones are already required
 * stats - think good
 * ensure can only move once, currently have flag but never resets - maybe good - check still
 *  clover based on testcritter - maybe good
 *  TODO - massive bug with updating array of locations. esp in encounter w/ seed 30
 */

public abstract class Critter {

    private int energy = 0;
    public boolean Moveable = true;
    private int x_coord;
    private int y_coord;
    private static int created =0;
    private static int killed = 0;

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
			created++;
			world.get(coordSwap(c.x_coord, c.y_coord)).add(c);
		}
    	catch (Exception e)
    	{
			throw new InvalidCritterException(critter_class_name);
		}
    }
    
    
    public static int coordSwap(int x, int y) {
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
        Class CritType = Class.forName(critter_class_name);
        
        for(Critter c: population) {
        		if(CritType.isInstance(c)) {
        			instances.add(c);
        		}
        }
        }
        catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}
        return instances;
    }
    public static void handleEncounters() {
    	for(List<Critter> list : world) {
    		while(list.size()>1) {
    			Critter a = list.get(0);
    			Critter b = list.get(1);
    			Critter remove = CritFight(a,b);
    			if(remove != null) {
    				population.remove(remove);
    				list.remove(remove);
    				killed++;
    			}
    			else {
    				list.remove(a);
    				list.remove(b);
    				update(a,b);
    			}
    		}
    	}
    }
    public static void update(Critter a, Critter b) {
    	//fix position of two trying to fight when not in same block
    	world.get(coordSwap(a.x_coord, a.y_coord)).add(a);
    	world.get(coordSwap(b.x_coord, b.y_coord)).add(b);
    }

    
    public static Critter CritFight(Critter a, Critter b) {
			//only handle if both alive.
			if(a.energy > 0 && b.energy>0) {
				System.out.println(a.toString() + " fighting " + b.toString());
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
        population = new java.util.ArrayList<Critter>();
    }

    public static void worldTimeStep() {
        for(Critter c: population) {
        	//System.out.println("before: " + c.x_coord + ", " + c.y_coord);
        	//TODO make sure this works for moveable flag
        	//infinite loop when both low energy
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
        		killed++;
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
				//TODO error message
			}
        }
    }

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
    //TODO - address walk/run from fight
    //2. The critter must not be moving into a position that is occupied by another critter

    /* a one-character long string that visually depicts your critter in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    protected final void walk(int direction) {
    	this.energy-= Params.WALK_ENERGY_COST;
    	//clear spot
    	if(Moveable) {
	        switch(direction) {
	        case 0: this.x_coord++;  break;
	        case 1: this.x_coord++; this.y_coord--; break;
	        case 2: this.y_coord--; break;
	        case 3: this.y_coord--; this.x_coord--; break;
	        case 4: this.x_coord--; break;
	        case 5: this.x_coord--; this.y_coord++; break;
	        case 6: this.y_coord++; break;
	        case 7: this.x_coord++; this.y_coord++; break;
	        }
	        //scroll world to avoid out of bounds
	
	        if(this.x_coord < 0) {
	        	this.x_coord += Params.WORLD_WIDTH;
	        }
	        if(this.y_coord < 0) {
	        	this.y_coord += Params.WORLD_HEIGHT;
	        }
	        if(this.x_coord >= Params.WORLD_WIDTH) {
	        	this.x_coord = this.x_coord-Params.WORLD_WIDTH;
	        }
	        if(this.y_coord >= Params.WORLD_HEIGHT) {
	        	this.y_coord = this.y_coord-Params.WORLD_HEIGHT;
	        }
    	Moveable = false;
    	}
    }

    protected final void run(int direction) {
    	//account for energy subtraction from two walk calls
    	this.energy-= Params.RUN_ENERGY_COST;
    	if(Moveable) {
	    	this.energy+= 2*Params.WALK_ENERGY_COST;
	        this.walk(direction);
	        this.walk(direction);
	        Moveable = false;
    	}
    }

    protected final void reproduce(Critter offspring, int direction) {
    	System.out.println(offspring.toString() + " reproducing");
       if(this.energy<Params.MIN_REPRODUCE_ENERGY) {
    	   //not enough energy
    	   return;
       }
       else {
    	   offspring.energy = this.energy/2;
    	   this.energy = (int) Math.ceil((double)this.energy/2);
    	   //TODO - check if this counts as round up
    	   offspring.x_coord = this.x_coord;
           offspring.y_coord = this.y_coord;
           offspring.walk(direction);
           babies.add(offspring);
           created++;
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

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
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
