package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.File;
import java.security.InvalidParameterException;
import javax.swing.filechooser.FileFilter;

public class GraphFileFilter extends FileFilter {
	public static final int XML = 0;
	public static final int TEXT = 1;
	public static final int PROJECT = 2;
	public static final int PNG = 3;
	public static final int PNGSVG = 4;
	public static final int TREE = 5;
	private int mode;

	public GraphFileFilter() {
		this(1);
	}

	public GraphFileFilter(int mode) {
		setMode(mode);
	}

	public void setMode(int mode) {
		if ((mode != 0) && (mode != 1) && (mode != 2) && (mode != 3) && (mode != 4) && (mode != 5)) {
			throw new InvalidParameterException("mode must be one of GraphFileFilter.XML, .TEXT, .PROJECT, or.PNG");
		}
		this.mode = mode;
	}

	public boolean accept(File arg0) {
		if (arg0.isDirectory())
			return true;
		String ext = Utils.getExtension(arg0);
		if (ext == null)
			return false;
		if (((ext.equals("txt")) || (ext.equals("csv")) || (ext.equals("tsv"))) && (mode == 1))
			return true;
		if (((ext.equals("mcg")) || (ext.equals("mog"))) && (mode == 2))
			return true;
		if ((ext.equals("xml")) && (mode == 0))
			return true;
		if ((ext.equals("tree")) && (mode == 5))
			return true;
		if ((ext.equals("png")) && (mode == 3))
			return true;
		if ((ext.equals("png") || (ext.equals("svg"))) && (mode == 4))
			return true;
		//else
		return false;
	}

	public String getDescription() {
		switch (mode) {
		case 0:
			return "XML files (*.xml)";
		case 2:
			return "MetaOmGraph project files (*.mog, *.mcg)";
		case 1:
			return "Delimited text files (*.txt, *.csv, *.tsv)";
		case 3:
			return "PNG image files (*.png)";
		case 4:
			return "Image files (*.png,*.svg)";
		case 5:
			return "MOG TREE files (*.tree)";
		}
		return "Unknown mode";
	}
}
