package com.infy.icinema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "SILVER", "GOLD", "RECLINER"

    @Column
    private String description;

    @Column(name = "icon_url")
    private String iconUrl; // URL for the seat icon (e.g., standard chair vs recliner svg)
}
