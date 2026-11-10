import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { problemDetailMessage } from '../shared/problem-detail';
import { Vehicle, VehicleApi } from './vehicle-api';

@Component({
  selector: 'app-vehicle-list',
  imports: [RouterLink],
  templateUrl: './vehicle-list.html',
})
export class VehicleList {
  private readonly vehicleApi = inject(VehicleApi);

  protected readonly vehicles = this.vehicleApi.list();
  protected readonly deleteError = signal<string | null>(null);

  protected delete(vehicle: Vehicle): void {
    this.deleteError.set(null);
    this.vehicleApi.delete(vehicle.id).subscribe({
      next: () => this.vehicles.reload(),
      error: (error: unknown) => this.deleteError.set(problemDetailMessage(error)),
    });
  }
}
