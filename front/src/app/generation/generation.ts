import { Component, inject, signal } from '@angular/core';
import { GenerationService, Enquete } from './generation.service';

@Component({
  selector: 'app-generation',
  templateUrl: './generation.html',
  styleUrl: './generation.css',
})
export class Generation {
  private readonly service = inject(GenerationService);

  readonly enquete = signal<Enquete | null>(null);
  readonly chargement = signal(false);
  readonly erreur = signal('');

  generer(): void {
    if (this.chargement()) return;

    this.chargement.set(true);
    this.erreur.set('');

    this.service.generer().subscribe({
      next: (enquete) => {
        this.enquete.set(enquete);
        this.chargement.set(false);
      },
      error: (e) => {
        this.erreur.set(
          e.error?.detail ?? 'Impossible de générer une enquête.'
        );
        this.chargement.set(false);
      }
    });
  }

  formater(valeur: string): string {
    const libelles: Record<string, string> = {
      NEUF_HEURES: '9h00',
      DIX_HEURES: '10h00',
      ONZE_HEURES: '11h00',
      BADGE: 'Badge',
      CLE: 'Clé',
      ACCES_LIBRE: 'Accès libre'
    };

    return libelles[valeur] ?? valeur;
  }

  formaterIndice(description: string): string {
    return description
      .replaceAll('NEUF_HEURES', '9h00')
      .replaceAll('DIX_HEURES', '10h00')
      .replaceAll('ONZE_HEURES', '11h00')
      .replaceAll('ACCES_LIBRE', 'accès libre')
      .replaceAll('BADGE', 'badge')
      .replaceAll('CLE', 'clé');
  }
}