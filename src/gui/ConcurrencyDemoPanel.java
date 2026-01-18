package gui;

import models.*;
import managers.*;
import models.*;
import managers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ConcurrencyDemoPanel extends JPanel {

    // === SENARYO 1 SABİTLERİ ===
    private static final int ROWS = 30;
    private static final int COLS = 6;
    private static final int TOTAL_SEATS = ROWS * COLS; // 180
    private static final int PASSENGER_COUNT = 90;

    // SeatViewerBasic'tan alınan sabitler
    private final String IMAGE_PATH = "savesAndImage/Image/";
    private final int SEAT_SIZE = 35;
    private static ImageIcon E_EMPTY, E_FULL;

    // Koltuk durumları: 0 = boş, pozitif sayı = yolcu ID
    private int[] seats;
    private final Object lock = new Object();

    // Senaryo 1 UI Bileşenleri
    private JPanel seatContainer;
    private JLabel[][] seatLabels;
    private JLabel statusLabel;
    private JLabel occupiedLabel;
    private JLabel emptyLabel;
    private JLabel conflictLabel;
    private JCheckBox syncCheckBox;
    private JButton startButton;
    private JProgressBar progressBar;

    // Senaryo 2 UI Bileşenleri
    private JButton reportButton;
    private JTextArea reportTextArea;
    private JLabel reportStatusLabel;
    private JProgressBar reportProgressBar;

    // Renkler
    private Color bgColor = new Color(25, 25, 30);
    private Color panelBgColor = new Color(40, 44, 52);
    private Color emptyColor = new Color(76, 175, 80);
    private Color occupiedColor = new Color(244, 67, 54);
    private Color conflictColor = new Color(255, 152, 0);

    private volatile boolean isRunning = false;
    private volatile boolean isReportRunning = false;
    private int conflictCount = 0;

    public ConcurrencyDemoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(bgColor);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        loadIcons();
        initializeSeats();
        createUI();
    }

    private void loadIcons() {
        E_EMPTY = getScaledIcon(IMAGE_PATH + "E_EMPTY.png");
        E_FULL = getScaledIcon(IMAGE_PATH + "E_FULL.png");
    }

    private ImageIcon getScaledIcon(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Dosya bulunamadı: " + path);
                return null;
            }
            Image img = ImageIO.read(file);
            if (img == null)
                return null;

            Image scaledImg = img.getScaledInstance(SEAT_SIZE, SEAT_SIZE, Image.SCALE_SMOOTH);

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

    private void initializeSeats() {
        seats = new int[TOTAL_SEATS];
        for (int i = 0; i < TOTAL_SEATS; i++) {
            seats[i] = 0;
        }
    }

    private void createUI() {
        // Ana başlık
        JLabel mainTitle = new JLabel("Thread & Concurrency Demo", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setBorder(new EmptyBorder(5, 0, 10, 0));
        add(mainTitle, BorderLayout.NORTH);

        // İki senaryo için split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(550);
        splitPane.setBackground(bgColor);
        splitPane.setBorder(null);

        // Sol panel: Senaryo 1 - Koltuk Rezervasyonu
        JPanel scenario1Panel = createScenario1Panel();
        splitPane.setLeftComponent(scenario1Panel);

        // Sağ panel: Senaryo 2 - Rapor Oluşturma
        JPanel scenario2Panel = createScenario2Panel();
        splitPane.setRightComponent(scenario2Panel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createScenario1Panel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(panelBgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Başlık ve kontroller
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(panelBgColor);

        JLabel titleLabel = new JLabel("Senaryo 1: Eşzamanlı Koltuk Rezervasyonu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 149, 237));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(panelBgColor);

        syncCheckBox = new JCheckBox("Synchronized");
        syncCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        syncCheckBox.setForeground(Color.WHITE);
        syncCheckBox.setBackground(panelBgColor);
        syncCheckBox.setSelected(true);
        controlPanel.add(syncCheckBox);

        startButton = new JButton("▶ Başlat");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        startButton.setBackground(emptyColor);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startSimulation());
        controlPanel.add(startButton);

        JLabel infoLabel = new JLabel("(" + TOTAL_SEATS + " koltuk, " + PASSENGER_COUNT + " yolcu)");
        infoLabel.setForeground(new Color(150, 150, 150));
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        controlPanel.add(infoLabel);

        topPanel.add(controlPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Koltuk Grid
        seatContainer = new JPanel();
        seatContainer.setLayout(new BoxLayout(seatContainer, BoxLayout.Y_AXIS));
        seatContainer.setBackground(bgColor);

        createSeatGrid();

        JScrollPane scrollPane = new JScrollPane(seatContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(bgColor);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Alt panel: İstatistikler
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(panelBgColor);

        progressBar = new JProgressBar(0, PASSENGER_COUNT);
        progressBar.setStringPainted(true);
        progressBar.setString("Hazır");
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        bottomPanel.add(progressBar, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        statsPanel.setBackground(panelBgColor);

        emptyLabel = new JLabel(String.valueOf(TOTAL_SEATS), SwingConstants.CENTER);
        emptyLabel.setForeground(emptyColor);
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsPanel.add(createMiniStatCard("Boş", emptyLabel));

        occupiedLabel = new JLabel("0", SwingConstants.CENTER);
        occupiedLabel.setForeground(occupiedColor);
        occupiedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsPanel.add(createMiniStatCard("Dolu", occupiedLabel));

        conflictLabel = new JLabel("0", SwingConstants.CENTER);
        conflictLabel.setForeground(conflictColor);
        conflictLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsPanel.add(createMiniStatCard("Çakışma", conflictLabel));

        statusLabel = new JLabel("Bekleniyor", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(100, 149, 237));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statsPanel.add(createMiniStatCard("Durum", statusLabel));

        bottomPanel.add(statsPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMiniStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setForeground(new Color(120, 120, 120));
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void createSeatGrid() {
        seatContainer.removeAll();
        seatLabels = new JLabel[ROWS][COLS];

        for (int row = 0; row < ROWS; row++) {
            JPanel rowPanel = createRowPanel(row);
            seatContainer.add(rowPanel);
        }

        seatContainer.revalidate();
        seatContainer.repaint();
    }

    private JPanel createRowPanel(int rowIndex) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        rowPanel.setOpaque(false);

        JLabel lblNum = new JLabel(String.valueOf(rowIndex + 1), SwingConstants.RIGHT);
        lblNum.setForeground(new Color(150, 150, 150));
        lblNum.setFont(new Font("Monospaced", Font.PLAIN, 9));
        lblNum.setPreferredSize(new Dimension(18, SEAT_SIZE));
        rowPanel.add(lblNum);

        String[] leftCols = { "A", "B", "C" };
        for (int i = 0; i < leftCols.length; i++) {
            int colIndex = i;
            JLabel seatLabel = createSeatLabel(rowIndex, colIndex, leftCols[i] + (rowIndex + 1));
            seatLabels[rowIndex][colIndex] = seatLabel;
            rowPanel.add(seatLabel);
        }

        rowPanel.add(Box.createHorizontalStrut(10));

        String[] rightCols = { "D", "E", "F" };
        for (int i = 0; i < rightCols.length; i++) {
            int colIndex = i + 3;
            JLabel seatLabel = createSeatLabel(rowIndex, colIndex, rightCols[i] + (rowIndex + 1));
            seatLabels[rowIndex][colIndex] = seatLabel;
            rowPanel.add(seatLabel);
        }

        return rowPanel;
    }

    private JLabel createSeatLabel(int row, int col, String seatCode) {
        JLabel seatLabel = new JLabel();
        seatLabel.setPreferredSize(new Dimension(SEAT_SIZE, SEAT_SIZE));
        seatLabel.setIcon(E_EMPTY);
        seatLabel.setToolTipText(seatCode);
        return seatLabel;
    }

    private JPanel createScenario2Panel() {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBackground(panelBgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Başlık
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(panelBgColor);

        JLabel titleLabel = new JLabel("Senaryo 2: Asenkron Rapor Oluşturma");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 149, 237));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel(
                "<html>ReportGenerator Thread ile arka planda<br>rapor oluşturma (GUI bloklanmaz)</html>");
        descLabel.setForeground(new Color(150, 150, 150));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        topPanel.add(descLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(panelBgColor);

        reportButton = new JButton("📊 Rapor Oluştur");
        reportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        reportButton.setBackground(new Color(100, 149, 237));
        reportButton.setForeground(Color.WHITE);
        reportButton.setFocusPainted(false);
        reportButton.addActionListener(e -> startReportGeneration());
        buttonPanel.add(reportButton);

        reportStatusLabel = new JLabel("Hazır");
        reportStatusLabel.setForeground(new Color(150, 150, 150));
        reportStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        buttonPanel.add(reportStatusLabel);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Rapor çıktısı
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setBackground(bgColor);
        reportTextArea.setForeground(new Color(200, 200, 200));
        reportTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        reportTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reportTextArea.setText("Rapor oluşturmak için butona tıklayın...\n\n" +
                "Bu işlem ReportGenerator Thread'inde çalışır.\n" +
                "GUI Thread bloklanmaz, arayüz kullanılabilir kalır.");

        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Progress bar
        reportProgressBar = new JProgressBar();
        reportProgressBar.setStringPainted(true);
        reportProgressBar.setString("Hazır");
        reportProgressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        reportProgressBar.setIndeterminate(false);
        panel.add(reportProgressBar, BorderLayout.SOUTH);

        return panel;
    }

    private void startSimulation() {
        if (isRunning)
            return;

        isRunning = true;
        startButton.setEnabled(false);
        syncCheckBox.setEnabled(false);
        startButton.setText("⏳ Çalışıyor...");

        initializeSeats();
        conflictCount = 0;
        updateSeatDisplay();
        progressBar.setValue(0);
        progressBar.setString("Başlatılıyor...");
        statusLabel.setText("Çalışıyor...");
        statusLabel.setForeground(new Color(100, 149, 237));

        boolean isSynchronized = syncCheckBox.isSelected();

        Thread simulationThread = new Thread(() -> runSimulation(isSynchronized));
        simulationThread.start();
    }

    private void runSimulation(boolean isSynchronized) {
        List<Integer> availableSeats = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < TOTAL_SEATS; i++) {
            availableSeats.add(i);
        }

        CountDownLatch latch = new CountDownLatch(PASSENGER_COUNT);
        Random random = new Random();

        for (int p = 0; p < PASSENGER_COUNT; p++) {
            final int passengerId = p + 1;

            Thread passengerThread = new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(50) + 10);

                    if (isSynchronized) {
                        reserveSeatSynchronized(availableSeats, passengerId);
                    } else {
                        reserveSeatUnsynchronized(availableSeats, passengerId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();

                    SwingUtilities.invokeLater(() -> {
                        int completed = PASSENGER_COUNT - (int) latch.getCount();
                        progressBar.setValue(completed);
                        progressBar.setString(completed + "/" + PASSENGER_COUNT);
                        updateStats();
                    });
                }
            });

            passengerThread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SwingUtilities.invokeLater(() -> {
            isRunning = false;
            startButton.setEnabled(true);
            syncCheckBox.setEnabled(true);
            startButton.setText("🔄 Tekrar");
            progressBar.setString("Tamamlandı!");

            int occupied = countOccupied();

            if (isSynchronized) {
                statusLabel.setText("✓ Başarılı");
                statusLabel.setForeground(emptyColor);
            } else {
                if (occupied != PASSENGER_COUNT) {
                    statusLabel.setText("⚠ Hata!");
                    statusLabel.setForeground(conflictColor);
                } else {
                    statusLabel.setText("✓ Şanslı");
                    statusLabel.setForeground(emptyColor);
                }
            }

            updateSeatDisplay();
            updateStats();
        });
    }

    private void reserveSeatSynchronized(List<Integer> availableSeats, int passengerId) {
        synchronized (lock) {
            if (availableSeats.isEmpty())
                return;

            int randomIndex = new Random().nextInt(availableSeats.size());
            int seatIndex = availableSeats.remove(randomIndex);

            seats[seatIndex] = passengerId;

            final int row = seatIndex / COLS;
            final int col = seatIndex % COLS;
            SwingUtilities.invokeLater(() -> {
                if (seatLabels[row][col] != null) {
                    seatLabels[row][col].setIcon(E_FULL);
                }
            });
        }
    }

    private void reserveSeatUnsynchronized(List<Integer> availableSeats, int passengerId) {
        if (availableSeats.isEmpty())
            return;

        try {
            Thread.sleep(new Random().nextInt(20));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            if (availableSeats.isEmpty())
                return;
            int randomIndex = new Random().nextInt(availableSeats.size());

            Thread.sleep(new Random().nextInt(10));

            if (randomIndex >= availableSeats.size()) {
                randomIndex = availableSeats.isEmpty() ? -1 : availableSeats.size() - 1;
            }

            if (randomIndex < 0 || availableSeats.isEmpty())
                return;

            int seatIndex = availableSeats.get(randomIndex);

            Thread.sleep(new Random().nextInt(15));

            if (seats[seatIndex] != 0) {
                conflictCount++;
                return;
            }

            seats[seatIndex] = passengerId;
            availableSeats.remove(Integer.valueOf(seatIndex));

            final int row = seatIndex / COLS;
            final int col = seatIndex % COLS;
            SwingUtilities.invokeLater(() -> {
                if (seatLabels[row][col] != null) {
                    seatLabels[row][col].setIcon(E_FULL);
                }
            });

        } catch (Exception e) {
            conflictCount++;
        }
    }

    private int countOccupied() {
        int count = 0;
        for (int seat : seats) {
            if (seat != 0)
                count++;
        }
        return count;
    }

    private void updateSeatDisplay() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int seatIndex = row * COLS + col;
                if (seatLabels[row][col] != null) {
                    seatLabels[row][col].setIcon(seats[seatIndex] == 0 ? E_EMPTY : E_FULL);
                }
            }
        }
    }

    private void updateStats() {
        int occupied = countOccupied();
        int empty = TOTAL_SEATS - occupied;

        occupiedLabel.setText(String.valueOf(occupied));
        emptyLabel.setText(String.valueOf(empty));
        conflictLabel.setText(String.valueOf(conflictCount));
    }

    private void startReportGeneration() {
        if (isReportRunning)
            return;

        isReportRunning = true;
        reportButton.setEnabled(false);
        reportButton.setText("⏳ Oluşturuluyor...");
        reportStatusLabel.setText("Rapor hazırlanıyor...");
        reportStatusLabel.setForeground(conflictColor);
        reportProgressBar.setIndeterminate(true);
        reportProgressBar.setString("Rapor hazırlanıyor...");
        reportTextArea.setText("⏳ Rapor oluşturuluyor, lütfen bekleyin...\n\n" +
                "ReportGenerator Thread çalışıyor...\n" +
                "(GUI hâlâ kullanılabilir durumda)");

        // ReportGenerator Thread'i başlat
        Thread reportGeneratorThread = new ReportGeneratorThread();
        reportGeneratorThread.start();
    }

    private class ReportGeneratorThread extends Thread {

        @Override
        public void run() {
            StringBuilder report = new StringBuilder();

            try {
                report.append("╔══════════════════════════════════════════════════════╗\n");
                report.append("║        UÇUŞ DOLULUK ORANI RAPORU                     ║\n");
                report.append("║        Oluşturma Tarihi: ").append(java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append("   ║\n");
                report.append("╚══════════════════════════════════════════════════════╝\n\n");

                // Adım 1: Uçuş verilerini toplama
                updateReportStatus("Uçuş verileri toplanıyor...");
                Thread.sleep(800);

                report.append("📊 GENEL İSTATİSTİKLER\n");
                report.append("─────────────────────────────────────────\n");

                // Adım 2: Koltuk analizleri
                updateReportStatus("Koltuk analizleri yapılıyor...");
                Thread.sleep(600);

                int totalSeats = TOTAL_SEATS;
                int occupiedSeats = countOccupied();
                int emptySeats = totalSeats - occupiedSeats;
                double occupancyRate = (occupiedSeats * 100.0) / totalSeats;

                report.append(String.format("  Toplam Koltuk Sayısı    : %d\n", totalSeats));
                report.append(String.format("  Dolu Koltuk Sayısı      : %d\n", occupiedSeats));
                report.append(String.format("  Boş Koltuk Sayısı       : %d\n", emptySeats));
                report.append(String.format("  Doluluk Oranı           : %.2f%%\n\n", occupancyRate));

                // Adım 3: Satır bazlı analiz
                updateReportStatus("Satır bazlı analiz yapılıyor...");
                Thread.sleep(700);

                report.append("📋 SATIR BAZLI DOLULUK\n");
                report.append("─────────────────────────────────────────\n");

                for (int row = 0; row < ROWS; row++) {
                    int rowOccupied = 0;
                    for (int col = 0; col < COLS; col++) {
                        if (seats[row * COLS + col] != 0)
                            rowOccupied++;
                    }
                    double rowRate = (rowOccupied * 100.0) / COLS;
                    String bar = generateProgressBar(rowRate);
                    report.append(String.format("  Sıra %2d: %s %.0f%% (%d/%d)\n",
                            row + 1, bar, rowRate, rowOccupied, COLS));

                    // Her 5 satırda bir kısa gecikme
                    if (row % 5 == 0) {
                        Thread.sleep(100);
                    }
                }

                // Adım 4: Performans metrikleri
                updateReportStatus("Performans metrikleri hesaplanıyor...");
                Thread.sleep(500);

                report.append("\n📈 PERFORMANS METRİKLERİ\n");
                report.append("─────────────────────────────────────────\n");
                report.append(String.format("  Thread Sayısı           : %d\n", PASSENGER_COUNT));
                report.append(String.format("  Çakışma Sayısı          : %d\n", conflictCount));
                report.append(String.format("  Başarı Oranı            : %.2f%%\n",
                        ((PASSENGER_COUNT - conflictCount) * 100.0) / PASSENGER_COUNT));
                report.append(String.format("  Synchronized Mod        : %s\n",
                        syncCheckBox.isSelected() ? "Aktif ✓" : "Pasif ✗"));

                // Adım 5: Sonuç
                updateReportStatus("Rapor tamamlanıyor...");
                Thread.sleep(400);

                report.append("\n╔══════════════════════════════════════════════════════╗\n");
                report.append("║  ✓ RAPOR BAŞARIYLA OLUŞTURULDU                       ║\n");
                report.append("║  ReportGenerator Thread tamamlandı.                  ║\n");
                report.append("╚══════════════════════════════════════════════════════╝\n");

            } catch (InterruptedException e) {
                report.append("\n⚠ Rapor oluşturma işlemi iptal edildi!");
                Thread.currentThread().interrupt();
            }

            // Sonucu GUI Thread'e gönder (asenkron bildirim)
            final String finalReport = report.toString();
            SwingUtilities.invokeLater(() -> {
                reportTextArea.setText(finalReport);
                reportTextArea.setCaretPosition(0);
                reportButton.setEnabled(true);
                reportButton.setText("📊 Rapor Oluştur");
                reportStatusLabel.setText("✓ Rapor hazır!");
                reportStatusLabel.setForeground(emptyColor);
                reportProgressBar.setIndeterminate(false);
                reportProgressBar.setValue(100);
                reportProgressBar.setString("Tamamlandı!");
                isReportRunning = false;
            });
        }

        private void updateReportStatus(String status) {
            SwingUtilities.invokeLater(() -> {
                reportStatusLabel.setText(status);
            });
        }

        private String generateProgressBar(double percentage) {
            int filled = (int) (percentage / 10);
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < 10; i++) {
                bar.append(i < filled ? "█" : "░");
            }
            bar.append("]");
            return bar.toString();
        }
    }
}
