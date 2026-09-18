package ui;

import exception.DuplicateResourceException;
import exception.ValidationException;
import model.Student;
import service.AuthService;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Registration modal dialog for new student account creation.
 */
public class RegisterDialog extends JDialog {

    private final AuthService authService;
    private boolean registrationSuccessful = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField regNoField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> hostelCombo;
    private JTextField roomNumberField;

    public RegisterDialog(Frame parent, AuthService authService) {
        super(parent, "Student Registration - Smart Campus Portal", true);
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setSize(560, 680);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLabel = new JLabel("Create Student Account");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Register your student details to file and track campus complaints");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(new Color(224, 231, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BG_MAIN);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        usernameField = UITheme.createTextField(20);
        passwordField = UITheme.createPasswordField(20);
        fullNameField = UITheme.createTextField(20);
        regNoField = UITheme.createTextField(20);
        emailField = UITheme.createTextField(20);
        phoneField = UITheme.createTextField(20);

        String[] departments = {
                "SCOPE - Computer Science & Engineering",
                "SCSE - Cyber Security & Digital Forensics",
                "SEEE - Electrical & Electronics Engineering",
                "Mechanical Engineering",
                "Bio-Engineering & Sciences",
                "Aerospace Engineering",
                "VIT School of Business"
        };
        departmentCombo = new JComboBox<>(departments);
        departmentCombo.setFont(UITheme.FONT_BODY);
        departmentCombo.setBackground(Color.WHITE);

        String[] hostels = {
                "Boys Hostel Block 1",
                "Boys Hostel Block 2",
                "Boys Hostel Block 3",
                "Girls Hostel Block A",
                "Girls Hostel Block B",
                "Day Scholar"
        };
        hostelCombo = new JComboBox<>(hostels);
        hostelCombo.setFont(UITheme.FONT_BODY);
        hostelCombo.setBackground(Color.WHITE);

        roomNumberField = UITheme.createTextField(10);
        roomNumberField.setText("N/A");

        formPanel.add(createFieldRow("Username *", usernameField, "e.g. atharv_k"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Password (min 6 chars) *", passwordField, "Enter secure password"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Full Name *", fullNameField, "e.g. Atharv Kulkarni"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Registration Number *", regNoField, "e.g. 22BCE10234"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Email Address *", emailField, "e.g. student@vitbhopal.ac.in"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Mobile Phone (10 digits) *", phoneField, "e.g. 9876543210"));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldRow("Academic School / Department", departmentCombo, null));
        formPanel.add(Box.createVerticalStrut(10));

        JPanel residencePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        residencePanel.setBackground(UITheme.BG_MAIN);
        residencePanel.add(createFieldRow("Hostel Block", hostelCombo, null));
        residencePanel.add(createFieldRow("Room No", roomNumberField, "e.g. 312"));
        formPanel.add(residencePanel);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton registerBtn = UITheme.createPrimaryButton("Register Account");
        registerBtn.addActionListener(e -> handleRegistration());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldRow(String labelText, JComponent component, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(UITheme.BG_MAIN);

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setForeground(UITheme.TEXT_PRIMARY);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        if (placeholder != null && component instanceof JTextField tf && tf.getText().isEmpty()) {
            tf.setToolTipText(placeholder);
        }
        return panel;
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String regNo = regNoField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();
        String hostel = (String) hostelCombo.getSelectedItem();
        String room = roomNumberField.getText().trim();

        try {
            Student registered = authService.registerStudent(
                    username, password, fullName, email, phone, regNo, department, hostel, room
            );
            registrationSuccessful = true;
            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful!\n\nWelcome, " + registered.getFullName() +
                            "\nYou may now log in with username: " + registered.getUsername(),
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        } catch (ValidationException | DuplicateResourceException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred during registration: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}
