package category.repository;

import category.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryrepository extends JpaRepository<ServiceCategory, Integer> {
    Optional<ServiceCategory> findByCategoryName(String categoryName);

}
