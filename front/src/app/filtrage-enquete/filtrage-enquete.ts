import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { ApiService, Enquete, Filtrage } from '../api.service';

/**
 * Écran de filtrage d'hypothèses : le joueur coche les indices recueillis et fixe une hypothèse
 * (suspect, lieu, objet...) ; le back renvoie le nombre de scénarios encore compatibles.
 * Toute la logique de calcul reste dans l'API : ce composant n'affiche que ce qu'elle renvoie.
 */
@Component({
  selector: 'app-filtrage-enquete',
  styleUrl: './filtrage-enquete.css',
  templateUrl: './filtrage-enquete.html',
})
export class FiltrageEnquete {
  private readonly api = inject(ApiService);
  private readonly enqueteId = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));
  private requeteEnCours?: Subscription;

  protected readonly enquete = signal<Enquete | null>(null);
  protected readonly indicesActifs = signal<ReadonlySet<number>>(new Set());
  protected readonly hypothese = signal<Readonly<Record<string, string>>>({});
  protected readonly resultat = signal<Filtrage | null>(null);
  protected readonly erreur = signal('');

  constructor() {
    inject(DestroyRef).onDestroy(() => this.requeteEnCours?.unsubscribe());

    if (!Number.isInteger(this.enqueteId)) {
      this.erreur.set("L'identifiant de l'enquête est invalide.");
      return;
    }
    this.api.enquete(this.enqueteId).subscribe({
      next: (enquete) => {
        this.enquete.set(enquete);
        this.recalculer();
      },
      error: (e) => this.erreur.set(this.message(e)),
    });
  }

  protected basculerIndice(id: number, coche: boolean): void {
    const actifs = new Set(this.indicesActifs());
    if (coche) {
      actifs.add(id);
    } else {
      actifs.delete(id);
    }
    this.indicesActifs.set(actifs);
    this.recalculer();
  }

  protected choisirValeur(variable: string, valeur: string): void {
    const hypothese = { ...this.hypothese() };
    if (valeur === '') {
      delete hypothese[variable];
    } else {
      hypothese[variable] = valeur;
    }
    this.hypothese.set(hypothese);
    this.recalculer();
  }

  protected reinitialiser(): void {
    this.indicesActifs.set(new Set());
    this.hypothese.set({});
    this.recalculer();
  }

  /** « SUSPECT » -> « Suspect » : le back renvoie des codes, pas des libellés d'affichage. */
  protected libelleVariable(code: string): string {
    return code.charAt(0).toUpperCase() + code.slice(1).toLowerCase();
  }

  /** Part de l'ensemble des scénarios compatibles qui contiennent cette valeur, pour la barre. */
  protected pourcentage(scenarios: number, total: number): number {
    return total > 0 ? (100 * scenarios) / total : 0;
  }

  private recalculer(): void {
    // Une réponse arrivée trop tard ne doit pas écraser un résultat plus récent : on annule la précédente.
    this.requeteEnCours?.unsubscribe();
    this.requeteEnCours = this.api
      .filtrer(this.enqueteId, {
        indicesActifsIds: [...this.indicesActifs()],
        hypothese: this.hypothese(),
      })
      .subscribe({
        next: (resultat) => {
          this.resultat.set(resultat);
          this.erreur.set('');
        },
        error: (e) => this.erreur.set(this.message(e)),
      });
  }

  private message(e: HttpErrorResponse): string {
    return e.error?.detail ?? e.error?.title ?? `Erreur HTTP ${e.status}`;
  }
}
