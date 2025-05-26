package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditEmployeeForm extends JDialog {

    private JTextField nameField, ageField, jobField, salaryField, phoneField, emailField;
    private JComboBox<String> genderCombo;
    private int employeeId;
    private Connection conn;
    private AdminEmployeeManagement parent;

    public EditEmployeeForm(AdminEmployeeManagement parent, int id, String name, int age, String gender, String job, double salary, String phone, String email) {
        super(parent, "Edit Employee", true);
        this.parent = parent;
        this.employeeId = id;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(9, 2, 10, 10));

        connectToDatabase();

        // Form Fields
        add(new JLabel("Name:"));
        nameField = new JTextField(name);
        add(nameField);

        add(new JLabel("Age:"));
        ageField = new JTextField(String.valueOf(age));
        add(ageField);

        add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(gender);
        add(genderCombo);

        add(new JLabel("Job:"));
        jobField = new JTextField(job);
        add(jobField);

        add(new JLabel("Salary:"));
        salaryField = new JTextField(String.valueOf(salary));
        add(salaryField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField(phone);
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField(email);
        add(emailField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveEmployee());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveEmployee() {
        try {
            String query = "UPDATE employee SET name=?, age=?, gender=?, job=?, salary=?, phone=?, email=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nameField.getText().trim());
            stmt.setInt(2, Integer.parseInt(ageField.getText().trim()));
            stmt.setString(3, (String) genderCombo.getSelectedItem());
            stmt.setString(4, jobField.getText().trim());
            stmt.setDouble(5, Double.parseDouble(salaryField.getText().trim()));
            stmt.setString(6, phoneField.getText().trim());
            stmt.setString(7, emailField.getText().trim());
            stmt.setInt(8, employeeId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
            parent.loadEmployeeData();
            dispose();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
