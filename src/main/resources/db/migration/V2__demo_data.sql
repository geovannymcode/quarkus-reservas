-- ============================================================
-- V2: Datos de demostración para el sistema de reservas
-- ============================================================

-- Profesionales de ejemplo
INSERT INTO profesionales (id, nombres, apellidos, especialidad, estado_activo) VALUES
                                                                                    ('a1000000-0000-0000-0000-000000000001', 'Luis Alberto', 'Salazar Ríos',    'Psicología Clínica',    TRUE),
                                                                                    ('a1000000-0000-0000-0000-000000000002', 'María Fernanda', 'Torres Vega',   'Mentoría Empresarial',  TRUE),
                                                                                    ('a1000000-0000-0000-0000-000000000003', 'Carlos Eduardo', 'Mendoza López', 'Tutoría Matemáticas',   TRUE),
                                                                                    ('a1000000-0000-0000-0000-000000000004', 'Ana Sofía',      'Paredes Ruiz',  'Asesoría Financiera',   TRUE),
                                                                                    ('a1000000-0000-0000-0000-000000000005', 'Jorge Andrés',   'Castillo Mora', 'Coaching de Vida',      FALSE);

-- Clientes de ejemplo
INSERT INTO clientes (id, nombres, apellidos, email, telefono, estado_activo) VALUES
                                                                                  ('b2000000-0000-0000-0000-000000000001', 'Ana',     'Torres Vega',    'ana.torres@email.com',    '+57 310 111 2222', TRUE),
                                                                                  ('b2000000-0000-0000-0000-000000000002', 'Marco',   'Díaz Peña',      'marco.diaz@email.com',    '+57 320 333 4444', TRUE),
                                                                                  ('b2000000-0000-0000-0000-000000000003', 'Sofía',   'Ramírez López',  'sofia.ramirez@email.com', '+57 315 555 6666', TRUE),
                                                                                  ('b2000000-0000-0000-0000-000000000004', 'Carlos',  'Gutiérrez Mora', 'carlos.g@email.com',      '+57 300 777 8888', TRUE),
                                                                                  ('b2000000-0000-0000-0000-000000000005', 'Valentina','Herrera Soto',  'valentina.h@email.com',   '+57 312 999 0000', FALSE);

-- Horarios disponibles (usando fechas futuras relativas)
INSERT INTO horarios_disponibles (id, profesional_id, fecha, hora_inicio, hora_fin, estado) VALUES
                                                                                                -- Luis Salazar: disponible 09:00-17:00 en varios días
                                                                                                ('c3000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001', CURRENT_DATE + 1, '09:00', '12:00', TRUE),
                                                                                                ('c3000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001', CURRENT_DATE + 1, '14:00', '17:00', TRUE),
                                                                                                ('c3000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001', CURRENT_DATE + 2, '09:00', '13:00', FALSE),
                                                                                                -- María Torres: disponible en el día 3
                                                                                                ('c3000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000002', CURRENT_DATE + 2, '10:00', '16:00', TRUE),
                                                                                                ('c3000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000002', CURRENT_DATE + 3, '08:00', '12:00', TRUE),
                                                                                                -- Carlos Mendoza: disponible en día 2
                                                                                                ('c3000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000003', CURRENT_DATE + 2, '14:00', '18:00', TRUE);

-- Reservas de demostración
INSERT INTO reservas (id, fecha, hora_inicio, hora_fin, cliente_id, profesional_id, estado) VALUES
                                                                                                -- Reserva activa: Ana Torres con Luis Salazar (el horario queda ocupado)
                                                                                                ('d4000000-0000-0000-0000-000000000001',
                                                                                                 CURRENT_DATE + 2, '09:00', '10:00',
                                                                                                 'b2000000-0000-0000-0000-000000000001',
                                                                                                 'a1000000-0000-0000-0000-000000000001',
                                                                                                 'CREADA'),
                                                                                                -- Reserva activa: Marco Díaz con Luis Salazar
                                                                                                ('d4000000-0000-0000-0000-000000000002',
                                                                                                 CURRENT_DATE + 2, '10:00', '11:00',
                                                                                                 'b2000000-0000-0000-0000-000000000002',
                                                                                                 'a1000000-0000-0000-0000-000000000001',
                                                                                                 'CREADA'),
                                                                                                -- Reserva activa: Sofía Ramírez con María Torres
                                                                                                ('d4000000-0000-0000-0000-000000000003',
                                                                                                 CURRENT_DATE + 3, '08:00', '09:00',
                                                                                                 'b2000000-0000-0000-0000-000000000003',
                                                                                                 'a1000000-0000-0000-0000-000000000002',
                                                                                                 'CREADA'),
                                                                                                -- Reserva cancelada: Carlos con Ana Paredes
                                                                                                ('d4000000-0000-0000-0000-000000000004',
                                                                                                 CURRENT_DATE - 5, '10:00', '11:00',
                                                                                                 'b2000000-0000-0000-0000-000000000004',
                                                                                                 'a1000000-0000-0000-0000-000000000004',
                                                                                                 'CANCELADA'),
                                                                                                -- Reserva completada: Ana Torres con Carlos Mendoza
                                                                                                ('d4000000-0000-0000-0000-000000000005',
                                                                                                 CURRENT_DATE - 2, '14:00', '15:00',
                                                                                                 'b2000000-0000-0000-0000-000000000001',
                                                                                                 'a1000000-0000-0000-0000-000000000003',
                                                                                                 'COMPLETADA');
