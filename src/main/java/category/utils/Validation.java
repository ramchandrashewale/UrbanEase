package category.utils;

import category.repository.ServiceCategoryrepository;
import category.dto.ServiceCategoryRequest;
import category.dto.ServiceRequest;
import category.entity.ServiceCategory;
import category.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public class Validation {
    public static void validateCheckService(Optional<ServiceCategory> checkService) {
        if (checkService.isEmpty()) {
            throw new EntityNotFoundException("Service not found");
        }
    }

    public static void validateServiceCategory(ServiceCategoryRequest serviceCategoryRequest, ServiceCategoryrepository categoryRepository) {
        if (serviceCategoryRequest.getCategoryName() == null || serviceCategoryRequest.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }

        // Check if category already exists
        Optional<ServiceCategory> existingCategory = categoryRepository.findByCategoryName(serviceCategoryRequest.getCategoryName());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Category already exists.");
        }

        List<ServiceRequest> serviceRequests = serviceCategoryRequest.getServiceRequests();
        if (serviceRequests == null || serviceRequests.isEmpty()) {
            throw new IllegalArgumentException("Service requests cannot be null or empty.");
        }
    }
}
