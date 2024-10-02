package tn.esprit.user.services.Implementations;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.Department;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.FieldOfStudy;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.ElementModuleRepository;
import tn.esprit.user.services.Implementations.ClassService;
import com.lowagie.text.FontFactory;
import com.lowagie.text.alignment.HorizontalAlignment;
import tn.esprit.user.services.Implementations.UserService;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;  
import com.lowagie.text.*;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.security.Principal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
@Service
@AllArgsConstructor
public class PdfExportService {
    private final DataFromDB dataFromDB;
    private final ElementModuleRepository elementModuleRepository;
    private final ClassService classService;
    private DepartmentService departmentService;
    private final UserService userService;
    List<DayOfWeek> days;


    public void ProfPDF(HttpServletResponse response, String id) throws IOException {
        dataFromDB.loadDataFromDatabase();
        Period[] periods = Period.values();
        Document myPDFDoc = new Document(PageSize.A4,
                40f,
                40f,
                70f,
                70f);
        {

            final PdfWriter pdfWriter = PdfWriter.getInstance(myPDFDoc, response.getOutputStream());
            myPDFDoc.open();
            User teacher = userService.getProfById(id);

            AddPageProf(myPDFDoc, teacher);


            myPDFDoc.close();
            pdfWriter.close();
        }


    }
    public void AddPageProf(Document myPDFDoc, User teacher) throws IOException {
        Period[] periods = Period.values();
        days = new ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        days.add(DayOfWeek.SATURDAY);
        String title = teacher.getProfile().getName()+" "+teacher.getProfile().getLastName();
        Table myTable = new Table(5); // 3 columns
        FontFactory.register("Fonts/QuattrocentoSans-Italic.ttf");
        FontFactory.register("Fonts/Calibri Regular.ttf");
        Font Calibri1 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10,Font.BOLD);
        Font Calibri2 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10,Font.BOLD);
        Font Calibri3 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10);
        Font Quatt = FontFactory.getFont("Quattrocento Sans Italic", BaseFont.WINANSI, 9,Font.ITALIC, Color.BLUE);
        float[] columnWidths = { 25f, 40f, 40f, 40f, 40f }; // Adjust the values as needed
        myTable.setWidths(columnWidths);
        myTable.setPadding(2f);
        myTable.setWidth(100f);
        ArrayList<String> headerTable = new ArrayList<>();
        headerTable.add("");
        headerTable.add("08h:30 - 10h:30");
        headerTable.add("10h:30 - 12h:30");
        headerTable.add("14h - 16h");
        headerTable.add("16h - 18h");
        headerTable.forEach(e -> {
            Paragraph paragraph = new Paragraph(e,Calibri1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            Cell current = new Cell(paragraph);
            current.setHeader(true);
            current.setHorizontalAlignment(HorizontalAlignment.CENTER);
            current.setBackgroundColor(Color.LIGHT_GRAY);
            myTable.addCell(current);
        });

        LinkedHashMap<Integer, List<List<Paragraph>>> listRows = new LinkedHashMap<>();
        int rowNumber = 1;
        for(DayOfWeek day:days){
            List<Paragraph> CellList = new ArrayList<>();
            List<List<Paragraph>> dayList = new ArrayList<>();
            Paragraph Day;
            if(rowNumber==1){
                Day = new Paragraph("\n\nMonday\n\n\n",Calibri2);
            } else if (rowNumber==2) {
                Day = new Paragraph("\n\nTuesday\n\n\n",Calibri2);
            } else if (rowNumber==3) {
                Day = new Paragraph("\n\nWednesday\n\n\n",Calibri2);
            } else if (rowNumber==4) {
                Day = new Paragraph("\n\nTuesday\n\n\n",Calibri2);
            } else if (rowNumber==5){
                Day = new Paragraph("\n\nFriday\n\n\n",Calibri2);
            }else{
                Day = new Paragraph("\n\nSaturday\n\n\n",Calibri2);
            }
            Day.setAlignment(Element.ALIGN_CENTER);
            ArrayList<Paragraph> dayCell = new ArrayList<>();
            dayCell.add(Day);
            dayList.add(dayCell);
            for(Period p :periods){
                CellList = new ArrayList<>();
                List<ElementModule> elms = elementModuleRepository.findByDayOfWeekAndPeriodAndTeacher(day,p,teacher);
                System.out.println("Elements "+elms.size());
                if(elms.size()>0){
                    Paragraph Libele = new Paragraph(elms.get(0).getName()+" ("+elms.get(0).getNmbrHours()+")",Calibri1);
                    Libele.setAlignment(Element.ALIGN_CENTER);
                    Paragraph classe = new Paragraph(elms.get(0).getModul().getAClass().getName(),Calibri3);
                    classe.setAlignment(Element.ALIGN_CENTER);
                    CellList.add(Libele);
                    CellList.add(classe);
                }
                else{
                    CellList.add(new Paragraph(""));
                }
                dayList.add(CellList);

            }
            System.out.println(dayList);
            listRows.put(rowNumber, dayList);
            rowNumber++;
        }
        listRows.forEach((index,userDetailRow) -> {
            userDetailRow.forEach(paragraphs -> {
                Cell cell = new Cell();
                paragraphs.forEach(paragraph ->{
                    cell.add(paragraph);
                    cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cell.setVerticalAlignment(VerticalAlignment.CENTER);
                });
                myTable.addCell(cell);
            });
        });
        myPDFDoc.addTitle("Prof Timetable");
        myPDFDoc.addSubject("Esprit Timetable");
        myPDFDoc.addKeywords("ESPRIT");
        myPDFDoc.addCreator("ESPRIT");
        myPDFDoc.addAuthor("ESPRIT");
        FontFactory.register("Fonts/COMIC.ttf");
        FontFactory.register("Fonts/times new roman.ttf");
        Font font1 = FontFactory.getFont("Comic Sans MS", 14, Font.UNDERLINE);
        Font font2 = FontFactory.getFont("Times New Roman", 14);
        Font font3 = FontFactory.getFont("Times New Roman", 12);
        Font font4 = FontFactory.getFont("Calibri", 20,Font.BOLD);
        Font font5 = FontFactory.getFont("Calibri", 18,Font.BOLD);
        Font font6 = FontFactory.getFont("Calibri", 16, Font.BOLD,Color.RED);
        int year = new Date().getYear();
        String text1 = "Teacher : "+teacher.getProfile().getName() +" "+teacher.getProfile().getLastName();
        String text2 = "Timetable";
        String text3 = Integer.toString(year+1900)+" / "+Integer.toString(year+1+1900);
        String text6 = "Temporary";
        Paragraph paragraph1 = new Paragraph(text1,font1);
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph2 = new Paragraph(text2,font2);
        paragraph2.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph3 = new Paragraph(text3,font3);
        paragraph3.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph6 = new Paragraph(text6,font6);
        paragraph6.setAlignment(Element.ALIGN_CENTER);
        Image headerImage = Image.getInstance("");
        Image footerImage = Image.getInstance("");
        float headerWidth = PageSize.A4.getWidth();
        float headerHeight = 50f;  // Adjust the height as needed
        float footerWidth = PageSize.A4.getWidth();
        float footerHeight = 50f;  // Adjust the height as needed
        Rectangle headerRect = new Rectangle(headerWidth, headerHeight);
        Rectangle footerRect = new Rectangle(footerWidth, footerHeight);
        headerImage.setAbsolutePosition(0, PageSize.A4.getHeight() - headerHeight - 10f);
        headerImage.scaleToFit(headerWidth, headerHeight);
        myPDFDoc.add(headerImage);
        footerImage.setAbsolutePosition(0, 10f);
        footerImage.scaleToFit(footerWidth, footerHeight);footerImage.scaleToFit(footerWidth, footerHeight);
        myPDFDoc.add(footerImage);
        myPDFDoc.add(paragraph1);
        myPDFDoc.add(paragraph2);
        myPDFDoc.add(paragraph3);
        myPDFDoc.add(paragraph6);
        myPDFDoc.add(new Paragraph(Chunk.NEWLINE));
        myPDFDoc.add(myTable);
        myPDFDoc.newPage();
    }
    public void AllProfsPDF(HttpServletResponse response) throws IOException {
        dataFromDB.loadDataFromDatabase();

        Document myPDFDoc = new Document(PageSize.A4,
                40f,   // left
                40f,   // right
                70f,  // top
                70f); // down
        final PdfWriter pdfWriter = PdfWriter.getInstance(myPDFDoc, response.getOutputStream());
        myPDFDoc.open();  // Open the Document
        for(User teacher:DataFromDB.teachers){
            AddPageProf(myPDFDoc, teacher);
        }
        myPDFDoc.close();
        pdfWriter.close();
    }

    public void DepartementsPDF(HttpServletResponse response, String id) throws IOException {
        dataFromDB.loadDataFromDatabase();
        Document myPDFDoc = new Document(PageSize.A4,
                40f,   // left
                40f,   // right
                70f,  // top
                70f); // down
        final PdfWriter pdfWriter = PdfWriter.getInstance(myPDFDoc, response.getOutputStream());
        Department departement = departmentService.getDepartmentById(id);
        myPDFDoc.open();  // Open the Document
        for(FieldOfStudy fieldOfStudy :departement.getFieldOfStudies()){
            for(Class aClass :fieldOfStudy.getClasses()){
                // Convert Class to ClassDTO
                ClassDTO classDTO = classService.mapToDTO(aClass);
                AddPageClasse(myPDFDoc, classDTO);
            }
        }
        myPDFDoc.close();
        pdfWriter.close();
    }
    public void OneClassePDF(HttpServletResponse response, String id) throws IOException {
        dataFromDB.loadDataFromDatabase();
        // Retrieve all classes
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ResponseEntity<ClassDTO> responseEntity = classService.getMyClass1(authentication, user.getEmail());
        ClassDTO classe = responseEntity.getBody();
        Document myPDFDoc = new Document(PageSize.A4,
                40f,   // left
                40f,   // right
                70f,  // top
                70f); // down
        final PdfWriter pdfWriter = PdfWriter.getInstance(myPDFDoc, response.getOutputStream());
        myPDFDoc.open();  // Open the Document
        AddPageClasse(myPDFDoc,classe);
        myPDFDoc.close();
        pdfWriter.close();
    }
    public void ClassesPDF(HttpServletResponse response) throws IOException {
        dataFromDB.loadDataFromDatabase();
        Document myPDFDoc = new Document(PageSize.A4,
                40f,   // left
                40f,   // right
                70f,  // top
                70f); // down
        final PdfWriter pdfWriter = PdfWriter.getInstance(myPDFDoc, response.getOutputStream());
        myPDFDoc.open();  // Open the Document
        for(ClassDTO classe :DataFromDB.classes){
            AddPageClasse(myPDFDoc,classe);
        }
        myPDFDoc.close();
        pdfWriter.close();
    }
    public void AddPageClasse(Document myPDFDoc, ClassDTO classe) throws IOException {
        // Set TimePeriods in Timetable
        Period[] periods = Period.values();
        days = new ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        // Define a string as title
        String title = classe.getName();
        //1) Let's create a Table object
        Table myTable = new Table(5); // 3 columns
        FontFactory.register("Fonts/QuattrocentoSans-Italic.ttf");
        FontFactory.register("Fonts/Calibri Regular.ttf");
        Font Calibri1 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10,Font.BOLD);
        Font Calibri2 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10,Font.BOLD);
        Font Calibri3 = FontFactory.getFont("Calibri", BaseFont.WINANSI, 10);
        Font Quatt = FontFactory.getFont("Quattrocento Sans Italic", BaseFont.WINANSI, 9,Font.ITALIC, Color.BLUE);
        float[] columnWidths = { 25f, 40f, 40f, 40f, 40f }; // Adjust the values as needed
        myTable.setWidths(columnWidths);
        myTable.setPadding(2f);
        myTable.setWidth(100f);
        //2) Create the header of the table
        ArrayList<String> headerTable = new ArrayList<>();
        headerTable.add("");
        headerTable.add("08h:30 - 10h:30");
        headerTable.add("10h:30 - 12h:30");
        headerTable.add("14h - 16h");
        headerTable.add("16h - 18h");
        headerTable.forEach(e -> {
            Paragraph paragraph = new Paragraph(e,Calibri1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            Cell current = new Cell(paragraph);
            current.setHeader(true);
            current.setHorizontalAlignment(HorizontalAlignment.CENTER);
            current.setBackgroundColor(Color.LIGHT_GRAY);
            myTable.addCell(current);
        });
        // 3) Then create a list of rows and add them to the table
        LinkedHashMap<Integer, List<List<Paragraph>>> listRows = new LinkedHashMap<>();
        int rowNumber = 1;
        for(DayOfWeek day:days){
            List<Paragraph> CellList = new ArrayList<>();
            List<List<Paragraph>> dayList = new ArrayList<>();
            Paragraph Day;
            if(rowNumber==1){
                Day = new Paragraph("\n\nMonday\n\n\n",Calibri2);
            } else if (rowNumber==2) {
                Day = new Paragraph("\n\nTuesday\n\n\n",Calibri2);
            } else if (rowNumber==3) {
                Day = new Paragraph("\n\nWednesday\n\n\n",Calibri2);
            } else if (rowNumber==4) {
                Day = new Paragraph("\n\nThursday\n\n\n",Calibri2);
            } else{
                Day = new Paragraph("\n\nFriday\n\n\n",Calibri2);
            }
            Day.setAlignment(Element.ALIGN_CENTER);
            ArrayList<Paragraph> dayCell = new ArrayList<>();
            dayCell.add(Day);
            dayList.add(dayCell);

            for(Period p :periods){
                CellList = new ArrayList<>();


                List<ElementModule> elms = elementModuleRepository.findByDayOfWeekAndPeriodAndClasses(day,p, classe.getId());
                System.out.println("Elements "+elms.size());
                if(elms.size()>0){
                    Paragraph Libele = new Paragraph(elms.get(0).getName()+" ("+elms.get(0).getNmbrHours()+")",Calibri1);
                    Libele.setAlignment(Element.ALIGN_CENTER);
                    Paragraph Prof = new Paragraph(elms.get(0).getTeacher().getProfile().getName()+" "+elms.get(0).getTeacher().getProfile().getLastName(),Calibri3);
                    Prof.setAlignment(Element.ALIGN_CENTER);
                    CellList.add(Libele);
                    CellList.add(Prof);
                }
                else{
                    CellList.add(new Paragraph(""));
                }
                dayList.add(CellList);
            }
            System.out.println(dayList);
            listRows.put(rowNumber, dayList);
            rowNumber++;
        }
        Paragraph samedi = new Paragraph("\n\nSamedi\n\n\n",Calibri2);
        List<Paragraph> Cellist = new ArrayList<>();
        Cellist.add(samedi);
        List<List<Paragraph>> DayList = new ArrayList<>();
        DayList.add(Cellist);
        Cellist = new ArrayList<>();
        Cellist.add(new Paragraph("\nControles \net ratrapages",Calibri1));
        DayList.add(Cellist);
        Cellist = new ArrayList<>();
        Cellist.add(new Paragraph("\nControles \net ratrapages",Calibri1));
        DayList.add(Cellist);
        Cellist = new ArrayList<>();
        Cellist.add(new Paragraph(""));
        DayList.add(Cellist);
        listRows.put(6, DayList);
        listRows.forEach((index,userDetailRow) -> {
            userDetailRow.forEach(paragraphs -> {
                Cell cell = new Cell();
                paragraphs.forEach(paragraph ->{
                    cell.add(paragraph);
                    cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cell.setVerticalAlignment(VerticalAlignment.CENTER);
                });
                myTable.addCell(cell);
            });
        });
        /* Here we add some metadata to the generated pdf */
        myPDFDoc.addTitle("Timetable for "+classe.getName());
        myPDFDoc.addSubject("Timetable for "+classe.getName());
        myPDFDoc.addKeywords("ESPRIT");
        myPDFDoc.addCreator("ESPRIT");
        myPDFDoc.addAuthor("ESPRIT");
        /* End of the adding metadata section */
        // Create a Font object
        FontFactory.register("Fonts/COMIC.ttf");
        FontFactory.register("Fonts/times new roman.ttf");
        Font font1 = FontFactory.getFont("Comic Sans MS", 14, Font.UNDERLINE);
        Font font2 = FontFactory.getFont("Times New Roman", 14);
        Font font3 = FontFactory.getFont("Times New Roman", 12);
        Font font4 = FontFactory.getFont("Calibri", 20,Font.BOLD);
        Font font5 = FontFactory.getFont("Calibri", 18,Font.BOLD);
        Font font6 = FontFactory.getFont("Calibri", 16, Font.BOLD,Color.RED);
        int year = new Date().getYear();
        String text1 = (classe.getFieldOfStudy() != null && classe.getFieldOfStudy().getDepartment() != null) ? "Departement : " + classe.getFieldOfStudy().getDepartment().getName() : "Departement : ";
        String text2 = (classe.getFieldOfStudy() != null) ? "Field of Study: " + classe.getFieldOfStudy().getName() : "Field of Study: ";
        String text3 = (classe != null) ? "Class: " + classe.getName() : "Class: ";
        String text4 = (classe.getSemester() != null) ? "Semester: " + classe.getSemester().getUniversityYear() : "Semester: ";
        String text5 = "TIMETABLE";
        String text6 = Integer.toString(year+1900)+" / "+Integer.toString(year+1+1900);
        String name = classe.getName();
        String[] parts = name.split(" ");
        String annee = "";
        if (parts.length > 1) {
            annee = parts[1];
        } else {
            // Handle the case where there is no second part
            // This could be setting a default value, logging an error, etc.
            System.out.println("No second part in the class name");
        }
        if(annee =="1"){
            text4 =text4+" -  1ère Année";
        }
        else{
            text4 =text4+" -  "+annee+"ème Année";
        }
        String name1 = classe.getName();
        String[] parts1 = name1.split(" ");
        String semester = "";
        if (parts.length > 1) {
            semester = parts1[1];
        } else {
            // Handle the case where there is no second part
            // This could be setting a default value, logging an error, etc.
            System.out.println("No second part in the class name");
        }

        // Create a paragraph with the new font
        Paragraph paragraph1 = new Paragraph(text1,font1);
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph2 = new Paragraph(text2,font2);
        paragraph2.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph3 = new Paragraph(text3,font3);
        paragraph3.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph4 = new Paragraph(text4,font4);
        paragraph4.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph5 = new Paragraph(text5,font5);
        paragraph5.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph6 = new Paragraph(text6,font6);
        paragraph6.setAlignment(Element.ALIGN_CENTER);
       // Image headerImage = Image.getInstance("");
        //Image footerImage = Image.getInstance("");
        float headerWidth = PageSize.A4.getWidth();
        float headerHeight = 50f;  // Adjust the height as needed
        float footerWidth = PageSize.A4.getWidth();
        float footerHeight = 50f;  // Adjust the height as needed
        Rectangle headerRect = new Rectangle(headerWidth, headerHeight);
        Rectangle footerRect = new Rectangle(footerWidth, footerHeight);
        // headerImage.setAbsolutePosition(0, PageSize.A4.getHeight() - headerHeight - 10f);
        //headerImage.scaleToFit(headerWidth, headerHeight);
       // myPDFDoc.add(headerImage);
        // Add footer image to the bottom of each page
        //footerImage.setAbsolutePosition(0, 10f);
       // footerImage.scaleToFit(footerWidth, footerHeight);footerImage.scaleToFit(footerWidth, footerHeight);
       // myPDFDoc.add(footerImage);
        myPDFDoc.add(paragraph1);
        myPDFDoc.add(paragraph2);
        myPDFDoc.add(paragraph3);
        myPDFDoc.add(paragraph4);
        myPDFDoc.add(paragraph5);
        myPDFDoc.add(paragraph6);
        // Adding an empty line
        myPDFDoc.add(new Paragraph(Chunk.NEWLINE));
        // 4)Finally add the table to the document
        myPDFDoc.add(myTable);
        myPDFDoc.newPage();
    }
}
