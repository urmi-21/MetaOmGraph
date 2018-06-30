package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.hiveface.GobiNode;

import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;


public class PathwaySelectionFrame
        extends JFrame {
    private static PathwaySelectionFrame instance = null;

    private JButton submit;

    private static ArrayList<Integer> pathwayList = new ArrayList();

    private static ArrayList<String> pathwayNameList = new ArrayList();


    private static ArrayList<String> allGenesLocusID;

    static ArrayList<GobiNode> gobiNodeList;


    public static ArrayList<String> getAllGenesLocusID() {
        return allGenesLocusID;
    }


    public static void setAllGenesLocusID(ArrayList<String> allGenesLocusID) {
        allGenesLocusID = allGenesLocusID;
    }


    public static ArrayList<GobiNode> getGobiNodeList() {
        return gobiNodeList;
    }


    public static void setGobiNodeList(ArrayList<GobiNode> gobiNodeList) {
        gobiNodeList = gobiNodeList;
    }


    public static ArrayList<String> getPathwayNameList() {
        return pathwayNameList;
    }


    public static void setPathwayNameList(ArrayList<String> pathwayNameList) {
        pathwayNameList = pathwayNameList;
    }


    public static ArrayList<Integer> getPathwayList() {
        return pathwayList;
    }


    public static void setPathwayList(ArrayList<Integer> pathwayList) {
        pathwayList = pathwayList;
    }


    public static PathwaySelectionFrame getInstance() {
        if (instance == null) {
            instance = new PathwaySelectionFrame();
        }
        return instance;
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.dispose();
            instance = null;
            System.gc();
        }
    }

    private PathwaySelectionFrame() {
        super("Pathway Selection Window");
    }

    public void init(Map pathwayMap, ArrayList<GobiNode> gobiNodeList, ArrayList<String> allGenesLocusID) {
    }
}
