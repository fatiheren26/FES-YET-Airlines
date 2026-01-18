package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PassengerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel cardPanel;
    private CardLayout cl;

    private FlightManager fm;
    private AssetManager am;
    private Passanger currentPassanger;
    private ReservationManager rm; // Yeni eklenen satır

    private JLabel lblBalanceValue, lblPointsValue;

    public static final Color BG_COLOR = new Color(18, 18, 20);
    public static final Color CARD_COLOR = new Color(28, 28, 32);
    public static final Color ACCENT_BLUE = new Color(99, 102, 241);
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);
    public static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    public static final Color TEXT_MAIN = new Color(240, 240, 240);
    public static final Color TEXT_SUB = new Color(150, 150, 155);

    // GÜNCELLENEN CONSTRUCTOR: Parametre olarak login olan yolcuyu alır
    public PassengerFrame(Passanger loggedInPassenger) {
        this.am = new AssetManager();
        this.fm = new FlightManager(new SeatManager());
        this.rm = new ReservationManager(); // Yeni eklenen satır
        this.currentPassanger = loggedInPassenger;

        setTitle("FES-YET Airlines Passenger Terminal - " + currentPassanger.getName());

        // FES-YET Airlines İkonu
        ImageIcon appIcon = new ImageIcon("savesAndImage/Image/logo.png");
        setIconImage(appIcon.getImage());

        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Pencere kapandığında MainMenu'ya dön
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                PersonManager pm = new PersonManager();
                MainMenu menu = new MainMenu(pm);
                menu.makeDarkLogin();
                menu.show();
            }
        });

        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // Özel Mouse İmleci
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            // Dosyanın varlığını kontrol et ve yükle
            String cursorPath = "savesAndImage/Image/mouse.png";
            Image cursorImage = toolkit.getImage(cursorPath);

            // İmleci oluştur (Hotspot: 0,0 sol üst köşe)
            Cursor customCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "SkyLineCursor");
            setCursor(customCursor);
        } catch (Exception e) {
            System.err.println("Mouse ikonu yüklenemedi: " + e.getMessage());
        }

        initializeSidebar();
        initializeMainContent();
    }

    private void initializeSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CARD_COLOR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        JLabel logo = new JLabel("FES-YET Airlines");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(ACCENT_BLUE);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        sidebar.add(logo);

        JButton btnDash = createNavButton("Dashboard", "🏠");
        JButton btnSearch = createNavButton("Uçuş Ara", "🔍");
        JButton btnTickets = createNavButton("Biletlerim", "🎫");
        JButton btnWallet = createNavButton("Cüzdanım", "💰");
        JButton btnProfile = createNavButton("Profilim", "👤");

        sidebar.add(btnDash);
        sidebar.add(btnSearch);
        sidebar.add(btnTickets);
        sidebar.add(btnWallet);
        sidebar.add(btnProfile);

        add(sidebar, BorderLayout.WEST);

        btnDash.addActionListener(e -> {
            updateDashboardValues();
            cl.show(cardPanel, "DASHBOARD");
        });
        btnSearch.addActionListener(e -> cl.show(cardPanel, "SEARCH"));
        btnTickets.addActionListener(e -> cl.show(cardPanel, "TICKETS"));
        btnWallet.addActionListener(e -> cl.show(cardPanel, "WALLET"));
        btnProfile.addActionListener(e -> cl.show(cardPanel, "PROFILE"));
    }

    private void initializeMainContent() {
        cl = new CardLayout();
        cardPanel = new JPanel(cl);
        cardPanel.setOpaque(false);

        cardPanel.add(createDashboardPanel(), "DASHBOARD");

        cardPanel.add(new FlightsPanelPassanger(fm, am, currentPassanger, this), "SEARCH");
        cardPanel.add(new MyTicketsPanel(currentPassanger, rm), "TICKETS");
        cardPanel.add(new WalletPanel(currentPassanger), "WALLET");
        cardPanel.add(new ProfilePanel(currentPassanger), "PROFILE");

        add(cardPanel, BorderLayout.CENTER);
    }

    public void showSeatSelection(Flight flight) {
        SeatViewerPassanger svp = new SeatViewerPassanger(flight, currentPassanger, rm, fm);
        cardPanel.add(svp, "SEAT_SELECTION");
        cl.show(cardPanel, "SEAT_SELECTION");
    }

    private JPanel createDashboardPanel() {
        JPanel mainDash = new JPanel(new BorderLayout(25, 25));
        mainDash.setOpaque(false);
        mainDash.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel welcome = new JLabel("Hoş Geldiniz, " + currentPassanger.getName());
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(TEXT_MAIN);
        JLabel sub = new JLabel("Bugün nereye uçmak istersiniz?");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(TEXT_SUB);
        header.add(welcome);
        header.add(sub);
        mainDash.add(header, BorderLayout.NORTH);

        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setOpaque(false);

        statsContainer.add(createMiniCard("Aktif Uçuşlar", "0 Uçuş", "✈️", ACCENT_BLUE));

        lblBalanceValue = new JLabel(currentPassanger.getBalance() + " ₺");
        statsContainer.add(createMiniCardWithLabel("Bakiye", lblBalanceValue, "💰", ACCENT_GREEN));

        lblPointsValue = new JLabel(currentPassanger.getLoyaltyPoints() + " Pts");
        statsContainer.add(createMiniCardWithLabel("Sadakat Puanı", lblPointsValue, "⭐", ACCENT_ORANGE));

        mainDash.add(statsContainer, BorderLayout.CENTER);
        return mainDash;
    }

    public void updateDashboardValues() {
        lblBalanceValue.setText(currentPassanger.getBalance() + " ₺");
        lblPointsValue.setText(currentPassanger.getLoyaltyPoints() + " Pts");
    }

    private JPanel createMiniCardWithLabel(String title, JLabel valueLbl, String icon, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(TEXT_SUB);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        valueLbl.setForeground(accent);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));

        textPanel.add(lblTitle);
        textPanel.add(valueLbl);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMiniCard(String title, String value, String icon, Color accent) {
        return createMiniCardWithLabel(title, new JLabel(value), icon, accent);
    }

    private JButton createNavButton(String text, String icon) {
        JButton btn = new JButton("  " + icon + "    " + text);
        btn.setPreferredSize(new Dimension(210, 50));
        btn.setForeground(TEXT_SUB);
        btn.setBackground(CARD_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 45));
                btn.setForeground(TEXT_MAIN);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD_COLOR);
                btn.setForeground(TEXT_SUB);
            }
        });
        return btn;
    }

    private JPanel createPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SUB);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        p.add(l);
        return p;
    }
}

