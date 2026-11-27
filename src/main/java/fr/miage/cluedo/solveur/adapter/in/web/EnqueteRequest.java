package fr.miage.cluedo.solveur.adapter.in.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.miage.cluedo.solveur.adapter.in.web.IndiceDto;

public record EnqueteRequest(Map<String, Set<String>> variables, List<IndiceDto> indices){
    
}

