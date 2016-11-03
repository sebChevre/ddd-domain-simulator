package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.stockexchange.prix.Operation;

import java.util.Random;

public class SimulatorUtil {
	
	public static int getRandomIntBeetween(int start, int stop){
		
		return new Random().nextInt(stop-start) + start;
		
	}
	
	public static double getRandomDoubleBeetween(int start, int stop){
		
		return (double)getRandomIntBeetween(start,stop) + new Random().nextDouble();
		
	}
	
	public static Operation getRandomOperation () {
		
		int r = new Random().nextInt(10);
		
		switch(r){
		
			case 0: 
			case 2:
			case 4:
			case 5:
			case 8:return Operation.ADD;
				
			
			case 1: 
			case 3: 
			case 6:
			case 7:
			case 9:	return Operation.SUBSTRACT;
			
			default: throw new IllegalArgumentException();
		
		}
	}
	
	public static Operation inverse(Operation op){
		
		switch(op){
			case ADD: return Operation.SUBSTRACT;
		
			case SUBSTRACT: return Operation.ADD;
			
			default: throw new IllegalArgumentException();
		}
	}
	
	

}
