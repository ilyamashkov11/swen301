package nz.ac.wgtn.swen301.assignment1.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import nz.ac.wgtn.swen301.studentdb.*;
import org.apache.commons.cli.*;
import nz.ac.wgtn.swen301.assignment1.StudentManager;

public class StudentManagerUI {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide a student id as single argument, and if the details are found,
     * the respective details are printed to the console.
     * E.g. a user could invoke this by running "java -cp <someclasspath> nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42"
     * @param arg
     */
    public static void main (String[] arg) {
        Options options = new Options();
        options.addOption("fetchone", true, "fetches one student with the provided id");
        options.addOption("fetchall", false, "fetches all students");
        options.addOption("export", false, "efetches all students, writes them to a file in CSV format");
        options.addOption("f", true, "filename");

        CommandLineParser cmdparser = new DefaultParser();
        try {
            new StudentManager();
            CommandLine cmdln = cmdparser.parse(options, arg);

            if (cmdln.hasOption("fetchone")) {
                String studentId = cmdln.getOptionValue("fetchone");
                try {
                    System.out.println("id" + " " + "name" + " " + "first name" + " " + "degree id");
                    System.out.println(studentToString(StudentManager.fetchStudent("id" +studentId)));
                } catch (NoSuchRecordException e) { e.printStackTrace(); }
            } else if (cmdln.hasOption("fetchall")) {
                allToString(StudentManager.fetchAllStudentIds(), true);
            } else if (cmdln.hasOption("export")) {
                String exportFileName = cmdln.getOptionValue("f");
                createFile(exportFileName);
                //exportStudentsToCSV(exportFileName);
            } else {
                System.out.println("Please provide a valid option.");
            }
        } catch (ParseException p) {p.printStackTrace();}
    }

    private static String studentToString(Student s) {
        return s.getId() + " " + s.getName() + " " + s.getFirstName()+ " " + s.getDegree().getId();
    }

    private static ArrayList<String> allToString(Collection<String> all, boolean print) {
        ArrayList<String> students = new ArrayList<>();
        try {
            for(String s : all) {
                if (!print) {students.add(studentToString(StudentManager.fetchStudent(s)));}
                else { 
                    //students.add(studentToString(StudentManager.fetchStudent(s)));
                    System.out.println(studentToString(StudentManager.fetchStudent(s)));
                }
            }
        } catch (NoSuchRecordException e) {e.printStackTrace();}
        return students;
    }

    private static void createFile(String filename) {
        try {
            File file = new File(filename + ".csv");
            if (file.createNewFile()) {System.out.println("file created");}
            else {System.out.println("file already exists");}

            try (FileWriter w = new FileWriter(filename + ".csv")) {
                for(String s : allToString(StudentManager.fetchAllStudentIds(), false)) {
                    w.write(s + ", ");
                }
                w.close();
                System.out.println("written to successfuly");
            }
        } catch(IOException e) {e.printStackTrace();}

    }
}
