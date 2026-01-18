package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;

/**
 * Yolcunun profil bilgilerini kimlik kartı tarzında gösteren panel
 */
public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private Passanger passanger;

    // Renk Teması
    private static final Color BG_COLOR = new Color(18, 18, 20);
    private static final Color CARD_BG = new Color(25, 35, 55);
    private static final Color CARD_HEADER = new Color(99, 102, 241);
    private static final Color ACCENT_GOLD = new Color(212, 175, 55);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_SUB = new Color(180, 180, 185);
    private static final Color BORDER_COLOR = new Color(60, 70, 90);

    public ProfilePanel(Passanger passanger) {
        this.passanger = passanger;

        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);

        add(createIdCard());
    }

    /**
     * Kimlik kartı tasarımı
     */
    private JPanel createIdCard() {
        // Ana kart container
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(450, 280));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // === HEADER (Mavi şerit) ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_HEADER);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblAirline = new JLabel("✈ FES-YET AIRLINES");
        lblAirline.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAirline.setForeground(Color.WHITE);

        JLabel lblCardType = new JLabel("YOLCU KİMLİK KARTI");
        lblCardType.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCardType.setForeground(new Color(200, 200, 255));

        header.add(lblAirline, BorderLayout.WEST);
        header.add(lblCardType, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        // === BODY ===
        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Sol: Fotoğraf
        JPanel photoPanel = createPhotoPanel();
        body.add(photoPanel, BorderLayout.WEST);

        // Sağ: Bilgiler
        JPanel infoPanel = createInfoPanel();
        body.add(infoPanel, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);

        // === FOOTER (Gold şerit) ===
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(35, 45, 65));
        footer.setPreferredSize(new Dimension(0, 45));
        footer.setBorder(new EmptyBorder(8, 20, 8, 20));

        // Bakiye
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        balancePanel.setOpaque(false);

        JLabel lblBalanceIcon = new JLabel("💰");
        lblBalanceIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblBalance = new JLabel(String.format("%.2f ₺", passanger.getBalance()));
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBalance.setForeground(ACCENT_GREEN);

        balancePanel.add(lblBalanceIcon);
        balancePanel.add(lblBalance);

        // Puan
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pointsPanel.setOpaque(false);

        JLabel lblPointsIcon = new JLabel("⭐");
        lblPointsIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel lblPoints = new JLabel(passanger.getLoyaltyPoints() + " Puan");
        lblPoints.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPoints.setForeground(ACCENT_GOLD);

        pointsPanel.add(lblPointsIcon);
        pointsPanel.add(lblPoints);

        footer.add(balancePanel, BorderLayout.WEST);
        footer.add(pointsPanel, BorderLayout.EAST);

        card.add(footer, BorderLayout.SOUTH);

        // Gölge efekti için wrapper
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 10, 10),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 0)));
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Fotoğraf paneli
     */
    private JPanel createPhotoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 120));

        // Fotoğraf çerçevesi
        JPanel photoFrame = new JPanel(new GridBagLayout());
        photoFrame.setBackground(new Color(40, 50, 70));
        photoFrame.setBorder(BorderFactory.createLineBorder(ACCENT_GOLD, 2));
        photoFrame.setPreferredSize(new Dimension(95, 115));

        // Profil fotoğrafı veya varsayılan
        ImageIcon profilePic = passanger.getProfilePicture();
        JLabel lblPhoto;

        if (profilePic != null) {
            Image img = profilePic.getImage().getScaledInstance(85, 105, Image.SCALE_SMOOTH);
            lblPhoto = new JLabel(new ImageIcon(img));
        } else {
            // Varsayılan avatar
            lblPhoto = new JLabel(passanger.getGender() == 1 ? "👨" : "👩");
            lblPhoto.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        }

        photoFrame.add(lblPhoto);
        panel.add(photoFrame, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Bilgi paneli
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Ad Soyad
        JLabel lblName = new JLabel(passanger.getName().toUpperCase());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblName.setForeground(TEXT_MAIN);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ID Numarası
        JPanel idRow = createInfoRow("YOLCU NO", String.valueOf(passanger.getID()));

        // Doğum Tarihi
        LocalDate birthDate = passanger.getBirthDate();
        String birthDateStr = birthDate != null ? birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "Belirtilmemiş";
        JPanel birthRow = createInfoRow("DOĞUM TARİHİ", birthDateStr);

        // Yaş
        String ageStr = "—";
        if (birthDate != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            ageStr = age + " yaş";
        }
        JPanel ageRow = createInfoRow("YAŞ", ageStr);

        // E-posta
        String email = passanger.geteMail();
        if (email != null && email.length() > 25) {
            email = email.substring(0, 22) + "...";
        }
        JPanel emailRow = createInfoRow("E-POSTA", email != null ? email : "—");

        // Cinsiyet
        String gender = passanger.getGender() == 1 ? "Erkek" : "Kadın";
        JPanel genderRow = createInfoRow("CİNSİYET", gender);

        panel.add(lblName);
        panel.add(Box.createVerticalStrut(12));
        panel.add(idRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(birthRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(ageRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(emailRow);

        return panel;
    }

    /**
     * Bilgi satırı oluşturur
     */
    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(300, 20));

        JLabel lblLabel = new JLabel(label + ": ");
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_SUB);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValue.setForeground(TEXT_MAIN);

        row.add(lblLabel);
        row.add(lblValue);

        return row;
    }
}


