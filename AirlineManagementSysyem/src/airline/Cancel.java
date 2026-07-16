package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Form to cancel a passenger's reservation and log it.
 */
public class Cancel extends JFrame implements ActionListener {

    private JTextField tfPnr, tfFlightCode;
    private JButton    btnCancel, btnClear, btnBack;

    public Cancel() {
        setTitle("Cancellation");
        setLayout(null);
        setSize(480, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("CANCELLATION");

        int y = 110;
        addLabel("PNR No:",      40, y);  tfPnr        = addField(220, y);

        y += 55;
        addLabel("Flight Code:", 40, y);  tfFlightCode = addField(220, y);

        y += 65;
        btnCancel = actionButton("CANCEL TICKET", new Color(198, 40, 40),  50,  y);
        btnClear  = actionButton("CLEAR",          new Color(96, 125, 139), 200, y);
        btnBack   = actionButton("BACK",           new Color(13, 71, 161),  330, y);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnCancel) cancelTicket();
        if (ae.getSource() == btnClear)  clearFields();
        if (ae.getSource() == btnBack)   dispose();
    }

    private void cancelTicket() {
        String pnr   = tfPnr.getText().trim();
        String fCode = tfFlightCode.getText().trim();

        if (pnr.isEmpty() || fCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PNR and Flight Code are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel the ticket for PNR: " + pnr + "?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Connection con = airline.DBConnection.getConnection();

            // Verify reservation exists
            PreparedStatement chk = con.prepareStatement(
                "SELECT pnr_no FROM reservation WHERE pnr_no = ? AND f_code = ?");
            chk.setString(1, pnr);
            chk.setString(2, fCode);
            ResultSet rs = chk.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "No reservation found for given PNR and Flight Code.", "Error", JOptionPane.ERROR_MESSAGE);
                chk.close();
                return;
            }
            chk.close();

            // Insert cancellation record
            String cancNo = "CAN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            String today  = LocalDate.now().toString();

            PreparedStatement ins = con.prepareStatement(
                "INSERT INTO cancellation (pnr_no, cancellation_no, cancellation_date, fli_code) " +
                "VALUES (?, ?, ?, ?)");
            ins.setString(1, pnr);
            ins.setString(2, cancNo);
            ins.setString(3, today);
            ins.setString(4, fCode);
            ins.executeUpdate();
            ins.close();

            // Remove reservation
            PreparedStatement del = con.prepareStatement(
                "DELETE FROM reservation WHERE pnr_no = ? AND f_code = ?");
            del.setString(1, pnr);
            del.setString(2, fCode);
            del.executeUpdate();
            del.close();

            JOptionPane.showMessageDialog(this,
                "Ticket cancelled.\nCancellation No: " + cancNo, "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfPnr.setText("");
        tfFlightCode.setText("");
    }

    // ── Helpers ───────────────────────────────────────────────
    private void buildHeader(String title) {
        JPanel h = new JPanel(null);
        h.setBackground(new Color(198, 40, 40));
        h.setBounds(0, 0, 480, 70);
        add(h);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 22));
        lbl.setBounds(160, 20, 300, 30);
        h.add(lbl);
    }

    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.PLAIN, 13));
        l.setBounds(x, y, 170, 28);
        add(l);
    }

    private JTextField addField(int x, int y) {
        JTextField f = new JTextField();
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)));
        f.setBounds(x, y, 200, 28);
        add(f);
        return f;
    }

    private JButton actionButton(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 120, 32);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }
}
