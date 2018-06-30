package edu.iastate.metnet.metaomgraph;

import edu.iastate.metnet.metaomgraph.utils.MetNetUtils;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;


public class AtGeneSearch {
    private static final int TAIR_ANNOTATION = 0;
    private static final int PREDICTED_SUBCELLLOC = 1;
    private static final int METNET_SUBCELLLOC = 2;
    private static final int GO_CELLULAR_COMPONENT = 3;
    private static final int GO_MOLECULAR_FUNCTION = 4;
    private static final int GO_BIOLOGICAL_PROCESS = 5;
    private static final int SWISS_PROT = 6;
    private static final int PATHWAY = 7;
    private static final int LOCUS_ID = 8;
    private static final int COMMENT = 9;
    private static final int AFFY25K_ID = 10;
    private static final int AFFY8K_ID = 11;
    private static final int AFGC_ID = 12;
    private static final int GENE_NAME = 13;
    public static final int COLUMN_COUNT = 14;

    public AtGeneSearch() {
    }

    public static String[] getHeaders() {
        String result[] = new String[14];
        result[TAIR_ANNOTATION] = "TAIR Annotation";
        result[PREDICTED_SUBCELLLOC] = "TargetP Predicted SubcellLoc";
        result[METNET_SUBCELLLOC] = "MetNet SubcellLoc";
        result[GO_CELLULAR_COMPONENT] = "GO Cellular Component";
        result[GO_MOLECULAR_FUNCTION] = "GO Molecular Function";
        result[GO_BIOLOGICAL_PROCESS] = "GO Biological Process";
        result[SWISS_PROT] = "Swiss PROT";
        result[PATHWAY] = "Pathways";
        result[LOCUS_ID] = "Locus ID";
        result[COMMENT] = "Comments";
        result[AFFY25K_ID] = "Affy25k ID";
        result[AFFY8K_ID] = "Affy8k ID";
        result[AFGC_ID] = "AFGC ID";
        result[GENE_NAME] = "Gene Name";
        return result;
    }

    public static String[] doQuery(String searchMe) throws SQLException {
        long startTime = Calendar.getInstance().getTimeInMillis();
        Statement statement = MetNetUtils.connectToMetNetDB();

        String locusID;

        if (searchMe.indexOf("\"") >= 0) {
            System.out.println("Doing a phrase search on " + searchMe);
            String sql = "select distinct locus_id from tair_sequenced_genes where match (description) against ("
                    + searchMe
                    + " in boolean mode) union select locus_id from tair_ath_go_goslim where match (go_term) against ("
                    + searchMe + " in boolean mode)";
            ResultSet rs = statement.executeQuery(sql);
            if (!rs.first())   return null;
            locusID = rs.getString("locus_id");
            rs.next();
            for (; !rs.isAfterLast(); rs.next())
                locusID = locusID + ";" + rs.getString("locus_id");
        } else { //String locusID;
            if (Utils.getIDType(searchMe) == Utils.LOCUS) {
                locusID = searchMe;
            } else { //String sql;
                String sql;
                if (Utils.getIDType(searchMe) == Utils.AFFY8K) {
                    sql = "select locus_id from tair_affy8k_array_elements where array_element_name='" + searchMe + "'";
                } else { //String sql;
                    if (Utils.getIDType(searchMe) == Utils.AFFY25K) {
                        sql = "select locus_id from tair_affy25k_array_elements where array_element_name='" + searchMe + "'";
                    } else
                        sql = "select locus_id from tair_afgc_array_elements where array_element_name='" + searchMe + "'";
                }
                ResultSet rs = statement.executeQuery(sql);
                if (!rs.first()) return null;
                locusID = rs.getString("locus_id");
            }
        }
        String locusIDQueryString;
        if (locusID.indexOf(";") >= 0) {
            StringTokenizer st = new StringTokenizer(locusID, ";");
            for (locusIDQueryString = "where locus_id like '%" + st.nextToken() + "%'"; st.hasMoreTokens(); ) {
                locusIDQueryString = locusIDQueryString + " or locus_id like '%" + st.nextToken() + "%'";
            }
        } else {
            locusIDQueryString = "where locus_id like '%" + locusID + "%'";
        }
        String sql = "select array_element_name, description, locus_id from tair_affy25k_array_elements "
                +  locusIDQueryString
                + " union "
                + "select array_element_name, description, locus_id from tair_affy8k_array_elements "
                + locusIDQueryString
                +  " union "
                + "select array_element_name, description, locus_id from tair_afgc_array_elements "
                + locusIDQueryString;

        ResultSet rs = statement.executeQuery(sql);
        if (!rs.first()) return null;

        String result[] = getHeaders();
        String ids[] = locusID.split(";");
        result[LOCUS_ID] = ids[0];
        for (int i = 1; i < ids.length; i++) result[LOCUS_ID] += "\n\n" + ids[i];

        result[TAIR_ANNOTATION] = rs.getString("description");
        String affy25kIDs = null;
        String affy8kIDs = null;
        String afgcIDs = null;
        for (; !rs.isAfterLast(); rs.next()) {
            String thisID = rs.getString("array_element_name");
            if (Utils.getIDType(thisID) == Utils.AFFY25K) {
                if (affy25kIDs == null) {
                    affy25kIDs = thisID;
                } else
                    affy25kIDs = affy25kIDs + ", " + thisID;
            } else if (Utils.getIDType(thisID) == Utils.AFFY8K) {
                if (affy8kIDs == null) {
                    affy8kIDs = thisID;
                } else
                    affy8kIDs = affy8kIDs + ", " + thisID;
            } else if (afgcIDs == null) {
                afgcIDs = thisID;
            } else {
                afgcIDs = afgcIDs + ", " + thisID;
            }
        }
        result[AFFY25K_ID] = affy25kIDs;
        result[AFFY8K_ID] = affy8kIDs;
        result[AFGC_ID] = afgcIDs;
        sql = "SELECT description, if (gene_name not like concat('%',locus_id,'%'), gene_name, '') as gene_name FROM tair_sequenced_genes "
                + locusIDQueryString + " group by description, gene_name";

        rs = statement.executeQuery(sql);
        if (rs.first()) {
            String description = rs.getString("description");
            String geneName = "";

            if (rs.getString("gene_name") != "")  geneName = rs.getString("gene_name");
            rs.next();

            for (; !rs.isAfterLast(); rs.next()) {
                description = description + "; " + rs.getString("description");
                if (rs.getString("gene_name") != "") {
                    if (geneName == "") {
                        geneName = rs.getString("gene_name");
                    } else
                        geneName = geneName + "\n\n" + rs.getString("gene_name");
                }
            }
            result[TAIR_ANNOTATION] = description;
            result[GENE_NAME] = geneName;
        }
        sql = "select pathway_name from tair_aracyc_dump " + locusIDQueryString + " group by pathway_name";
        rs = statement.executeQuery(sql);
        String pathways = "";
        if (rs.first()) {
            pathways = rs.getString("pathway_name");
            rs.next();
            for (; !rs.isAfterLast(); rs.next()) {
                pathways = pathways + "\n\n" + rs.getString("pathway_name");
            }
        }
        result[PATHWAY] = pathways;
        sql = "select go_term, aspect from tair_ath_go_goslim " + locusIDQueryString + " group by go_term, aspect";

        rs = statement.executeQuery(sql);
        String gocomp = null;
        String gofunc = null;
        String goproc = null;
        if (rs.first()) {
            for (; !rs.isAfterLast(); rs.next()) {
                String thisAspect = rs.getString("aspect");
                if ("comp".equals(thisAspect)) {
                    if (gocomp == null) {
                        gocomp = rs.getString("go_term");
                    } else
                        gocomp = gocomp + "\n\n" + rs.getString("go_term");
                } else if ("func".equals(thisAspect)) {
                    if (gofunc == null) {
                        gofunc = rs.getString("go_term");
                    }
                    else gofunc = gofunc + "\n\n" + rs.getString("go_term");
                }
                else if ("proc".equals(thisAspect))
                    if (goproc == null) {
                        goproc = rs.getString("go_term");
                    } else
                        goproc = goproc + "\n\n" + rs.getString("go_term");
            }
        }
        result[GO_BIOLOGICAL_PROCESS] = goproc;
        result[GO_CELLULAR_COMPONENT] = gocomp;
        result[GO_MOLECULAR_FUNCTION] = gofunc;
        sql = "select target_location from tair_targetp_analysis " + locusIDQueryString;
        rs = statement.executeQuery(sql);
        String location;
        if (rs.first()) {
            location = rs.getString("target_location");
            if ("C".equals(location)) {
                location = "Chloroplast";
            } else if ("M".equals(location)) {
                location = "Mitochondria";
            } else if ("S".equals(location)) {
                location = "Secretory pathway";
            } else
                location = "unknown";
        } else {
            location = "undefined";
        }
        result[PREDICTED_SUBCELLLOC] = location;
        sql = "select uniprot_id from tair_agi_to_uniprot " + locusIDQueryString;
        rs = statement.executeQuery(sql);
        String uniprot = null;
        if (rs.first()) {
            for (; !rs.isAfterLast(); rs.next())
                if (uniprot == null) {
                    uniprot = rs.getString("uniprot_id");
                } else
                    uniprot = uniprot + ", " + rs.getString("uniprot_id");
        }
        result[SWISS_PROT] = uniprot;
        sql = "select blockid from blockunit " + locusIDQueryString.replaceAll("locus_id", "name");

        rs = statement.executeQuery(sql);
        if (rs.first()) {
            String blockIDQueryString = makeQueryString(rs, "blockid","blockid");
            sql = "select entityid from entitywithcontext " + blockIDQueryString;
            rs = statement.executeQuery(sql);
            String entityIDQueryString;
            if (rs.first()) {
                entityIDQueryString = makeQueryString(rs, "entityid", "entityid");
            } else {
                entityIDQueryString = "where entityid=null";
            }
            sql = "select blockid from interactionparts " + blockIDQueryString.replaceAll("blockid", "part");

            rs = statement.executeQuery(sql);
            if (rs.first()) {
                String interactionQueryString = makeQueryString(rs, "blockid","part");
                sql = "select blockid from pathwayparts " + interactionQueryString;
                rs = statement.executeQuery(sql);
                if (rs.first()) {
                    String pathwayQueryString = makeQueryString(rs, "blockid","blockid");
                    sql = "select name from blockunit " + pathwayQueryString;
                    rs = statement.executeQuery(sql);
                    if (rs.first()) {
                        String metnetPathways = "";
                        for (; !rs.isAfterLast(); rs.next()) {
                            if (result[PATHWAY].indexOf(rs.getString("name")) < 0) {
                                if ((!result[PATHWAY].equals("")) || (!metnetPathways.equals("")))
                                    metnetPathways = metnetPathways + "\n\n";

                                metnetPathways = metnetPathways + rs.getString("name");
                            }
                            result[PATHWAY] += metnetPathways;
                        }
                    }
                }
            }
            sql = "SELECT blockunit.name, location, entity.type FROM blockunit left join entitywithcontext on entitywithcontext.blockid=blockunit.blockid left join entity on entity.entityid=entitywithcontext.entityid " + blockIDQueryString.replaceAll("blockid", "blockunit.blockid") + " and location!='unknown' and entity.type!='gene' and entity.type!='rna' and location!='cytosol' and location!='nucleus' and location is not null";
            rs = statement.executeQuery(sql);

            if (!rs.first()) {
                sql = "SELECT blockunit.name, location, entity.type FROM blockunit left join entitywithcontext on entitywithcontext.blockid=blockunit.blockid left join entity on entity.entityid=entitywithcontext.entityid " + blockIDQueryString.replaceAll("blockid", "blockunit.blockid") + " and location!='unknown' and entity.type!='gene' and entity.type!='rna' and location is not null";
                rs = statement.executeQuery(sql);
            }
            if (rs.first()) {
                Vector<String> pc = new Vector();
                Vector<String> poly = new Vector();
                String metnetLocation = "";
                for (; !rs.isAfterLast(); rs.next()) {
                    if ("protein complex".equals(rs.getString("type"))) {
                        pc.add(rs.getString("location"));
                    } else if ("polypeptide".equals(rs.getString("type"))) {
                        poly.add(rs.getString("location"));
                    }
                }
                if (!pc.isEmpty()) {
                    for (int x = 0; x < pc.size(); x++) {
                        String loc = pc.get(x);
                        if (metnetLocation == "") {
                            metnetLocation = loc;
                        } else {
                            metnetLocation = metnetLocation + "\n\n" + loc;
                        }
                    }
                } else if (!poly.isEmpty()) {
                    for (int x = 0; x < poly.size(); x++) {
                        String loc = poly.get(x);
                        if (metnetLocation == "") {
                            metnetLocation = loc;
                        } else {
                            metnetLocation = metnetLocation + "\n\n" + loc;
                        }
                    }
                }
                result[METNET_SUBCELLLOC] = metnetLocation;
            }

            sql = "select comment, user.firstname, user.lastname from entitycomment left join user on user.username=entitycomment.username " + entityIDQueryString + " union select comment, user.firstname, user.lastname from blockunitcomment left join user on user.username=blockunitcomment.username " + blockIDQueryString;
            rs = statement.executeQuery(sql);

            String comments = "";
            if (rs.first()) {
                if (!"tair".equals(rs.getString("firstname"))) {
                    comments = rs.getString("comment");
                    if (rs.getString("firstname") != null) {
                        comments = comments + " (" + rs.getString("firstname");
                        if (rs.getString("lastname") != null)
                            comments = comments + " " + rs.getString("lastname");
                        comments = comments + ")";
                    }
                }
                rs.next();
                for (; !rs.isAfterLast(); rs.next()) {
                    if (!"tair".equals(rs.getString("firstname"))) {
                        if (comments != "") comments = comments + "\n\n";
                        comments = comments + rs.getString("comment");
                        if (rs.getString("firstname") != null) {
                            comments = comments + " (" + rs.getString("firstname");
                            if (rs.getString("lastname") != null)
                                comments = comments + " " + rs.getString("lastname");
                            comments = comments + ")";
                        }
                    }
                }
                result[COMMENT] = comments;
            }
        }

        System.out.println("Query took " + (  Calendar.getInstance().getTimeInMillis() - startTime) + "ms");
        return result;
    }


    private static String makeQueryString(ResultSet rs, String column, String name)
            throws SQLException {
        if (!rs.first()) return "";
        String result = "where (" + name + "='" + rs.getString(column) + "'";
        rs.next();
        for (; !rs.isAfterLast(); rs.next()) {
            result = result + " or " + name + "='" + rs.getString(column) + "'";
        }
        result = result + ")";
        return result;
    }
}
