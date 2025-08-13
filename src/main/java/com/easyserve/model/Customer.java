
package com.easyserve.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email"),
        @Index(name = "idx_customer_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(length = 1000)
    private String preferences;

    @Column(nullable = false)
    private boolean marketingOptIn = false;

    @Column(nullable = false)
    private int totalOrders = 0;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name=" + firstName + " " + lastName +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", marketingOptIn=" + marketingOptIn +
                ", totalOrders=" + totalOrders +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
