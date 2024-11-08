package tn.esprit.user.services.Interfaces;
import tn.esprit.user.entities.Niveau;
import java.util.List;
public interface INiveauService {
    public Niveau addNiveau(Niveau niveau);
    public Niveau updateNiveau(String id_niveau, Niveau niveau);
    public List<Niveau> getAllNiveau();
    public Niveau getNiveauById(String id_niveau);
    public void deleteNiveau(String id_niveau);
}
