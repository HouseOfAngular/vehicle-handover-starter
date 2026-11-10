import { Component, computed, inject, input, linkedSignal } from '@angular/core';
import {
  FormField,
  FormRoot,
  form,
  max,
  maxLength,
  min,
  pattern,
  required,
} from '@angular/forms/signals';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { problemDetailToFormErrors } from '../shared/problem-detail';
import { Vehicle, VehicleApi, VehicleRequest } from './vehicle-api';

interface VehicleFormModel {
  vin: string;
  make: string;
  model: string;
  productionYear: number | null;
  plannedHandoverDate: string;
}

const MIN_PRODUCTION_YEAR = 1990;
const MAX_PRODUCTION_YEAR = new Date().getFullYear() + 1;

function toFormModel(vehicle: Vehicle | undefined): VehicleFormModel {
  return {
    vin: vehicle?.vin ?? '',
    make: vehicle?.make ?? '',
    model: vehicle?.model ?? '',
    productionYear: vehicle?.productionYear ?? null,
    plannedHandoverDate: vehicle?.plannedHandoverDate ?? '',
  };
}

function toRequest(model: VehicleFormModel): VehicleRequest {
  return {
    vin: model.vin,
    make: model.make,
    model: model.model,
    productionYear: model.productionYear!,
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

  readonly id = input(undefined, {
    transform: (value: string | undefined) => (value === undefined ? undefined : Number(value)),
  });

  protected readonly isEdit = computed(() => this.id() !== undefined);
  protected readonly vehicle = this.vehicleApi.get(this.id);

  private readonly model = linkedSignal(() => toFormModel(this.vehicle.value()));

  protected readonly vehicleForm = form(
    this.model,
    (path) => {
      required(path.vin, { message: 'VIN is required' });
      pattern(path.vin, /^[A-HJ-NPR-Z0-9]{17}$/, {
        message: 'VIN must be 17 characters: digits and capital letters except I, O, Q',
      });
      required(path.make, { message: 'Make is required' });
      maxLength(path.make, 50, { message: 'Make must be at most 50 characters' });
      required(path.model, { message: 'Model is required' });
      maxLength(path.model, 50, { message: 'Model must be at most 50 characters' });
      required(path.productionYear, { message: 'Production year is required' });
      min(path.productionYear, MIN_PRODUCTION_YEAR, {
        message: `Production year must be ${MIN_PRODUCTION_YEAR} or later`,
      });
      max(path.productionYear, MAX_PRODUCTION_YEAR, {
        message: `Production year must be ${MAX_PRODUCTION_YEAR} or earlier`,
      });
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
}
