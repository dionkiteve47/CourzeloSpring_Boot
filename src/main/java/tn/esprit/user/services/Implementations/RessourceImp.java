package tn.esprit.user.services.Implementations;
import org.springframework.util.StringUtils;
import tn.esprit.user.entities.Cours;
import tn.esprit.user.entities.Ressource;
import tn.esprit.user.repositories.CoursRepository;
import tn.esprit.user.repositories.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.services.Interfaces.IRessourceService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

@Service
public class RessourceImp implements IRessourceService {

    private static final String videoUploadDir = "C:\\Users\\MAHMAB\\Downloads";  //ne9sa


    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";


    @Autowired
    RessourceRepository ressourceRepository;
    @Autowired
    CoursRepository coursRepository;
    @Override
    public Ressource ajouterRessource(Ressource ressource) {
        return ressourceRepository.save(ressource);
    }

    @Override
    public List<Ressource> getRessource() {
        return ressourceRepository.findAll();
    }

    @Override
    public void supprimerRessource(String idr) {
        Ressource r= ressourceRepository.findById(idr).get();
        ressourceRepository.delete(r);
    }

    @Override
    public Ressource modifierRessource(Ressource r, String idr) {

        Ressource res = ressourceRepository.findById(idr).get();
        res.setNomRessource(r.getNomRessource());
        return ressourceRepository.save(res);
    }

    @Override
    public List<Ressource> getRessourcesByCourId(String id) {
        Cours cours = coursRepository.findById(id).get();
        return cours.getRessourceList();
    }


    @Override
    public String uploadImage(Model model, MultipartFile file) {
        StringBuilder fileNames = new StringBuilder();
        try {
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
            Files.write(fileNameAndPath, file.getBytes());
            fileNames.append(file.getOriginalFilename());
            model.addAttribute("msg", "Uploaded images: " + fileNames.toString());
        } catch (IOException e) {
            // Gérer les erreurs liées à l'écriture du fichier, par exemple, en ajoutant un message d'erreur au modèle.
            model.addAttribute("error", "Error uploading the image.");
            e.printStackTrace(); // Vous pouvez également logger l'exception.
        }
        return "imageupload/index";
    }


    private String generateNewFileName(String originalFileName, String fileType) {
        String extension = FilenameUtils.getExtension(originalFileName);
        return fileType + "-" + UUID.randomUUID().toString() + "." + extension;
    }

    @Override
    public String storeVideo(MultipartFile videoFile, String ressourceId) {
        String originalVideoName = StringUtils.cleanPath(videoFile.getOriginalFilename());
        String newVideoName = generateNewFileName(originalVideoName, "videos");
        Path videoUploadPath = Paths.get(videoUploadDir);
        try {
            if (Files.notExists(videoUploadPath)) {
                Files.createDirectories(videoUploadPath);
            }
            Path videoFilePath = videoUploadPath.resolve(newVideoName);
            Files.copy(videoFile.getInputStream(), videoFilePath);

            Ressource ressource = ressourceRepository.findById(ressourceId)
                    .orElseThrow(() -> new RuntimeException("Ressource not found with id: " + ressourceId));

            // Save both original and new video names
            ressource.setVideo(newVideoName);
            ressource.setOriginalVideoName(originalVideoName);

            ressourceRepository.save(ressource);
            return newVideoName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store video file: " + newVideoName, e);
        }
    }


}
