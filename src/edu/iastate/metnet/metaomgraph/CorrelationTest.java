package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

public class CorrelationTest {

	public static void main(String[] args) {
		System.out.println("In Main");
		ExecutorService pool = Executors.newFixedThreadPool(4);

		Worker worker1 = new Worker("1");
		Worker worker2 = new Worker("2");
		Worker worker3 = new Worker("3");
		Worker worker4 = new Worker("4");
		/*
		pool.submit(worker1);
		pool.submit(worker2);
		pool.submit(worker3);
		pool.submit(worker4);
		pool.shutdown();
		*/
		//pool.in
		System.out.println("Hello World");
		
		int []list1=new int[800];
		for(int i=0;i<list1.length;i++) {
			list1[i]=i;
		}
		
		int []list2=new int[800];
		for(int i=0;i<list2.length;i++) {
			list2[i]=i+100;
		}
		
		int []list3=new int[800];
		for(int i=0;i<list3.length;i++) {
			list3[i]=i+500;
		}
		
		java.util.List<Sq> oblist=new ArrayList<>();
		
		oblist.add(new Sq(list1));
		oblist.add(new Sq(list2));
		oblist.add(new Sq(list3));
		oblist.add(new Sq(list3));
		oblist.add(new Sq(list2));
		oblist.add(new Sq(list1));
		oblist.add(new Sq(list1));
		oblist.add(new Sq(list2));
		oblist.add(new Sq(list3));
		java.util.List<Future<int[]>> results = null;
		try {
			List<Future<double[]>> r1=pool.invokeAll(oblist);
			for(Future<double[]> re:r1 ) {
				try {
					System.out.println("r1:"+Arrays.toString(re.get()));
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	/*	Future<double[]> r1=pool.submit(new Sq(list1));
		try {
			System.out.println("r1:"+Arrays.toString(r1.get()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
}

class Sq implements Callable<double[]> {
	int [] arr;
	public Sq(int[] a) {
		arr=a;
	}
	@Override
	public double[] call() throws Exception {
		// TODO Auto-generated method stub
		double [] res=new double[arr.length];
		for(int i=0;i<arr.length;i++) {
			res[i]=arr[i]*arr[i];
		}
		return res;
	}
	
}

class Worker implements Callable {

	String identifier;

	Worker(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String call() throws Exception {
		System.out.println("Worker ID: " + this.identifier);

		for (int i = 0; i < 10; i++) {
			System.out.println("ID: " + this.identifier + " ,Value: " + i);
		}

		return null;
	}

}