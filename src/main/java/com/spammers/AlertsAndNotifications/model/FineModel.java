package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Fines")
@RequiredArgsConstructor
@Getter
public class FineModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fineId;

    @ManyToOne
    @JoinColumn(name="loanId", nullable=false)
    private LoanModel loan;

    @Column(name="description", nullable=false, length=300)
    private String description;

    @Column(name="amount", nullable = false)
    private float amount;

    @Column(name="expiredDate", nullable = false)
    private LocalDate expiredDate;

    @Column(name="fineStatus", nullable = false)
    private FineStatus fineStatus;

    @Column(name="fineType", nullable = false)
    private FineType fineType;
}
