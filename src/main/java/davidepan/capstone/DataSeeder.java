package davidepan.capstone;

import davidepan.capstone.entities.*;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
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
                "Scarola", "Cigoli", "Pomodorino rosso ciliegino", "Alici di Cetara", "Tarallo 'n sugna e pepe extra mandorlato",
                "Ragù di carne mista", "Succo di limone", "Zeste di limone", "Mais", "'Nduja", "Melanzane", "Sugo polpette",
                "Polpette", "Grana", "Parmigiano", "Uovo", "Crema alla genovese", "Crema di carciofi", "Guanciale",
                "Carbo crema", "Cacio e pepe", "Funghi porcini", "Melanzana al funghetto", "Prezzemolo", "Patatine"
        };

        for (String name : ingredientNames) {
            ing.put(name, ingredientRepository.save(new Ingredient(name)));
        }

        Product margherita = productRepository.save(new Product(
                "Margherita", null, new BigDecimal("10.00"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Bufala", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Bufala DOC"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Margherita del Perdono", null, new BigDecimal("16.00"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Crema da condimento al formaggio"), ing.get("Ragù"), ing.get("Olio all'aglio"), ing.get("Peperoncino"), ing.get("Basilico napoletano"), ing.get("Pepe nero"), ing.get("Provola affumicata DOC"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Margherita Gold and Lady", null, new BigDecimal("11.50"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Datterino del piennolo giallo"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Bufala Gold", null, new BigDecimal("14.00"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Bufala DOC"), ing.get("Datterino del piennolo giallo"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Cosacca (Margherita dei Poveri)", null, new BigDecimal("8.50"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Margherita White Passion", null, new BigDecimal("11.00"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Margherita Giallo Rosso", null, new BigDecimal("11.50"), true,
                DestinationArea.PIZZERIA, catMargherite,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Datterino del piennolo giallo"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Marinara (Campione del Mondo)", null, new BigDecimal("8.50"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Aglio"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Origano"))
        ));

        productRepository.save(new Product(
                "La Marinara Gold (Pizzaiolo dell'Anno)", null, new BigDecimal("9.50"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Datterino del piennolo giallo"), ing.get("Aglio"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Origano"))
        ));

        productRepository.save(new Product(
                "La Marinara De Dios", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Datterino del piennolo giallo"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Crema da condimento al formaggio"), ing.get("Aglio"), ing.get("Origano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Marinara della Signora \"Petrillo\"", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Basilico napoletano"), ing.get("Aglio"), ing.get("Alici"), ing.get("Olive nere"), ing.get("Origano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Marinara Sbagliata", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Bufala DOC"), ing.get("Aglio"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Origano"))
        ));

        productRepository.save(new Product(
                "Marinara \"a bella mia\"", null, new BigDecimal("10.00"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Aglio"), ing.get("Datterino del piennolo giallo"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Origano"))
        ));

        productRepository.save(new Product(
                "Marinara Shock", "Vincitrice premio speciale con alici e burrata in uscita", new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catMarinare,
                Set.of(ing.get("Datterino del piennolo giallo"), ing.get("Olio"), ing.get("Aglio"), ing.get("Basilico napoletano"), ing.get("Origano"), ing.get("Alici"), ing.get("Burrata"))
        ));

        productRepository.save(new Product(
                "La Napoli", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Alici"), ing.get("Fior di latte d'Agerola"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        Product salamePiccante = productRepository.save(new Product(
                "La Salame Piccante", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Ventricina piccante"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Fior di latte d'Agerola"))
        ));

        productRepository.save(new Product(
                "La Quattro Stagioni", null, new BigDecimal("14.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Salsiccia in arrosto"), ing.get("Carciofini all'olio"), ing.get("Prosciutto cotto"), ing.get("Funghi freschi tagliati a mano"), ing.get("Fior di latte d'Agerola"), ing.get("Olio"), ing.get("Basilico napoletano"))
        ));

        productRepository.save(new Product(
                "La Quattroformaggi", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Bufala DOC"), ing.get("Pecorino"), ing.get("Crema da condimento al formaggio"), ing.get("Gorgonzola"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Capricciosa", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Salsiccia in arrosto"), ing.get("Carciofini all'olio"), ing.get("Prosciutto cotto"), ing.get("Funghi freschi tagliati a mano"), ing.get("Olive nere"), ing.get("Basilico napoletano"), ing.get("Olio"), ing.get("Fior di latte d'Agerola"))
        ));

        productRepository.save(new Product(
                "La Salsiccia e Friarielli", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Friarielli"), ing.get("Salsiccia in arrosto"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Salsiccia e Friarielli alla Casertana", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Friarielli"), ing.get("Salsiccia in arrosto"), ing.get("Provola"), ing.get("Basilico napoletano"), ing.get("Pecorino"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Tonno e Cipolla", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Fior di latte d'Agerola"), ing.get("Tonno"), ing.get("Cipolla rossa"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Patate e Salsiccia", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Fior di latte d'Agerola"), ing.get("Patate"), ing.get("Salsiccia in arrosto"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Funghi e Salsiccia", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Funghi freschi tagliati a mano"), ing.get("Salsiccia in arrosto"), ing.get("Fior di latte d'Agerola"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Biancaneve", null, new BigDecimal("14.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Basilico napoletano"), ing.get("Prosciutto crudo"), ing.get("Rucola"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "L'Americana", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Wurstel"), ing.get("Patate"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Vegetariana", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Verdure grigliate"), ing.get("Fior di latte d'Agerola"), ing.get("Olio"), ing.get("Basilico napoletano"))
        ));

        productRepository.save(new Product(
                "La Funghi e Crudo", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Funghi champignon"), ing.get("Basilico napoletano"), ing.get("Prosciutto crudo"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Funghi e Cotto", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Funghi champignon"), ing.get("Basilico napoletano"), ing.get("Prosciutto cotto"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Scarola", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Scarola"), ing.get("Alici"), ing.get("Olive nere"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Provola e Pepe", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Provola affumicata DOC"), ing.get("Pepe nero"), ing.get("Fior di latte d'Agerola"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Provola e Pepe + Cigoli", null, new BigDecimal("14.50"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Provola affumicata DOC"), ing.get("Pepe nero"), ing.get("Fior di latte d'Agerola"), ing.get("Cigoli"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Provola e Pepe White Passion", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catClassiche,
                Set.of(ing.get("Crema da condimento al formaggio"), ing.get("Provola affumicata DOC"), ing.get("Pepe nero"), ing.get("Fior di latte d'Agerola"), ing.get("Cigoli"), ing.get("Basilico napoletano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Datterina", "Pizza speciale con datterino giallo, alici di Cetara e bufala DOP", new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Bufala DOC"), ing.get("Pomodorino rosso ciliegino"), ing.get("Datterino del piennolo giallo"), ing.get("Alici di Cetara"), ing.get("Aglio"), ing.get("Olio"), ing.get("Basilico napoletano"))
        ));

        productRepository.save(new Product(
                "La Pugliese", null, new BigDecimal("14.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Friarielli"), ing.get("Alici di Cetara"), ing.get("Olive nere"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Zozzona", "Golosità della casa con crema al formaggio, gorgonzola e patate", new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Crema da condimento al formaggio"), ing.get("Ventricina piccante"), ing.get("Basilico napoletano"), ing.get("Patate"), ing.get("Gorgonzola"), ing.get("Olio"), ing.get("Fior di latte d'Agerola"))
        ));

        productRepository.save(new Product(
                "La Santa Domenica", "Specialità al ragù di carne mista e stracciata di bufala DOP", new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Ragù di carne mista"), ing.get("Provola affumicata DOC"), ing.get("Basilico napoletano"), ing.get("Grana"), ing.get("Fior di latte d'Agerola"), ing.get("Stracciata di bufala DOP"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "La Costa d'Amalfi", "Pizza d'autore profumata con succo e zeste di limone fresco", new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Crema da condimento al formaggio"), ing.get("Alici di Cetara"), ing.get("Bufala DOC"), ing.get("Pepe nero"), ing.get("Basilico napoletano"), ing.get("Succo di limone"), ing.get("Zeste di limone"), ing.get("Stracciata di bufala DOP"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Calabrese", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Fior di latte d'Agerola"), ing.get("Cipolla rossa"), ing.get("Olive nere"), ing.get("'Nduja"))
        ));

        productRepository.save(new Product(
                "Hawaii", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catSpeciali,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Fior di latte d'Agerola"), ing.get("Prosciutto cotto"))
        ));

        productRepository.save(new Product(
                "La Parmigiana", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catRicordi,
                Set.of(ing.get("Melanzane"), ing.get("Fior di latte d'Agerola"), ing.get("Basilico napoletano"), ing.get("Provola affumicata DOC"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Olio"))
        ));

        productRepository.save(new Product(
                "Mamma Mia", "Ricetta tradizionale con polpette al sugo fatte in casa", new BigDecimal("17.00"), true,
                DestinationArea.PIZZERIA, catRicordi,
                Set.of(ing.get("Sugo polpette"), ing.get("Polpette"), ing.get("Friarielli"), ing.get("Grana"))
        ));

        productRepository.save(new Product(
                "Amatriciana", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catRicordi,
                Set.of(ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Guanciale"), ing.get("Pecorino"), ing.get("Pepe nero"))
        ));

        productRepository.save(new Product(
                "Carbonara", null, new BigDecimal("15.00"), true,
                DestinationArea.PIZZERIA, catRicordi,
                Set.of(ing.get("Carbo crema"), ing.get("Guanciale"), ing.get("Fior di latte d'Agerola"), ing.get("Pecorino"), ing.get("Pepe nero"))
        ));

        productRepository.save(new Product(
                "Cacio e Pepe", null, new BigDecimal("14.00"), true,
                DestinationArea.PIZZERIA, catRicordi,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Cacio e pepe"))
        ));

        productRepository.save(new Product(
                "Calzone Classico", null, new BigDecimal("10.00"), true,
                DestinationArea.PIZZERIA, catCalzoni,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Pomodoro San Marzano schiacciato a mano"), ing.get("Grana"), ing.get("Basilico napoletano"))
        ));

        productRepository.save(new Product(
                "Calzone Prosciutto Cotto", null, new BigDecimal("12.00"), true,
                DestinationArea.PIZZERIA, catCalzoni,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Grana"), ing.get("Prosciutto cotto"))
        ));

        productRepository.save(new Product(
                "Calzone Salsiccia e Friarielli", null, new BigDecimal("13.00"), true,
                DestinationArea.PIZZERIA, catCalzoni,
                Set.of(ing.get("Fior di latte d'Agerola"), ing.get("Salsiccia in arrosto"), ing.get("Friarielli"))
        ));

        Product acqua = productRepository.save(new Product(
                "Acqua Naturale 1L", "Bottiglia di vetro 1L", new BigDecimal("2.50"), true,
                DestinationArea.SALA, catBevande, Set.of()
        ));

        Product tiramisu = productRepository.save(new Product(
                "Tiramisù della Casa", "Dessert al cucchiaio con mascarpone e savoiardi", new BigDecimal("5.00"), true,
                DestinationArea.SALA, catBevande, Set.of()
        ));

        BigDecimal coperto = new BigDecimal("2.00");

        Order order1 = new Order();
        order1.setTableNumber(4);
        order1.setCoverCount(2);
        order1.setCoverPrice(coperto);
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        order1.setNotes("Pizze ben cotte");

        OrderItem item1_1 = new OrderItem(order1, margherita, 2, margherita.getPrice(), "Una ben cotta");
        OrderItem item1_2 = new OrderItem(order1, salamePiccante, 1, salamePiccante.getPrice(), null);
        OrderItem item1_3 = new OrderItem(order1, acqua, 2, acqua.getPrice(), null);

        order1.getItems().addAll(List.of(item1_1, item1_2, item1_3));
        order1.setTotalAmount(margherita.getPrice().multiply(BigDecimal.valueOf(2))
                .add(salamePiccante.getPrice())
                .add(acqua.getPrice().multiply(BigDecimal.valueOf(2)))
                .add(coperto.multiply(BigDecimal.valueOf(2))));
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setTableNumber(12);
        order2.setCoverCount(3);
        order2.setCoverPrice(coperto);
        order2.setOrderStatus(OrderStatus.PREPARATION);
        order2.setCreatedAt(LocalDateTime.now().minusMinutes(25));
        order2.setNotes(null);

        OrderItem item2_1 = new OrderItem(order2, tiramisu, 3, tiramisu.getPrice(), null);

        order2.getItems().add(item2_1);
        order2.setTotalAmount(tiramisu.getPrice().multiply(BigDecimal.valueOf(3))
                .add(coperto.multiply(BigDecimal.valueOf(3))));
        orderRepository.save(order2);
    }
}