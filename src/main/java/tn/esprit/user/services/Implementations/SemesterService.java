package tn.esprit.user.services.Implementations;


import tn.esprit.user.dto.schedule.SemesterDTO;
import tn.esprit.user.entities.schedule.Semester;
import tn.esprit.user.entities.schedule.SemesterNumber;
import tn.esprit.user.repositories.SemesterRepository;
import tn.esprit.user.utils.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SemesterService {

    private static SemesterRepository semesterRepository = null;

    public SemesterService(final SemesterRepository semesterRepository) {
        SemesterService.semesterRepository = semesterRepository;
    }

    public List<SemesterDTO> findAll() {
        final List<Semester> semesters = semesterRepository.findAll(Sort.by("id"));
        return semesters.stream()
                .map(semester -> mapToDTO(semester, new SemesterDTO()))
                .toList();
    }

    public SemesterDTO get(final String id) {
        return semesterRepository.findById(id)
                .map(semester -> mapToDTO(semester, new SemesterDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final SemesterDTO semesterDTO) {
        final Semester semester = new Semester();
        mapToEntity(semesterDTO, semester);
        return semesterRepository.save(semester).getId();
    }

    public void update(final String id, final SemesterDTO semesterDTO) {
        final Semester semester = semesterRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(semesterDTO, semester);
        semesterRepository.save(semester);
    }

    public void delete(final String id) {
        semesterRepository.deleteById(id);
    }

    private SemesterDTO mapToDTO(final Semester semester, final SemesterDTO semesterDTO) {
        semesterDTO.setId(semester.getId());
        semesterDTO.setStartDate(semester.getStartDate());
        semesterDTO.setEndDate(semester.getEndDate());
        semesterDTO.setUniversityYear(semester.getUniversityYear());
        semesterDTO.setSemesterNumber(semester.getSemesterNumber());
        return semesterDTO;
    }
    private Semester mapToEntity(final SemesterDTO semesterDTO, final Semester semester) {
        semester.setStartDate(semesterDTO.getStartDate());
        semester.setEndDate(semesterDTO.getEndDate());
        semester.setUniversityYear(semesterDTO.getUniversityYear());
        semester.setSemesterNumber(semesterDTO.getSemesterNumber());
        return semester;
    }
    public static List<Semester> findSemestreByNum(SemesterNumber semesterNumber) {
        return semesterRepository.findSemesterBySemesterNumber(semesterNumber);
    }
    public Semester addSemestre(Semester semester ) {
        return semesterRepository.save(semester);
    }

}
