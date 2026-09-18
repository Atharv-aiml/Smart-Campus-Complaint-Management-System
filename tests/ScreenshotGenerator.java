package tests;

import model.*;
import repository.AdminRepository;
import repository.ComplaintRepository;
import repository.StudentRepository;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;
import ui.*;
import util.DataInitializer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility that programmatically renders and captures actual PNG screenshots
 * of all application screens directly from the live Java Swing components.
 */
public class ScreenshotGenerator {

    private static final String SCREENSHOTS_DIR = "screenshots";

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        new File(SCREENSHOTS_DIR).mkdirs();

        // 1. Initialize data repositories with sample data
        File tempDir = new File("test_screenshots_data");
        tempDir.mkdirs();

        StudentRepository studentRepo = new StudentRepository(tempDir.getPath() + "/students.dat");
        AdminRepository adminRepo = new AdminRepository(tempDir.getPath() + "/admins.dat");
        ComplaintRepository complaintRepo = new ComplaintRepository(tempDir.getPath() + "/complaints.dat");

        studentRepo.clear();
        adminRepo.clear();
        complaintRepo.clear();
        DataInitializer.initializeSampleData(studentRepo, adminRepo, complaintRepo);

        AuthService authService = new AuthService(studentRepo, adminRepo);
        ComplaintService complaintService = new ComplaintService(complaintRepo, studentRepo);
        ReportService reportService = new ReportService(complaintRepo);

        Student demoStudent = studentRepo.findByUsername("atharv").orElseThrow();
        Admin demoAdmin = adminRepo.findByUsername("admin").orElseThrow();
        Complaint demoComplaint = complaintService.getAllComplaints().get(0);

        System.out.println("Rendering screenshots of application screens...");

        // 1. Login Frame
        LoginFrame loginFrame = new LoginFrame(authService, complaintService, reportService);
        captureComponent(loginFrame, SCREENSHOTS_DIR + "/01_login_screen.png", 520, 640);

        // 2. Register Dialog
        RegisterDialog regDialog = new RegisterDialog(null, authService);
        captureComponent(regDialog, SCREENSHOTS_DIR + "/02_student_registration.png", 560, 680);

        // 3. Student Dashboard
        StudentDashboardFrame studentDash = new StudentDashboardFrame(demoStudent, authService, complaintService, reportService);
        captureComponent(studentDash, SCREENSHOTS_DIR + "/03_student_dashboard_my_complaints.png", 1080, 720);

        // 4. Complaint Details Dialog
        ComplaintDetailsDialog detailsDialog = new ComplaintDetailsDialog(null, demoComplaint);
        captureComponent(detailsDialog, SCREENSHOTS_DIR + "/05_complaint_details_modal.png", 620, 650);

        // 5. Admin Dashboard
        AdminDashboardFrame adminDash = new AdminDashboardFrame(demoAdmin, authService, complaintService, reportService);
        captureComponent(adminDash, SCREENSHOTS_DIR + "/06_admin_dashboard_management.png", 1180, 760);

        // 6. Analytics Panel
        AnalyticsPanel analyticsPanel = new AnalyticsPanel(reportService, null);
        analyticsPanel.setSize(1000, 650);
        capturePanel(analyticsPanel, SCREENSHOTS_DIR + "/07_campus_analytics_reports.png", 1000, 650);

        System.out.println("All screenshots successfully captured into " + SCREENSHOTS_DIR + "/");

        // Clean up temp data
        studentRepo.clear();
        adminRepo.clear();
        complaintRepo.clear();
        tempDir.delete();
    }

    private static void captureComponent(Window window, String outputPath, int width, int height) {
        window.setSize(width, height);
        window.doLayout();
        window.validate();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        window.paint(g2);
        g2.dispose();

        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("  [SAVED] " + outputPath);
        } catch (Exception e) {
            System.err.println("  [ERROR] Failed to save " + outputPath + ": " + e.getMessage());
        } finally {
            window.dispose();
        }
    }

    private static void capturePanel(JPanel panel, String outputPath, int width, int height) {
        panel.setSize(width, height);
        panel.doLayout();
        panel.validate();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        panel.paint(g2);
        g2.dispose();

        try {
            ImageIO.write(image, "png", new File(outputPath));
            System.out.println("  [SAVED] " + outputPath);
        } catch (Exception e) {
            System.err.println("  [ERROR] Failed to save " + outputPath + ": " + e.getMessage());
        }
    }
}
