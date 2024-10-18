package category.service.impl;

import category.constant.AppConstant;
import category.repository.ServiceCategoryrepository;
import category.dto.CategoryResponse;
import category.dto.ServiceCategoryRequest;
import category.dto.ServiceCategoryResponse;
import category.dto.ServiceResponse;
import category.entity.ServiceCategory;
import category.exception.BusinessServiceException;
import category.exception.EntityNotFoundException;
import category.service.ServiceCategoryServices;
import category.utils.Validation;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCategoryServicesImpl implements ServiceCategoryServices {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryServicesImpl.class);


    private WebClient webClient;

    private ServiceCategoryrepository categoryRepository;

    @Autowired
    public ServiceCategoryServicesImpl(WebClient webClient, ServiceCategoryrepository categoryRepository) {
        this.webClient = webClient;
        this.categoryRepository = categoryRepository;
    }
    /**
     * used to retrieve categories
     *
     * @return list of categories
     */
    @Override
    public List<CategoryResponse> getServiceCategories() {
        try {
            logger.info("Fetching all service categories");
            List<ServiceCategory> serviceCategories = categoryRepository.findAll();
            List<CategoryResponse> categoryResponses = serviceCategories.stream()
                    .map(serviceCategory -> new CategoryResponse(
                            serviceCategory.getId(),
                            serviceCategory.getCategoryName()
                    ))
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} categories", categoryResponses.size());
            return categoryResponses;
        } catch (Exception e) {
            logger.error("Error fetching categories from the database", e);
            throw new BusinessServiceException(AppConstant.Business_Service_Error_MSG);
        }

    }

    /**
     * Method is used to retrieve  service on basis of category
     *
     * @param categoryName name of category
     * @return used to return services
     */
    public Mono<List<ServiceResponse>> retrieveServices(String categoryName) {
        logger.info("Fetching services for category: {}", categoryName);

        return Mono.justOrEmpty(categoryRepository.findByCategoryName(categoryName))

                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Category not found: {}", categoryName);
                    return Mono.error(new EntityNotFoundException("Entity with given id not present"));
                }))
                .flatMap(serviceCategory -> getServiceResponses(serviceCategory))
                .doOnNext(serviceResponses -> {
                    if (serviceResponses.isEmpty()) {
                        logger.warn("No services found for category: {}", categoryName);
                        throw new ServiceException(AppConstant.NO_SERVICES_FOUND_MSG + categoryName);
                    }
                    logger.info("Successfully fetched services for category: {}", categoryName);
                })
                .onErrorMap(WebClientException.class, e -> {
                    logger.error("Error calling external service for category: {}", categoryName, e);
                    return new ServiceException(AppConstant.SERVICE_MICROSERVICE_ERROR_MSG + e.getMessage(), e);
                });
    }

    /**
     * Internal method  for get request from service
     * @param serviceCategory category of service
     * @return list of service
     */

    private Mono<List<ServiceResponse>> getServiceResponses(ServiceCategory serviceCategory) {
        logger.debug("Fetching services for categoryId: {}", serviceCategory.getId());

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AppConstant.SERVICE_LIST_URL)
                        .queryParam("categoryId", serviceCategory.getId())
                        .build())
                .retrieve()
                .bodyToFlux(ServiceResponse.class)
                .collectList()
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No services found for categoryId: {}", serviceCategory.getId());
                    return Mono.empty();
                }));  // Handle empty Flux case
    }

    /**
     * used to save Category with list of services
     *
     * @param serviceCategoryRequest consist of category and list of service
     * @return return saved category and services
     */
    public ServiceCategoryResponse addServiceCategory(ServiceCategoryRequest serviceCategoryRequest) {
        try {
            logger.info("Adding new service category: {}", serviceCategoryRequest.getCategoryName());
            // Validate the serviceCategoryRequest
            Validation.validateServiceCategory(serviceCategoryRequest, categoryRepository);

            // Create new ServiceCategory
            ServiceCategory serviceCategory = new ServiceCategory();
            serviceCategory.setCategoryName(serviceCategoryRequest.getCategoryName());

            ServiceCategory category = categoryRepository.save(serviceCategory);

            logger.info("Service category saved with id: {}", category.getId());

            List<ServiceResponse> serviceResponses = getPostServiceResponses(serviceCategoryRequest, category);

            logger.info("Successfully added services for category: {}", category.getCategoryName());

            return new ServiceCategoryResponse(category.getId(), category.getCategoryName(), serviceResponses);
        } catch (WebClientException e) {
            logger.error("Error calling external service for category: {}", serviceCategoryRequest.getCategoryName(), e);
            throw new ServiceException(AppConstant.SERVICE_MICROSERVICE_ERROR_MSG + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error: {}", e.getMessage());
            // Rethrow for global handling
            throw e;
        }catch (Exception e) {
            logger.error("Unexpected error adding service category: {}", serviceCategoryRequest.getCategoryName(), e);
            throw new BusinessServiceException(AppConstant.Business_Service_Error_MSG);
        }
    }

    private List<ServiceResponse> getPostServiceResponses(ServiceCategoryRequest serviceCategoryRequest, ServiceCategory category) {
        logger.debug("Posting services for categoryId: {}", category.getId());
        return serviceCategoryRequest.getServiceRequests().stream()
                .peek(serviceRequest -> serviceRequest.setCategoryId(category.getId())) // Set the categoryId
                .map(serviceRequest -> webClient.post()
                        .uri("/services")
                        .bodyValue(serviceRequest)
                        .retrieve()
                        .bodyToMono(ServiceResponse.class)
                        .block())// Post the request and get the response

                .collect(Collectors.toList()); // Collect responses to a list

    }
}
