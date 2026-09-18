package ui;

import exception.AuthenticationException;
import exception.ValidationException;
import model.Admin;
import model.Student;
import model.User;
import model.UserRole;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Entry-point login frame for both Students and Administrators.
 * Features demo credential quick-fill shortcuts for hassle-free evaluation.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final ComplaintService complaintService;
    private final ReportService reportService;

    private JComboBox<UserRole> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerBtn;

    public LoginFrame(AuthService authService, ComplaintService complaintService, ReportService reportService) {
        super("VIT Bhopal - Smart Campus Complaint Management System");
        this.authService = authService;
        this.complaintService = complaintService;
        this.reportService = reportService;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Banner
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(26, 24, 26, 24));

        JLabel title = new JLabel("Smart Campus Portal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Complaint Tracking & Resolution Management System");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(224, 231, 255));
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel inst = new JLabel("VIT Bhopal University - Java Academic Project");
        inst.setFont(new Font("SansSerif", Font.ITALIC, 11));
        inst.setForeground(new Color(199, 210, 254));
        inst.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        header.add(inst, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Login Card
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(UITheme.BG_MAIN);
        main.setBorder(new EmptyBorder(24, 36, 24, 36));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("Sign In to Your Account");
        cardTitle.setFont(UITheme.FONT_HEADER);
        cardTitle.setForeground(UITheme.TEXT_PRIMARY);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(18));

        // Role Selector
        JPanel rolePanel = new JPanel(new BorderLayout(0, 4));
        rolePanel.setOpaque(false);
        JLabel roleLabel = new JLabel("Select Portal Role:");
        roleLabel.setFont(UITheme.FONT_BODY_BOLD);
        roleLabel.setForeground(UITheme.TEXT_PRIMARY);

        roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setFont(UITheme.FONT_BODY);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.addActionListener(e -> updateRoleMode());

        rolePanel.add(roleLabel, BorderLayout.NORTH);
        rolePanel.add(roleCombo, BorderLayout.CENTER);
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(12));

        // Username
        JPanel userPanel = new JPanel(new BorderLayout(0, 4));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Username / ID:");
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        usernameField = UITheme.createTextField(20);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);
        card.add(userPanel);
        card.add(Box.createVerticalStrut(12));

        // Password
        JPanel passPanel = new JPanel(new BorderLayout(0, 4));
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_BODY_BOLD);
        passLabel.setForeground(UITheme.TEXT_PRIMARY);
        passwordField = UITheme.createPasswordField(20);
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(passwordField, BorderLayout.CENTER);
        card.add(passPanel);
        card.add(Box.createVerticalStrut(18));

        // Login Button
        JButton loginBtn = UITheme.createPrimaryButton("Secure Login");
        loginBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));

        // Student Registration link button
        registerBtn = UITheme.createSecondaryButton("New Student? Register Here");
        registerBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> openRegistration());
        card.add(registerBtn);

        main.add(card);
        main.add(Box.createVerticalStrut(16));

        // Quick Credentials Demo Helper Box
        JPanel demoBox = new JPanel(new BorderLayout(0, 6));
        demoBox.setBackground(new Color(238, 242, 255));
        demoBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel demoHeader = new JLabel("Demo Credentials (Click to Auto-fill):");
        demoHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        demoHeader.setForeground(UITheme.PRIMARY);
        demoBox.add(demoHeader, BorderLayout.NORTH);

        JPanel demoBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        demoBtns.setOpaque(false);

        JButton adminFillBtn = new JButton("Admin (admin/admin123)");
        adminFillBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        adminFillBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminFillBtn.addActionListener(e -> {
            roleCombo.setSelectedItem(UserRole.ADMIN);
            usernameField.setText("admin");
            passwordField.setText("admin123");
        });

        JButton studentFillBtn = new JButton("Student (atharv/student123)");
        studentFillBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        studentFillBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        studentFillBtn.addActionListener(e -> {
            roleCombo.setSelectedItem(UserRole.STUDENT);
            usernameField.setText("atharv");
            passwordField.setText("student123");
        });

        demoBtns.add(adminFillBtn);
        demoBtns.add(studentFillBtn);
        demoBox.add(demoBtns, BorderLayout.CENTER);

        main.add(demoBox);
        add(main, BorderLayout.CENTER);

        // Pre-fill student demo by default
        roleCombo.setSelectedItem(UserRole.STUDENT);
        usernameField.setText("atharv");
        passwordField.setText("student123");

        // Allow pressing Enter key to login
        getRootPane().setDefaultButton(loginBtn);
    }

    private void updateRoleMode() {
        UserRole selected = (UserRole) roleCombo.getSelectedItem();
        registerBtn.setVisible(selected == UserRole.STUDENT);
    }

    private void openRegistration() {
        RegisterDialog dialog = new RegisterDialog(this, authService);
        dialog.setVisible(true);
        if (dialog.isRegistrationSuccessful()) {
            roleCombo.setSelectedItem(UserRole.STUDENT);
        }
    }

    private void handleLogin() {
        UserRole role = (UserRole) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = authService.login(username, password, role);

            if (user instanceof Student student) {
                dispose();
                SwingUtilities.invokeLater(() ->
                        new StudentDashboardFrame(student, authService, complaintService, reportService).setVisible(true)
                );
            } else if (user instanceof Admin admin) {
                dispose();
                SwingUtilities.invokeLater(() ->
                        new AdminDashboardFrame(admin, authService, complaintService, reportService).setVisible(true)
                );
            }
        } catch (ValidationException | AuthenticationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
