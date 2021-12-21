package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.NoneditableTableModel;
import edu.iastate.metnet.metaomgraph.ui.StripedTable;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdom.Element;


public class RepInfo implements XMLizable {
    private static final int COLUMN_COL = 0;
    private static final int EXP_COL = 1;
    private static final int SAMPLE_COL = 2;
    private static final int GNAME_COL = 3;
    private static final int TREATMENT_COL = 4;
    private TreeMap<Integer, Integer> columnMap;
    private TreeMap<String, Integer> sampleNameCount;
    private TreeMap<Integer, ArrayList<Sample>> repGroups;
    private TreeMap<Integer, String> repGroupNames;
    private TreeMap<Integer, ArrayList<Sample>> goodGroups;
    private TreeSet<Integer> badCols;
    private Vector<ChangeListener> listeners;

    public RepInfo(MetaOmProject myProject) {
        Metadata root = myProject.getMetadata();
        if (root != null) {
            findRepsMetadata(myProject);
        } else {
            findReps(myProject);
        }
    }

    public RepInfo(Element source) {
        fromXML(source);
    }


    public RepInfo() {
    }

    public static class Sample implements Comparable<Sample> {
        public String name;
        public String expID;
        public int col;

        public Sample(String name, String expID, int col) {
            this.name = name;
            this.expID = expID;
            this.col = col;
        }

        @Override
		public boolean equals(Object obj) {
            if (!(obj instanceof Sample)) {
                return false;
            }
            return compareTo((Sample) obj) == 0;
        }

        @Override
		public int compareTo(Sample o) {
            if ((name.equals(name)) && (expID.equals(expID))) {
                return 0;
            }
            if (!name.equals(name)) {
                return name.compareTo(name);
            }
            return expID.compareTo(expID);
        }

        @Override
		public String toString() {
            return expID + ": " + name;
        }
    }

    private void findRepsMetadata(MetaOmProject myProject) {
        System.out.println("Finding replicates");

        columnMap = new TreeMap();
        sampleNameCount = new TreeMap();
        repGroups = new TreeMap();
        repGroupNames = new TreeMap();
        BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Finding Replicates",
                "Searching Metadata for Replicates", 0L, myProject.getDataColumnCount(), true);
        Metadata root = myProject.getMetadata();
        new Thread() {


            @Override
			public void run() {

                throw new Error("Unresolved compilation problems: \n\tThe method getChildCount() is undefined for the type Metadata\n\tThe method getChildAt(int) is undefined for the type Metadata\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");

            }


        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            columnMap = null;
            repGroups = null;
        }
    }


    private void findReps(MetaOmProject myProject) {
        System.out.println("Finding replicates");

        columnMap = new TreeMap();
        sampleNameCount = new TreeMap();
        repGroups = new TreeMap();
        repGroupNames = new TreeMap();
        BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(), "Finding Replicates",
                "Searching for Replicates", 0L, myProject.getDataColumnCount(), true);

        new Thread() {


            @Override
			public void run() {

                throw new Error("Unresolved compilation problems: \n\troot cannot be resolved\n\troot cannot be resolved\n\tDuplicate local variable samples\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");

            }


        }.start();
        progress.setVisible(true);
        if (progress.isCanceled()) {
            columnMap = null;
            repGroups = null;
        }
    }


    public JPanel getRepPanel() {
        if (columnMap == null) return null;

        System.out.println("Samples: " + columnMap.size());
        JPanel result = new JPanel(new BorderLayout());

        Set<Integer> keys = repGroups.keySet();
        System.out.println("Keys: " + keys.size());
        int sampleCount = 0;
        for (Integer key : keys) {
            sampleCount += repGroups.get(key).size();
        }
        System.out.println("Grouped samples: " + sampleCount);
        Object[][] data = new Object[sampleCount][5];
        int index = 0;
        for (Integer key : keys) {
            ArrayList<Sample> samples = repGroups.get(key);
            for (Sample sample : samples) {
                data[index][COLUMN_COL] = sample.col;
                data[index][EXP_COL] = sample.expID;
                data[index][SAMPLE_COL] = sample.name;
                if (repGroupNames.containsKey(key)) {
                    data[index][GNAME_COL] = repGroupNames.get(key);
                }
                data[index][TREATMENT_COL] = key;
                index++;
            }
        }

        String[] headers = {"Column", "Experiment", "Sample", "Group Name", "Treatment"};
        final NoneditableTableModel model = new NoneditableTableModel(data, headers);
        model.setColumnEditable(3, true);
        TableSorter sorter = new TableSorter(model);
        StripedTable table = new StripedTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        result.add(new JScrollPane(table), "Center");

        model.addTableModelListener(new TableModelListener() {
            boolean update = true;

            @Override
			public void tableChanged(TableModelEvent e) {
                if (!update)
                    return;
                if (e.getColumn() == GNAME_COL) {
                    Integer treatment;
                    try {
                        treatment = Integer.valueOf(model.getValueAt(e.getFirstRow(), TREATMENT_COL) + "");
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        return;
                    }
                    String newName = model.getValueAt(e.getFirstRow(), e.getColumn()) + "";
                    repGroupNames.put(treatment, newName);
                    update = false;
                    for (int i = 0; i < model.getRowCount(); i++) {
                        try {
                            Integer thisTreatment = Integer.valueOf(model.getValueAt(i, TREATMENT_COL) + "");
                            if (thisTreatment.equals(treatment)) {
                                model.setValueAt(newName, i, GNAME_COL);
                                // table.setValueAt(newName, i, GNAME_COL);
                            } else {
                                // System.out.println(thisTreatment+"!="+treatment);
                            }
                        } catch (NumberFormatException nfe) {
                        }
                    }
                    update = true;
                }
            }
        });
        return result;
    }

    public static boolean areReps(String s1, String s2) {
        String n1 = s1.replaceAll("[\\s\\-\\=_:]|ÃƒÂ¯|ÌÄåø|Ã\\?Â\\?|Ì\\?å\\?", "").toLowerCase();
        n1 = Utils.removeExtendedChars(n1);
        String n2 = s2.replaceAll("[\\s\\-\\=_:]|ÃƒÂ¯|ÌÄåø|Ã\\?Â\\?|Ì\\?å\\?", "").toLowerCase();
        n2 = Utils.removeExtendedChars(n2);
        n1 = n1.replaceAll("ath1", "");
        n2 = n2.replaceAll("ath1", "");
        String diff = getStringDiff(n1, n2);
        Matcher m1 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n1);
        Matcher m2 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n2);
        boolean match = false;
        String name1;
        String name2;
        String num1;
        if ((m1.find()) && (m2.find())) {
            int end1 = n1.lastIndexOf("rep");
            int end2 = n2.lastIndexOf("rep");
            name1 = n1.substring(0, end1);
            name2 = n2.substring(0, end2);
            num1 = n1.substring(end1);
            String num2 = n2.substring(end2);
            String numDiff = getStringDiff(num1, num2);
            String nameDiff = getStringDiff(name1, name2);
            if (numDiff.length() > 0) {
                if (name1.equals(name2)) {
                    match = true;
                } else {
                    try {
                        Integer.parseInt(nameDiff);
                        match = true;
                        Integer[] diffs = getDiffLocations(n1, n2);
                        Integer[] arrayOfInteger1;
                        int j = (arrayOfInteger1 = diffs).length;
                        for (int i = 0; i < j; i++) {
                            int d = arrayOfInteger1[i].intValue();
                            if ((d + 1 < name1.length()) || (d + 1 < name2.length())) {

                                if (n1.length() >= n2.length()) {
                                    if ((n1.charAt(d + 1) == 'h') || (n1.charAt(d + 1) == 'd')) {
                                        match = false;
                                    }
                                } else if ((n2.charAt(d + 1) == 'h') || (n2.charAt(d + 1) == 'd')) {
                                    match = false;
                                }

                                if (d - 1 >= 0) {
                                    if ((!Character.isDigit(n1.charAt(d - 1))) || (!Character.isDigit(n2.charAt(d - 1))))
                                        match = false;
                                }
                            }
                        }
                    } catch (NumberFormatException localNumberFormatException) {
                    }
                }
            }
        } else if (n1.substring(0, n1.length() - 1).equals(n2.substring(0, n2.length() - 1))) {
            match = true;
            Integer[] diffs = getDiffLocations(n1, n2);
            for (int d : diffs) {
                if (d + 1 >= n1.length() && d + 1 >= n2.length()) continue;
                if (n1.length() >= n2.length()) {
                    if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') match = false;
                }
                else {
                    if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') match = false;
                }
            }
        } else if (diff.length() == 1) {
            try {
                Integer.parseInt(diff);
                match = true;
                Integer[] diffs = getDiffLocations(n1, n2);
                for (int d : diffs) {
                    if (d + 1 >= n1.length() && d + 1 >= n2.length()) continue;

                    if (n1.length() >= n2.length()) {
                        if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') match = false;
                    } else {
                        if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') match = false;
                    }
                }
            } catch (NumberFormatException nfe) {
            }
        }
        return match;
    }

    public static Integer[] getDiffLocations(String s1, String s2) {
        ArrayList<Integer> diffs = new ArrayList();

        for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diffs.add(Integer.valueOf(i));
            }
        }

        return diffs.toArray(new Integer[0]);
    }

    public static String getStringDiff(String s1, String s2) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                result.append(s2.charAt(i));
            } else if (result.length() != 0) {
                result.append(" ");
            }
        }
        if (s1.length() > s2.length()) {
            result.append(s1.substring(s2.length()));
        } else if (s2.length() > s1.length()) {
            result.append(s2.substring(s1.length()));
        }
        return result.toString().trim();
    }


    private ArrayList<Sample> getMissingSamples(MetaOmProject paramMetaOmProject) {
        throw new Error("Unresolved compilation problem: \n\tExtendedInfoTree cannot be resolved to a type\n");
    }

    public class RepAveragedData {
        public String[] repGroupNames;
        public double[] values;
        public double[] stdDevs;
        public int[] repCounts;

        public RepAveragedData(MetaOmProject myProject, int row) throws IOException {
            if (goodGroups == null) {
                goodGroups = repGroups;
            }
            repGroupNames = new String[goodGroups.size()];
            values = new double[goodGroups.size()];
            stdDevs = new double[goodGroups.size()];
            repCounts = new int[goodGroups.size()];

            double[] data = myProject.getAllData(row);
            Set<Integer> groups = goodGroups.keySet();
            int index = 0;
            double ave = 0.0D;
            double diffSum = 0.0D;
            for (Integer groupNumber : groups) {
                ArrayList<RepInfo.Sample> samples = goodGroups.get(groupNumber);

                repGroupNames[index] = RepInfo.makeGroupName(samples.get(0).name);
                ave = 0.0D;

                for (RepInfo.Sample s : samples) ave += data[s.col];

                repCounts[index] = samples.size();
                ave /= repCounts[index];
                values[index] = ave;

                diffSum = 0.0D;
                for (RepInfo.Sample s : samples) {
                    diffSum += (data[s.col] - ave) * (data[s.col] - ave);
                }
                diffSum /= repCounts[index];
                stdDevs[index] = Math.sqrt(diffSum);
                index++;
            }
        }
    }

    public RepAveragedData getRepAveragedData(MetaOmProject myProject, int row) throws IOException {
        return new RepAveragedData(myProject, row);
    }


    public void createMetadataFile(MetaOmProject paramMetaOmProject, File paramFile)
            throws IOException {
        throw new Error("Unresolved compilation problems: \n\tThe method getPathForCol(int) is undefined for the type Metadata\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tThe method getPathForCol(int) is undefined for the type Metadata\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");
    }


    public ArrayList<Sample> getGoodReps(MetaOmProject myProject, int group, double cutoff) throws IOException {
        ArrayList<Sample> members = new ArrayList();
        members.addAll(getRepGroupMembers(group));
        ArrayList<Sample[]> badReps = new ArrayList();

        for (int i = 0; i < members.size(); i++) {
            Sample s = members.get(i);
            if (isBad(s.col)) {
                members.remove(i);
                i--;
            }
        }

        if ((members.size() <= 1) || (cutoff <= -1.0D)) return members;

        double[][] data = new double[members.size()][myProject.getRowCount()];
        double[][] corrs = new double[members.size()][members.size()];

        for (int i = 0; i < data.length; i++) {
            System.out.print("Retrieving data for column " + members.get(i).col + ": ");
            System.out.flush();
            Utils.startWatch();
            data[i] = myProject.getDataForColumn(members.get(i).col);
            System.out.println(Utils.stopWatch() + "ms");
        }

        for (int i = 0; i < members.size() - 1; i++) {
            CorrelationCalc calcy = new CorrelationCalc(data[i]);
            for (int j = i + 1; j < members.size(); j++) {
                corrs[i][j] = calcy.pearsonCorrelation(data[j], myProject.mayContainBlankValues(), myProject.getBlankValue());
                corrs[j][i] = corrs[i][j];
                System.out.println("Correlation between " + members.get(i).name + " and " + members.get(i).name + ": " + corrs[i][j]);
                if (corrs[i][j] < cutoff) {
                    badReps.add(new Sample[]{members.get(i), members.get(j)});
                }
            }
        }
        if (badReps.size() > 0) {
            TreeSet<Sample> removed = new TreeSet();
            for (Sample[] badSamples : badReps)
                if ((!removed.contains(badSamples[0])) && (!removed.contains(badSamples[1]))) {

                    double[] aves = new double[2];
                    for (int i = 0; i < aves.length; i++) {
                        for (int j = 0; j < corrs.length; j++) {
                            if (j != members.indexOf(badSamples[i])) {
                                aves[i] += corrs[members.indexOf(badSamples[i])][j];
                            }
                        }
                        aves[i] /= (corrs.length - 1);
                    }
                    if (aves[0] < aves[1]) {
                        System.out.println("Removing " + badSamples[0].name);
                        members.remove(badSamples[0]);
                        removed.add(badSamples[0]);
                    } else {
                        System.out.println("Removing " + badSamples[1].name);
                        members.remove(badSamples[1]);
                        removed.add(badSamples[1]);
                    }
                }
        }
        return members;
    }

    @Override
	public void fromXML(Element source) {
        if (!"reps".equals(source.getName())) {
            throw new IllegalArgumentException("Not a replicate Element");
        }
        columnMap = new TreeMap();
        repGroups = new TreeMap();
        sampleNameCount = new TreeMap();
        repGroupNames = new TreeMap();
        List groups = source.getChildren();
        for (Object o : groups) {
            Element groupElement = (Element) o;
            Integer groupID = Integer.valueOf(groupElement.getAttributeValue("id"));
            String groupName = groupElement.getAttributeValue("name");
            ArrayList<Sample> memberList = new ArrayList();
            List members = groupElement.getChildren();
            for (Object o2 : members) {
                Element memberElement = (Element) o2;
                int col = Integer.parseInt(memberElement
                        .getAttributeValue("col"));
                String name = memberElement.getAttributeValue("name");
                String expID = memberElement.getAttributeValue("expid");
                Sample thisMember = new Sample(name, expID, col);
                memberList.add(thisMember);
                columnMap.put(Integer.valueOf(col), groupID);
                Integer count = sampleNameCount.get(name);
                if (count == null) {
                    count = Integer.valueOf(1);
                } else {
                    count = Integer.valueOf(count.intValue() + 1);
                }
                sampleNameCount.put(name, count);
                if ("true".equals(memberElement.getAttributeValue("isBad"))) {
                    setAsBad(col, true);
                }
            }
            repGroups.put(groupID, memberList);
            repGroupNames.put(groupID, groupName);
        }
    }

    public static String makeGroupName(String sampleName) {
        String result = sampleName.replaceAll("(?i)[_.]?replicate\\W*\\d+\\z|[_.]?rep\\W*\\d+\\z","");
        if (!result.equals(sampleName)) {
            return Utils.superClean(result);
        }
        Pattern regex = Pattern.compile(".*(?=\\W?\\d$)", 194);
        Matcher regexMatcher = regex.matcher(sampleName);
        regexMatcher.find();
        try {
            result = regexMatcher.group();
        }
        catch (Exception localException) { }

        if (result != null) return Utils.superClean(result);

        return Utils.superClean(sampleName);
    }

    public Collection<Integer> getValidGroups(MetaOmProject myProject, double cutoff) throws IOException {
        goodGroups = new TreeMap();
        Set<Integer> groupIDs = getRepGroupNumbers();
        for (Iterator localIterator = groupIDs.iterator(); localIterator.hasNext(); ) {
            int group = ((Integer) localIterator.next()).intValue();
            ArrayList<Sample> goodReps = getGoodReps(myProject, group, cutoff);
            if (goodReps.size() > 1) {
                goodGroups.put(Integer.valueOf(group), goodReps);
            }
        }
        return goodGroups.keySet();
    }

    public String[] getRepGroupNames(boolean removeDeleted) {
        Set<Integer> keys = repGroups.keySet();
        ArrayList<String> result = new ArrayList();
        for (Iterator localIterator1 = keys.iterator(); localIterator1.hasNext(); ) {
            int key = ((Integer) localIterator1.next()).intValue();
            if (removeDeleted) {
                int goodMembers = 0;
                ArrayList<Sample> members = getRepGroupMembers(key);
                for (Sample s : members) {
                    if (!isBad(s.col)) {
                        goodMembers++;
                    }
                }
                if (goodMembers > 1) {
                    result.add(repGroupNames.get(Integer.valueOf(key)));
                }
            } else {
                result.add(getRepGroupName(key));
            }
        }
        return result.toArray(new String[0]);
    }


    public String getExpID(int groupID) {
        if (repGroups.containsKey(groupID)) {
            return repGroups.get(groupID).get(0).expID;
        } else {
            throw new IllegalArgumentException("No such group: " + groupID);
        }
    }

    public String getSampleNames(int groupID) {
        if (repGroups.containsKey(groupID)) {
            ArrayList<Sample> samples = repGroups.get(groupID);
            StringBuilder result = new StringBuilder(samples.get(0).name);
            for (int i = 1; i < samples.size(); i++) {
                result.append(", " + samples.get(i).name);
            }
            return result.toString();
        } else {
            throw new IllegalArgumentException("No such group: " + groupID);
        }
    }

    @Override
	public Element toXML() {
        Element result = new Element("reps");
        Set<Integer> groups = repGroups.keySet();
        for (Integer group : groups) {
            Element groupElement = new Element("group").setAttribute("id",group + "");
            if ((repGroupNames.containsKey(group)) && (repGroupNames.get(group) != null)) {
                groupElement.setAttribute("name", repGroupNames.get(group));
            }
            ArrayList<Sample> members = repGroups.get(group);
            for (Sample member : members) {
                Element memberElement = new Element("member");
                memberElement.setAttribute("col", member.col + "");
                memberElement.setAttribute("name", member.name);
                memberElement.setAttribute("expid", member.expID);
                if ((badCols != null) && (badCols.contains(Integer.valueOf(member.col)))) {
                    memberElement.setAttribute("isBad", "true");
                }
                groupElement.addContent(memberElement);
            }
            result.addContent(groupElement);
        }
        return result;
    }

    public void loadFromFile(File source) throws IOException {
    }

    public Set<Integer> getRepGroupNumbers() {
        if (repGroups == null) return null;

        return repGroups.keySet();
    }

    public ArrayList<Sample> getRepGroupMembers(int group) {
        if (repGroups == null) return null;

        if (repGroups.get(Integer.valueOf(group)) == null) {
            System.out.println("Null group: " + group);
            return null;
        }
        ArrayList<Sample> result = repGroups.get(Integer.valueOf(group));
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) == null) {
                result.remove(i);
            }
        }
        return result;
    }

    public String getRepGroupName(int group) {
        if (repGroupNames == null) return null;

        return repGroupNames.get(Integer.valueOf(group));
    }

    public void setRepGroupName(int group, String newName) {
        if (repGroupNames == null) repGroupNames = new TreeMap();

        String oldName = repGroupNames.get(Integer.valueOf(group));
        if (((newName == null) && (oldName != null)) || (!newName.equals(oldName))) {
            repGroupNames.put(Integer.valueOf(group), newName);
            fireStateChanged(null);
        }
    }

    public int oldgetFirstAvailableGroupNumber() {
        Set<Integer> groups = getRepGroupNumbers();
        int result = 1;
        for (Integer group : groups) {
            if (group.intValue() != result) break;
            result++;
        }

        return result;
    }

    public int getFirstAvailableGroupNumber() {
        int result = 1;
        while (repGroups.containsKey(Integer.valueOf(result))) {
            result++;
        }
        return result;
    }

    public int getLastAvailableGroupNumber() {
        Set<Integer> keys = repGroups.keySet();
        int result = -1;
        for (Integer key : keys) {
            result = key.intValue();
        }
        return result + 1;
    }

    public Sample removeMember(int column, int group) {
        ArrayList<Sample> samples = getRepGroupMembers(group);
        if (samples == null) return null;

        for (int i = 0; i < samples.size(); i++) {
            Sample s = samples.get(i);
            if (s.col == column) {
                samples.remove(s);
                if (samples.size() == 0) {
                    repGroups.remove(Integer.valueOf(group));
                    repGroupNames.remove(Integer.valueOf(group));
                }
                columnMap.remove(Integer.valueOf(column));
                fireStateChanged(null);
                return s;
            }
        }
        return null;
    }


    public void addToGroup(int group, Sample newSample) {
        ArrayList<Sample> addMe = new ArrayList();
        addMe.add(newSample);
        addToGroup(group, addMe);
    }

    public void addToGroup(int group, ArrayList<Sample> newSamples) {
        if (newSamples.size() <= 0) {
            return;
        }

        ArrayList<Sample> samples = repGroups.get(Integer.valueOf(group));
        if (samples == null) {
            samples = new ArrayList();
            repGroupNames.put(Integer.valueOf(group), makeGroupName(newSamples.iterator().next().name));
        }
        samples.addAll(newSamples);
        repGroups.put(Integer.valueOf(group), samples);
        for (int i = 0; i < newSamples.size(); i++) {
            Sample s = newSamples.get(i);

            columnMap.put(Integer.valueOf(s.col), Integer.valueOf(group));
        }
        fireStateChanged(null);
    }

    public Integer getGroupForCol(int col) {
        if (columnMap != null) return columnMap.get(Integer.valueOf(col));

        return null;
    }

    public void setAsBad(int col, boolean bad) {
        if (badCols == null) {
            if (!bad) return;

            badCols = new TreeSet();
        }
        if (bad) {
            if (!badCols.contains(Integer.valueOf(col))) {
                fireStateChanged(null);
                badCols.add(Integer.valueOf(col));
            }
        } else if (badCols.contains(Integer.valueOf(col))) {
            fireStateChanged(null);
            badCols.remove(Integer.valueOf(col));
        }
    }

    public boolean isBad(int col) {
        if (badCols == null) return false;

        return badCols.contains(Integer.valueOf(col));
    }

    public void sort() {
        int groupCount = repGroups.size();

        ArrayList<Integer> cols = new ArrayList();
        cols.addAll(columnMap.keySet());
        int currentGroup = 1;

        Integer moveTo;
        while (!cols.isEmpty()) {
            Integer col = cols.get(0);

            Integer group = getGroupForCol(col.intValue());
            String name = getRepGroupName(group.intValue());

            ArrayList<Sample> members = getRepGroupMembers(group);
            if (members == null) {
                System.out.println("What the hell?  null group for column " + col + ", group " + group);
                cols.remove(col);
            } else {
                System.out.println("Column " + col + ": Moving " + members.size() + " members of group " + group + " to group " + currentGroup);
                if (group.intValue() == currentGroup) {

                    System.out.println("No need!");
                    for (Sample s : members) {
                        cols.remove(Integer.valueOf(col));
                    }
                    currentGroup++;
                } else {
                    ArrayList<Sample> oldMembers = getRepGroupMembers(currentGroup);
                    if (oldMembers != null) {
                        String oldName = getRepGroupName(currentGroup);
                        moveTo = Integer.valueOf(getLastAvailableGroupNumber());

                        System.out.println("Moving " + oldMembers.size() + " members from group " + currentGroup + " to " + moveTo);
                        addToGroup(moveTo.intValue(), oldMembers);
                        setRepGroupName(moveTo.intValue(), oldName);
                    }

                    repGroups.remove(Integer.valueOf(currentGroup));
                    repGroupNames.remove(Integer.valueOf(currentGroup));
                    if (repGroups.containsKey(Integer.valueOf(currentGroup))) {
                        System.out.println(currentGroup + " still exists!");
                    }

                    addToGroup(currentGroup, members);
                    setRepGroupName(currentGroup, name);
                    currentGroup++;

                    for (Sample s : members) {
                        System.out.print("Removing " + col + " ");
                        System.out.println(cols.remove(Integer.valueOf(col)));
                    }
                }
            }
        }
        int maxGroup = getLastAvailableGroupNumber();
        for (int i = groupCount + 1; i < maxGroup; i++) {
            repGroups.remove(Integer.valueOf(i));
            repGroupNames.remove(Integer.valueOf(i));
        }
        Set<Integer> groups = repGroups.keySet();
        for (int group : groups) {
            ArrayList<Sample> members = repGroups.get(group);
            for (Sample s : members) {
                columnMap.put(s.col, group);
            }
        }
    }

    public void addChangeListener(ChangeListener listener) {
        System.out.println("Adding change listener");
        if (listeners == null) {
            listeners = new Vector();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        } else {
            System.out.println("Trying to add duplicate change listener");
        }
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void fireStateChanged(ChangeEvent event) {
        if (listeners == null) {
            return;
        }
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

    public TreeMap<Integer, TreeSet<Sample>> getDeepReps(MetaOmProject paramMetaOmProject, String paramString) throws IOException {
        throw new Error("Unresolved compilation problems: \n\tThe method getRoot() is undefined for the type Metadata\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");
    }


    public double[][] getGroupCorrelations(MetaOmProject paramMetaOmProject, int paramInt) {
        throw new Error("Unresolved compilation problem: \n\tThe method getReps() is undefined for the type MetaOmProject\n");
    }
}
