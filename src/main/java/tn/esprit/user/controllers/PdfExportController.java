package tn.esprit.user.controllers;

import tn.esprit.user.services.Implementations.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@Controller
@RequestMapping("/api/pdf")
@AllArgsConstructor
public class PdfExportController {
    private final PdfExportService pdfExportService;
    @GetMapping("/classes")
    public void generateAll(HttpServletResponse response) throws IOException {
        this.pdfExportService.ClassesPDF(response);
    }
    @GetMapping("/departments/{id}")
    public void generateDepartement(HttpServletResponse response,@PathVariable String id) throws IOException {
        this.pdfExportService.DepartementsPDF(response,id);
    }
    @GetMapping("/classes/{id}")
    public void generatePDFbyClass(HttpServletResponse response,@PathVariable String  id) throws IOException {

        this.pdfExportService.OneClassePDF(response,id);

    }

    @GetMapping("/teachers/{id}")
    public void generatePDFbyProf(HttpServletResponse response,@PathVariable String  id) throws IOException {

        this.pdfExportService.ProfPDF(response,id);

    }

    @GetMapping("/teachers")
    public void generatePDFbyProf(HttpServletResponse response) throws IOException {

        this.pdfExportService.AllProfsPDF(response);

    }

}
