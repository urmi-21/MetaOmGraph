package edu.iastate.metnet.metaomgraph.test;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.metnet.metaomgraph.ComputeMean;

public class ComputeMeanTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ComputeMean	c = new ComputeMean();
		List<Integer> first = new ArrayList<Integer>();
		List<Integer> second = new ArrayList<Integer>();
		List<Integer> third = new ArrayList<Integer>();
		List<Integer> fourth = new ArrayList<Integer>();

		int result1,result2,result3,result4;

		//Test 1
		first.add(33);
		first.add(92);
		first.add(81);
		first.add(2);
		result1 = c.getMean(first);
		System.out.println("Result of Testcase 1 : "+result1);
		if(result1 == 52) {
			System.out.println("Testcase 1 Passed\n");
		}
		else {
			System.out.println("Testcase 1 Failed\n");
		}


		
		//Test 2
		second.add(-89);
		second.add(29288);
		second.add(-188829);
		second.add(0);
		second.add(-119);
		result2 = c.getMean(second);
		System.out.println("Result of Testcase 2 : "+result2);
		if(result2 == -31949) {
			System.out.println("Testcase 2 Passed\n");
		}
		else {
			System.out.println("Testcase 2 Failed\n");
		}

		//Test 3
		result3 = c.getMean(third);
		System.out.println("Result of Testcase 3 : "+result3);
		if(result3 == 0) {
			System.out.println("Testcase 3 Passed\n");
		}
		else {
			System.out.println("Testcase 3 Failed\n");
		}
		
		//Test 4
		fourth.add(-22);
		fourth.add(12);
		fourth.add(8);
		fourth.add(2);
		result4 = c.getMean(fourth);
		System.out.println("Result of Testcase 4 : "+result4);
		if(result4 == 0) {
			System.out.println("Testcase 4 Passed\n");
		}
		else {
			System.out.println("Testcase 4 Failed\n");
		}


	}

}
