import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { problemDetailMessage } from '../shared/problem-detail';
import { FUEL_TYPE_LABELS, Vehicle, VehicleApi } from './vehicle-api';

@Component({
  selector: 'app-vehicle-list',
  imports: [RouterLink],
  templateUrl: './vehicle-list.html',
})
export class VehicleList {
  private readonly vehicleApi = inject(VehicleApi);

  protected readonly vehicles = this.vehicleApi.list();
  protected readonly fuelTypeLabels = FUEL_TYPE_LABELS;
  protected readonly deleteError = signal<string | null>(null);

  protected loadError(error: unknown): string {
    return problemDetailMessage(error);
  }

  protected async delete(vehicle: Vehicle): Promise<void> {
    this.deleteError.set(null);
    try {
      await firstValueFrom(this.vehicleApi.delete(vehicle.id));
      this.vehicles.reload();
    } catch (error) {
      this.deleteError.set(problemDetailMessage(error));
    }
  }
}
