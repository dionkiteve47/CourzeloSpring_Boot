package tn.esprit.user.services.Implementations;

import tn.esprit.user.dtos.DeviceDTO;
import tn.esprit.user.dtos.DeviceListDTO;
import tn.esprit.user.entities.DeviceMetadata;
import tn.esprit.user.entities.User;
import tn.esprit.user.exceptions.DeviceNotFoundException;
import tn.esprit.user.repositories.DeviceMetadataRepository;
import tn.esprit.user.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tn.esprit.user.services.Interfaces.IDeviceMetadataService;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceMetadataService implements IDeviceMetadataService {
    private final DeviceMetadataRepository deviceMetadataRepository;
    private final UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void saveDeviceDetails(String device, User user) {
        if(isNewDevice(device, user)) {
            log.info("Saving Device...");
            log.info("Device : " + device);
            DeviceMetadata deviceMetadata = new DeviceMetadata();
            deviceMetadata.setDeviceDetails(device);
            deviceMetadata.setUser(user);
            deviceMetadata.setLastLoggedIn(Instant.now().plusSeconds(3600));
            deviceMetadataRepository.save(deviceMetadata);
            log.info("Device Saved!");
        }
    }

    @Override
    public void updateDeviceLastLogin(String userAgent, User user) {
        log.info("Searching for Device...");
        List<DeviceMetadata> devices = deviceMetadataRepository.findByUser(user);
        for (DeviceMetadata device : devices) {
            if (device.getDeviceDetails().equals(userAgent)) {
                device.setLastLoggedIn(Instant.now().plusSeconds(3600));
                deviceMetadataRepository.save(device);
                log.info("Device updated!");
            }
        }
    }

    @Override
    public boolean isNewDevice(String userAgent, User user) {
        log.info("Searching for Device...");
        List<DeviceMetadata> devices = deviceMetadataRepository.findByUser(user);
        for (DeviceMetadata device : devices) {
            if (device.getDeviceDetails().equals(userAgent)) {
                log.info("Device found!");
                return false;
            }
        }
        log.info("Device not found!");
        return true;
    }

    private static final List<String> POSSIBLE_IP_HEADERS = List.of(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    );

    public String getIpAddressFromHeader(HttpServletRequest request) {
        for (String ipHeader : POSSIBLE_IP_HEADERS) {
            String headerValue = Collections.list(request.getHeaders(ipHeader)).stream()
                    .filter(StringUtils::hasLength)
                    .findFirst()
                    .orElse(null);

            if (headerValue != null) {
                return headerValue;
            }
        }

        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteDevice(String id) {
        log.info("Deleting Device...");
        try {
            DeviceMetadata deviceMetadata = deviceMetadataRepository.findById(id)
                    .orElseThrow(() -> new DeviceNotFoundException("Device not found"));

            deviceMetadataRepository.delete(deviceMetadata);
            log.info("Device deleted!");
            return ResponseEntity.ok().build();
        } catch (DeviceNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<DeviceListDTO> getDevices(int page, int sizePerPage, Principal principal) {
        log.info("Getting Devices ...");
        try {
            if (page < 0 || sizePerPage <= 0) {
                return ResponseEntity.badRequest().build();
            }

            Pageable pageable = PageRequest.of(page, sizePerPage);

            User user = userRepository.findUserByEmail(principal.getName());
            List<DeviceDTO> deviceDTOS = deviceMetadataRepository.findByUser(user, pageable)
                    .stream()
                    .map(device -> modelMapper.map(device, DeviceDTO.class))
                    .toList();
            long totalItems = deviceMetadataRepository.countByUser(user);
            log.info("Total devices: {}", totalItems);

            int totalPages = (int) Math.ceil((double) totalItems / sizePerPage);
            log.info("Total pages: {}", totalPages);
            log.info("Devices in page {}: {}", page, deviceDTOS);
            DeviceListDTO deviceListDTO = new DeviceListDTO(deviceDTOS, totalPages);

            return ResponseEntity.ok().body(deviceListDTO);
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
