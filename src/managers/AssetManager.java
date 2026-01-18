package managers;
import models.*;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AssetManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Klasör yolu ve Dosya isimleri
    private static final String SAVE_PATH = "savesAndImage/Assets/";
    private static final String AIRPORT_FILE = SAVE_PATH + "airports.dat";
    private static final String MODELS_FILE = SAVE_PATH + "models.dat"; // Modeller ayrıldı
    private static final String FLEET_FILE = SAVE_PATH + "fleet.dat";   // Uçaklar ayrıldı

    private Map<String, Plane> fleet;
    private Map<String, PlaneModel> models;
    private Map<String, Airport> airports;
    private int tailNoCounter = 100;

    public AssetManager() {
        this.fleet = new HashMap<>();
        this.models = new HashMap<>();
        this.airports = new HashMap<>();
        
        ensureDirectoryExists();
        
        loadAirports();
        loadModels(); 
        loadFleet(); 
    }

    private void ensureDirectoryExists() {
        File directory = new File(SAVE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // --- EKLEME VE SATIN ALMA ---
    public void addAirport(String city, String name, double lat, double lon) {
        airports.put(name, new Airport(city, name, lat, lon));
        saveAirports();
    }

    public void definePlaneModel(String brand, String series, int bRow, int eRow, int speed, int fuel) {
        String key = brand + " " + series;
        models.put(key, new PlaneModel(brand, series, bRow, eRow, speed, fuel));
        saveModels();
    }

    public void buyPlane(String modelKey) {
        PlaneModel model = models.get(modelKey);
        if (model != null) {
            tailNoCounter++;
            
            //KUYRUK NO FORMATI: BOE 738 - 112
            String brandPart = model.getBrandName().substring(0, Math.min(model.getBrandName().length(), 3)).toUpperCase();
            String generatedTailNo = String.format("%s %s - %d", brandPart, model.getSeriesNo(), tailNoCounter);
            
            fleet.put(generatedTailNo, new Plane(generatedTailNo, model));
            saveFleet();
        }
    }

    // --- SİLME METOTLARI ---
    public void removeAirport(String name) { airports.remove(name); saveAirports(); }
    public void removeModel(String key) { models.remove(key); saveModels(); }
    public void removePlane(String tailNo) { fleet.remove(tailNo); saveFleet(); }

    // --- AYRI DOSYA KAYIT SİSTEMLERİ ---
    public void saveAirports() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AIRPORT_FILE))) {
            oos.writeObject(airports);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveModels() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODELS_FILE))) {
            oos.writeObject(models);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveFleet() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FLEET_FILE))) {
            Object[] data = {fleet, tailNoCounter};
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- AYRI DOSYA YÜKLEME SİSTEMLERİ ---
    @SuppressWarnings("unchecked")
    private void loadAirports() {
        File f = new File(AIRPORT_FILE);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AIRPORT_FILE))) {
                airports = (Map<String, Airport>) ois.readObject();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadModels() {
        File f = new File(MODELS_FILE);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MODELS_FILE))) {
                models = (Map<String, PlaneModel>) ois.readObject();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFleet() {
        File f = new File(FLEET_FILE);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FLEET_FILE))) {
                Object[] data = (Object[]) ois.readObject();
                fleet = (Map<String, Plane>) data[0];
                tailNoCounter = (int) data[1];
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public Map<String, Plane> getFleet() { return fleet; }
    public Map<String, PlaneModel> getModels() { return models; }
    public Map<String, Airport> getAirports() { return airports; }
}

