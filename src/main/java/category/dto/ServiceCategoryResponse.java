package category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryResponse {
    private int category_id;
    private String category_name;
    private List<ServiceResponse> service_responses;

}
