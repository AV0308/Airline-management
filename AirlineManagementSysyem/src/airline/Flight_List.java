package airline;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Displays all flights in a JTable with search and delete capability.
 */
public class Flight_List extends JFrame implements ActionListener {

    private JTable  table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnSearch, btnRefresh, btnDelete, btnClose;

    public Flight_List() {
        setTitle("Flight List");
        setLayout(new BorderLayout());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setBackground(new Color(13, 71, 161));
        header.setPreferredSize(new Dimension(700, 65));
        JLabel title = new JLabel("FLIGHT LIST");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setBounds(270, 18, 300, 30);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ── Toolbar ───────────────────────────────────────────
        JPanel toolbar = new JPanel(null);
        toolbar.setBackground(new Color(240, 240, 240));
        toolbar.setPreferredSize(new Dimension(700, 50));

        JLabel lbl = new JLabel("Search:");
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lbl.setBounds(15, 12, 60, 25);
        toolbar.add(lbl);

        tfSearch = new JTextField();
        tfSearch.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfSearch.setBounds(80, 12, 180, 26);
        toolbar.add(tfSearch);

        btnSearch  = tBtn("Search",  new Color(13, 71, 161),  270, 12);
        btnRefresh = tBtn("Refresh", new Color(96, 125, 139), 375, 12);
        btnDelete  = tBtn("Delete",  new Color(230, 81, 0),   480, 12);
        btnClose   = tBtn("Close",   new Color(198, 40, 40),  585, 12);
        toolbar.add(btnSearch); toolbar.add(btnRefresh);
        toolbar.add(btnDelete); toolbar.add(btnClose);
        add(toolbar, BorderLayout.CENTER);

        // ── Table ─────────────────────────────────────────────
        String[] cols = {"Flight Code", "Flight Name", "Source", "Destination"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(13, 71, 161));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setGridColor(new Color(224, 224, 224));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(700, 370));
        add(scroll, BorderLayout.SOUTH);

        loadFlights(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnSearch)  loadFlights(tfSearch.getText().trim());
        if (ae.getSource() == btnRefresh) { tfSearch.setText(""); loadFlights(null); }
        if (ae.getSource() == btnDelete)  deleteFlight();
        if (ae.getSource() == btnClose)   dispose();
    }

    private void loadFlights(String keyword) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst;
            if (keyword == null || keyword.isEmpty()) {
                pst = con.prepareStatement("SELECT * FROM flight ORDER BY f_code");
            } else {
                pst = con.prepareStatement("SELECT * FROM flight WHERE f_code LIKE ? OR f_name LIKE ?");
                pst.setString(1, "%" + keyword + "%");
                pst.setString(2, "%" + keyword + "%");
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("f_code"), rs.getString("f_name"),
                    rs.getString("src"),    rs.getString("dst")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlight() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a flight to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String code = (String) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete flight " + code + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM flight WHERE f_code = ?");
            pst.setString(1, code);
            pst.executeUpdate();
            pst.close();
            loadFlights(null);
            JOptionPane.showMessageDialog(this, "Flight deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton tBtn(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 90, 26);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }
}
