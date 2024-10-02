
package tn.esprit.user.controllers;

import tn.esprit.user.entities.CourseContent;
import tn.esprit.user.services.Implementations.CoursContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/coursContet")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class CoursContentController {

    @Autowired
    CoursContentService coursContentService;


    @PostMapping()
    public void saveCourse(@RequestBody CourseContent cours) {
        coursContentService.saveCoursContent(cours);
    }

    @PutMapping()
    public void updateCourse(@RequestBody CourseContent cours) {
        coursContentService.updateCoursContent(cours);
    }

    @GetMapping("/{coursId}")
    public List<CourseContent> getCourseByID(@PathVariable String coursId) {
        return coursContentService.getCoursContentByID(coursId);
    }

    @GetMapping()
    public List<CourseContent> getCourses() {
        return coursContentService.getCoursContents();
    }

    @DeleteMapping("/{id}")
    public String DeleteCourse(@PathVariable String id) {
        coursContentService.deleteCoursContent(id)
        ;
        return "delete";
    }


    @PostMapping("/upload/{idCours}")
    public void handleFileUpload(@RequestParam("file") MultipartFile file,@PathVariable("idCours") String idCours) {
        if (file.isEmpty()) {
            System.out.println("Veuillez sélectionner un fichier à télécharger.");
        }

        CourseContent content = coursContentService.getContentById(idCours);


        try {
            // Extraire le nom et l'extension du fichier
            String originalFilename = file.getOriginalFilename();
            String fileName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

            // Convertir le MultipartFile en un tableau de bytes
            content.setData(file.getBytes());
            content.setExtension(fileExtension);
            content.setFichierName(fileName);
            coursContentService.saveCoursContent(content);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}