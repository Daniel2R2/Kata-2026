package api.models;

/**
 * Payload para creación de usuario en endpoint `/users`.
 */
public class SignupRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Constructor vacío para serialización JSON.
     */
    public SignupRequest() {
    }

    /**
     * Construye payload de registro.
     *
     * @param firstName nombre del usuario.
     * @param lastName apellido del usuario.
     * @param email correo del usuario.
     * @param password contraseña inicial.
     */
    public SignupRequest(String firstName, String lastName, String email, String password) {
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
     * @param firstName nombre a registrar.
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
     * @param lastName apellido a registrar.
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
     * @param email correo a registrar.
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
     * @param password contraseña inicial.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
