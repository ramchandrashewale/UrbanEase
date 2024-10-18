package category.controller;


import category.dto.CategoryResponse;
import category.dto.ServiceCategoryRequest;
import category.dto.ServiceCategoryResponse;
import category.dto.ServiceResponse;
import category.service.ServiceCategoryServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/serviceCategory")
public class ServiceCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryController.class);


    private ServiceCategoryServices serviceCategoryServices;

    @Autowired
    public ServiceCategoryController(ServiceCategoryServices serviceCategoryServices) {
        this.serviceCategoryServices = serviceCategoryServices;
    }

    /**
     * Method is used to retrieve service on basis of category name
     *
     * @param categoryName name of category
     * @return list of services
     */
    @GetMapping("/services")
    public Mono<ResponseEntity<List<ServiceResponse>>> retrieveService(@RequestParam String categoryName) {
        logger.debug("Received request to retrieve services for category: {}", categoryName);

        return serviceCategoryServices.retrieveServices(categoryName)
                .map(serviceResponses -> {
                    logger.debug("Successfully retrieved {} services for category: {}", serviceResponses.size(), categoryName);
                    return ResponseEntity.ok(serviceResponses);
                })
                .onErrorResume(e -> {
                    logger.error("Error while retrieving services for category: {}", categoryName, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Collections.emptyList()));
                });
    }


    /**
     * Method is used to retrieve categories
     *
     * @return list of different categories
     */

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> retrieveCategory() {
        logger.debug("Received request to retrieve all service categories");
        List<CategoryResponse> categoryResponseList = serviceCategoryServices.getServiceCategories();
        logger.debug("Successfully retrieved {} categories", categoryResponseList.size());
        return new ResponseEntity<>(categoryResponseList, HttpStatus.OK);
    }

    /**
     * Method is used to post category with services
     *
     * @param serviceCategoryRequest
     * @return returns category with services
     */
    @PostMapping
    public ResponseEntity<ServiceCategoryResponse> addServiceCategory(@RequestBody ServiceCategoryRequest serviceCategoryRequest) {
        logger.debug("Received request to add a new service category: {}", serviceCategoryRequest.getCategoryName());
        ServiceCategoryResponse serviceCategoryResponse = serviceCategoryServices.addServiceCategory(serviceCategoryRequest);
        logger.debug("Successfully added category with id: {} and services", serviceCategoryResponse.getCategory_id());
        return new ResponseEntity<>(serviceCategoryResponse, HttpStatus.CREATED);
    }


}
