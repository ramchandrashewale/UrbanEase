package category.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategory {
    @Id
    @GeneratedValue
    private int id;
    private String categoryName;
}
