import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'vehicles' },
  {
    path: 'vehicles',
    loadChildren: () => import('./vehicles/vehicles.routes').then((m) => m.vehiclesRoutes),
  },
];
