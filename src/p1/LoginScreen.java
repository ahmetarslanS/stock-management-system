package p1;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;


public class LoginScreen extends JFrame implements ActionListener {

    //Declaring GUI components
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    //Declaring JDBC Variables
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public LoginScreen() {
        //Initializing GUI components
        lblUsername = new JLabel("Username: ");
        // Set the minimum size of the label to 300x20
        lblUsername.setMinimumSize(new Dimension(300, 20));
        txtUsername = new JTextField(20);
        // Set the minimum size of the text field to 300x20
        txtUsername.setMinimumSize(new Dimension(300, 20));
        lblPassword = new JLabel("Password: ");
        // Set the minimum size of the text field to 300x20
        lblPassword.setMinimumSize(new Dimension(300, 20));
        txtPassword = new JPasswordField(20);
        // Set the minimum size of the text field to 300x20
        txtPassword.setMinimumSize(new Dimension(300, 20));
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);

        //Create a JLabel with an image
        ImageIcon icon = new ImageIcon("bg.jpg");
        JLabel label = new JLabel(icon);

        setSize(500, 500);

        lblUsername.setForeground(Color.BLUE);
        lblPassword.setForeground(Color.BLUE);
        lblUsername.setFont(new Font(lblUsername.getFont().getName(), lblUsername.getFont().getStyle(), 15));
        lblPassword.setFont(new Font(lblPassword.getFont().getName(), lblPassword.getFont().getStyle(), 15));

        // Create a panel for the buttons with FlowLayout and add the buttons to it
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtons.add(btnLogin);
        panelButtons.add(btnRegister);


      /*  //Adding the Action Listener to the login button
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this); */

        // Create a panel for the grid layout with GridBagLayout
        JPanel panelGrid = new JPanel(new GridBagLayout());

        // Set the constraints for the label and text field
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        // Add the label and text field to the panel
        panelGrid.add(lblUsername, constraints);
        constraints.gridx = 1;
        panelGrid.add(txtUsername, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panelGrid.add(lblPassword, constraints);
        constraints.gridx = 1;
        panelGrid.add(txtPassword, constraints);

        // Create a panel with BorderLayout
        JPanel panelBorder = new JPanel(new BorderLayout());
        // Add the panel for the grid layout to the center region of the panel
        panelBorder.add(panelGrid, BorderLayout.CENTER);
        // Add the panel for the buttons to the south region of the panel
        panelBorder.add(panelButtons, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        // Add the panel to the frame
        add(panelBorder, BorderLayout.NORTH);
        add(label, BorderLayout.SOUTH);

        //Set the size and location of the label
        label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        // Set the size and location of the label
        label.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        //Setting the frame properties
        setTitle("Login Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        //connect to database
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:admins.db");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Check which UI element triggered the action
        if (e.getSource() == btnLogin) {
            // Get the entered username and password
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            // Call the login method and check the returned value
            if (login(username, password)) {
                //Login successful
            } else {
                // Login failed
            }
        } else if (e.getSource() == btnRegister) {
            // Get the entered username and password
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            // Call the register method and check the returned value
            if (register(username, password)) {
                // Registration successful
            } else {
                // Registration failed
            }
        }
    }

    public boolean login(String username, String password) {
        // Check if the entered username and password match a record in the database
        try {
            String query = "SELECT * FROM admins WHERE username = '" + username + "' AND password = '" + password + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                // Login successful
                JOptionPane.showMessageDialog(this, "Login Succesful!");
                new DatabaseConnection();
                dispose(); // Close the login screen
                return true;
            } else {
                // Login failed
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        // Check if the fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
            return false;
        } else {
            // Check if the user already exists
            try {
                String query = "SELECT * FROM admins WHERE username = '" + username + "'";
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    // User already exists
                    JOptionPane.showMessageDialog(this, "Username already exists!");
                    return false;
                } else {
                    // Add new user into database
                    query = "INSERT INTO admins (username, password) VALUES ('" + username + "', '" + password + "')";
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    return true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

}
