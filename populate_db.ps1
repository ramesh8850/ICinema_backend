$baseUrl = "http://localhost:8080/api"

# Configuration: Screens and their corresponding Shows
$configs = @(
    # 1. PVR Koramangala - Gold Class
    @{
        TheatreId = 1
        ScreenName = "Gold Class"
        TotalSeats = 60
        Shows = @(
            @{ MovieId = 1; Date = "2026-01-01"; Time = "10:00:00"; PriceS = 150; PriceG = 250; PriceP = 350 }, # Inception
            @{ MovieId = 2; Date = "2026-01-01"; Time = "14:00:00"; PriceS = 150; PriceG = 250; PriceP = 350 }  # The Dark Knight
        )
    },
    # 2. INOX Lido - Audi 1
    @{
        TheatreId = 2
        ScreenName = "Audi 1"
        TotalSeats = 100
        Shows = @(
            @{ MovieId = 3; Date = "2026-01-01"; Time = "18:00:00"; PriceS = 120; PriceG = 200; PriceP = 300 }, # Interstellar
            @{ MovieId = 4; Date = "2026-01-02"; Time = "21:00:00"; PriceS = 120; PriceG = 200; PriceP = 300 }  # Parasite
        )
    },
    # 3. Cinepolis Seasons - IMAX Screen
    @{
        TheatreId = 3
        ScreenName = "IMAX Screen"
        TotalSeats = 150
        Shows = @(
            @{ MovieId = 5; Date = "2026-01-02"; Time = "11:00:00"; PriceS = 250; PriceG = 400; PriceP = 600 }, # Avengers Endgame
            @{ MovieId = 6; Date = "2026-01-02"; Time = "15:00:00"; PriceS = 250; PriceG = 400; PriceP = 600 }  # Spider-Man
        )
    },
    # 4. PVR Icon - Large Screen
    @{
        TheatreId = 4
        ScreenName = "Large Screen"
        TotalSeats = 120
        Shows = @(
            @{ MovieId = 1; Date = "2026-01-03"; Time = "16:00:00"; PriceS = 180; PriceG = 280; PriceP = 380 }, # Inception
            @{ MovieId = 7; Date = "2026-01-03"; Time = "20:00:00"; PriceS = 180; PriceG = 280; PriceP = 380 }  # Lion King
        )
    },
    # 5. AMB Cinemas - Screen 1
    @{
        TheatreId = 5
        ScreenName = "Screen 1"
        TotalSeats = 200
        Shows = @(
            @{ MovieId = 8; Date = "2026-01-03"; Time = "12:00:00"; PriceS = 150; PriceG = 250; PriceP = 350 }, # Joker
            @{ MovieId = 5; Date = "2026-01-03"; Time = "19:00:00"; PriceS = 150; PriceG = 250; PriceP = 350 }  # Avengers Endgame
        )
    },
    # 6. Prasads IMAX - Main Hall
    @{
        TheatreId = 6
        ScreenName = "Main Hall"
        TotalSeats = 250
        Shows = @(
            @{ MovieId = 3; Date = "2026-01-04"; Time = "09:00:00"; PriceS = 200; PriceG = 300; PriceP = 500 }, # Interstellar
            @{ MovieId = 9; Date = "2026-01-04"; Time = "13:00:00"; PriceS = 150; PriceG = 250; PriceP = 350 }  # Frozen II
        )
    },
    # 7. Sathyam Cinemas - RGB Laser
    @{
        TheatreId = 7
        ScreenName = "RGB Laser"
        TotalSeats = 180
        Shows = @(
            @{ MovieId = 10; Date = "2026-01-04"; Time = "17:00:00"; PriceS = 160; PriceG = 260; PriceP = 360 }, # Toy Story 4
            @{ MovieId = 2; Date = "2026-01-04"; Time = "21:00:00"; PriceS = 160; PriceG = 260; PriceP = 360 }  # The Dark Knight
        )
    }
)

Write-Host "Starting Data Population..." -ForegroundColor Cyan

foreach ($config in $configs) {
    try {
        # 1. Create Screen
        $screenBody = @{
            screenName = $config.ScreenName
            totalSeats = $config.TotalSeats
            theatreId = $config.TheatreId
        } | ConvertTo-Json

        $screenResponse = Invoke-RestMethod -Uri "$baseUrl/screens" -Method Post -Body $screenBody -ContentType "application/json"
        
        # Extract Screen ID from response (Handling wrapped response structure)
        # Assuming response structure: { message: "...", status: ..., data: { id: ... } }
        $screenId = $screenResponse.data.id

        if (-not $screenId) {
            Write-Error "Failed to get Screen ID for $($config.ScreenName)"
            continue
        }

        Write-Host "Created Screen: '$($config.ScreenName)' (ID: $screenId)" -ForegroundColor Green

        # 2. Create Shows linked to this Screen
        foreach ($show in $config.Shows) {
            $showBody = @{
                showDate = $show.Date
                showTime = $show.Time
                priceSilver = $show.PriceS
                priceGold = $show.PriceG
                pricePlatinum = $show.PriceP
                movieId = $show.MovieId
                screenId = $screenId
            } | ConvertTo-Json
            
            try {
                Invoke-RestMethod -Uri "$baseUrl/shows" -Method Post -Body $showBody -ContentType "application/json"
                Write-Host "  -> Created Show for Movie $($show.MovieId) on $show.Date at $show.Time" -ForegroundColor Gray
            } catch {
                Write-Error "Failed to create show for Movie $($show.MovieId): $_"
            }
        }
    } catch {
        Write-Error "Failed to create screen $($config.ScreenName): $_"
    }
}

Write-Host "Data Population Completed!" -ForegroundColor Cyan
