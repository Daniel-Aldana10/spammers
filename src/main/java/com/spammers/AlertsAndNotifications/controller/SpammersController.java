package com.spammers.AlertsAndNotifications.controller;


import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class SpammersController {

    private final NotificationService notificationService;

    /**
     * This method returns the notifications of a given user.
     *
     * @param userId The user ID.
     * @param page   The page number for pagination (zero-based index).
     * @param size   The number of items per page.
     * @return A map containing the notifications associated with the user.
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<NotificationDTO> getNotifications(
            @PathVariable("userId") String userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getNotifications(userId, page, size);
    }

    /**
     * This method returns the fines of a given user.
     * @param userId The user id
     * @return the fines of the user.
     */
    @GetMapping("/users/{userId}/fines")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponseDTO<FineOutputDTO> getFines(
            @PathVariable("userId") String userId,
            @RequestParam int page,
            @RequestParam int size) {
        return notificationService.getFinesByUserId(userId, page, size);
    }

    /**
     * This method sends a notification of a loan created.
     * @param loanDTO the information required to send the notification:
     *                (userId, bookId, email of the Parent, book name and the
     *                return date)
     * @return A message of successfully sent notification.
     */
    @PostMapping("/notify-create-loan")
    @ResponseStatus(HttpStatus.OK)
    public String notifyLoan(@RequestBody LoanDTO loanDTO){
        notificationService.notifyLoan(loanDTO);
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
    @PostMapping("/notify-return-loan")
    @ResponseStatus(HttpStatus.OK)
    public String returnBook(@RequestParam String bookId, @RequestParam boolean returnedInBadCondition) {
        notificationService.returnBook(bookId, returnedInBadCondition);
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
        notificationService.openFine(fineDTO);
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
        notificationService.closeFine(fineId);
        return "Fine Closed";
    }

    /**
     * Marks a notification as seen.
     * <p>
     * This method calls the service layer to mark the notification with the given ID as seen.
     * <p>
     * @param notificationId the ID of the notification to be marked as seen.
     * @return the number of rows that has been actualized.
     */
    @PutMapping("/mark-seen/{notificationId}")
    public int markNotificationAsSeen(@PathVariable String notificationId) {
        return notificationService.markNotificationAsSeen(notificationId);
    }

    /**
     * Retrieves the number of notifications that have not been seen by a specific user.
     * <p>
     * This method retrieves the number of notifications that have not been seen for a user
     * by interacting with the service layer.
     * <p>
     * @param userId the ID of the user whose unseen notifications are to be counted.
     * @return a ResponseEntity containing a ResponseMessage object with the result and the count of unseen notifications
     * and the count of active fines.
     */
    @GetMapping("/count/{userId}")
    public UserNotificationsInformationDTO getNumberNotificationsNotSeenByUser(@PathVariable String userId) {
        return notificationService.getNumberNotificationsNotSeenByUser(userId);
    }
}