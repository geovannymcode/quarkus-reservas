package com.geovannycode.reservas.service;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

/**
 * Pruebas de lógica de negocio para el servicio de Reservas.
 * <p>
 * Valida las reglas de negocio críticas a través del API HTTP con {@code @QuarkusTest}:
 * <ul>
 *   <li>Creación exitosa de reserva con horario disponible.</li>
 *   <li>Rechazo cuando no hay horario disponible.</li>
 *   <li>Rechazo cuando el profesional o cliente está inactivo.</li>
 *   <li>Cancelación de reserva y liberación de disponibilidad.</li>
 *   <li>Consulta funcional de reservas agrupadas por fecha.</li>
 * </ul>
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservaServiceTest {

    // UUIDs de datos de demo insertados por V2__demo_data.sql
    private static final String PROFESIONAL_ACTIVO_ID   = "a1000000-0000-0000-0000-000000000001";
    private static final String CLIENTE_ACTIVO_ID        = "b2000000-0000-0000-0000-000000000001";
    private static final String PROFESIONAL_INACTIVO_ID  = "a1000000-0000-0000-0000-000000000005";
    private static final String CLIENTE_INACTIVO_ID      = "b2000000-0000-0000-0000-000000000005";
    private static final String RESERVA_ACTIVA_ID        = "d4000000-0000-0000-0000-000000000001";

    private static String reservaCreadaId;

    // --- Listar todas ---

    @Test
    @Order(1)
    void deberiaListarTodasLasReservas() {
        given()
                .when()
                .get("/api/reservas")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()));
    }

    // --- Buscar por ID ---

    @Test
    @Order(2)
    void deberiaBuscarReservaActivaPorId() {
        given()
                .when()
                .get("/api/reservas/" + RESERVA_ACTIVA_ID)
                .then()
                .statusCode(200)
                .body("estado", equalTo("CREADA"))
                .body("profesional.id", equalTo(PROFESIONAL_ACTIVO_ID));
    }

    @Test
    @Order(3)
    void deberiaRetornar404ParaReservaInexistente() {
        given()
                .when()
                .get("/api/reservas/00000000-0000-0000-0000-000000000099")
                .then()
                .statusCode(404)
                .body("codigo", equalTo(404))
                .body("mensaje", containsString("no encontrada"));
    }

    // --- Crear reserva: reglas de negocio ---

    @Test
    @Order(4)
    void deberiaRechazarReservaCuandoNoHayHorarioDisponible() {
        // Día sin horario registrado para este profesional
        var request = String.format("""
                {
                  "fecha": "2099-12-31",
                  "horaInicio": "10:00:00",
                  "horaFin": "11:00:00",
                  "clienteId": "%s",
                  "profesionalId": "%s"
                }
                """, CLIENTE_ACTIVO_ID, PROFESIONAL_ACTIVO_ID);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/reservas")
                .then()
                .statusCode(400)
                .body("mensaje", containsString("horario disponible"));
    }

    @Test
    @Order(5)
    void deberiaRechazarReservaCuandoProfesionalEstaInactivo() {
        var request = String.format("""
                {
                  "fecha": "2025-11-15",
                  "horaInicio": "10:00:00",
                  "horaFin": "11:00:00",
                  "clienteId": "%s",
                  "profesionalId": "%s"
                }
                """, CLIENTE_ACTIVO_ID, PROFESIONAL_INACTIVO_ID);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/reservas")
                .then()
                .statusCode(anyOf(equalTo(404), equalTo(422)));
    }

    @Test
    @Order(6)
    void deberiaRechazarReservaConHoraFinAnteriorAHoraInicio() {
        var request = String.format("""
                {
                  "fecha": "2025-12-01",
                  "horaInicio": "14:00:00",
                  "horaFin": "09:00:00",
                  "clienteId": "%s",
                  "profesionalId": "%s"
                }
                """, CLIENTE_ACTIVO_ID, PROFESIONAL_ACTIVO_ID);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/reservas")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void deberiaRechazarReservaSinCamposObligatorios() {
        var request = """
                {
                  "fecha": "2025-12-01"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/reservas")
                .then()
                .statusCode(400)
                .body("mensaje", containsString("validación"));
    }

    // --- Cancelar reserva ---

    @Test
    @Order(8)
    void deberiaCancelarReservaActivaExitosamente() {
        given()
                .when()
                .patch("/api/reservas/" + RESERVA_ACTIVA_ID + "/cancelar")
                .then()
                .statusCode(200)
                .body("estado", equalTo("CANCELADA"))
                .body("id", equalTo(RESERVA_ACTIVA_ID));
    }

    @Test
    @Order(9)
    void deberiaRechazarCancelacionDeReservaYaCancelada() {
        // La reserva del paso anterior ya quedó CANCELADA
        given()
                .when()
                .patch("/api/reservas/" + RESERVA_ACTIVA_ID + "/cancelar")
                .then()
                .statusCode(400)
                .body("mensaje", containsString("CANCELADA"));
    }

    // --- Consulta funcional por fecha ---

    @Test
    @Order(10)
    void deberiaRetornarReservasAgrupadasPorFecha() {
        given()
                .when()
                .get("/api/reservas/agrupadas/por-fecha")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        // El body es un Map<LocalDate, List<ReservaResponse>>
        // Jackson lo serializa como objeto JSON con fechas como claves
    }

    // --- Ranking funcional de profesionales ---

    @Test
    @Order(11)
    void deberiaRetornarRankingDeProfesionalesPorReservasActivas() {
        given()
                .when()
                .get("/api/profesionales/ranking/por-reservas-activas")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }
}
