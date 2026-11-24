import { Routes } from '@angular/router';
import { FiltrageEnquete } from './filtrage-enquete/filtrage-enquete';

export const routes: Routes = [
  // L'API ne liste pas encore les enquêtes : on ouvre l'enquête du jeu de démonstration.
  { path: '', pathMatch: 'full', redirectTo: 'enquetes/1' },
  { path: 'enquetes/:id', component: FiltrageEnquete },
];
