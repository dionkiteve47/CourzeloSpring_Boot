package tn.esprit.user.services.Implementations;

import tn.esprit.user.GAlgo.GAlgorithm;
import tn.esprit.user.GAlgo.UniversityTimetable;
import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dto.schedule.TimeTableDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.entities.schedule.SemesterNumber;
import tn.esprit.user.entities.schedule.TimeTable;
import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.ElementModuleRepository;
import tn.esprit.user.repositories.TimeTableRepository;
import tn.esprit.user.services.Implementations.UserService;
import tn.esprit.user.utils.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.internal.authentication.AwsCredentialHelper.LOGGER;

@Service
public class TimeTableService {
    private final DataFromDB dataFromDb;
    private final ElementModuleRepository elementModuleRepository;
    private final TimeTableRepository timeTableRepository ;
    private ElementModuleService elementModuleService;
    private UserService userService;

    private Map<DayOfWeek, Map<Period, String>> schedule;

    public TimeTableService(DataFromDB dataFromDb, ElementModuleRepository elementModuleRepository, final TimeTableRepository timeTableRepository, ElementModuleService elementModuleService) {
        this.dataFromDb = dataFromDb;
        this.elementModuleRepository = elementModuleRepository;
        this.timeTableRepository = timeTableRepository;
        this.elementModuleService = elementModuleService;
    }




    public long countTimetables() {
        return elementModuleRepository.count();
    }


    public List<Map<String, List<ElementModule>>> generateTimetable() {
        List<ElementModule> elementModules=elementModuleRepository.findAll();

        return new ArrayList<>();
    }


    public List<Map<String, List<ElementModule>>> getAllEmplois() {
        List<Map<String, List<ElementModule>>> emplois = new ArrayList<>();
        dataFromDb.loadDataFromDatabase();
        // Retrieve all classes
        List<ClassDTO> classes = DataFromDB.classes;
        for (ClassDTO classe : classes) {
            Map<String, List<ElementModule>> emploi = new HashMap<>();
            emploi.put(classe.getId(), elementModuleService.getEmploisByClasse(classe.getId()));
            emplois.add(emploi);
        }
        return emplois;
    }
    public List<ElementModule> getEmploisByClasse(String id) {
        return elementModuleService.getEmploisByClasse(id);
    }
    public List<Map<String, List<ElementModule>>> generateEmplois() {
        LOGGER.info("Generating timetable...");
        List<Map<String, List<ElementModule>>> emplois = new ArrayList<>();
        dataFromDb.loadDataFromDatabase();
        GAlgorithm algorithm = new GAlgorithm();
        UniversityTimetable universityTimetable = algorithm.generateTimetable();

        for (int i = 0; i < universityTimetable.getNumberOfClasses(); i++) {
            if (!universityTimetable.getClasses().isEmpty() && i < universityTimetable.getClasses().size()) {
                Map<String, List<ElementModule>> emploi = new HashMap<>();
                emploi.put(universityTimetable.getClasses().get(i).getId(), universityTimetable.getTimetable(i));
                emplois.add(emploi);
            }
        }

        for (ElementModule elementDeModule : universityTimetable.getAllElements()) {
            elementModuleService.addElementModule(elementDeModule);
        }

        return emplois;
    }
    public List<ElementModule> getEmploiByProf(String id) {

        User teacher = userService.getProfById(id);
        // show only  element de module of S1 ou S3 or S5

        List<ElementModule> elementModules = new ArrayList<>();
        for (ElementModule elementModule : teacher.getEducation().getElementModules()) {
            if (elementModule.getModul().getAClass().getSemester().getSemesterNumber()== SemesterNumber.S3 || elementModule.getModul().getAClass().getSemester().getSemesterNumber()== SemesterNumber.S5 || elementModule.getModul().getAClass().getSemester().getSemesterNumber()== SemesterNumber.S1) {
                elementModules.add(elementModule);
            }
        }

        return elementModules;
    }


   public String create(final TimeTableDTO timeTableDTO ) {
       final TimeTable timeTable = new TimeTable();
       mapToEntity(timeTableDTO, timeTable);
       return timeTableRepository.save(timeTable).getId();
   }
    private TimeTable mapToEntity(final TimeTableDTO timeTableDTO,
                                   final TimeTable timeTable) {
        timeTable.setName(timeTableDTO.getName());
        timeTable.setSemesters(timeTableDTO.getSemesters());
        timeTable.setDepartments(timeTableDTO.getDepartments());
        timeTable.setElementModules(timeTableDTO.getElementModules());
        timeTable.setClasse(timeTableDTO.getClasse());
        timeTable.setSchedule(timeTableDTO.getSchedule());
                ;
        return timeTable;
    }
    private TimeTableDTO mapToDTO(final TimeTable timeTable,
                                  final TimeTableDTO timeTableDTO) {
        timeTableDTO.setId( timeTable.getId());
        timeTableDTO.setDepartments(timeTable.getDepartments());
        timeTableDTO.setSemesters(timeTable.getSemesters());
        timeTableDTO.setClasse(timeTable.getClasse());
        timeTableDTO.setSchedule(timeTable.getSchedule());

        return timeTableDTO;
    }

    public TimeTableDTO getById(String id) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TimeTable not found with id: " + id));
        return mapToDTO(timeTable, new TimeTableDTO());
    }


    public void update(String id, TimeTableDTO timeTableDTO) {
        TimeTable existingTimeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TimeTable not found with id: " + id));
        TimeTable updatedTimeTable = mapToEntity(timeTableDTO, existingTimeTable);
        timeTableRepository.save(updatedTimeTable);
    }

    public void delete(String id) {
        TimeTable existingTimeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TimeTable not found with id: " + id));
        timeTableRepository.delete(existingTimeTable);
    }



}

