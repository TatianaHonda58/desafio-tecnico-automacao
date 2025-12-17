Código:

  // LoginUITest.java
package tests;

import config.TestConfig;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import utils.WebDriverFactory;
import utils.DBUtils;

import static org.junit.jupiter.api.Assertions.*;

public class LoginUITest {
    private static WebDriver driver;
    private LoginPage loginPage;
    private static final String TEST_USER = "test_user";
    private static final String TEST_VISITOR = "test_visitor";
    private static final String VALID_PASS = "senha123";

    @BeforeAll
    static void setupClass() {
        driver = WebDriverFactory.createDriver(TestConfig.getBrowser());
        DBUtils.prepareTestUsers();
    }

    @BeforeEach
    void setup() {
        driver.get(TestConfig.getBaseUrl() + "/login");
        loginPage = new LoginPage(driver);
    }

    @AfterAll
    static void tearDown() {
        DBUtils.cleanupTestUsers();
        WebDriverFactory.quitDriver();
    }

    @Test
    @DisplayName("Login válido com perfil USER")
    void shouldLoginSuccessfullyAsUser() {
        loginPage.login(TEST_USER, VALID_PASS);
        assertTrue(loginPage.isDashboardLoaded());
        assertTrue(loginPage.getWelcomeText().contains(TEST_USER));
    }

    @Test
    @DisplayName("Usuário VISITOR não deve acessar dashboard")
    void shouldDenyAccessForVisitor() {
        loginPage.login(TEST_VISITOR, VALID_PASS);
        String errorMsg = loginPage.getErrorMessage();
        assertTrue(errorMsg.contains("acesso negado") || errorMsg.contains("403"));
        assertFalse(driver.getCurrentUrl().contains("/dashboard"));
    }

    @Test
    @DisplayName("Bloqueio após 3 tentativas inválidas")
    void shouldBlockUserAfter3FailedAttempts() {
        // 3 tentativas inválidas
        for (int i = 0; i < 3; i++) {
            loginPage.login(TEST_USER, "senha_errada");
            try {
                loginPage.getErrorMessage(); // aguardar feedback
            } catch (Exception ignored) {}
            driver.navigate().refresh();
            loginPage = new LoginPage(driver);
        }

        // Tentativa válida após falhas
        loginPage.login(TEST_USER, VALID_PASS);
        String errorMsg = loginPage.getErrorMessage();
        assertTrue(errorMsg.contains("bloqueado") || errorMsg.contains("423"));
    }
}
  
