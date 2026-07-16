package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.UUID;

/**
 * Form for adding journey / reservation details.
 */
public class Journey_Details extends JFrame implements ActionListener {

    private JTextField tfPnr, tfTicketId, tfFlightCode;
    private JTextField tfJourneyDate, tfJourneyTime, tfSource, tfDest;
    private JButton    btnAdd, btnClear, btnBack;

    public Journey_Details() throws Exception {
        setTitle("Journey Details");
        setLayout(null);
        setSize(560, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("JOURNEY DETAILS");

        int y = 90;
        addLabel("PNR No:",        40, y);  tfPnr        = addField(220, y);

        y += 45;
        addLabel("Ticket ID:",     40, y);
        tfTicketId = styledField();
        tfTicketId.setText("TKT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        tfTicketId.setEditable(false);
        tfTicketId.setBackground(new Color(240, 240, 240));
        tfTicketId.setBounds(220, y, 290, 28);
        add(tfTicketId);

        y += 45;
        addLabel("Flight Code:",   40, y);  tfFlightCode = addField(220, y);

        y += 45;
        addLabel("Journey Date:",  40, y);
        tfJourneyDate = addField(220, y);
        ((JTextField) tfJourneyDate).setToolTipText("Format: YYYY-MM-DD");

        y += 45;
        addLabel("Journey Time:",  40, y);
        tfJourneyTime = addField(220, y);
        ((JTextField) tfJourneyTime).setToolTipText("Format: HH:MM");

        y += 45;
        addLabel("Source:",        40, y);  tfSource = addField(220, y);

        y += 45;
        addLabel("Destination:",   40, y);  tfDest   = addField(220, y);

        y += 55;
        btnAdd   = actionButton("ADD",   new Color(13, 71, 161),  70,  y);
        btnClear = actionButton("CLEAR", new Color(96, 125, 139), 200, y);
        btnBack  = actionButton("BACK",  new Color(198, 40, 40),  330, y);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnAdd)   addJourney();
        if (ae.getSource() == btnClear) clearFields();
        if (ae.getSource() == btnBack)  dispose();
    }

    private void addJourney() {
        String pnr      = tfPnr.getText().trim();
        String ticket   = tfTicketId.getText().trim();
        String fCode    = tfFlightCode.getText().trim();
        String jDate    = tfJourneyDate.getText().trim();
        String jTime    = tfJourneyTime.getText().trim();
        String src      = tfSource.getText().trim();
        String dst      = tfDest.getText().trim();

        if (pnr.isEmpty() || fCode.isEmpty() || jDate.isEmpty() ||
            jTime.isEmpty() || src.isEmpty() || dst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate date format YYYY-MM-DD
        if (!jDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Date must be YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            // Verify PNR exists
            PreparedStatement chk = con.prepareStatement("SELECT pnr_no FROM passenger WHERE pnr_no = ?");
            chk.setString(1, pnr);
            if (!chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "PNR not found. Register the customer first.", "Error", JOptionPane.ERROR_MESSAGE);
                chk.close();
                return;
            }
            chk.close();

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO reservation (pnr_no, ticket_id, f_code, jny_date, jny_time, src, dst) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, pnr);
            pst.setString(2, ticket);
            pst.setString(3, fCode);
            pst.setString(4, jDate);
            pst.setString(5, jTime);
            pst.setString(6, src);
            pst.setString(7, dst);
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this,
                "Journey details saved!\nTicket ID: " + ticket, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfPnr.setText(""); tfFlightCode.setText("");
        tfJourneyDate.setText(""); tfJourneyTime.setText("");
        tfSource.setText(""); tfDest.setText("");
        tfTicketId.setText("TKT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
    }

    // ── Helpers ───────────────────────────────────────────────
    private void buildHeader(String title) {
        JPanel h = new JPanel(null);
        h.setBackground(new Color(13, 71, 161));
        h.setBounds(0, 0, 560, 70);
        add(h);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        lbl.setBounds(170, 20, 400, 30);
        h.add(lbl);
    }

    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 13));
        l.setBounds(x, y, 170, 28);
        add(l);
    }

    private JTextField addField(int x, int y) {
        JTextField f = styledField();
        f.setBounds(x, y, 290, 28);
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
