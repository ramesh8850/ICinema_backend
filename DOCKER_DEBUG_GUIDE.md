# Docker & Backend Workflow Guide

## 1. How to KNOW which Database is connected?

There are two ways to verify this yourself:

### Method A: Check the Logs (The "Proof")
When your application starts, it prints the "Active Profile" and setup details. One of the best ways to be sure is to look at the logs.

Run this command:

**PowerShell (Default Windows Terminal):**
`docker compose logs app | Select-String "jdbc"`

**Git Bash (MINGW64) / Mac / Linux:**
`docker compose logs app | grep "jdbc"`

Look for lines mentioning `HikariPool` or `Driver`. While Spring Boot hides passwords, you can explicitly see the URL it is using in the `docker-compose.yml` file under `environment`.

**The Logic:**
- **In `docker-compose.yml`**, we set: `DB_URL: jdbc:mysql://mysqldb:3306/icinema_db`
- `mysqldb` is a **hostname** that *only exists inside Docker*.
- Your local computer does not know what "mysqldb" is.
- Therefore, if the app works at all, **it MUST be connected to the Docker database**. If it tried to connect to your local DB using that name, it would crash with "Unknown Host".

### Method B: The "Data Test" (The Physical Check)
1. **Create a "Marker" Record:**
   - Log in to the App (localhost:4200) and Book a ticker for "Inception" (or any movie).
2. **Check Local DB (Port 3306):**
   - run `mysql -u ... -P 3306 ...`
   - Select * from bookings.
   - **Result:** You will NOT see the booking.
3. **Check Docker DB (Port 3307):**
   - run `mysql -u ... -P 3307 ...`
   - Select * from bookings.
   - **Result:** You WILL see the booking.

---

## 2. Future Backend Modifications: Pitfalls & Solutions

When you modify Java code or Database schemas in the future, Docker behaves differently than running locally in Eclipse/IntelliJ.

### Scenario A: "I changed the Java code, but the App didn't update!"
*   **The Issue:** You changed `BookingService.java`, saved it, and refreshed the browser. Nothing changed.
*   **The Reason:** Docker is running a **packaged JAR file** (a snapshot of your code from 20 minutes ago). It does not "see" your live code changes in the IDE.
*   **The Fix:** You must rebuild the image.
    1.  `mvn clean package -DskipTests` (Create new JAR)
    2.  `docker compose up -d --build` (Tell Docker to recreate the container with the new JAR).

### Scenario B: "I added a new Table/Column, but it's crashing!"
*   **The Issue:** You added `private String phoneNumber;` to `User.java`. The app starts but crashes when you save a user.
*   **The Reason:** The Database inside the container still has the **Old Structure** (without the phone column).
*   **The Fix:**
    *   **Auto-Update:** We set `spring.jpa.hibernate.ddl-auto=update`. This usually handles it safely.
    *   **Nuclear Option (Reset):** If your database gets really messed up, you can delete the container's volume to start fresh (WARNING: DELETES ALL DATA):
        `docker compose down -v`
        `docker compose up --build`

### Scenario C: "I added a dependency in pom.xml, getting ClassNotFound!"
*   **The Issue:** Added a library (like PDF generator). Code compiles, but Docker app crashes saying "Class Not Found".
*   **The Reason:** Docker layers are cached. It might be re-using an old "Dependency Layer" that doesn't have your new library.
*   **The Fix:** Force a rebuild of the dependency layer.
    `docker compose build --no-cache`
