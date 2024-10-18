package com.example.CartService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResult {

    private String serviceName;
    private String serviceDescription;
    private Double servicePrice;
    private Integer serviceQuantity;
}
