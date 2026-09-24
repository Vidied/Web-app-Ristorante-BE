package davidepan.capstone;

import davidepan.capstone.entities.*;
import davidepan.capstone.enums.Allergen;
import davidepan.capstone.enums.DestinationArea;
import davidepan.capstone.enums.OrderStatus;
import davidepan.capstone.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@org.springframework.core.annotation.Order(2)
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@restaurant.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN non trovato"));

            User admin = new User();
            admin.setName("Admin");
            admin.setSurname("Master");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("AdminPassword123!"));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }

        if (productRepository.count() > 0) {
            return;
        }

                Category catMargherite = categoryRepository.save(new Category("Le Nostre Margherite", 1));
        Category catMarinare = categoryRepository.save(new Category("Le Nostre Marinare", 2));
        Category catClassiche = categoryRepository.save(new Category("Le Nostre Pizze Classiche", 3));
        Category catSpeciali = categoryRepository.save(new Category("Le Pizze Speciali", 4));
        Category catRicordi = categoryRepository.save(new Category("Ricordi a Tavola", 5));
        Category catCalzoni = categoryRepository.save(new Category("Calzoni", 6));
        Category catBevande = categoryRepository.save(new Category("Bevande & Dessert", 7));

        Map<String, Ingredient> ing = new HashMap<>();
        String[] ingredientNames = {
                "Fior di latte d'Agerola", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Pecorino", "Olio",
                "Bufala DOC", "Crema da condimento al formaggio", "Ragù", "Olio all'aglio", "Peperoncino", "Pepe nero",
                "Provola affumicata DOC", "Datterino del piennolo giallo", "Aglio", "Origano", "Alici", "Olive nere",
                "Burrata", "Stracciata di bufala DOP", "Ventricina piccante", "Salsiccia in arrosto", "Carciofini all'olio",
                "Prosciutto cotto", "Funghi freschi tagliati a mano", "Gorgonzola", "Friarielli", "Provola", "Tonno",
                "Cipolla rossa", "Patate", "Prosciutto crudo", "Rucola", "Wurstel", "Verdure grigliate", "Funghi champignon",
                "Scarola", "Cigoli", "Pomodorino rosso ciliegino", "Tarallo 'n sugna e pepe extra mandorlato",
                "Ragù di carne mista", "Succo di limone", "Zeste di limone", "Mais", "'Nduja", "Melanzane", "Sugo polpette",
                "Polpette", "Grana", "Parmigiano", "Uovo", "Crema alla genovese", "Crema di carciofi", "Guanciale",
                "Carbo crema", "Cacio e pepe", "Funghi porcini", "Melanzana al funghetto", "Prezzemolo", "Patatine",
                "Datterino rosso", "Olive", "Pomodorini pachino", "Ananas", "Funghi","Alici di Cetara"
        };

        for (String name : ingredientNames) {
            ing.put(name, ingredientRepository.save(new Ingredient(name)));
        }


        ing.get("Alici").setExtraPrice(new BigDecimal("2.00"));
        ingredientRepository.save(ing.get("Alici"));
        ing.get("Alici di Cetara").setExtraPrice(new BigDecimal("2.00"));
        ingredientRepository.save(ing.get("Alici di Cetara"));

        ingredientRepository.save(new Ingredient("Ruota di Carro", new BigDecimal("1.00")));
        ingredientRepository.save(new Ingredient("Ruotiello", new BigDecimal("2.00")));
        ingredientRepository.save(new Ingredient("Baby", new BigDecimal("-1.00")));

                Product margheritaRef = saveProduct("Margherita", null, "10.00", "8.00", catMargherite, ing,
                        Set.of("Fior di latte d'Agerola", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Pecorino", "Olio"),
                        Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Bufala", null, "13.00", "11.00", catMargherite, ing,
                Set.of("Bufala DOC", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

                saveProduct("Margherita del Perdono", null, "16.00", "14.00", catMargherite, ing,
                Set.of("Crema da condimento al formaggio", "Ragù", "Olio all'aglio", "Peperoncino", "Basilico napoletano", "Pepe nero", "Provola affumicata DOC", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.SEDANO));

        saveProduct("Margherita Gold and Lady", null, "11.50", "9.50", catMargherite, ing,
                Set.of("Fior di latte d'Agerola", "Datterino del piennolo giallo", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Bufala Gold", null, "14.00", "12.00", catMargherite, ing,
                Set.of("Bufala DOC", "Datterino del piennolo giallo", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Cosacca (Margherita dei Poveri)", null, "8.50", "6.50", catMargherite, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Margherita White Passion", null, "11.00", "9.00", catMargherite, ing,
                Set.of("Fior di latte d'Agerola", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Margherita Giallo Rosso", null, "11.50", "9.50", catMargherite, ing,
                Set.of("Fior di latte d'Agerola", "Datterino del piennolo giallo", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

                saveProduct("La Marinara (Campione del Mondo)", null, "8.50", "6.50", catMarinare, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Aglio", "Basilico napoletano", "Olio", "Origano"),
                Set.of(Allergen.GLUTINE));

        saveProduct("La Marinara Gold (Pizzaiolo dell'Anno)", null, "9.50", "7.50", catMarinare, ing,
                Set.of("Datterino del piennolo giallo", "Aglio", "Basilico napoletano", "Olio", "Origano"),
                Set.of(Allergen.GLUTINE));

        saveProduct("La Marinara De Dios", null, "12.00", "10.00", catMarinare, ing,
                Set.of("Datterino del piennolo giallo", "Pomodoro San Marzano schiacciato a mano", "Crema da condimento al formaggio", "Aglio", "Origano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Marinara della Signora \"Petrillo\"", null, "13.00", "11.00", catMarinare, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Datterino rosso", "Aglio", "Alici", "Olive nere", "Origano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE));

        saveProduct("Marinara Sbagliata", null, "13.00", "11.00", catMarinare, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Bufala DOC", "Aglio", "Basilico napoletano", "Olio", "Origano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Marinara \"a bella mia\"", null, "10.00", "8.00", catMarinare, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Aglio", "Datterino del piennolo giallo", "Basilico napoletano", "Olio", "Origano"),
                Set.of(Allergen.GLUTINE));

        saveProduct("Marinara Shock", "Vincitrice premio speciale con alici e burrata in uscita", "15.00", "13.00", catMarinare, ing,
                Set.of("Datterino del piennolo giallo", "Olio", "Aglio", "Basilico napoletano", "Origano", "Alici", "Burrata"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        // LE NOSTRE PIZZE CLASSICHE
                saveProduct("La Napoli", null, "12.00", "10.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Alici", "Fior di latte d'Agerola", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

                Product salamePiccanteRef = saveProduct("La Salame Piccante", null, "12.00", "10.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Ventricina piccante", "Basilico napoletano", "Olio", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Quattro Stagioni", null, "14.00", "12.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Salsiccia in arrosto", "Carciofini all'olio", "Prosciutto cotto", "Funghi freschi tagliati a mano", "Fior di latte d'Agerola", "Olio", "Basilico napoletano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Quattro Formaggi", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Bufala DOC", "Pecorino", "Crema da condimento al formaggio", "Gorgonzola", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Capricciosa", null, "15.00", "13.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Salsiccia in arrosto", "Carciofini all'olio", "Prosciutto cotto", "Funghi freschi tagliati a mano", "Olive nere", "Basilico napoletano", "Olio", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Salsiccia e Friarielli", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Salsiccia in arrosto", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Salsiccia e Friarelli alla Casertana", null, "15.00", "13.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Salsiccia in arrosto", "Provola", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Tonno e Cipolla", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Fior di latte d'Agerola", "Tonno", "Cipolla rossa", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("La Patate e Salsiccia", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Fior di latte d'Agerola", "Patate", "Salsiccia in arrosto", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Funghi e Salsiccia", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Funghi freschi tagliati a mano", "Salsiccia in arrosto", "Fior di latte d'Agerola", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Biancaneve", null, "14.00", "12.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Pomodorini pachino", "Basilico napoletano", "Prosciutto crudo", "Rucola", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("L'Americana", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Wurstel", "Patate", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Vegetariana", null, "15.00", "13.00", catClassiche, ing,
                Set.of("Verdure grigliate", "Fior di latte d'Agerola", "Olio", "Basilico napoletano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Funghi e Crudo", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Funghi champignon", "Basilico napoletano", "Prosciutto crudo", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Funghi e Cotto", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Funghi champignon", "Basilico napoletano", "Prosciutto cotto", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Scarola", null, "13.00", "11.00", catClassiche, ing,
                Set.of("Fior di latte d'Agerola", "Scarola", "Alici", "Olive", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("La Provola e Pepe", null, "12.00", "10.00", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Provola affumicata DOC", "Pepe nero", "Fior di latte d'Agerola", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Provola e Pepe + Cigoli", null, "14.50", "12.50", catClassiche, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Provola affumicata DOC", "Pepe nero", "Fior di latte d'Agerola", "Cigoli", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Provola e Pepe White Passion", null, "12.00", "10.00", catClassiche, ing,
                Set.of("Crema da condimento al formaggio", "Provola affumicata DOC", "Pepe nero", "Fior di latte d'Agerola", "Cigoli", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

                saveProduct("La Datterina", "Pizza speciale con datterino giallo, alici di Cetara e bufala DOP", "15.00", "13.00", catSpeciali, ing,
                Set.of("Bufala DOC", "Pomodorino rosso ciliegino", "Datterino del piennolo giallo", "Alici di Cetara", "Aglio", "Olio", "Basilico napoletano"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("La Pugliese", null, "14.00", "12.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Friarielli", "Pomodorini pachino", "Alici di Cetara", "Olive", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE));

        saveProduct("La Rustica", null, "13.00", "11.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Funghi", "Grana", "Aglio", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Nordica", null, "14.00", "12.00", catSpeciali, ing,
                Set.of("Datterino del piennolo giallo", "Cipolla rossa", "Gorgonzola", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Zozzona", "Golosità della casa con crema al formaggio, gorgonzola e patate", "15.00", "13.00", catSpeciali, ing,
                Set.of("Crema da condimento al formaggio", "Ventricina piccante", "Basilico napoletano", "Patate", "Gorgonzola", "Olio", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Mediterranea", null, "13.00", "11.00", catSpeciali, ing,
                Set.of("Bufala DOC", "Alici di Cetara", "Pomodorini pachino", "Basilico napoletano", "Olio", "Origano"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("La Sorrentina", null, "14.00", "12.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Bufala DOC", "Aglio", "Pomodorini pachino", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Del Compare", null, "14.00", "12.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Ventricina piccante", "Gorgonzola", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Salsiccia e Friarielli Special", null, "12.00", "10.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Salsiccia in arrosto", "Basilico napoletano", "Olio", "Tarallo 'n sugna e pepe extra mandorlato"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.FRUTTA_A_GUSCIO));

        saveProduct("La Quattro Formaggi Special", null, "11.00", "9.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Bufala DOC", "Grana", "Gorgonzola", "Tarallo 'n sugna e pepe extra mandorlato"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.FRUTTA_A_GUSCIO));

        saveProduct("La Turista per Caso", null, "14.00", "12.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Gorgonzola", "Crema da condimento al formaggio", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Santa Domenica", "Specialità al ragù di carne mista e stracciata di bufala DOP", "15.00", "13.00", catSpeciali, ing,
                Set.of("Ragù di carne mista", "Provola affumicata DOC", "Basilico napoletano", "Grana", "Fior di latte d'Agerola", "Stracciata di bufala DOP", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.SEDANO));

        saveProduct("La Costa d'Amalfi", "Pizza d'autore profumata con succo e zeste di limone fresco", "15.00", "13.00", catSpeciali, ing,
                Set.of("Crema da condimento al formaggio", "Alici di Cetara", "Bufala DOC", "Pepe nero", "Basilico napoletano", "Succo di limone", "Zeste di limone", "Stracciata di bufala DOP", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("La Brindisina", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Bufala DOC", "Crema da condimento al formaggio", "Prosciutto crudo", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("La Svizzera", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Ventricina piccante", "Carciofini all'olio", "Cipolla rossa", "Pomodoro San Marzano schiacciato a mano", "Basilico napoletano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Aglio, Olio e Peperoncino Special", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Olio all'aglio", "Aglio", "Peperoncino", "Alici di Cetara", "Basilico napoletano", "Grana", "Tarallo 'n sugna e pepe extra mandorlato", "Stracciata di bufala DOP", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE, Allergen.FRUTTA_A_GUSCIO));

        saveProduct("Mimosa", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Crema da condimento al formaggio", "Prosciutto cotto", "Mais", "Pepe nero"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calabrese", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Fior di latte d'Agerola", "Cipolla rossa", "Olive nere", "'Nduja"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Sfiziosa", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Ventricina piccante", "'Nduja", "Gorgonzola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Pizza Amoremio", null, "13.00", "11.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Olive"),
                Set.of(Allergen.GLUTINE));

        saveProduct("Manfredi", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Salsiccia in arrosto", "Gorgonzola", "Basilico napoletano", "Pecorino", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Hawaii", null, "15.00", "13.00", catSpeciali, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Fior di latte d'Agerola", "Prosciutto cotto", "Ananas"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

                saveProduct("La Parmigiana", null, "15.00", "13.00", catRicordi, ing,
                Set.of("Melanzane", "Fior di latte d'Agerola", "Basilico napoletano", "Provola affumicata DOC", "Pomodoro San Marzano schiacciato a mano", "Olio"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Mamma Mia", "Ricetta tradizionale con polpette al sugo fatte in casa", "17.00", "15.00", catRicordi, ing,
                Set.of("Sugo polpette", "Polpette", "Friarielli", "Grana"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Uee a Noo'", null, "17.00", "15.00", catRicordi, ing,
                Set.of("Sugo polpette", "Polpette", "Provola", "Parmigiano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Pizza e Patate", null, "16.00", "14.00", catRicordi, ing,
                Set.of("Crema da condimento al formaggio", "Pomodoro San Marzano schiacciato a mano", "Provola", "Patate", "Pepe nero", "Olio all'aglio", "Aglio", "Basilico napoletano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Casatiello Scomposto", null, "17.00", "15.00", catRicordi, ing,
                Set.of("Fior di latte d'Agerola", "Uovo", "Prosciutto crudo", "Cigoli", "Grana", "Pepe nero", "Tarallo 'n sugna e pepe extra mandorlato"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.UOVA, Allergen.FRUTTA_A_GUSCIO));

        saveProduct("Nonna", null, "17.00", "15.00", catRicordi, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Melanzane", "Fior di latte d'Agerola", "Provola", "Grana", "Prosciutto cotto", "Ragù"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.SEDANO));

        saveProduct("Tu vuo fa l'Americano", null, "16.00", "14.00", catRicordi, ing,
                Set.of("Sugo polpette", "Polpette", "Datterino rosso", "Grana", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Ciao Napoli", null, "17.00", "15.00", catRicordi, ing,
                Set.of("Fior di latte d'Agerola", "Friarielli", "Cigoli", "Polpette", "Tarallo 'n sugna e pepe extra mandorlato"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.FRUTTA_A_GUSCIO));

        saveProduct("La Genovese", null, "14.00", "12.00", catRicordi, ing,
                Set.of("Crema alla genovese", "Provola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.SEDANO));

        saveProduct("La Giudia", null, "16.00", "14.00", catRicordi, ing,
                Set.of("Crema di carciofi", "Guanciale", "Fior di latte d'Agerola", "Pecorino"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Amatriciana", null, "15.00", "13.00", catRicordi, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Guanciale", "Pecorino", "Pepe nero"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Carbonara", null, "15.00", "13.00", catRicordi, ing,
                Set.of("Carbo crema", "Guanciale", "Fior di latte d'Agerola", "Pecorino", "Pepe nero"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.UOVA));

        saveProduct("Gricia", null, "15.00", "13.00", catRicordi, ing,
                Set.of("Fior di latte d'Agerola", "Guanciale", "Pecorino"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Cacio e Pepe", null, "14.00", "12.00", catRicordi, ing,
                Set.of("Fior di latte d'Agerola", "Cacio e pepe"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Tuscia in Fiore", null, "16.00", "14.00", catRicordi, ing,
                Set.of("Funghi porcini", "Salsiccia in arrosto", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Anima Mia", null, "15.00", "13.00", catRicordi, ing,
                Set.of("Melanzana al funghetto", "Polpette", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Scarpariello", null, "14.00", "12.00", catRicordi, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Aglio", "Peperoncino", "Pecorino"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Arrabbiata", null, "13.00", "11.00", catRicordi, ing,
                Set.of("Pomodoro San Marzano schiacciato a mano", "Peperoncino", "Aglio", "Prezzemolo"),
                Set.of(Allergen.GLUTINE));

        saveProduct("Calzone Classico", null, "10.00", "8.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Pomodoro San Marzano schiacciato a mano", "Grana", "Basilico napoletano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Prosciutto Cotto", null, "12.00", "10.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Grana", "Prosciutto cotto"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Salsiccia e Funghi", null, "12.00", "10.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Salsiccia in arrosto", "Funghi freschi tagliati a mano"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Salsiccia e Friarielli", null, "13.00", "11.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Salsiccia in arrosto", "Friarielli"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Prosciutto Crudo e Funghi", null, "12.00", "10.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Funghi champignon", "Prosciutto crudo"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Scarola", null, "13.00", "11.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Scarola", "Alici", "Olive"),
                Set.of(Allergen.GLUTINE, Allergen.PESCE, Allergen.LATTE));

        saveProduct("Calzone Wurstel e Patatine", null, "12.00", "10.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Wurstel", "Patatine"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Sfizioso", null, "15.00", "13.00", catCalzoni, ing,
                Set.of("Fior di latte d'Agerola", "Ventricina piccante", "'Nduja", "Gorgonzola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

        saveProduct("Calzone Tu vuo fa l'Americano", null, "16.00", "14.00", catCalzoni, ing,
                Set.of("Sugo polpette", "Polpette", "Datterino rosso", "Grana", "Fior di latte d'Agerola"),
                Set.of(Allergen.GLUTINE, Allergen.LATTE));

                Product acqua = saveProduct("Acqua Naturale 75cl", "", "3.5", "3.5", DestinationArea.SALA,
                        catBevande, ing, Set.of(), Set.of());

        saveProduct("Acqua Frizzante 75cl", "", "3.5", "3.5", DestinationArea.SALA,
                catBevande, ing, Set.of(), Set.of());

        saveProduct("Tiramisù della Casa", "Dessert al cucchiaio con mascarpone e savoiardi",
                "5.00", "5.00", DestinationArea.SALA, catBevande, ing, Set.of(),
                Set.of(Allergen.GLUTINE, Allergen.LATTE, Allergen.UOVA));

                BigDecimal coperto = new BigDecimal("2.00");

        Order order1 = new Order();
        order1.setTableNumber(4);
        order1.setCoverCount(2);
        order1.setCoverPrice(coperto);
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        order1.setNotes("Pizze ben cotte");

        OrderItem item1_1 = new OrderItem(order1, margheritaRef, 2, margheritaRef.getPrice(), margheritaRef.getTakeawayPrice(), "Una ben cotta");
        OrderItem item1_2 = new OrderItem(order1, salamePiccanteRef, 1, salamePiccanteRef.getPrice(), salamePiccanteRef.getTakeawayPrice(), null);
        OrderItem item1_3 = new OrderItem(order1, acqua, 2, acqua.getPrice(), acqua.getTakeawayPrice(), null);

        order1.getItems().addAll(List.of(item1_1, item1_2, item1_3));
        order1.setTotalAmount(margheritaRef.getPrice().multiply(BigDecimal.valueOf(2))
                .add(salamePiccanteRef.getPrice())
                .add(acqua.getPrice().multiply(BigDecimal.valueOf(2)))
                .add(coperto.multiply(BigDecimal.valueOf(2))));
                orderRepository.save(order1);
    }

        private Product saveProduct(String name,
                                    String description,
                                    String price,
                                    String takeawayPrice,
                                    Category category,
                                    Map<String, Ingredient> ing,
                                    Set<String> ingredientNames,
                                    Set<Allergen> allergens) {
            return saveProduct(name, description, price, takeawayPrice, DestinationArea.PIZZERIA,
                    category, ing, ingredientNames, allergens);
        }

        private Product saveProduct(String name,
                                    String description,
                                    String price,
                                    String takeawayPrice,
                                    DestinationArea area,
                                    Category category,
                                    Map<String, Ingredient> ing,
                                    Set<String> ingredientNames,
                                    Set<Allergen> allergens) {
            Set<Ingredient> ingredients = new HashSet<>();
        for (String ingredientName : ingredientNames) {
            Ingredient ingredient = ing.get(ingredientName);
            if (ingredient == null) {
                throw new IllegalStateException("Ingrediente non definito nel seeder: " + ingredientName);
            }
            ingredients.add(ingredient);
        }

        Product product = new Product(
                name,
                description,
                new BigDecimal(price),
                                new BigDecimal(takeawayPrice),
                true,
                area,
                category,
                ingredients
        );
                product.setAllergens(allergens != null ? allergens : new HashSet<>());
        return productRepository.save(product);
    }
}