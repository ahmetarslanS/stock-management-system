package p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class LoginScreenTest {
    @Test
    public void testLogin() {
        // Create an instance of the LoginScreen class
        LoginScreen loginScreen = new LoginScreen();

        // Test the login method with correct username and password
        assertTrue(loginScreen.login("ahmetarslan", "123456"));

        // Test the login method with incorrect username and password
        assertFalse(loginScreen.login("invalid", "invalid"));
    }
    @Test
    public void testRegister() {
        // Create a LoginScreen object
        LoginScreen loginScreen = new LoginScreen();

        // Test the register method with valid username and password
        boolean result = loginScreen.register("newuser", "newpassword");
        assertTrue(result);

        // Test the register method with an existing username
        result = loginScreen.register("newuser", "differentpassword");
        assertTrue(!result);
    }
}
