/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QuizSystem.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author trant
 */
public class XJdbc {

    private static final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String url = "jdbc:sqlserver://172.16.84.215:1433;databaseName=QuizDB;encrypt=true;trustServerCertificate=true";
    private static final String username = "sa";
    private static final String password = "123";

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException();
        }
    }

    public static PreparedStatement getStatement(String sql, Object... rest) throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement pre = null;
        if (sql.trim().startsWith("{")) {
            pre = conn.prepareCall(sql);
        } else {
            pre = conn.prepareStatement(sql);
        }

        for (int i = 0; i < rest.length; i++) {
            pre.setObject(i + 1, rest[i]);
        }

        return pre;
    }

    public static void update(String sql, Object... rest) throws SQLException {
        PreparedStatement pre = getStatement(sql, rest);
        pre.executeUpdate();
    }

    public static ResultSet query(String sql, Object... rest) throws SQLException {
        ResultSet rs = getStatement(sql, rest).executeQuery();
        return rs;
    }
}
