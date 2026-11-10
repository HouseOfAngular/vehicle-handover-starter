import { Component, inject, input, numberAttribute } from '@angular/core';
import { RouterLink } from '@angular/router';
import { VehicleApi } from './vehicle-api';

@Component({
  selector: 'app-vehicle-detail',
  imports: [RouterLink],
  templateUrl: './vehicle-detail.html',
})
export class VehicleDetail {
  private readonly vehicleApi = inject(VehicleApi);

  readonly id = input.required({ transform: numberAttribute });

  protected readonly vehicle = this.vehicleApi.get(this.id);
}
