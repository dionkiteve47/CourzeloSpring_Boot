package tn.esprit.user.services.Implementations;


import tn.esprit.user.dto.program.CalendarDTO;
import tn.esprit.user.dto.program.InstitutionDTO;
import tn.esprit.user.dto.program.InstitutionListDTO;
import tn.esprit.user.dto.program.InstitutionUsersCountDTO;
import tn.esprit.user.dtos.UserDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.institution.Program;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.exceptions.ClassNotFoundException;
import tn.esprit.user.exceptions.InstitutionNotFoundException;
import tn.esprit.user.repositories.ClassRepository;
import tn.esprit.user.repositories.InstitutionRepository;
import tn.esprit.user.repositories.ProgramRepository;
import tn.esprit.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tn.esprit.user.services.Interfaces.IInstitutionService;
import tn.esprit.user.services.Interfaces.IProgramService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionService implements IInstitutionService {
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final IProgramService iProgramService;
    private final ClassRepository classRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final String FILE_PATH = "school-year-calendar.xlsx"; // Change this to your desired file path

    @Override
    public ResponseEntity<InstitutionListDTO> getInstitutions(int page, int sizePerPage) {
        log.info("Getting all institutions");
        Pageable pageable = PageRequest.of(page, sizePerPage);
        long totalItems = institutionRepository.count();
        log.info("total institutions : " + totalItems);
        int totalPages = (int) Math.ceil((double) totalItems / sizePerPage);
        log.info("total pages : " + totalPages);
        List<InstitutionDTO> institutionDTO = institutionRepository.findAll(pageable)
                .stream()
                .map(institution -> modelMapper.map(institution, InstitutionDTO.class))
                .toList();
        log.info("institutions in page: " + page + " " + institutionDTO);
        InstitutionListDTO institutionListDTO = new InstitutionListDTO(institutionDTO, totalPages);
        return ResponseEntity
                .ok()
                .body(institutionListDTO);
    }

    @Override
    public ResponseEntity<HttpStatus> generateExcel(List<CalendarDTO> events, Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null && user.getEducation().getInstitution() != null) {
            String institutionId = user.getEducation().getInstitution().getId();
            Institution institution = institutionRepository.findById(institutionId)
                    .orElseThrow(()->new InstitutionNotFoundException("Institution " + institutionId + " not found"));
            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                createCalendarSheet(workbook, events);
                workbook.write(outputStream);
                institution.setExcelFile(outputStream.toByteArray());
                institutionRepository.save(institution);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private void createCalendarSheet(Workbook workbook, List<CalendarDTO> events) {
        Sheet sheet = workbook.createSheet("School Year Calendar");
        CellStyle cellStyle = createCellStyle(workbook);
        String[] MONTHS = {
                "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"
        };
        CellStyle headerStyle = createHeaderStyle(workbook);
        Font font = workbook.createFont();
        styleMonthHeader(headerStyle, font);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < MONTHS.length; i++) {
            int startColIndex = i * 3;
            int endColIndex = (i * 3) + 2;
            sheet.addMergedRegion(new CellRangeAddress(0, 0, startColIndex, endColIndex));
            Cell headerCell = headerRow.createCell(startColIndex);
            headerCell.setCellValue(MONTHS[i]);
            headerCell.setCellStyle(headerStyle);
            createDaysInMonth(sheet, i, cellStyle);
            createEvents(events, sheet, i, workbook,font);
        }
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    private CellStyle createEventStyle(Workbook workbook, CalendarDTO event) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        java.awt.Color awtColor = java.awt.Color.decode(event.getColor());
        XSSFColor color = new XSSFColor(new java.awt.Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()), new DefaultIndexedColorMap());
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    private void styleEvent(CellStyle style, Font font) {
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
    }
    private void styleMonthHeader(CellStyle style, Font font) {
        font.setBold(true);
        font.setFontHeightInPoints((short) 15); 
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
    }
    private void createDaysInMonth(Sheet sheet, int monthIndex, CellStyle style) {
        int daysInMonth = YearMonth.now().withMonth(monthIndex + 1).lengthOfMonth();
        for (int j = 1; j <= daysInMonth; j++) {
            Row daysRow = sheet.getRow(j);
            if (daysRow == null) {
                daysRow = sheet.createRow(j);
            }
            Cell daysCell = daysRow.createCell(monthIndex * 3);
            daysCell.setCellValue(j);
            daysCell.setCellStyle(style);
        }
    }

    private void createEvents(List<CalendarDTO> events, Sheet sheet, int monthIndex, Workbook workbook,Font font) {
        Calendar cal = Calendar.getInstance();
        for (CalendarDTO event : events) {
            cal.setTime(event.getStartDate());
            Calendar finishCal = Calendar.getInstance();
            finishCal.setTime(event.getFinishDate());
            if (cal.get(Calendar.MONTH)+1 == monthIndex + 1) {
                int startRowIndex = cal.get(Calendar.DAY_OF_MONTH);
                int endRowIndex = finishCal.get(Calendar.DAY_OF_MONTH);
                for (int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
                    int columnIndex = monthIndex * 3 + 1;
                    Row eventRow = sheet.getRow(rowIndex);
                    if (eventRow == null) {
                        eventRow = sheet.createRow(rowIndex);
                    }
                    Cell eventCell = eventRow.createCell(columnIndex);
                    eventCell.setCellValue(event.getName());
                    CellStyle eventStyle = createEventStyle(workbook, event);
                    styleEvent(eventStyle, font);
                    eventCell.setCellStyle(eventStyle);
                }
                // Merge the cells after creating them
                sheet.addMergedRegion(new CellRangeAddress(startRowIndex, endRowIndex, monthIndex * 3 + 1, monthIndex * 3 + 2));
            }
        }
    }


    @Override
    public ResponseEntity<InstitutionDTO> getInstitutionByID(String institutionID) {
        log.info("Get institution by id :" + institutionID);
        Institution institution = institutionRepository.findById(institutionID)
                .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionID + " not found"));
        return ResponseEntity
                .ok()
                .body(modelMapper.map(institution, InstitutionDTO.class));
    }

    @Override
    public ResponseEntity<InstitutionDTO> getInstitution(String email) {
        log.info("Get institution for user :" + email);
        User user = userRepository.findUserByEmail(email);
        if (user != null && user.getEducation().getInstitution() != null) {
            if (user.getRoles().contains(Role.ADMIN)) {
                return ResponseEntity.ok().body(modelMapper.map(user.getEducation().getInstitution(), InstitutionDTO.class));
            } else {
                InstitutionDTO simplifiedDTO = new InstitutionDTO();
                simplifiedDTO.setId(user.getEducation().getInstitution().getId());
                simplifiedDTO.setName(user.getEducation().getInstitution().getName());
                simplifiedDTO.setDescription(user.getEducation().getInstitution().getDescription());
                simplifiedDTO.setLocation(user.getEducation().getInstitution().getLocation());
                simplifiedDTO.setWebsite(user.getEducation().getInstitution().getWebsite());
                return ResponseEntity.ok().body(simplifiedDTO);
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<Boolean> deleteInstitution(String institutionID) {
        log.info("Delete institution :" + institutionID);
        Institution institution = institutionRepository.findById(institutionID)
                .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionID + " not found"));
        if (institution != null) {
            List<User> users = userRepository.findByEducationInstitution(institution);
            for (User user : users) {
                user.getEducation().setInstitution(null);
                userRepository.save(user);
            }
            List<Program> programs = programRepository.findByInstitution(institution);
            for (Program program : programs) {
                iProgramService.deleteProgramChain(program);
            }
            institutionRepository.deleteById(institutionID);
            return ResponseEntity.ok().body(true);
        }
        return ResponseEntity.badRequest().body(false);
    }

    @Override
    public ResponseEntity<Boolean> addInstitution(InstitutionDTO institutionDTO) {
        log.info("Adding institution ");
        Institution institution = modelMapper.map(institutionDTO, Institution.class);
        Institution savedInstitution = institutionRepository.save(institution);

        if (savedInstitution.getId() != null) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @Override
    public ResponseEntity<Boolean> updateInstitution(InstitutionDTO institutionDTO) {
        log.info("Update institution :" + institutionDTO.getId());
        Institution institution = institutionRepository.findById(institutionDTO.getId())
                .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionDTO.getId() + " not found"));
        institution.setName(institutionDTO.getName());
        institution.setWebsite(institutionDTO.getWebsite());
        institution.setLocation(institutionDTO.getLocation());
        institution.setDescription(institutionDTO.getDescription());
        Institution savedInstitution = institutionRepository.save(institution);

        if (savedInstitution.getId() != null) {
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }


    @Override
    public ResponseEntity<Boolean> addUserToInstitution(String institutionID, String userEmail, String role, Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        log.info("institution id : " + institutionID);
        User target = userRepository.findUserByEmail(userEmail);
        if (user.getRoles().contains(Role.SUPERADMIN) && !Objects.equals(institutionID, "")) {
            Institution institution = institutionRepository.findById(institutionID)
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionID + " not found"));
            if (!isAdminInInstitution(principal, institutionID)) {
                log.info("Not authorized to manage this institution");
                return ResponseEntity.badRequest().body(false);
            }
            return addUser(role, target, institution);
        } else {
            Institution institution = institutionRepository.findById(user.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + user.getEducation().getInstitution().getId() + " not found"));
            if (!isAdminInInstitution(principal, institution.getId())) {
                log.info("Not authorized to manage this institution");
                return ResponseEntity.badRequest().body(false);
            }
            return addUser(role, target, institution);
        }
    }

    private ResponseEntity<Boolean> addUser(String role, User target, Institution institution) {
        if (target != null) {
            switch (role) {
                case "Admins":
                    if (!institution.getAdmins().contains(target) && target.getEducation().getInstitution() == null) {
                        institution.getAdmins().add(target);
                        target.getEducation().setInstitution(institution);
                        if (!target.getRoles().contains(Role.ADMIN)) {
                            target.getRoles().add(Role.ADMIN);
                        }
                        userRepository.save(target);
                        institutionRepository.save(institution);
                        log.info("Admin added !");
                        return ResponseEntity.ok().body(true);
                    } else if (institution.getAdmins().contains(target)) {
                        log.info("Admin already added !");
                        return ResponseEntity.ok().body(true);
                    }
                    break;
                case "Teachers":
                    if (!institution.getTeachers().contains(target) && target.getEducation().getInstitution() == null) {
                        institution.getTeachers().add(target);
                        target.getEducation().setInstitution(institution);
                        if (!target.getRoles().contains(Role.TEACHER)) {
                            target.getRoles().add(Role.TEACHER);
                        }
                        userRepository.save(target);
                        institutionRepository.save(institution);
                        log.info("teacher added !");
                        return ResponseEntity.ok().body(true);
                    } else if (institution.getTeachers().contains(target)) {
                        log.info("teacher already added !");
                        return ResponseEntity.ok().body(true);
                    }
                    break;
                case "Students":
                    if (!institution.getStudents().contains(target) && target.getEducation().getInstitution() == null) {
                        institution.getStudents().add(target);
                        target.getEducation().setInstitution(institution);
                        if (!target.getRoles().contains(Role.STUDENT)) {
                            target.getRoles().add(Role.STUDENT);
                        }
                        userRepository.save(target);
                        institutionRepository.save(institution);
                        log.info("Student added !");
                        return ResponseEntity.ok().body(true);
                    } else if (institution.getStudents().contains(target)) {
                        log.info("Student already added !");
                        return ResponseEntity.ok().body(true);
                    }
                    break;
                default:
                    ResponseEntity.badRequest().body(false);
                    break;
            }

        }
        return ResponseEntity.badRequest().body(false);
    }

    @Override
    public ResponseEntity<Boolean> removeUser(String institutionID, String userEmail, Principal principal) {
        log.info("id institution : " + institutionID);
        User user = userRepository.findUserByEmail(principal.getName());
        User userToBeRemoved = userRepository.findUserByEmail(userEmail);

        Institution institution;

        if (user.getRoles().contains(Role.SUPERADMIN) && institutionID != null) {
            log.info("User is Superadmin");
            institution = institutionRepository.findById(institutionID)
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionID + " not found"));
        } else {
            institution = user.getEducation().getInstitution();
            log.info("User is admin");
            if (institution == null) {
                log.info("Institution not found ");
                throw new InstitutionNotFoundException("User's institution not found");
            }
        }

        if (!isAdminInInstitution(principal, institution.getId())) {
            log.info("Not authorized to manage this institution");
            return ResponseEntity.badRequest().body(false);
        }

        if (institution.getAdmins().contains(userToBeRemoved)) {
            institution.getAdmins().remove(userToBeRemoved);
            userToBeRemoved.getEducation().setInstitution(null);
            userToBeRemoved.getRoles().remove(Role.ADMIN);
        } else if (institution.getTeachers().contains(userToBeRemoved)) {
            institution.getTeachers().remove(userToBeRemoved);
            userToBeRemoved.getEducation().setInstitution(null);
            userToBeRemoved.getRoles().remove(Role.TEACHER);
            checkIfUserInClassAndRemove(userToBeRemoved);
        } else if (institution.getStudents().contains(userToBeRemoved)) {
            institution.getStudents().remove(userToBeRemoved);
            userToBeRemoved.getEducation().setInstitution(null);
            userToBeRemoved.getRoles().remove(Role.STUDENT);
            checkIfUserInClassAndRemove(userToBeRemoved);
        } else {
            log.info("all conditions = false ");
            return ResponseEntity.badRequest().body(false);
        }

        institutionRepository.save(institution);
        userRepository.save(userToBeRemoved);
        return ResponseEntity.ok().body(true);
    }


    @Override
    public ResponseEntity<Boolean> updateMyInstitution(InstitutionDTO institutionDTO, Principal principal) {
        log.info("Update institution :" + institutionDTO.getId());
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null && user.getEducation().getInstitution() != null) {

            Institution institution = institutionRepository.findById(user.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionDTO.getId() + " not found"));
            isAdminInInstitution(principal, institution.getId());
            institution.setDescription(institutionDTO.getDescription());
            institution.setName(institutionDTO.getName());
            institution.setLocation(institutionDTO.getLocation());
            institution.setWebsite(institutionDTO.getWebsite());
            Institution savedInstitution = institutionRepository.save(institution);

            if (savedInstitution.getId() != null) {
                return ResponseEntity.ok().body(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    @Override
    public ResponseEntity<InstitutionUsersCountDTO> countUsers(Principal principal) {
        log.info("counting users");
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null && user.getEducation().getInstitution() != null) {
            Institution institution = institutionRepository.findById(user.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + user.getEducation().getInstitution().getId() + " not found"));
            isAdminInInstitution(principal, institution.getId());
            InstitutionUsersCountDTO institutionUsersCountDTO = new InstitutionUsersCountDTO(
                    institution.getAdmins().size(),
                    institution.getTeachers().size(),
                    institution.getStudents().size());
            log.info(String.valueOf(institutionUsersCountDTO));
            return ResponseEntity.ok().body(institutionUsersCountDTO);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @Override
    public ResponseEntity<UserListDTO> getInstitutionUsers(String institutionID, Principal principal, String role, int page, int sizePerPage) {
        log.info("role = " + role);
        log.info("page = " + page);
        log.info("sizeperpage = " + sizePerPage);

        User userr = userRepository.findUserByEmail(principal.getName());
        if (userr.getRoles().contains(Role.SUPERADMIN) && !Objects.equals(institutionID, "")) {
            Institution institution = institutionRepository.findById(institutionID)
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + userr.getEducation().getInstitution().getId() + " not found"));
            return getUserListDTOResponseEntity(institution, principal, role, page, sizePerPage);
        } else if (userr.getEducation().getInstitution() != null) {
            Institution institution = institutionRepository.findById(userr.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + userr.getEducation().getInstitution().getId() + " not found"));
            return getUserListDTOResponseEntity(institution, principal, role, page, sizePerPage);
        }
        log.info("find users condition failed ");
        return ResponseEntity.badRequest().body(null);
    }

    @Override
    public ResponseEntity<InstitutionDTO> getMyInstitution(Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        if (user.getEducation().getInstitution() != null) {
            log.info("Getting institution ");
            Institution institution = institutionRepository.findById(user.getEducation().getInstitution().getId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + user.getEducation().getInstitution().getId() + " not found"));
            return ResponseEntity
                    .ok()
                    .body(modelMapper.map(institution, InstitutionDTO.class));
        }
        return ResponseEntity.badRequest().body(null);
    }
    public ResponseEntity<byte[]> downloadExcel(Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null && user.getEducation().getInstitution() != null) {
            String institutionId = user.getEducation().getInstitution().getId();
            Institution institution = institutionRepository.findById(institutionId)
                    .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionId + " not found"));
            byte[] excelFile = institution.getExcelFile();
            if (excelFile != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=school-year-calendar.xlsx");
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(excelFile);
            }
        }
        return ResponseEntity.badRequest().build();
    }
    @Override
    public ResponseEntity<Boolean> saveLocation(InstitutionDTO institutionDTO) {
        log.info("Saving location "+institutionDTO);
        Institution institution = institutionRepository.findById(institutionDTO.getId()).orElse(null);
        if (institution != null) {
            institution.setLatitude(institutionDTO.getLatitude());
            institution.setLongitude(institutionDTO.getLongitude());
            institutionRepository.save(institution);
            log.info("Location saved");
            return ResponseEntity.ok().body(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    private ResponseEntity<UserListDTO> getUserListDTOResponseEntity(Institution institution, Principal principal, String role, int page, int sizePerPage) {

        isAdminInInstitution(principal, institution.getId());
        log.info("Getting institution Users: " + institution.getId());
        List<User> users;
        if (Objects.equals(role, "Admins")) {
            users = institution.getAdmins();
        } else if (Objects.equals(role, "Teachers")) {
            users = institution.getTeachers();
        } else {
            users = institution.getStudents();
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

    private boolean isAdminInInstitution(Principal principal, String institutionID) {
        User user = userRepository.findUserByEmail(principal.getName());
        Institution institution = institutionRepository.findById(institutionID)
                .orElseThrow(() -> new InstitutionNotFoundException("Institution " + institutionID + " not found"));
        return institution.getAdmins().contains(user) || user.getRoles().contains(Role.SUPERADMIN);
    }

    private void checkIfUserInClassAndRemove(User user) {
        if (user.getEducation().getStclass() != null) {
            Class aClass = classRepository.findById(user.getEducation().getStclass().getId())
                    .orElseThrow(() -> new ClassNotFoundException("Class not found"));
            if (aClass.getStudents().contains(user)) {
                aClass.getStudents().remove(user);
                user.getEducation().setStclass(null);
            } else {
                aClass.getTeachers().remove(user);
                user.getEducation().setStclass(null);
            }
            classRepository.save(aClass);
            userRepository.save(user);
        }
    }

}
