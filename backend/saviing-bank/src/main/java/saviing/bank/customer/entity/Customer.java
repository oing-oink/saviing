package saviing.bank.customer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import saviing.bank.common.enums.OAuth2Provider;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pin")
    private String pin;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider")
    private OAuth2Provider oauth2Provider;

    @Column(name = "oauth2_id")
    private String oauth2Id;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Customer(String name, String pin, OAuth2Provider oauth2Provider, String oauth2Id) {
        this.name = name;
        this.pin = pin;
        this.oauth2Provider = oauth2Provider;
        this.oauth2Id = oauth2Id;
    }

    public void updatePin(String pin) {
        this.pin = pin;
    }
}