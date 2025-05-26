package hms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminCustomerManagement extends JFrame {

    private Connection conn;
    private DefaultTableModel model;
    private JTable customerTable;

    public AdminCustomerManagement() {
        setTitle("Admin - Manage Customers");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();

        JPanel panel = new JPanel(new BorderLayout());
        // Top panel with search, go back, and add customer buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchCustomers(searchField.getText().trim()));

        JButton clearSearchButton = new JButton("Clear");
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomerData();
        });

        JButton goBackButton = new JButton("Go Back");
        goBackButton.setFont(new Font("Arial", Font.BOLD, 18));
        goBackButton.addActionListener(e -> {
            dispose();
            new Dashboard().setVisible(true);
        });

        JButton addButton = new JButton("Add Customer");
        addButton.setFont(new Font("Arial", Font.BOLD, 18));
        addButton.addActionListener(e -> new AddCustomerForm(this).setVisible(true));

        // Add to top panel
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearSearchButton);
        topPanel.add(goBackButton);
        topPanel.add(addButton);

        panel.add(topPanel, BorderLayout.NORTH);


        // Table setup
        model = new DefaultTableModel(new Object[]{
            "Booking ID", "User ID", "Name", "Gender", "Contact", "Edit", "Delete"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return column >= 5;
            }
        };

        customerTable = new JTable(model);
        customerTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadCustomerData();
        addButtonsToTable();
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

    public void loadCustomerData() {
        model.setRowCount(0); // Clear existing data
        try {
            String query = "SELECT bookingid, userid, name, gender, contact FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bookingid"),
                    rs.getInt("userid"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("contact"),
                    "Edit", "Delete"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load customer data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addButtonsToTable() {
        customerTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        customerTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));

        customerTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));
        customerTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private int row;
        private String type;

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.type = type;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                row = customerTable.getSelectedRow();
                int bookingId = (int) model.getValueAt(row, 0);

                if ("Edit".equals(type)) {
                    String name = (String) model.getValueAt(row, 2);
                    String gender = (String) model.getValueAt(row, 3);
                    String contact = (String) model.getValueAt(row, 4);

                    new EditCustomerForm(AdminCustomerManagement.this, bookingId, name, gender, contact).setVisible(true);
                } else if ("Delete".equals(type)) {
                    int confirm = JOptionPane.showConfirmDialog(
                            AdminCustomerManagement.this,
                            "Are you sure you want to delete booking ID " + bookingId + "?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteCustomer(bookingId);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

    private void deleteCustomer(int bookingId) {
        try {
            String query = "DELETE FROM customer WHERE bookingid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting customer", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchCustomers(String keyword) {
        model.setRowCount(0); // Clear table

        String query = "SELECT bookingid, userid, name, gender, contact FROM customer " +
                       "WHERE CAST(bookingid AS CHAR) LIKE ? OR CAST(userid AS CHAR) LIKE ? OR name LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bookingid"),
                    rs.getInt("userid"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("contact"),
                    "Edit", "Delete"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminCustomerManagement().setVisible(true));
    }
}
 