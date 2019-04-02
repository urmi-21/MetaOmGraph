package edu.iastate.metnet.metaomgraph;

import javax.swing.ImageIcon;

public class JavaIconTheme implements IconTheme {
	private ImageIcon defaultZoom;
	private ImageIcon externalSource;
	private ImageIcon legend;
	private ImageIcon listAdd;
	private ImageIcon listDelete;
	private ImageIcon listEdit;
	private ImageIcon listLoad;
	private ImageIcon listRename;
	private ImageIcon listSave;
	private ImageIcon math;
	private ImageIcon plot;
	private ImageIcon popupClose;
	private ImageIcon popupUp;
	private ImageIcon popups;
	private ImageIcon print;
	private ImageIcon properties;
	private ImageIcon saveAs;
	private ImageIcon shapes;
	private ImageIcon textFieldClear;
	private ImageIcon textFieldClearHover;
	private ImageIcon textFieldClearPress;
	private ImageIcon zoomIn;
	private ImageIcon zoomOut;
	private ImageIcon sort;
	private ImageIcon excel;
	private ImageIcon report;
	// urmi
	private ImageIcon palette;

	private String defaultZoomPath = "/resource/javaicon/Zoom16.gif";

	private String externalSourcePath = "/resource/javaicon/Search16.gif";

	private String legendPath = "/resource/customicon/legend16.gif";

	private String listAddPath = "/resource/javaicon/Add16.gif";

	private String listDeletePath = "/resource/javaicon/Delete16.gif";

	public JavaIconTheme() {
	}

	private String listEditPath = "/resource/javaicon/Edit16.gif";

	private String listLoadPath = "/resource/javaicon/Open16.gif";

	private String listRenamePath = "/resource/customicon/Rename16.gif";

	private String listSavePath = "/resource/javaicon/Save16.gif";

	private String mathPath = "/resource/customicon/math16.png";

	private String plotPath = "/resource/customicon/plot16.png";

	private String popupClosePath = "/resource/customicon/yellow_close_icon.gif";

	private String popupUpPath = "/resource/customicon/yellow_arrow.gif";

	private String popupsPath = "/resource/customicon/popup16.png";

	private String printPath = "/resource/javaicon/Print16.gif";

	private String propertiesPath = "/resource/javaicon/Properties16.gif";

	private String saveAsPath = "/resource/javaicon/SaveAs16.gif";

	private String shapesPath = "/resource/customicon/shapesicon16.gif";

	private String textFieldClearPath = "/resource/customicon/light_grey_close_icon.gif";

	private String textFieldClearHoverPath = "/resource/customicon/yellow_close_icon.gif";

	private String textFieldClearPressPath = "/resource/customicon/dark_yellow_close_icon.gif";

	private String zoomInPath = "/resource/javaicon/ZoomIn16.gif";

	private String zoomOutPath = "/resource/javaicon/ZoomOut16.gif";

	private String sortPath = "/resource/customicon/sorticon16.png";

	private String excelPath = "/resource/customicon/excel16.gif";

	private String reportPath = "/resource/javaicon/History16.gif";

	public ImageIcon getProperties() {
		if (properties == null) {
			properties = new ImageIcon(getClass().getResource(propertiesPath));
		}
		return properties;
	}

	public ImageIcon getSaveAs() {
		if (saveAs == null) {
			saveAs = new ImageIcon(getClass().getResource(saveAsPath));
		}
		return saveAs;
	}

	public ImageIcon getPrint() {
		if (print == null) {
			print = new ImageIcon(getClass().getResource(printPath));
		}
		return print;
	}

	public ImageIcon getZoomIn() {
		if (zoomIn == null) {
			zoomIn = new ImageIcon(getClass().getResource(zoomInPath));
		}
		return zoomIn;
	}

	public ImageIcon getZoomOut() {
		if (zoomOut == null) {
			zoomOut = new ImageIcon(getClass().getResource(zoomOutPath));
		}
		return zoomOut;
	}

	public ImageIcon getDefaultZoom() {
		if (defaultZoom == null) {
			defaultZoom = new ImageIcon(getClass().getResource(defaultZoomPath));
		}
		return defaultZoom;
	}

	public ImageIcon getShapes() {
		if (shapes == null) {
			shapes = new ImageIcon(getClass().getResource(shapesPath));
		}
		return shapes;
	}

	public ImageIcon getLegend() {
		if (legend == null) {
			legend = new ImageIcon(getClass().getResource(legendPath));
		}
		return legend;
	}

	public ImageIcon getPopups() {
		if (popups == null) {
			popups = new ImageIcon(getClass().getResource(popupsPath));
		}
		return popups;
	}

	public ImageIcon getPopupClose() {
		if (popupClose == null) {
			popupClose = new ImageIcon(getClass().getResource(popupClosePath));
		}
		return popupClose;
	}

	public ImageIcon getPopupUp() {
		if (popupUp == null) {
			popupUp = new ImageIcon(getClass().getResource(popupUpPath));
		}
		return popupUp;
	}

	public ImageIcon getTextFieldClear() {
		if (textFieldClear == null) {
			textFieldClear = new ImageIcon(getClass().getResource(textFieldClearPath));
		}
		return textFieldClear;
	}

	public ImageIcon getTextFieldClearHover() {
		if (textFieldClearHover == null) {
			textFieldClearHover = new ImageIcon(getClass().getResource(textFieldClearHoverPath));
		}
		return textFieldClearHover;
	}

	public ImageIcon getTextFieldClearPress() {
		if (textFieldClearPress == null) {
			textFieldClearPress = new ImageIcon(getClass().getResource(textFieldClearPressPath));
		}
		return textFieldClearPress;
	}

	public ImageIcon getListLoad() {
		if (listLoad == null) {
			listLoad = new ImageIcon(getClass().getResource(listLoadPath));
		}
		return listLoad;
	}

	public ImageIcon getListSave() {
		if (listSave == null) {
			listSave = new ImageIcon(getClass().getResource(listSavePath));
		}
		return listSave;
	}

	public ImageIcon getListDelete() {
		if (listDelete == null) {
			listDelete = new ImageIcon(getClass().getResource(listDeletePath));
		}
		return listDelete;
	}

	public ImageIcon getListAdd() {
		if (listAdd == null) {
			listAdd = new ImageIcon(getClass().getResource(listAddPath));
		}
		return listAdd;
	}

	public ImageIcon getListEdit() {
		if (listEdit == null) {
			listEdit = new ImageIcon(getClass().getResource(listEditPath));
		}
		return listEdit;
	}

	public ImageIcon getListRename() {
		if (listRename == null) {
			listRename = new ImageIcon(getClass().getResource(listRenamePath));
		}
		return listRename;
	}

	public ImageIcon getPlot() {
		if (plot == null) {
			plot = new ImageIcon(getClass().getResource(plotPath));
		}
		return plot;
	}

	public ImageIcon getMath() {
		if (math == null) {
			math = new ImageIcon(getClass().getResource(mathPath));
		}
		return math;
	}

	public ImageIcon getReport() {
		if (report == null) {
			report = new ImageIcon(getClass().getResource(reportPath));
		}
		return report;
	}

	public ImageIcon getExternalSource() {
		if (externalSource == null) {
			externalSource = new ImageIcon(getClass().getResource(externalSourcePath));
		}
		return externalSource;
	}

	public ImageIcon getSort() {
		if (sort == null) {
			sort = new ImageIcon(getClass().getResource(sortPath));
		}
		return sort;
	}

	public ImageIcon getExcel() {
		if (excel == null) {
			excel = new ImageIcon(getClass().getResource(excelPath));
		}
		return excel;
	}

	public static void main(String[] args) {
		java.lang.reflect.Method[] methods = JavaIconTheme.class.getDeclaredMethods();
		JavaIconTheme theme = new JavaIconTheme();
		for (java.lang.reflect.Method thisMethod : methods) {
			try {
				if (!thisMethod.getName().equals("main")) {
					System.out.print("Executing " + thisMethod.getName() + "...");
					System.out.flush();
					Object[] nullset = null;
					thisMethod.invoke(theme, nullset);
					System.out.println("done!");
				}
			} catch (Exception e) {
				System.err.println(thisMethod.getName() + " had a problem!");
			}
		}
	}

	public ImageIcon getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageIcon getPalette() {
		if (palette == null) {
			palette = new ImageIcon(getClass().getResource(propertiesPath));
		}
		return palette;
	}

	@Override
	public ImageIcon getOpts() {
		// TODO Auto-generated method stub
		return null;
	}
}
