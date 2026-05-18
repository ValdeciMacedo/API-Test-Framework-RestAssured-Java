package com.valdeci.apitests.tests.users;

import com.valdeci.apitests.clients.AuthClient;
import com.valdeci.apitests.clients.UserClient;
import com.valdeci.apitests.models.User;
import com.valdeci.apitests.utils.DataFactory;
import com.valdeci.apitests.utils.SchemaValidator;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de CRUD completo para usuários.
 */
@Epic("Usuários")
@Feature("CRUD de Usuários")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    private final UserClient userClient = new UserClient();
    private final AuthClient authClient = new AuthClient();

    private static String createdUserId;
    private static String createdUserEmail;
    private static String adminToken;

    @BeforeAll
    static void setup() {
        UserClient client = new UserClient();
        AuthClient auth = new AuthClient();

        User admin = DataFactory.generateAdminUser();
        createdUserEmail = admin.getEmail();

        client.createUser(admin).then().statusCode(201);
        adminToken = auth.getToken(admin.getEmail(), admin.getPassword());
    }

    @Test
    @Order(1)
    @Story("Criar usuário com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldCreateUserSuccessfully() {
        User user = DataFactory.generateAdminUser();
        createdUserEmail = user.getEmail();

        var response = userClient.createUser(user)
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().response();

        createdUserId = response.jsonPath().getString("_id");
        assertNotNull(createdUserId);
    }

    @Test
    @Order(2)
    @Story("Criar usuário duplicado")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturn400WhenEmailAlreadyExists() {
        User user = DataFactory.generateUserWithEmail(createdUserEmail);
        userClient.createUser(user)
                .then()
                .statusCode(400)
                .body("message", equalTo("Este email já está sendo usado"));
    }

    @Test
    @Order(3)
    @Story("Criar usuário sem email")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WhenEmailIsMissing() {
        User user = new User("Nome Teste", "", "senha123", "false");
        userClient.createUser(user)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    @Story("Listar todos os usuários")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldGetAllUsersAndValidateSchema() {
        userClient.getAllUsers()
                .then()
                .statusCode(200)
                .body("quantidade", greaterThan(0))
                .body("usuarios", not(empty()))
                .body(SchemaValidator.validateSchema("users-list-schema.json"));
    }

    @Test
    @Order(5)
    @Story("Buscar usuário por ID")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldGetUserById() {
        userClient.getUserById(createdUserId)
                .then()
                .statusCode(200)
                .body("_id", equalTo(createdUserId));
    }

    @Test
    @Order(6)
    @Story("Buscar usuário com ID inexistente")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WithInvalidUserId() {
        // ServeRest exige exatamente 16 caracteres alfanuméricos
        userClient.getUserById("1234567890123456")
                .then()
                .statusCode(400)
                .body("message", equalTo("Usuário não encontrado"));
    }

    @Test
    @Order(7)
    @Story("Atualizar usuário com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldUpdateUserSuccessfully() {
        User updatedUser = new User(
                "Nome Atualizado",
                DataFactory.generateEmail(),
                "NovaSenha@123",
                "false"
        );
        userClient.updateUser(createdUserId, updatedUser, adminToken)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Order(8)
    @Story("Deletar usuário com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDeleteUserSuccessfully() {
        userClient.deleteUser(createdUserId, adminToken)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Order(9)
    @Story("Fluxo completo de usuário")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldExecuteFullUserLifecycle() {
        // 1. Criar
        User newUser = DataFactory.generateAdminUser();
        String userId = userClient.createUser(newUser)
                .then().statusCode(201)
                .extract().jsonPath().getString("_id");

        // 2. Buscar
        userClient.getUserById(userId)
                .then().statusCode(200)
                .body("_id", equalTo(userId));

        // 3. Atualizar
        User updatedUser = DataFactory.generateRegularUser();
        userClient.updateUser(userId, updatedUser, adminToken)
                .then().statusCode(200);

        // 4. Deletar
        userClient.deleteUser(userId, adminToken)
                .then().statusCode(200);

        // 5. Confirmar que não existe mais
        userClient.getUserById(userId)
                .then().statusCode(400);
    }
}
