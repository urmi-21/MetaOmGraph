package edu.iastate.metnet.metaomgraph;

import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * 
 * @author urmi class to store meta-analysis correlation data and display them
 *         for later use. There will be a separate Jinternal frame to display
 *         this as main table can get crowded. main table only displays
 *         correlation values. Save these objects in myproject class.
 *
 */
public class CorrelationMeta {

	private double rval;
	private double pval;
	private double zval;
	private double rCIstrt;
	private double rCIend;
	private double pooledzr;
	private double stdErr;
	private double qval;
	private double q_pval;
	private double alpha = 0.05;
	DecimalFormat df = new DecimalFormat("#.####");
	// row coressponding to this result
	private String targetName;

	public CorrelationMeta() {

	}

	public CorrelationMeta(double r, double p, double z, double q, double pooledzr, double stderr) {

		this.rval = r;
		this.pval = p;
		this.zval = z;
		this.qval = q;
		this.pooledzr = pooledzr;
		this.stdErr = stderr; // calculate CI rCIstrt =
		setCIstrt(this.alpha);
		rCIend = setCIend(this.alpha);

	}

	private double setCIstrt(double alpha) {
		NormalDistribution stdNorm = new NormalDistribution();
		double res = pooledzr + (stdNorm.inverseCumulativeProbability(alpha / 2.0) * stdErr);
		return res;
	}

	private double setCIend(double alpha) {
		NormalDistribution stdNorm = new NormalDistribution();
		double res = pooledzr - (stdNorm.inverseCumulativeProbability(alpha / 2.0) * stdErr);
		return res;

	}

	/**
	 * constructor for pearson correlation with only r and pval
	 * 
	 * @param r
	 * @param p
	 */
	public CorrelationMeta(double r, double p) {
		this.rval = r;
		this.pval = p;
	}

	public double getrVal() {
		return this.rval;
	}

	public double getpVal() {
			
		return this.pval;
	}
	
	public String getpValString() {
		
		return df.format(pval);
	}

	public double getzVal() {
		return this.zval;
	}

	public double getqVal() {
		return this.qval;
	}

	public String getName() {
		return this.targetName;
	}

	public void settargetName(String name) {
		this.targetName = name;
	}

	public String getrCI() {
		String res = "";
		res = res + "[" + String.format("%.2f", this.rCIstrt) + "," + String.format("%.2f", this.rCIend) + "]";
		return res;

	}

	public String getrCI(double alpha) {
		this.rCIstrt = setCIstrt(alpha);
		this.rCIend = setCIend(alpha);

		return getrCI();

	}

	public double getpooledzr() {
		return this.pooledzr;
	}

	public double getstdErr() {
		return this.stdErr;
	}

}
