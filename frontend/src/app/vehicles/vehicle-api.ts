import { HttpClient, httpResource } from '@angular/common/http';
import { Injectable, Signal, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface Vehicle {
  id: number;
  vin: string;
  make: string;
  model: string;
  productionYear: number;
  plannedHandoverDate: string | null;
}

export type VehicleRequest = Omit<Vehicle, 'id'>;

@Injectable({ providedIn: 'root' })
export class VehicleApi {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/vehicles';

  list() {
    return httpResource<Vehicle[]>(() => this.url, { defaultValue: [] });
  }

  get(id: Signal<number | undefined>) {
    return httpResource<Vehicle>(() => {
      const vehicleId = id();
      return vehicleId === undefined ? undefined : `${this.url}/${vehicleId}`;
    });
  }

  create(request: VehicleRequest): Observable<Vehicle> {
    return this.http.post<Vehicle>(this.url, request);
  }

  update(id: number, request: VehicleRequest): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
