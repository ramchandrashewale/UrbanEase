package category.service;

import category.dto.CategoryResponse;
import category.dto.ServiceCategoryRequest;
import category.dto.ServiceCategoryResponse;
import category.dto.ServiceResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ServiceCategoryServices {

    List<CategoryResponse> getServiceCategories();

    Mono<List<ServiceResponse>> retrieveServices(String category);

    ServiceCategoryResponse addServiceCategory(ServiceCategoryRequest serviceCategoryRequest);
}
