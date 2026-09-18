package ui;

import model.Complaint;
import util.DateTimeUtil;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Detailed modal view dialog for inspecting a complete complaint record.
 */
public class ComplaintDetailsDialog extends JDialog {

    public ComplaintDetailsDialog(Window parent, Complaint complaint) {
        super(parent, "Complaint Details - " + complaint.getComplaintId(), ModalityType.APPLICATION_MODAL);
        initUI(complaint);
    }

    private void initUI(Complaint complaint) {
        setSize(620, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel idLabel = new JLabel(complaint.getComplaintId());
        idLabel.setFont(UITheme.FONT_TITLE);
        idLabel.setForeground(Color.WHITE);

        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgesPanel.setOpaque(false);

        JLabel priorityBadge = new JLabel(" " + complaint.getPriority().getDisplayName().toUpperCase() + " ");
        priorityBadge.setFont(UITheme.FONT_BADGE);
        priorityBadge.setForeground(Color.WHITE);
        priorityBadge.setBackground(Color.decode(complaint.getPriority().getColorHex()));
        priorityBadge.setOpaque(true);
        priorityBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel statusBadge = new JLabel(" " + complaint.getStatus().getDisplayName().toUpperCase() + " ");
        statusBadge.setFont(UITheme.FONT_BADGE);
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(Color.decode(complaint.getStatus().getBadgeColorHex()));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        badgesPanel.add(priorityBadge);
        badgesPanel.add(statusBadge);

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(badgesPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content Body
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(UITheme.BG_MAIN);
        bodyPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        bodyPanel.add(createSectionHeader("Issue Overview"));
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(createDetailItem("Title", complaint.getTitle()));
        bodyPanel.add(createDetailItem("Category", complaint.getCategory().getDisplayName() + " (" + complaint.getCategory().getDescription() + ")"));
        bodyPanel.add(createDetailItem("Location", complaint.getLocation()));

        bodyPanel.add(Box.createVerticalStrut(14));
        bodyPanel.add(createSectionHeader("Description"));
        bodyPanel.add(Box.createVerticalStrut(6));

        JTextArea descArea = new JTextArea(complaint.getDescription());
        descArea.setFont(UITheme.FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(550, 80));
        bodyPanel.add(descScroll);

        bodyPanel.add(Box.createVerticalStrut(14));
        bodyPanel.add(createSectionHeader("Student & Tracking Information"));
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(createDetailItem("Submitted By", complaint.getStudentName() + " (" + complaint.getStudentRegNo() + ")"));
        bodyPanel.add(createDetailItem("Student Username", complaint.getStudentUsername()));
        bodyPanel.add(createDetailItem("Assigned Department / Staff", complaint.getAssignedTo()));
        bodyPanel.add(createDetailItem("Date Submitted", DateTimeUtil.format(complaint.getCreatedAt()) + " (" + DateTimeUtil.getRelativeTime(complaint.getCreatedAt()) + ")"));
        bodyPanel.add(createDetailItem("Last Updated", DateTimeUtil.format(complaint.getUpdatedAt())));
        if (complaint.getResolvedAt() != null) {
            bodyPanel.add(createDetailItem("Resolved At", DateTimeUtil.format(complaint.getResolvedAt())));
        }

        bodyPanel.add(Box.createVerticalStrut(14));
        bodyPanel.add(createSectionHeader("Official Administration Remarks"));
        bodyPanel.add(Box.createVerticalStrut(6));

        JTextArea remarksArea = new JTextArea(complaint.getAdminRemarks());
        remarksArea.setFont(UITheme.FONT_BODY);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        remarksArea.setEditable(false);
        remarksArea.setBackground(new Color(254, 252, 232)); // Light subtle yellow
        remarksArea.setForeground(new Color(113, 63, 18));
        remarksArea.setBorder(new CompoundBorder(new LineBorder(new Color(254, 240, 138), 1), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setPreferredSize(new Dimension(550, 65));
        bodyPanel.add(remarksScroll);

        JScrollPane mainScroll = new JScrollPane(bodyPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(mainScroll, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(6, 12, 6, 12)));

        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel createSectionHeader(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(UITheme.FONT_HEADER);
        lbl.setForeground(UITheme.PRIMARY);
        return lbl;
    }

    private JPanel createDetailItem(String labelText, String valueText) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(UITheme.BG_MAIN);

        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(190, 22));

        JLabel val = new JLabel(valueText != null ? valueText : "N/A");
        val.setFont(UITheme.FONT_BODY);
        val.setForeground(UITheme.TEXT_PRIMARY);

        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.CENTER);
        return p;
    }
}
