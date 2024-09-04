package timetable_managergpk1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Font;








//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.File;

public class TimetableManager extends JFrame {

    private Connection connection;

    // Main UI Components
    // labels for entering data
    private JLabel p_no_label;
    private JLabel day_label;
    private JLabel branch_label;
    private JLabel subject_label;
    private JLabel teacher_label;
    
    
    
    // textfields for entering data
    private JTextField periodField;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> branchComboBox;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> teacherComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTable timetableTable;
    
    // export data to pdf excel button
    private JButton exportToPdfButton;
    private JButton exportToExcelButton;

    private JButton showdatabyteacher;
    
    // Add components for adding branches, subjects, and teachers
    private JTextField newBranchField;
    private JTextField newSubjectField;
    private JTextField newTeacherField;
    private JButton addBranchButton;
    private JButton addSubjectButton;
    private JButton addTeacherButton;
   // private  JTable  timetableTable;
    
    //specific_teacherui tui=new specific_teacherui();
    
    
    
    
    
    
    
    

    public TimetableManager() {
        setTitle("Timetable Manager IT Dept@government Polytechnic Kanpur developed by svinfotech");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        p_no_label =new JLabel("Period No:");
        day_label =new JLabel("Day:");
        branch_label =new JLabel("Branch:");
        subject_label =new JLabel("Subject:");
        teacher_label =new JLabel("Teacher:");

        
        // Main Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(p_no_label);
        periodField = new JTextField();
        inputPanel.add(periodField);

        inputPanel.add(day_label);
        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        inputPanel.add(dayComboBox);

        inputPanel.add(branch_label);
        branchComboBox = new JComboBox<>();
        inputPanel.add(branchComboBox);

        inputPanel.add(subject_label);
        subjectComboBox = new JComboBox<>();
        inputPanel.add(subjectComboBox);

        inputPanel.add(teacher_label);
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
        
        
        exportToPdfButton = new JButton("Export to PDF");
        exportToExcelButton = new JButton("Export to Excel");
        showdatabyteacher=new JButton("Show TIME Table by Teacher");
        
        
        buttonPanel.add(exportToPdfButton);
        buttonPanel.add(exportToExcelButton);
        buttonPanel.add( showdatabyteacher);
        
        

        // Table for timetable
        timetableTable = new JTable(new DefaultTableModel(
            new Object[]{"Period No", "Day", "Branch", "Subject", "Teacher"}, 0
        ));
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);
        timetableTable.setRowHeight(100); // Adjust this value as needed
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); // Center align content
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER); // Center align vertically
        
        centerRenderer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding inside cells

        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        BoldTableCellRenderer boldRenderer = new BoldTableCellRenderer();
        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(boldRenderer);
        }

        
        
        
        
        
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
        
        exportToPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //  exportToPDF();
            }
        });

        exportToExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             //   exportToExcel();
            }
        });
    
       // showdatabyteacher.addActionListener((ActionListener) this);
        showdatabyteacher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            new specific_teacherui();
            }
        });
     
        
        
       // specific_teacherui tui=new specific_teacherui();
        
        
        
    }
    
    
 /* Method to export table data to PDF
    private void exportToPDF() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("timetable.pdf"));
            document.open();
            PdfPTable pdfTable = new PdfPTable(timetableTable.getColumnCount());

            // Add table headers
            for (int i = 0; i < timetableTable.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(timetableTable.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Add table rows
            for (int i = 0; i < timetableTable.getRowCount(); i++) {
                for (int j = 0; j < timetableTable.getColumnCount(); j++) {
                    pdfTable.addCell(timetableTable.getValueAt(i, j).toString());
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF exported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting to PDF.");
        }
    }

    // Method to export table data to Excel
    private void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Timetable");

        // Create the header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(timetableTable.getColumnName(i));
        }

        // Create rows for each table row
        for (int i = 0; i < timetableTable.getRowCount(); i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < timetableTable.getColumnCount(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(timetableTable.getValueAt(i, j).toString());
            }
        }

        try {
            FileOutputStream fileOut = new FileOutputStream("timetable.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            JOptionPane.showMessageDialog(this, "Excel exported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting to Excel.");
        }
    }
    
    
    
    
*/
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

            // Set column names: first column for days, then periods
            model.setColumnIdentifiers(new Object[]{"Day", "Period 1", "Period 2", "Period 3", "Period 4", "Period 5", "Period 6", "Period 7", "Period 8"});

            // Days of the week
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            // Prepare a map to store the schedule for each day and period
            Map<String, String[]> timetableData = new HashMap<>();

            // Initialize the map with empty strings for each day
            for (String day : days) {
                timetableData.put(day, new String[8]);
            }

            // Fetch the data from the database
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT p.period_no, p.day, b.name AS branch, s.name AS subject, t.name AS teacher " +
                "FROM periods p " +
                "JOIN branches b ON p.branch_id = b.id " +
                "JOIN subjects s ON p.subject_id = s.id " +
                "JOIN teachers t ON p.teacher_id = t.id");

            // Populate the map with the fetched data
            while (rs.next()) {
                int periodNo = rs.getInt("period_no");
                String day = rs.getString("day");
                String branch = rs.getString("branch");
                String subject = rs.getString("subject");
                String teacher = rs.getString("teacher");

                // Construct the cell content
                String cellContent = String.format("<html>%s<br>%s<br>%s</html>", subject, teacher, branch);
                
                // Insert the data into the appropriate day and period
                timetableData.get(day)[periodNo - 1] = cellContent;
            }

            // Add the data to the table model row by row (each day)
            for (String day : days) {
                Object[] row = new Object[9]; // 1 for day, 8 for periods
                row[0] = day; // First column is the day
                String[] periods = timetableData.get(day);
                System.arraycopy(periods, 0, row, 1, 8); // Copy periods into the row
                model.addRow(row);
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


class BoldTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setFont(c.getFont().deriveFont(Font.BOLD)); // Set bold font
        return c;
    }
}
















