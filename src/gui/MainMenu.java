package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class MainMenu {

    private JFrame frame;
    private JPanel panelSiyah, panelBeyaz, panelLogin;
    private JTextField textField;
    private JPasswordField passwordField;
    private JButton girisButton, kayitButton, kapatButton;
    private JLabel lblID, lblParola, lblYolcu, lblAdmin;

    private Color a = Color.BLACK;
    private Color b = Color.WHITE;
    private int girisModu = 1; // 1: Yolcu, 0: Admin

    private PersonManager personManager;
    private int mouseX, mouseY;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PersonManager pm = new PersonManager();

                    if (pm.getPersonByID(9001) == null) {
                        LocalDate adminBirth = LocalDate.of(1995, 5, 15);
                        pm.addPerson(new Admin("Ahmet Veysel", 1234, "admin@airline.com", 1, pm.getDefaultImage(1),
                                adminBirth));

                        LocalDate passengerBirth = LocalDate.of(2000, 1, 1);
                        pm.addPerson(new Passanger("Ahmet Yolcu", 111, "yolcu@airline.com", 1, pm.getDefaultImage(1),
                                passengerBirth));
                    }

                    MainMenu window = new MainMenu(pm);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainMenu(PersonManager pm) {
        this.personManager = pm;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("FES-YET Airlines");
        frame.setUndecorated(true);
        frame.setPreferredSize(new Dimension(480, 320));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.setResizable(false);

        // Logo ve İkon (Taskbar için)
        ImageIcon appIcon = new ImageIcon("savesAndImage/Image/logo.png");
        frame.setIconImage(appIcon.getImage());

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });

        panelSiyah = new JPanel();
        panelSiyah.setPreferredSize(new Dimension(130, 10));
        panelSiyah.setBackground(Color.BLACK);
        panelSiyah.setLayout(null);
        frame.getContentPane().add(panelSiyah, BorderLayout.WEST);

        lblAdmin = new JLabel("Admin");
        lblAdmin.setHorizontalAlignment(SwingConstants.CENTER);
        lblAdmin.setFont(new Font("Impact", Font.PLAIN, 26));
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setBounds(0, 110, 130, 40);
        panelSiyah.add(lblAdmin);

        panelSiyah.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                makeLightLogin();
            }
        });

        panelBeyaz = new JPanel();
        panelBeyaz.setBackground(Color.WHITE);
        panelBeyaz.setPreferredSize(new Dimension(130, 10));
        panelBeyaz.setLayout(null);
        frame.getContentPane().add(panelBeyaz, BorderLayout.EAST);

        lblYolcu = new JLabel("Yolcu");
        lblYolcu.setHorizontalAlignment(SwingConstants.CENTER);
        lblYolcu.setForeground(Color.BLACK);
        lblYolcu.setFont(new Font("Impact", Font.PLAIN, 26));
        lblYolcu.setBounds(0, 110, 130, 40);
        panelBeyaz.add(lblYolcu);

        kapatButton = new JButton("X");
        kapatButton.setBounds(107, 0, 23, 22);
        kapatButton.setMargin(new Insets(0, 0, 0, 0));
        panelBeyaz.add(kapatButton);
        kapatButton.setFocusPainted(false);
        kapatButton.setBackground(new Color(204, 0, 51));
        kapatButton.setForeground(Color.WHITE);
        kapatButton.setBorder(null);
        kapatButton.addActionListener(e -> System.exit(0));

        panelBeyaz.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                makeDarkLogin();
            }
        });

        panelLogin = new JPanel();
        panelLogin.setBackground(Color.WHITE);
        panelLogin.setLayout(null);
        frame.getContentPane().add(panelLogin, BorderLayout.CENTER);

        // ORTA LOGO
        JLabel mainLogo = new JLabel(new ImageIcon(appIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        mainLogo.setBounds(80, 10, 60, 60);
        panelLogin.add(mainLogo);

        lblID = new JLabel("ID:");
        lblID.setBounds(20, 90, 60, 25);
        panelLogin.add(lblID);

        textField = new JTextField();
        textField.setBounds(85, 90, 110, 25);
        textField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Border kaldırıldı, padding eklendi
        panelLogin.add(textField);

        lblParola = new JLabel("Parola:");
        lblParola.setBounds(20, 130, 60, 25);
        panelLogin.add(lblParola);

        passwordField = new JPasswordField();
        passwordField.setBounds(85, 130, 110, 25);
        passwordField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Border kaldırıldı
        panelLogin.add(passwordField);

        girisButton = new JButton("Giriş Yap");
        girisButton.setBounds(10, 190, 95, 30);
        girisButton.setFocusPainted(false);
        girisButton.setBorder(null);
        panelLogin.add(girisButton);

        kayitButton = new JButton("Kayıt Ol");
        kayitButton.setBounds(110, 190, 95, 30);
        kayitButton.setFocusPainted(false);
        kayitButton.setBorder(null);
        panelLogin.add(kayitButton);

        makeDarkLogin();

        girisButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(textField.getText());
                    String pass = new String(passwordField.getPassword());

                    if (girisModu == 0) {
                        if (personManager.IsCorrectAdminPass(id, pass)) {
                            Admin a = (Admin) personManager.getPersonByID(id);
                            new AdminFrame(a).setVisible(true);
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Hatalı Admin Bilgisi!");
                        }
                    } else {
                        if (personManager.IsCorrectPassangerPass(id, pass)) {
                            Passanger p = (Passanger) personManager.getPersonByID(id);
                            PassengerFrame pFrame = new PassengerFrame(p);
                            pFrame.setVisible(true);
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Hatalı Yolcu Bilgisi!");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz giriş!");
                }
            }
        });

        kayitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame registerFrame = new JFrame("Kayıt Ol - FES-YET Airlines");
                registerFrame.setUndecorated(true);
                registerFrame.setSize(800, 400);
                RegisterPanelPassanger registrationPanel = new RegisterPanelPassanger(personManager);
                registerFrame.getContentPane().add(registrationPanel);
                registerFrame.setLocationRelativeTo(null);
                registerFrame.setVisible(true);
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setBounds(100, 100, 480, 320);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void makeDarkLogin() {
        girisModu = 1;
        panelLogin.setBackground(b); // Beyaz
        kayitButton.setVisible(true);
        updateComponentsColor(Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE);
    }

    public void makeLightLogin() {
        girisModu = 0;
        panelLogin.setBackground(a); // Siyah
        kayitButton.setVisible(false);
        updateComponentsColor(Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK);
    }

    private void updateComponentsColor(Color fore, Color back, Color btnFore, Color btnBack) {
        for (Component c : panelLogin.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(fore);
            } else if (c instanceof JTextField) { // TextField ve PasswordField
                c.setForeground(Color.BLACK); // Yazı her zaman siyah olsun (okunabilirlik için)
                c.setBackground(new Color(230, 230, 230)); // Hafif gri
            } else if (c instanceof JButton) {
                c.setForeground(btnFore);
                c.setBackground(btnBack);
                // Buton hover efekti eklenebilir ama basit tutalım
                if (girisModu == 1) { // Yolcu Modu (Arkaplan Beyaz)
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                } else { // Admin Modu (Arkaplan Siyah)
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
        }
    }
}

