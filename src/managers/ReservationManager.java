package managers;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import models.*;

public class ReservationManager {
    private List<Reservation> reservations;
    private List<Ticket> tickets; // Biletler için liste eklendi

    // Dosya yolları - savesAndImage klasörüne kaydediliyor
    private final String RESERVATION_FILE = "savesAndImage/Assets/reservations.dat";
    private final String TICKET_FILE = "savesAndImage/Assets/tickets.dat";

    public ReservationManager() {
        this.reservations = loadReservations();
        this.tickets = loadTickets();
    }

    public synchronized boolean createReservation(Flight flight, Passanger passanger, String seatCode) {
        // 1. Uçuşa özel koltuk haritasını (Map) alıyoruz
        Map<String, Seat> flightSeats = flight.getSeatMap();

        if (flightSeats == null) {
            System.out.println("Hata: Uçuşun koltuk planı oluşturulmamış.");
            return false;
        }

        // 2. İlgili koltuğu haritadan "Key" (seatCode) ile çekiyoruz
        Seat seat = flightSeats.get(seatCode);

        // 3. Koltuk var mı kontrolü
        if (seat == null) {
            System.out.println("Hata: Geçersiz koltuk numarası: " + seatCode);
            return false;
        }

        // 4. Koltuk dolu mu kontrolü (Seat.java'daki isReserved metodu)
        if (seat.isReserved()) {
            System.out.println("Bilgi: " + seatCode + " nolu koltuk zaten dolu.");
            return false;
        }

        seat.reserve();

        // Benzersiz bir rezervasyon kodu oluştur
        String resCode = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Reservation nesnesini oluştur
        Reservation newReservation = new Reservation(resCode, flight, passanger, seat, LocalDateTime.now());
        reservations.add(newReservation);

        // 6. Rezervasyonları kaydet
        saveReservations();

        System.out
                .println("Başarılı: " + passanger.getName() + " için " + seatCode + " rezerve edildi. Kod: " + resCode);
        return true;
    }

    
    // Bilet oluşturur ve kaydeder
    
    public synchronized Ticket createTicket(Reservation reservation, double price) {
        String ticketID = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketID, reservation, price, reservation.getSeat().getAllowance());
        tickets.add(ticket);
        saveTickets();
        System.out.println("Bilet oluşturuldu: " + ticketID);
        return ticket;
    }

    /**
     * Rezervasyon ve bilet birlikte oluşturur
     */
    public synchronized Ticket createReservationWithTicket(Flight flight, Passanger passanger, String seatCode,
            double price) {
        if (createReservation(flight, passanger, seatCode)) {
            // Son eklenen rezervasyonu al
            Reservation lastRes = reservations.get(reservations.size() - 1);
            return createTicket(lastRes, price);
        }
        return null;
    }

    /**
     * Rezervasyonu iptal eder.
     */
    public synchronized boolean cancelReservation(String reservationCode) {
        Reservation resToRemove = null;

        // Listede rezervasyonu ara
        for (Reservation res : reservations) {
            if (res.getReservationCode().equals(reservationCode)) {
                resToRemove = res;
                break;
            }
        }

        if (resToRemove != null) {
            // Koltuğu boşa çıkar
            Seat seat = resToRemove.getSeat();
            if (seat != null) {
                seat.unReserve();
            }

            reservations.remove(resToRemove);
            saveReservations();

            System.out.println("Rezervasyon iptal edildi: " + reservationCode);
            return true;
        } else {
            System.out.println("Hata: Rezervasyon bulunamadı.");
            return false;
        }
    }

    // === GETTER METODLARI ===

    public List<Reservation> getAllReservations() {
        return reservations;
    }

    public List<Ticket> getAllTickets() {
        return tickets;
    }

    public Ticket getTicketByID(String ticketID) {
        for (Ticket t : tickets) {
            if (t.getTicketID().equals(ticketID)) {
                return t;
            }
        }
        return null;
    }

    public List<Ticket> getTicketsByPassenger(Passanger passanger) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            // ID bazlı karşılaştırma (farklı oturumlarda da çalışır)
            if (t.getReservation().getpassanger().getID() == passanger.getID()) {
                result.add(t);
            }
        }
        return result;
    }

    public void printReservationsByPassenger(String passengerName) {
        boolean found = false;
        for (Reservation res : reservations) {
            if (res.getpassanger().getName().equalsIgnoreCase(passengerName)) {
                System.out.println(res.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println(passengerName + " adına kayıt bulunamadı.");
        }
    }

    // === KAYDETME METODLARI ===

    private void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATION_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.err.println("Rezervasyon kaydetme hatası: " + e.getMessage());
        }
    }

    private void saveTickets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TICKET_FILE))) {
            oos.writeObject(tickets);
        } catch (IOException e) {
            System.err.println("Bilet kaydetme hatası: " + e.getMessage());
        }
    }

    /**
     * Tüm verileri kaydeder (rezervasyonlar + biletler)
     */
    public void saveAll() {
        saveReservations();
        saveTickets();
    }

    // === YÜKLEME METODLARI ===

    @SuppressWarnings("unchecked")
    private List<Reservation> loadReservations() {
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESERVATION_FILE))) {
            return (List<Reservation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Rezervasyon okuma hatası: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Ticket> loadTickets() {
        File file = new File(TICKET_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TICKET_FILE))) {
            return (List<Ticket>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Bilet okuma hatası: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

