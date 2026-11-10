import { Routes } from '@angular/router';
import { VehicleDetail } from './vehicle-detail';
import { VehicleForm } from './vehicle-form';
import { VehicleList } from './vehicle-list';

export const vehiclesRoutes: Routes = [
  { path: '', component: VehicleList },
  { path: 'new', component: VehicleForm },
  { path: ':id/edit', component: VehicleForm },
  { path: ':id', component: VehicleDetail },
];
