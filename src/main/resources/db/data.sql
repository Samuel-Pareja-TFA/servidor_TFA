-- Ejemplos Roles
INSERT INTO roles (name) VALUES ('Administrador'), ('Usuario');

-- Ejemplos Usuarios
INSERT INTO users (username, email, password, description, create_date, role_id) VALUES
('admin', 'admin@example.com', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'Administrador del sistema', '2025-01-01', 1),
('juan01', 'juan@example.com', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 'Hola, soy Juan', '2025-01-02', 2),
('maria2025', 'maria@example.com', 'cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc', 'Amante de los viajes', '2025-01-03', 2);

-- Ejemplos Publicaciones
INSERT INTO publications (user_id, text, create_date, update_date) VALUES
(2, 'Mi primera publicación!', '2025-01-02 10:30:00', '2025-01-02 10:45:00'),
(3, 'Fotos de mis vacaciones', '2025-01-03 18:00:00', NULL),
(2, 'Aprendiendo SQL 🚀', '2025-01-05 09:15:00', '2025-01-05 09:20:00');

-- Ejemplos Seguidores
INSERT INTO users_follow_users (user_who_follows_id, user_to_follow_id) VALUES
(2, 3),
(3, 2),
(3, 1);
