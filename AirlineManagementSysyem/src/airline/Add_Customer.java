package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.UUID;

/**
 * Form for registering a new passenger / customer.
 */
public class Add_Customer extends JFrame implements ActionListener {

    private JTextField  tfPnr, tfName, tfAddress, tfNationality;
    private JTextField  tfPhone, tfPassport, tfFlightCode;
    private JComboBox<String> cbGender;
    private JButton     btnAdd, btnClear, btnBack;

    public Add_Customer() throws Exception {
        setTitle("Add Customer Details");
        setLayout(null);
        setSize(560, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("ADD CUSTOMER DETAILS");

        // Auto-generate PNR
        String pnr = "PNR" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        int y = 90;
        // PNR (read-only)
        addLabel("PNR No:", 40, y);
        tfPnr = styledField();
        tfPnr.setText(pnr);
        tfPnr.setEditable(false);
        tfPnr.setBackground(new Color(240, 240, 240));
        tfPnr.setBounds(200, y, 300, 28);
        add(tfPnr);

        y += 45;
        addLabel("Passenger Name:", 40, y);
        tfName = addField(200, y);

        y += 45;
        addLabel("Address:", 40, y);
        tfAddress = addField(200, y);

        y += 45;
        addLabel("Nationality:", 40, y);
        tfNationality = addField(200, y);

        y += 45;
        addLabel("Gender:", 40, y);
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setBounds(200, y, 300, 28);
        cbGender.setFont(new Font("Tahoma", Font.PLAIN, 13));
        add(cbGender);

        y += 45;
        addLabel("Phone No:", 40, y);
        tfPhone = addField(200, y);

        y += 45;
        addLabel("Passport No:", 40, y);
        tfPassport = addField(200, y);

        y += 45;
        addLabel("Flight Code:", 40, y);
        tfFlightCode = addField(200, y);

        // ── Buttons ───────────────────────────────────────────
        y += 55;
        btnAdd   = actionButton("ADD",   new Color(13, 71, 161),  80,  y);
        btnClear = actionButton("CLEAR", new Color(96, 125, 139), 210, y);
        btnBack  = actionButton("BACK",  new Color(198, 40, 40),  340, y);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnAdd)   addCustomer();
        if (ae.getSource() == btnClear) clearFields();
        if (ae.getSource() == btnBack)  dispose();
    }

    private void addCustomer() {
        String pnr         = tfPnr.getText().trim();
        String name        = tfName.getText().trim();
        String address     = tfAddress.getText().trim();
        String nationality = tfNationality.getText().trim();
        String gender      = (String) cbGender.getSelectedItem();
        String phone       = tfPhone.getText().trim();
        String passport    = tfPassport.getText().trim();
        String flightCode  = tfFlightCode.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() ||
            passport.isEmpty() || flightCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Basic phone validation
        if (!phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Phone must be 10–15 digits.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = airline.DBConnection.getConnection();

            // Verify flight exists
            PreparedStatement chk = con.prepareStatement("SELECT f_code FROM flight WHERE f_code = ?");
            chk.setString(1, flightCode);
            if (!chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Flight code not found.", "Error", JOptionPane.ERROR_MESSAGE);
                chk.close();
                return;
            }
            chk.close();

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO passenger (pnr_no, name, address, nationality, gender, ph_no, passport_no, fl_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, pnr);
            pst.setString(2, name);
            pst.setString(3, address);
            pst.setString(4, nationality);
            pst.setString(5, gender);
            pst.setString(6, phone);
            pst.setString(7, passport);
            pst.setString(8, flightCode);
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this,
                "Customer added!\nPNR: " + pnr, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        String newPnr = "PNR" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        tfPnr.setText(newPnr);
        tfName.setText(""); tfAddress.setText(""); tfNationality.setText("");
        tfPhone.setText(""); tfPassport.setText(""); tfFlightCode.setText("");
        cbGender.setSelectedIndex(0);
    }

    // ── Helpers ───────────────────────────────────────────────
    private void buildHeader(String title) {
        JPanel h = new JPanel(null);
        h.setBackground(new Color(13, 71, 161));
        h.setBounds(0, 0, 560, 70);
        add(h);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 18));
        lbl.setBounds(130, 20, 400, 30);
        h.add(lbl);
    }

    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 13));
        l.setBounds(x, y, 150, 28);
        add(l);
    }

    private JTextField addField(int x, int y) {
        JTextField f = styledField();
        f.setBounds(x, y, 300, 28);
        add(f);
        return f;
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
