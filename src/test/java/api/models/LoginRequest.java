package api.models;

/**
 * Payload para endpoint de autenticación `/users/login`.
 */
public class LoginRequest {

    private String email;
    private String password;

    /**
     * Constructor vacío para serialización JSON.
     */
    public LoginRequest() {
    }

    /**
     * Construye payload completo de login.
     *
     * @param email correo de autenticación.
     * @param password contraseña.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @return email a enviar en login.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define email para login.
     *
     * @param email correo del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return contraseña a enviar en login.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define contraseña para login.
     *
     * @param password contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
