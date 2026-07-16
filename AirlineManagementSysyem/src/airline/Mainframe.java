package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window with menu-bar navigation.
 */
public class Mainframe extends JFrame {

    public Mainframe() {
        super(" AV AIRLINE SYSTEM");
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        // ── Background label ──────────────────────────────────
        JLabel background = new JLabel();
        background.setBackground(new Color(13, 71, 161));
        background.setOpaque(true);
        background.setBounds(0, 0, 1920, 1080);
        background.setLayout(null);
        add(background);

        // ── Welcome label ─────────────────────────────────────
        JLabel welcome = new JLabel("✈  AV AIRLINES WELCOMES YOU");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Tahoma", Font.BOLD, 38));
        welcome.setBounds(480, 100, 900, 60);
        background.add(welcome);

        JLabel tagline = new JLabel("Your trusted partner in AV travel management");
        tagline.setForeground(new Color(187, 222, 251));
        tagline.setFont(new Font("Tahoma", Font.ITALIC, 18));
        tagline.setBounds(510, 165, 700, 30);
        background.add(tagline);

        // ── Menu bar ──────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        setJMenuBar(menuBar);

        // AIRLINE SYSTEM menu
        JMenu menuAirline = new JMenu("✈ AV AIRLINE SYSTEM");
        menuAirline.setForeground(new Color(13, 71, 161));
        menuAirline.setFont(new Font("Tahoma", Font.BOLD, 13));
        menuBar.add(menuAirline);

        String[] airlineItems = {
            "FLIGHT INFO", "ADD CUSTOMER DETAILS",
            "JOURNEY DETAILS", "PAYMENT DETAILS", "CANCELLATION"
        };
        for (String item : airlineItems) {
            JMenuItem mi = new JMenuItem(item);
            mi.setFont(new Font("Tahoma", Font.PLAIN, 13));
            mi.addActionListener(buildAirlineListener(item));
            menuAirline.add(mi);
        }

        // TICKET menu
        JMenu menuTicket = new JMenu("🎫  TICKET");
        menuTicket.setForeground(new Color(198, 40, 40));
        menuTicket.setFont(new Font("Tahoma", Font.BOLD, 13));
        menuBar.add(menuTicket);

        JMenuItem miPrintTicket = new JMenuItem("PRINT TICKET");
        miPrintTicket.setFont(new Font("Tahoma", Font.PLAIN, 13));
        miPrintTicket.addActionListener(e -> new Print_Ticket());
        menuTicket.add(miPrintTicket);

        // LIST menu
        JMenu menuList = new JMenu("📋  LIST");
        menuList.setForeground(new Color(13, 71, 161));
        menuList.setFont(new Font("Tahoma", Font.BOLD, 13));
        menuBar.add(menuList);

        JMenuItem miPassengerList = new JMenuItem("PASSENGER LIST");
        miPassengerList.setFont(new Font("Tahoma", Font.PLAIN, 13));
        miPassengerList.addActionListener(e -> new Passenger_List());
        menuList.add(miPassengerList);

        JMenuItem miFlightList = new JMenuItem("FLIGHT LIST");
        miFlightList.setFont(new Font("Tahoma", Font.PLAIN, 13));
        miFlightList.addActionListener(e -> new Flight_List());
        menuList.add(miFlightList);

        // MISC menu
        JMenu menuMisc = new JMenu("⚙  MISC");
        menuMisc.setForeground(new Color(198, 40, 40));
        menuMisc.setFont(new Font("Tahoma", Font.BOLD, 13));
        menuBar.add(menuMisc);

        JMenuItem miLogout = new JMenuItem("LOGOUT");
        miLogout.setFont(new Font("Tahoma", Font.PLAIN, 13));
        miLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new Login();
                dispose();
            }
        });
        menuMisc.add(miLogout);

        JMenuItem miExit = new JMenuItem("EXIT");
        miExit.setFont(new Font("Tahoma", Font.PLAIN, 13));
        miExit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Exit the application?", "Exit",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                DBConnection.closeConnection();
                System.exit(0);
            }
        });
        menuMisc.add(miExit);

        setVisible(true);
    }

    private ActionListener buildAirlineListener(String item) {
        return e -> {
            switch (item) {
                case "FLIGHT INFO"           -> new Flight_Info();
                case "ADD CUSTOMER DETAILS"  -> { try { new Add_Customer(); } catch (Exception ex) { ex.printStackTrace(); } }
                case "JOURNEY DETAILS"       -> { try { new Journey_Details(); } catch (Exception ex) { ex.printStackTrace(); } }
                case "PAYMENT DETAILS"       -> { try { new airline.Payment_Details(); } catch (Exception ex) { ex.printStackTrace(); } }
                case "CANCELLATION"          -> new Cancel();
            }
        };
    }
}
