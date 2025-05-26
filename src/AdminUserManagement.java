package hms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminUserManagement extends JFrame {

    private Connection conn;
    private DefaultTableModel model;
    private JTable userTable;

    public AdminUserManagement() {
        setTitle("Admin - Manage Users");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();
        
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel with Add User and Go Back buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Go Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(e -> {
            dispose(); // Close the current window
            new Dashboard().setVisible(true); // Open the dashboard
        });
        topPanel.add(backButton);
        
        JButton addUserButton = new JButton("Add User");
        addUserButton.setFont(new Font("Arial", Font.BOLD, 18));
        addUserButton.addActionListener(e -> new AddUserForm(this).setVisible(true));
        topPanel.add(addUserButton);

        // Filter dropdown
        String[] filterOptions = {"All", "Admins", "Users"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        filterComboBox.addActionListener(e -> {
            String selected = (String) filterComboBox.getSelectedItem();
            loadUserData(selected);
        });
        topPanel.add(new JLabel(" Filter: "));
        topPanel.add(filterComboBox);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table model
        model = new DefaultTableModel(new Object[]{"Username", "Password", "Administrator", "Edit", "Delete"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Only Edit and Delete columns
            }
        };

        userTable = new JTable(model);
        userTable.setRowHeight(30);

        //button renderer and editor for the "Edit" and "Delete" columns
        userTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));
        userTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        loadUserData("All");
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    public void loadUserData(String filter) {
        model.setRowCount(0);
        try {
            String query = "SELECT * FROM user";
            if (filter.equals("Admins")) {
                query += " WHERE administrator = TRUE";
            } else if (filter.equals("Users")) {
                query += " WHERE administrator = FALSE";
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getBoolean("administrator") ? "Yes" : "No",
                    "Edit",
                    "Delete"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load user data", "Error", JOptionPane.ERROR_MESSAGE);
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

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Determine action based on label (Edit or Delete)
                    int row = userTable.getSelectedRow();
                    if (label.equals("Edit")) {
                        String username = (String) model.getValueAt(row, 0);
                        String password = (String) model.getValueAt(row, 1);
                        boolean isAdmin = (model.getValueAt(row, 2).equals("Yes"));
                        new EditUserForm(AdminUserManagement.this, username, password, isAdmin).setVisible(true);
                    } else if (label.equals("Delete")) {
                        String username = (String) model.getValueAt(row, 0);
                        int confirm = JOptionPane.showConfirmDialog(null, "Delete user: " + username + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            deleteUser(username);
                        }
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

    private void deleteUser(String username) {
        try {
            String query = "DELETE FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUserData("All");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminUserManagement().setVisible(true));
    }
}