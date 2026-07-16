package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Form for adding and viewing flight information.
 */
public class Flight_Info extends JFrame implements ActionListener {

    private JTextField tfCode, tfName, tfSource, tfDest;
    private JButton btnAdd, btnClear, btnBack;

    public Flight_Info() {
        setTitle("Flight Information");
        setLayout(null);
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("FLIGHT INFORMATION");

        // ── Form fields ───────────────────────────────────────
        String[] labels = {"Flight Code:", "Flight Name:", "Source:", "Destination:"};
        int y = 100;
        JTextField[] fields = new JTextField[4];
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = styledLabel(labels[i]);
            lbl.setBounds(60, y, 120, 28);
            add(lbl);

            fields[i] = styledField();
            fields[i].setBounds(190, y, 240, 28);
            add(fields[i]);
            y += 50;
        }

        tfCode   = fields[0];
        tfName   = fields[1];
        tfSource = fields[2];
        tfDest   = fields[3];

        // ── Buttons ───────────────────────────────────────────
        btnAdd   = actionButton("ADD FLIGHT",  new Color(13, 71, 161), 80,  320);
        btnClear = actionButton("CLEAR",       new Color(96, 125, 139), 200, 320);
        btnBack  = actionButton("BACK",        new Color(198, 40, 40),  320, 320);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnAdd)   addFlight();
        if (ae.getSource() == btnClear) clearFields();
        if (ae.getSource() == btnBack)  dispose();
    }

    private void addFlight() {
        String code = tfCode.getText().trim();
        String name = tfName.getText().trim();
        String src  = tfSource.getText().trim();
        String dst  = tfDest.getText().trim();

        if (code.isEmpty() || name.isEmpty() || src.isEmpty() || dst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            // Check for duplicate
            PreparedStatement check = con.prepareStatement("SELECT f_code FROM flight WHERE f_code = ?");
            check.setString(1, code);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Flight code already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                check.close();
                return;
            }
            check.close();

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO flight (f_code, f_name, src, dst) VALUES (?, ?, ?, ?)");
            pst.setString(1, code);
            pst.setString(2, name);
            pst.setString(3, src);
            pst.setString(4, dst);
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfCode.setText(""); tfName.setText("");
        tfSource.setText(""); tfDest.setText("");
        tfCode.requestFocus();
    }

    // ── Shared UI helpers ──────────────────────────────────────
    private void buildHeader(String title) {
        JPanel header = new JPanel(null);
        header.setBackground(new Color(13, 71, 161));
        header.setBounds(0, 0, 520, 70);
        add(header);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        lbl.setBounds(120, 20, 380, 30);
        header.add(lbl);
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 13));
        return l;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)));
        return f;
    }

    private JButton actionButton(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 110, 32);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }
}
