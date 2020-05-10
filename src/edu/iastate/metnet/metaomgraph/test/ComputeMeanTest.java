/**
 * 
 */
package edu.iastate.metnet.metaomgraph.test;

import java.util.ArrayList;

import edu.iastate.metnet.metaomgraph.ComputeMean;
/**
 * @author sumanth
 *
 */
public class ComputeMeanTest {
	
	private static ArrayList<Integer> integerList;
	
	private static void OutPutTestResult(String testCaseName, double actualValue, double expectedValue) {
		if(actualValue == expectedValue) {
			System.out.println(testCaseName + ": " + "passed");
		}
		else {
			System.out.println(testCaseName + ": " + "failed");
			System.out.println("Actual value" + ": " + actualValue);
			System.out.println("Expected value" + ": " + expectedValue);
		}
		System.out.println();
	}
	
	private static void TestMeanwithNoElements() {
		integerList.clear();
	}
	
	private static void TestMeanwith1Element() {
		integerList.clear();
		integerList.add(1);
	}
	
	private static void TestMeanwithNegativeElement() {
		integerList.clear();
		integerList.add(-3);
	}
	
	private static void TestMeanwithEvenLengthElement() {
		integerList.clear();
		integerList.add(2);
		integerList.add(3);
		integerList.add(4);
		integerList.add(5);
	}
	
	private static void TestMeanwithOddLengthElement() {
		integerList.clear();
		integerList.add(7);
		integerList.add(10);
		integerList.add(15);
		integerList.add(23);
		integerList.add(50);
	}
	
	private static void TestMeanwithPositiveNegativeElements() {
		integerList.clear();
		integerList.add(-3);
		integerList.add(100);
		integerList.add(-27);
		integerList.add(-33);
		integerList.add(70);	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		integerList = new ArrayList<Integer>();
		TestMeanwithNoElements();
		ComputeMean cm = new ComputeMean(integerList);
		OutPutTestResult("No Elements", cm.GetMean(), 0.0);
		
		TestMeanwith1Element();
		cm.UpdateArrayList(integerList);
		OutPutTestResult("1 Element", cm.GetMean(), 1.0);
		
		TestMeanwithNegativeElement();
		cm.UpdateArrayList(integerList);
		OutPutTestResult("Negative Elements", cm.GetMean(), -3.0);
		
		TestMeanwithEvenLengthElement();
		cm.UpdateArrayList(integerList);
		OutPutTestResult("Even Length Element list", cm.GetMean(), 3.5);
		
		TestMeanwithOddLengthElement();
		cm.UpdateArrayList(integerList);
		OutPutTestResult("Odd Length Element list", cm.GetMean(), 21);
		
		TestMeanwithPositiveNegativeElements();
		cm.UpdateArrayList(integerList);
		OutPutTestResult("Positive and Negative Elements", cm.GetMean(), 21.4);
		
	}

}
