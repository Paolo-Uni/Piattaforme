package org.example.progetto.services;

import org.example.progetto.repositories.CarrelloRepository;
import org.example.progetto.repositories.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarrelloService {

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;
}
