package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddEmployeeForm extends JDialog {

    private JTextField nameField, ageField, jobField, salaryField, phoneField, emailField;
    private JComboBox<String> genderBox;
    private Connection conn;
    private AdminEmployeeManagement parent;

    public AddEmployeeForm(AdminEmployeeManagement parent) {
        super(parent, "Add Employee", true);
        this.parent = parent;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        connectToDatabase();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField();
        formPanel.add(nameField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField();
        formPanel.add(ageField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderBox, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Job:"), gbc);
        gbc.gridx = 1;
        jobField = new JTextField();
        formPanel.add(jobField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        salaryField = new JTextField();
        formPanel.add(salaryField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField();
        formPanel.add(phoneField, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField();
        formPanel.add(emailField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> addEmployee());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void addEmployee() {
        String name = nameField.getText().trim();
        String job = jobField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        int age;
        double salary;

        try {
            age = Integer.parseInt(ageField.getText().trim());
            salary = Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for age and salary.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO employee (name, age, gender, job, salary, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, gender);
            stmt.setString(4, job);
            stmt.setDouble(5, salary);
            stmt.setString(6, phone);
            stmt.setString(7, email);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            parent.loadEmployeeData();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
