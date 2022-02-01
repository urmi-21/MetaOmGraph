package edu.iastate.metnet.metaomgraph;

import javax.swing.ImageIcon;

public class SilkIconTheme implements IconTheme {
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
    private ImageIcon palette;
    private ImageIcon groupBy;
    private ImageIcon rIcon;
    private ImageIcon selectIcon;
    private ImageIcon clusterIcon;
    
    private String defaultZoomPath = "/resource/silkicons/zoom.png";

    private String externalSourcePath = "/resource/silkicons/world_link.png";

    private String legendPath = "/resource/customicon/legend16.gif";

    private String listAddPath = "/resource/silkicons/add.png";

    private String listDeletePath = "/resource/silkicons/delete.png";

    public SilkIconTheme() {
    }

    private String listEditPath = "/resource/silkicons/pencil.png";

    private String listLoadPath = "/resource/silkicons/accept.png";

    private String listRenamePath = "/resource/silkicons/textfield_rename.png";

    private String listSavePath = "/resource/silkicons/disk.png";

    private String mathPath = "/resource/silkicons/calculator.png";

    private String plotPath = "/resource/silkicons/chart_line.png";

    private String popupClosePath = "/resource/customicon/yellow_close_icon.gif";

    private String popupUpPath = "/resource/customicon/yellow_arrow.gif";

    private String popupsPath = "/resource/customicon/popup16.png";

    private String printPath = "/resource/silkicons/printer.png";

    private String propertiesPath = "/resource/silkicons/page_white_wrench.png";

    private String saveAsPath = "/resource/silkicons/disk.png";

    private String shapesPath = "/resource/silkicons/silkshapes.png";

    private String textFieldClearPath = "/resource/customicon/light_grey_close_icon.gif";

    private String textFieldClearHoverPath = "/resource/customicon/yellow_close_icon.gif";

    private String textFieldClearPressPath = "/resource/customicon/dark_yellow_close_icon.gif";

    private String zoomInPath = "/resource/silkicons/zoom_in.png";

    private String zoomOutPath = "/resource/silkicons/zoom_out.png";

    private String sortPath = "/resource/customicon/sorticon16.png";

    private String excelPath = "/resource/customicon/excel16.gif";

    private String reportPath = "/resource/silkicons/newspaper.png";
    
    private String groupByPath = "/resource/customicon/categorizeIcon16.png";
    
    private String rIconPath =  "/resource/customicon/r_logoIcon16.png";
    
    private String selectIconPath = "/resource/customicon/select16.png";
    
    private final String clusterIconPath = "/resource/customicon/cluster16.png";

    @Override
	public ImageIcon getProperties() {
        if (properties == null) {
            properties = new ImageIcon(getClass().getResource(propertiesPath));
        }
        return properties;
    }

    @Override
	public ImageIcon getSaveAs() {
        if (saveAs == null) {
            saveAs = new ImageIcon(getClass().getResource(saveAsPath));
        }
        return saveAs;
    }

    @Override
	public ImageIcon getPrint() {
        if (print == null) {
            print = new ImageIcon(getClass().getResource(printPath));
        }
        return print;
    }

    @Override
	public ImageIcon getZoomIn() {
        if (zoomIn == null) {
            zoomIn = new ImageIcon(getClass().getResource(zoomInPath));
        }
        return zoomIn;
    }

    @Override
	public ImageIcon getZoomOut() {
        if (zoomOut == null) {
            zoomOut = new ImageIcon(getClass().getResource(zoomOutPath));
        }
        return zoomOut;
    }

    @Override
	public ImageIcon getDefaultZoom() {
        if (defaultZoom == null) {
            defaultZoom = new ImageIcon(getClass().getResource(defaultZoomPath));
        }
        return defaultZoom;
    }

    @Override
	public ImageIcon getShapes() {
        if (shapes == null) {
            shapes = new ImageIcon(getClass().getResource(shapesPath));
        }
        return shapes;
    }

    @Override
	public ImageIcon getLegend() {
        if (legend == null) {
            legend = new ImageIcon(getClass().getResource(legendPath));
        }
        return legend;
    }

    @Override
	public ImageIcon getPopups() {
        if (popups == null) {
            popups = new ImageIcon(getClass().getResource(popupsPath));
        }
        return popups;
    }

    @Override
	public ImageIcon getPopupClose() {
        if (popupClose == null) {
            popupClose = new ImageIcon(getClass().getResource(popupClosePath));
        }
        return popupClose;
    }

    @Override
	public ImageIcon getPopupUp() {
        if (popupUp == null) {
            popupUp = new ImageIcon(getClass().getResource(popupUpPath));
        }
        return popupUp;
    }

    @Override
	public ImageIcon getTextFieldClear() {
        if (textFieldClear == null) {
            textFieldClear = new ImageIcon(getClass().getResource(textFieldClearPath));
        }
        return textFieldClear;
    }

    @Override
	public ImageIcon getTextFieldClearHover() {
        if (textFieldClearHover == null) {
            textFieldClearHover = new ImageIcon(getClass().getResource(textFieldClearHoverPath));
        }
        return textFieldClearHover;
    }

    @Override
	public ImageIcon getTextFieldClearPress() {
        if (textFieldClearPress == null) {
            textFieldClearPress = new ImageIcon(getClass().getResource(textFieldClearPressPath));
        }
        return textFieldClearPress;
    }

    @Override
	public ImageIcon getListLoad() {
        if (listLoad == null) {
            listLoad = new ImageIcon(getClass().getResource(listLoadPath));
        }
        return listLoad;
    }

    @Override
	public ImageIcon getListSave() {
        if (listSave == null) {
            listSave = new ImageIcon(getClass().getResource(listSavePath));
        }
        return listSave;
    }

    @Override
	public ImageIcon getListDelete() {
        if (listDelete == null) {
            listDelete = new ImageIcon(getClass().getResource(listDeletePath));
        }
        return listDelete;
    }

    @Override
	public ImageIcon getListAdd() {
        if (listAdd == null) {
            listAdd = new ImageIcon(getClass().getResource(listAddPath));
        }
        return listAdd;
    }

    @Override
	public ImageIcon getListEdit() {
        if (listEdit == null) {
            listEdit = new ImageIcon(getClass().getResource(listEditPath));
        }
        return listEdit;
    }

    @Override
	public ImageIcon getListRename() {
        if (listRename == null) {
            listRename = new ImageIcon(getClass().getResource(listRenamePath));
        }
        return listRename;
    }

    @Override
	public ImageIcon getPlot() {
        if (plot == null) {
            plot = new ImageIcon(getClass().getResource(plotPath));
        }
        return plot;
    }

    @Override
	public ImageIcon getMath() {
        if (math == null) {
            math = new ImageIcon(getClass().getResource(mathPath));
        }
        return math;
    }

    @Override
	public ImageIcon getReport() {
        if (report == null) {
            report = new ImageIcon(getClass().getResource(reportPath));
        }
        return report;
    }

    @Override
	public ImageIcon getExternalSource() {
        if (externalSource == null) {
            externalSource = new ImageIcon(getClass().getResource(
                    externalSourcePath));
        }
        return externalSource;
    }

    @Override
	public ImageIcon getSort() {
        if (sort == null) {
            sort = new ImageIcon(getClass().getResource(sortPath));
        }
        return sort;
    }

    @Override
	public ImageIcon getExcel() {
        if (excel == null) {
            excel = new ImageIcon(getClass().getResource(excelPath));
        }
        return excel;
    }
    
    //urmi
    @Override
	public ImageIcon getPalette() {
		if (palette == null) {
			palette = new ImageIcon(getClass().getResource(propertiesPath));
		}
		return palette;
	}

    public static void main(String[] args) {
        java.lang.reflect.Method[] methods = SilkIconTheme.class.getDeclaredMethods();
        IconTheme theme = new SilkIconTheme();
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

    @Override
	public ImageIcon getMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public ImageIcon getOpts() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ImageIcon getGroupBy() {
		if(groupBy == null) {
			groupBy = new ImageIcon(getClass().getResource(groupByPath));
		}
		return groupBy;
	}
	
	public ImageIcon getRIcon() {
		if(rIcon == null) {
			rIcon = new ImageIcon(getClass().getResource(rIconPath));
		}
		return rIcon;
	}
	
	public ImageIcon getSelectIcon() {
		if(selectIcon == null) {
			selectIcon = new ImageIcon(getClass().getResource(selectIconPath));
		}
		return selectIcon;
	}

    @Override
    public ImageIcon getReset() {
        return null;
    }

    public ImageIcon getClusterIcon() {

		if(clusterIcon == null) {
			clusterIcon = new ImageIcon(getClass().getResource(clusterIconPath));
		}
		return clusterIcon;
	}
}
