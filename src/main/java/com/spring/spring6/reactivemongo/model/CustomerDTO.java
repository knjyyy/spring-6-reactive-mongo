package com.spring.spring6.reactivemongo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {
    private String id;

    @NotBlank
    private String name;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
