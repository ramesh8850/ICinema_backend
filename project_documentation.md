# iCinema Project Documentation

This document provides a comprehensive overview of the backend data model (Entities) and the available REST API endpoints.

## 1. Data Model (Entities)

The application uses the following entities to model the cinema booking system:

### Core Entities
*   **`Movie`**: Represents a film. Contains details like title, genre, language, censor rating, and duration.
*   **`User`**: Represents a registered customer. Stores credentials, email, and mobile number.
*   **`Theatre`**: Represents a cinema complex (e.g., "PVR Koramangala").
*   **`Screen`**: Represents a specific auditorium within a theatre (e.g., "Screen 1").
*   **`Seat`**: Defines the physical layout of a screen (Row + Number + Type).

### Scheduling & Inventory
*   **`Show`**: A specific instance of a Movie playing on a Screen at a particular Date and Time.
*   **`ShowSeat`**: The inventory for a specific Show. It links a physical `Seat` to a `Show` and tracks its status (`AVAILABLE`, `BOOKED`, `BLOCKED`) and price.

### Transactional Entities
*   **`Booking`**: A user's reservation. Tracks the total amount, status (`PENDING`/`CONFIRMED`), and links the User to the Show.
*   **`Ticket`**: Represents an individual seat within a booking. Links a `Booking` to a specific `ShowSeat`.
*   **`Payment`**: Records the financial transaction. Stores the `amountPaid`, `paymentMode`, and a system-generated `transactionId`.

### Social
*   **`Review`**: User ratings and comments for a movie.

---

## 2. API Endpoints

### Authentication (`UserController`)
*   **Register User**
    *   `POST /api/users/register`
    *   **Body:** `{ "username": "...", "password": "...", "email": "...", "mobileNumber": "..." }`
*   **Login**
    *   `POST /api/users/login`
    *   **Body:** `{ "email": "...", "password": "..." }`

### Movies (`MovieController`)
*   **Get All Movies**
    *   `GET /api/movies`
*   **Get Movie by ID**
    *   `GET /api/movies/{id}`
*   **Search Movies** (Global Search)
    *   `GET /api/movies/search?query=Action`
*   **Filter Movies** (Structured Filter)
    *   `GET /api/movies/filter?genre=Action&language=English`
*   **Add Movie** (Admin)
    *   `POST /api/movies`

### Shows & Layout (`ShowController`)
*   **Get Shows for a Movie**
    *   `GET /api/shows?movieId={id}`
*   **Get Seat Layout**
    *   `GET /api/shows/{showId}/seats`
    *   **Response:** List of seats with status (`AVAILABLE`/`BOOKED`), row, number, and price.
*   **Add Show** (Admin)
    *   `POST /api/shows`

### Booking (`BookingController`)
*   **Create Booking**
    *   `POST /api/bookings`
    *   **Body:** `{ "userId": 1, "showId": 1, "showSeatIds": [1, 2] }`
    *   *Note: Backend calculates the total cost automatically.*
*   **Get User Bookings**
    *   `GET /api/bookings/user/{userId}`
*   **Get Ticket Details**
    *   `GET /api/bookings/{bookingId}/ticket`
    *   **Response:** Full summary (Movie, Theatre, Seats, Transaction ID, Price Breakdown).

### Payments (`PaymentController`)
*   **Make Payment**
    *   `POST /api/payments`
    *   **Body:** `{ "bookingId": 1, "amountPaid": 300.0, "paymentMode": "CREDIT_CARD" }`
    *   *Note: Backend auto-generates a unique Transaction ID.*

### Reviews (`ReviewController`)
*   **Add Review**
    *   `POST /api/reviews`
*   **Get Movie Reviews**
    *   `GET /api/reviews/movie/{movieId}`
