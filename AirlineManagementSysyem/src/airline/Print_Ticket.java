package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Retrieves and displays a formatted ticket for a given PNR.
 */
public class Print_Ticket extends JFrame implements ActionListener {

    private JTextField tfPnr;
    private JTextArea  taTicket;
    private JButton    btnFetch, btnPrint, btnClose;

    public Print_Ticket() {
        setTitle("Print Ticket");
        setLayout(null);
        setSize(580, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        buildHeader("PRINT TICKET");

        // ── PNR search ────────────────────────────────────────
        JLabel lblPnr = new JLabel("Enter PNR:");
        lblPnr.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblPnr.setBounds(40, 90, 90, 28);
        add(lblPnr);

        tfPnr = new JTextField();
        tfPnr.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfPnr.setBounds(140, 90, 220, 28);
        add(tfPnr);

        btnFetch = new JButton("FETCH");
        btnFetch.setBounds(375, 90, 90, 28);
        btnFetch.setBackground(new Color(13, 71, 161));
        btnFetch.setForeground(Color.WHITE);
        btnFetch.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnFetch.setFocusPainted(false);
        btnFetch.addActionListener(this);
        add(btnFetch);

        // ── Ticket display ────────────────────────────────────
        taTicket = new JTextArea();
        taTicket.setFont(new Font("Courier New", Font.PLAIN, 13));
        taTicket.setEditable(false);
        taTicket.setBackground(new Color(250, 250, 250));
        taTicket.setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)));

        JScrollPane scroll = new JScrollPane(taTicket);
        scroll.setBounds(40, 135, 500, 300);
        add(scroll);

        // ── Buttons ───────────────────────────────────────────
        btnPrint = new JButton("🖨  PRINT");
        btnPrint.setBounds(160, 450, 110, 32);
        btnPrint.setBackground(new Color(56, 142, 60));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnPrint.setFocusPainted(false);
        btnPrint.addActionListener(this);
        add(btnPrint);

        btnClose = new JButton("CLOSE");
        btnClose.setBounds(295, 450, 110, 32);
        btnClose.setBackground(new Color(198, 40, 40));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(this);
        add(btnClose);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnFetch) fetchTicket();
        if (ae.getSource() == btnPrint) printTicket();
        if (ae.getSource() == btnClose) dispose();
    }

    private void fetchTicket() {
        String pnr = tfPnr.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a PNR number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pstP = con.prepareStatement(
                "SELECT * FROM passenger WHERE pnr_no = ?");
            pstP.setString(1, pnr);
            ResultSet rsP = pstP.executeQuery();

            if (!rsP.next()) {
                JOptionPane.showMessageDialog(this, "No passenger found with PNR: " + pnr, "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name        = rsP.getString("name");
            String nationality = rsP.getString("nationality");
            String gender      = rsP.getString("gender");
            String phone       = rsP.getString("ph_no");
            String passport    = rsP.getString("passport_no");
            String fCode       = rsP.getString("fl_code");
            pstP.close();

            // Fetch journey
            PreparedStatement pstR = con.prepareStatement(
                "SELECT * FROM reservation WHERE pnr_no = ?");
            pstR.setString(1, pnr);
            ResultSet rsR = pstR.executeQuery();
            String journeyDate = "N/A", journeyTime = "N/A", src = "N/A", dst = "N/A", ticketId = "N/A";
            if (rsR.next()) {
                journeyDate = rsR.getString("jny_date");
                journeyTime = rsR.getString("jny_time");
                src         = rsR.getString("src");
                dst         = rsR.getString("dst");
                ticketId    = rsR.getString("ticket_id");
            }
            pstR.close();

            // Fetch payment
            PreparedStatement pstPay = con.prepareStatement(
                "SELECT paid_amt, pay_date FROM payment WHERE pnr_no = ?");
            pstPay.setString(1, pnr);
            ResultSet rsPay = pstPay.executeQuery();
            String amtPaid = "N/A", payDate = "N/A";
            if (rsPay.next()) {
                amtPaid = rsPay.getString("paid_amt");
                payDate = rsPay.getString("pay_date");
            }
            pstPay.close();

            // Build ticket
            String line  = "━".repeat(50);
            String dline = "═".repeat(50);
            StringBuilder sb = new StringBuilder();
            sb.append(dline).append("\n");
            sb.append("          ✈  AV AIRLINE BOARDING PASS\n");
            sb.append(dline).append("\n");
            sb.append(String.format("  PNR No      : %s\n", pnr));
            sb.append(String.format("  Ticket ID   : %s\n", ticketId));
            sb.append(line).append("\n");
            sb.append(String.format("  Passenger   : %s\n", name));
            sb.append(String.format("  Nationality : %s\n", nationality));
            sb.append(String.format("  Gender      : %s\n", gender));
            sb.append(String.format("  Phone       : %s\n", phone));
            sb.append(String.format("  Passport    : %s\n", passport));
            sb.append(line).append("\n");
            sb.append(String.format("  Flight Code : %s\n", fCode));
            sb.append(String.format("  From        : %s\n", src));
            sb.append(String.format("  To          : %s\n", dst));
            sb.append(String.format("  Date        : %s\n", journeyDate));
            sb.append(String.format("  Time        : %s\n", journeyTime));
            sb.append(line).append("\n");
            sb.append(String.format("  Amount Paid : ₹%s\n", amtPaid));
            sb.append(String.format("  Payment Date: %s\n", payDate));
            sb.append(dline).append("\n");
            sb.append("   Thank you for choosing AV AIRLINE!\n");
            sb.append(dline).append("\n");

            taTicket.setText(sb.toString());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printTicket() {
        if (taTicket.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fetch a ticket first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            taTicket.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildHeader(String title) {
        JPanel h = new JPanel(null);
        h.setBackground(new Color(13, 71, 161));
        h.setBounds(0, 0, 580, 70);
        add(h);
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        lbl.setBounds(210, 20, 300, 30);
        h.add(lbl);
    }
}
