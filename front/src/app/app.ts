import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './api.service';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  private readonly api = inject(ApiService);

  protected readonly version = signal('');
  protected readonly erreur = signal('');

  constructor() {
    this.api.info().subscribe({
      next: ({ name, version }) => this.version.set(`${name} ${version}`),
      error: (e) => this.erreur.set(e.error?.detail ?? e.error?.title ?? `Erreur HTTP ${e.status}`),
    });
  }
}
