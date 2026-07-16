package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Login screen — authenticates users against the login table.
 */
public class Login extends JFrame implements ActionListener {

    private JTextField  tfUsername;
    private JPasswordField tfPassword;
    private JButton     btnLogin, btnCancel;
    private JLabel      lblTitle, lblUser, lblPass;

    public Login() {
        setTitle(" AV Airline System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(450, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Background panel ──────────────────────────────────
        JPanel panel = new JPanel();
        panel.setBackground(new Color(13, 71, 161));
        panel.setBounds(0, 0, 450, 320);
        panel.setLayout(null);
        add(panel);

        // ── Title ─────────────────────────────────────────────
        lblTitle = new JLabel(" AV AIRLINE SYSTEM");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setBounds(110, 20, 340, 30);
        panel.add(lblTitle);

        JLabel lblSub = new JLabel("Please login to continue");
        lblSub.setForeground(new Color(187, 222, 251));
        lblSub.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblSub.setBounds(140, 50, 200, 20);
        panel.add(lblSub);

        // ── Inner card ────────────────────────────────────────
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(50, 85, 350, 180);
        panel.add(card);

        lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblUser.setBounds(20, 20, 90, 25);
        card.add(lblUser);

        tfUsername = new JTextField();
        tfUsername.setBounds(120, 20, 200, 28);
        tfUsername.setFont(new Font("Tahoma", Font.PLAIN, 13));
        card.add(tfUsername);

        lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblPass.setBounds(20, 65, 90, 25);
        card.add(lblPass);

        tfPassword = new JPasswordField();
        tfPassword.setBounds(120, 65, 200, 28);
        tfPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
        card.add(tfPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(65, 120, 100, 32);
        btnLogin.setBackground(new Color(13, 71, 161));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(this);
        card.add(btnLogin);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(185, 120, 100, 32);
        btnCancel.setBackground(new Color(198, 40, 40));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this);
        card.add(btnCancel);

        // Allow Enter key to trigger login
        tfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticateUser();
                }
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnLogin) {
            authenticateUser();
        } else if (ae.getSource() == btnCancel) {
            System.exit(0);
        }
    }

    private void authenticateUser() {
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username and Password cannot be empty.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "Login Successful! Welcome, " + username + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                new Mainframe();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                tfPassword.setText("");
            }
            rs.close();
            pst.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
