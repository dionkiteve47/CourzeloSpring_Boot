package com.courzelo.lms.services.user;

import tn.esprit.user.dtos.DeviceListDTO;
import tn.esprit.user.entities.DeviceMetadata;
import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.DeviceMetadataRepository;
import tn.esprit.user.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import tn.esprit.user.services.Implementations.DeviceMetadataService;

import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceMetadataServiceTest {
    @Mock
    private DeviceMetadataRepository deviceMetadataRepository;

    @InjectMocks
    private DeviceMetadataService deviceMetadataService;
    @Mock
    private UserRepository userRepository;

    private User testUser;
    private DeviceMetadata testDevice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testDevice = new DeviceMetadata();
        testDevice.setDeviceDetails("Test Device");
        testDevice.setUser(testUser);
    }

    @Test
    void isNewDeviceReturnsFalseWhenDeviceExists() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(List.of(testDevice));

        assertFalse(deviceMetadataService.isNewDevice("Test Device", testUser));
    }

    @Test
    void isNewDeviceReturnsTrueWhenDeviceDoesNotExist() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        assertTrue(deviceMetadataService.isNewDevice("Test Device", testUser));
    }

    @Test
    void isNewDeviceReturnsTrueWhenDifferentDeviceExists() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(List.of(testDevice));

        assertTrue(deviceMetadataService.isNewDevice("Different Device", testUser));
    }
    @Test
    void saveDeviceDetailsSavesNewDevice() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        deviceMetadataService.saveDeviceDetails("Test Device", testUser);

        verify(deviceMetadataRepository, times(1)).save(any(DeviceMetadata.class));
    }

    @Test
    void saveDeviceDetailsDoesNotSaveExistingDevice() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(List.of(testDevice));

        deviceMetadataService.saveDeviceDetails("Test Device", testUser);

        verify(deviceMetadataRepository, times(0)).save(any(DeviceMetadata.class));
    }

    @Test
    void updateDeviceLastLoginUpdatesExistingDevice() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(List.of(testDevice));

        deviceMetadataService.updateDeviceLastLogin("Test Device", testUser);

        verify(deviceMetadataRepository, times(1)).save(testDevice);
        assertEquals(Instant.now().plusSeconds(3600), testDevice.getLastLoggedIn());
    }

    @Test
    void updateDeviceLastLoginDoesNotUpdateNonExistingDevice() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        deviceMetadataService.updateDeviceLastLogin("Test Device", testUser);

        verify(deviceMetadataRepository, times(0)).save(any(DeviceMetadata.class));
    }

    @Test
    void updateDeviceLastLoginDoesNotUpdateDifferentDevice() {
        when(deviceMetadataRepository.findByUser(testUser)).thenReturn(List.of(testDevice));

        deviceMetadataService.updateDeviceLastLogin("Different Device", testUser);

        verify(deviceMetadataRepository, times(0)).save(any(DeviceMetadata.class));
    }

    @Test
    void getIpAddressFromHeaderReturnsFirstNonEmptyHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Enumeration<String> headers = Collections.enumeration(List.of("", "192.168.1.1"));
        when(request.getHeaders("X-Forwarded-For")).thenReturn(headers);

        String ipAddress = deviceMetadataService.getIpAddressFromHeader(request);

        assertEquals("192.168.1.1", ipAddress);
    }

    @Test
    void getIpAddressFromHeaderReturnsNullWhenNoMatchingHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaders(anyString())).thenReturn(Collections.emptyEnumeration());

        String ipAddress = deviceMetadataService.getIpAddressFromHeader(request);

        assertNull(ipAddress);
    }

    @Test
    void deleteDeviceDeletesExistingDevice() {
        when(deviceMetadataRepository.findById(anyString())).thenReturn(Optional.of(testDevice));

        ResponseEntity<org.springframework.http.HttpStatus> response = deviceMetadataService.deleteDevice("testId");

        verify(deviceMetadataRepository, times(1)).delete(testDevice);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteDeviceReturnsBadRequestWhenDeviceNotFound() {
        when(deviceMetadataRepository.findById(anyString())).thenReturn(Optional.empty());

        ResponseEntity<HttpStatus> response = deviceMetadataService.deleteDevice("testId");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getDevicesReturnsDeviceListWhenValidRequest() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findUserByEmail(anyString())).thenReturn(user);
        when(deviceMetadataRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(List.of(testDevice));
        when(deviceMetadataRepository.countByUser(any(User.class))).thenReturn(1L);

        ResponseEntity<DeviceListDTO> response = deviceMetadataService.getDevices(0, 1, mock(Principal.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getDevices().size());
        assertEquals(1, response.getBody().getTotalPages());
    }

    @Test
    void getDevicesReturnsBadRequestWhenInvalidRequest() {
        ResponseEntity<DeviceListDTO> response = deviceMetadataService.getDevices(-1, 0, mock(Principal.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getDevicesReturnsInternalServerErrorWhenExceptionThrown() {
        when(userRepository.findUserByEmail(anyString())).thenThrow(new RuntimeException());

        ResponseEntity<DeviceListDTO> response = deviceMetadataService.getDevices(0, 1, mock(Principal.class));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}