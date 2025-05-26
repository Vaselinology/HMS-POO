package hms;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCustomerForm extends JDialog {

    private JTextField nameField, bookingIdField, roomField, contactField;
    private JComboBox<String> genderBox;
    private JDateChooser checkInPicker, checkOutPicker;
    private Connection conn;
    private AdminCustomerManagement parent;

    public AddCustomerForm(AdminCustomerManagement parent) {
        super(parent, "Add Customer", true);
        this.parent = parent;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(8, 2, 10, 10));

        connectToDatabase();

        bookingIdField = createLabeledField("Booking ID:");
        nameField = createLabeledField("Name:");

        add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female"});
        add(genderBox);

        contactField = createLabeledField("Contact:");
        roomField = createLabeledField("Room Number:");

        add(new JLabel("Check-in Date:"));
        checkInPicker = new JDateChooser();
        checkInPicker.setDateFormatString("yyyy-MM-dd");
        add(checkInPicker);

        add(new JLabel("Checkout Date:"));
        checkOutPicker = new JDateChooser();
        checkOutPicker.setDateFormatString("yyyy-MM-dd");
        add(checkOutPicker);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addCustomer());
        add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private JTextField createLabeledField(String label) {
        JLabel jLabel = new JLabel(label);
        JTextField field = new JTextField();
        add(jLabel);
        add(field);
        return field;
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void addCustomer() {
        try {
            conn.setAutoCommit(false); // Start transaction

            int bookingId = Integer.parseInt(bookingIdField.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String checkInDate = sdf.format(checkInPicker.getDate());
            String checkOutDate = sdf.format(checkOutPicker.getDate());
            //insert into booking 
            String insertBooking = "INSERT INTO booking (bookingid, roomid, checkin, checkout) VALUES (?, ?, ?, ?)";
            PreparedStatement bookingStmt = conn.prepareStatement(insertBooking);
            bookingStmt.setInt(1, bookingId);
            bookingStmt.setString(2, roomField.getText());
            bookingStmt.setString(3, checkInDate);
            bookingStmt.setString(4, checkOutDate);
            bookingStmt.executeUpdate();

            //insert into customer
            String insertCustomer = "INSERT INTO customer (bookingid, name, gender, contact) VALUES (?, ?, ?, ?)";
            PreparedStatement customerStmt = conn.prepareStatement(insertCustomer);
            customerStmt.setInt(1, bookingId);
            customerStmt.setString(2, nameField.getText());
            customerStmt.setString(3, genderBox.getSelectedItem().toString());
            customerStmt.setString(4, contactField.getText());
            customerStmt.executeUpdate();

            conn.commit(); // Commit transaction
            JOptionPane.showMessageDialog(this, "Customer and booking added successfully!");
            parent.loadCustomerData();
            dispose();

        } catch (SQLException | NumberFormatException | NullPointerException e) {
            try {
                conn.rollback(); // Roll back on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conn.setAutoCommit(true); // Restore autocommit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
