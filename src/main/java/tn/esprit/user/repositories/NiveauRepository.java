package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import tn.esprit.user.entities.Niveau;

import java.util.List;

@Repository
public interface NiveauRepository extends MongoRepository<Niveau, String> {
        @Query("{ 'nom_niveau' : ?0 }")
        List<Niveau> findByNomNiveau(String nom_niveau);
    }
