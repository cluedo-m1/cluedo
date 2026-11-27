import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../api.service';

export interface Suspect {
  nom: string;
  moyensAcces: string[];
  horairesPresence: string[];
}

export interface Lieu {
  nom: string;
  moyenAccesRequis: string;
}

export interface Indice {
  variable: string;
  operateur: string;
  valeur: string;
  description: string;
}

export interface Enquete {
  id: string;
  titre: string;
  description: string;
  suspects: Suspect[];
  lieux: Lieu[];
  horaires: string[];
  moyensAcces: string[];
  indices: Indice[];
}

@Injectable({ providedIn: 'root' })
export class GenerationService {
  private readonly http = inject(HttpClient);

  generer(): Observable<Enquete> {
    return this.http.post<Enquete>(
      `${API_URL}/enquetes`, {}
    );
  }

  consulter(id: string): Observable<Enquete> {
    return this.http.get<Enquete>(
      `${API_URL}/enquetes/${id}`
    );
  }
}