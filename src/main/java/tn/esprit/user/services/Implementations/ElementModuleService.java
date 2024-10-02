package tn.esprit.user.services.Implementations;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.*;
import tn.esprit.user.dto.schedule.ElementModuleDTO;
import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.*;
import tn.esprit.user.utils.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ElementModuleService {

    private final ElementModuleRepository elementModuleRepository;
    private final SemesterRepository semesterRepository;
    private final DepartmentRepository departmentRepository;
    private final ModulRepository modulRepository;
    private final UserRepository userRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final ClassRepository classRepository;

    public ElementModuleService(final ElementModuleRepository elementModuleRepository, SemesterRepository semesterRepository, DepartmentRepository departmentRepository, ModulRepository modulRepository, UserRepository userRepository, FieldOfStudyRepository fieldOfStudyRepository, ClassRepository classRepository) {
        this.elementModuleRepository = elementModuleRepository;
        this.semesterRepository = semesterRepository;
        this.departmentRepository = departmentRepository;
        this.modulRepository = modulRepository;
        this.userRepository = userRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository;
        this.classRepository = classRepository;
    }

    public List<ElementModuleDTO> findAll() {
        final List<ElementModule> elementModules = elementModuleRepository.findAll(Sort.by("id"));
        return elementModules.stream()
                .map(elementModule -> mapToDTO(elementModule, new ElementModuleDTO()))
                .toList();
    }

    public ElementModuleDTO get(final String id) {
        return elementModuleRepository.findById(id)
                .map(elementModule -> mapToDTO(elementModule, new ElementModuleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final ElementModuleDTO elementModuleDTO) {
        final ElementModule elementModule = new ElementModule();
        if (elementModuleDTO.getId() != null) {
            Modul modul = modulRepository.findById(elementModuleDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Modul not found for ID: " + elementModuleDTO.getId()));
            elementModule.setModul(modul);
        }
        if (elementModuleDTO.getTeacher() != null && elementModuleDTO.getTeacher().getId() != null) {
            User teacher = userRepository.findById(elementModuleDTO.getTeacher().getId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found for ID: " + elementModuleDTO.getTeacher().getId()));
            elementModule.setTeacher(teacher);
        }
        if (elementModuleDTO.getFieldOfStudies() != null) {
            List<FieldOfStudy> fieldOfStudies = new ArrayList<>();
            for (FieldOfStudy fieldOfStudyDTO : elementModuleDTO.getFieldOfStudies()) {
                if (fieldOfStudyDTO.getId() != null) {
                    FieldOfStudy fieldOfStudy = fieldOfStudyRepository.findById(fieldOfStudyDTO.getId())
                            .orElseThrow(() -> new RuntimeException("FieldOfStudy not found for ID: " + fieldOfStudyDTO.getId()));
                    fieldOfStudies.add(fieldOfStudy);
                }
            }
            elementModule.setFieldOfStudies(fieldOfStudies);
        }


        mapToEntity(elementModuleDTO, elementModule);
        ElementModule savedElementModule = elementModuleRepository.save(elementModule);
        if (elementModule.getModul() != null) {
            modulRepository.save(elementModule.getModul());
        }
        return savedElementModule.getId();
    }

    public void update(final String id, final ElementModuleDTO elementModuleDTO) {
        final ElementModule elementModule = elementModuleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(elementModuleDTO, elementModule);
        elementModuleRepository.save(elementModule);
    }

    public void delete(final String id) {
        elementModuleRepository.deleteById(id);
    }

    private ElementModuleDTO mapToDTO(final ElementModule elementModule,
                                      final ElementModuleDTO elementModuleDTO) {
        elementModuleDTO.setId(elementModule.getId());
        elementModuleDTO.setNmbrHours(elementModule.getNmbrHours());
        elementModuleDTO.setName(elementModule.getName());
        elementModuleDTO.setDayOfWeek(elementModule.getDayOfWeek());
        elementModuleDTO.setPeriod(elementModule.getPeriod());
        elementModuleDTO.setSemesters(elementModule.getSemesters());
        elementModuleDTO.setDepartments(elementModule.getDepartments());
        elementModuleDTO.setNumSemesters(elementModule.getNumSemesters());
        elementModuleDTO.setNumDepartments(elementModule.getNumDepartments());
        elementModuleDTO.setModul(elementModule.getModul());
        elementModuleDTO.setClasses(elementModule.getClasses());
        elementModuleDTO.setTeacher(elementModule.getTeacher());
        elementModuleDTO.setFieldOfStudies(elementModule.getFieldOfStudies());
        return elementModuleDTO;
    }

    private ElementModule mapToEntity(final ElementModuleDTO elementModuleDTO,
                                      final ElementModule elementModule) {
        elementModule.setNmbrHours(elementModuleDTO.getNmbrHours());
        elementModule.setName(elementModuleDTO.getName());
        elementModule.setDayOfWeek(elementModuleDTO.getDayOfWeek());
        elementModule.setPeriod(elementModuleDTO.getPeriod());
        elementModule.setSemesters(elementModuleDTO.getSemesters());
        elementModule.setDepartments(elementModuleDTO.getDepartments());
        elementModule.setNumSemesters(elementModuleDTO.getNumSemesters());
        elementModule.setNumDepartments(elementModuleDTO.getNumDepartments());
        elementModule.setModul(elementModuleDTO.getModul());
        elementModule.setClasses(elementModuleDTO.getClasses());
        elementModule.setTeacher(elementModuleDTO.getTeacher());
        elementModule.setFieldOfStudies(elementModuleDTO.getFieldOfStudies());
        return elementModule;
    }

    public ElementModule getElementDeModuleById(String id) {
        return elementModuleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module Element number " + id + " does not exist!"));
    }

    public List<ElementModule> getEmploisByClasse(String id) {
        return elementModuleRepository.getElementModulesByClasses(id);
    }

    public ElementModule addElementModule(ElementModule elementDeModule) {
        return elementModuleRepository.save(elementDeModule);
    }
    public ElementModuleDTO createElementModule(ElementModuleDTO elementModuleDTO) {
        List <Class> classes = new ArrayList<>();
        for (String classId : elementModuleDTO.getClassIds()) {
            Class aClass = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found for ID: " + classId));
            classes.add(aClass);
        }

        List<Semester> semesters = new ArrayList<>();
        for (String semesterId : elementModuleDTO.getSemesterIds()) {
            Semester semester = semesterRepository.findById(semesterId)
                    .orElseThrow(() -> new RuntimeException("Semester not found for ID: " + semesterId));
            semesters.add(semester);
        }

        List<Department> departments = new ArrayList<>();
        for (String departmentId : elementModuleDTO.getDepartmentIds()) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found for ID: " + departmentId));
            departments.add(department);
        }

        Modul modul = modulRepository.findById(elementModuleDTO.getModulId())
                .orElseThrow(() -> new RuntimeException("Modul not found for ID: " + elementModuleDTO.getModulId()));

        User teacher = userRepository.findById(elementModuleDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found for ID: " + elementModuleDTO.getTeacherId()));

        ElementModule elementModule = new ElementModule();
        elementModule.setClasses(classes);
        elementModule.setSemesters(semesters);
        elementModule.setDepartments(departments);
        elementModule.setModul(modul);
        elementModule.setTeacher(teacher);

        ElementModule savedElementModule = elementModuleRepository.save(elementModule);
        return mapToDTO(savedElementModule, new ElementModuleDTO());
    }

   /* public List<ElementModule>getEmploisByClass(String classe){
        return elementModuleRepository.findByClasse(classe);
    }*/





}




