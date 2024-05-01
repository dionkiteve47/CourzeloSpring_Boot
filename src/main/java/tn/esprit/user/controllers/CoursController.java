package tn.esprit.user.controllers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.entities.Cours;
import tn.esprit.user.entities.Ressource;
import tn.esprit.user.repositories.CoursRepository;
import tn.esprit.user.repositories.RessourceRepository;
import tn.esprit.user.services.Interfaces.ICoursService;
import tn.esprit.user.services.Interfaces.IMatiereService;
import tn.esprit.user.services.Interfaces.IRessourceService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import jakarta.mail.MessagingException;
import static java.lang.reflect.Array.get;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_DISPOSITION;
import static tn.esprit.user.services.Implementations.CoursImp.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RestController
@RequestMapping("/cours")
//@CrossOrigin({"http://localhost:8089", "http://localhost:4200"})
@CrossOrigin(origins = {"http://localhost:62822","http://localhost:4200"}, maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class CoursController {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    ICoursService iCoursService;
    @Autowired
    IMatiereService iMatiereService;
    @Autowired
    IRessourceService iRessourceService;
    @Autowired
    RessourceRepository ressourceRepository;
    @Autowired
    CoursRepository coursRepository;
    @Autowired
    private JavaMailSender mailSender;

    private final String stripeSecretKey = "sk_test_51OrVypCBYwUBNRKeLpQFdrP2WnYXdrk4o6VC3tfgW4nerqzrvi3uMwSuAgHuqGEi55jTLeu8ANnxzQIZ2p64jiM600F0Gp4FTI";
    public static final String DIRECTORY = System.getProperty("user.home") + "/uploads/";

    @PostMapping("/addCours/{id_matiere}")
    private Cours addCours(@RequestBody Cours cours, @PathVariable String id_matiere) {
        return iCoursService.addCours(cours, id_matiere);
    }


    @PutMapping("/updateCours/{id_cours}")
    public Cours updateCours(@PathVariable String id_cours, @RequestBody Cours cours) {
        return iCoursService.updateCours(id_cours, cours);
    }

    @GetMapping("/getCours")
    public List<Cours> getAllCours() {
        return iCoursService.getAllCourse();
    }

    @GetMapping("/get/{id_cours}")
    public Cours getCoursById(@PathVariable String id_cours) {
        return iCoursService.getCoursById(id_cours);
    }

    @DeleteMapping("/delete/{id_cours}")
    public void deleteCoursById(@PathVariable String id_cours) {
        iCoursService.deleteCours(id_cours);
    }


    @GetMapping("/findAllByNomCour")
    public List<Cours> findAllByNomCours(String nomCours) {
        return iCoursService.findAllByNomCours(nomCours);
    }

    @GetMapping("/findAllByOrderByDateDesc")
    public List<Cours> findAllByOrderByPrixDesc() {
        return iCoursService.findAllByOrderByDateInscriptionDesc();
    }

    @PostMapping("/upload/{id}") //(upload photo )
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("id") String courId) {
        String fileName = iCoursService.storeFile(file, courId); // Utilisez correctement la méthode storeFile
        Cours c = coursRepository.findById(courId).orElseThrow(() -> new RuntimeException("Cours not found with ID: " + courId));
        c.setPhoto(fileName);
        log.info("File uploaded successfully");
        return ResponseEntity.ok().body("File uploaded successfully: " + fileName); // Renvoyer le nom du fichier stocké
    }

    @PostMapping("/uploadRessource/{id}")
    public ResponseEntity<String> storeFileRessource(@RequestParam("file") MultipartFile file, @PathVariable("id") String idRessource) {
        String fileName = iCoursService.storeFileRessource(file, idRessource); // Utilisez correctement la méthode storeFile
        Ressource r = ressourceRepository.findById(idRessource).orElseThrow(() -> new RuntimeException("Ressource not found with ID: " + idRessource));
        r.setPhoto(fileName);
        log.info("File uploaded successfully");
        return ResponseEntity.ok().body("File uploaded successfully: " + fileName); // Renvoyer le nom du fichier stocké
    }




    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = iCoursService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    // Define a method to download files




    @PostMapping("/uploadVideo/{id_ressource}")
    public ResponseEntity<List<String>> handleVideoUpload(@RequestParam("video") MultipartFile videoFile, @PathVariable("id_ressource") String idRessource) {
        String videoFileName = iRessourceService.storeVideo(videoFile, idRessource);
        log.info("Video uploaded successfully");
        return ResponseEntity.ok().body(Collections.singletonList(videoFileName));
    }

    @GetMapping("/downloadVideo/{filename}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable("filename") String filename) throws IOException {
        String videoUploadDir = System.getProperty("user.home") + "/Downloads";
        Path filePath = Paths.get(videoUploadDir, filename).toAbsolutePath().normalize();

        log.info("Trying to access file: {}", filePath);

        if (!Files.exists(filePath)) {
            log.error("{} was not found on the server", filename);
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        // Use the original filename for the downloaded file
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders)
                .body(resource);
    }




    @PostMapping("/affecterRessourcesACour/{idc}")
    public Cours affecterRessourcesACour(@RequestBody Ressource res, @PathVariable("idc") String idc) {
        return iCoursService.affecterRessourcesACour(res, idc);
    }

    @PostMapping("/ajouterRessource")
    public Ressource ajouterRessource(@RequestBody Ressource ressource) {
        return iRessourceService.ajouterRessource(ressource);
    }

    @PutMapping("/modifierRessource/{idr}")
    public Ressource modifierRessource(@RequestBody Ressource r, @PathVariable String idr) {
        return iRessourceService.modifierRessource(r, idr);
    }

    @DeleteMapping("/supprimerRessource/{id}")
    public void supprimerRessource(@PathVariable("id") String id) {
        iRessourceService.supprimerRessource(id);
    }

    @GetMapping("/findCoursByDateInscriptionGreaterThan")
    public List<Cours> findCoursByDateInscriptionGreaterThan() {
        return iCoursService.findCoursByDateInscriptionGreaterThan();
    }

    @GetMapping("/findByDescriptionCoursOrNomProfesseur/{search}")
    public List<Cours> findByDescriptionCoursOrNomProfesseur( @PathVariable("search") String search ) {
        return iCoursService.rechercheCours(search);
    }

   /* @PostMapping("/stripe/{amount}")
    public String payer(@PathVariable("amount") Long amount) throws StripeException {
        Stripe.apiKey = "sk_test_51OrVypCBYwUBNRKeLpQFdrP2WnYXdrk4o6VC3tfgW4nerqzrvi3uMwSuAgHuqGEi55jTLeu8ANnxzQIZ2p64jiM600F0Gp4FTI";
        ChargeCreateParams params =
                ChargeCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency("usd")
                        .setSource("tok_visa")
                        .build();
        Charge charge = Charge.create(params);
        return "success";
    }*/
 /*  @PostMapping("/stripe/{amount}")
   public ResponseEntity<String> payer(@PathVariable("amount") Long amount) {
       try {
           Stripe.apiKey = "sk_test_51OrVypCBYwUBNRKeLpQFdrP2WnYXdrk4o6VC3tfgW4nerqzrvi3uMwSuAgHuqGEi55jTLeu8ANnxzQIZ2p64jiM600F0Gp4FTI";
           ChargeCreateParams params =
                   ChargeCreateParams.builder()
                           .setAmount(amount)
                           .setCurrency("usd")
                           .setSource("tok_visa")
                           .build();
           Charge charge = Charge.create(params);
           return ResponseEntity.ok("success");
       } catch (StripeException e) {
           log.error("Erreur lors du paiement Stripe :", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du paiement");
       }
   }*/

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestParam Integer amount) {
        try {
            Stripe.apiKey = "sk_test_51OrVypCBYwUBNRKeLpQFdrP2WnYXdrk4o6VC3tfgW4nerqzrvi3uMwSuAgHuqGEi55jTLeu8ANnxzQIZ2p64jiM600F0Gp4FTI";

            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("currency", "eur");

            PaymentIntent intent = PaymentIntent.create(params);
            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating payment intent");
        }
    }


    @PostMapping("/sendHtmlEmail/{recepientEmail}/{amount}")
    public String sendHtmlEmail(@PathVariable("recepientEmail") String recepientEmail, @PathVariable("amount") Long amount) throws MessagingException {
        // Message HTML de l'e-mail
        String htmlMessage = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #e9ecef; color: #343a40; margin: 0; padding: 0; }"
                + ".container { width: 80%; margin: auto; overflow: hidden; background-color: #ffffff; border-radius: 5px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }"
                + ".header { background-color: #17a2b8; color: white; text-align: center; padding: 1em 0; border-top-left-radius: 5px; border-top-right-radius: 5px; }"
                + ".content { padding: 20px; }"
                + ".footer { background-color: #17a2b8; color: white; text-align: center; padding: 1em 0; border-bottom-left-radius: 5px; border-bottom-right-radius: 5px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h1>Confirmation de paiement Réussie</h1>"
                + "</div>"
                + "<div class='content'>"
                + "<p>Votre paiement a été effectué avec succès. Nous vous remercions pour votre confiance.</p>"
                + "<p>Voici les détails de votre transaction :</p>"
                + "<ul>"
                + "<li>Montant payé : " + amount + "$" + "</li>"
                + "<li>Date de paiement : " + new Date() + "</li>"
                + "</ul>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>© 2024 Courzelo Tous droits réservés.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        mimeMessage.setContent(htmlMessage, "text/html");
        helper.setTo(recepientEmail);
        helper.setSubject("payment success");
        helper.setFrom("ines.mabrouk777@gmail.com");
        mailSender.send(mimeMessage);
        return "success";
    }
    @PostMapping("/PdfGenerator/{amount}")
    public ResponseEntity<byte[]> PdfGenerator(@PathVariable("amount") Long amount) {
        try {
            String fileName = "PDFExample_" + System.currentTimeMillis() + ".pdf";
            iCoursService.generatePdf(fileName, amount);

            Path path = Paths.get(fileName);
            byte[] pdfContent = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(fileName, fileName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{action}/{id_cours}/{user_id}")
    public ResponseEntity<?> likeOrDislikeCours(@PathVariable String action, @PathVariable String id_cours, @PathVariable String user_id) {
        try {
            iCoursService.likeOrDislikeCours(user_id, id_cours, action);
            return ResponseEntity.ok().body("{\"message\": \"Course " + action + "d successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

     @GetMapping("/sortByPrice")
    public List<Cours> getAllCoursSortedByPrice(@RequestParam(required = false, defaultValue = "asc") String sortOrder) {
        return iCoursService.getAllCoursSortedByPrice(sortOrder);
    }


}
