package cl.tofcompany.ready.Model;

public class Usuario {
    String Id;
    String username;
    String email;
    String password;
    String password2;

    public Usuario() {
    }

    public Usuario(String id , String username , String email , String password , String password2) {
        Id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.password2 = password2;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}