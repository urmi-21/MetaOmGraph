package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CorrelationMultiThreaded {

	List<double[]> fullData;
	double[] target;
	int option;
	

	public CorrelationMultiThreaded(List<double[]> data, double[] target, int opt) {
		this.fullData = data;
		this.target = target;
		this.option = opt;

	}

	public Number[] compute() throws InterruptedException, ExecutionException {

		Number[] finalRes = null;
		int size = 8;

		List<Compute> taskList = new ArrayList<>();

		for (int start = 0; start < fullData.size(); start += size) {
			int end = Math.min(start + size, fullData.size());
			List<double[]> sublist = fullData.subList(start, end);
			taskList.add(new Compute(target, sublist));
		}

		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(4);

		List<Future<Number[]>> allRes = pool.invokeAll(taskList);
		
		finalRes=new Number[fullData.size()];
		int index=0;
		for(Future<Number[]> re:allRes ) {
			Number[] temp=re.get();
			for(int i=0;i<temp.length;i++) {
				finalRes[index++]=temp[i];
			}
			
		}
		

		return finalRes;
	}

}

class Compute implements Callable<Number[]> {
	double[] target;
	List<double[]> data;

	public Compute(double[] a, List<double[]> b) {
		this.target = a;
		this.data = b;
	}

	@Override
	public Number[] call() throws Exception {
		// TODO Auto-generated method stub
		Number[] res = new Number[data.size()];
		PearsonsCorrelation pc = new PearsonsCorrelation();

		for (int d = 0; d < data.size(); d++) {
			double thisVal=0.0D+pc.correlation(target, data.get(d));
			if(Double.isNaN(thisVal)) {
			res[d] = 0.0D;
			}else {
				res[d] = pc.correlation(target, data.get(d));
			}
		}

		return res;
	}

}