package edu.iastate.metnet.metaomgraph;

import javax.swing.ImageIcon;

public class TangoIconTheme implements IconTheme {
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
    private ImageIcon metadata;
    private ImageIcon palette;
    
    private String dir = "/resource/tango/16x16/";

    private String defaultZoomPath = dir + "actions/system-search.png";

    private String externalSourcePath = dir + "categories/applications-internet.png";

    private String legendPath = "/resource/tango/legend16.png";

    private String listAddPath = dir + "actions/list-add.png";

    private String listDeletePath = dir + "actions/list-remove.png";

    private String listEditPath = dir + "apps/accessories-text-editor.png";

    public TangoIconTheme() {
    }

    private String listLoadPath = dir + "actions/document-open.png";

    private String listRenamePath = "/resource/silkicon/textfield_rename.png";

    private String listSavePath = dir + "actions/document-save.png";

    private String mathPath = dir + "apps/accessories-calculator.png";

    private String plotPath = "/resource/silkicon/chart_line.png";

    private String popupClosePath = "/resource/customicon/yellow_close_icon.gif";

    private String popupUpPath = "/resource/customicon/yellow_arrow.gif";

    private String popupsPath = "/resource/customicon/popup16.png";

    private String printPath = dir + "actions/document-print.png";

    private String propertiesPath = dir + "actions/document-properties.png";

    private String saveAsPath = dir + "actions/document-save-as.png";

    private String shapesPath = "/resource/silkicon/silkshapes.png";

    private String textFieldClearPath = "/resource/customicon/light_grey_close_icon.gif";

    private String textFieldClearHoverPath = "/resource/customicon/yellow_close_icon.gif";

    private String textFieldClearPressPath = "/resource/customicon/dark_yellow_close_icon.gif";

    private String zoomInPath = "/resource/tango/zoomin16.png";

    private String zoomOutPath = "/resource/tango/zoomout16.png";

    private String sortPath = "/resource/customicon/sorticon16.png";

    private String excelPath = "/resource/customicon/excel16.gif";

    private String reportPath = dir + "mimetypes/text-x-generic.png";

    private String metadataPath = dir + "status/dialog-information.png";

    public ImageIcon getProperties() {
        if (properties == null) {
            properties = new ImageIcon(getClass().getResource(propertiesPath));
        }
        return properties;
    }
    
    //urmi
    public ImageIcon getPalette() {
        if (palette == null) {
        	palette = new ImageIcon(getClass().getResource(propertiesPath));
        }
        return palette;
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
            textFieldClear = new ImageIcon(getClass().getResource(
                    textFieldClearPath));
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
            listSave = new ImageIcon(getClass().getResource(listAddPath));
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
            externalSource = new ImageIcon(getClass().getResource(
                    externalSourcePath));
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

    public ImageIcon getMetadata() {
        if (metadata == null) {
            metadata = new ImageIcon(getClass().getResource(metadataPath));
        }
        return metadata;
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
}
