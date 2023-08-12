package com.kg.controller;

import com.kg.dto.QueryExportDto;
import com.kg.dto.QueryManagerExportDto;
import com.kg.service.QueryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/export")
public class QueryController {
    private final QueryService queryService;
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }
    @PostMapping
    public ResponseEntity<byte[]> export(@Valid @RequestBody QueryExportDto queryExportDto) {
        QueryManagerExportDto queryManagerExportDto = queryService.export(queryExportDto);
        String fileName= queryManagerExportDto.getFileName();
        byte[] content = queryManagerExportDto.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename="+ fileName);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }


}
