package com.spammers.AlertsAndNotifications.service.implementations;
import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.*;
import com.spammers.AlertsAndNotifications.model.dto.*;
import com.spammers.AlertsAndNotifications.model.enums.*;
import com.spammers.AlertsAndNotifications.repository.FinesRepository;
import com.spammers.AlertsAndNotifications.repository.LoanRepository;
import com.spammers.AlertsAndNotifications.repository.NotificationRepository;
import com.spammers.AlertsAndNotifications.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private FinesRepository finesRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private LoanDTO loanDTO;
    private UserInfo userInfo;
    private LoanModel loanModel;

    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO();
        loanDTO.setUserId("user123");
        loanDTO.setBookId("book456");
        loanDTO.setBookName("Test Book");
        loanDTO.setEmailGuardian("guardian@email.com");
        loanDTO.setLoanReturn(LocalDate.now().plusDays(14));

        userInfo = new UserInfo();
        userInfo.setName("Test User");
        userInfo.setGuardianEmail("guardian@email.com");

        loanModel = new LoanModel();
        loanModel.setUserId("user123");
        loanModel.setBookId("book456");
        loanModel.setLoanDate(LocalDate.now());
        loanModel.setBookName("Test Book");
        loanModel.setLoanExpired(LocalDate.now().plusDays(14));
        loanModel.setBookReturned(false);
    }


    @Test
    void testGetNotifications_Success() {
        // Arrange
        String userId = "user123";
        int pageNumber = 0;
        int pageSize = 10;

        List<NotificationModel> notifications = new ArrayList<>();
        NotificationModel notificationModel = new NotificationModel(userId,"example@mail.com",
                LocalDate.now().minusDays(2),NotificationType.FINE,false,"Boulevard");
        notifications.add(notificationModel);
        PageImpl<NotificationModel> page = new PageImpl<>(notifications);
        when(notificationRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        // Act
        PaginatedResponseDTO<NotificationDTO> result = notificationService.getNotifications(userId, pageNumber, pageSize);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("Boulevard", result.getData().get(0).getBookName());

        verify(notificationRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void testGetNotifications_NoNotifications() {
        // Arrange
        String userId = "user123";
        int pageNumber = 0;
        int pageSize = 10;

        PageImpl<NotificationModel> emptyPage = new PageImpl<>(Collections.emptyList());

        when(notificationRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        PaginatedResponseDTO<NotificationDTO> result = notificationService.getNotifications(userId, pageNumber, pageSize);

        // Assert
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(notificationRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void testMarkNotificationAsSeen_Success(){
        String userId = "user123";
        when(notificationRepository.markNotificationAsSeen(userId)).thenReturn(1);
        notificationService.markNotificationAsSeen(userId);
        verify(notificationRepository).markNotificationAsSeen(userId);
    }

    @Test
    void testMarkNotificationAsSeen_Failure(){
        String userId = "user123";
        when(notificationRepository.markNotificationAsSeen(userId)).thenReturn(0);
        notificationService.markNotificationAsSeen(userId);
        verify(notificationRepository).markNotificationAsSeen(userId);
    }




    @Test
    void testGetFines_Success() {
        // Arrange
        String userId = "user123";
        int size = 15;
        int pageNumber = 0;

        List<FineModel> fines = new ArrayList<>();
        FineModel fineModel = new FineModel();
        fineModel.setLoan(loanModel);
        fineModel.setFineId("fine123");
        fineModel.setAmount(50.0f);
        fineModel.setDescription("Test Fine");
        fines.add(fineModel);

        PageImpl<FineModel> page = new PageImpl<>(fines);

        when(finesRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);
        // Act
        PaginatedResponseDTO<FineOutputDTO> result = notificationService.getFinesByUserId(userId, pageNumber, size);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        verify(finesRepository).findByUserId(eq(userId), any(Pageable.class));
    }



    @Test
    void testGetNumberNotificationsNotSeenByUser_WithNotifications() {
        // Arrange
        String userId = "user123";
        Long expectedNotifications = 3L;
        Long expectedActiveFines = 2L;

        when(notificationRepository.getNumberNotificationsNotSeenByUser(userId, false))
                .thenReturn(expectedNotifications);

        when(finesRepository.getNumberActiveFinesByUser(userId, FineStatus.PENDING))
                .thenReturn(expectedActiveFines);

        // Act
        UserNotificationsInformationDTO result =
                notificationService.getNumberNotificationsNotSeenByUser(userId);

        // Assert
        assertEquals(expectedNotifications, result.getNumberNotificationsNotSeen());
        assertEquals(expectedActiveFines, result.getNumberActiveFines());

        // Verify interactions
        verify(notificationRepository).getNumberNotificationsNotSeenByUser(userId, false);
        verify(finesRepository).getNumberActiveFinesByUser(userId, FineStatus.PENDING);
    }

    @Test
    void testGetNumberNotificationsNotSeenByUser_NoNotifications() {
        // Arrange
        String userId = "user123";
        Long expectedNotifications = 0L;
        Long expectedActiveFines = 0L;

        when(notificationRepository.getNumberNotificationsNotSeenByUser(userId, false))
                .thenReturn(expectedNotifications);

        when(finesRepository.getNumberActiveFinesByUser(userId, FineStatus.PENDING))
                .thenReturn(expectedActiveFines);

        // Act
        UserNotificationsInformationDTO result =
                notificationService.getNumberNotificationsNotSeenByUser(userId);

        // Assert
        assertEquals(expectedNotifications, result.getNumberNotificationsNotSeen());
        assertEquals(expectedActiveFines, result.getNumberActiveFines());

        // Verify interactions
        verify(notificationRepository).getNumberNotificationsNotSeenByUser(userId, false);
        verify(finesRepository).getNumberActiveFinesByUser(userId, FineStatus.PENDING);
    }

    @Test
    void testGetNumberNotificationsNotSeenByUser_OnlyNotifications() {
        // Arrange
        String userId = "user123";
        Long expectedNotifications = 5L;
        Long expectedActiveFines = 0L;

        when(notificationRepository.getNumberNotificationsNotSeenByUser(userId, false))
                .thenReturn(expectedNotifications);

        when(finesRepository.getNumberActiveFinesByUser(userId, FineStatus.PENDING))
                .thenReturn(expectedActiveFines);

        // Act
        UserNotificationsInformationDTO result =
                notificationService.getNumberNotificationsNotSeenByUser(userId);

        // Assert
        assertEquals(expectedNotifications, result.getNumberNotificationsNotSeen());
        assertEquals(expectedActiveFines, result.getNumberActiveFines());

        // Verify interactions
        verify(notificationRepository).getNumberNotificationsNotSeenByUser(userId, false);
        verify(finesRepository).getNumberActiveFinesByUser(userId, FineStatus.PENDING);
    }

    @Test
    void testGetNumberNotificationsNotSeenByUser_OnlyActiveFines() {
        // Arrange
        String userId = "user123";
        Long expectedNotifications = 0L;
        Long expectedActiveFines = 3L;

        when(notificationRepository.getNumberNotificationsNotSeenByUser(userId, false))
                .thenReturn(expectedNotifications);

        when(finesRepository.getNumberActiveFinesByUser(userId, FineStatus.PENDING))
                .thenReturn(expectedActiveFines);

        // Act
        UserNotificationsInformationDTO result =
                notificationService.getNumberNotificationsNotSeenByUser(userId);

        // Assert
        assertEquals(expectedNotifications, result.getNumberNotificationsNotSeen());
        assertEquals(expectedActiveFines, result.getNumberActiveFines());

        // Verify interactions
        verify(notificationRepository).getNumberNotificationsNotSeenByUser(userId, false);
        verify(finesRepository).getNumberActiveFinesByUser(userId, FineStatus.PENDING);
    }


}