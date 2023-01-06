package p1;

public class Admin {

    protected String username;
    protected char[] password;

    public Admin(String username, char[] password) {

        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
