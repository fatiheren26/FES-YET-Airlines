package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegisterPanelPassanger extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField txtName, txtEmail;
    private JPasswordField txtPass; // JTextField yerine JPasswordField yapıldı
    private JFormattedTextField txtBirthDate;
    private JLabel lblPPPreview;
    private JRadioButton rdbtnMale, rdbtnFemale;
    private File selectedImageFile = null;
    private PersonManager personManager;

    private int mouseX, mouseY;

    public static void main(String[] args) {
        PersonManager pm = new PersonManager();
        JFrame fr = new JFrame();
        fr.setUndecorated(true);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(800, 400); 
        fr.setResizable(false); 
        
        RegisterPanelPassanger panel = new RegisterPanelPassanger(pm);
        fr.getContentPane().add(panel);
        
        fr.setLocationRelativeTo(null);
        fr.setVisible(true);
    }

    public RegisterPanelPassanger(PersonManager pm) {
        this.personManager = pm;
        
        setBackground(new Color(24, 26, 27)); 
        setLayout(null);
        setPreferredSize(new Dimension(800, 400));

        Font inkFreeBold = new Font("Ink Free", Font.BOLD, 16);
        Font inkFreeLarge = new Font("Ink Free", Font.BOLD, 22);

        // Pencere Taşıma
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Window window = SwingUtilities.windowForComponent(RegisterPanelPassanger.this);
                if (window != null) {
                    window.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });

        // Sol Bölüm (PP)
        JPanel ppContainer = new JPanel();
        ppContainer.setBackground(new Color(45, 48, 50));
        ppContainer.setBounds(50, 60, 200, 200);
        ppContainer.setLayout(new BorderLayout());
        add(ppContainer);

        lblPPPreview = new JLabel("Resim Seçilmedi", SwingConstants.CENTER);
        lblPPPreview.setFont(new Font("Ink Free", Font.BOLD, 12));
        lblPPPreview.setForeground(Color.LIGHT_GRAY);
        ppContainer.add(lblPPPreview);

        JButton btnAddPP = new JButton("PP EKLE");
        btnAddPP.setBackground(Color.YELLOW); 
        btnAddPP.setForeground(Color.BLACK);
        btnAddPP.setFont(inkFreeBold);
        btnAddPP.setBounds(50, 275, 200, 35);
        btnAddPP.setFocusPainted(false);
        btnAddPP.setBorder(null);
        add(btnAddPP);

        // Sağ Bölüm (Form)
        JLabel lblTitle = new JLabel("YOLCU KAYIT SİSTEMİ");
        lblTitle.setForeground(new Color(255, 215, 0)); 
        lblTitle.setFont(inkFreeLarge);
        lblTitle.setBounds(350, 20, 350, 40);
        add(lblTitle);

        JLabel lblName = new JLabel("Tam İsim:");
        lblName.setFont(inkFreeBold);
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(320, 80, 120, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtName.setBounds(460, 80, 250, 30);
        add(txtName);

        JLabel lblMail = new JLabel("E-Posta:");
        lblMail.setFont(inkFreeBold);
        lblMail.setForeground(Color.WHITE);
        lblMail.setBounds(320, 125, 120, 25);
        add(lblMail);

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtEmail.setBounds(460, 125, 250, 30);
        add(txtEmail);

        JLabel lblPass = new JLabel("Parola (Sayı):");
        lblPass.setFont(inkFreeBold);
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(320, 170, 120, 25);
        add(lblPass);

        // JTextField -> JPasswordField olarak güncellendi
        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtPass.setBounds(460, 170, 250, 30);
        add(txtPass);

        JLabel lblBirth = new JLabel("Doğum Tarihi:");
        lblBirth.setFont(inkFreeBold);
        lblBirth.setForeground(Color.WHITE);
        lblBirth.setBounds(320, 215, 120, 25);
        add(lblBirth);

        txtBirthDate = new JFormattedTextField(DateTimeFormatter.ofPattern("dd/MM/yyyy").toFormat());
        txtBirthDate.setText("01/01/1900"); 
        txtBirthDate.setBounds(460, 215, 250, 30);
        add(txtBirthDate);

        JLabel lblGender = new JLabel("Cinsiyet:");
        lblGender.setFont(inkFreeBold);
        lblGender.setForeground(Color.WHITE);
        lblGender.setBounds(320, 260, 120, 25);
        add(lblGender);

        rdbtnFemale = new JRadioButton("Kadın");
        rdbtnFemale.setFont(inkFreeBold);
        rdbtnFemale.setForeground(Color.WHITE);
        rdbtnFemale.setOpaque(false);
        rdbtnFemale.setBounds(460, 260, 100, 25);
        add(rdbtnFemale);

        rdbtnMale = new JRadioButton("Erkek");
        rdbtnMale.setFont(inkFreeBold);
        rdbtnMale.setForeground(Color.WHITE);
        rdbtnMale.setOpaque(false);
        rdbtnMale.setBounds(570, 260, 100, 25);
        add(rdbtnMale);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rdbtnFemale);
        genderGroup.add(rdbtnMale);

        // Alt Butonlar
        JButton btnCancel = new JButton("İptal");
        btnCancel.setBackground(new Color(220, 53, 69)); 
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(inkFreeBold);
        btnCancel.setBounds(460, 320, 110, 40);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(null);
        add(btnCancel);

        JButton btnRegister = new JButton("Kayıt Ol");
        btnRegister.setBackground(new Color(40, 167, 69)); 
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(inkFreeBold);
        btnRegister.setBounds(600, 320, 110, 40);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(null);
        add(btnRegister);

        // Fonksiyonlar
        btnAddPP.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Resimler", "jpg", "png", "jpeg");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = chooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(new ImageIcon(selectedImageFile.getAbsolutePath())
                        .getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                lblPPPreview.setIcon(icon);
                lblPPPreview.setText("");
            }
        });

        btnCancel.addActionListener(e -> {
            Window window = SwingUtilities.windowForComponent(this);
            if (window != null) window.dispose();
        });

        btnRegister.addActionListener(e -> {
            if (validateFields()) {
                try {
                    String name = txtName.getText();
                    String email = txtEmail.getText();
                    
                    // JPasswordField verisini güvenli bir şekilde String'e çeviriyoruz
                    String passStr = new String(txtPass.getPassword());
                    int pass = Integer.parseInt(passStr);
                    
                    int gender = rdbtnMale.isSelected() ? 1 : 0;
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate birthDate = LocalDate.parse(txtBirthDate.getText(), formatter);

                    ImageIcon finalPP;
                    if (selectedImageFile != null) {
                        finalPP = new ImageIcon(selectedImageFile.getAbsolutePath());
                    } else {
                        finalPP = personManager.getDefaultImage(gender);
                    }

                    Passanger p = new Passanger(name, pass, email, gender, finalPP, birthDate);
                    personManager.addPerson(p);
                    
                    JOptionPane.showMessageDialog(this, "Hoşgeldin " + name + "!\nID'niz: " + p.getID());
                    
                    Window window = SwingUtilities.windowForComponent(this);
                    if (window != null) window.dispose();
                    
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Parola sadece rakamlardan oluşmalıdır!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Bilgileri kontrol edin! Tarih formatı: GG/AA/YYYY");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen gerekli alanları doldurun!");
            }
        });
    }

    private boolean validateFields() {
        return !txtName.getText().isEmpty() && 
               !txtEmail.getText().isEmpty() && 
               txtPass.getPassword().length > 0 && // Şifre doluluk kontrolü
               (rdbtnMale.isSelected() || rdbtnFemale.isSelected());
    }
}

