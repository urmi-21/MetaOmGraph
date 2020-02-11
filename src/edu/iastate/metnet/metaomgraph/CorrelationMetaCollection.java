package edu.iastate.metnet.metaomgraph;

import java.util.List;

/**
 * This class defines a correlation run. This has list of all correlationmeta
 * objects and details of the correlation run.
 * 
 * @author mrbai
 *
 */
public class CorrelationMetaCollection {

	private String corrId;
	private String corrType; // pearson, spearmam , pooled etc
	private int corrTypeId;
	/**
	 * pooled=0 pearson=1 spearman=2 mutualInformation=3
	 * 
	 */
	private String corrModel; // random, grouped, fixed effect etc
	// row against wich corr was computed
	private String corrAgainstName;
	List<CorrelationMeta> corrMetaResList;

	// extra for MI
	private int bins;
	private int order;

	/**
	 * 
	 * @param corrId name of correlation run
	 * @param corrTypeId type of correlation 0,1,2 etc where  pooled=0 pearson=1 spearman=2 mutualInformation=3
	 * @param corrModel  correlation model used
	 * @param corrAgainstName variable/row against which correlations are computed
	 * @param corrMetaResList list of correlationMeta objects contains correlation for each row
	 */
	public CorrelationMetaCollection(String corrId, int corrTypeId, String corrModel, String corrAgainstName,
			List<CorrelationMeta> corrMetaResList) {
		/*
		 * this.corrId = corrId; this.corrTypeId = corrTypeId; this.corrType =
		 * getCorrType(corrTypeId); this.corrModel = corrModel; this.corrAgainstName =
		 * corrAgainstName; this.corrMetaResList = corrMetaResList;
		 */
		this(corrId, corrTypeId, corrModel, corrAgainstName, corrMetaResList, -1, -1);

	}

	// for MI
	public CorrelationMetaCollection(String corrId, int corrTypeId, String corrModel, String corrAgainstName,
			List<CorrelationMeta> corrMetaResList, int bins, int order) {
		this.corrId = corrId;
		this.corrTypeId = corrTypeId;
		this.corrType = getCorrType(corrTypeId);
		this.corrModel = corrModel;
		this.corrAgainstName = corrAgainstName;
		this.corrMetaResList = corrMetaResList;
		this.bins = bins;
		this.order = order;

	}

	public String getCorrType(int cid) {
		if (cid == 0) {
			return "Weighted Pearson";
		} else if (cid == 1) {
			return "Pearson";
		} else if (cid == 2) {
			return "Spearman";
		} else if (cid == 3) {
			return "Mutual Information";
		} else {
			return "Unknown";
		}
	}

	public int getCorrTypeId() {
		return this.corrTypeId;
	}

	public List<CorrelationMeta> getCorrList() {
		return this.corrMetaResList;
	}

	public String getCorrType() {
		return this.corrType;
	}

	public String getCorrId() {
		return this.corrId;
	}

	public String getCorrModel() {
		return this.corrModel;
	}

	public String getMIModelInfo() {
		if (this.corrTypeId != 3) {
			return "";
		} else {
			return "bins:";
		}
	}

	public String getCorrAgainst() {
		return this.corrAgainstName;
	}

	public int getBins() {
		return bins;
	}

	public int getOrder() {
		return order;
	}

	public String getcorrInfo() {
		String res = "";
		res += "Correlation type:" + getCorrType();
		res += "||" + "Correlation model:" + getCorrModel();
		res += "||" + "Correlation of row:" + getCorrAgainst();
		if (corrTypeId == 3) {
			res+="||"+"bins:"+getBins()+" order:"+getOrder();
		}

		return res;
	}
}
