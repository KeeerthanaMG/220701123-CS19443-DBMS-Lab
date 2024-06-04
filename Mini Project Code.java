import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class blood_donate {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            String jdbcURL = "jdbc:mysql://localhost:3306/blood_donation";
            String dbUsername = "root";
            String dbPassword = "0709";
            connection = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);

            createTableIfNotExists(connection);

            SwingUtilities.invokeLater(() -> createAndShowMainGUI());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createDonorsTableSQL = "CREATE TABLE IF NOT EXISTS donors ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "blood_group VARCHAR(5) NOT NULL,"
                + "location VARCHAR(255) NOT NULL,"
                + "mobile_number VARCHAR(15) NOT NULL"
                + ")";
        
        String createDonorStatusTableSQL = "CREATE TABLE IF NOT EXISTS donor_status ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "donor_id INT,"
                + "last_donation_month VARCHAR(20),"
                + "eligibility VARCHAR(20),"
                + "FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createDonorsTableSQL);
            statement.executeUpdate(createDonorStatusTableSQL);
        }
    }

    private static void createAndShowMainGUI() {
        JFrame frame = new JFrame("Blood Donation Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        JButton registerButton = new JButton("Register Donor");
        JButton findButton = new JButton("Find Donors by Blood Group");
        JButton deleteButton = new JButton("Delete Donor by Name");
        JButton updateButton = new JButton("Update Donor Information");
        JButton enterStatusButton = new JButton("Enter Donor Status");
        JButton viewStatusButton = new JButton("View Donor Status");
        JButton exitButton = new JButton("Exit");

        panel.add(registerButton);
        panel.add(findButton);
        panel.add(deleteButton);
        panel.add(updateButton);
        panel.add(enterStatusButton);
        panel.add(viewStatusButton);
        panel.add(exitButton);

        registerButton.addActionListener(e -> showRegisterDonorWindow());
        findButton.addActionListener(e -> showFindDonorsWindow());
        deleteButton.addActionListener(e -> showDeleteDonorWindow());
        updateButton.addActionListener(e -> showUpdateDonorWindow());
        enterStatusButton.addActionListener(e -> showEnterDonorStatusWindow());
        viewStatusButton.addActionListener(e -> showViewDonorStatusWindow());
        exitButton.addActionListener(e -> System.exit(0));

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showRegisterDonorWindow() {
        JFrame frame = new JFrame("Register Donor");
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JTextField nameField = new JTextField();
        JTextField bloodGroupField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField mobileNumberField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Blood Group:"));
        panel.add(bloodGroupField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Mobile Number:"));
        panel.add(mobileNumberField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            registerDonor(nameField.getText(), bloodGroupField.getText(), locationField.getText(), mobileNumberField.getText());
            frame.dispose();
        });

        panel.add(registerButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showFindDonorsWindow() {
        JFrame frame = new JFrame("Find Donors by Blood Group");
        frame.setSize(300, 150);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JTextField bloodGroupField = new JTextField();

        panel.add(new JLabel("Blood Group:"));
        panel.add(bloodGroupField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> {
            findDonorsByBloodGroup(bloodGroupField.getText());
            frame.dispose();
        });

        panel.add(findButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showDeleteDonorWindow() {
        JFrame frame = new JFrame("Delete Donor by Name");
        frame.setSize(300, 150);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JTextField nameField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            deleteDonorByName(nameField.getText());
            frame.dispose();
        });

        panel.add(deleteButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showUpdateDonorWindow() {
        JFrame frame = new JFrame("Update Donor Information");
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JTextField donorNameField = new JTextField();
        JTextField newNameField = new JTextField();
        JTextField newBloodGroupField = new JTextField();
        JTextField newLocationField = new JTextField();
        JTextField newMobileNumberField = new JTextField();

        panel.add(new JLabel("Donor Name:"));
        panel.add(donorNameField);
        panel.add(new JLabel("New Name:"));
        panel.add(newNameField);
        panel.add(new JLabel("New Blood Group:"));
        panel.add(newBloodGroupField);
        panel.add(new JLabel("New Location:"));
        panel.add(newLocationField);
        panel.add(new JLabel("New Mobile Number:"));
        panel.add(newMobileNumberField);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            updateDonorInformation(
                    donorNameField.getText(),
                    newNameField.getText(),
                    newBloodGroupField.getText(),
                    newLocationField.getText(),
                    newMobileNumberField.getText()
            );
            frame.dispose();
        });

        panel.add(updateButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showEnterDonorStatusWindow() {
        JFrame frame = new JFrame("Enter Donor Status");
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JTextField nameField = new JTextField();
        JTextField bloodGroupField = new JTextField();
        JTextField lastDonationMonthField = new JTextField();
        JTextField eligibilityField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Blood Group:"));
        panel.add(bloodGroupField);
        panel.add(new JLabel("Last Donation Month:"));
        panel.add(lastDonationMonthField);
        panel.add(new JLabel("Eligibility:"));
        panel.add(eligibilityField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            enterDonorStatus(nameField.getText(), bloodGroupField.getText(), lastDonationMonthField.getText(), eligibilityField.getText());
            frame.dispose();
        });

        panel.add(saveButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void showViewDonorStatusWindow() {
        JFrame frame = new JFrame("View Donor Status");
        frame.setSize(300, 150);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField nameField = new JTextField();
        JTextField bloodGroupField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Blood Group:"));
        panel.add(bloodGroupField);

        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> {
            viewDonorStatus(nameField.getText(), bloodGroupField.getText());
            frame.dispose();
        });

        panel.add(viewButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    private static void registerDonor(String name, String bloodGroup, String location, String mobileNumber) {
        try {
            String insertSQL = "INSERT INTO donors (name, blood_group, location, mobile_number) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, bloodGroup);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, mobileNumber);

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Donor registered successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void findDonorsByBloodGroup(String bloodGroup) {
        try {
            String selectSQL = "SELECT * FROM donors WHERE blood_group = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, bloodGroup);
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder result = new StringBuilder();
                if (resultSet.next()) {
                    result.append("\nDonors with blood group ").append(bloodGroup).append(":\n");
                    result.append(String.format("%-20s %-15s %-15s %-15s\n", "Name", "Blood Group", "Location", "Mobile Number"));
                    do {
                        result.append(String.format("%-20s %-15s %-15s %-15s\n",
                                resultSet.getString("name"),
                                resultSet.getString("blood_group"),
                                resultSet.getString("location"),
                                resultSet.getString("mobile_number")));
                    } while (resultSet.next());
                } else {
                    result.append("No donors found with blood group ").append(bloodGroup).append("\n");
                }
                JOptionPane.showMessageDialog(null, result.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteDonorByName(String donorName) {
        try {
            String deleteSQL = "DELETE FROM donors WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setString(1, donorName);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Donor with name " + donorName + " deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "No donor found with name " + donorName);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateDonorInformation(String donorName, String newName, String newBloodGroup, String newLocation, String newMobileNumber) {
        try {
            String updateSQL = "UPDATE donors SET name=?, blood_group=?, location=?, mobile_number=? WHERE name=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, newBloodGroup);
                preparedStatement.setString(3, newLocation);
                preparedStatement.setString(4, newMobileNumber);
                preparedStatement.setString(5, donorName);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Donor with name " + donorName + " updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "No donor found with name " + donorName);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void enterDonorStatus(String name, String bloodGroup, String lastDonationMonth, String eligibility) {
        try {
            String selectSQL = "SELECT id FROM donors WHERE name = ? AND blood_group = ?";
            int donorId = -1;
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, bloodGroup);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    donorId = resultSet.getInt("id");
                } else {
                    JOptionPane.showMessageDialog(null, "No donor found with the provided name and blood group.");
                    return;
                }
            }

            String insertSQL = "INSERT INTO donor_status (donor_id, last_donation_month, eligibility) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setInt(1, donorId);
                preparedStatement.setString(2, lastDonationMonth);
                preparedStatement.setString(3, eligibility);

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Donor status entered successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void viewDonorStatus(String name, String bloodGroup) {
        try {
            String selectSQL = "SELECT donors.name, donors.blood_group, donor_status.last_donation_month, donor_status.eligibility "
                    + "FROM donors JOIN donor_status ON donors.id = donor_status.donor_id "
                    + "WHERE donors.name = ? AND donors.blood_group = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, bloodGroup);
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder result = new StringBuilder();
                if (resultSet.next()) {
                    result.append("Donor Status:\n");
                    result.append("Name: ").append(resultSet.getString("name")).append("\n");
                    result.append("Blood Group: ").append(resultSet.getString("blood_group")).append("\n");
                    result.append("Last Donation Month: ").append(resultSet.getString("last_donation_month")).append("\n");
                    result.append("Eligibility: ").append(resultSet.getString("eligibility")).append("\n");
                } else {
                    result.append("No status found for donor with the provided name and blood group.");
                }
                JOptionPane.showMessageDialog(null, result.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
