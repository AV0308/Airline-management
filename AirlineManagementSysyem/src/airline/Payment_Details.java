package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * Form for recording payment details for a reservation.
 */
public class Payment_Details extends JFrame implements ActionListener {

    private JTextField tfPnr, tfPhone, tfChequeNo, tfCardNo, tfAmount;
    private JTextField tfPayDate;
    private JComboBox<String> cbPayMode;
    private JButton    btnPay, btnClear, btnBack;

    public Payment_Details() throws Exception {
        setTitle("Payment Details");
        setLayout(null);
        setSize(560, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("PAYMENT DETAILS");

        int y = 90;
        addLabel("PNR No:",        40, y);  tfPnr     = addField(220, y);

        y += 45;
        addLabel("Phone No:",      40, y);  tfPhone   = addField(220, y);

        y += 45;
        addLabel("Payment Mode:",  40, y);
        cbPayMode = new JComboBox<>(new String[]{"Cash", "Card", "Cheque", "UPI"});
        cbPayMode.setBounds(220, y, 290, 28);
        cbPayMode.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cbPayMode.addActionListener(this);
        add(cbPayMode);

        y += 45;
        addLabel("Cheque No:",     40, y);  tfChequeNo = addField(220, y);

        y += 45;
        addLabel("Card No:",       40, y);  tfCardNo   = addField(220, y);

        y += 45;
        addLabel("Amount Paid (₹):", 40, y); tfAmount = addField(220, y);

        y += 45;
        addLabel("Payment Date:",  40, y);
        tfPayDate = addField(220, y);
        tfPayDate.setText(LocalDate.now().toString());

        y += 55;
        btnPay   = actionButton("PAY",   new Color(13, 71, 161),  70,  y);
        btnClear = actionButton("CLEAR", new Color(96, 125, 139), 200, y);
        btnBack  = actionButton("BACK",  new Color(198, 40, 40),  330, y);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnPay)   processPayment();
        if (ae.getSource() == btnClear) clearFields();
        if (ae.getSource() == btnBack)  dispose();
    }

    private void processPayment() {
        String pnr    = tfPnr.getText().trim();
        String phone  = tfPhone.getText().trim();
        String cheque = tfChequeNo.getText().trim();
        String card   = tfCardNo.getText().trim();
        String amount = tfAmount.getText().trim();
        String date   = tfPayDate.getText().trim();

        if (pnr.isEmpty() || phone.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PNR, Phone, and Amount are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Amount must be numeric
        try { Double.parseDouble(amount); }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            // Verify PNR
            PreparedStatement chk = con.prepareStatement("SELECT pnr_no FROM passenger WHERE pnr_no = ?");
            chk.setString(1, pnr);
            if (!chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "PNR not found.", "Error", JOptionPane.ERROR_MESSAGE);
                chk.close();
                return;
            }
            chk.close();

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO payment (pnr_no, ph_no, cheque_no, card_no, paid_amt, pay_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, pnr);
            pst.setString(2, phone);
            pst.setString(3, cheque.isEmpty() ? "N/A" : cheque);
            pst.setString(4, card.isEmpty()   ? "N/A" : card);
            pst.setString(5, amount);
            pst.setString(6, date);
            pst.executeUpdate();
            pst.close();

            JOptionPane.showMessageDialog(this,
                "Payment of ₹" + amount + " recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfPnr.setText(""); tfPhone.setText(""); tfChequeNo.setText("");
        tfCardNo.setText(""); tfAmount.setText("");
        tfPayDate.setText(LocalDate.now().toString());
        cbPayMode.setSelectedIndex(0);
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
        l.setBounds(x, y, 175, 28);
        add(l);
    }

    private JTextField addField(int x, int y) {
        JTextField f = new JTextField();
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)));
        f.setBounds(x, y, 290, 28);
        add(f);
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
