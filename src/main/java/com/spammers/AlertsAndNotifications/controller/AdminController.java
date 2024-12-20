package com.spammers.AlertsAndNotifications.controller;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.FineInputDTO;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.service.interfaces.AdminService;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/admin")
@CrossOrigin
public class AdminController {
    private final AdminService adminService;


    /**
     * Retrieves all active fines (with status PENDING) within a specific date
     * and returns them in a paginated response.
     * @param date the date to filter the fines
     * @param page the current page of the request.
     * @param size the size of the page requested.
     * @return The paginated response of fines.
     */
    @GetMapping("/fines/pending-by-date")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getPendingFinesByDate(@RequestParam LocalDate date,
                                                                     @RequestParam int page, @RequestParam int size){
        return adminService.returnAllActiveFinesBetweenDate(date, size, page);
    }

    /**
     * This method returns all active fines (with status PENDING)
     * in a paginated response.
     * @param page the current page of the request.
     * @param size the size of the page requested.
     * @return The paginated response of fines.
     */
    @GetMapping("/fines-pending")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getPendingFines(@RequestParam int page, @RequestParam int size){
        return adminService.returnAllActiveFines(page, size);
    }

    /**
     * This method allows the user to change the fines day rate.
     * @param newRate the new fines day rate.
     * @return Success Message confirmation
     */
    @PutMapping("/fines/{newRate}/rate")
    @ResponseStatus(HttpStatus.OK)
    public String setFinesDayRate(@PathVariable float newRate){
        adminService.setFinesRateDay(newRate);
        return "Fine updated Correctly";
    }

    /**
     * This method allows consult the fines day rate.
     * @return the fines day rate.
     */
    @GetMapping("/fines/rate")
    @ResponseStatus(HttpStatus.OK)
    public float getFinesDayRate(){
        return adminService.getFinesDayRate();
    }


    /**
     * This method sends a notification of a loan created.
     * @param loanDTO the information required to send the notification:
     *                (userId, bookId, email of the Parent, book name and the
     *                return date)
     * @return A message of successfully sent notification.
     */
    @PostMapping("/loan/create")
    @ResponseStatus(HttpStatus.OK)
    public String notifyLoan(@RequestBody LoanDTO loanDTO){
        adminService.notifyLoan(loanDTO);
        return "Notification Sent!";
    }

    /**
     * This method handles the creation of a return notification.
     * It sends a notification to the parent of the student when a book is returned,
     * indicating whether the book was returned in good or bad condition.
     *
     * @param bookId the ID of the book being returned.
     * @param returnedInBadCondition a flag indicating whether the book was returned in bad condition.
     * @return A message confirming that the book return notification was sent.
     * @throws SpammersPrivateExceptions if the loan record is not found for the given bookId.
     */
    @PostMapping("/loan/return")
    @ResponseStatus(HttpStatus.OK)
    public String returnBook(@RequestParam String bookId, @RequestParam boolean returnedInBadCondition) {
        adminService.returnBook(bookId, returnedInBadCondition);
        return "Book Returned";
    }

    /**
     * This method handles the creation of a fine for a given user.
     * It creates a fine based on the provided information in the request body.
     *
     * @param fineDTO The data transfer object (DTO) containing the information for the fine (description, amount, expired date, etc.).
     * @param userId The user ID for whom the fine is being created.
     * @return A message indicating that the fine has been successfully created.
     */
    @PostMapping("/users/{userId}/fines/create")
    @ResponseStatus(HttpStatus.OK)
    public String openFine(@RequestBody FineInputDTO fineDTO, @PathVariable String userId) {
        adminService.openFine(fineDTO);
        return "Fine Created";
    }

    /**
     * This method handles the closing of a fine for a given user.
     * It marks the fine as closed based on the provided fine ID.
     *
     * @param fineId The ID of the fine that is being closed.
     * @return A message indicating that the fine has been successfully closed.
     */
    @PutMapping("/users/fines/{fineId}/close")
    @ResponseStatus(HttpStatus.OK)
    public String closeFine(@PathVariable String fineId) {
        adminService.closeFine(fineId);
        return "Fine Closed";
    }


}
