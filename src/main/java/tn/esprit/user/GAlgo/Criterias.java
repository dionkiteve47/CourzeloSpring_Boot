package tn.esprit.user.GAlgo;

import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.Modul;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.entities.User;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;

public class Criterias {
    UniversityTimetable universityTimetable;

    public Criterias(UniversityTimetable universityTimetable) {
        this.universityTimetable = universityTimetable;
    }

    public int isDisponibilitesTeachersSatisfied() {
        int counter = 0;
        List<User> teachers = universityTimetable.getTeachers();
        for (User teacher : teachers) {
           for (ElementModule element : teacher.getEducation().getElementModules()) {
                boolean isAvailable = teacher.getEducation().getNonDisponibilities().stream()
                        .noneMatch(nonDispo ->
                                nonDispo.getDayOfWeek() == element.getDayOfWeek() &&
                                        nonDispo.getPeriod() == element.getPeriod()
                        );
                if (!isAvailable) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int isTeacherClasseConflictSatisfied() {
        int counter = 0;
        List<User> teachers = universityTimetable.getTeachers();
        for (User teacher : teachers) {
            List<ElementModule> elements = teacher.getEducation().getElementModules();
            if (elements != null) {
                for (ElementModule element : elements) {
                    boolean isSatisfied = elements.stream()
                            .noneMatch(elementCompar ->
                                    element.getDayOfWeek() == elementCompar.getDayOfWeek() &&
                                            element.getClasses() != elementCompar.getClasses() &&
                                            elementCompar.getPeriod() == element.getPeriod() &&
                                            !Objects.equals(elementCompar.getId(), element.getId())


                            );
                    if (!isSatisfied) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    public int isClasseSeancesConflictSatisfied() {
        int couner = 0;
        List<Class> classes = universityTimetable.getClasses();
        for (Class classe : classes) {
            List<Modul> modules = (List<Modul>) classe.getModuls();
            if (modules != null) {
                for (Modul module : modules) {
                    List<ElementModule> elements = (List<ElementModule>) module.getElementModules();
                    if (elements != null) {
                        for (ElementModule element : elements) {
                            // Check if the classe has two elements in the same jour and periode
                            for (Modul module1 : modules) {
                                List<ElementModule> elements1 = (List<ElementModule>) module1.getElementModules();
                                boolean isSatisfied = elements1.stream()
                                        .noneMatch(elementCompar ->
                                                element.getDayOfWeek() == elementCompar.getDayOfWeek() &&
                                                        elementCompar.getPeriod() == element.getPeriod() &&
                                                        elementCompar.getId() != element.getId() &&
                                                        element.getModul().getAClass().getSemester().getSemesterNumber().ordinal() % 2 == elementCompar.getModul().getAClass().getSemester().getSemesterNumber().ordinal() % 2/*&&
                                                        elementCompar.getModule().isMetuale()==false &&
                                                        elementCompar.getModule().getClasse() != element.getModule().getClasse()&&
                                                        elementCompar.getModule().getClasse().getFiliere()== element.getModule().getClasse().getFiliere()
*/
                                        );
                                if (!isSatisfied) {
                                    couner++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return couner;

    }

    public int isClasseAfternoonFreeDaySatisfied() {
        int couner = 0;
        List<Class> classes = universityTimetable.getClasses();
        for (Class classe : classes) {
            List<Modul> modules = (List<Modul>) classe.getModuls();
            if (modules != null) {
                boolean hasAfternoonFreeDay = modules.stream()
                        .flatMap(mod -> mod.getElementModules().stream())
                        .noneMatch(el -> el.getPeriod().equals(Period.P3) && el.getPeriod().equals(Period.P4) && el.getDayOfWeek() == DayOfWeek.WEDNESDAY);

                if (!hasAfternoonFreeDay) {
                    couner++;
                }
            }
        }
        return couner;
    }
    public int areElementsAdjacent() {
        int couner = 0;
        List<Modul> modules =universityTimetable.getModules();
        for (Modul module : modules) {
            if (!module.isSeperated()) {
                List<ElementModule> elements = (List<ElementModule>) module.getElementModules();
                if (elements != null) {
                    ElementModule currentElement = elements.get(0);
                    ElementModule nextElement = elements.get(1);
                    // Check if the elements are not adjacent in the timetable
                    if ((currentElement.getDayOfWeek()!= nextElement.getDayOfWeek() &&
                            currentElement.getPeriod().ordinal() != nextElement.getPeriod().ordinal() - 1)|| (currentElement.getDayOfWeek() != nextElement.getDayOfWeek() &&
                            currentElement.getPeriod().ordinal() != nextElement.getPeriod().ordinal() + 1)) {

                        couner++;

                    }
                }
            }
        }
        return couner;
    }
    public int areElementsInSamePeriod() {
        int couner = 0;
        List<Class> classes = universityTimetable.getClasses();
        for (Class classe : classes) {
            List<Modul> modules = (List<Modul>) classe.getModuls();
            if (modules != null) {
                for (Modul module : modules) {
                    if (module.isMetuale()) {
                        List<ElementModule> elements = (List<ElementModule>) module.getElementModules();
                        if (elements != null) {
                            for (ElementModule element : elements) {
                                // Check if there is an element in another class within the same department
                                // with the same module name that is in the same period
                                for (Class otherClasse : classes) {
                                    if (otherClasse != classe && otherClasse.getFieldOfStudy().equals(classe.getFieldOfStudy())) {
                                        List<Modul> otherModules = (List<Modul>) otherClasse.getModuls();
                                        if (otherModules != null) {
                                            for (Modul otherModule : otherModules) {
                                                if (otherModule.getName().equals(module.getName())) {
                                                    List<ElementModule> otherElements = (List<ElementModule>) otherModule.getElementModules();
                                                    if (otherElements != null) {
                                                        boolean isSatisfied = otherElements.stream()
                                                                .noneMatch(otherElement ->
                                                                        otherElement.getPeriod() == element.getPeriod()
                                                                );
                                                        if (!isSatisfied) {
                                                            couner++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return couner;
    }
}

