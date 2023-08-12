package com.kg.service;

import com.kg.dto.QueryExportDto;
import com.kg.dto.QueryManagerExportDto;

public interface QueryService {
    QueryManagerExportDto export(QueryExportDto queryExportDto);
}
