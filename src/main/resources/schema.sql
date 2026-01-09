-- DROP DATABASE IF EXISTS icinema_db;
-- CREATE DATABASE icinema_db;
-- USE icinema_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mobile_number VARCHAR(15)
);

-- Movies Table
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

-- Theatres Table
CREATE TABLE IF NOT EXISTS theatres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(500)
);

-- Reviews Table (FK to User and Movie)
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1000),
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Screens Table (Child of Theatre)
CREATE TABLE IF NOT EXISTS screens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_name VARCHAR(100) NOT NULL,
    total_seats INT,
    theatre_id BIGINT NOT NULL,
    CONSTRAINT fk_screen_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(id)
);

-- Seats Table (Child of Screen - Defines Layout)
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    row_name VARCHAR(10) NOT NULL, -- e.g., "A"
    seat_number INT NOT NULL,      -- e.g., 1
    seat_type VARCHAR(20) NOT NULL, -- 'SILVER', 'GOLD', 'PLATINUM'
    screen_id BIGINT NOT NULL,
    CONSTRAINT fk_seat_screen FOREIGN KEY (screen_id) REFERENCES screens(id)
);

-- Shows Table (Links Movie to Screen at a Time)
CREATE TABLE IF NOT EXISTS shows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price_silver DOUBLE NOT NULL,
    price_gold DOUBLE NOT NULL,
    price_platinum DOUBLE NOT NULL,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    CONSTRAINT fk_show_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_show_screen FOREIGN KEY (screen_id) REFERENCES screens(id)
);

-- Show Seats Table (Inventory for specific show)
CREATE TABLE IF NOT EXISTS show_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL, -- 'AVAILABLE', 'BOOKED', 'BLOCKED'
    price DOUBLE NOT NULL,
    show_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL, -- Points to physical seat definition
    CONSTRAINT fk_showseat_show FOREIGN KEY (show_id) REFERENCES shows(id),
    CONSTRAINT fk_showseat_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
);

-- Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_date DATETIME NOT NULL,
    total_amount DOUBLE NOT NULL,
    booking_status VARCHAR(20) NOT NULL, -- 'PENDING', 'CONFIRMED'
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_show FOREIGN KEY (show_id) REFERENCES shows(id)
);

-- Tickets Table (Links Booking to Specific Seats)
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    show_seat_id BIGINT NOT NULL,
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_ticket_showseat FOREIGN KEY (show_seat_id) REFERENCES show_seats(id)
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    amount_paid DOUBLE NOT NULL,
    payment_time DATETIME NOT NULL,
    payment_mode VARCHAR(20) NOT NULL, -- 'CREDIT_CARD', 'DEBIT_CARD'
    booking_id BIGINT NOT NULL UNIQUE, -- One payment per booking
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Dummy Data for Users
INSERT IGNORE INTO users (username, email, password, mobile_number) VALUES
('alice_w', 'alice@example.com', 'pass123', '1234567890'),
('bob_m', 'bob@example.com', 'pass123', '1234567891'),
('charlie_d', 'charlie@example.com', 'pass123', '1234567892'),
('david_s', 'david@example.com', 'pass123', '1234567893'),
('eve_a', 'eve@example.com', 'pass123', '1234567894'),
('frank_h', 'frank@example.com', 'pass123', '1234567895'),
('grace_k', 'grace@example.com', 'pass123', '1234567896'),
('heidi_v', 'heidi@example.com', 'pass123', '1234567897'),
('ivan_t', 'ivan@example.com', 'pass123', '1234567898'),
('judy_z', 'judy@example.com', 'pass123', '1234567899');

-- Dummy Data for Movies
INSERT IGNORE INTO movies (title, description, genre, release_date, duration_minutes, average_rating, language, image_url, censor_rating) VALUES
('Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'Sci-Fi', '2010-07-16', 148, 4.8, 'English', 'https://image.tmdb.org/t/p/original/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg', 'UA'),
('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 'Action', '2008-07-18', 152, 4.9, 'English', 'https://image.tmdb.org/t/p/original/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 'UA'),
('Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 'Sci-Fi', '2014-11-07', 169, 4.7, 'English', 'https://image.tmdb.org/t/p/original/gEU2QniL6E8ahMc996hKfb7KD87.jpg', 'UA'),
('Parasite', 'Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.', 'Thriller', '2019-05-30', 132, 4.6, 'Korean', 'https://image.tmdb.org/t/p/original/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', 'A'),
('Avengers: Endgame', 'After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos'' actions and restore balance to the universe.', 'Action', '2019-04-26', 181, 4.8, 'English', 'https://image.tmdb.org/t/p/original/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 'UA'),
('Spider-Man: No Way Home', 'Peter Parker is unmasked and no longer able to separate his normal life from the high-stakes of being a Super Hero. When he asks for help from Doctor Strange the stakes become even more dangerous, forcing him to discover what it truly means to be Spider-Man.', 'Action', '2021-12-17', 148, 4.7, 'English', 'https://image.tmdb.org/t/p/original/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg', 'UA'),
('The Lion King', 'Simba idolizes his father, King Mufasa, and takes to heart his own royal destiny. But not everyone in the kingdom celebrates the new cub''s arrival.', 'Animation', '2019-07-19', 118, 4.5, 'English', 'https://image.tmdb.org/t/p/original/dzBtMocZuJbjk0ptw8lKSauuf8.jpg', 'U'),
('Joker', 'During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure.', 'Drama', '2019-10-04', 122, 4.4, 'English', 'https://image.tmdb.org/t/p/original/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg', 'A'),
('Frozen II', 'Anna, Elsa, Kristoff, Olaf and Sven leave Arendelle to travel to an ancient, autumn-bound forest of an enchanted land.', 'Animation', '2019-11-22', 103, 4.3, 'English', 'https://image.tmdb.org/t/p/original/qdfARIhgpgUEXDdAfaBO1yrgSaW.jpg', 'U'),
('Toy Story 4', 'Woody has always been confident about his place in the world and that his priority is taking care of his kid, whether that''s Andy or Bonnie. But when Bonnie adds a reluctant new toy called "Forky" to her room, a road trip adventure alongside old and new friends will show Woody how big the world can be for a toy.', 'Animation', '2019-06-21', 100, 4.2, 'English', 'https://image.tmdb.org/t/p/original/w9kR8qbmQ01HwnvK4alvnQ2ca0L.jpg', 'U');

-- Dummy Data for Theatres
-- Dummy Data for Theatres
INSERT IGNORE INTO theatres (name, city, address) VALUES
('PVR Koramangala', 'Bangalore', 'The Forum Mall, Koramangala'),
('INOX Lido', 'Bangalore', 'Off MG Road, Ulsoor'),
('Cinepolis Seasons', 'Pune', 'Seasons Mall, Magarpatta City'),
('PVR Icon', 'Mumbai', 'Oberoi Mall, Goregaon East'),
('AMB Cinemas', 'Hyderabad', 'Gachibowli'),
('Prasads IMAX', 'Hyderabad', 'Necklace Road'),
('Sathyam Cinemas', 'Chennai', 'Royapettah'),
('Luxe Cinemas', 'Chennai', 'Phoenix Market City, Velachery'),
('PVR Plaza', 'Delhi', 'Connaught Place'),
('Carnival Cinemas', 'Mumbai', 'Andheri West');

/* 
   STOP HERE! 
   The following data should be generated via API calls to ensure Seats are auto-generated.
   1. Run the App.
   2. Call POST /api/screens (This will create Screen + Seats)
   3. Call POST /api/shows (This will create Show + ShowSeats)
*/

/*
-- Dummy Data for Screens
INSERT IGNORE INTO screens (screen_name, theatre_id, total_seats) VALUES
('Gold Class', 1, 50),
('Premiere', 1, 150),
('Audi 1', 2, 120),
('Audi 2', 2, 100),
('IMAX Screen', 3, 300),
('Screen 1', 3, 150),
('Large Screen', 4, 350),
('Club Class', 4, 100),
('Screen 1', 5, 250),
('Screen 2', 5, 200);

-- Dummy Data for Seats (Sample for Screen 1)
INSERT IGNORE INTO seats (seat_number, row_name, seat_type, screen_id) VALUES
(1, 'A', 'SILVER', 1), (2, 'A', 'SILVER', 1), (3, 'A', 'SILVER', 1), (4, 'A', 'SILVER', 1),
(1, 'B', 'GOLD', 1), (2, 'B', 'GOLD', 1);

-- Dummy Data for Shows
INSERT IGNORE INTO shows (movie_id, screen_id, show_date, show_time, price_silver, price_gold, price_platinum) VALUES
(1, 1, '2026-12-01', '10:00:00', 10.0, 15.0, 25.0),
(1, 1, '2026-12-01', '14:00:00', 12.0, 18.0, 30.0),
(2, 2, '2026-12-01', '11:00:00', 10.0, 15.0, 25.0),
(2, 2, '2026-12-01', '15:00:00', 12.0, 18.0, 30.0),
(3, 1, '2026-12-01', '18:00:00', 15.0, 25.0, 40.0);

-- Dummy Data for Show Seats (Sample for Show 1)
INSERT IGNORE INTO show_seats (status, price, show_id, seat_id) VALUES
('AVAILABLE', 10.0, 1, 1),
('AVAILABLE', 10.0, 1, 2),
('BOOKED', 10.0, 1, 3),
('BOOKED', 10.0, 1, 4),
('AVAILABLE', 20.0, 1, 5),
('AVAILABLE', 20.0, 1, 6);

-- Dummy Data for Bookings
INSERT IGNORE INTO bookings (booking_date, total_amount, booking_status, user_id, show_id) VALUES
('2026-11-30 10:00:00', 20.0, 'CONFIRMED', 1, 1),
('2026-11-30 11:00:00', 10.0, 'PENDING', 2, 1);

-- Dummy Data for Payments
INSERT IGNORE INTO payments (transaction_id, amount_paid, payment_time, payment_mode, booking_id) VALUES
('TXN123456789', 20.0, '2026-11-30 10:00:00', 'CREDIT_CARD', 1),
('TXN234567890', 10.0, '2026-11-30 11:00:00', 'DEBIT_CARD', 2);

-- Dummy Data for Tickets
INSERT IGNORE INTO tickets (booking_id, show_seat_id) VALUES
(1, 3), (1, 4);

*/


-- Dummy Data for Reviews (Safe to restore as Users and Movies are static)
INSERT IGNORE INTO reviews (user_id, movie_id, rating, comment) VALUES
(1, 1, 5, 'Great movie!'),
(2, 2, 4, 'Good movie!');
