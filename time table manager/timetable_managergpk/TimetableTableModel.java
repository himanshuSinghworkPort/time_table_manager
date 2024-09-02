package timetable_managergpk;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;


import javax.swing.table.DefaultTableModel;

public class TimetableTableModel extends DefaultTableModel {

    private static final String[] COLUMN_NAMES = {
        "Period No", "Day", "Branch", "Subject", "Teacher"
    };

    private static final int ROW_COUNT = 6; // Assuming 6 periods or days in the week

    public TimetableTableModel() {
        super(COLUMN_NAMES, ROW_COUNT);
        // Initialize with empty cells
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_NAMES.length; col++) {
                setValueAt("", row, col);
            }
        }
    }
}
