package hms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminEmployeeManagement extends JFrame {

    private Connection conn;
    private DefaultTableModel model;
    private JTable employeeTable;
    private JTextField searchField;

    public AdminEmployeeManagement() {
        setTitle("Admin - Manage Employees");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();

        JPanel panel = new JPanel(new BorderLayout());

        // Top panel with buttons and search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //go back button
        JButton backButton = new JButton("Go Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard().setVisible(true);
        });
        topPanel.add(backButton);

        JButton addButton = new JButton("Add Employee");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.addActionListener(e -> new AddEmployeeForm(this).setVisible(true));
        topPanel.add(addButton);

        topPanel.add(new JLabel(" Search: "));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.addActionListener(e -> searchEmployees(searchField.getText().trim()));
        topPanel.add(searchButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table setup
        employeeTable = new JTable();

        model = new DefaultTableModel(new Object[]{
            "ID", "Name", "Age", "Gender", "Job", "Salary", "Phone", "Email", "Edit", "Delete"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 8 || column == 9;
            }
        };
        employeeTable.setModel(model);
        employeeTable.setRowHeight(30);

        // Add buttons to table
        employeeTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        employeeTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));
        employeeTable.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
        employeeTable.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadEmployeeData();

        add(panel);
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadEmployeeData() {
        searchEmployees(""); // Load all
    }

    public void searchEmployees(String keyword) {
        model.setRowCount(0);
        try {
            String query = "SELECT id, name, age, gender, job, salary, phone, email FROM employee " +
               "WHERE CONCAT(COALESCE(id, ''), ' ', COALESCE(name, ''), ' ', COALESCE(job, ''), ' ', COALESCE(phone, ''), ' ', COALESCE(email, '')) LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("job"),
                    rs.getDouble("salary"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    "Edit",
                    "Delete"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load employee data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private String label;
        private JButton button;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped(); 
                int id = (int) model.getValueAt(currentRow, 0);
                if (label.equals("Edit")) {
                    String name = (String) model.getValueAt(currentRow, 1);
                    int age = (int) model.getValueAt(currentRow, 2);
                    String gender = (String) model.getValueAt(currentRow, 3);
                    String job = (String) model.getValueAt(currentRow, 4);
                    double salary = (double) model.getValueAt(currentRow, 5);
                    String phone = (String) model.getValueAt(currentRow, 6);
                    String email = (String) model.getValueAt(currentRow, 7);
                    new EditEmployeeForm(AdminEmployeeManagement.this, id, name, age, gender, job, salary, phone, email).setVisible(true);
                } else if (label.equals("Delete")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Delete employee with ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteEmployee(id);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void deleteEmployee(int id) {
        try {
            String query = "DELETE FROM employee WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting Employee!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminEmployeeManagement().setVisible(true));
    }
}
