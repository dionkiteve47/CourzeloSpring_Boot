package tn.esprit.user.services.Implementations;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.entities.Cours;
import tn.esprit.user.entities.Matiere;
import tn.esprit.user.entities.Ressource;
import tn.esprit.user.repositories.CoursRepository;
import tn.esprit.user.repositories.MatiereRepository;
import tn.esprit.user.repositories.RessourceRepository;
import tn.esprit.user.services.Interfaces.ICoursService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CoursImp implements ICoursService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private CoursRepository coursRepository;
    @Autowired
    private MatiereRepository matiereRepository;
    @Autowired
    private RessourceRepository ressourceRepository;


    @Override
    public Cours addCours(Cours c, String id_matiere) {
        String idc = RandomStringUtils.randomAlphabetic(10);
        c.setId_cours(idc);
        Date date = new Date();
        c.setDateInscription(date);
        // Assigner l'ID de la matière au cours
        Matiere matiere = new Matiere();
        matiere.setId_matiere(id_matiere);
        c.setMatiere(matiere);
        for (Ressource ressource : c.getRessourceList()) {
            String idRessource = RandomStringUtils.randomAlphabetic(10);
            ressource.setIdRessource(idRessource);
        }
        List<Ressource> ressourceList = c.getRessourceList();
        ressourceRepository.saveAll(ressourceList);
        return coursRepository.save(c);
    }


    @Override
    public Cours updateCours(String id_cours, Cours cours) {
        // Recherche du cours existant par son ID
        Optional<Cours> existingCoursOptional = coursRepository.findById(id_cours);
        if (existingCoursOptional.isPresent()) {
            // Obtenir le cours existant
            Cours existingCours = existingCoursOptional.get();
            // Mettre à jour les champs du cours existant avec les nouvelles valeurs
            existingCours.setNomCours(cours.getNomCours());
            existingCours.setNomProfesseur(cours.getNomProfesseur());
            existingCours.setTypeCours(cours.getTypeCours());
            existingCours.setDateInscription(cours.getDateInscription());
            existingCours.setMatiere(cours.getMatiere());
            existingCours.setDescriptionCours(cours.getDescriptionCours());
            existingCours.setPrix(cours.getPrix());
            existingCours.setLikes(cours.getLikes());
            existingCours.setDislikes(cours.getDislikes());
            // Sauvegarder les modifications dans la base de données
            return coursRepository.save(existingCours);
        } else {
            // Gérer le cas où le cours n'est pas trouvé
            throw new RuntimeException("Cours not found with ID: " + id_cours);
        }
    }



    @Override
    public List<Cours> getAllCourse() {
        return coursRepository.findAll();
    }

    @Override
    public Cours getCoursById(String id_cours) {
        return coursRepository.findById(id_cours).orElse(null);
    }

    @Override
    public void deleteCours(String id_cours) {
        coursRepository.deleteById(id_cours);
    }

    @Override
    public List<Cours> findAllByOrderByDateInscriptionDesc() {
        for (Cours c : coursRepository.findAllByOrderByDateInscriptionDesc()) {
            log.info("le nom est \n" + c.getNomCours());
        }
        return coursRepository.findAllByOrderByDateInscriptionDesc();
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    }

    @Override
    public List<Cours> findAllByNomCours(String nomCours) {
        return coursRepository.findAllByNomCours(nomCours);
    }


    private String generateNewFileName(String originalFileName) {
        // You can customize this method to generate a unique file name.
        // For example, appending a timestamp or using a UUID.
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "_" + originalFileName;
    }

    @Override
    public String storeFile(MultipartFile file, String id_cours) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String newFileName = generateNewFileName(originalFileName);
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);
            Cours cours = coursRepository.findById(id_cours).orElseThrow(() -> new RuntimeException("Cour not found with id: " + id_cours));
            cours.setPhoto(newFileName);
            coursRepository.save(cours);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + newFileName, e);
        }
    }


     /* @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    } */

    @Override
    public Cours affecterRessourcesACour(Ressource res, String idc) {
        Cours cours = coursRepository.findById(idc)
                .orElseThrow(() -> new RuntimeException("Le cours avec l'ID spécifié n'existe pas"));
        // Vérifier si la ressource existe déjà dans la base de données
        Ressource existingRessource = ressourceRepository.findById(res.getIdRessource()).orElse(null);
        if (existingRessource != null) {
            // La ressource existe déjà dans la base de données, l'ajouter directement au cours
            cours.getRessourceList().add(existingRessource);
            log.info("La ressource existante a été ajoutée au cours avec succès.");
        } else {
            // La ressource n'existe pas dans la base de données, donc la sauvegarder d'abord
            Ressource savedRessource = ressourceRepository.save(res);
            cours.getRessourceList().add(savedRessource);
            log.info("La nouvelle ressource a été sauvegardée et ajoutée au cours avec succès.");
        }
        return coursRepository.save(cours);
    }


    @Override
    public String storeFileRessource(MultipartFile file, String idRessource) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String newFileName = generateNewFileName(originalFileName);
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath);
            Ressource ressource = ressourceRepository.findById(idRessource).get();
            ressource.setPhoto(newFileName);
            ressourceRepository.save(ressource);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + newFileName, e);
        }
    }
    @Override
    public List<Cours> findCoursByDateInscriptionGreaterThan() {
        LocalDate dateActuelleMoins20Jours = LocalDate.now().minusDays(3);
        Date dateMoins20Jours = Date.from(dateActuelleMoins20Jours.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return coursRepository.findByDateInscriptionGreaterThan(dateMoins20Jours);
    }

    @Override
    public List<Cours> findByPrix(float prix) {
        return coursRepository.findByPrix(prix);
    }

    @Override
    public List<Cours> rechercheCours(String search) {
        return coursRepository.findByDescriptionCoursIgnoreCaseOrNomProfesseur(search,search);
    }



    public void generatePdf(String fileName, Long amount) throws DocumentException, IOException, URISyntaxException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));

        // Ajouter une bordure à la page
        document.setMargins(50, 50, 50, 50);

        document.open();

        // Définir la couleur du texte en bleu
        Font font = new Font(getBaseFont(), 12, Font.NORMAL, BaseColor.BLUE);

        document.add(new Paragraph("Voici votre confirmation de paiement:", font));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        addRow(table, "Ines", new Date(), amount);
        addCustomRows(table);

        // Ajouter une bordure à la table
        table.getDefaultCell().setBorder(Rectangle.BOX);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        table.addCell(new PdfPCell(new Phrase("Name")));
        table.addCell(new PdfPCell(new Phrase("Date")));
        table.addCell(new PdfPCell(new Phrase("Amount")));
    }

    private void addRow(PdfPTable table, String name, Date date, Long amount) {
        table.addCell(new PdfPCell(new Phrase(name)));
        table.addCell(new PdfPCell(new Phrase(date.toString())));
        table.addCell(new PdfPCell(new Phrase(amount.toString())));
    }

    private void addCustomRows(PdfPTable table) {
        // Add custom rows here if needed
    }

    private BaseFont getBaseFont() throws DocumentException, IOException, URISyntaxException {
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }


    @Override
    public List<Cours> getAllCoursSortedByPrice(String sortOrder) {
        if ("asc".equalsIgnoreCase(sortOrder)) {
            return coursRepository.findAllByOrderByPrixAsc();
        } else if ("desc".equalsIgnoreCase(sortOrder)) {
            return coursRepository.findAllByOrderByPrixDesc();
        } else {
            throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
        }
    }


    public void likeOrDislikeCours(String userId, String coursId, String action) {
        // Retrieve the course by its ID
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + coursId));

        // Check if the user has already liked or disliked the course
        boolean alreadyLiked = cours.getLikedBy().contains(userId);
        boolean alreadyDisliked = cours.getDislikedBy().contains(userId);

        if (action.equals("like")) {
            if (!alreadyLiked) {
                // If user hasn't already liked, increment likes
                cours.setLikes(cours.getLikes() + 1);
                cours.getLikedBy().add(userId);

                // If user previously disliked, decrement dislikes
                if (alreadyDisliked) {
                    cours.setDislikes(cours.getDislikes() - 1);
                    cours.getDislikedBy().remove(userId);
                }
                coursRepository.save(cours);
            } else {
                throw new RuntimeException("User already liked this course.");
            }
        } else if (action.equals("dislike")) {
            if (!alreadyDisliked) {
                // If user hasn't already disliked, increment dislikes
                cours.setDislikes(cours.getDislikes() + 1);
                cours.getDislikedBy().add(userId);

                // If user previously liked, decrement likes
                if (alreadyLiked) {
                    cours.setLikes(cours.getLikes() - 1);
                    cours.getLikedBy().remove(userId);
                }
                coursRepository.save(cours);
            } else {
                throw new RuntimeException("User already disliked this course.");
            }
        } else {
            throw new IllegalArgumentException("Invalid action provided.");
        }
    }




}



