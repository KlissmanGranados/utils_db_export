package com.kg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryExportDto {
    @NotBlank
    private String uri;
    @NotBlank
    private String query;
    @NotBlank
    private String format;
}
