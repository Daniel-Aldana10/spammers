package com.spammers.AlertsAndNotifications.repository;

import com.spammers.AlertsAndNotifications.model.LoanModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Transactional
@Repository
public interface LoanRepository extends JpaRepository<LoanModel, String> {

    /**
     * Finds all loans that have expired before the given current date.
     *
     * This method retrieves a list of loans whose expiration date is earlier than the specified current date.
     * The result is pageable, allowing for pagination of the loans that meet the criteria.
     *
     * @param currentDate The current date to compare against the loan expiration dates.
     * @param pageable    The pagination details, such as page number and size.
     * @return A list of LoanModel objects that have expired before the given current date.
     */
    @Query("SELECT l FROM LoanModel l WHERE l.loanExpired < :currentDate AND l.bookReturned = false AND l.status = true")
    List<LoanModel> findExpiredLoans(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    /**
     * Finds all loans that are expiring exactly on the specified date.
     *
     * This method retrieves a list of loans that expire on a specific date.
     * It uses the `FUNCTION` keyword to extract only the date part of the loan's expiration date for comparison.
     *
     * @param dateInThreeDays The date to compare against the loan expiration dates.
     * @return A list of LoanModel objects that are expiring on the specified date.
     */
    @Query("SELECT l FROM LoanModel l WHERE FUNCTION('DATE', l.loanExpired) = :dateInThreeDays AND l.bookReturned = false")
    List<LoanModel> findLoansExpiringInExactlyNDays(@Param("dateInThreeDays") LocalDate dateInThreeDays,Pageable pageable);

    /**
     * Finds a loan by its loan ID.
     *
     * This method retrieves a loan based on its unique loan ID.
     * The method returns an Optional, which may or may not contain the loan if it exists.
     *
     * @param loanId The unique ID of the loan.
     * @return An Optional containing the LoanModel object if found, otherwise an empty Optional.
     */
    Optional<LoanModel> findByLoanId(String loanId);
    
    @Query("SELECT l FROM LoanModel l WHERE l.userId = :givenUserId AND l.bookId = :givenBookId")
    Optional<LoanModel> findLoanByUserAndBookId(@Param("givenUserId") String givenUserId, @Param("givenBookId") String givenBookId);
  
    @Query("SELECT l FROM LoanModel l WHERE l.bookId = :givenBookId AND l.bookReturned = :bookReturned")
    Optional<LoanModel> findLoanByBookIdAndBookReturned(@Param("givenBookId") String givenBookId, @Param("bookReturned") boolean bookReturned);

    @Query("SELECT l FROM LoanModel l WHERE l.userId = :givenUserId")
    Page<LoanModel> findByUserId(@Param("givenUserId") String givenUserId, Pageable pageable);

    @Query("SELECT l FROM LoanModel l  WHERE l.userId = :givenUserId AND l.bookId = :givenBookId ORDER BY l.loanDate DESC LIMIT 1")
    Optional<LoanModel> findLastLoan(@Param("givenBookId") String givenBookId, @Param("givenUserId") String givenUserId);
}
