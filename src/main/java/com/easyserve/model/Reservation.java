package com.easyserve.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_restaurant", columnList = "restaurant_id"),
        @Index(name = "idx_reservation_customer", columnList = "customer_id"),
        @Index(name = "idx_reservation_date_time", columnList = "reservationDate, reservationTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Column(nullable = false)
    private LocalDate reservationDate;

    @NotNull
    @Column(nullable = false)
    private LocalTime reservationTime;

    @Min(1)
    @Column(nullable = false)
    private int partySize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CONFIRMED;

    @Column(length = 1000)
    private String specialRequests;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source = Source.ONLINE;

    private Integer tableNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Status {
        CONFIRMED, SEATED, COMPLETED, CANCELLED, NO_SHOW
    }

    public enum Source {
        ONLINE, PHONE, WALK_IN
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" + reservationDate +
                ", time=" + reservationTime +
                ", partySize=" + partySize +
                ", status=" + status +
                ", source=" + source +
                ", tableNumber=" + tableNumber +
                '}';
    }
}
