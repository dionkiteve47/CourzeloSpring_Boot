package tn.esprit.user.services.Implementations;


import tn.esprit.user.dto.schedule.NonDisponibilityDTO;
import tn.esprit.user.entities.schedule.NonDisponibility;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.NonDisponibilityRepository;
import tn.esprit.user.repositories.UserRepository;
import tn.esprit.user.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Slf4j
@Service
public class NonDisponibilityService {

    private final NonDisponibilityRepository nonDisponibilityRepository;
    private final UserRepository userRepository;

    public NonDisponibilityService(final NonDisponibilityRepository nonDisponibilityRepository, UserRepository userRepository) {
        this.nonDisponibilityRepository = nonDisponibilityRepository;
        this.userRepository = userRepository;
    }
    

    public List<NonDisponibilityDTO> findAll() {
        log.info("Finding all non disponibilities");
        final List<NonDisponibility> nonDisponibilities = nonDisponibilityRepository.findAll(Sort.by("id"));
        log.info("Found {} non disponibilities", nonDisponibilities.size());
        return nonDisponibilities.stream()
                .map(nonDisponibility -> mapToDTO(nonDisponibility, new NonDisponibilityDTO()))
                .toList();
    }

    public NonDisponibilityDTO get(final String id) {
        return nonDisponibilityRepository.findById(id)
                .map(nonDisponibility -> mapToDTO(nonDisponibility, new NonDisponibilityDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final NonDisponibilityDTO nonDisponibilityDTO, String email) {
        log.info("Creating non disponibility");
        User teacher = userRepository.findUserByEmail(email);
        if(!teacher.getRoles().contains(Role.TEACHER)){
            throw new RuntimeException("User is not a teacher"+teacher.getRoles()+teacher.getEmail());
        }
        final NonDisponibility nonDisponibility = new NonDisponibility();
        mapToEntity(nonDisponibilityDTO, nonDisponibility);
        NonDisponibility saveDISPO = nonDisponibilityRepository.save(nonDisponibility);
        saveDISPO.setTeacher(teacher);
        return nonDisponibilityRepository.save(saveDISPO).getId();
    }

    public void update(final String id, final NonDisponibilityDTO nonDisponibilityDTO) {
        final NonDisponibility nonDisponibility = nonDisponibilityRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        nonDisponibility.setPeriod(nonDisponibilityDTO.getPeriod());
        nonDisponibility.setDayOfWeek(nonDisponibilityDTO.getDayOfWeek());
        nonDisponibilityRepository.save(nonDisponibility);
    }

    public void delete(final String id) {
        nonDisponibilityRepository.deleteById(id);
    }

    private NonDisponibilityDTO mapToDTO(final NonDisponibility nonDisponibility,
            final NonDisponibilityDTO nonDisponibilityDTO) {
        nonDisponibilityDTO.setId(nonDisponibility.getId());
        nonDisponibilityDTO.setPeriod(nonDisponibility.getPeriod());
        nonDisponibilityDTO.setDayOfWeek(nonDisponibility.getDayOfWeek());
        return nonDisponibilityDTO;
    }


    private NonDisponibility mapToEntity(final NonDisponibilityDTO nonDisponibilityDTO,
                                         final NonDisponibility nonDisponibility) {
        nonDisponibility.setPeriod(nonDisponibilityDTO.getPeriod());
        nonDisponibility.setDayOfWeek(nonDisponibilityDTO.getDayOfWeek());
        log.info("dto : "+nonDisponibilityDTO);
        log.info("entity : "+nonDisponibility);
        return nonDisponibility;
    }

}
