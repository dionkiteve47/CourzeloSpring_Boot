package tn.esprit.user.entities.schedule;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.entities.institution.Class;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection =  "Module")
@Data
public class Modul {
    @Id
    private String id;
    private Integer nmbrHours;
    @Size(max = 255)
    private String name;
    private Boolean isSeperated;
    private Boolean isMetuale;
    @DBRef
    private Class aClass;
    @DBRef
    private List<ElementModule>elementModules;
    public boolean isMetuale() {
        return false;
    }
    public boolean isSeperated() {
        return false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modul that = (Modul) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(nmbrHours, that.nmbrHours) &&
                Objects.equals(name, that.name) &&
                Objects.equals(isSeperated, that.isSeperated) &&
                Objects.equals(isMetuale, that.isMetuale);
    }
}
