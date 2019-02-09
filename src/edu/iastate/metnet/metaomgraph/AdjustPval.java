package edu.iastate.metnet.metaomgraph;

/**
 * Class contains p value adjustment methods
 * @author mrbai
 *
 */
public class AdjustPval {
	
	
	public double[] getBonferroniAdj(double[] pvals) {
		double[] res=new double[pvals.length];
		int m=pvals.length;
		for(int i=0;i<res.length;i++) {
			res[i]=pvals[i]*m;
		}
		
		return res;
	}
	
}


