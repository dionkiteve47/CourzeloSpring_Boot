package tn.esprit.user.services.Interfaces;
import com.itextpdf.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.entities.Cours;
import org.springframework.core.io.Resource;
import tn.esprit.user.entities.Ressource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

public interface ICoursService {
    public Cours addCours(Cours c, String id_matiere) ;
    public Cours updateCours(String id_cours, Cours cours);
    public List<Cours> getAllCourse();
    public Cours getCoursById(String id_cours);
    public void deleteCours(String id_cours);
    List<Cours> findAllByOrderByDateInscriptionDesc();
    List<Cours> findAllByNomCours(String  nomCours);
    public String storeFile(MultipartFile file, String blogCode);
    public Resource loadFileAsResource(String fileName);
    public Cours affecterRessourcesACour(Ressource r , String idc);
    public String storeFileRessource(MultipartFile file, String idRessource);
    List<Cours> findCoursByDateInscriptionGreaterThan();
    List<Cours> findByPrix(float prix);
    List<Cours> rechercheCours(String search);
    List<Cours> getAllCoursSortedByPrice(String sortOrder);
    void generatePdf(String fileName, Long amount) throws DocumentException, IOException, URISyntaxException;

}
