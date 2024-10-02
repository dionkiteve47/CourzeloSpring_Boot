package tn.esprit.user.services.Interfaces;


import tn.esprit.user.entities.Grades;

import java.util.List;

public interface IGradeService {
    void saveGrades(Grades grades);
    void deleteGrades(String grades);
    void updateGrades (Grades grades);
    Grades getGradesByID(String gradesID);
    List<Grades> getGradess();
}
