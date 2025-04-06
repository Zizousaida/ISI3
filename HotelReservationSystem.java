import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String clientName;
    private Date arrivalDate;
    private Date departureDate;
    private int roomNumber;

    public Reservation(String clientName, Date arrivalDate, Date departureDate, int roomNumber) {
        this.clientName = clientName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.roomNumber = roomNumber;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Client: " + clientName + 
               ", Arrival: " + sdf.format(arrivalDate) + 
               ", Departure: " + sdf.format(departureDate) + 
               ", Room: " + roomNumber;
    }
}

public class HotelReservationSystem {
    private static ArrayList<Reservation> reservations = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            try {
                displayMenu();
                int choice = getIntInput("Enter your choice: ");
                
                switch (choice) {
                    case 1:
                        addReservation();
                        break;
                    case 2:
                        deleteReservation();
                        break;
                    case 3:
                        displayAllReservations();
                        break;
                    case 4:
                        saveReservationsToFile();
                        break;
                    case 5:
                        loadReservationsFromFile();
                        break;
                    case 6:
                        System.out.println("Exiting the program...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
            
            // Add pause before redisplaying menu
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
        System.out.println("1. Add a new reservation");
        System.out.println("2. Delete a reservation");
        System.out.println("3. Display all reservations");
        System.out.println("4. Save reservations to file");
        System.out.println("5. Load reservations from file");
        System.out.println("6. Exit");
    }

    private static void addReservation() {
        try {
            System.out.println("\n--- Add New Reservation ---");
            String name = getStringInput("Enter client name: ");
            
            Date arrivalDate = null;
            boolean validArrival = false;
            while (!validArrival) {
                try {
                    String dateString = getStringInput("Enter arrival date (dd/MM/yyyy): ");
                    arrivalDate = dateFormat.parse(dateString);
                    validArrival = true;
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
                }
            }
            
            Date departureDate = null;
            boolean validDeparture = false;
            while (!validDeparture) {
                try {
                    String dateString = getStringInput("Enter departure date (dd/MM/yyyy): ");
                    departureDate = dateFormat.parse(dateString);
                    
                    if (departureDate.before(arrivalDate)) {
                        System.out.println("Departure date cannot be before arrival date.");
                    } else {
                        validDeparture = true;
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
                }
            }
            
            int roomNumber = getIntInput("Enter room number: ");
            while (roomNumber <= 0) {
                System.out.println("Room number must be a positive number.");
                roomNumber = getIntInput("Enter room number: ");
            }
            
            // Check if room is already booked for the given dates
            if (isRoomBooked(roomNumber, arrivalDate, departureDate)) {
                System.out.println("This room is already booked for the selected dates.");
                return;
            }
            
            reservations.add(new Reservation(name, arrivalDate, departureDate, roomNumber));
            System.out.println("Reservation added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding reservation: " + e.getMessage());
        }
    }
    
    private static boolean isRoomBooked(int roomNumber, Date arrivalDate, Date departureDate) {
        for (Reservation res : reservations) {
            if (res.toString().contains("Room: " + roomNumber)) {
                // Check if dates overlap
                if ((arrivalDate.before(res.toString().split("Departure: ")[1].split(",")[0]) && 
                     departureDate.after(res.toString().split("Arrival: ")[1].split(",")[0])) ||
                    (arrivalDate.equals(res.toString().split("Arrival: ")[1].split(",")[0]) || 
                     departureDate.equals(res.toString().split("Departure: ")[1].split(",")[0]))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void deleteReservation() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations to delete.");
            return;
        }
        
        System.out.println("\n--- Delete Reservation ---");
        String name = getStringInput("Enter client name to delete: ");
        
        boolean found = false;
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getClientName().equalsIgnoreCase(name)) {
                reservations.remove(i);
                System.out.println("Reservation deleted successfully!");
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("No reservation found with that client name.");
        }
    }

    private static void displayAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations to display.");
            return;
        }
        
        System.out.println("\n--- All Reservations ---");
        for (int i = 0; i < reservations.size(); i++) {
            System.out.println((i + 1) + ". " + reservations.get(i));
        }
    }

    private static void saveReservationsToFile() {
        try {
            String filename = getStringInput("Enter filename to save: ");
            if (!filename.endsWith(".dat")) {
                filename += ".dat";
            }
            
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(reservations);
            out.close();
            fileOut.close();
            System.out.println("Reservations saved successfully to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadReservationsFromFile() {
        try {
            String filename = getStringInput("Enter filename to load: ");
            if (!filename.endsWith(".dat")) {
                filename += ".dat";
            }
            
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            reservations = (ArrayList<Reservation>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Reservations loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
