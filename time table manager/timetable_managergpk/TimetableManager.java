package timetable_managergpk;




import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TimetableManager extends JFrame {

    private Connection connection;

    // Main UI Components
    private JTextField periodField;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> branchComboBox;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> teacherComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable timetableTable;

    // Add components for adding branches, subjects, and teachers
    private JTextField newBranchField;
    private JTextField newSubjectField;
    private JTextField newTeacherField;
    private JButton addBranchButton;
    private JButton addSubjectButton;
    private JButton addTeacherButton;
   // private  JTable  timetableTable;

    public TimetableManager() {
        setTitle("Timetable Manager IT Dept@government Polytechnic Kanpur developed by svinfotech");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Period No:"));
        periodField = new JTextField();
        inputPanel.add(periodField);

        inputPanel.add(new JLabel("Day:"));
        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        inputPanel.add(dayComboBox);

        inputPanel.add(new JLabel("Branch:"));
        branchComboBox = new JComboBox<>();
        inputPanel.add(branchComboBox);

        inputPanel.add(new JLabel("Subject:"));
        subjectComboBox = new JComboBox<>();
        inputPanel.add(subjectComboBox);

        inputPanel.add(new JLabel("Teacher:"));
        teacherComboBox = new JComboBox<>();
        inputPanel.add(teacherComboBox);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table for timetable
        timetableTable = new JTable(new DefaultTableModel(
            new Object[]{"Period No", "Day", "Branch", "Subject", "Teacher"}, 0
        ));
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        // Add Branch, Subject, Teacher Panels
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(4, 3));

        addPanel.add(new JLabel("New Branch:"));
        newBranchField = new JTextField();
        addPanel.add(newBranchField);
        addBranchButton = new JButton("Add Branch");
        addPanel.add(addBranchButton);

        addPanel.add(new JLabel("New Subject:"));
        newSubjectField = new JTextField();
        addPanel.add(newSubjectField);
        addSubjectButton = new JButton("Add Subject");
        addPanel.add(addSubjectButton);

        addPanel.add(new JLabel("New Teacher:"));
        newTeacherField = new JTextField();
        addPanel.add(newTeacherField);
        addTeacherButton = new JButton("Add Teacher");
        addPanel.add(addTeacherButton);

        add(addPanel, BorderLayout.EAST);

        // Connect to the database
        connectToDatabase();

        // Populate combo boxes
        populateComboBoxes();

        // Refresh the table to show the current timetable
        refreshTable();

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntry();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEntry();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEntry();
            }
        });

        addBranchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBranch();
            }
        });

        addSubjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSubject();
            }
        });

        addTeacherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeacher();
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/timetable_manager", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateComboBoxes() {
        try {
            // Populate branches
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM branches");
            branchComboBox.removeAllItems();
            while (rs.next()) {
                branchComboBox.addItem(rs.getString("name"));
            }

            // Populate subjects
            rs = stmt.executeQuery("SELECT * FROM subjects");
            subjectComboBox.removeAllItems();
            while (rs.next()) {
                subjectComboBox.addItem(rs.getString("name"));
            }

            // Populate teachers
            rs = stmt.executeQuery("SELECT * FROM teachers");
            teacherComboBox.removeAllItems();
            while (rs.next()) {
                teacherComboBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEntry() {
        String periodNo = periodField.getText();
        String day = (String) dayComboBox.getSelectedItem();
        String branch = (String) branchComboBox.getSelectedItem();
        String subject = (String) subjectComboBox.getSelectedItem();
        String teacher = (String) teacherComboBox.getSelectedItem();

        if (periodNo.isEmpty() || branch == null || subject == null || teacher == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out");
            return;
        }

        try {
            // Get IDs for branch, subject, and teacher
            int branchId = getId("branches", branch);
            int subjectId = getId("subjects", subject);
            int teacherId = getId("teachers", teacher);

            PreparedStatement ps = connection.prepareStatement("INSERT INTO periods (period_no, day, branch_id, subject_id, teacher_id) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, Integer.parseInt(periodNo));
            ps.setString(2, day);
            ps.setInt(3, branchId);
            ps.setInt(4, subjectId);
            ps.setInt(5, teacherId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Entry added successfully");
            populateComboBoxes(); // Refresh the combo boxes
            refreshTable(); // Refresh the table data
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding entry");
        }
    }

    
    
    
    
    private void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) timetableTable.getModel();
            model.setRowCount(0); // Clear existing rows

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT p.period_no, p.day, b.name AS branch, s.name AS subject, t.name AS teacher " +
                                             "FROM periods p " +
                                             "JOIN branches b ON p.branch_id = b.id " +
                                             "JOIN subjects s ON p.subject_id = s.id " +
                                             "JOIN teachers t ON p.teacher_id = t.id");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("period_no"),
                    rs.getString("day"),
                    rs.getString("branch"),
                    rs.getString("subject"),
                    rs.getString("teacher")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing table");
        }
    }

    
    
    
    
    private int getId(String table, String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT id FROM " + table + " WHERE name = ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        throw new SQLException("No ID found for " + name);
    }

    private void updateEntry() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.");
            return;
        }

        String periodNo = periodField.getText();
        String day = (String) dayComboBox.getSelectedItem();
        String branch = (String) branchComboBox.getSelectedItem();
        String subject = (String) subjectComboBox.getSelectedItem();
        String teacher = (String) teacherComboBox.getSelectedItem();

        if (periodNo.isEmpty() || branch == null || subject == null || teacher == null) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out");
            return;
        }

        try {
            // Get IDs for branch, subject, and teacher
            int branchId = getId("branches", branch);
            int subjectId = getId("subjects", subject);
            int teacherId = getId("teachers", teacher);

            // Get the period_no and day of the original selected row to identify the record in the database
            int originalPeriodNo = (int) timetableTable.getValueAt(selectedRow, 0);
            String originalDay = (String) timetableTable.getValueAt(selectedRow, 1);

            PreparedStatement ps = connection.prepareStatement(
                "UPDATE periods SET period_no = ?, day = ?, branch_id = ?, subject_id = ?, teacher_id = ? " +
                "WHERE period_no = ? AND day = ?"
            );
            ps.setInt(1, Integer.parseInt(periodNo));
            ps.setString(2, day);
            ps.setInt(3, branchId);
            ps.setInt(4, subjectId);
            ps.setInt(5, teacherId);
            ps.setInt(6, originalPeriodNo);
            ps.setString(7, originalDay);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Entry updated successfully");
            refreshTable(); // Refresh the table data
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating entry");
        }
    }

    private void deleteEntry() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        // Confirm deletion with the user
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Get the original period_no and day from the selected row to identify the record in the database
            int periodNo = (int) timetableTable.getValueAt(selectedRow, 0);
            String day = (String) timetableTable.getValueAt(selectedRow, 1);

            // Prepare the SQL delete statement
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM periods WHERE period_no = ? AND day = ?"
            );
            ps.setInt(1, periodNo);
            ps.setString(2, day);

            // Execute the delete operation
            ps.executeUpdate();

            // Inform the user and refresh the table
            JOptionPane.showMessageDialog(this, "Entry deleted successfully.");
            refreshTable(); // Refresh the table to remove the deleted row

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting entry.");
        }
    }


    private void addBranch() {
        String branchName = newBranchField.getText();
        if (branchName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Branch name cannot be empty");
            return;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO branches (name) VALUES (?)");
            ps.setString(1, branchName);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Branch added successfully");
            populateComboBoxes(); // Refresh the combo boxes
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding branch");
        }
    }

    private void addSubject() {
        String subjectName = newSubjectField.getText();
        if (subjectName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject name cannot be empty");
            return;
        }

        // Here you should add logic to select branch and type (Theory/Lab)
        int branchId = 1; // Placeholder, should be replaced with actual branch ID selection
        String type = "Theory"; // Placeholder, should be replaced with actual type selection

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO subjects (branch_id, name, type) VALUES (?, ?, ?)");
            ps.setInt(1, branchId);
            ps.setString(2, subjectName);
            ps.setString(3, type);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject added successfully");
            populateComboBoxes(); // Refresh the combo boxes
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding subject");
        }
    }

    private void addTeacher() {
        String teacherName = newTeacherField.getText();
        if (teacherName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Teacher name cannot be empty");
            return;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO teachers (name) VALUES (?)");
            ps.setString(1, teacherName);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Teacher added successfully");
            populateComboBoxes(); // Refresh the combo boxes
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding teacher");
        }
    }
    
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TimetableManager().setVisible(true);
            }
        });
    }
}

