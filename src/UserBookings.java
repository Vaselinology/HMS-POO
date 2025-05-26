package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserBookings extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public UserBookings(String username) {
        setTitle("Your Bookings");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(
            new String[]{"Booking ID", "Room", "Check-in", "Check-out"}, 0
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel Selected Booking");
        cancelButton.addActionListener(e -> cancelBooking(username));
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.addActionListener(e -> {
            dispose();
            new UserDashboard(username).setVisible(true);
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        topPanel.add(cancelButton);
        add(topPanel, BorderLayout.NORTH);

        loadBookings(username);
    }

    private void loadBookings(String username) {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "");
             PreparedStatement getUserIdStmt = con.prepareStatement("SELECT id FROM user WHERE username = ?");
             PreparedStatement bookingStmt = con.prepareStatement(
                 "SELECT bookingid, roomid, checkin, checkout FROM booking WHERE userid = ?")) {

            getUserIdStmt.setString(1, username);
            ResultSet userRs = getUserIdStmt.executeQuery();

            if (userRs.next()) {
                int userId = userRs.getInt("id");

                bookingStmt.setInt(1, userId);
                ResultSet bookingRs = bookingStmt.executeQuery();

                while (bookingRs.next()) {
                    int bookingId = bookingRs.getInt("bookingid");
                    String room = bookingRs.getString("roomid");
                    String checkin = bookingRs.getString("checkin");
                    String checkout = bookingRs.getString("checkout");

                    model.addRow(new Object[]{bookingId, room, checkin, checkout});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }

    private void cancelBooking(String username) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) model.getValueAt(selectedRow, 0);
        String roomId = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "")) {
            con.setAutoCommit(false);

            try (
                PreparedStatement deleteCustomerStmt = con.prepareStatement("DELETE FROM customer WHERE bookingid = ?");
                PreparedStatement deleteBookingStmt = con.prepareStatement("DELETE FROM booking WHERE bookingid = ?");
                PreparedStatement updateRoomStmt = con.prepareStatement("UPDATE room SET availability = 'Available' WHERE roomnumber = ?")
            ) {
                // Delete from customer
                deleteCustomerStmt.setInt(1, bookingId);
                deleteCustomerStmt.executeUpdate();

                // Delete from booking
                deleteBookingStmt.setInt(1, bookingId);
                deleteBookingStmt.executeUpdate();

                // Update room to available
                updateRoomStmt.setString(1, roomId);
                updateRoomStmt.executeUpdate();

                con.commit();
                JOptionPane.showMessageDialog(this, "Booking cancelled.");
                loadBookings(username);

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cancelling booking: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
