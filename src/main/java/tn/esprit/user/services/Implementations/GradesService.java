package tn.esprit.user.services.Implementations;


import tn.esprit.user.entities.Grades;
import tn.esprit.user.repositories.GradeRepository;

import tn.esprit.user.services.Interfaces.IGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradesService implements IGradeService {
    private  final GradeRepository gradesRepository;
    @Override
    public void saveGrades(Grades grades) {
        gradesRepository.save(grades);
    }

    @Override
    public void deleteGrades(String gradesID) {
        gradesRepository.deleteById(gradesID);
    }

    @Override
    public void updateGrades(Grades grades) {
        gradesRepository.save(grades);
    }

    @Override
    public Grades getGradesByID(String gradesID) {
        return gradesRepository.findById(gradesID).orElseThrow(()-> new RuntimeException("Grades Not Found!"));
    }

    @Override
    public List<Grades> getGradess() {
        return gradesRepository.findAll();
    }
}
