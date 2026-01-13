DROP DATABASE IF EXISTS icinema_db;
CREATE DATABASE icinema_db;
USE icinema_db;

-- 1. Users Table (Core)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mobile_number VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE -- 'ROLE_USER', 'ROLE_ADMIN'
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 2. Master Data: Movies & Theatres
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    language VARCHAR(50),
    description VARCHAR(1000),
    image_url VARCHAR(500),
    release_date DATE,
    duration_minutes INT,
    censor_rating VARCHAR(10),
    average_rating DOUBLE DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS theatres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(500)
);

-- 3. Screens Table
CREATE TABLE IF NOT EXISTS screens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_name VARCHAR(100) NOT NULL,
    total_seats INT,
    theatre_id BIGINT NOT NULL,
    CONSTRAINT fk_screen_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(id)
);

-- 4. NEW: Seat Types (Dynamic Categories)
CREATE TABLE IF NOT EXISTS seat_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,  -- 'SILVER', 'GOLD', 'RECLINER'
    description VARCHAR(255),
    icon_url VARCHAR(500)
);

-- 5. Seats Table (Refactored: Uses seat_type_id)
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    row_name VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    screen_id BIGINT NOT NULL,
    seat_type_id BIGINT NOT NULL,      -- Link to Dynamic Type
    CONSTRAINT fk_seat_screen FOREIGN KEY (screen_id) REFERENCES screens(id),
    CONSTRAINT fk_seat_type FOREIGN KEY (seat_type_id) REFERENCES seat_types(id)
);

-- 6. Shows Table (Refactored: Removed Prices)
CREATE TABLE IF NOT EXISTS shows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_show_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_show_screen FOREIGN KEY (screen_id) REFERENCES screens(id)
);

-- 7. NEW: Show Seat Prices (Dynamic Pricing Engine)
CREATE TABLE IF NOT EXISTS show_seat_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_type_id BIGINT NOT NULL,
    price DOUBLE NOT NULL,
    CONSTRAINT fk_ssp_show FOREIGN KEY (show_id) REFERENCES shows(id),
    CONSTRAINT fk_ssp_type FOREIGN KEY (seat_type_id) REFERENCES seat_types(id),
    UNIQUE (show_id, seat_type_id) -- One price per type per show
);

-- 8. Show Seats (Refactored: Sparse Storage - Only for Exceptions)
CREATE TABLE IF NOT EXISTS show_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL, -- 'BOOKED', 'BLOCKED'
    price DOUBLE NOT NULL,       -- Snapshot of price at booking time
    show_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_showseat_show FOREIGN KEY (show_id) REFERENCES shows(id),
    CONSTRAINT fk_showseat_seat FOREIGN KEY (seat_id) REFERENCES seats(id),
    UNIQUE (show_id, seat_id) -- Prevent Double Booking
);

-- 9. Bookings & Tickets
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_date DATETIME NOT NULL,
    total_amount DOUBLE NOT NULL,
    booking_status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    created_date DATETIME(6),
    last_modified_date DATETIME(6),
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_show FOREIGN KEY (show_id) REFERENCES shows(id)
);

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    show_seat_id BIGINT NOT NULL,
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_ticket_showseat FOREIGN KEY (show_seat_id) REFERENCES show_seats(id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    amount_paid DOUBLE NOT NULL,
    payment_time DATETIME NOT NULL,
    payment_mode VARCHAR(20) NOT NULL,
    booking_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1000),
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id)
);


-- ================= SEED DATA =================

-- 1. Users
INSERT IGNORE INTO users (username, email, password, mobile_number) VALUES
('alice_w', 'alice@example.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLa', '1234567890'),
('bob_m', 'bob@example.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLa', '1234567891'),
('admin', 'admin@icinema.com', '$2a$10$jbZpquE8LJ2xQTX.3YwWyOH35PWgdPIoi/JSuJamPn6bS9/x9KcYa', '0000000000');

INSERT IGNORE INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_ADMIN');

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- alice is USER
(2, 1), -- bob is USER
(3, 2); -- admin is ADMIN

-- 2. Movies
INSERT IGNORE INTO movies (title, description, genre, release_date, duration_minutes, average_rating, language, image_url, censor_rating) VALUES
('Inception', 'Dream theft sci-fi.', 'Sci-Fi', '2010-07-16', 148, 4.8, 'English', 'https://image.tmdb.org/t/p/original/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg', 'UA'),
('The Dark Knight', 'Batman vs Joker.', 'Action', '2008-07-18', 152, 4.9, 'English', 'https://image.tmdb.org/t/p/original/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 'UA');

-- 3. Theatres
INSERT IGNORE INTO theatres (name, city, address) VALUES
('PVR Koramangala', 'Bangalore', 'Forum Mall'),
('INOX Lido', 'Bangalore', 'MG Road');

-- 4. Seat Types (CRITICAL NEW STEP)
INSERT IGNORE INTO seat_types (name, description) VALUES
('SILVER', 'Standard seating, front rows'),
('GOLD', 'Premium view, middle rows'),
('PLATINUM', 'Recliners, back rows');

-- 5. Screens
INSERT IGNORE INTO screens (screen_name, theatre_id, total_seats) VALUES
('Screen 1', 1, 100),
('Screen 2', 2, 120);

-- 6. Seats (Linked to Types)
-- Screen 1: Rows A-C are SILVER (ID=1), D-H are GOLD (ID=2)
INSERT IGNORE INTO seats (row_name, seat_number, screen_id, seat_type_id) VALUES
('A', 1, 1, 1), ('A', 2, 1, 1), ('B', 1, 1, 1), ('B', 2, 1, 1), -- Silver
('D', 1, 1, 2), ('D', 2, 1, 2), ('E', 1, 1, 2), ('E', 2, 1, 2); -- Gold

-- 7. Shows (No Prices here anymore!)
INSERT IGNORE INTO shows (movie_id, screen_id, show_date, show_time) VALUES
(1, 1, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '18:00:00'),
(2, 2, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), '20:00:00');

-- 8. Show Seat Prices (The Pricing Engine)
-- Show 1: Silver=150, Gold=250, Platinum=400
INSERT IGNORE INTO show_seat_prices (show_id, seat_type_id, price) VALUES
(1, 1, 150.0), -- Show 1, Type Silver
(1, 2, 250.0), -- Show 1, Type Gold
(1, 3, 400.0); -- Show 1, Type Platinum

-- 9. Bookings (Confirmed)
INSERT IGNORE INTO bookings (booking_date, total_amount, booking_status, user_id, show_id) VALUES
(NOW(), 150.0, 'CONFIRMED', 1, 1);

-- 10. Show Seats (ONLY for the booked seat)
INSERT IGNORE INTO show_seats (status, price, show_id, seat_id) VALUES
('BOOKED', 150.0, 1, 1); -- Seat A1 is booked

-- 11. Tickets
INSERT IGNORE INTO tickets (booking_id, show_seat_id) VALUES
(1, 1);
