package com.example.catalogservice.controller;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog-service")
@RequiredArgsConstructor
public class CatalogController {

    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    private String status(){
        return String.format("It's working in catalog service post %s",env.getProperty("local.server.port"));
    }
    @GetMapping("/catalogs")
    public ResponseEntity<Iterable<ResponseCatalog>> getCatalogs() {

        Iterable<CatalogEntity> catalogs = catalogService.getAllCatalogs();

        ModelMapper modelMapper = new ModelMapper();

        List<ResponseCatalog> responseCatalogs = new ArrayList<>();

        for (CatalogEntity catalog : catalogs) {
            responseCatalogs.add(modelMapper.map(catalog, ResponseCatalog.class));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseCatalogs);


    }

}
