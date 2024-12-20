package com.spammers.AlertsAndNotifications.model.dto;



import lombok.*;

import java.time.LocalDate;
// Esta clase para lo que deberia tener el prestamo que nos envia el modulo de gestion de prestamos
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class LoanDTO {
    private String userId;
    private String emailGuardian;
    private String bookId;
    private String bookName;
    private LocalDate loanReturn;
}
