package tests;

import model.Student;
import repository.StudentRepository;

import java.io.File;
import java.util.Optional;

/**
 * Unit tests verifying FileRepository persistence, reload, and serialization integrity.
 */
public class FileRepositoryTest {

    private static final String TEST_FILE = "test_data" + File.separator + "persistence_test.dat";

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testSaveAndReloadAcrossInstances(runner);
        testUpdatePersistsAcrossReload(runner);
        testDeleteRemovesFromDisk(runner);
    }

    private static void testSaveAndReloadAcrossInstances(TestSuiteRunner.TestCollector runner) {
        String testName = "FileRepository: Entities persist to disk and reload across new repository instances";
        try {
            File f = new File(TEST_FILE);
            if (f.exists()) f.delete();

            // Instance 1: Save student
            StudentRepository repo1 = new StudentRepository(TEST_FILE);
            Student s1 = new Student("S1", "persisted_user", "pass123", "Persistent Student",
                    "persist@vitbhopal.ac.in", "9876543210", "22BCE9999");
            repo1.save(s1);

            // Instance 2: Reload from same file
            StudentRepository repo2 = new StudentRepository(TEST_FILE);
            Optional<Student> loaded = repo2.findByUsername("persisted_user");

            if (loaded.isPresent() && loaded.get().getRegistrationNumber().equals("22BCE9999")) {
                runner.recordPass(testName, "Entity successfully reloaded from file", loaded.get().toString());
            } else {
                runner.recordFail(testName, "Failed to reload entity from file", "Not found");
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Persistence test failed", e.getMessage());
        }
    }

    private static void testUpdatePersistsAcrossReload(TestSuiteRunner.TestCollector runner) {
        String testName = "FileRepository: Entity modification updates record on disk";
        try {
            StudentRepository repo1 = new StudentRepository(TEST_FILE);
            Student s = repo1.findByUsername("persisted_user").orElseThrow();
            s.setRoomNumber("505");
            repo1.save(s);

            StudentRepository repo2 = new StudentRepository(TEST_FILE);
            Student reloaded = repo2.findByUsername("persisted_user").orElseThrow();

            if ("505".equals(reloaded.getRoomNumber())) {
                runner.recordPass(testName, "Updated room number 505 verified on reload", reloaded.getRoomNumber());
            } else {
                runner.recordFail(testName, "Room number was not updated on reload", reloaded.getRoomNumber());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Update test failed", e.getMessage());
        }
    }

    private static void testDeleteRemovesFromDisk(TestSuiteRunner.TestCollector runner) {
        String testName = "FileRepository: Deletion removes record from storage";
        try {
            StudentRepository repo1 = new StudentRepository(TEST_FILE);
            boolean deleted = repo1.deleteById("S1");

            StudentRepository repo2 = new StudentRepository(TEST_FILE);
            boolean exists = repo2.existsById("S1");

            if (deleted && !exists && repo2.count() == 0) {
                runner.recordPass(testName, "Entity deleted and confirmed missing in new instance", "Count = 0");
            } else {
                runner.recordFail(testName, "Entity still exists after delete", "exists = " + exists);
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Delete test failed", e.getMessage());
        }
    }
}
