package com.valdeci.apitests.tests.products;

import com.valdeci.apitests.clients.AuthClient;
import com.valdeci.apitests.clients.ProductClient;
import com.valdeci.apitests.clients.UserClient;
import com.valdeci.apitests.models.Product;
import com.valdeci.apitests.models.User;
import com.valdeci.apitests.utils.DataFactory;
import com.valdeci.apitests.utils.SchemaValidator;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.*;

/**
 * Testes de CRUD de Produtos com validação de autenticação e contrato.
 */
@Epic("Produtos")
@Feature("CRUD de Produtos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTests {

    private final ProductClient productClient = new ProductClient();
    private final UserClient userClient = new UserClient();
    private final AuthClient authClient = new AuthClient();

    private static String adminToken;
    private static String createdProductId;

    @BeforeAll
    static void setup() {
        UserClient client = new UserClient();
        AuthClient auth = new AuthClient();

        User admin = DataFactory.generateAdminUser();
        client.createUser(admin).then().statusCode(201);
        adminToken = auth.getToken(admin.getEmail(), admin.getPassword());
    }

    @Test
    @Order(1)
    @Story("Criar produto com sucesso")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldCreateProductSuccessfully() {
        Product product = DataFactory.generateProduct();

        var response = productClient.createProduct(product, adminToken)
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().response();

        createdProductId = response.jsonPath().getString("_id");
    }

    @Test
    @Order(2)
    @Story("Criar produto sem autenticação")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturn401WhenCreatingProductWithoutToken() {
        Product product = DataFactory.generateProduct();
        productClient.createProduct(product, "token_invalido_aqui_123")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(3)
    @Story("Listar produtos e validar schema")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldListProductsAndValidateSchema() {
        productClient.getAllProducts()
                .then()
                .statusCode(200)
                .body("quantidade", greaterThan(0))
                .body("produtos", not(empty()))
                .body(SchemaValidator.validateSchema("products-list-schema.json"));
    }

    @Test
    @Order(4)
    @Story("Buscar produto por ID")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldGetProductById() {
        productClient.getProductById(createdProductId)
                .then()
                .statusCode(200)
                .body("_id", equalTo(createdProductId));
    }

    @Test
    @Order(5)
    @Story("Buscar produto com ID inexistente")
    @Severity(SeverityLevel.NORMAL)
    public void shouldReturn400WithInvalidProductId() {
        // ServeRest exige exatamente 16 caracteres alfanuméricos
        productClient.getProductById("1234567890123456")
                .then()
                .statusCode(400)
                .body("message", equalTo("Produto não encontrado"));
    }

    @Test
    @Order(6)
    @Story("Atualizar produto com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldUpdateProductSuccessfully() {
        Product updatedProduct = new Product(
                "Produto Atualizado " + System.currentTimeMillis(),
                999,
                "Descrição atualizada",
                50
        );
        productClient.updateProduct(createdProductId, updatedProduct, adminToken)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Order(7)
    @Story("Deletar produto com sucesso")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDeleteProductSuccessfully() {
        productClient.deleteProduct(createdProductId, adminToken)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Order(8)
    @Story("Fluxo completo de produto")
    @Severity(SeverityLevel.BLOCKER)
    public void shouldExecuteFullProductLifecycle() {
        // 1. Criar
        Product product = DataFactory.generateProduct();
        String productId = productClient.createProduct(product, adminToken)
                .then().statusCode(201)
                .extract().jsonPath().getString("_id");

        // 2. Buscar
        productClient.getProductById(productId)
                .then().statusCode(200)
                .body("_id", equalTo(productId));

        // 3. Atualizar
        Product updated = DataFactory.generateProduct();
        productClient.updateProduct(productId, updated, adminToken)
                .then().statusCode(200);

        // 4. Deletar
        productClient.deleteProduct(productId, adminToken)
                .then().statusCode(200);

        // 5. Confirmar inexistência
        productClient.getProductById(productId)
                .then().statusCode(400);
    }
}
