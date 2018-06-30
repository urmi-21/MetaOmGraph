package edu.iastate.metnet.metaomgraph.test;

import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;


public class XMLtoExcel {
    public XMLtoExcel() {
    }

    public static void main(String[] args) {
        File source = Utils.chooseFileToOpen();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document myDoc = builder.build(source);
            Element rootNode = (Element) myDoc.getContent(new ElementFilter()).get(0);
            List<Element> content = rootNode.getChildren();
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("MetaOmGraph data");
            int rowNum = 0;
            for (Element thisObj : content) {
                outputElement(thisObj, sheet.createRow(rowNum++));
            }
            File destination = Utils.chooseFileToSave();
            FileOutputStream fileOut = new FileOutputStream(destination);
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            JOptionPane.showMessageDialog(
                    MetaOmGraph.getDesktop(),
                    "The specified project contains extended information, but it appears to be corrupt.",
                    "Error", 0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MetaOmGraph.getDesktop(),
                    "Error reading extended info", "IOException",
                    0);
        }
    }

    private static void outputElement(Element outputMe, HSSFRow row) {
        List<Element> children = outputMe.getChildren();
        short column = 0;
        for (Element thisChild : children) {
            String cellText = thisChild.getName() + ": ";
            if (thisChild.getText().trim().equals("")) {
                cellText = cellText + thisChild.getAttributeValue("name");
            } else {
                cellText = cellText + thisChild.getText().trim();
            }
            row.createCell(column++).setCellValue(cellText);
        }
    }
}
