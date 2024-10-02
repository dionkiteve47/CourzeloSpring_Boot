package tn.esprit.user.services.Implementations;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dtos.UserDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.institution.Program;
import tn.esprit.user.entities.schedule.FieldOfStudy;
import tn.esprit.user.entities.schedule.Modul;
import tn.esprit.user.entities.schedule.SemesterNumber;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.exceptions.ClassNotFoundException;
import tn.esprit.user.exceptions.InstitutionNotFoundException;
import tn.esprit.user.exceptions.ProgramNotFoundException;
import tn.esprit.user.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tn.esprit.user.services.Interfaces.IClassService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService implements IClassService {
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final ProgramRepository programRepository;
    private final FieldOfStudyRepository fieldOfRepository;
    private final ModulRepository modulRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<ClassDTO>> getClasses() {
        log.info("Getting all classes");
        return ResponseEntity
                .ok()
                .body(classRepository.findAll()
                        .stream()
                        .map(classes -> modelMapper.map(classes, ClassDTO.class))
                        .toList());
    }

    @Override
    public ResponseEntity<Boolean> deleteClass(String classID) {
        log.info("Delete CLASS :" + classID);
        Class aClass = classRepository.findById(classID)
                .orElseThrow(() -> new ClassNotFoundException("Class " + classID + " not found"));
        if (aClass != null) {
            removeUsersInClass(classID);
            classRepository.deleteById(classID);
            return ResponseEntity.ok().body(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    public void removeUsersInClass(String classID) {
        Class aClass = classRepository.findById(classID)
                .orElseThrow(() -> new ClassNotFoundException("Class not found"));
        List<User> users = userRepository.findByEducationStclass(aClass);
        if (!users.isEmpty()) {
            for (User user : users) {
                user.getEducation().setStclass(null);
                userRepository.save(user);
            }
        }
    }

    @Override
    public ResponseEntity<Boolean> addClass(ClassDTO classDTO) {
        log.info("Adding class ");
        Class aClass = modelMapper.map(classDTO, Class.class);
        Class savedClass = classRepository.save(aClass);
        if (savedClass.getId() != null) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @Override
    public ResponseEntity<Boolean> updateClass(ClassDTO classDTO) {
        log.info("Update class :" + classDTO.getId());
        Class aClass = classRepository.findById(classDTO.getId())
                .orElseThrow(() -> new ClassNotFoundException("Class " + classDTO.getId() + " not found"));
        aClass.setName(classDTO.getName());
        //aClass.setCapacity(classDTO.getCapacity());
        Class savedClass = classRepository.save(aClass);
        if (savedClass.getId() != null) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @Override
    public ResponseEntity<UserListDTO> getClassUsers(String classID, Principal principal, String role, int page, int sizePerPage) {
        log.info("role = " + role);
        log.info("page = " + page);
        log.info("sizeperpage = " + sizePerPage);

        User userr = userRepository.findUserByEmail(principal.getName());
        if (userr.getRoles().contains(Role.SUPERADMIN) && !Objects.equals(classID, "")) {
            Class aClass = classRepository.findById(classID)
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + userr.getEducation().getInstitution().getId() + " not found"));
            return getUserListDTOResponseEntity(aClass, principal, role, page, sizePerPage);
        } else if (userr.getEducation().getInstitution() != null) {
            log.info("Checking class");
            Institution institution = institutionRepository.findById(userr.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
            log.info("Class id : " + classID);
            Class aClass = classRepository.findById(classID)
                    .orElseThrow(() -> new ClassNotFoundException("Class " + classID + " not found"));
            log.info("Program id : " + aClass.getProgram().getId());
            Program program = programRepository.findById(aClass.getProgram().getId())
                    .orElseThrow(() -> new ClassNotFoundException("Class not found"));
            Institution programInstitution = institutionRepository.findById(program.getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
            log.info("finished checking");
            if (Objects.equals(institution.getId(), programInstitution.getId())) {
                return getUserListDTOResponseEntity(aClass, principal, role, page, sizePerPage);
            }
        }

        return ResponseEntity.badRequest().body(null);
    }

    @Override
    public ResponseEntity<Boolean> addUserToClass(String classID, String userEmail, String role) {
        log.info("Adding user!");

        Class aClass = classRepository.findById(classID)
                .orElseThrow(() -> new ClassNotFoundException("Class " + classID + " not found"));

        User target = userRepository.findUserByEmail(userEmail);
        if (target == null) {
            log.info("User not found!");
            return ResponseEntity.badRequest().body(false);
        }

        Program program = programRepository.findById(aClass.getProgram().getId())
                .orElseThrow(() -> new ProgramNotFoundException("Program Not Found"));

        Institution institution = institutionRepository.findById(program.getInstitution().getId())
                .orElseThrow(() -> new InstitutionNotFoundException("Institution Not found"));

        if (!userInInstitution(target, institution)) {
            log.info("User is not part of the institution!");
            return ResponseEntity.badRequest().body(false);
        }
        if (Objects.equals(role, "Teachers")) {
            if (aClass.getTeachers().contains(target)) {
                log.info("Teacher already added to the class!");
                return ResponseEntity.ok().body(true);
            }
            aClass.getTeachers().add(target);
            target.getEducation().setStclass(aClass);
            if (!target.getRoles().contains(Role.TEACHER)) {
                target.getRoles().add(Role.TEACHER);
            }
            if (!institution.getTeachers().contains(target)) {
                institution.getTeachers().add(target);
            }
        } else if (Objects.equals(role, "Students")) {
            if (aClass.getStudents().contains(target)) {
                log.info("Student already added to the class!");
                return ResponseEntity.ok().body(true);
            }
            aClass.getStudents().add(target);
            target.getEducation().setStclass(aClass);
            if (!target.getRoles().contains(Role.STUDENT)) {
                target.getRoles().add(Role.STUDENT);
            }
            if (!institution.getStudents().contains(target)) {
                institution.getStudents().add(target);
            }
        }
        userRepository.save(target);
        institutionRepository.save(institution);
        classRepository.save(aClass);

        log.info("User added to the class!");
        return ResponseEntity.ok().body(true);
    }


    @Override
    public ResponseEntity<Boolean> removeUser(String classID, String userEmail) {
        log.info("removing user !");
        User user = userRepository.findUserByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.badRequest().body(false);
        }
        Class aClass = classRepository.findById(classID)
                .orElseThrow(() -> new ClassNotFoundException("Class " + classID + " not found"));
        if (aClass.getTeachers().contains(user)) {
            aClass.getTeachers().remove(user);
            user.getEducation().setStclass(null);
            user.getRoles().remove(Role.TEACHER);
            classRepository.save(aClass);
            userRepository.save(user);
            return ResponseEntity.ok().body(true);
        }
        if (aClass.getStudents().contains(user)) {
            aClass.getStudents().remove(user);
            user.getEducation().setStclass(null);
            user.getRoles().remove(Role.STUDENT);
            classRepository.save(aClass);
            userRepository.save(user);
            return ResponseEntity.ok().body(true);
        }

        return ResponseEntity.badRequest().body(false);
    }

    @Override
    public boolean userInInstitution(User user, Institution institution) {
        if (user.getEducation().getInstitution() != null) {
            log.info("user institution id = " + user.getEducation().getInstitution().getId());
            log.info("institution id = " + institution.getId());

            return Objects.equals(user.getEducation().getInstitution().getId(), institution.getId());
        }
        return false;
    }

    private ResponseEntity<UserListDTO> getUserListDTOResponseEntity(Class aClass, Principal principal, String role, int page, int sizePerPage) {

        log.info("Getting aClass Users: " + aClass.getId());
        List<User> users;
        if (Objects.equals(role, "Students")) {
            users = aClass.getStudents();
        } else {
            users = aClass.getTeachers();
        }

        int start = page * sizePerPage;
        int end = Math.min((start + sizePerPage), users.size());
        List<User> paginatedUsers = users.subList(start, end);

        List<UserDTO> userDTOs = paginatedUsers.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getRoles().stream().map(Role::name).toList(),
                        user.getSecurity(),
                        user.getProfile(),
                        user.getEducation(),
                        user.getContact(),
                        user.getActivity(),
                        user.getSettings(),
                        user.getScore()
                ))
                .toList();
        log.info("users in page : " + page + " " + userDTOs);

        Page<UserDTO> pageResult = new PageImpl<>(userDTOs, PageRequest.of(page, sizePerPage), users.size());
        log.info("users total pages : " + pageResult.getTotalPages());

        UserListDTO userListDTO = new UserListDTO(userDTOs, pageResult.getTotalPages());
        return ResponseEntity.ok().body(userListDTO);
    }

    public ClassDTO getClasseById(String id) {
        Class aClass = classRepository.findById(id)
                .orElseThrow(() ->
                        new ClassNotFoundException("Class " + id + " not found"));
        return mapToDTO(aClass);
    }

    public Class addClasse(Class classe, String idField) {
        FieldOfStudy fieldOfStudy = fieldOfRepository.findById(idField).orElseThrow(() -> new RuntimeException("Field with ID" + idField + " doesn't exist!"));
        classe.setFieldOfStudy(fieldOfStudy);
        return classRepository.save(classe);
    }
    public List<Class> searchClassesBySemester(SemesterNumber semesterNumber) {
        return classRepository.findBySemester_SemesterNumber(semesterNumber);
    }

    @Override
    public ResponseEntity<List<ClassDTO>> getClassesWithoutPagination() {
        log.info("Getting all classes without pagination");
        return ResponseEntity
                .ok()
                .body(classRepository.findAll()
                        .stream()
                        .map(classes -> modelMapper.map(classes, ClassDTO.class))
                        .toList());
    }

    @Override
    public ResponseEntity<ClassDTO> getMyClass(Principal principal) {
        log.info("Getting my class");
        User user = userRepository.findUserByEmail(principal.getName());
        if (user.getEducation().getStclass() != null) {
            return ResponseEntity.ok().body(modelMapper.map(user.getEducation().getStclass(), ClassDTO.class));
        }
        return ResponseEntity.badRequest().body(null);
    }

    public List<Class> findAll() {
        return classRepository.findAll();
    }
    public List<ClassDTO> getClasses2() {
        log.info("Getting all classes");
        return classRepository.findAll()
                .stream()
                .map(classes -> modelMapper.map(classes, ClassDTO.class))
                .collect(Collectors.toList());
    }
    public ClassDTO mapToDTO(Class aClass) {
        ClassDTO classDTO = new ClassDTO();
        classDTO.setId(aClass.getId());
        classDTO.setName(aClass.getName());
        classDTO.setCapacity(aClass.getCapacity());
        classDTO.setTeachers(aClass.getTeachers());
        classDTO.setModuls(aClass.getModuls());
        classDTO.setFieldOfStudy(aClass.getFieldOfStudy());
        classDTO.setSemester(aClass.getSemester());
        return classDTO;
    }
    @Override
    public ResponseEntity<Boolean> addClass1(ClassDTO classDTO) {
        log.info("Adding class ");
        Class aClass = modelMapper.map(classDTO, Class.class);
        List<Modul> moduls = new ArrayList<>();
        if (classDTO.getModuls() != null) {
            moduls = classDTO.getModuls().stream()
                    .map(modulDTO -> {
                        Modul modul = modulRepository.findById(modulDTO.getId())
                                .orElseThrow(() -> new RuntimeException("Modul not found for ID: " + modulDTO.getId()));
                        return modul;
                    })
                    .collect(Collectors.toList());
        }
        aClass.setModuls(moduls);

        if (classDTO.getFieldOfStudy() != null && classDTO.getFieldOfStudy().getId() != null) {
            FieldOfStudy fieldOfStudy = fieldOfRepository.findById(classDTO.getFieldOfStudy().getId())
                    .orElseThrow(() -> new RuntimeException("FieldOfStudy not found for ID: " + classDTO.getFieldOfStudy().getId()));
            aClass.setFieldOfStudy(fieldOfStudy);
        }

        Class savedClass = classRepository.save(aClass);
        if (savedClass != null && savedClass.getId() != null) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    public ResponseEntity<ClassDTO> getMyClass1(Principal principal, String email) {
        log.info("Getting my class");
        User user = userRepository.findUserByEmail(principal.getName());
        if (user == null) {
            user = userRepository.findUserByEmail(email);
        }
        if (user.getEducation().getStclass() != null) {
            return ResponseEntity.ok().body(modelMapper.map(user.getEducation().getStclass(), ClassDTO.class));
        }
        return ResponseEntity.badRequest().body(null);
    }
}