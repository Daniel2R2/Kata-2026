package utils;

import api.models.ContactRequest;
import api.models.SignupRequest;
import java.text.Normalizer;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import ui.models.Contact;
import ui.models.UserCredentials;

/**
 * Fábrica de datos para las pruebas.
 * Todo sale con formato humano y cada escenario queda independiente.
 */
public final class DataFactory {

    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String EMAIL_DOMAIN = "gmail.com";
    private static final String DEFAULT_COUNTRY = "Colombia";

    private static final List<PersonName> HUMAN_NAMES = List.of(
            new PersonName("Juan", "Perez"),
            new PersonName("Maria", "Rodriguez"),
            new PersonName("Carlos", "Gomez"),
            new PersonName("Laura", "Martinez"),
            new PersonName("Andres", "Lopez"),
            new PersonName("Sofia", "Herrera"),
            new PersonName("Diego", "Ramirez"),
            new PersonName("Paula", "Morales")
    );

    private static final List<String> EMAIL_ROLES = List.of(
            "test",
            "qa",
            "calidad",
            "soporte",
            "equipo",
            "operaciones",
            "clientes",
            "registro"
    );

    private static final List<String> EMAIL_AREAS = List.of(
            "base",
            "norte",
            "sur",
            "centro",
            "ventas",
            "servicio",
            "proyectos",
            "producto"
    );

    private static final List<String> BIRTHDATES = List.of(
            "1991-03-15",
            "1992-07-22",
            "1993-11-09",
            "1994-01-27",
            "1995-05-20",
            "1996-09-12",
            "1997-12-03",
            "1998-04-18"
    );

    private static final List<AddressProfile> ADDRESS_PROFILES = List.of(
            new AddressProfile("Calle 72 #10-34", "Apto 301", "Bogota", "Cundinamarca", "110111", DEFAULT_COUNTRY),
            new AddressProfile("Avenida 19 #45-88", "Oficina 405", "Medellin", "Antioquia", "050021", DEFAULT_COUNTRY),
            new AddressProfile("Carrera 7 #80-15", "Casa 12", "Cali", "Valle del Cauca", "760032", DEFAULT_COUNTRY),
            new AddressProfile("Calle 25 #14-60", "Torre B 802", "Barranquilla", "Atlantico", "080006", DEFAULT_COUNTRY),
            new AddressProfile("Carrera 15 #93-40", "Apto 902", "Bucaramanga", "Santander", "680004", DEFAULT_COUNTRY),
            new AddressProfile("Avenida 30 #55-22", "Casa 7", "Pereira", "Risaralda", "660001", DEFAULT_COUNTRY),
            new AddressProfile("Calle 5 #18-90", "Apto 204", "Manizales", "Caldas", "170001", DEFAULT_COUNTRY),
            new AddressProfile("Carrera 44 #76-31", "Oficina 203", "Cartagena", "Bolivar", "130001", DEFAULT_COUNTRY)
    );

    private static final AtomicInteger PERSON_SEQUENCE = new AtomicInteger(
            ThreadLocalRandom.current().nextInt(0, HUMAN_NAMES.size())
    );
    private static final AtomicInteger EMAIL_SEQUENCE = new AtomicInteger(
            ThreadLocalRandom.current().nextInt(0, EMAIL_ROLES.size() * EMAIL_AREAS.size())
    );
    private static final AtomicInteger ADDRESS_SEQUENCE = new AtomicInteger(
            ThreadLocalRandom.current().nextInt(0, ADDRESS_PROFILES.size())
    );
    private static final AtomicInteger BIRTHDATE_SEQUENCE = new AtomicInteger(
            ThreadLocalRandom.current().nextInt(0, BIRTHDATES.size())
    );

    private DataFactory() {
    }

    /**
     * Genera un correo natural y único.
     */
    public static String uniqueEmail() {
        return nextEmailForPerson(nextPerson());
    }

    /**
     * @return contraseña base de pruebas.
     */
    public static String defaultPassword() {
        return DEFAULT_PASSWORD;
    }

    /**
     * Crea credenciales para signup/login con datos realistas.
     */
    public static UserCredentials uniqueUserCredentials() {
        PersonName person = nextPerson();
        return new UserCredentials(
                person.firstName(),
                person.lastName(),
                nextEmailForPerson(person),
                DEFAULT_PASSWORD
        );
    }

    /**
     * Crea el payload de signup para API.
     */
    public static SignupRequest uniqueSignupRequest() {
        UserCredentials userCredentials = uniqueUserCredentials();
        return new SignupRequest(
                userCredentials.getFirstName(),
                userCredentials.getLastName(),
                userCredentials.getEmail(),
                userCredentials.getPassword()
        );
    }

    /**
     * Arma un contacto UI con todos los campos del formulario.
     */
    public static Contact validUiContact() {
        PersonName person = nextPerson();
        AddressProfile address = nextAddressProfile();
        return new Contact(
                person.firstName(),
                person.lastName(),
                nextBirthdate(),
                uniquePhone(),
                nextEmailForPerson(person),
                address.street1(),
                address.street2(),
                address.city(),
                address.stateProvince(),
                address.postalCode(),
                address.country()
        );
    }

    /**
     * Arma datos nuevos para pruebas de edición.
     */
    public static Contact updatedUiContact() {
        PersonName person = nextPerson();
        AddressProfile address = nextAddressProfile();
        return new Contact(
                person.firstName(),
                person.lastName(),
                nextBirthdate(),
                uniquePhone(),
                nextEmailForPerson(person),
                address.street1(),
                address.street2(),
                address.city(),
                address.stateProvince(),
                address.postalCode(),
                address.country()
        );
    }

    /**
     * Arma payload completo para crear contacto por API.
     */
    public static ContactRequest validApiContact() {
        Contact uiContact = validUiContact();
        return toContactRequest(uiContact);
    }

    /**
     * Arma payload completo para actualizar contacto por API.
     */
    public static ContactRequest updatedApiContact() {
        Contact uiContact = updatedUiContact();
        return toContactRequest(uiContact);
    }

    /**
     * Arma payload incompleto para escenarios negativos.
     */
    public static ContactRequest incompleteApiContactMissingLastName() {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName(nextPerson().firstName());
        return contactRequest;
    }

    private static ContactRequest toContactRequest(Contact contact) {
        ContactRequest contactRequest = new ContactRequest();
        contactRequest.setFirstName(contact.getFirstName());
        contactRequest.setLastName(contact.getLastName());
        contactRequest.setBirthdate(contact.getBirthdate());
        contactRequest.setPhone(contact.getPhone());
        contactRequest.setEmail(contact.getEmail());
        contactRequest.setStreet1(contact.getStreet1());
        contactRequest.setStreet2(contact.getStreet2());
        contactRequest.setCity(contact.getCity());
        contactRequest.setStateProvince(contact.getStateProvince());
        contactRequest.setPostalCode(contact.getPostalCode());
        contactRequest.setCountry(contact.getCountry());
        return contactRequest;
    }

    private static String uniquePhone() {
        return "800" + ThreadLocalRandom.current().nextInt(1000000, 9999999);
    }

    private static String nextBirthdate() {
        int index = BIRTHDATE_SEQUENCE.getAndIncrement();
        return BIRTHDATES.get(Math.floorMod(index, BIRTHDATES.size()));
    }

    private static PersonName nextPerson() {
        int index = PERSON_SEQUENCE.getAndIncrement();
        return HUMAN_NAMES.get(Math.floorMod(index, HUMAN_NAMES.size()));
    }

    private static AddressProfile nextAddressProfile() {
        int index = ADDRESS_SEQUENCE.getAndIncrement();
        return ADDRESS_PROFILES.get(Math.floorMod(index, ADDRESS_PROFILES.size()));
    }

    /**
     * Genera correos tipo nombre.apellido.rol[.area]@dominio.
     * Solo mete timestamp corto cuando ya se gastaron combinaciones base.
     */
    private static String nextEmailForPerson(PersonName person) {
        int index = EMAIL_SEQUENCE.getAndIncrement();
        int combinationsPerPerson = EMAIL_ROLES.size() * EMAIL_AREAS.size();
        int combinationIndex = Math.floorMod(index, combinationsPerPerson);
        int roleIndex = combinationIndex % EMAIL_ROLES.size();
        int areaIndex = (combinationIndex / EMAIL_ROLES.size()) % EMAIL_AREAS.size();
        int cycle = Math.floorDiv(index, combinationsPerPerson);

        String firstName = normalizeForEmail(person.firstName());
        String lastName = normalizeForEmail(person.lastName());
        String role = EMAIL_ROLES.get(roleIndex);
        String area = EMAIL_AREAS.get(areaIndex);

        StringBuilder localPart = new StringBuilder();
        localPart.append(firstName).append('.').append(lastName).append('.').append(role);
        if (!"base".equals(area)) {
            localPart.append('.').append(area);
        }
        if (cycle > 0) {
            localPart.append('.').append(shortTimestampToken());
        }
        return localPart + "@" + EMAIL_DOMAIN;
    }

    private static String normalizeForEmail(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z]", "");
        if (normalized.isBlank()) {
            return "usuario";
        }
        return normalized;
    }

    private static String shortTimestampToken() {
        return DateTimeFormatter.ofPattern("yyMMddHHmm")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    private record PersonName(String firstName, String lastName) {
    }

    private record AddressProfile(
            String street1,
            String street2,
            String city,
            String stateProvince,
            String postalCode,
            String country
    ) {
    }
}
