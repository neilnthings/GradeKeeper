/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guessNumber;

import guessNumber.model.ClassWork;
import guessNumber.model.CourseInfo;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author neil
 */
@Named(value = "gradeFunctions")
@SessionScoped
public class gradeFunctions implements Serializable {

    String courseNameInput;
    String newClass;
    String workNameInput;
    float earnedPointsInput;
    float maxPointsInput;
    private static Statement selectStmt;
    private static ResultSet rs;
    private static Connection conn;

    /**
     * Creates a new instance of UserNumberBean
     *
     * @throws java.sql.SQLException
     */
    public gradeFunctions() throws SQLException {
        conn = openConn("ORCL", "system", "1234surf", "localhost", "1521");
    }

    public String getCourseNameInput() {
        return courseNameInput;
    }

    public void setCourseNameInput(String courseNameInput) {
        this.courseNameInput = courseNameInput;
    }

    public String getNewClass() {
        return newClass;
    }

    public void setNewClass(String newClass) {
        this.newClass = newClass;
    }

    public String getWorkNameInput() {
        return workNameInput;
    }

    public void setWorkNameInput(String workNameInput) {
        this.workNameInput = workNameInput;
    }

    public float getEarnedPointsInput() {
        return earnedPointsInput;
    }

    public void setEarnedPointsInput(float earnedPointsInput) {
        this.earnedPointsInput = earnedPointsInput;
    }

    public float getMaxPointsInput() {
        return maxPointsInput;
    }

    public void setMaxPointsInput(float maxPointsInput) {
        this.maxPointsInput = maxPointsInput;
    }

    /**
     * Get Database connection using Database name, username, password, machine
     * name and port number
     *
     * @param db
     * @param userName
     * @param passwd
     * @param host
     * @param port
     * @return
     * @throws java.sql.SQLException
     *
     */
    public static Connection openConn(String db, String userName, String passwd, String host, String port) throws SQLException {
        conn = null;

        OracleDataSource ds;
        ds = new OracleDataSource();
        /**
         * Create Database URL and establish DB Connection *
         */
        String databaseURL = "jdbc:oracle:thin:@" + host + ":" + port;
        ds.setURL(databaseURL);
        conn = ds.getConnection(userName, passwd);
        /**
         * print any error messages *
         */
        if (conn == null) {
            System.out.println("Connection Failed \n");
        } else {
            System.out.println("Connection Successful \n");
        }

        return conn;
    }

    /**
     * Close connections
     */
    public static void closeConn() {
        try {
            conn.close();
            /*poStmt.close();*/
        } catch (SQLException sqle) {
            System.out.println("Error Msg: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("SQLError: " + sqle.getErrorCode());
            System.out.println("Rollback the transaction and quit the program");
            System.out.println();
            try {
                conn.rollback();
            } catch (SQLException e) {

            }
            System.exit(1);
        }
    }

    public String getCourseInfo() {
        return courseNameInput;
    }

    /**
     * Creates a list of Courses
     *
     * @return
     * @throws SQLException
     */
    public List<CourseInfo> getCourseList() throws SQLException {
        String query = "select class_name from courses order by class_name";

        selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);

        //get customer data from database
        ResultSet result = selectStmt.executeQuery(query);

        List<CourseInfo> list = new ArrayList<CourseInfo>();

        while (result.next()) {
            CourseInfo dbinfo = new CourseInfo();

            dbinfo.setCourseName(result.getString(1));

            //store all data into a List
            list.add(dbinfo);
        }

        return list;
    }

    /**
     * Creates a list of Class Work Information
     *
     * @return
     * @throws SQLException
     */
    public List<ClassWork> getClassWorkList() throws SQLException {
        String query = "select hw_name, earned_points, max_points from " + courseNameInput + " order by hw_name";

        selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);

        //get customer data from database
        ResultSet result = selectStmt.executeQuery(query);

        List<ClassWork> list = new ArrayList<ClassWork>();

        while (result.next()) {
            ClassWork dbinfo = new ClassWork();

            dbinfo.setWorkName(result.getString(1));
            dbinfo.setEarnedPoints(result.getFloat(2));
            dbinfo.setMaxPoints(result.getFloat(3));

            //store all data into a List
            list.add(dbinfo);
        }

        return list;
    }

    public void insertClassWork() throws SQLException {
        String insertQuery = "insert into " + courseNameInput + " values ('"
                + workNameInput + "', " + earnedPointsInput + ", " + maxPointsInput + ")";
        
        System.out.println(insertQuery);
        
        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

            rs = selectStmt.executeQuery(insertQuery);

            rs.close();
        } catch (SQLException sqle) {
            System.out.println("Error Msg: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("SQLError: " + sqle.getErrorCode());
            System.out.println("Rollback the transaction and quit the program");
            System.out.println();
            try {
                conn.setAutoCommit(false);
            } catch (java.sql.SQLException e) {
                System.exit(-1);
            }
            try {
                conn.rollback();
            } catch (SQLException e) {

            }
            System.exit(1);
        }
    }
}