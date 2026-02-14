package ui.models;

/**
 * Modelo de contacto usado por flujos UI (Screenplay).
 * Representa los campos mínimos que se llenan y validan en formularios.
 */
public class Contact {

    private String firstName;
    private String lastName;
    private String birthdate;
    private String phone;
    private String email;
    private String street1;
    private String street2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    /**
     * Constructor vacío requerido por utilitarios y serializadores.
     */
    public Contact() {
    }

    /**
     * Crea un contacto con datos de prueba completos.
     *
     * @param firstName nombre del contacto.
     * @param lastName apellido del contacto.
     * @param phone teléfono del contacto.
     * @param email correo del contacto.
     */
    public Contact(String firstName, String lastName, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Crea un contacto con todos los campos disponibles en el formulario UI.
     *
     * @param firstName nombre del contacto.
     * @param lastName apellido del contacto.
     * @param birthdate fecha de nacimiento en formato ISO yyyy-MM-dd.
     * @param phone telÃ©fono del contacto.
     * @param email correo del contacto.
     * @param street1 direcciÃ³n lÃ­nea 1.
     * @param street2 direcciÃ³n lÃ­nea 2.
     * @param city ciudad del contacto.
     * @param stateProvince estado o provincia del contacto.
     * @param postalCode cÃ³digo postal.
     * @param country paÃ­s del contacto.
     */
    public Contact(String firstName,
                   String lastName,
                   String birthdate,
                   String phone,
                   String email,
                   String street1,
                   String street2,
                   String city,
                   String stateProvince,
                   String postalCode,
                   String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.phone = phone;
        this.email = email;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
    }

    /**
     * @return nombre del contacto.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Actualiza nombre del contacto.
     *
     * @param firstName nuevo nombre.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return apellido del contacto.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Actualiza apellido del contacto.
     *
     * @param lastName nuevo apellido.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return teléfono del contacto.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Actualiza teléfono del contacto.
     *
     * @param phone nuevo teléfono.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return correo del contacto.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Actualiza correo del contacto.
     *
     * @param email nuevo correo.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return fecha de nacimiento del contacto.
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Actualiza fecha de nacimiento del contacto.
     *
     * @param birthdate fecha en formato ISO yyyy-MM-dd.
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * @return direcciÃ³n principal.
     */
    public String getStreet1() {
        return street1;
    }

    /**
     * Actualiza direcciÃ³n principal.
     *
     * @param street1 direcciÃ³n lÃ­nea 1.
     */
    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    /**
     * @return direcciÃ³n secundaria.
     */
    public String getStreet2() {
        return street2;
    }

    /**
     * Actualiza direcciÃ³n secundaria.
     *
     * @param street2 direcciÃ³n lÃ­nea 2.
     */
    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    /**
     * @return ciudad del contacto.
     */
    public String getCity() {
        return city;
    }

    /**
     * Actualiza ciudad del contacto.
     *
     * @param city ciudad.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return estado o provincia.
     */
    public String getStateProvince() {
        return stateProvince;
    }

    /**
     * Actualiza estado o provincia.
     *
     * @param stateProvince estado/provincia.
     */
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    /**
     * @return cÃ³digo postal del contacto.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Actualiza cÃ³digo postal.
     *
     * @param postalCode valor de cÃ³digo postal.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return paÃ­s del contacto.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Actualiza paÃ­s del contacto.
     *
     * @param country paÃ­s.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Entrega nombre completo para búsquedas/validaciones en el listado UI.
     *
     * @return nombre y apellido concatenados.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
