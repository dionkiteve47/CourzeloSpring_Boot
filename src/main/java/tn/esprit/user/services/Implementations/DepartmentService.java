package tn.esprit.user.services.Implementations;


import tn.esprit.user.dto.schedule.DepartmentDTO;
import tn.esprit.user.entities.schedule.Department;
import tn.esprit.user.entities.schedule.FieldOfStudy;
import tn.esprit.user.repositories.DepartmentRepository;
import tn.esprit.user.repositories.FieldOfStudyRepository;
import tn.esprit.user.utils.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final FieldOfStudyService fieldOfStudyService;

    public DepartmentService(final DepartmentRepository departementRepository, FieldOfStudyRepository fieldOfStudyRepository, FieldOfStudyRepository fieldOfStudyRepository1, FieldOfStudyService fieldOfStudyService) {
        this.departmentRepository = departementRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository1;
        this.fieldOfStudyService = fieldOfStudyService;

    }

    public List<DepartmentDTO> findAll() {
        final List<Department> departments = departmentRepository.findAll(Sort.by("id"));
        return departments.stream()
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .toList();
    }

    public DepartmentDTO get(final String id) {
        return departmentRepository.findById(id)
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final DepartmentDTO departmentDTO) {
        final Department department = new Department();
        mapToEntity(departmentDTO, department);
        return departmentRepository.save(department).getId();


    }

    public void update(final String id, final DepartmentDTO departmentDTO) {
        final Department department = departmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(departmentDTO, department);
        departmentRepository.save(department);
    }



    /*public String create1(final DepartmentDTO departmentDTO ) {

        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setChefDepartment(departmentDTO.getChefDepartment());


        department = departmentRepository.save(department);

        // Create FieldOfStudy entity
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName(fieldOfStudy.getName());
        fieldOfStudy.setChefField(fieldOfStudy.getChefField());
        fieldOfStudy.setNumbrWeeks(fieldOfStudy.getNumbrWeeks());



        //fieldOfStudy.setDepartment(department);

        // Save the FieldOfStudy
        fieldOfStudy = fieldOfStudyRepository.save(fieldOfStudy);

        return department.getId();
    }
*/


    public void delete(final String id) {
        departmentRepository.deleteById(id);
    }

    private DepartmentDTO mapToDTO(final Department departement,
                                   final DepartmentDTO departmentDTO) {
        departmentDTO.setId(departement.getId());
        departmentDTO.setName(departement.getName());
        departmentDTO.setChefDepartment(departement.getChefDepartment());
        departmentDTO.setFieldOfStudies(departement.getFieldOfStudies());

        return departmentDTO;
    }

    private Department mapToEntity(final DepartmentDTO departmentDTO,
                                   final Department department) {

        department.setName(departmentDTO.getName());
        department.setChefDepartment(departmentDTO.getChefDepartment());
        department.setFieldOfStudies(departmentDTO.getFieldOfStudies());


        return department;
    }

    public long countDepartements() {
        return departmentRepository.count();
    }

    public List<DepartmentDTO> searchDepartments(String name) {
        List<Department> departments = departmentRepository.findByName(name);
        if (!departments.isEmpty()) {
            return departments.stream()
                    .map(department -> mapToDTO(department, new DepartmentDTO()))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Department not found");
        }
    }

    public List<DepartmentDTO> getDepartmentsByFieldOfStudy(String id) {
        FieldOfStudy fieldOfStudy = fieldOfStudyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Field of study not found"));
        List<Department> departments = (List<Department>) fieldOfStudy.getDepartment();
        if (departments == null) {
            return Collections.emptyList();
        }
        return departments.stream()
                .map(department -> mapToDTO(department, new DepartmentDTO()))
                .collect(Collectors.toList());
    }
    public Department getDepartmentById(String id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }
    public List<Department> findDepartmentByName(String name) {
        return  departmentRepository.findByName(name);
    }
    public Department addDepartment(Department department ) {
        return departmentRepository.save(department);
    }


}
