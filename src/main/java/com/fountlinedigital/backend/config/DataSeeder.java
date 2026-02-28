package com.fountlinedigital.backend.config;

import com.fountlinedigital.backend.dao.AppUserRepository;
import com.fountlinedigital.backend.dao.ClientRepository;
import com.fountlinedigital.backend.dao.EmployeeRepository;
import com.fountlinedigital.backend.dao.InventoryItemRepository;
import com.fountlinedigital.backend.dao.PersonRepository;
import com.fountlinedigital.backend.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final PersonRepository personRepo;
    private final EmployeeRepository employeeRepo;
    private final ClientRepository clientRepo;
    private final InventoryItemRepository inventoryRepo;
    private final PasswordEncoder encoder;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.seed.admins:10}")
    private int seedAdmins;

    @Value("${app.seed.employees:200}")
    private int seedEmployees;

    @Value("${app.seed.clients:150}")
    private int seedClients;

    @Value("${app.seed.inventory:200}")
    private int seedInventory;

    private static final String[] FIRST_NAMES = {
            "James","Mary","Robert","Patricia","John","Jennifer","Michael","Linda","William","Elizabeth",
            "David","Barbara","Richard","Susan","Joseph","Jessica","Thomas","Sarah","Charles","Karen",
            "Daniel","Nancy","Matthew","Lisa","Anthony","Betty","Mark","Sandra","Donald","Ashley"
    };

    private static final String[] LAST_NAMES = {
            "Smith","Johnson","Williams","Brown","Jones","Garcia","Miller","Davis","Rodriguez","Martinez",
            "Hernandez","Lopez","Gonzalez","Wilson","Anderson","Thomas","Taylor","Moore","Jackson","Martin",
            "Lee","Perez","Thompson","White","Harris","Sanchez","Clark","Ramirez","Lewis","Robinson"
    };

    private static final String[] COMPANY_WORDS = {
            "North","Pine","Summit","River","Cedar","Blue","Iron","Stone","Silver","Golden",
            "Oak","Maple","Evergreen","Redwood","Coastal","Metro","Vertex","Apex","Nimbus","Atlas"
    };

    private static final String[] COMPANY_SUFFIX = {
            "Solutions","Holdings","Supply","Logistics","Consulting","Enterprises","Industries","Group","Partners","Systems"
    };

    // IMPORTANT: keep these aligned with your Department enum
    private static final Department[] EMP_DEPARTMENTS = new Department[] {
            Department.IT, Department.SALES, Department.HR, Department.FINANCE, Department.OPERATIONS
    };

    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled) return;
        seedAll();
    }

    @Transactional
    public void seedAll() throws Exception {
        List<String> adminCreds = seedAdmins();

        seedEmployees();
        seedClients();

        seedInventory();

        writeAdminCredsToFile(adminCreds);

        System.out.println("\n SEED COMPLETE");
        System.out.println("Admins created: " + adminCreds.size());
        System.out.println("People total: " + personRepo.count());
        System.out.println("Inventory total: " + inventoryRepo.count());
        System.out.println("Admin logins saved to: seed-admin-logins.txt (project root)\n");
    }

    private List<String> seedAdmins() {
        List<String> created = new ArrayList<>();

        for (int i = 1; i <= seedAdmins; i++) {
            String username = String.format("admin%03d", i);

            if (userRepo.findByUsername(username).isPresent()) {
                continue; // already exists
            }

            String password = "Adm!" + randomDigits(6);

            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPasswordHash(encoder.encode(password));
            u.setRoles(Set.of("ROLE_ADMIN"));
            userRepo.save(u);

            created.add(username + " / " + password);
        }

        if (userRepo.findByUsername("admin").isEmpty()) {
            AppUser u = new AppUser();
            u.setUsername("admin");
            u.setPasswordHash(encoder.encode("admin123"));
            u.setRoles(Set.of("ROLE_ADMIN"));
            userRepo.save(u);
            created.add("admin / admin123");
        }

        System.out.println("\n=== ADMIN LOGINS (copy these) ===");
        created.forEach(System.out::println);
        System.out.println("=================================\n");

        return created;
    }

    private void seedEmployees() {
        long existingEmployees = personRepo.findAll().stream().filter(p -> p instanceof Employee).count();
        int toCreate = Math.max(0, seedEmployees - (int) existingEmployees);

        for (int i = 0; i < toCreate; i++) {
            String first = pick(FIRST_NAMES);
            String last = pick(LAST_NAMES);
            String email = uniqueEmail(first, last);

            Employee e = new Employee();
            e.setFirstName(first);
            e.setLastName(last);
            e.setEmail(email);
            e.setDepartment(pick(EMP_DEPARTMENTS));
            e.setEmployeeCode(uniqueEmployeeCode());

            employeeRepo.save(e);
        }
    }

    private void seedClients() {
        long existingClients = personRepo.findAll().stream().filter(p -> p instanceof Client).count();
        int toCreate = Math.max(0, seedClients - (int) existingClients);

        for (int i = 0; i < toCreate; i++) {
            String first = pick(FIRST_NAMES);
            String last = pick(LAST_NAMES);
            String email = uniqueEmail(first, last);

            Client c = new Client();
            c.setFirstName(first);
            c.setLastName(last);
            c.setEmail(email);
            c.setCompanyName(randomCompany());
            c.setClientCode(uniqueClientCode());

            clientRepo.save(c);
        }
    }

    private void seedInventory() {
        long existing = inventoryRepo.count();
        int toCreate = Math.max(0, seedInventory - (int) existing);

        for (int i = 0; i < toCreate; i++) {
            InventoryItem item = new InventoryItem();
            item.setSku(uniqueSku());
            item.setName(randomInventoryName());
            item.setQuantity(randInt(0, 250));
            item.setReorderLevel(randInt(3, 40));
            item.setUpdatedAt(OffsetDateTime.now());
            inventoryRepo.save(item);
        }
    }

    private void writeAdminCredsToFile(List<String> creds) throws Exception {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("seed-admin-logins.txt", false))) {
            w.write("Admin logins (username / password)\n");
            w.write("=================================\n");
            for (String c : creds) {
                w.write(c);
                w.newLine();
            }
        }
    }

    private static <T> T pick(T[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }

    private static String pick(String[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }

    private static int randInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    private String uniqueEmail(String first, String last) {
        String base = (first + "." + last).toLowerCase(Locale.ROOT)
                .replace("'", "")
                .replace(" ", "");
        String email;
        do {
            email = base + randomDigits(3) + "@example.com";
        } while (personRepo.existsByEmailIgnoreCase(email));
        return email;
    }

    private String uniqueSku() {
        String sku;
        do {
            sku = "SKU-" + randomDigits(8);
        } while (inventoryRepo.findBySku(sku).isPresent());
        return sku;
    }

    private String randomInventoryName() {
        String[] types = { "Laptop", "Router", "Switch", "Monitor", "Keyboard", "Mouse", "Dock", "Cable", "Printer", "SSD", "RAM", "Firewall" };
        String[] brands = { "Apex", "Nimbus", "Atlas", "Vertex", "Iron", "Silver", "Metro", "Coastal" };
        return pick(brands) + " " + pick(types) + " " + randomDigits(2);
    }

    private String randomCompany() {
        return pick(COMPANY_WORDS) + " " + pick(COMPANY_WORDS) + " " + pick(COMPANY_SUFFIX);
    }

    private static String randomDigits(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(ThreadLocalRandom.current().nextInt(10));
        return sb.toString();
    }

    private String uniqueEmployeeCode() {
        String code;
        do {
            code = "FDE" + randomDigits(3); // ex: FDE482
        } while (employeeRepo.existsByEmployeeCodeIgnoreCase(code));
        return code;
    }

    private String uniqueClientCode() {
        String code;
        do {
            code = "FDC" + randomDigits(3); // ex: FDC193
        } while (clientRepo.existsByClientCodeIgnoreCase(code));
        return code;
    }
}