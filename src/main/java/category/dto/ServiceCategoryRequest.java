package category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryRequest {
    private String categoryName;
    private List<ServiceRequest> serviceRequests;
}
