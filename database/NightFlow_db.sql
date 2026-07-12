-- ══════════════════════════════
--  NIGHTFLOW DATABASE — SCHEMA
-- ══════════════════════════════

DROP SCHEMA IF EXISTS nightflowdb;
CREATE SCHEMA nightflowdb;
USE nightflowdb;

-- ══════════════════════════════
--  TABLES
-- ══════════════════════════════

CREATE TABLE nightflowdb.user (
    id INT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender ENUM('Uomo', 'Donna', 'Altro') NOT NULL,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CLIENT', 'ORGANIZER') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE nightflowdb.event (
    id INT AUTO_INCREMENT,
    organizer_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    date_time DATETIME NOT NULL,
    location VARCHAR(200) NOT NULL,
    club_name VARCHAR(100) NOT NULL,
    total_tickets INT NOT NULL,
    available_tickets INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'CANCELLED') DEFAULT 'ACTIVE',
    PRIMARY KEY (id),
    FOREIGN KEY (organizer_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE nightflowdb.booking (
    id INT AUTO_INCREMENT,
    client_id INT NOT NULL,
    event_id INT NOT NULL,
    ticket_type VARCHAR(50) NOT NULL,  -- es. 'Senza drink', 'Con drink', 'Tavolo VIP'
    price_paid DECIMAL(10,2) NOT NULL,
	payment_method VARCHAR(50) DEFAULT NULL,
    ticket_code VARCHAR(100) DEFAULT NULL,
    status ENUM('CONFIRMED', 'CANCELLED', 'EXPIRED') DEFAULT 'CONFIRMED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (client_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (event_id)
        REFERENCES event(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════
--  INDEX
-- ══════════════════════════════

CREATE INDEX idx_user_email ON nightflowdb.user (email);
CREATE INDEX idx_event_date ON nightflowdb.event (date_time);
CREATE INDEX idx_booking_client ON nightflowdb.booking (client_id);
CREATE INDEX idx_booking_event ON nightflowdb.booking (event_id);

-- ══════════════════════════════
--  STORED PROCEDURE
-- ══════════════════════════════

DELIMITER $$

DROP PROCEDURE IF EXISTS nightflowdb.login$$
CREATE PROCEDURE nightflowdb.login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255),
    OUT p_id INT,
    OUT p_name VARCHAR(100),
    OUT p_surname VARCHAR(100),
    OUT p_role VARCHAR(20)
)
BEGIN
    SELECT id, name, surname, role
    INTO p_id, p_name, p_surname, p_role
    FROM user
    WHERE email = p_email
      AND password = p_password;

    IF p_role IS NULL THEN
        SET p_role = 'NOT_FOUND';
    END IF;
END$$

DROP PROCEDURE IF EXISTS nightflowdb.book_ticket$$
CREATE PROCEDURE nightflowdb.book_ticket(
    IN  p_event_id INT,
    IN  p_qty INT,
    OUT p_success BOOLEAN
)
BEGIN
    UPDATE event
    SET available_tickets = available_tickets - p_qty
    WHERE id = p_event_id
      AND available_tickets >= p_qty;

    SET p_success = (ROW_COUNT() > 0);
END$$

DELIMITER ;

-- ══════════════════════════════
--  USERS MYSQL
-- ══════════════════════════════

DROP USER IF EXISTS 'nf_login'@'localhost';
CREATE USER 'nf_login'@'localhost' IDENTIFIED BY 'nf_login';
GRANT EXECUTE ON PROCEDURE nightflowdb.login TO 'nf_login'@'localhost';
GRANT SELECT, INSERT ON nightflowdb.user TO 'nf_login'@'localhost';

DROP USER IF EXISTS 'nf_cliente'@'localhost';
CREATE USER 'nf_cliente'@'localhost' IDENTIFIED BY 'nf_cliente';
GRANT EXECUTE ON PROCEDURE nightflowdb.login TO 'nf_cliente'@'localhost';
GRANT EXECUTE ON PROCEDURE nightflowdb.book_ticket TO 'nf_cliente'@'localhost';
GRANT SELECT ON nightflowdb.event TO 'nf_cliente'@'localhost';
GRANT SELECT ON nightflowdb.user TO 'nf_cliente'@'localhost';
GRANT SELECT, INSERT, UPDATE ON nightflowdb.booking TO 'nf_cliente'@'localhost';
GRANT UPDATE (email, password) ON nightflowdb.user TO 'nf_cliente'@'localhost';
GRANT UPDATE ON nightflowdb.event TO 'nf_cliente'@'localhost';

DROP USER IF EXISTS 'nf_organizzatore'@'localhost';
CREATE USER 'nf_organizzatore'@'localhost' IDENTIFIED BY 'nf_organizzatore';
GRANT EXECUTE ON PROCEDURE nightflowdb.login TO 'nf_organizzatore'@'localhost';
GRANT SELECT ON nightflowdb.user TO 'nf_organizzatore'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON nightflowdb.event TO 'nf_organizzatore'@'localhost';
GRANT SELECT ON nightflowdb.booking TO 'nf_organizzatore'@'localhost';
GRANT SELECT, UPDATE ON nightflowdb.booking TO 'nf_organizzatore'@'localhost';
GRANT UPDATE (email, password) ON nightflowdb.user TO 'nf_organizzatore'@'localhost';
FLUSH PRIVILEGES;

-- ══════════════════════════════
--  NIGHTFLOW — TEST DATA
--  Dati fittizi basati sui mock della GUI
-- ══════════════════════════════
USE nightflowdb;

-- ══════════════════════════════
--  USERS
--  password: 'password123' (hash fittizio per test)
-- ══════════════════════════════

INSERT INTO user (name, surname, dob, gender, country, city, email, password, role) VALUES
-- Clienti
('Anna',    'Bianchi', '2003-01-13', 'Donna', 'Italia', 'Roma',   'annabianchi@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CLIENT'),
('Martina', 'Maurizi', '2000-07-10', 'Donna', 'Italia', 'Roma',   'martinamaurizi30@gmail.com',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CLIENT'),
('Marco',   'Rossi',   '1999-05-22', 'Uomo',  'Italia', 'Milano', 'marco.rossi@email.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CLIENT'),
-- Organizzatori
('Luca', 'Messina',   '1985-03-14', 'Uomo',  'Italia', 'Roma',   'info@jolieclub.com',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ORGANIZER'),
('Sara', 'Cassino',    '1990-11-05', 'Donna', 'Italia', 'Roma',   'info@jerorestaurant.it','ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ORGANIZER');

-- ══════════════════════════════
--  EVENTS
-- ══════════════════════════════

INSERT INTO event (organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price) VALUES
-- Eventi Jolie Club (Organizer ID 4)
-- Evento 1
(4, 'Latin Night', 'Ospite speciale DJ Carl Cox Orario: 22:00 - 01:30', '2026-07-22 22:00:00', 'Via Velletri 13, Roma', 'Jolie Club', 100, 100, 20.00),
-- Evento 2
(4, 'Aperitivo in Terrazza', 'Buffet e spritz Orario: 19:00 - 21:30', '2026-07-23 19:00:00', 'Via del Santuario, Roma', 'Amazonia', 50, 50, 15.00),
-- Evento 3
(4, 'The sanctuary eco retreat techno', 'Ingresso in lista Orario: 22:00 - 00:30', '2026-07-06 22:00:00', 'Via delle Terme di Traiano', 'Sanctuary', 150, 150, 30.00),
-- Evento 4
(4, 'Dinner&Dance', 'Serata reggaeton e musica latina con DJ set esclusivo Orario: 21:00 - 00:30', '2026-07-22 21:00:00', 'Via Torrita Tiberina, 22, Roma, Italia', 'Jerò restaurant', 300, 250, 15.00),
-- Evento 5
(4, 'Fluo Party', 'Il party più luminoso della capitale. Dress code: Colori Fluo! Orario: 18:00 - 21:00', '2026-07-29 18:00:00', 'Via Tribale, 3, Roma, Italia', 'Satyrus', 300, 300, 20.00),
-- Evento 6
(4, 'Techno House', 'Searata soft! Orario: 18:00 - 21:00', '2026-07-29 18:00:00', 'Via Trovia, 3, Roma, Italia', 'The sanctuary eco retreat', 300, 300, 10.00),
-- Eventi Jerò Restaurant (Organizer ID 5)
-- Evento 7
(5, 'Dinner & Dance', 'Cena spettacolo con musica dal vivo e a seguire DJ set Orario: 21:00 - 00:30', '2026-07-06 21:00:00', 'Via Torrita Tiberina, 22, Roma, Italia', 'Jerò restaurant', 150, 80, 35.00),
-- Evento 8
(5, 'Indie Rock Live', 'Musica dal vivo. Orario: 22:00 - 00:30', '2026-07-22 22:00:00', 'Via Pietrasanta 16, Roma', 'Magazzini Generali', 200, 200, 25.00),
-- Evento 9
(5, 'Magazzini Generali', 'Serata Magazzini Orario: 22:00 - 01:30', '2026-07-02 23:00:00', 'Via Pietrasanta 16, Roma', 'Magazzini Generali', 200, 200, 20.00);

-- ══════════════════════════════
--  BOOKINGS
-- ══════════════════════════════
INSERT INTO booking (client_id, event_id, ticket_type, price_paid, payment_method, ticket_code, status, created_at) VALUES
-- Anna prenota Tavolo VIP al Latin Night (Paga con carta)
(1, 1, 'Tavolo VIP', 105.00, 'CREDIT_CARD', 'NF-TKT-A1B2C3', 'CONFIRMED', NOW() - INTERVAL 2 DAY),

-- 1. Martina: Tavolo VIP al Fluo Party (event_id = 5, base 20€ + 85€) - Paga con PayPal (FUTURO)
(2, 5, 'Tavolo VIP', 105.00, 'PAYPAL', 'NF-TKT-M1A2B3', 'CONFIRMED', NOW() - INTERVAL 1 DAY),

-- 2. Martina: Con drink al Dinner&Dance Jerò (event_id = 4, base 15€ + 5€) - Paga con Carta (FUTURO)
(2, 4, 'Con drink', 20.00, 'CREDIT_CARD', 'NF-TKT-M4C5D6', 'CONFIRMED', NOW() - INTERVAL 5 HOUR),

-- 3. Martina: Senza drink (Liste) all'Aperitivo in Terrazza (event_id = 2, base 15€) - Paga all'ingresso (FUTURO)
(2, 2, 'Senza drink', 15.00, 'PAY_ON_SITE', 'NF-TKT-M7E8F9', 'CONFIRMED', NOW() - INTERVAL 10 MINUTE),

-- 4. Martina: Con drink al The sanctuary (event_id = 3, data evento 06-Lug-2026) - EVENTO PASSATO
(2, 3, 'Con drink',  35.00,  'PAYPAL', 'NF-TKT-X7Y8Z9', 'CONFIRMED', NOW() - INTERVAL 1 DAY),
-- Marco aveva prenotato ma ha annullato (Niente pagamento)
(3, 1, 'Senza drink', 20.00, NULL, NULL, 'CANCELLED', NOW() - INTERVAL 5 DAY);