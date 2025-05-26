package hms;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookRoomForm extends JFrame {

    private JTextField nameField, contactField, depositField;
    private JComboBox<String> genderBox;
    private JDateChooser checkinDatePicker, checkoutDatePicker;

    public BookRoomForm(String username, String roomType) {
        setTitle("Book Room - " + roomType);
        setSize(450, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // User info
        panel.add(new JLabel("User:"));
        panel.add(new JLabel(username));

        panel.add(new JLabel("Requested Room Type:"));
        panel.add(new JLabel(roomType));

        // Customer info
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        panel.add(genderBox);

        panel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        panel.add(contactField);

        panel.add(new JLabel("Check-in Date:"));
        checkinDatePicker = new JDateChooser();
        checkinDatePicker.setDateFormatString("yyyy-MM-dd");
        panel.add(checkinDatePicker);

        panel.add(new JLabel("Check-out Date:"));
        checkoutDatePicker = new JDateChooser();
        checkoutDatePicker.setDateFormatString("yyyy-MM-dd");
        panel.add(checkoutDatePicker);

        // Buttons
        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(e -> saveBooking(username, roomType));
        panel.add(bookButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        add(panel);
    }



    private void saveBooking(String username, String roomType) {
        String name = nameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String contact = contactField.getText().trim();
        Date checkinDate = checkinDatePicker.getDate();
        Date checkoutDate = checkoutDatePicker.getDate();

        if (name.isEmpty() || contact.isEmpty() || checkinDate == null || checkoutDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "")) {
            con.setAutoCommit(false);

            try (
                PreparedStatement getUserIdStmt = con.prepareStatement("SELECT id FROM user WHERE username = ?");
                PreparedStatement findRoomStmt = con.prepareStatement("SELECT roomnumber FROM room WHERE bed_type = ? AND availability = 'Available' AND cleaning_status = 'Clean' LIMIT 1");
                PreparedStatement insertBookingStmt = con.prepareStatement("INSERT INTO booking (userid, roomid, checkin, checkout) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement insertCustomerStmt = con.prepareStatement("INSERT INTO customer (bookingid, name, gender, contact, userid) VALUES (?, ?, ?, ?, ?)");
                PreparedStatement updateRoomStmt = con.prepareStatement("UPDATE room SET availability = 'Occupied' WHERE roomnumber = ?")
            ) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String checkin = sdf.format(checkinDate);
                String checkout = sdf.format(checkoutDate);

                // Get user ID
                getUserIdStmt.setString(1, username);
                ResultSet userRs = getUserIdStmt.executeQuery();

                if (userRs.next()) {
                    int userId = userRs.getInt("id");

                    // Find available & clean room
                    findRoomStmt.setString(1, capitalize(roomType));
                    ResultSet roomRs = findRoomStmt.executeQuery();

                    if (roomRs.next()) {
                        String availableRoom = roomRs.getString("roomnumber");

                        // Insert into booking
                        insertBookingStmt.setInt(1, userId);
                        insertBookingStmt.setString(2, availableRoom);
                        insertBookingStmt.setString(3, checkin);
                        insertBookingStmt.setString(4, checkout);
                        insertBookingStmt.executeUpdate();

                        ResultSet keys = insertBookingStmt.getGeneratedKeys();
                        if (keys.next()) {
                            int bookingId = keys.getInt(1);

                            // Insert into customer
                            insertCustomerStmt.setInt(1, bookingId);
                            insertCustomerStmt.setString(2, name);
                            insertCustomerStmt.setString(3, gender);
                            insertCustomerStmt.setString(4, contact);
                            insertCustomerStmt.setInt(5, userId);
                            insertCustomerStmt.executeUpdate();

                            // Update room availability
                            updateRoomStmt.setString(1, availableRoom);
                            updateRoomStmt.executeUpdate();

                            con.commit();
                            JOptionPane.showMessageDialog(this, "Room booked successfully!");
                            dispose();
                        } else {
                            con.rollback();
                            JOptionPane.showMessageDialog(this, "Booking ID retrieval failed.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No available and clean " + roomType + " room found.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found.");
                }

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during booking: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage());
        }
    }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

