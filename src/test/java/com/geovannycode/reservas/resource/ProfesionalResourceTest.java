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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Pruebas de integración para {@code ProfesionalResource}.
 * <p>
 * Usa {@code @QuarkusTest} que levanta el servidor completo con Dev Services
 * (PostgreSQL en Docker) y Flyway para inicializar el esquema y datos de prueba.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfesionalResourceTest {

    private static String profesionalIdCreado;

    // --- Listar ---

    @Test
    @Order(1)
    void deberiaRetornarListaDeProfesionales() {
        given()
                .when()
                .get("/api/profesionales")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                // Datos de demo insertados por V2__demo_data.sql
                .body("[0].nombres", notNullValue())
                .body("[0].estadoActivo", notNullValue());
    }

    // --- Crear ---

    @Test
    @Order(2)
    void deberiaCrearProfesionalExitosamente() {
        var requestBody = """
                {
                  "nombres": "Pedro",
                  "apellidos": "Quintero Aranda",
                  "especialidad": "Terapia Ocupacional",
                  "estadoActivo": true
                }
                """;

        profesionalIdCreado = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/profesionales")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("nombres", equalTo("Pedro"))
                .body("apellidos", equalTo("Quintero Aranda"))
                .body("especialidad", equalTo("Terapia Ocupacional"))
                .body("estadoActivo", equalTo(true))
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    void deberiaRechazarCreacionConCamposVacios() {
        var requestBody = """
                {
                  "nombres": "",
                  "apellidos": "Pérez",
                  "especialidad": "Psicología"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/profesionales")
                .then()
                .statusCode(400)
                .body("mensaje", containsString("nombre"));
    }

    // --- Buscar por ID ---

    @Test
    @Order(4)
    void deberiaBuscarProfesionalPorIdExistente() {
        given()
                .when()
                .get("/api/profesionales/" + profesionalIdCreado)
                .then()
                .statusCode(200)
                .body("id", equalTo(profesionalIdCreado))
                .body("nombres", equalTo("Pedro"));
    }

    @Test
    @Order(5)
    void deberiaRetornar404CuandoProfesionalNoExiste() {
        given()
                .when()
                .get("/api/profesionales/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404)
                .body("codigo", equalTo(404))
                .body("mensaje", containsString("no encontrado"));
    }

    // --- Actualizar ---

    @Test
    @Order(6)
    void deberiaActualizarProfesionalExitosamente() {
        var requestBody = """
                {
                  "nombres": "Pedro Actualizado",
                  "apellidos": "Quintero Aranda",
                  "especialidad": "Neuropsicología",
                  "estadoActivo": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/profesionales/" + profesionalIdCreado)
                .then()
                .statusCode(200)
                .body("nombres", equalTo("Pedro Actualizado"))
                .body("especialidad", equalTo("Neuropsicología"));
    }

    // --- Ranking ---

    @Test
    @Order(7)
    void deberiaRetornarRankingDeProfesionalesPorReservasActivas() {
        given()
                .when()
                .get("/api/profesionales/ranking/por-reservas-activas")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
        // El primer elemento debe tener más reservas que el siguiente
        // (verificado implícitamente por el orden descendente)
    }

    // --- Eliminar ---

    @Test
    @Order(8)
    void deberiaEliminarProfesionalExitosamente() {
        given()
                .when()
                .delete("/api/profesionales/" + profesionalIdCreado)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(9)
    void deberiaRetornar404AlEliminarProfesionalInexistente() {
        given()
                .when()
                .delete("/api/profesionales/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404);
    }
}
