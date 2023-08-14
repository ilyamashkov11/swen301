package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;

/**
 * Unit tests for StudentManager, to be extended.
 */
public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @BeforeEach
    public  void init () {
        StudentDB.init();
    }

    @AfterEach
    public void reset() {
        StudentManager.reset();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    @Test
    public static void dummyTest() throws Exception {
        new StudentManager();
        Student student = StudentManager.fetchStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    @Test
    public void testFetchDegree1() throws Exception {
        // new StudentManager();
        Degree degree = StudentManager.fetchDegree("deg3");
        assertNotNull(degree);
    }

    @Test
    public void testFetchDegree2() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchDegree("deg10"));
        // assert(true);
    }

     @Test
    public void testFetchDegree3() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchDegree(""));
    }

    @Test
    public void testFetchDegree4() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchDegree(null));
    }

    @Test
    public void testFetchStudent1() throws Exception {
        // new StudentManager();
        assertNotNull(StudentManager.fetchStudent("id4"));
    }

    @Test
    public void testFetchStudent2() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent("id-1"));
    }

    @Test
    public void testFetchStudent3() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent(""));
    }

    @Test
    public void testFetchStudent4() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent(null));
    }

    @Test
    public void testRemoveStudent1() throws Exception {
        // new StudentManager();
        Student student = StudentManager.fetchStudent("id2");
        assertNotNull(student);
        StudentManager.remove(student);
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent("id2"));
    }

    @Test
    public void testRemoveStudent2() throws Exception {
        // new StudentManager();
        assertThrows(NoSuchRecordException.class, () -> StudentManager.remove(null));
    }

    @Test
    public void testFetchAllStudentIds() throws Exception {
        // new StudentManager();
        assertNotNull(StudentManager.fetchAllStudentIds());
    }

    @Test
    public void testUpdate() throws Exception {
        // Student initial = StudentManager.fetchStudent("id9");
        Student newInfo = new Student("id9", "Mandalawi", "Leo", new Degree("deg3", "Dropout"));
        StudentManager.update(newInfo);
        Student updated = StudentManager.fetchStudent("id9");
        assertEquals(updated, newInfo);
    }

    @Test
    public void testNewStudent() throws Exception {
        //new StudentManager();
        Collection<String> initial = StudentManager.fetchAllStudentIds();
        StudentManager.newStudent("Charlton", "Bobby", StudentManager.fetchDegree("deg1"));
        Collection<String> edited = StudentManager.fetchAllStudentIds();

        assert initial.size() < edited.size();
    }

    @Test
    public void testPerformance() throws Exception {
        long time = System.currentTimeMillis();
        
        for(int i = 0; i < 500; i++) {
            StudentManager.fetchStudent("id" + i);
        }
        
        long totalTime = System.currentTimeMillis() - time;
        System.out.println(totalTime + " ms");
    }
}
