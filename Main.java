package assignment4;
/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Spring 2019
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.awt.List;
import java.io.*;
import java.lang.reflect.Method;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    //private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name,
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }
        commandInterpreter(kb);
        System.out.flush();

    }
    /* Do not alter the code above for your submission. */

    private static void commandInterpreter (Scanner kb) {    	
    	boolean quit = false;
    	String full = null;
    	System.out.println("critters> ");
    	//System.out.println(entriesList.toString());
    	//tabs and spaces do not matter, make sure to fix
    	while(!quit) {
    		full = kb.nextLine();
        	//TODO change to any whitespace
        	String[] entries = full.split(" ");
        	//create arraylist of all entries before new line
        	ArrayList<String> entriesList = new ArrayList<String>();
        	for(String s:entries) {
        		entriesList.add(s);
        	}
        	String command = entriesList.get(0);
		    	switch(command){
		    	case "quit": quit = true; break;
		    	case "step": int Steps = 1; 
		    	//TODO: make sure no neg nums
		    		if(entriesList.size()>1) Steps = Integer.parseInt(entriesList.get(1));
		    		for(int i = 0; i < Steps; i++){ Critter.worldTimeStep();} break;
		    	case "show": Critter.displayWorld();  break;
		    	case "seed": Critter.setSeed(Integer.parseInt(entriesList.get(1))); break;
		    	case "create": 
		    	String critType = entriesList.get(1);
		    		int numCrits = Integer.parseInt(entriesList.get(2));
					try {
						for(int i=0; i< numCrits; i++) {
							Critter.createCritter(critType);
						}
						
					} catch (InvalidCritterException e1) {
						// TODO print error message
				
					} 	break;
		    	case "stats": 
		    	try {
		    		//TODO - try this
		    		String classname = myPackage + "." + entriesList.get(1);
					java.util.List<Critter> critterList = Critter.getInstances(classname);
					//System.out.println(critterList.toString());
					Class critterType = null;
					critterType = Class.forName(classname);
					//System.out.println(critterType);
					Method m = critterType.getMethod("runStats", java.util.List.class);
					m.invoke(critterList.getClass(), critterList);
		    	}
		    	catch(Exception e) {
		    		//TODO print error message
		    	}
		    	break;
		    	case "clear": Critter.clearWorld();	break;
		    	default: System.out.println("invalid command: " + command);
		    	}
		    	//System.out.println();
    	}
    }
}
