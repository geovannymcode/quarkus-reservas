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
 * Pruebas de integración para {@code HorarioDisponibleResource}.
 * <p>
 * Valida el registro de horarios, anti-solapamiento y eliminación.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HorarioDisponibleResourceTest {

    private static final String PROFESIONAL_ANA_ID = "a1000000-0000-0000-0000-000000000004";
    private static final String PROFESIONAL_LUIS_ID = "a1000000-0000-0000-0000-000000000001";

    private static String horarioIdCreado;

    // --- Listar ---

    @Test
    @Order(1)
    void deberiaRetornarListaDeHorarios() {
        given()
                .when()
                .get("/api/horarios")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()));
    }

    @Test
    @Order(2)
    void deberiaRetornarHorariosPorProfesional() {
        given()
                .when()
                .get("/api/horarios/profesional/" + PROFESIONAL_LUIS_ID)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()));
    }

    @Test
    @Order(3)
    void deberiaRetornarListaVaciaParaProfesionalSinHorarios() {
        given()
                .when()
                .get("/api/horarios/profesional/a1000000-0000-0000-0000-000000000005")
                .then()
                .statusCode(200)
                .body("$", empty());
    }

    // --- Registrar ---

    @Test
    @Order(4)
    void deberiaRegistrarHorarioExitosamente() {
        var requestBody = String.format("""
                {
                  "profesionalId": "%s",
                  "fecha": "2026-06-15",
                  "horaInicio": "09:00",
                  "horaFin": "12:00"
                }
                """, PROFESIONAL_ANA_ID);

        horarioIdCreado = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/horarios")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("fecha", equalTo("2026-06-15"))
                .body("horaInicio", equalTo("09:00:00"))
                .body("horaFin", equalTo("12:00:00"))
                .extract()
                .path("id");
    }

    @Test
    @Order(5)
    void deberiaRechazarHorarioSolapado() {
        var requestBody = String.format("""
                {
                  "profesionalId": "%s",
                  "fecha": "2026-06-15",
                  "horaInicio": "11:00",
                  "horaFin": "14:00"
                }
                """, PROFESIONAL_ANA_ID);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/horarios")
                .then()
                .statusCode(409)
                .body("mensaje", containsString("solapa"));
    }

    @Test
    @Order(6)
    void deberiaRechazarHorarioConHoraFinAnteriorAInicio() {
        var requestBody = String.format("""
                {
                  "profesionalId": "%s",
                  "fecha": "2026-06-20",
                  "horaInicio": "14:00",
                  "horaFin": "10:00"
                }
                """, PROFESIONAL_ANA_ID);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/horarios")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void deberiaRechazarHorarioConProfesionalInexistente() {
        var requestBody = """
                {
                  "profesionalId": "00000000-0000-0000-0000-000000000099",
                  "fecha": "2026-06-20",
                  "horaInicio": "09:00",
                  "horaFin": "12:00"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/horarios")
                .then()
                .statusCode(404)
                .body("mensaje", containsString("no encontrado"));
    }

    @Test
    @Order(8)
    void deberiaRechazarHorarioConCamposNulos() {
        var requestBody = """
                {
                  "profesionalId": null,
                  "fecha": null,
                  "horaInicio": null,
                  "horaFin": null
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/horarios")
                .then()
                .statusCode(400);
    }

    // --- Eliminar ---

    @Test
    @Order(9)
    void deberiaEliminarHorarioExitosamente() {
        given()
                .when()
                .delete("/api/horarios/" + horarioIdCreado)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void deberiaRetornar404AlEliminarHorarioInexistente() {
        given()
                .when()
                .delete("/api/horarios/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404);
    }
}
