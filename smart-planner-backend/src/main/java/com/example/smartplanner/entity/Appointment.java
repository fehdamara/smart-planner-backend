package com.example.smartplanner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "appointments")
@DiscriminatorValue("APPOINTMENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Appointment extends Item {

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    private String location;
    private String meetingUrl;
}
