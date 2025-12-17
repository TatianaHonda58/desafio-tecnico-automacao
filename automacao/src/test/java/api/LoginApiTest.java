Código:
  
//LoginApiTests.java
package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.DBUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginApiTest {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String USER = "test_user";
    private static final String VISITOR = "test_visitor";
    private static final String VALID_PASS = "senha123";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
        DBUtils.prepareTestUsers();
    }

    @AfterAll
    static void cleanup() {
        DBUtils.cleanupTestUsers();
    }

    @Test
    @DisplayName("200 - Login válido retorna token e perfil")
    void shouldReturnTokenAndProfileOnValidLogin() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + USER + "\", \"password\":\"" + VALID_PASS + "\"}")
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", is(notNullValue()))
            .body("profile", is("USER"));
    }

    @Test
    @DisplayName("401 - Credenciais inválidas")
    void shouldReturn401OnInvalidCredentials() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + USER + "\", \"password\":\"senha_errada\"}")
        .when()
            .post("/login")
        .then()
            .statusCode(401)
            .body("error", containsString("credenciais inválidas"));
    }

    @Test
    @DisplayName("403 - Acesso negado para VISITOR")
    void shouldReturn403ForVisitorProfile() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + VISITOR + "\", \"password\":\"" + VALID_PASS + "\"}")
        .when()
            .post("/login")
        .then()
            .statusCode(403)
            .body("error", containsString("acesso negado"));
    }

    @Test
    @DisplayName("423 - Usuário bloqueado após 3 tentativas")
    void shouldReturn423WhenUserIsBlocked() {
        // Simular 3 falhas
        for (int i = 0; i < 3; i++) {
            given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"" + USER + "\", \"password\":\"senha_errada" + i + "\"}")
            .when()
                .post("/login");
        }

        // Tentar login válido
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + USER + "\", \"password\":\"" + VALID_PASS + "\"}")
        .when()
            .post("/login")
        .then()
            .statusCode(423)
            .body("error", containsString("bloqueado"));
    }
}
