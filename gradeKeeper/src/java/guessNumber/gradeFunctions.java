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
import java.text.DecimalFormat;
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
        conn = openConn("ORCL", "system", "password", "localhost", "1521");
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

    public float getGrade(String courseName) throws SQLException {
        String query = "select 100 * (sum(c.earned_points) / sum(c.max_points)) from " + courseName + " c";

        System.out.println(query);

        selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);

        //get customer data from database
        ResultSet result = selectStmt.executeQuery(query);
        
        float gradeNumber;
        String convertedFloat;
        String TWO_SPOTS = "##.00";
        DecimalFormat decimalFormat = new DecimalFormat(TWO_SPOTS);

        while (result.next()) {
            gradeNumber = result.getFloat(1);
            convertedFloat = decimalFormat.format(gradeNumber);
            gradeNumber = Float.parseFloat(convertedFloat);
            return gradeNumber;
        }
        
        result.close();

        return 0;
    }

    public void insertClassWork() throws SQLException {
        String insertQuery = "insert into " + courseNameInput + " values ('"
                + courseNameInput + "', '" + workNameInput + "', " + earnedPointsInput + ", " + maxPointsInput + ")";

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

    public void deleteClassWork() throws SQLException {
        String deleteQuery = "delete from " + courseNameInput + " where hw_name = '" + workNameInput + "'";

        System.out.println(deleteQuery);

        try {
            selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

            rs = selectStmt.executeQuery(deleteQuery);

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

    public void createNewClass() throws SQLException {
        String insertQuery = "insert into courses values ('" + newClass + "')";
        String createQuery = "create table " + newClass + " (class_name varchar(25), hw_name varchar(50), earned_points number, max_points number, foreign key (class_name) references courses(class_name))";

        System.out.println(insertQuery);
        System.out.println(createQuery);

        if (!newClass.isEmpty()) {

            try {
                selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);

                rs = selectStmt.executeQuery(insertQuery);
                rs = selectStmt.executeQuery(createQuery);

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

    public void deleteClass() throws SQLException {
        String dropQuery = "drop table " + courseNameInput;
        String deleteQuery = "delete from courses where class_name = '" + courseNameInput + "'";

        System.out.println(dropQuery);
        System.out.println(deleteQuery);

        if (!courseNameInput.isEmpty()) {

            try {
                selectStmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);

                rs = selectStmt.executeQuery(dropQuery);
                rs = selectStmt.executeQuery(deleteQuery);

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
}
