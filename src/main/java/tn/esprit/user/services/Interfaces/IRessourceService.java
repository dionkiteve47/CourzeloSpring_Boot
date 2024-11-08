package tn.esprit.user.services.Interfaces;
import tn.esprit.user.entities.Ressource;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IRessourceService {
        public Ressource ajouterRessource(Ressource ressource);
        public List<Ressource> getRessource();
        public void supprimerRessource(String idr);
        public Ressource modifierRessource(Ressource r, String idr) ;
        public List<Ressource> getRessourcesByCourId(String id) ;
        public String uploadImage(Model model , MultipartFile file);
        public String storeVideo(MultipartFile videoFile, String ressourceId);

}

