package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AdminFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private Person admin;

    //Managerlar
    private AssetManager assetManager;
    private FlightManager flightManager;
    private SeatManager seatManager;

    private Color sidePanelColor = new Color(33, 37, 41);
    private Color activeBtnColor = new Color(52, 58, 64);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color accentColor = new Color(13, 110, 253);

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Admin testAdmin = new Admin(
                            "Furkan ÇAKMAKTAS",
                            1234,
                            "admin@test.com",
                            1,
                            null,
                            java.time.LocalDate.of(1995, 5, 15));

                    AdminFrame frame = new AdminFrame(testAdmin);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public AdminFrame(Person admin) {
        this.admin = admin;

        
        this.assetManager = new AssetManager();
        this.seatManager = new SeatManager();
        this.flightManager = new FlightManager(this.seatManager);

        setMinimumSize(new Dimension(1000, 650));
        setTitle("FES-YET Airlines Admin Console - " + admin.getName());

        
        ImageIcon appIcon = new ImageIcon("savesAndImage/Image/logo.png");
        setIconImage(appIcon.getImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1100, 700);

        // Pencere kapandığında MainMenu'ya dön
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                PersonManager pm = new PersonManager();
                MainMenu menu = new MainMenu(pm);
                menu.makeLightLogin(); // Admin girişi için light theme
                menu.show();
            }
        });

        contentPane = new JPanel();
        contentPane.setBackground(backgroundColor);
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // --- YAN MENÜ ---
        JPanel sideMenu = new JPanel();
        sideMenu.setBackground(sidePanelColor);
        sideMenu.setPreferredSize(new Dimension(200, 10));
        sideMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        contentPane.add(sideMenu, BorderLayout.WEST);

        JLabel lblAdminName = new JLabel(admin.getName().toUpperCase());
        lblAdminName.setForeground(accentColor);
        lblAdminName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAdminName.setBorder(new EmptyBorder(15, 0, 25, 0));
        sideMenu.add(lblAdminName);

        JButton btnGiris = createMenuButton("GIRIŞ");
        JButton btnFilo = createMenuButton("FİLO");
        JButton btnUcus = createMenuButton("UÇUŞLAR");
        JButton btnMulti = createMenuButton("Multi Threads");

        sideMenu.add(btnGiris);
        sideMenu.add(btnFilo);
        sideMenu.add(btnUcus);
        sideMenu.add(btnMulti);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(backgroundColor);
        contentPane.add(cardPanel, BorderLayout.CENTER);

        createDashboardPanel();
        createFleetPanel();
        createFlightPanel(); // FlightsPanelAdm buraya bağlanacak
        createLogPanel();
        createThreadPanel();

        btnGiris.addActionListener(e -> cardLayout.show(cardPanel, "Dash"));
        btnFilo.addActionListener(e -> cardLayout.show(cardPanel, "Fleet"));
        btnUcus.addActionListener(e -> cardLayout.show(cardPanel, "Flight"));
        btnUcus.addActionListener(e -> {
            Component[] components = cardPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof FlightsPanelAdm) {
                    ((FlightsPanelAdm) comp).refreshCombos(); // AssetManager'daki yeni uçakları çek
                    ((FlightsPanelAdm) comp).updateTable(); // Dosyadan yüklenen uçuşları göster
                }
            }
            cardLayout.show(cardPanel, "Flight");
        });
        btnMulti.addActionListener(e -> cardLayout.show(cardPanel, "Threads"));
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 35));
        btn.setFocusPainted(false);
        btn.setBackground(activeBtnColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBorder(new LineBorder(activeBtnColor, 1));
        return btn;
    }

    private void createDashboardPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        JLabel title = new JLabel("System Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(30, 20, 300, 40);
        p.add(title);
        cardPanel.add(p, "Dash");
    }

    private void createFleetPanel() {
        AssetPanel ap = new AssetPanel(assetManager);
        cardPanel.add(ap, "Fleet");
    }

    private void createFlightPanel() {
        // Yeni oluşturduğumuz 3 bölümlü Admin Uçuş Panelini bağlıyoruz
        FlightsPanelAdm fp = new FlightsPanelAdm(flightManager, assetManager);
        cardPanel.add(fp, "Flight");
    }

    private void createLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea logArea = new JTextArea("System Logs...\nAdmin logged in: " + admin.getName());
        logArea.setEditable(false);
        p.add(new JScrollPane(logArea));
        cardPanel.add(p, "Logs");
    }

    private void createThreadPanel() {
        // Eşzamanlı Koltuk Rezervasyonu Demo Paneli
        ConcurrencyDemoPanel concurrencyPanel = new ConcurrencyDemoPanel();
        cardPanel.add(concurrencyPanel, "Threads");
    }
}

