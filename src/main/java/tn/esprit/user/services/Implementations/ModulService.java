package tn.esprit.user.services.Implementations;

import tn.esprit.user.dto.schedule.ModulDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.Modul;
import tn.esprit.user.repositories.ClassRepository;
import tn.esprit.user.repositories.ElementModuleRepository;
import tn.esprit.user.repositories.ModulRepository;
import tn.esprit.user.utils.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModulService {
    private final ModulRepository modulRepository;
    private final ElementModuleRepository elementModuleRepository;
    private final ClassRepository classRepository;

    public ModulService(final ModulRepository modulRepository,
                        final ElementModuleRepository elementModuleRepository, ClassRepository classRepository) {
        this.modulRepository = modulRepository;
        this.elementModuleRepository = elementModuleRepository;
        this.classRepository = classRepository;
    }

    public List<ModulDTO> findAll() {
        final List<Modul> moduls = modulRepository.findAll(Sort.by("id"));
        return moduls.stream()
                .map(modul -> mapToDTO(modul, new ModulDTO()))
                .toList();
    }

    public ModulDTO get(final String id) {
        return modulRepository.findById(id)
                .map(modul -> mapToDTO(modul, new ModulDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final ModulDTO modulDTO) {
        final Modul modul = new Modul();
        mapToEntity(modulDTO, modul);
        modul.setId(modulDTO.getId());
        return modulRepository.save(modul).getId();
    }
    public ModulDTO createModul(ModulDTO modulDTO) {
        List<ElementModule> elementModules = new ArrayList<>();
        if (modulDTO.getElementModules() != null) {
            elementModules = modulDTO.getElementModules().stream()
                    .filter(dto -> dto.getId() != null)
                    .map(dto -> elementModuleRepository.findById(dto.getId())
                            .orElseThrow(() -> new RuntimeException("ElementModule not found for ID: " + dto.getId())))
                    .collect(Collectors.toList());
        }
        Class aClass = null;
        if (modulDTO.getAClass() != null && modulDTO.getAClass().getId() != null) {
            aClass = classRepository.findById(modulDTO.getAClass().getId())
                    .orElseThrow(() -> new RuntimeException("Class not found for ID: " + modulDTO.getAClass().getId()));
        }
        Modul modul = new Modul();
        modul.setName(modulDTO.getName());
        modul.setElementModules(elementModules);
        modul.setAClass(aClass);
        modul = modulRepository.save(modul);
        return mapToDTO(modul, new ModulDTO());
    }
    public ModulDTO createModul1(ModulDTO modulDTO) {
        List<ElementModule> elementModules = modulDTO.getElementModules().stream()
                .map(dto -> {
                    ElementModule elementModule = new ElementModule();
                    elementModule.setName(dto.getName());
                    return elementModuleRepository.save(elementModule);
                })
                .collect(Collectors.toList());

        // Retrieve the Class entity using the provided ID
        Class aClass = classRepository.findById(modulDTO.getAClass().getId())
                .orElseThrow(() -> new NotFoundException("Class not found for ID: " + modulDTO.getAClass().getId()));

        Modul modul = new Modul();
        modul.setName(modulDTO.getName());
        modul.setElementModules(elementModules);
        modul.setAClass(aClass); // Associate the retrieved Class entity with the new Modul entity
        modul = modulRepository.save(modul);
        return mapToDTO(modul, new ModulDTO());
    }

    public void update(final String id, final ModulDTO modulDTO) {
        final Modul modul = modulRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modul not found for ID: " + id));
        if (modul == null) {
            throw new NotFoundException("Modul not found for ID: " + id);
        }
        mapToEntity(modulDTO, modul);
        modulRepository.save(modul);
    }

    public void delete(final String id) {
        modulRepository.deleteById(id);
    }

    private ModulDTO mapToDTO(final Modul modul, final ModulDTO modulDTO) {
        modulDTO.setId(modul.getId());
        modulDTO.setNmbrHours(modul.getNmbrHours());
        modulDTO.setName(modul.getName());
        modulDTO.setIsSeperated(modul.getIsSeperated());
        modulDTO.setIsMetuale(modul.getIsMetuale());
        modulDTO.setElementModules(modul.getElementModules());
        modulDTO.setAClass(modul.getAClass());

        //modulDTO.setElement(modul.getElement() == null ? null : modul.getElement().getId());
        return modulDTO;
    }

    private Modul mapToEntity(final ModulDTO modulDTO, final Modul modul) {
        modul.setNmbrHours(modulDTO.getNmbrHours());
        modul.setName(modulDTO.getName());
        modul.setIsSeperated(modulDTO.getIsSeperated());
        modul.setIsMetuale(modulDTO.getIsMetuale());
        modul.setElementModules(modulDTO.getElementModules());
        modul.setAClass(modulDTO.getAClass());
        /*final ElementModule element = modulDTO.getElement() == null ? null : elementModuleRepository.findById(modulDTO.getElement())
                .orElseThrow(() -> new NotFoundException("element not found"));
        modul.setElement(element);*/
        return modul;
    }

    public boolean idExists(final String id) {
        return modulRepository.existsByIdIgnoreCase(id);
    }
    public Modul addModul(Modul modul ) {
        return modulRepository.save(modul);
    }
    public ModulDTO updateModulClass(String modulId, String classId) {
        // Retrieve the Modul entity using the provided ID
        Modul modul = modulRepository.findById(modulId)
                .orElseThrow(() -> new NotFoundException("Modul not found for ID: " + modulId));

        // Retrieve the Class entity using the provided ID
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found for ID: " + classId));

        // Associate the retrieved Class entity with the Modul entity
        modul.setAClass(aClass);

        // Save the updated Modul entity
        modul = modulRepository.save(modul);

        // Convert the updated Modul entity to DTO and return
        return mapToDTO(modul, new ModulDTO());
    }


}
