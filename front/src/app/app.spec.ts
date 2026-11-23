import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { API_URL } from './api.service';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
  });

  it('affiche le titre et la version renvoyée par l\'API', async () => {
    const fixture = TestBed.createComponent(App);
    const http = TestBed.inject(HttpTestingController);
    http.expectOne(`${API_URL}/info`).flush({ name: 'cluedo-back', version: '0.1.0' });
    await fixture.whenStable();
    const texte = (fixture.nativeElement as HTMLElement).textContent;
    expect(texte).toContain('Cluedo');
    expect(texte).toContain('cluedo-back 0.1.0');
  });

  it('affiche l\'erreur de l\'API', async () => {
    const fixture = TestBed.createComponent(App);
    const http = TestBed.inject(HttpTestingController);
    http.expectOne(`${API_URL}/info`).flush({ detail: 'indisponible' }, { status: 500, statusText: 'KO' });
    await fixture.whenStable();
    expect((fixture.nativeElement as HTMLElement).querySelector('[role=alert]')?.textContent).toContain('indisponible');
  });
});
