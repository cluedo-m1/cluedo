import { KeyValuePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService, EnqueteRequest, ResolutionResponse } from './api.service';

@Component({
  imports: [RouterOutlet, KeyValuePipe],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  private readonly api = inject(ApiService);

  protected readonly version = signal('');
  protected readonly erreur = signal('');
  protected readonly resultat = signal<ResolutionResponse | null>(null);
  protected resoudre() {
    const requete: EnqueteRequest = {
      variables: {
        Coupable: ['Alice', 'Bob', 'Chloé'],
        Lieu: ['salle', 'imprimante', 'serveur'],
      },
      indices: [
        { type: 'EXCLUSION', variable: 'Lieu', valeur: 'salle' },
        { type: 'IMPLICATION', variableX: 'Coupable', valeurX: 'Alice',
          variableY: 'Lieu', valeursAutoriseesY: ['salle'] },
        { type: 'IMPLICATION', variableX: 'Coupable', valeurX: 'Chloé',
          variableY: 'Lieu', valeursAutoriseesY: ['salle'] },
      ],
    };
    this.api.resoudre(requete).subscribe({
      next: (reponse) => this.resultat.set(reponse),
      error: (e) => this.erreur.set(e.error?.detail ?? e.error?.title ?? `Erreur HTTP ${e.status}`),
    });
           
  }
  constructor() {
    this.api.info().subscribe({
      next: ({ name, version }) => this.version.set(`${name} ${version}`),
      error: (e) => this.erreur.set(e.error?.detail ?? e.error?.title ?? `Erreur HTTP ${e.status}`),
    });
  }
}
