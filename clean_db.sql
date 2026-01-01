-- Disable foreign key checks to allow truncation/deletion in any order (safe method)
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Delete Transactional Data (Children)
-- Use DELETE instead of TRUNCATE to handle Foreign Keys gracefully
DELETE FROM tickets;
DELETE FROM payments;
DELETE FROM reviews;

-- 2. Delete Bookings (Parents of tickets/payments)
DELETE FROM bookings;

-- 3. Delete Users (Parents of bookings/reviews)
DELETE FROM users;

-- 4. Reset Seat Status (Very Important!)
-- Sets all seats back to 'AVAILABLE' so they can be booked again.
UPDATE show_seats SET status = 'AVAILABLE';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- CONFIRMATION
SELECT 'Data Cleanup Completed Successfully' AS Status;
