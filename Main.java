import java.io.*;
import java.util.Scanner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
	// problem
	private static int[][] qkValueWeight; // [0][] is Value // [1][] is Weight
	private static int[][] qkPairValue;
	private static int qkCapacity;
	private static int numObjects;
	
	private static int[] sumValue;
	
	private static final String problemFilName = "test.txt";
	
	public static void main(String[] args) {
		System.out.println("Main: Starting readProblem()");
		readProblem();
		printProblemStat();
		printProblem();
		
		// Run GA
		long start = System.currentTimeMillis();
		
		int resultValue = run();
		
		
		long finished = System.currentTimeMillis();
		double timeElapsed = (finished - start) / (double)1000;
		System.out.println("It took " + timeElapsed + " seconds");
		
		System.out.println("Result Value = " + resultValue);
		
		//String[] gaResult = ga_Permutation.getFitnessStat();
		//String[] outputData = {problemFilName, gaResult[0], gaResult[1], gaResult[2], Double.toString(timeElapsed)};
		
		//printResultToCSV(outputData);
	}
	
	private static void printResultToCSV(String[] outputData) {
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("test.csv"), true))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append(outputData[0]);
		      sb.append(',');
		      sb.append(outputData[1]);
		      sb.append(',');
		      sb.append(outputData[2]);
		      sb.append(',');
		      sb.append(outputData[3]);
		      sb.append(',');
		      sb.append(outputData[4]);
		      sb.append('\n');

		      writer.write(sb.toString());

		      System.out.println("Permutation's result written to csv file.");
		      
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void readProblem() {
		try {
			File file  = new File("..\\problem_instances\\" + problemFilName);
			Scanner scanner = new Scanner(file);
			
			String fileName = scanner.nextLine();
			numObjects = scanner.nextInt(); // size of object in this problem instance
			
			// init problem
			qkValueWeight = new int[2][numObjects];
			qkPairValue = new int[numObjects][numObjects];
			
			// read in value
			for(int i = 0; i < numObjects; i++) {
				qkValueWeight[0][i] = scanner.nextInt();
			}
			
			// init array
			for(int i = 0; i < numObjects; i++) {
				for(int j = 0; j < numObjects; j++) {
					qkPairValue[i][j] = -1;
				}
			}
			
			// read in pair value
			for(int i = 0; i < numObjects-1; i++) {
				for(int j = i+1; j < numObjects; j++) {
					qkPairValue[i][j] = scanner.nextInt();
				}
			}
			
			// empty line and constraint
			scanner.nextInt();
			
			// read in capacity
			qkCapacity = scanner.nextInt();
			
			sumValue = new int[qkCapacity];
			
			// read in weight
			for(int i = 0; i < numObjects; i++) {
				qkValueWeight[1][i] = scanner.nextInt();
			}
			
			// close scanner
			scanner.close();
		} catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	
	public static void printProblem() {
		printProblemStat();
		System.out.println("*** Value and Weight ***");
		print(qkValueWeight);
		System.out.println("*** Pair Value ***");
		print(qkPairValue);
	}
	
	public static void printProblemStat() {
		System.out.println("*** The Problem ***");
		System.out.print("Number of Objects = " + numObjects);
		System.out.println(" Capacity = " + qkCapacity);
	}
	
	public static void print(int[][] twoDArray) {
		int size1 = twoDArray.length;
		int size2 = twoDArray[0].length;
		for(int i = 0; i < size1; i++){
			for(int j = 0; j < size2; j++){
				System.out.print(twoDArray[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	public static int run(){
		
		double[][] valueDensity = new double[2][numObjects]; // [0] valueDensity [1] Index
		
		int sumRelativeValue = 0;
		int sumWeight = 0;
		int result = 0;
		
		for(int i = 0; i < numObjects; i++){
			sumRelativeValue = 0;
			for(int j = 0; j < numObjects; j++){
				if(j > i){
					sumRelativeValue += qkPairValue[i][j];
				} else if (j < i){
					sumRelativeValue += qkPairValue[j][i];
				}
			}
			
			valueDensity[0][i] = (qkValueWeight[0][i] + sumRelativeValue)/ (double) qkValueWeight[1][i];
			valueDensity[1][i] = i;
		}
		
		// sort large to small
		sort(valueDensity);
		printArray(valueDensity);
		
		int[] solution1 = new int[numObjects];
		for(int i = 0; i < numObjects; i++)
			solution1[i] = -1;
		int counter = 0;
		while(sumWeight + qkValueWeight[1][ (int) valueDensity[1][counter]] <= qkCapacity && counter < numObjects){
			sumWeight += qkValueWeight[1][ (int) valueDensity[1][counter]];
			solution1[counter] = (int) valueDensity[1][counter];
			counter++;
		}
		
		int[] solution2 = new int[counter];
		for(int i = 0; i < counter; i++){
			solution2[i] = solution1[i];
			System.out.print(solution2[i]+ " ");
		}
		System.out.println();
		sortInt(solution2);
		
		for(int i = 0; i < counter; i++){
			System.out.print(solution2[i]+ " ");
		}
		System.out.println();
		result = getFitness(solution2);
		
		return result;
	}
	
	//selection sort
	public static void sort(double arr[][])
    {
        int n = arr[0].length;
 
        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (arr[0][j] > arr[0][min_idx])
                    min_idx = j;
 
            // Swap the found minimum element with the first
            // element
            double temp = arr[0][min_idx];
            arr[0][min_idx] = arr[0][i];
            arr[0][i] = temp;
			
			temp = arr[1][min_idx];
            arr[1][min_idx] = arr[1][i];
            arr[1][i] = temp;
        }
    }
	
	public static void sortInt(int arr[])
    {
        int n = arr.length;
 
        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (arr[j] < arr[min_idx])
                    min_idx = j;
 
            // Swap the found minimum element with the first
            // element
            int temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;
        }
    }
	
	public static void printArray(double arr[][])
    {
        int n = arr[0].length;
        for (int i=0; i<n; ++i)
            System.out.print(arr[0][i]+" ");
        System.out.println();
		
        for (int i=0; i<n; ++i)
            System.out.print(arr[1][i]+" ");
        System.out.println();
    }
	
	private static int getFitness(int[] chromosome) {
		int totalWieght = 0;
		int totalValue = 0;
		int size = chromosome.length;

		// Add up the pair value with the other object if they are choosen too
		for(int i = 0; i < size; i++) {
			for(int j = i+1; j < size; j++) {
				totalValue += qkPairValue[chromosome[i]][chromosome[j]];
				totalValue += qkValueWeight[0][chromosome[i]];
			}
		}
		
		return totalValue;
	}
}