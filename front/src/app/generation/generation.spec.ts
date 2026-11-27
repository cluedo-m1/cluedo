import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { Generation } from './generation';
import { GenerationService, Enquete } from './generation.service';

describe('Generation', () => {
  const enquete: Enquete = {
    id: '123',
    titre: "Fuite d'un sujet d'examen",
    description: 'Une affaire sur le campus.',
    suspects: [
      {
        nom: 'Alice',
        moyensAcces: ['CLE'],
        horairesPresence: ['NEUF_HEURES']
      }
    ],
    lieux: [
      {
        nom: 'Bibliothèque',
        moyenAccesRequis: 'ACCES_LIBRE'
      }
    ],
    horaires: ['NEUF_HEURES'],
    moyensAcces: ['CLE'],
    indices: [
      {
        variable: 'SUSPECT',
        operateur: 'DIFFERENT',
        valeur: 'Thomas',
        description: "Le coupable n'est pas Thomas."
      }
    ]
  };

  const service = {
    generer: vi.fn()
  };

  beforeEach(async () => {
    service.generer.mockReset();

    await TestBed.configureTestingModule({
      imports: [Generation],
      providers: [
        { provide: GenerationService, useValue: service }
      ]
    }).compileComponents();
  });

  it('cree le composant', () => {
    const fixture = TestBed.createComponent(Generation);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('genere une enquete', () => {
    service.generer.mockReturnValue(of(enquete));

    const fixture = TestBed.createComponent(Generation);
    const component = fixture.componentInstance;

    component.generer();

    expect(service.generer).toHaveBeenCalledOnce();
    expect(component.enquete()).toEqual(enquete);
    expect(component.chargement()).toBe(false);
  });

  it('affiche les suspects et les indices', () => {
    service.generer.mockReturnValue(of(enquete));

    const fixture = TestBed.createComponent(Generation);

    fixture.componentInstance.generer();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Alice');
    expect(fixture.nativeElement.textContent)
      .toContain("Le coupable n'est pas Thomas.");
  });

  it('gere une erreur du backend', () => {
    service.generer.mockReturnValue(
      throwError(() => new Error('Erreur serveur'))
    );

    const fixture = TestBed.createComponent(Generation);
    const component = fixture.componentInstance;

    component.generer();

    expect(component.erreur()).toBe(
      'Impossible de générer une enquête.'
    );
    expect(component.chargement()).toBe(false);
  });

  it('formate correctement les horaires et les acces', () => {
    const fixture = TestBed.createComponent(Generation);
    const component = fixture.componentInstance;

    expect(component.formater('NEUF_HEURES')).toBe('9h00');
    expect(component.formater('CLE')).toBe('Clé');
    expect(component.formater('ACCES_LIBRE')).toBe('Accès libre');
  });
});
