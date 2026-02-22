-- ============================================================
-- V1: Esquema inicial del sistema de reservas profesionales
-- ============================================================

-- Tabla: profesionales
CREATE TABLE profesionales (
                               id           UUID         NOT NULL,
                               nombres      VARCHAR(100) NOT NULL,
                               apellidos    VARCHAR(100) NOT NULL,
                               especialidad VARCHAR(150) NOT NULL,
                               estado_activo BOOLEAN     NOT NULL DEFAULT TRUE,
                               CONSTRAINT pk_profesionales PRIMARY KEY (id)
);

-- Tabla: clientes
CREATE TABLE clientes (
                          id            UUID         NOT NULL,
                          nombres       VARCHAR(100) NOT NULL,
                          apellidos     VARCHAR(100) NOT NULL,
                          email         VARCHAR(255) NOT NULL,
                          telefono      VARCHAR(20)  NOT NULL,
                          estado_activo BOOLEAN      NOT NULL DEFAULT TRUE,
                          CONSTRAINT pk_clientes         PRIMARY KEY (id),
                          CONSTRAINT uk_clientes_email   UNIQUE (email)
);

-- Tabla: horarios_disponibles
CREATE TABLE horarios_disponibles (
                                      id              UUID    NOT NULL,
                                      profesional_id  UUID    NOT NULL,
                                      fecha           DATE    NOT NULL,
                                      hora_inicio     TIME    NOT NULL,
                                      hora_fin        TIME    NOT NULL,
                                      estado          BOOLEAN NOT NULL DEFAULT TRUE,
                                      CONSTRAINT pk_horarios_disponibles PRIMARY KEY (id),
                                      CONSTRAINT fk_horarios_profesional
                                          FOREIGN KEY (profesional_id)
                                              REFERENCES profesionales (id)
                                              ON DELETE CASCADE
);

CREATE INDEX idx_horarios_profesional_fecha
    ON horarios_disponibles (profesional_id, fecha);

-- Tabla: reservas
CREATE TABLE reservas (
                          id              UUID        NOT NULL,
                          fecha           DATE        NOT NULL,
                          hora_inicio     TIME        NOT NULL,
                          hora_fin        TIME        NOT NULL,
                          cliente_id      UUID        NOT NULL,
                          profesional_id  UUID        NOT NULL,
                          estado          VARCHAR(20) NOT NULL DEFAULT 'CREADA',
                          CONSTRAINT pk_reservas              PRIMARY KEY (id),
                          CONSTRAINT fk_reservas_cliente
                              FOREIGN KEY (cliente_id)
                                  REFERENCES clientes (id),
                          CONSTRAINT fk_reservas_profesional
                              FOREIGN KEY (profesional_id)
                                  REFERENCES profesionales (id),
                          CONSTRAINT chk_reservas_estado
                              CHECK (estado IN ('CREADA', 'CANCELADA', 'COMPLETADA'))
);

CREATE INDEX idx_reservas_profesional_fecha ON reservas (profesional_id, fecha);
CREATE INDEX idx_reservas_cliente           ON reservas (cliente_id);
CREATE INDEX idx_reservas_estado            ON reservas (estado);
