package managers;

import models.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightManager {
    private Map<Integer, Flight> flights;
    private final String FILE_PATH = "savesAndImage/Assets/flights.dat";
    private SeatManager seatManager;
    private int counter = 16876375;

    public FlightManager(SeatManager sm) {
        this.flights = loadFlights();
        this.seatManager = sm;
    }

    // ÇAKIŞMA KONTROLÜ: Kalkış-1saat ve İniş+1saat
    public boolean isPlaneConflict(Plane targetPlane, Route newRoute, LocalDate date, LocalTime time) {
        // 1. Yeni eklenmek istenen uçuşun zaman aralığını hesapla
        // Geçici bir uçuş objesi oluşturarak iniş zamanını hesaplatıyoruz
        Flight dummyFlight = new Flight(0, newRoute, targetPlane, date, time, null);
        LocalDateTime newStart = dummyFlight.getDepartureDateTime().minusHours(1);
        LocalDateTime newEnd = dummyFlight.getEstimatedLandingTime().plusHours(1);

        return flights.values().stream()
                .filter(f -> f.getPlane().getTailNumber().equals(targetPlane.getTailNumber()))
                .anyMatch(f -> {
                    // Mevcut uçuşun meşguliyet aralığı (Kalkış-1, İniş+1)
                    LocalDateTime existingStart = f.getDepartureDateTime().minusHours(1);
                    LocalDateTime existingEnd = f.getEstimatedLandingTime().plusHours(1);

                    // İki zaman aralığının kesişip kesişmediğini kontrol et
                    // (Başlangıç1 < Bitiş2) VE (Bitiş1 > Başlangıç2) ise çakışma vardır.
                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });
    }

    public void addFlight(Plane plane, Route route, LocalDate date, LocalTime time) {
        Map<String, Seat> seats = seatManager.createSeatsForFlight(plane.getPlaneModel());
        Flight newFlight = new Flight(counter, route, plane, date, time, seats);
        flights.put(counter++, newFlight);
        saveFlights();
    }

    public void removeFlight(int id) {
        flights.remove(id);
        saveFlights();
    };

    /**
     * Tüm uçuşları dosyaya kaydeder.
     * Koltuk durumları değiştiğinde bu metod çağrılmalıdır.
     */
    public void saveFlights() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(flights);
            oos.writeInt(counter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Flight> loadFlights() {
        File f = new File(FILE_PATH);
        if (!f.exists())
            return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Map<Integer, Flight> data = (Map<Integer, Flight>) ois.readObject();
            this.counter = ois.readInt();
            return data;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    // ... Diğer filtreleme metotları (filterByTimeRange vb.) aynı kalabilir ...
    public Map<Integer, Flight> getAllFlights() {
        return flights;
    }
}
