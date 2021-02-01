package edu.iastate.metnet.metaomgraph.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.dizitart.no2.Document;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.MetadataCollection;

/**
 * 
 * @author sumanth
 * 
 * This class is used to display the selected points in scatter plot in a table.
 *
 */
public class SelectedSampleMetaDataDisplayTable{
	
	private ArrayList<String> sampleMetaDataCols;
	private MetadataCollection metaDataCol;
	
	/**
	 * default constructor
	 */
	public SelectedSampleMetaDataDisplayTable() {
		this(new ArrayList<String>());
	}
	
	/**
	 * Parametric constructor
	 * @param sampleMetaDataCols
	 */
	public SelectedSampleMetaDataDisplayTable(ArrayList<String> sampleMetaDataCols) {
		super();
		this.sampleMetaDataCols = sampleMetaDataCols;
		this.metaDataCol = MetaOmGraph.getActiveProject().getMetadataHybrid().getMetadataCollection();
	}
	
	/**
	 * Create new table model from the sample metadata rows.
	 * @param sampleMetaDataCols
	 * @return TableModel
	 */
	public TableModel getTableModel() {
		DefaultTableModel tableModel = new DefaultTableModel();

		String[] headers = metaDataCol.getHeaders();

		for (int i = 0; i < headers.length; i++) {
			tableModel.addColumn(headers[i]);
		}

		List<Document> metadataSelectedRows = metaDataCol.getRowsByDatacols(sampleMetaDataCols);
		for (int i = 0; i < metadataSelectedRows.size(); i++) {
			String[] temp = new String[headers.length];
			for (int j = 0; j < headers.length; j++) {
				temp[j] = metadataSelectedRows.get(i).get(headers[j]).toString();
			}
			tableModel.addRow(temp);
		}
		return (TableModel)tableModel;
	}
	
	/**
	 * Create new table model from the given sample metadata rows.
	 * @param sampleMetaDataCols
	 * @return
	 */
	public TableModel getTableModel(ArrayList<String> sampleMetaDataCols) {
		this.sampleMetaDataCols = sampleMetaDataCols;
		return getTableModel();
	}
	
	/**
	 * Return the sample meta data columns in the table
	 * @return
	 */
	public ArrayList<String> getMetaDataColsInTable(){
		return this.sampleMetaDataCols;
	}
	
	/**
	 * Clear all sample metadata columns in table.
	 */
	public void clearMetaDataCols() {
		if(this.sampleMetaDataCols != null)
			this.sampleMetaDataCols.clear();
	}
}
