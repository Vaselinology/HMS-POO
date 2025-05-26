package hms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminRoomManagement extends JFrame {

    private Connection conn;
    private DefaultTableModel model;
    private JTable roomTable;

    public AdminRoomManagement() {
        setTitle("Admin - Manage Rooms");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        connectToDatabase();

        JPanel panel = new JPanel(new BorderLayout());

        // Header panel with buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Go Back button
        JButton backButton = new JButton("Go Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard().setVisible(true);
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        // Add Room Button
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(addRoomButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table and model
        model = new DefaultTableModel(
                new Object[]{"Room Number", "Availability", "Cleaning Status", "Price", "Bed Type", "Edit", "Delete"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column >= 5; // Only the "Edit" and "Delete" buttons are clickable
            }
        };

        roomTable = new JTable(model);
        roomTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        addButtonsToTable(); // Attach Edit and Delete buttons
        loadRoomData();     // Populate the table

        // Add Room action
        addRoomButton.addActionListener(e -> new AddRoomForm(AdminRoomManagement.this).setVisible(true));
        add(panel);
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void loadRoomData() {
        model.setRowCount(0);
        try {
            String query = "SELECT * FROM room";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String roomNumber = rs.getString("roomnumber");
                String availability = rs.getString("availability");
                String cleaningStatus = rs.getString("cleaning_status");
                double price = rs.getDouble("price");
                String bedType = rs.getString("bed_type");

                model.addRow(new Object[]{roomNumber, availability, cleaningStatus, price, bedType, "Edit", "Delete"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading rooms!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshRoomTable() {
        loadRoomData();  
    }

    private void addButtonsToTable() {
        // Edit button
        roomTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Edit"));
        roomTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));
        
        // Delete button
        roomTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("Delete"));
        roomTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private int row;
        private String buttonType;

        public ButtonEditor(JCheckBox checkBox, String buttonType) {
            super(checkBox);
            this.buttonType = buttonType;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    row = roomTable.getSelectedRow();
                    String roomNumber = (String) model.getValueAt(row, 0);
                    
                    if (buttonType.equals("Edit")) {
                        // Retrieve room data from the table when "Edit" is clicked
                        String availability = (String) model.getValueAt(row, 1);
                        String cleaningStatus = (String) model.getValueAt(row, 2);
                        double price = (double) model.getValueAt(row, 3);
                        String bedType = (String) model.getValueAt(row, 4);

                        // Open the EditRoomForm with the room data
                        new EditRoomForm(AdminRoomManagement.this, roomNumber, availability, cleaningStatus, price, bedType).setVisible(true);
                    } else if (buttonType.equals("Delete")) {
                        // Handle delete action
                        int confirm = JOptionPane.showConfirmDialog(
                            AdminRoomManagement.this, 
                            "Are you sure you want to delete room " + roomNumber + "?", 
                            "Confirm Delete", 
                            JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            deleteRoom(roomNumber);
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    private void deleteRoom(String roomNumber) {
        try {
            String query = "DELETE FROM room WHERE roomnumber = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomNumber);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshRoomTable();
            } else {
                JOptionPane.showMessageDialog(this, "Room not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting room!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminRoomManagement().setVisible(true));
    }
}