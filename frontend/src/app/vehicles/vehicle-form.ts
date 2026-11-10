import { Component, computed, inject, input, linkedSignal } from '@angular/core';
import { FormField, FormRoot, form, required } from '@angular/forms/signals';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { loadErrorMessage, problemDetailToFormErrors } from '../shared/problem-detail';
import { optionalNumberAttribute } from '../shared/route-params';
import {
  FUEL_TYPES,
  FUEL_TYPE_LABELS,
  FuelType,
  Vehicle,
  VehicleApi,
  VehicleRequest,
} from './vehicle-api';

interface VehicleFormModel {
  vin: string;
  make: string;
  model: string;
  productionYear: number | null;
  fuelType: FuelType | '';
  plannedHandoverDate: string;
}

function toFormModel(vehicle: Vehicle | undefined): VehicleFormModel {
  return {
    vin: vehicle?.vin ?? '',
    make: vehicle?.make ?? '',
    model: vehicle?.model ?? '',
    productionYear: vehicle?.productionYear ?? null,
    fuelType: vehicle?.fuelType ?? '',
    plannedHandoverDate: vehicle?.plannedHandoverDate ?? '',
  };
}

function toRequest(model: VehicleFormModel): VehicleRequest {
  return {
    vin: model.vin,
    make: model.make,
    model: model.model,
    productionYear: model.productionYear!,
    fuelType: model.fuelType as FuelType,
    plannedHandoverDate: model.plannedHandoverDate || null,
  };
}

@Component({
  selector: 'app-vehicle-form',
  imports: [FormField, FormRoot, RouterLink],
  templateUrl: './vehicle-form.html',
})
export class VehicleForm {
  private readonly vehicleApi = inject(VehicleApi);
  private readonly router = inject(Router);

  readonly id = input(undefined, { transform: optionalNumberAttribute });

  protected readonly isEdit = computed(() => this.id() !== undefined);
  protected readonly vehicle = this.vehicleApi.get(this.id);
  protected readonly fuelTypes = FUEL_TYPES;
  protected readonly fuelTypeLabels = FUEL_TYPE_LABELS;

  private readonly model = linkedSignal(() =>
    toFormModel(this.vehicle.hasValue() ? this.vehicle.value() : undefined),
  );

  protected readonly vehicleForm = form(
    this.model,
    (path) => {
      required(path.vin, { message: 'VIN is required' });
      required(path.make, { message: 'Make is required' });
      required(path.model, { message: 'Model is required' });
      required(path.productionYear, { message: 'Production year is required' });
      required(path.fuelType, { message: 'Fuel type is required' });
    },
    {
      submission: {
        action: async (vehicleForm) => {
          const request = toRequest(vehicleForm().value());
          const id = this.id();
          try {
            const saved = await firstValueFrom(
              id === undefined
                ? this.vehicleApi.create(request)
                : this.vehicleApi.update(id, request),
            );
            await this.router.navigate(['/vehicles', saved.id]);
            return undefined;
          } catch (error) {
            return problemDetailToFormErrors(error, vehicleForm);
          }
        },
      },
    },
  );

  protected loadError(error: unknown): string {
    return loadErrorMessage(error, 'Vehicle not found');
  }
}
