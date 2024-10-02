package tn.esprit.user.services.Implementations;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dto.schedule.*;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.User;
import tn.esprit.user.services.Implementations.ClassService;
import tn.esprit.user.services.Implementations.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor

public class DataFromDB {

    public static List<User> teachers;

    public List<SemesterDTO> semesters;
    public static List<ElementModuleDTO> elementModules;
    public static List<FieldOfStudyDTO> fieldOfStudies;
    public static List<DepartmentDTO> departments;
    public static List<ClassDTO> classes;
    public static List<ModulDTO> moduls;
    private UserService userService;
    private SemesterService semesterService;
    private ElementModuleService elementModuleService;
    private FieldOfStudyService fieldOfStudyService;
    private DepartmentService departmentService;
    private ModulService modulService;
    private ClassService classService;

    public void loadDataFromDatabase() {
        semesters = semesterService.findAll();
        //userDTOS=userService.getUserByID();
        elementModules = elementModuleService.findAll();
        fieldOfStudies = fieldOfStudyService.findAll();
        departments = departmentService.findAll();
        moduls = modulService.findAll();
        teachers=userService.getProfsByRole();
        classes = classService.getClasses2();

    }
}
