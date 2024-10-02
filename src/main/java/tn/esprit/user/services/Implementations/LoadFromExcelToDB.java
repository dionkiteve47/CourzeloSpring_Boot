package tn.esprit.user.services.Implementations;
import tn.esprit.user.dto.schedule.DepartmentDTO;
import tn.esprit.user.dto.schedule.FieldOfStudyDTO;
import tn.esprit.user.dto.schedule.SemesterDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.*;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.services.Implementations.*;
import tn.esprit.user.services.Implementations.UserService;
import lombok.AllArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Component
@AllArgsConstructor
public class LoadFromExcelToDB {
    private FieldOfStudyService fieldOfStudyService;
    private ClassService classService;
    private UserService userService;
    private ElementModuleService elementModuleService;
    private ModulService modulService;
    private SemesterService semesterService;
    private DepartmentService departmentService;

    public boolean PutDataToDb(String path) throws IOException {
        boolean isImported = true;
        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            // Retrieving the number of sheets in the Workbook
            int numberOfSheets = workbook.getNumberOfSheets();
            // Getting the Sheet at index zero
            List<Semester> semestres = new ArrayList<>();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().contains("TEACHERS")&& !sheet.getSheetName().contains("DEPARTMENTS") && !sheet.getSheetName().contains("FIELDS_OF_STUDY") && !sheet.getSheetName().contains("SEMESTERS")) {
                    User t = null;
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 4) {
                            for (Cell cell : row) {
                                if (cell.getColumnIndex() == 2) {
                                    t = new User();
                                    if (!cell.getStringCellValue().equals("")) {
                                        t.getProfile().setName(cell.getStringCellValue().split("_")[0]);
                                        t.getProfile().setLastName(cell.getStringCellValue().split("_")[1]);
                                    }
                                }
                                if (cell.getColumnIndex() == 3) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        assert t != null;
                                        t.getProfile().setSpeciality(cell.getStringCellValue());
                                    }
                                }
                                if (cell.getColumnIndex() == 4) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        assert t != null;
                                        t.getContact().setPhoneNumber(cell.getStringCellValue());
                                    }
                                }
                                assert t != null;
                                t.setRoles(Collections.singletonList(Role.TEACHER));
                                if (userService.findTeachersByNameAndRole(t.getId(), t.getProfile().getName(), Role.TEACHER).isEmpty()) {
                                    userService.addTeacher(t);
                                }
                            }
                        }
                    }
                }
                if (!sheet.getSheetName().contains("TEACHERS")&& !sheet.getSheetName().contains("DEPARTMENTS") && !sheet.getSheetName().contains("FIELDS_OF_STUDY") && !sheet.getSheetName().contains("SEMESTERS")){
                    Department department = new Department();
                    FieldOfStudy fieldOfStudy = new FieldOfStudy();
                    fieldOfStudy.setClasses(new ArrayList<>());
                    fieldOfStudy.setName(sheet.getRow(2).getCell(2).getStringCellValue());
                    fieldOfStudy.setChefField(sheet.getRow(2).getCell(4).getStringCellValue());
                    department.setName(sheet.getRow(2).getCell(5).getStringCellValue());
                    // Create a DataFormatter to format and get each cell's value as String
                    DataFormatter dataFormatter = new DataFormatter();
                    Semester semester = null;
                    Class classe = null;
                    Modul module = null;
                    ElementModule element;
                    int j = 5;
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 6) {
                            j++;
                            for (Cell cell : row) {
                                /*if (cell.getColumnIndex() == 6){
                                     int Nbeleve=(int) cell.getRow().getCell(7).getNumericCellValue();
                                }*/
                                if (cell.getColumnIndex() == 1) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        semester = new Semester();
                                        semester.setSemesterNumber(SemesterNumber.valueOf(cell.getStringCellValue()));
                                        semester.setUniversityYear(sheet.getRow(0).getCell(2).getStringCellValue());
                                        classe = new Class();
                                        classe.setCapacity((int) sheet.getRow(j).getCell(7).getNumericCellValue());
                                        classe.setName(fieldOfStudy.getName() + " " + semester.getSemesterNumber().toString().charAt(1));
                                        if (SemesterService.findSemestreByNum(semester.getSemesterNumber()).size() == 0) {
                                            semesterService.addSemestre(semester);
                                            classe.setSemester(semester);
                                        } else {
                                            classe.setSemester(SemesterService.findSemestreByNum(semester.getSemesterNumber()).get(0));
                                        }

                                        //(int) cell.getRow().getCell(4).getNumericCellValue();
                                        fieldOfStudy.getClasses().add(classe);
                                        if (departmentService.findDepartmentByName(department.getName()).size() == 0) {
                                            fieldOfStudy.setDepartment(department);
                                            departmentService.addDepartment(department);
                                        } else {
                                            fieldOfStudy.setDepartment(departmentService.findDepartmentByName(department.getName()).get(0));
                                        }
                                        fieldOfStudyService.addField(fieldOfStudy);
                                    }
                                }
                                if (cell.getColumnIndex() == 2) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        module = new Modul();
                                        module.setElementModules(new ArrayList<>());
                                        module.setName(cell.getStringCellValue());
                                        //module.setSemestre(semestre);
                                        assert classe != null;
                                        classe.getModuls().add(module);
                                    }
                                    assert classe != null;
                                    classe.setFieldOfStudy(fieldOfStudy);
                                    classService.addClasse(classe, fieldOfStudy.getId());
                                }
                                if (cell.getColumnIndex() == 4) {
                                    if (!cell.getStringCellValue().equals("")) {
                                        element = new ElementModule();
                                        element.setName(cell.getStringCellValue());
                                        User t = null;
                                        if (!cell.getRow().getCell(5).getStringCellValue().equals("")) {
                                            t = new User();
                                            t.getProfile().setName(cell.getRow().getCell(5).getStringCellValue().split("_")[0]);
                                            t.getProfile().setLastName(cell.getRow().getCell(5).getStringCellValue().split("_")[1]);
                                            if (userService.findTeachersByNameAndRole(t.getId(), t.getProfile().getName(), Collections.singletonList(Role.TEACHER)).size() != 0) {
                                                element.setTeacher(userService.findTeachersByNameAndRole(t.getId(), t.getProfile().getName(), Collections.singletonList(Role.TEACHER)).get(0));
                                            } else {
                                                isImported = false;
                                                System.out.println("Teacher doesn't exist in the database");
                                            }
                                        }
                                        modulService.addModul(module);
                                        element.setNmbrHours((int) cell.getRow().getCell(6).getNumericCellValue());
                                        element.setModul(module);
                                        elementModuleService.addElementModule(element);
                                        assert module != null;
                                        module.getElementModules().add(element);
                                        module.setAClass(classe);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (EncryptedDocumentException | IOException e) {
            isImported = false;
            e.printStackTrace();
        }
        return isImported;


    }

    public String exportData() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        // Create a sheet for teachers
        Sheet teachersSheet = workbook.createSheet("TEACHERS");
        // Add data to the teachers sheet
        List<User> teachers = userService.getTeachers();
        for (int i = 0; i < teachers.size(); i++) {
            User teacher = teachers.get(i);
            Row row = teachersSheet.createRow(i + 4);
            row.createCell(2).setCellValue(teacher.getProfile().getName() + "_" + teacher.getProfile().getLastName());
            row.createCell(3).setCellValue(teacher.getProfile().getSpeciality());
            row.createCell(4).setCellValue(teacher.getContact().getPhoneNumber());
        }
        Sheet departmentsSheet = workbook.createSheet("DEPARTMENTS");

        // Fetch all departments from the application
        List<DepartmentDTO> departments = departmentService.findAll();

        // Iterate over the departments and add them to the sheet
        for (int i = 0; i < departments.size(); i++) {
            DepartmentDTO department = departments.get(i);

            // Create a new row for the department
            Row row = departmentsSheet.createRow(i + 4);

            // Add the department's name to the row
            row.createCell(2).setCellValue(department.getName());
        }

        // Create a new sheet for fields of study
        Sheet fieldsOfStudySheet = workbook.createSheet("FIELDS_OF_STUDY");

        // Fetch all fields of study from the application
        List<FieldOfStudyDTO> fieldsOfStudy = fieldOfStudyService.findAll();

        // Iterate over the fields of study and add them to the sheet
        for (int i = 0; i < fieldsOfStudy.size(); i++) {
            FieldOfStudyDTO fieldOfStudy = fieldsOfStudy.get(i);

            // Create a new row for the field of study
            Row row = fieldsOfStudySheet.createRow(i + 4);

            // Add the field of study's name and chef field to the row
            row.createCell(2).setCellValue(fieldOfStudy.getName());
            row.createCell(3).setCellValue(fieldOfStudy.getChefField());
        }

        // Create a new sheet for semesters
        Sheet semestersSheet = workbook.createSheet("SEMESTERS");

        // Fetch all semesters from the application
        List<SemesterDTO> semesters = semesterService.findAll();

        // Iterate over the semesters and add them to the sheet
        for (int i = 0; i < semesters.size(); i++) {
            SemesterDTO semester = semesters.get(i);

            // Create a new row for the semester
            Row row = semestersSheet.createRow(i + 4);

            // Add the semester's number and university year to the row
            row.createCell(2).setCellValue(semester.getSemesterNumber().toString());
            row.createCell(3).setCellValue(semester.getUniversityYear());
        }
        String filename = "GeneratedExcelFile_" + System.currentTimeMillis() + ".xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(filename)) {
            workbook.write(outputStream);
            workbook.close();
        } catch(IOException e){
            // Log the error message
            System.err.println("An error occurred while exporting data: " + e.getMessage());
        }

        return filename;
    }
    public ResponseEntity<InputStreamResource> downloadGeneratedExcelFile() throws IOException {
        String filename = exportData();
        return downloadExcelFile(filename);
    }
    public ResponseEntity<InputStreamResource> downloadExcelFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

}

