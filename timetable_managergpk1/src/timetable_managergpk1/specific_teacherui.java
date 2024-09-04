package timetable_managergpk1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;




import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import java.io.FileOutputStream;
//import java.io.IOException;

public class specific_teacherui extends JFrame {
    private JComboBox<String> branchComboBox;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> teacherComboBox;
    private JComboBox<String> teacherFilterComboBox;
    private JTable timetableTable;
    private Connection connection;

    public specific_teacherui() {
        // Initialize the JFrame
        setTitle("Timetable Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the input panel for adding periods
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        branchComboBox = new JComboBox<>();
        subjectComboBox = new JComboBox<>();
        teacherComboBox = new JComboBox<>();

      //  inputPanel.add(new JLabel("Branch:"));
     //   inputPanel.add(branchComboBox);
    //    inputPanel.add(new JLabel("Subject:"));
    //    inputPanel.add(subjectComboBox);
    //    inputPanel.add(new JLabel("Teacher:"));
    //    inputPanel.add(teacherComboBox);

        JButton backButton = new JButton("BACK");
        inputPanel.add(backButton );
        
        JButton exporttopdf = new JButton("EXPORT TO PDF");
        inputPanel.add(exporttopdf);
        
        JButton exporttoexcel = new JButton("EXPORT TO EXCEL");
        inputPanel.add(exporttoexcel);
        
        add(inputPanel, BorderLayout.SOUTH);

        // Initialize the filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());

        teacherFilterComboBox = new JComboBox<>();
        filterPanel.add(new JLabel("Filter by Teacher:"));
        filterPanel.add(teacherFilterComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // Initialize the timetable table
        timetableTable = new JTable(new DefaultTableModel(new Object[]{"Day", "Period 1", "Period 2", "Period 3", "Period 4", "Period 5", "Period 6", "Period 7", "Period 8"}, 0));
        add(new JScrollPane(timetableTable), BorderLayout.CENTER);
        timetableTable.setRowHeight(70);
        // Initialize database connection
        initializeDatabaseConnection();

        // Populate the combo boxes
        populateComboBoxes();

        // Add action listeners
       backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new TimetableManager().setVisible(true);;
            	dispose();
            }
        });

        exporttoexcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


            	        Workbook workbook = new XSSFWorkbook();
            	        Sheet sheet = workbook.createSheet("Timetable");

            	        // Create header row
            	        Row headerRow = sheet.createRow(0);
            	        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            	            Cell cell = headerRow.createCell(i);
            	            cell.setCellValue(timetableTable.getColumnName(i));
            	        }

            	        // Create data rows
            	        for (int i = 0; i < timetableTable.getRowCount(); i++) {
            	            Row row = sheet.createRow(i + 1);
            	            for (int j = 0; j < timetableTable.getColumnCount(); j++) {
            	                Cell cell = row.createCell(j);
            	                cell.setCellValue(timetableTable.getValueAt(i, j).toString());
            	            }
            	        }

            	        try (FileOutputStream fileOut = new FileOutputStream("Timetable.xlsx")) {
            	            workbook.write(fileOut);
            	            workbook.close();
            	            JOptionPane.showMessageDialog(null, "Excel file exported successfully!");
            	        } catch (IOException ex) {
            	            ex.printStackTrace();
            	            JOptionPane.showMessageDialog(null, "Error exporting Excel file: " + ex.getMessage());
            	        }
            	    }
            	});
	
            	
            	
            	
        
        
        exporttopdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
Document document = new Document();
            	        try {
            	            PdfWriter.getInstance(document, new FileOutputStream("Timetable.pdf"));
            	       //     PdfWriter.getInstance(document, new FileOutputStream("C:/Users/YourUsername/Documents/Timetable.pdf"));

            	            
            	            document.open();

            	            PdfPTable pdfTable = new PdfPTable(timetableTable.getColumnCount());
            	            // Add table headers
            	            for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            	                pdfTable.addCell(timetableTable.getColumnName(i));
            	            }

            	            // Add table rows
            	            for (int rows = 0; rows < timetableTable.getRowCount(); rows++) {
            	                for (int cols = 0; cols < timetableTable.getColumnCount(); cols++) {
            	                    pdfTable.addCell(timetableTable.getValueAt(rows, cols).toString());
            	                }
            	            }

            	            document.add(new Paragraph("Timetable"));
            	            document.add(pdfTable);
            	            document.close();

            	            JOptionPane.showMessageDialog(null, "PDF exported successfully!");
            	        } catch (DocumentException | IOException ex) {
            	            ex.printStackTrace();
            	            JOptionPane.showMessageDialog(null, "Error exporting PDF: " + ex.getMessage());
            	        }
            	    }
            	});

            	
            	
            	
            	
        
        
        
        teacherFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // Load the timetable initially
        refreshTable();
    }

    private void initializeDatabaseConnection() {
        try {
            // Replace with your database connection details
            String url = "jdbc:mysql://localhost:3306/timetable_manager"
            		+ "";
            String user = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database");
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
            teacherFilterComboBox.removeAllItems();
            teacherFilterComboBox.addItem("All Teachers");
            while (rs.next()) {
                String teacherName = rs.getString("name");
                teacherComboBox.addItem(teacherName);
                teacherFilterComboBox.addItem(teacherName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPeriod() {
        try {
            String branch = (String) branchComboBox.getSelectedItem();
            String subject = (String) subjectComboBox.getSelectedItem();
            String teacher = (String) teacherComboBox.getSelectedItem();

            int branchId = getIdFromName("branches", branch);
            int subjectId = getIdFromName("subjects", subject);
            int teacherId = getIdFromName("teachers", teacher);

            String sql = "INSERT INTO periods (branch_id, subject_id, teacher_id, day, period_no) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ps.setInt(2, subjectId);
            ps.setInt(3, teacherId);
            ps.setString(4, "Monday"); // Example, you may want to get this from a user input
           ps.setInt(5, 1); // Example, you may want to get this from a user input
            ps.executeUpdate();

            refreshTable();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding period");
        }
    }

    private int getIdFromName(String tableName, String name) throws SQLException {
        String sql = "SELECT id FROM " + tableName + " WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("No record found for " + name);
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

            // Fetch the selected teacher
            String selectedTeacher = (String) teacherFilterComboBox.getSelectedItem();

            // Build the SQL query based on the selected teacher
            String query = "SELECT p.period_no, p.day, b.name AS branch, s.name AS subject, t.name AS teacher " +
                           "FROM periods p " +
                           "JOIN branches b ON p.branch_id = b.id " +
                           "JOIN subjects s ON p.subject_id = s.id " +
                           "JOIN teachers t ON p.teacher_id = t.id";

            if (!"All Teachers".equals(selectedTeacher)) {
                query += " WHERE t.name = ?";
            }

            PreparedStatement ps = connection.prepareStatement(query);
            if (!"All Teachers".equals(selectedTeacher)) {
                ps.setString(1, selectedTeacher);
            }

            ResultSet rs = ps.executeQuery();

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new specific_teacherui().setVisible(true);
            }
        });
    }
}
