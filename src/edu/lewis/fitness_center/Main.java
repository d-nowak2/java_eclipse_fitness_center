package edu.lewis.fitness_center;

import java.util.*;

public class Main {

	// "Global" data structures ---------------------------------------------

    static List<Map<String, Object>> members = new ArrayList<>();
    // member: { id (Integer), name (String), membership_type (String), active (Boolean), balance (Double) }

    static List<Map<String, Object>> fitnessClasses = new ArrayList<>();
    // class: { id (Integer), name (String), difficulty (String), capacity (Integer), enrolled_ids (List<Integer>) }

    static List<Map<String, Object>> trainers = new ArrayList<>();
    // trainer: { id (Integer), name (String), specialty (String), schedule (List<String>) }

    static int nextMemberId = 1;
    static int nextClassId = 1;
    static int nextTrainerId = 1;

    static Scanner scanner = new Scanner(System.in);

    // Utility functions -----------------------------------------------------

    public static int readInt(String prompt, Integer minValue, Integer maxValue) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (minValue != null && value < minValue) {
                    System.out.println("Please enter a value >= " + minValue);
                    continue;
                }
                if (maxValue != null && value > maxValue) {
                    System.out.println("Please enter a value <= " + maxValue);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public static String readNonempty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Member-related functions ----------------------------------------------

    public static void addMember() {
        System.out.println("\n=== Add New Member ===");
        String name = readNonempty("Name: ");
        System.out.println("Membership types: student, faculty, community");
        System.out.print("Membership type: ");
        String membershipType = scanner.nextLine().trim().toLowerCase();
        if (!membershipType.equals("student") &&
            !membershipType.equals("faculty") &&
            !membershipType.equals("community")) {
            System.out.println("Unknown membership type. Defaulting to 'community'.");
            membershipType = "community";
        }

        Map<String, Object> member = new HashMap<>();
        member.put("id", nextMemberId);
        member.put("name", name);
        member.put("membership_type", membershipType);
        member.put("active", true);
        member.put("balance", 0.0);

        members.add(member);
        System.out.println("Member added with id " + nextMemberId);
        nextMemberId++;
    }

    public static void listMembers(boolean showDetails) {
        System.out.println("\n=== Members ===");
        if (members.isEmpty()) {
            System.out.println("No members found.");
        }
        for (Map<String, Object> m : members) {
            int id = (Integer) m.get("id");
            String name = (String) m.get("name");
            if (showDetails) {
                String membershipType = (String) m.get("membership_type");
                boolean active = (Boolean) m.get("active");
                double balance = (Double) m.get("balance");
                String status = active ? "Active" : "Inactive";
                System.out.printf("[%d] %s (%s, %s) Balance: $%.2f%n",
                        id, name, membershipType, status, balance);
            } else {
                System.out.printf("[%d] %s%n", id, name);
            }
        }
        System.out.println();
    }

    public static Map<String, Object> findMemberById(int memberId) {
        for (Map<String, Object> m : members) {
            int id = (Integer) m.get("id");
            if (id == memberId) {
                return m;
            }
        }
        return null;
    }

    public static void deactivateMember() {
        System.out.println("\n=== Deactivate Member ===");
        listMembers(false);
        int mid = readInt("Enter member id to deactivate: ", null, null);
        Map<String, Object> member = findMemberById(mid);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        boolean active = (Boolean) member.get("active");
        if (!active) {
            System.out.println("Member is already inactive.");
            return;
        }
        member.put("active", false);
        System.out.println("Member " + member.get("name") + " is now inactive.");
    }

    public static void addChargeToMember() {
        System.out.println("\n=== Add Charge to Member ===");
        listMembers(false);
        int mid = readInt("Enter member id to charge: ", null, null);
        Map<String, Object> member = findMemberById(mid);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        System.out.print("Charge amount: $");
        String line = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }
        double balance = (Double) member.get("balance");
        balance += amount;
        member.put("balance", balance);
        System.out.printf("Added $%.2f to %s's balance.%n", amount, member.get("name"));
    }

    public static void applyPaymentFromMember() {
        System.out.println("\n=== Record Payment ===");
        listMembers(false);
        int mid = readInt("Enter member id: ", null, null);
        Map<String, Object> member = findMemberById(mid);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        System.out.print("Payment amount: $");
        String line = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }
        double balance = (Double) member.get("balance");
        balance -= amount;
        member.put("balance", balance);
        System.out.printf("Recorded payment of $%.2f from %s.%n", amount, member.get("name"));
    }

    // Class-related functions -----------------------------------------------

    public static void createClass() {
        System.out.println("\n=== Create Fitness Class ===");
        String name = readNonempty("Class name: ");
        System.out.print("Difficulty (beginner/intermediate/advanced): ");
        String difficulty = scanner.nextLine().trim().toLowerCase();
        if (!difficulty.equals("beginner") &&
            !difficulty.equals("intermediate") &&
            !difficulty.equals("advanced")) {
            System.out.println("Unknown difficulty. Defaulting to 'beginner'.");
            difficulty = "beginner";
        }
        int capacity = readInt("Capacity: ", 1, null);

        Map<String, Object> fitnessClass = new HashMap<>();
        fitnessClass.put("id", nextClassId);
        fitnessClass.put("name", name);
        fitnessClass.put("difficulty", difficulty);
        fitnessClass.put("capacity", capacity);
        fitnessClass.put("enrolled_ids", new ArrayList<Integer>());

        fitnessClasses.add(fitnessClass);
        System.out.println("Fitness class created with id " + nextClassId);
        nextClassId++;
    }

    public static void listClasses(boolean showEnrollment) {
        System.out.println("\n=== Fitness Classes ===");
        if (fitnessClasses.isEmpty()) {
            System.out.println("No classes found.");
        }
        for (Map<String, Object> c : fitnessClasses) {
            int id = (Integer) c.get("id");
            String name = (String) c.get("name");
            if (showEnrollment) {
                String difficulty = (String) c.get("difficulty");
                int capacity = (Integer) c.get("capacity");
                @SuppressWarnings("unchecked")
                List<Integer> enrolledIds = (List<Integer>) c.get("enrolled_ids");
                System.out.printf("[%d] %s (%s, capacity %d, enrolled %d)%n",
                        id, name, difficulty, capacity, enrolledIds.size());
            } else {
                System.out.printf("[%d] %s%n", id, name);
            }
        }
        System.out.println();
    }

    public static Map<String, Object> findClassById(int classId) {
        for (Map<String, Object> c : fitnessClasses) {
            int id = (Integer) c.get("id");
            if (id == classId) {
                return c;
            }
        }
        return null;
    }

    public static void enrollMemberInClass() {
        System.out.println("\n=== Enroll Member in Class ===");
        listMembers(false);
        int mid = readInt("Enter member id: ", null, null);
        Map<String, Object> member = findMemberById(mid);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        boolean active = (Boolean) member.get("active");
        if (!active) {
            System.out.println("Cannot enroll an inactive member.");
            return;
        }

        listClasses(false);
        int cid = readInt("Enter class id: ", null, null);
        Map<String, Object> fclass = findClassById(cid);
        if (fclass == null) {
            System.out.println("Class not found.");
            return;
        }

        int capacity = (Integer) fclass.get("capacity");
        @SuppressWarnings("unchecked")
        List<Integer> enrolledIds = (List<Integer>) fclass.get("enrolled_ids");

        if (enrolledIds.size() >= capacity) {
            System.out.println("Class is full.");
            return;
        }

        if (enrolledIds.contains(mid)) {
            System.out.println("Member is already enrolled in this class.");
            return;
        }

        enrolledIds.add(mid);
        System.out.println("Enrolled " + member.get("name") + " in " + fclass.get("name") + ".");

        // Add charge with manual discount rules
        double baseFee = 10.0;
        String membershipType = (String) member.get("membership_type");
        double discountedFee;
        if (membershipType.equals("student")) {
            discountedFee = baseFee * 0.5;
        } else if (membershipType.equals("faculty")) {
            discountedFee = baseFee * 0.75;
        } else {
            discountedFee = baseFee;
        }

        double balance = (Double) member.get("balance");
        balance += discountedFee;
        member.put("balance", balance);

        System.out.printf(
                "Charged $%.2f for class (base $%.2f, type: %s).%n",
                discountedFee, baseFee, membershipType
        );
    }

    public static void listClassRoster() {
        System.out.println("\n=== Class Roster ===");
        listClasses(false);
        int cid = readInt("Enter class id: ", null, null);
        Map<String, Object> fclass = findClassById(cid);
        if (fclass == null) {
            System.out.println("Class not found.");
            return;
        }

        System.out.println("\nRoster for " + fclass.get("name") + ":");
        @SuppressWarnings("unchecked")
        List<Integer> enrolledIds = (List<Integer>) fclass.get("enrolled_ids");
        if (enrolledIds.isEmpty()) {
            System.out.println("No members enrolled.");
            return;
        }

        for (int mid : enrolledIds) {
            Map<String, Object> m = findMemberById(mid);
            if (m != null) {
                System.out.println("- " + m.get("name") + " (" + m.get("membership_type") + ")");
            }
        }
    }

    // Trainer-related functions ---------------------------------------------

    public static void addTrainer() {
        System.out.println("\n=== Add Trainer ===");
        String name = readNonempty("Trainer name: ");
        String specialty = readNonempty("Specialty (e.g., yoga, strength, cardio): ");
        List<String> schedule = new ArrayList<>();

        System.out.println("Enter trainer availability (e.g., Mon 9-11). Leave blank to stop.");
        while (true) {
            System.out.print("Availability: ");
            String slot = scanner.nextLine().trim();
            if (slot.isEmpty()) {
                break;
            }
            schedule.add(slot);
        }

        Map<String, Object> trainer = new HashMap<>();
        trainer.put("id", nextTrainerId);
        trainer.put("name", name);
        trainer.put("specialty", specialty);
        trainer.put("schedule", schedule);

        trainers.add(trainer);
        System.out.println("Trainer added with id " + nextTrainerId);
        nextTrainerId++;
    }

    public static void listTrainers(boolean showSchedule) {
        System.out.println("\n=== Trainers ===");
        if (trainers.isEmpty()) {
            System.out.println("No trainers found.");
        }
        for (Map<String, Object> t : trainers) {
            int id = (Integer) t.get("id");
            String name = (String) t.get("name");
            String specialty = (String) t.get("specialty");
            if (showSchedule) {
                @SuppressWarnings("unchecked")
                List<String> schedule = (List<String>) t.get("schedule");
                String scheduleStr = (schedule == null || schedule.isEmpty())
                        ? "No availability"
                        : String.join(", ", schedule);
                System.out.printf("[%d] %s - %s (Schedule: %s)%n",
                        id, name, specialty, scheduleStr);
            } else {
                System.out.printf("[%d] %s%n", id, name);
            }
        }
        System.out.println();
    }

    public static Map<String, Object> findTrainerById(int trainerId) {
        for (Map<String, Object> t : trainers) {
            int id = (Integer) t.get("id");
            if (id == trainerId) {
                return t;
            }
        }
        return null;
    }

    public static void updateTrainerSchedule() {
        System.out.println("\n=== Update Trainer Schedule ===");
        listTrainers(false);
        int tid = readInt("Enter trainer id: ", null, null);
        Map<String, Object> trainer = findTrainerById(tid);
        if (trainer == null) {
            System.out.println("Trainer not found.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> schedule = (List<String>) trainer.get("schedule");
        if (schedule == null) {
            schedule = new ArrayList<>();
            trainer.put("schedule", schedule);
        }

        System.out.println("Current schedule:");
        for (String s : schedule) {
            System.out.println("- " + s);
        }

        System.out.println("1. Replace schedule");
        System.out.println("2. Add to schedule");
        int choice = readInt("Choice: ", 1, 2);

        if (choice == 1) {
            schedule.clear();
            System.out.println("Enter new availability (blank to finish):");
            while (true) {
                System.out.print("Availability: ");
                String slot = scanner.nextLine().trim();
                if (slot.isEmpty()) {
                    break;
                }
                schedule.add(slot);
            }
        } else {
            System.out.println("Enter additional availability (blank to finish):");
            while (true) {
                System.out.print("Availability: ");
                String slot = scanner.nextLine().trim();
                if (slot.isEmpty()) {
                    break;
                }
                schedule.add(slot);
            }
        }

        System.out.println("Updated schedule:");
        for (String s : schedule) {
            System.out.println("- " + s);
        }
    }

    // Reporting / summaries -------------------------------------------------

    public static void showSummaryReport() {
        System.out.println("\n=== Summary Report ===");
        int totalMembers = members.size();
        int activeMembers = 0;
        double totalBalance = 0.0;

        for (Map<String, Object> m : members) {
            if ((Boolean) m.get("active")) {
                activeMembers++;
            }
            totalBalance += (Double) m.get("balance");
        }

        System.out.println("Total members: " + totalMembers);
        System.out.println("Active members: " + activeMembers);
        System.out.printf("Total outstanding balance: $%.2f%n", totalBalance);

        System.out.println("\nClasses:");
        for (Map<String, Object> c : fitnessClasses) {
            String name = (String) c.get("name");
            String difficulty = (String) c.get("difficulty");
            int capacity = (Integer) c.get("capacity");
            @SuppressWarnings("unchecked")
            List<Integer> enrolledIds = (List<Integer>) c.get("enrolled_ids");
            System.out.printf("- %s (%s): %d/%d enrolled%n",
                    name, difficulty, enrolledIds.size(), capacity);
        }

        System.out.println("\nTrainers:");
        for (Map<String, Object> t : trainers) {
            System.out.printf("- %s (%s)%n", t.get("name"), t.get("specialty"));
        }
        System.out.println();
    }

    // Menus -----------------------------------------------------------------

    public static void memberMenu() {
        while (true) {
            System.out.println("\n=== Member Menu ===");
            System.out.println("1. Add member");
            System.out.println("2. List members");
            System.out.println("3. Deactivate member");
            System.out.println("4. Add charge to member");
            System.out.println("5. Record payment from member");
            System.out.println("6. Back to main menu");
            int choice = readInt("Choice: ", 1, 6);

            if (choice == 1) {
                addMember();
            } else if (choice == 2) {
                listMembers(true);
            } else if (choice == 3) {
                deactivateMember();
            } else if (choice == 4) {
                addChargeToMember();
            } else if (choice == 5) {
                applyPaymentFromMember();
            } else if (choice == 6) {
                break;
            }

            pause();
        }
    }

    public static void classesMenu() {
        while (true) {
            System.out.println("\n=== Classes Menu ===");
            System.out.println("1. Create fitness class");
            System.out.println("2. List fitness classes");
            System.out.println("3. Enroll member in class");
            System.out.println("4. Show class roster");
            System.out.println("5. Back to main menu");
            int choice = readInt("Choice: ", 1, 5);

            if (choice == 1) {
                createClass();
            } else if (choice == 2) {
                listClasses(true);
            } else if (choice == 3) {
                enrollMemberInClass();
            } else if (choice == 4) {
                listClassRoster();
            } else if (choice == 5) {
                break;
            }

            pause();
        }
    }

    public static void trainerMenu() {
        while (true) {
            System.out.println("\n=== Trainer Menu ===");
            System.out.println("1. Add trainer");
            System.out.println("2. List trainers");
            System.out.println("3. Update trainer schedule");
            System.out.println("4. Back to main menu");
            int choice = readInt("Choice: ", 1, 4);

            if (choice == 1) {
                addTrainer();
            } else if (choice == 2) {
                listTrainers(true);
            } else if (choice == 3) {
                updateTrainerSchedule();
            } else if (choice == 4) {
                break;
            }

            pause();
        }
    }

    public static void mainMenu() {
        while (true) {
            System.out.println("\n=== Campus Fitness Center Management ===");
            System.out.println("1. Manage members");
            System.out.println("2. Manage classes");
            System.out.println("3. Manage trainers");
            System.out.println("4. Show summary report");
            System.out.println("5. Exit");
            int choice = readInt("Choice: ", 1, 5);

            if (choice == 1) {
                memberMenu();
            } else if (choice == 2) {
                classesMenu();
            } else if (choice == 3) {
                trainerMenu();
            } else if (choice == 4) {
                showSummaryReport();
                pause();
            } else if (choice == 5) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        mainMenu();
    }

}
