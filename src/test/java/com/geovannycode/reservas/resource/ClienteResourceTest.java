package com.geovannycode.reservas.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Pruebas de integración para {@code ClienteResource}.
 * <p>
 * Valida el CRUD completo incluyendo la regla de unicidad de email.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClienteResourceTest {

    private static String clienteIdCreado;

    // --- Listar ---

    @Test
    @Order(1)
    void deberiaRetornarListaDeClientes() {
        given()
                .when()
                .get("/api/clientes")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("[0].nombres", notNullValue())
                .body("[0].email", notNullValue());
    }

    // --- Crear ---

    @Test
    @Order(2)
    void deberiaCrearClienteExitosamente() {
        var requestBody = """
                {
                  "nombres": "Laura",
                  "apellidos": "Gómez Ríos",
                  "email": "laura.gomez.test@email.com",
                  "telefono": "+57 300 111 2222",
                  "estadoActivo": true
                }
                """;

        clienteIdCreado = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clientes")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("nombres", equalTo("Laura"))
                .body("apellidos", equalTo("Gómez Ríos"))
                .body("email", equalTo("laura.gomez.test@email.com"))
                .body("estadoActivo", equalTo(true))
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    void deberiaRechazarEmailDuplicado() {
        var requestBody = """
                {
                  "nombres": "Otra",
                  "apellidos": "Persona",
                  "email": "laura.gomez.test@email.com",
                  "telefono": "+57 300 999 8888",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clientes")
                .then()
                .statusCode(409)
                .body("mensaje", containsString("email"));
    }

    @Test
    @Order(4)
    void deberiaRechazarCreacionConCamposVacios() {
        var requestBody = """
                {
                  "nombres": "",
                  "apellidos": "",
                  "email": "invalido",
                  "telefono": "abc",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clientes")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    void deberiaRechazarEmailConFormatoInvalido() {
        var requestBody = """
                {
                  "nombres": "Test",
                  "apellidos": "User",
                  "email": "no-es-un-email",
                  "telefono": "+57 300 111 2222",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clientes")
                .then()
                .statusCode(400);
    }

    // --- Buscar por ID ---

    @Test
    @Order(6)
    void deberiaBuscarClientePorIdExistente() {
        given()
                .when()
                .get("/api/clientes/" + clienteIdCreado)
                .then()
                .statusCode(200)
                .body("id", equalTo(clienteIdCreado))
                .body("nombres", equalTo("Laura"));
    }

    @Test
    @Order(7)
    void deberiaRetornar404CuandoClienteNoExiste() {
        given()
                .when()
                .get("/api/clientes/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404)
                .body("codigo", equalTo(404))
                .body("mensaje", containsString("no encontrado"));
    }

    // --- Actualizar ---

    @Test
    @Order(8)
    void deberiaActualizarClienteExitosamente() {
        var requestBody = """
                {
                  "nombres": "Laura Actualizada",
                  "apellidos": "Gómez Ríos",
                  "email": "laura.gomez.test@email.com",
                  "telefono": "+57 300 111 3333",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/clientes/" + clienteIdCreado)
                .then()
                .statusCode(200)
                .body("nombres", equalTo("Laura Actualizada"))
                .body("telefono", equalTo("+57 300 111 3333"));
    }

    @Test
    @Order(9)
    void deberiaRechazarActualizacionConEmailDuplicadoDeOtroCliente() {
        var requestBody = """
                {
                  "nombres": "Laura Actualizada",
                  "apellidos": "Gómez Ríos",
                  "email": "ana.torres@email.com",
                  "telefono": "+57 300 111 3333",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/clientes/" + clienteIdCreado)
                .then()
                .statusCode(409)
                .body("mensaje", containsString("email"));
    }

    // --- Eliminar ---

    @Test
    @Order(10)
    void deberiaEliminarClienteExitosamente() {
        given()
                .when()
                .delete("/api/clientes/" + clienteIdCreado)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(11)
    void deberiaRetornar404AlEliminarClienteInexistente() {
        given()
                .when()
                .delete("/api/clientes/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404);
    }
}
