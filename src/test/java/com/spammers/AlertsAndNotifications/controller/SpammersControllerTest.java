package com.spammers.AlertsAndNotifications.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spammers.AlertsAndNotifications.model.dto.FineInputDTO;
import com.spammers.AlertsAndNotifications.model.dto.FineOutputDTO;
import com.spammers.AlertsAndNotifications.model.dto.LoanDTO;
import com.spammers.AlertsAndNotifications.model.dto.NotificationDTO;
import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import com.spammers.AlertsAndNotifications.model.enums.NotificationType;
import com.spammers.AlertsAndNotifications.service.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SpammersControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SpammersController spammersController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(spammersController).build();
    }

    @Test
    void testGetNotifications_Success() throws Exception {
        // Arrange
        String userId = "user123";
        int page = 0;
        int size = 10;

        List<NotificationDTO> notifications = Arrays.asList(
                new NotificationDTO("notif1", LocalDate.now(), NotificationType.FINE,"Boulevard", false),
                new NotificationDTO("notif2", LocalDate.now(), NotificationType.FINE,"Harry Potter", false)
        );

        PaginatedResponseDTO<NotificationDTO> responseDTO = new PaginatedResponseDTO<>(
                notifications, page, 1, 2
        );

        // Mock the service method
        when(notificationService.getNotifications(eq(userId), eq(page), eq(size)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/usersNotifications/users/{userId}", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("notif1"))
                .andExpect(jsonPath("$.data[1].id").value("notif2"));

        // Verify service method was called
        verify(notificationService).getNotifications(eq(userId), eq(page), eq(size));
    }

    @Test
    void testGetFines_Success() throws Exception {
        // Arrange
        String userId = "user123";
        int page = 0;
        int size = 10;

        List<FineOutputDTO> fines = Arrays.asList(
                new FineOutputDTO("fine1","Description 1",50.0f, FineStatus.PENDING, FineType.DAMAGE,LocalDate.now(),"Boulevard"),
                new FineOutputDTO("fine2","Description 2",75.0f, FineStatus.PENDING, FineType.DAMAGE,LocalDate.now(),"Harry Potter")
        );

        PaginatedResponseDTO<FineOutputDTO> responseDTO = new PaginatedResponseDTO<>(
                fines, page, 1, 2
        );

        // Mock the service method
        when(notificationService.getFinesByUserId(userId, page, size))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/usersNotifications/users/{userId}/fines", userId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].fineId").value("fine1"))
                .andExpect(jsonPath("$.data[1].fineId").value("fine2"));

        // Verify service method was called
        verify(notificationService).getFinesByUserId(eq(userId), eq(page), eq(size));
    }


}