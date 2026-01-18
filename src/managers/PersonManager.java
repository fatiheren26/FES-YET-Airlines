package managers;

import java.io.*;
import java.util.*;
import javax.swing.ImageIcon;
import models.*;

public class PersonManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Person> users = new HashMap<>();
    private final String FILE_NAME = "savesAndImage\\person\\users.dat";

    private final String MALE_DEFAULT = "savesAndImage\\Image\\manDefault.png";
    private final String FEMALE_DEFAULT = "savesAndImage\\Image\\womanDefault.png";

    public PersonManager() {
        loadData();
    }

    public ImageIcon getDefaultImage(int gender) {
        String path = (gender == 1) ? MALE_DEFAULT : FEMALE_DEFAULT;
        return new File(path).exists() ? new ImageIcon(path) : null;
    }

    public void addPerson(Person p) {
        users.put(p.getID(), p);
        saveData();
    }

    public boolean IsCorrectAdminPass(int id, String pass) {
        Person p = users.get(id);
        if (p != null && p instanceof Admin) {
            return String.valueOf(p.getPassword()).equals(pass);
        }
        return false;
    }

    public boolean IsCorrectPassangerPass(int id, String pass) {
        Person p = users.get(id);
        if (p != null && p instanceof Passanger) {
            return String.valueOf(p.getPassword()).equals(pass);
        }
        return false;
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<Integer, Person>) ois.readObject();

            // ID Sayacını Güncelle - Passanger'lar için +1000 offset var
            if (!users.isEmpty()) {
                int maxBaseId = 0;
                for (Map.Entry<Integer, Person> entry : users.entrySet()) {
                    int id = entry.getKey();
                    Person person = entry.getValue();

                    // Passanger ise +1000 offset'i çıkar
                    int baseId = (person instanceof Passanger) ? (id - 1000) : id;

                    if (baseId > maxBaseId) {
                        maxBaseId = baseId;
                    }
                }
                Person.setIdCounter(maxBaseId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Person getPersonByID(int id) {
        return users.get(id);
    }
}
