import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

/** Seul endroit du front qui connaît l'URL du back : tous les appels à l'API REST passent par ici. */
export const API_URL = 'http://localhost:8080/api/v1';

export interface Info {
  name: string;
  version: string;
}

/** Une dimension de l'enquête (suspect, lieu, objet...) et ses valeurs possibles, dans l'ordre de l'API. */
export interface Variable {
  code: string;
  valeurs: string[];
}

export interface Indice {
  id: number;
  libelle: string;
}

export interface Enquete {
  id: number;
  titre: string;
  variables: Variable[];
  indices: Indice[];
}

export interface FiltrageRequete {
  indicesActifsIds: number[];
  /** code de variable -> valeur choisie ; vide = aucune hypothèse */
  hypothese: Record<string, string>;
}

export interface ComptageValeur {
  valeur: string;
  scenarios: number;
}

export interface ComptageVariable {
  variable: string;
  valeurs: ComptageValeur[];
}

export interface Filtrage {
  totalScenariosCompatibles: number;
  noeudsVisites: number;
  comptageParVariable: ComptageVariable[];
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  info(): Observable<Info> {
    return this.http.get<Info>(`${API_URL}/info`);
  }

  enquete(id: number): Observable<Enquete> {
    return this.http.get<Enquete>(`${API_URL}/joueur/enquetes/${id}`);
  }

  filtrer(enqueteId: number, requete: FiltrageRequete): Observable<Filtrage> {
    return this.http.post<Filtrage>(`${API_URL}/joueur/enquetes/${enqueteId}/filtrages`, requete);
  }
}
