package gui;
import models.*;
import managers.*;
import models.*;
import managers.*;


import javax.swing.*;
import java.awt.*;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;

public class SeatViewerBasic extends JPanel {
    protected Flight flight;
    protected Map<String, Seat> seatMap;
    protected final String IMAGE_PATH = "savesAndImage/Image/";
    protected static ImageIcon B_EMPTY, B_FULL, E_EMPTY, E_FULL;
    protected final int SEAT_SIZE = 35; 

    public SeatViewerBasic(Flight flight) {
        this.flight = flight;
        this.seatMap = flight.getSeatMap();
        loadIcons();
        initializeUI();
    }

    protected void loadIcons() {
        B_EMPTY = getScaledIcon(IMAGE_PATH + "B_EMPTY.png");
        B_FULL = getScaledIcon(IMAGE_PATH + "B_FULL.png");
        E_EMPTY = getScaledIcon(IMAGE_PATH + "E_EMPTY.png");
        E_FULL = getScaledIcon(IMAGE_PATH + "E_FULL.png");
    }

    protected ImageIcon getScaledIcon(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Dosya bulunamadı: " + path);
                return null;
            }
            // ImageIO.read() senkron çalışır, resim yüklenene kadar bekler.
            Image img = ImageIO.read(file);
            if (img == null) return null;

            Image scaledImg = img.getScaledInstance(SEAT_SIZE, SEAT_SIZE, Image.SCALE_SMOOTH);
            
            // Ölçeklenmiş resmin de hazır olduğundan emin oluyoruz.
            ImageIcon icon = new ImageIcon(scaledImg);
            while (icon.getImageLoadStatus() == MediaTracker.LOADING) {
                Thread.sleep(5);
            }
            return icon;
        } catch (Exception e) {
            System.err.println("İkon yükleme hatası (" + path + "): " + e.getMessage());
            return null;
        }
    }

    protected void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 30));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        PlaneModel model = flight.getPlane().getPlaneModel();

        container.add(createSectionTitle("Business Class"));
        for (int i = 1; i <= model.getBusinessRow(); i++) {
            container.add(createRowPanel(i, new String[]{"A", "B"}, new String[]{"C", "D"}));
        }

        container.add(Box.createVerticalStrut(15)); 

        container.add(createSectionTitle("Economy Class"));
        int ecoStart = model.getBusinessRow() + 1;
        int ecoEnd = model.getBusinessRow() + model.getEconomyRow();
        for (int i = ecoStart; i <= ecoEnd; i++) {
            container.add(createRowPanel(i, new String[]{"A", "B", "C"}, new String[]{"D", "E", "F"}));
        }

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(new Color(25, 25, 30));
        add(scroll, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private JPanel createRowPanel(int rowNum, String[] leftGroup, String[] rightGroup) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 1));
        rowPanel.setOpaque(false);

        JLabel lblNum = new JLabel(String.valueOf(rowNum), SwingConstants.RIGHT);
        lblNum.setForeground(new Color(150, 150, 150));
        lblNum.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblNum.setPreferredSize(new Dimension(20, SEAT_SIZE));
        rowPanel.add(lblNum);

        for (String c : leftGroup) rowPanel.add(createSeatButton(c + rowNum));
        rowPanel.add(Box.createHorizontalStrut(15)); 
        for (String c : rightGroup) rowPanel.add(createSeatButton(c + rowNum));

        return rowPanel;
    }

    protected JComponent createSeatButton(String code) {
        Seat seat = seatMap.get(code);
        JLabel seatLabel = new JLabel();
        seatLabel.setPreferredSize(new Dimension(SEAT_SIZE, SEAT_SIZE));
        if (seat != null) {
            seatLabel.setIcon(seat.getTicketClass() == TicketClass.BUSINESS ? 
                (seat.isReserved() ? B_FULL : B_EMPTY) : 
                (seat.isReserved() ? E_FULL : E_EMPTY));
        }
        return seatLabel;
    }

    private JLabel createSectionTitle(String title) {
        JLabel l = new JLabel(title);
        l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return l;
    }
}

