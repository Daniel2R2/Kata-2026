package ui.models;

/**
 * Modelo de credenciales para autenticación y registro.
 * Se comparte entre flujos UI y preparación de datos API.
 */
public class UserCredentials {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Constructor vacío para inicialización flexible.
     */
    public UserCredentials() {
    }

    /**
     * Crea un set completo de credenciales.
     *
     * @param firstName nombre del usuario.
     * @param lastName apellido del usuario.
     * @param email email del usuario.
     * @param password contraseña del usuario.
     */
    public UserCredentials(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * @return nombre del usuario.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Define nombre del usuario.
     *
     * @param firstName valor de nombre.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return apellido del usuario.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Define apellido del usuario.
     *
     * @param lastName valor de apellido.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define email del usuario.
     *
     * @param email valor de correo.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define contraseña del usuario.
     *
     * @param password valor de contraseña.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
