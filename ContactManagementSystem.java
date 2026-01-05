import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Contact Management System with File Management (Auto Save/Load)
 * File: contacts_db.txt (created automatically in the same folder)
 *
 * Compile: javac ContactManagementSystem.java
 * Run:     java ContactManagementSystem
 */
public class ContactManagementSystem {

    private static final String DB_FILE = "contacts_db.txt";

    // In-memory storage
    private static final List<Contact> contacts = new ArrayList<>();
    private static int nextId = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Load from file at startup
        loadFromFile();

        while (true) {
            printMenu();
            int choice = readInt(sc, "Enter choice: ");

            switch (choice) {
                case 1 -> addContact(sc);
                case 2 -> viewAllContacts();
                case 3 -> searchContacts(sc);
                case 4 -> updateContact(sc);
                case 5 -> deleteContact(sc);
                case 6 -> sortContactsByName();
                case 7 -> {
                    saveToFile(); // final save
                    System.out.println("👋 Bye. Go build bigger things.");
                    sc.close();
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }

            System.out.println();
        }
    }

    // ---------------- MENU ----------------

    private static void printMenu() {
        System.out.println("======================================");
        System.out.println("       CONTACT MANAGEMENT SYSTEM      ");
        System.out.println("======================================");
        System.out.println("1. Add Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact");
        System.out.println("4. Update Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Sort Contacts by Name");
        System.out.println("7. Exit");
        System.out.println("======================================");
    }

    // ---------------- CRUD ----------------

    private static void addContact(Scanner sc) {
        System.out.println("---- Add Contact ----");
        String name = readNonEmpty(sc, "Name: ");
        String phone = readNonEmpty(sc, "Phone: ");
        String email = readNonEmpty(sc, "Email: ");
        String address = readNonEmpty(sc, "Address: ");

        Contact c = new Contact(nextId++, name, phone, email, address);
        contacts.add(c);

        saveToFile(); // auto-save after change
        System.out.println("✅ Contact added successfully. ID = " + c.id);
    }

    private static void viewAllContacts() {
        System.out.println("---- All Contacts ----");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }
        printTableHeader();
        for (Contact c : contacts) {
            System.out.println(c.toRow());
        }
    }

    private static void searchContacts(Scanner sc) {
        System.out.println("---- Search Contact ----");
        System.out.println("Search by: 1) ID  2) Name  3) Phone  4) Email");
        int opt = readInt(sc, "Enter option: ");

        List<Contact> results = new ArrayList<>();

        switch (opt) {
            case 1 -> {
                int id = readInt(sc, "Enter ID: ");
                Contact c = findById(id);
                if (c != null) results.add(c);
            }
            case 2 -> {
                String name = readNonEmpty(sc, "Enter name keyword: ").toLowerCase();
                for (Contact c : contacts) {
                    if (c.name.toLowerCase().contains(name)) results.add(c);
                }
            }
            case 3 -> {
                String phone = readNonEmpty(sc, "Enter phone keyword: ");
                for (Contact c : contacts) {
                    if (c.phone.contains(phone)) results.add(c);
                }
            }
            case 4 -> {
                String email = readNonEmpty(sc, "Enter email keyword: ").toLowerCase();
                for (Contact c : contacts) {
                    if (c.email.toLowerCase().contains(email)) results.add(c);
                }
            }
            default -> {
                System.out.println("❌ Invalid search option.");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching contacts found.");
        } else {
            System.out.println("✅ Matches found: " + results.size());
            printTableHeader();
            for (Contact c : results) {
                System.out.println(c.toRow());
            }
        }
    }

    private static void updateContact(Scanner sc) {
        System.out.println("---- Update Contact ----");
        int id = readInt(sc, "Enter Contact ID to update: ");
        Contact c = findById(id);

        if (c == null) {
            System.out.println("❌ Contact not found.");
            return;
        }

        System.out.println("Current details:");
        printTableHeader();
        System.out.println(c.toRow());

        System.out.println("\nEnter new values (press ENTER to keep old value):");
        String name = readOptional(sc, "New Name: ");
        String phone = readOptional(sc, "New Phone: ");
        String email = readOptional(sc, "New Email: ");
        String address = readOptional(sc, "New Address: ");

        if (!name.isBlank()) c.name = name;
        if (!phone.isBlank()) c.phone = phone;
        if (!email.isBlank()) c.email = email;
        if (!address.isBlank()) c.address = address;

        saveToFile();
        System.out.println("✅ Contact updated successfully.");
    }

    private static void deleteContact(Scanner sc) {
        System.out.println("---- Delete Contact ----");
        int id = readInt(sc, "Enter Contact ID to delete: ");
        Contact c = findById(id);

        if (c == null) {
            System.out.println("❌ Contact not found.");
            return;
        }

        System.out.println("Deleting:");
        printTableHeader();
        System.out.println(c.toRow());

        System.out.print("Are you sure? (y/n): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("y")) {
            contacts.remove(c);
            saveToFile();
            System.out.println("✅ Contact deleted successfully.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private static void sortContactsByName() {
        System.out.println("---- Sort Contacts by Name ----");
        contacts.sort(Comparator.comparing(c -> c.name.toLowerCase()));
        saveToFile();
        System.out.println("✅ Contacts sorted by name.");
    }

    // ---------------- FILE MANAGEMENT (SAVE/LOAD) ----------------

    private static void loadFromFile() {
        Path path = Paths.get(DB_FILE);

        if (!Files.exists(path)) {
            System.out.println("📁 No database file found. A new one will be created: " + DB_FILE);
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int maxId = 0;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // Each line: id \t name \t phone \t email \t address (with escaping)
                String[] parts = splitTSV(line);
                if (parts.length != 5) continue; // skip malformed lines safely

                int id = Integer.parseInt(parts[0]);
                String name = unescape(parts[1]);
                String phone = unescape(parts[2]);
                String email = unescape(parts[3]);
                String address = unescape(parts[4]);

                contacts.add(new Contact(id, name, phone, email, address));
                if (id > maxId) maxId = id;
            }

            nextId = maxId + 1;
            System.out.println("✅ Loaded " + contacts.size() + " contact(s) from " + DB_FILE);

        } catch (Exception e) {
            System.out.println("❌ Failed to load contacts: " + e.getMessage());
            System.out.println("Tip: Delete " + DB_FILE + " if it's corrupted and re-run.");
        }
    }

    private static void saveToFile() {
        Path path = Paths.get(DB_FILE);

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (Contact c : contacts) {
                String line = c.id + "\t"
                        + escape(c.name) + "\t"
                        + escape(c.phone) + "\t"
                        + escape(c.email) + "\t"
                        + escape(c.address);
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to save contacts: " + e.getMessage());
        }
    }

    // TSV split (not regex-heavy; safe)
    private static String[] splitTSV(String line) {
        // This file format uses literal tabs as separators
        return line.split("\t", -1);
    }

    // Escape tabs, newlines, backslashes so file stays clean
    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String s) {
        // unescape in reverse order
        return s.replace("\\t", "\t")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }

    // ---------------- HELPERS ----------------

    private static Contact findById(int id) {
        for (Contact c : contacts) {
            if (c.id == id) return c;
        }
        return null;
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("❌ This field cannot be empty.");
        }
    }

    private static String readOptional(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine(); // can be blank
    }

    private static void printTableHeader() {
        System.out.printf("%-5s %-20s %-15s %-25s %-30s%n",
                "ID", "NAME", "PHONE", "EMAIL", "ADDRESS");
        System.out.println("-------------------------------------------------------------------------------------------");
    }

    // ---------------- MODEL ----------------

    private static class Contact {
        int id;
        String name;
        String phone;
        String email;
        String address;

        Contact(int id, String name, String phone, String email, String address) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        String toRow() {
            return String.format("%-5d %-20s %-15s %-25s %-30s",
                    id, truncate(name, 20), truncate(phone, 15), truncate(email, 25), truncate(address, 30));
        }

        private String truncate(String s, int max) {
            if (s == null) return "";
            if (s.length() <= max) return s;
            return s.substring(0, max - 3) + "...";
        }
    }
}
