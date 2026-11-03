package com.rania.projet;

import com.rania.projet.model.Departement;
import com.rania.projet.service.DepartementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProjetApplicationTests {

    @Autowired
    private DepartementService departementService;

    @Test
    void contextLoads() {
        assertNotNull(departementService);
        List<Departement> departements = departementService.getAllDepartements();
        assertNotNull(departements);
    }
}
