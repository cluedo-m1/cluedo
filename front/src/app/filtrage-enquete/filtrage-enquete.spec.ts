import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { FiltrageEnquete } from './filtrage-enquete';

describe('FiltrageEnquete', () => {
  let fixture: ComponentFixture<FiltrageEnquete>;
  let httpMock: HttpTestingController;

  const enqueteMock = {
    id: 1,
    titre: "Fuite du sujet d'examen de POO",
    variables: [
      { code: 'SUSPECT', valeurs: ['Prof. Leblanc', 'Mme Duval'] },
      { code: 'LIEU', valeurs: ['Salle serveur', 'Amphi A'] },
    ],
    indices: [
      { id: 1, libelle: "Le suspect n'est pas Prof. Leblanc" },
      { id: 2, libelle: 'Incident en Salle serveur' },
    ],
  };

  const reponseInitiale = {
    totalScenariosCompatibles: 4,
    noeudsVisites: 6,
    comptageParVariable: [
      {
        variable: 'SUSPECT',
        valeurs: [
          { valeur: 'Prof. Leblanc', scenarios: 2 },
          { valeur: 'Mme Duval', scenarios: 2 },
        ],
      },
      {
        variable: 'LIEU',
        valeurs: [
          { valeur: 'Salle serveur', scenarios: 2 },
          { valeur: 'Amphi A', scenarios: 2 },
        ],
      },
    ],
  };

  function creerComposantAvecRouteId(id: string): ComponentFixture<FiltrageEnquete> {
    TestBed.overrideProvider(ActivatedRoute, {
      useValue: {
        snapshot: { paramMap: convertToParamMap({ id }) },
      },
    });
    const f = TestBed.createComponent(FiltrageEnquete);
    httpMock = TestBed.inject(HttpTestingController);
    return f;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FiltrageEnquete],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ id: '1' }) },
          },
        },
      ],
    }).compileComponents();
  });

  afterEach(() => {
    httpMock.verify();
  });

  function initialiserNominal(): void {
    fixture = TestBed.createComponent(FiltrageEnquete);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();

    const reqGet = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1');
    expect(reqGet.request.method).toBe('GET');
    reqGet.flush(enqueteMock);

    const reqPost = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(reqPost.request.method).toBe('POST');
    reqPost.flush(reponseInitiale);
    fixture.detectChanges();
  }

  it('F1 : affiche le titre et les indices apres le GET', () => {
    initialiserNominal();

    const texte = fixture.nativeElement.textContent;
    expect(texte).toContain("Fuite du sujet d'examen de POO");
    expect(texte).toContain("Le suspect n'est pas Prof. Leblanc");
    expect(texte).toContain('Incident en Salle serveur');
  });

  it('F2 : cocher un indice envoie un POST avec indicesActifsIds: [id]', () => {
    initialiserNominal();

    const checkbox: HTMLInputElement = fixture.nativeElement.querySelector('input[type="checkbox"]');
    checkbox.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(req.request.body).toEqual({
      indicesActifsIds: [1],
      hypothese: {},
    });
    req.flush(reponseInitiale);
  });

  it('F3 : choisir une valeur envoie hypothese, et aucune retire la cle', () => {
    initialiserNominal();

    const select: HTMLSelectElement = fixture.nativeElement.querySelector('select');
    select.value = 'Mme Duval';
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    const reqAvecHypothese = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(reqAvecHypothese.request.body.hypothese).toEqual({ SUSPECT: 'Mme Duval' });
    reqAvecHypothese.flush(reponseInitiale);

    select.value = '';
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    const reqSansHypothese = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(reqSansHypothese.request.body.hypothese).toEqual({});
    reqSansHypothese.flush(reponseInitiale);
  });

  it('F4 : affiche le total, les comptages et marque une valeur a 0 comme eliminee', () => {
    initialiserNominal();

    const checkbox: HTMLInputElement = fixture.nativeElement.querySelector('input[type="checkbox"]');
    checkbox.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    req.flush({
      totalScenariosCompatibles: 2,
      noeudsVisites: 4,
      comptageParVariable: [
        {
          variable: 'SUSPECT',
          valeurs: [
            { valeur: 'Prof. Leblanc', scenarios: 0 },
            { valeur: 'Mme Duval', scenarios: 2 },
          ],
        },
        {
          variable: 'LIEU',
          valeurs: [
            { valeur: 'Salle serveur', scenarios: 1 },
            { valeur: 'Amphi A', scenarios: 1 },
          ],
        },
      ],
    });
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('.total')?.textContent).toContain('2');
    const eliminees = el.querySelectorAll('li.elimine');
    expect(eliminees).toHaveLength(1);
    expect(eliminees[0].textContent).toContain('Prof. Leblanc');
  });

  it('F5 : affiche le detail d une erreur 400 dans role=alert', () => {
    initialiserNominal();

    const checkbox: HTMLInputElement = fixture.nativeElement.querySelector('input[type="checkbox"]');
    checkbox.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    req.flush(
      { title: 'Requête invalide', status: 400, detail: 'Indice inconnu : 999' },
      { status: 400, statusText: 'Bad Request' }
    );
    fixture.detectChanges();

    const alerte = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alerte).not.toBeNull();
    expect(alerte.textContent).toContain('Indice inconnu : 999');
  });

  it('F6 : deux changements rapproches annulent la premiere requete', () => {
    initialiserNominal();

    const checkboxes: NodeListOf<HTMLInputElement> =
      fixture.nativeElement.querySelectorAll('input[type="checkbox"]');

    checkboxes[0].click();
    fixture.detectChanges();
    const req1 = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');

    checkboxes[1].click();
    fixture.detectChanges();
    expect(req1.cancelled).toBe(true);

    const req2 = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(req2.request.body.indicesActifsIds).toEqual([1, 2]);
    req2.flush(reponseInitiale);
  });

  it('F7 : un id non numerique affiche une erreur sans aucun appel HTTP', () => {
    fixture = creerComposantAvecRouteId('abc');
    fixture.detectChanges();

    httpMock.expectNone('http://localhost:8080/api/v1/joueur/enquetes/abc');
    const alerte = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alerte).not.toBeNull();
    expect(alerte.textContent).toContain('invalide');
  });

  it('F8 : reinitialiser vide les indices actifs et l hypothese', () => {
    initialiserNominal();

    const checkbox: HTMLInputElement = fixture.nativeElement.querySelector('input[type="checkbox"]');
    checkbox.click();
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages').flush(reponseInitiale);

    const select: HTMLSelectElement = fixture.nativeElement.querySelector('select');
    select.value = 'Mme Duval';
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages').flush(reponseInitiale);

    const boutonReset = Array.from(
      fixture.nativeElement.querySelectorAll('button') as NodeListOf<HTMLButtonElement>
    ).find((b) => b.textContent?.includes('Réinitialiser'))!;
    boutonReset.click();
    fixture.detectChanges();

    const reqReset = httpMock.expectOne('http://localhost:8080/api/v1/joueur/enquetes/1/filtrages');
    expect(reqReset.request.body).toEqual({
      indicesActifsIds: [],
      hypothese: {},
    });
    reqReset.flush(reponseInitiale);
  });
});