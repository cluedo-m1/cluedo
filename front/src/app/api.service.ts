import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

/** Seul endroit du front qui connaît l'URL du back : tous les appels à l'API REST passent par ici. */
export const API_URL = 'http://localhost:8080/api/v1';

export interface Info {
  name: string;
  version: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  info(): Observable<Info> {
    return this.http.get<Info>(`${API_URL}/info`);
  }
}
