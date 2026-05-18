package com.valdeci.apitests.tests.auth;

import com.valdeci.apitests.clients.AuthClient;
import com.valdeci.apitests.clients.UserClient;
import com.valdeci.apitests.models.User;
import com.valdeci.apitests.utils.DataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

/**
 * Testes de autenticação — login, token, cenários negativos.
 */
@Epic("Autenticação")
@Feature("Login")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTests {

    private final AuthClient authClient = new AuthClient();
    private final UserClient userClient = new UserClient();

    private static String userEmail;
    private static String userPassword = "Test@12345";

    @BeforeAll
    static void setup() {
        // Cria usuário para usar nos testes de login
        UserClient client = new UserClient();
        User user = DataFactory.generateAdminUser();
        userEmail = user.getEmail();

        client.createUser(user)
                .then()
                .statusCode(201);
    }

    @Test
    @Order(1)
    @Story("Login com credenciais válidas")
    @Description("Deve realizar login com sucesso e retornar token Bearer")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldLoginWithValidCredentials() {
        authClient.login(userEmail, userPassword)
                .then()
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", startsWith("Bearer "))
                .body("authorization", notNullValue());
    }

    @Test
    @Order(2)
    @Story("Token deve ser válido")
    @Description("Token retornado deve ser utilizável em endpoints protegidos")
    @Severity(SeverityLevel.CRITICAL)
    public void tokenShouldBeValid() {
        String token = authClient.getToken(userEmail, userPassword);

        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.startsWith("Bearer "), "Token deve iniciar com 'Bearer '");
    }

    @Test
    @Order(3)
    @Story("Login com senha inválida")
    @Description("Deve retornar 401 ao tentar login com senha incorreta")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturn401WithWrongPassword() {
        authClient.login(userEmail, "senhaErrada123")
                .then()
                .statusCode(401)
                .body("message", equalTo("Email e/ou senha inválidos"));
    }

    @Test
    @Order(4)
    @Story("Login com email inválido")
    @Description("Deve retornar 401 ao tentar login com email não cadastrado")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn401WithInvalidEmail() {
        authClient.login("email_inexistente@teste.com", userPassword)
                .then()
                .statusCode(401)
                .body("message", equalTo("Email e/ou senha inválidos"));
    }

    @Test
    @Order(5)
    @Story("Login com campos vazios")
    @Description("Deve retornar 400 ao enviar campos obrigatórios vazios")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WithEmptyFields() {
        authClient.login("", "")
                .then()
                .statusCode(400);
    }
}
