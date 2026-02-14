package api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Payload de contacto para endpoints `/contacts`.
 * Solo serializa campos no nulos para facilitar pruebas parciales (PATCH/negativas).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactRequest {

    private String firstName;
    private String lastName;
    private String birthdate;
    private String email;
    private String phone;
    private String street1;
    private String street2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    /**
     * Constructor vacío para serialización JSON.
     */
    public ContactRequest() {
    }

    /**
     * @return nombre del contacto.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Define nombre del contacto.
     *
     * @param firstName nombre a persistir.
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
     * Define apellido del contacto.
     *
     * @param lastName apellido a persistir.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return fecha de nacimiento en formato ISO yyyy-MM-dd.
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * Define fecha de nacimiento.
     *
     * @param birthdate fecha en formato ISO local date.
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * @return email del contacto.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define email del contacto.
     *
     * @param email correo de contacto.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return teléfono del contacto.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Define teléfono del contacto.
     *
     * @param phone número de teléfono.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return dirección línea 1.
     */
    public String getStreet1() {
        return street1;
    }

    /**
     * Define dirección línea 1.
     *
     * @param street1 dirección principal.
     */
    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    /**
     * @return dirección línea 2.
     */
    public String getStreet2() {
        return street2;
    }

    /**
     * Define dirección línea 2.
     *
     * @param street2 dirección secundaria.
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
     * Define ciudad del contacto.
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
     * Define estado o provincia.
     *
     * @param stateProvince valor de estado/provincia.
     */
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    /**
     * @return código postal.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Define código postal.
     *
     * @param postalCode valor de código postal.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return país del contacto.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Define país del contacto.
     *
     * @param country nombre del país.
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
