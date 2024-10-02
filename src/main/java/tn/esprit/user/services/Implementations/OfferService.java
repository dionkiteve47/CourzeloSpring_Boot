package tn.esprit.user.services.Implementations;

import tn.esprit.user.dtos.OfferDTO;
import tn.esprit.user.entities.Offer;
import tn.esprit.user.repositories.OfferRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    public ResponseEntity<HttpStatus> deleteOffer(String id) {
        offerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    public ResponseEntity<HttpStatus> updateOffer(OfferDTO offerDTO) {
        Offer offer = offerRepository.findById(offerDTO.getId()).orElse(null);
        if (offer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        offer.setTitle(offerDTO.getTitle());
        offer.setDescription(offerDTO.getDescription());
        offer.setLocation(offerDTO.getLocation());
        offer.setCompany(offerDTO.getCompany());
        offer.setSalary(offerDTO.getSalary());
        offer.setType(offerDTO.getType());
        offer.setExperience(offerDTO.getExperience());
        offer.setSkills(offerDTO.getSkills());
        offer.setDeadline(offerDTO.getDeadline());
        offerRepository.save(offer);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

