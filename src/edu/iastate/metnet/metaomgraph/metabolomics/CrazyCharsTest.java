package edu.iastate.metnet.metaomgraph.metabolomics;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gjt.mm.mysql.Driver;

public class CrazyCharsTest {
    public CrazyCharsTest() {
    }

    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException {
        Driver.class.newInstance();
        String connString = "jdbc:mysql://localhost/sandbox_pbais05";
        Connection connection = null;
        Statement statement = null;
        connection = DriverManager.getConnection(connString, "guest", "");
        statement = connection.createStatement();
        String sql = "select normal from testtable";
        ResultSet rs = statement.executeQuery(sql);
        rs.first();
        while (!rs.isAfterLast()) {
            System.out.println(rs.getString("normal"));
            rs.next();
        }
    }
}
