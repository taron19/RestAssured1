public class UserRegistration {

    private String email;
    private String password;

    public UserRegistration(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserRegistration() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
