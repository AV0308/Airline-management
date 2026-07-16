package airline;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Displays all passengers in a scrollable JTable with search capability.
 */
public class Passenger_List extends JFrame implements ActionListener {

    private JTable  table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnSearch, btnRefresh, btnClose;

    public Passenger_List() {
        setTitle("Passenger List");
        setLayout(new BorderLayout());
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setBackground(new Color(13, 71, 161));
        header.setPreferredSize(new Dimension(900, 65));
        JLabel title = new JLabel("PASSENGER LIST");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setBounds(340, 18, 300, 30);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Search bar ────────────────────────────────────────
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBackground(new Color(240, 240, 240));
        searchPanel.setPreferredSize(new Dimension(900, 50));

        JLabel lblSearch = new JLabel("Search PNR / Name:");
        lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSearch.setBounds(20, 12, 145, 25);
        searchPanel.add(lblSearch);

        tfSearch = new JTextField();
        tfSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfSearch.setBounds(165, 12, 200, 26);
        searchPanel.add(tfSearch);

        btnSearch  = smallButton("Search",  new Color(13, 71, 161),  375, 12);
        btnRefresh = smallButton("Refresh", new Color(96, 125, 139), 490, 12);
        btnClose   = smallButton("Close",   new Color(198, 40, 40),  605, 12);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        searchPanel.add(btnClose);
        add(searchPanel, BorderLayout.CENTER);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {"PNR No", "Name", "Address", "Nationality", "Gender", "Phone", "Passport No", "Flight Code"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(13, 71, 161));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setGridColor(new Color(224, 224, 224));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(900, 400));
        add(scroll, BorderLayout.SOUTH);

        loadAllPassengers();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnSearch)  searchPassengers();
        if (ae.getSource() == btnRefresh) loadAllPassengers();
        if (ae.getSource() == btnClose)   dispose();
    }

    private void loadAllPassengers() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM passenger ORDER BY name");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("pnr_no"), rs.getString("name"),
                    rs.getString("address"), rs.getString("nationality"),
                    rs.getString("gender"),  rs.getString("ph_no"),
                    rs.getString("passport_no"), rs.getString("fl_code")
                });
            }
            rs.close(); st.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPassengers() {
        String keyword = tfSearch.getText().trim();
        if (keyword.isEmpty()) { loadAllPassengers(); return; }

        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM passenger WHERE pnr_no LIKE ? OR name LIKE ?");
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("pnr_no"), rs.getString("name"),
                    rs.getString("address"), rs.getString("nationality"),
                    rs.getString("gender"),  rs.getString("ph_no"),
                    rs.getString("passport_no"), rs.getString("fl_code")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton smallButton(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 100, 26);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }
}
