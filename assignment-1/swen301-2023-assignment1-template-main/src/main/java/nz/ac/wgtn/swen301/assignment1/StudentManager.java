package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * A student manager providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    private static Connection connection;
    private static String connectionURL = "jdbc:derby:memory:studentdb";
    private static String fetch_student_query = "SELECT * FROM STUDENTS WHERE id = ?";
    private static String fetch_degree_query = "SELECT * FROM DEGREES WHERE id = ?";
    private static String remove_query = "DELETE FROM STUDENTS WHERE ID = ?";
    private static String allID_query = "SELECT * FROM STUDENTS";
    private static String update_query = "UPDATE STUDENTS SET NAME = ?, FIRST_NAME = ?, DEGREE = ? WHERE ID = ?";
    private static String newStudent_query = "INSERT INTO STUDENTS (id, name, first_name, degree) VALUES (?, ?, ?, ?)";

    static PreparedStatement student_preparedStatement;
    static PreparedStatement degree_preparedStatement;
    static PreparedStatement remove_preparedStatement;
    static PreparedStatement allID_preparedStatement;
    static PreparedStatement update_PreparedStatement;
    static PreparedStatement new_student_PreparedStatement;

    private static HashMap<String, Student> cache = new HashMap<>();
    private static HashMap<String, Degree> degree_cache = new HashMap<>();
    
    static {
        StudentDB.init();
        try { 
            makeConnection(connectionURL);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    // DO NOT REMOVE BLOCK ENDS HERE



    private static void makeConnection(String connectionURL) throws SQLException {
        try { 
            if(connection == null){
                connection = DriverManager.getConnection(connectionURL);
            }
            student_preparedStatement = connection.prepareStatement(fetch_student_query);
            degree_preparedStatement = connection.prepareStatement(fetch_degree_query);
            remove_preparedStatement = connection.prepareStatement(remove_query);
            allID_preparedStatement = connection.prepareStatement(allID_query);
            update_PreparedStatement = connection.prepareStatement(update_query);
            new_student_PreparedStatement = connection.prepareStatement(newStudent_query);
        } catch (SQLException e) {  
            e.printStackTrace();
        }
    }

    static void reset() {
        degree_cache.clear();
        cache.clear();
        try { 
            student_preparedStatement.close();
            degree_preparedStatement.close();
            remove_preparedStatement.close();
            allID_preparedStatement.close();
            update_PreparedStatement.close();
            new_student_PreparedStatement.close();
            connection.close();
        } catch (SQLException e) {e.printStackTrace();}
        connection = null;
    }


    // THE FOLLOWING METHODS MUST BE IMPLEMENTED :

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student fetchStudent(String id) throws NoSuchRecordException {
        if (cache.containsKey(id)) { return cache.get(id); }
        try {
            makeConnection(connectionURL);
            student_preparedStatement.setString(1, id);
            ResultSet res = student_preparedStatement.executeQuery();

            if (res.next()) {
                String student_id = res.getString("id");
                String student_first_name = res.getString("first_name");
                String student_name = res.getString("name");
                String student_degree = res.getString("degree");
                Degree degree = fetchDegree(student_degree);
                Student student = new Student(student_id, student_name, student_first_name, degree);
                cache.put(student_id, student);
                return student;
            }
        } catch (SQLException e) {e.printStackTrace();}
        throw new NoSuchRecordException();
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree fetchDegree(String id) throws NoSuchRecordException {
        if (degree_cache.containsKey(id)) { return degree_cache.get(id); }
        try {
            makeConnection(connectionURL);
            degree_preparedStatement.setString(1, id);
            ResultSet res = degree_preparedStatement.executeQuery();
            Degree degree;

            if (res.next()) {
                String deg_id = res.getString("id");
                String deg_name = res.getString("name");
                degree = new Degree(String.valueOf(deg_id), deg_name);
                degree_cache.put(deg_id, degree);
                return degree;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        throw new NoSuchRecordException();
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {
        try {
            makeConnection(connectionURL);
            if (student != null) {
                if (!student.getId().isBlank()) {
                    remove_preparedStatement.setString(1, student.getId()); 
                }
                remove_preparedStatement.executeUpdate();
                cache.remove(student.getId());
            } else { throw new NoSuchRecordException(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        try {
            makeConnection(connectionURL);
            if (student != null) {
                //fetchStudent(student.getId());
                update_PreparedStatement.setString(1, student.getName());
                update_PreparedStatement.setString(2, student.getFirstName());
                update_PreparedStatement.setString(3, student.getDegree().getId());
                update_PreparedStatement.setString(4, student.getId());
                update_PreparedStatement.executeUpdate();
                cache.put(student.getId(), student);
            } else { throw new NoSuchRecordException(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name, String firstName, Degree degree) {
        Student stud = new Student();
        try {
            makeConnection(connectionURL);
            ArrayList<String> allIDs_strings = new ArrayList<>();
            for (String s : fetchAllStudentIds()) {
                allIDs_strings.add(s);
            }
            String endID = allIDs_strings.get(allIDs_strings.size() - 1);
            int number = Integer.parseInt(endID.substring(2));
            String newID = "id" + (number + 1);
            stud = new Student(newID, name, firstName, degree);

            new_student_PreparedStatement.setString(1, newID);
            new_student_PreparedStatement.setString(2, name);
            new_student_PreparedStatement.setString(3, firstName);
            new_student_PreparedStatement.setString(4, degree.getId());
            new_student_PreparedStatement.executeUpdate();
            cache.put(newID, stud);
            return stud;
        } catch (SQLException e) { e.printStackTrace(); }
        return stud;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        Collection<String> result = new ArrayList<String>();
        try {
            makeConnection(connectionURL);
            ResultSet res = allID_preparedStatement.executeQuery();
            while(res.next()) {
                result.add(res.getString(1));
            }
            return result;
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
}
