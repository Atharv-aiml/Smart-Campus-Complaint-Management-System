package main;

import repository.AdminRepository;
import repository.ComplaintRepository;
import repository.StudentRepository;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;
import ui.LoginFrame;
import util.DataInitializer;

import javax.swing.*;
import java.io.File;

/**
 * Main application entry point for the Smart Campus Complaint Management System.
 * VIT Bhopal University - Java Academic Project.
 */
public class Main {

    private static final String DATA_DIR = "data" + File.separator;
    private static final String STUDENTS_FILE = DATA_DIR + "students.dat";
    private static final String ADMINS_FILE = DATA_DIR + "admins.dat";
    private static final String COMPLAINTS_FILE = DATA_DIR + "complaints.dat";

    public static void main(String[] args) {
        // 1. Enable modern Swing UI rendering & anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to cross-platform look and feel
        }

        // 2. Initialize Layered Architecture Components
        System.out.println("==========================================================");
        System.out.println("  SMART CAMPUS COMPLAINT MANAGEMENT SYSTEM - VIT BHOPAL   ");
        System.out.println("==========================================================");
        System.out.println("Initializing data repositories...");

        StudentRepository studentRepository = new StudentRepository(STUDENTS_FILE);
        AdminRepository adminRepository = new AdminRepository(ADMINS_FILE);
        ComplaintRepository complaintRepository = new ComplaintRepository(COMPLAINTS_FILE);

        // 3. Seed default demo data if starting fresh
        DataInitializer.initializeSampleData(studentRepository, adminRepository, complaintRepository);
        System.out.printf("Loaded: %d Students, %d Administrators, %d Complaints.%n",
                studentRepository.count(), adminRepository.count(), complaintRepository.count());

        // 4. Initialize Business Logic Services
        AuthService authService = new AuthService(studentRepository, adminRepository);
        ComplaintService complaintService = new ComplaintService(complaintRepository, studentRepository);
        ReportService reportService = new ReportService(complaintRepository);

        // 5. Launch Swing GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame loginFrame = new LoginFrame(authService, complaintService, reportService);
                loginFrame.setVisible(true);
                System.out.println("Swing GUI initialized successfully. Welcome window is active.");
            } catch (Exception e) {
                System.err.println("Fatal error launching GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
