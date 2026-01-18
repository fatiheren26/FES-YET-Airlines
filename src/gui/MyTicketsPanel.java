package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class MyTicketsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private Passanger passanger;
    private ReservationManager resManager;
    private JPanel ticketsContainer;

    // Renk Teması (PassengerFrame ile uyumlu)
    private static final Color BG_COLOR = new Color(18, 18, 20);
    private static final Color CARD_COLOR = new Color(28, 28, 32);
    private static final Color ACCENT_BLUE = new Color(99, 102, 241);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color TEXT_MAIN = new Color(240, 240, 240);
    private static final Color TEXT_SUB = new Color(150, 150, 155);
    private static final Color BORDER_COLOR = new Color(50, 50, 55);

    public MyTicketsPanel(Passanger passanger, ReservationManager resManager) {
        this.passanger = passanger;
        this.resManager = resManager;

        setLayout(new BorderLayout(0, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initializeUI();
        loadTickets();
    }

    private void initializeUI() {
        // === BAŞLIK ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🎫 Biletlerim");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Satın aldığınız tüm biletler burada listelenir");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SUB);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(5));
        titleGroup.add(subtitleLabel);

        // Yenile butonu
        JButton refreshBtn = new JButton("🔄 Yenile");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(ACCENT_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadTickets());

        headerPanel.add(titleGroup, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // === BİLET KARTLARI CONTAINER ===
        ticketsContainer = new JPanel();
        ticketsContainer.setLayout(new BoxLayout(ticketsContainer, BoxLayout.Y_AXIS));
        ticketsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(ticketsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Biletleri yükler ve gösterir
     */
    public void loadTickets() {
        ticketsContainer.removeAll();

        List<Ticket> tickets = resManager.getTicketsByPassenger(passanger);

        if (tickets.isEmpty()) {
            // Boş durum
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);

            JLabel emptyIcon = new JLabel("🎫");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));

            JLabel emptyText = new JLabel("Henüz biletiniz bulunmuyor");
            emptyText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            emptyText.setForeground(TEXT_SUB);

            JLabel emptySubText = new JLabel("Uçuş arayarak ilk biletinizi alın!");
            emptySubText.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptySubText.setForeground(new Color(100, 100, 105));

            JPanel emptyContent = new JPanel();
            emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
            emptyContent.setOpaque(false);
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptySubText.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyContent.add(emptyIcon);
            emptyContent.add(Box.createVerticalStrut(15));
            emptyContent.add(emptyText);
            emptyContent.add(Box.createVerticalStrut(5));
            emptyContent.add(emptySubText);

            emptyPanel.add(emptyContent);
            ticketsContainer.add(emptyPanel);
        } else {
            // Bilet kartlarını oluştur
            for (Ticket ticket : tickets) {
                ticketsContainer.add(createTicketCard(ticket));
                ticketsContainer.add(Box.createVerticalStrut(15));
            }
        }

        ticketsContainer.revalidate();
        ticketsContainer.repaint();
    }

    /**
     * Tek bir bilet kartı oluşturur
     */
    private JPanel createTicketCard(Ticket ticket) {
        Reservation res = ticket.getReservation();
        Flight flight = res.getFlight();
        Seat seat = res.getSeat();

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // === SOL KISIM: Uçuş Bilgileri ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        // Rota
        String routeText = flight.getRoute().getDeparture().getCity() + " → " +
                flight.getRoute().getArrival().getCity();
        JLabel routeLabel = new JLabel(routeText);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        routeLabel.setForeground(TEXT_MAIN);

        // Tarih ve saat
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String dateTimeText = "📅 " + flight.getDepartureDate().format(dateFormatter) +
                "  ⏰ " + flight.getDepartureTime().format(timeFormatter);
        JLabel dateTimeLabel = new JLabel(dateTimeText);
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTimeLabel.setForeground(TEXT_SUB);

        // Uçak bilgisi
        String planeText = "✈️ " + flight.getPlane().getPlaneModel().getFullModelName();
        JLabel planeLabel = new JLabel(planeText);
        planeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        planeLabel.setForeground(new Color(100, 100, 105));

        leftPanel.add(routeLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(dateTimeLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(planeLabel);

        card.add(leftPanel, BorderLayout.CENTER);

        // === ORTA KISIM: Koltuk ve Sınıf ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Koltuk numarası
        JLabel seatLabel = new JLabel(seat.getSeatCode());
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        seatLabel.setForeground(ACCENT_BLUE);
        seatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sınıf
        boolean isBusiness = seat.getTicketClass() == TicketClass.BUSINESS;
        JLabel classLabel = new JLabel(isBusiness ? "Business" : "Economy");
        classLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        classLabel.setForeground(isBusiness ? ACCENT_ORANGE : ACCENT_GREEN);
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(seatLabel);
        centerPanel.add(classLabel);
        centerPanel.add(Box.createVerticalGlue());

        card.add(centerPanel, BorderLayout.EAST);

        // === SAĞ KISIM: Bilet ID ve Fiyat ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(150, 0));

        // Bilet ID
        JLabel ticketIdLabel = new JLabel(ticket.getTicketID());
        ticketIdLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        ticketIdLabel.setForeground(ACCENT_BLUE);
        ticketIdLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Fiyat
        JLabel priceLabel = new JLabel(String.format("%.0f ₺", ticket.getfinalprice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(ACCENT_GREEN);
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Bagaj hakkı
        JLabel baggageLabel = new JLabel("🧳 " + (int) ticket.getBaggageAllowance().getBaggageAllowance() + " kg");
        baggageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        baggageLabel.setForeground(TEXT_SUB);
        baggageLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(ticketIdLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(priceLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(baggageLabel);

        // Sağ panel için wrapper (sağa hizalama)
        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrapper.setOpaque(false);
        rightWrapper.add(rightPanel);

        card.add(rightWrapper, BorderLayout.EAST);

        return card;
    }
}


