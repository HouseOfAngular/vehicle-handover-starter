import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { VehicleForm } from './vehicle-form';

describe('VehicleForm', () => {
  let fixture: ComponentFixture<VehicleForm>;
  let element: HTMLElement;
  let http: HttpTestingController;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    fixture = TestBed.createComponent(VehicleForm);
    element = fixture.nativeElement;
    http = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
  });

  afterEach(() => http.verify());

  function fill(type: string, index: number, value: string): void {
    const input = element.querySelectorAll<HTMLInputElement>(`input[type="${type}"]`)[index];
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function fillValidVehicle(): void {
    fill('text', 0, 'WVWZZZ1KZAW000001');
    fill('text', 1, 'Volkswagen');
    fill('text', 2, 'Golf');
    fill('number', 0, '2025');
  }

  async function submit(): Promise<void> {
    element.querySelector('form')!.dispatchEvent(new Event('submit'));
    await fixture.whenStable();
  }

  function errors(): string[] {
    return Array.from(element.querySelectorAll('.error'), (error) => error.textContent!.trim());
  }

  it('shows validation errors and sends nothing when the form is invalid', async () => {
    fill('text', 0, 'TOO-SHORT');

    await submit();

    http.expectNone('/api/vehicles');
    expect(errors()).toEqual([
      'VIN must be 17 characters: digits and capital letters except I, O, Q',
      'Make is required',
      'Model is required',
      'Production year is required',
    ]);
  });

  it('creates the vehicle and navigates to its details', async () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fillValidVehicle();
    fill('date', 0, '2026-12-15');

    await submit();
    const request = http.expectOne({ method: 'POST', url: '/api/vehicles' });
    expect(request.request.body).toEqual({
      vin: 'WVWZZZ1KZAW000001',
      make: 'Volkswagen',
      model: 'Golf',
      productionYear: 2025,
      plannedHandoverDate: '2026-12-15',
    });
    request.flush({ id: 7, ...request.request.body });

    await vi.waitFor(() => expect(navigate).toHaveBeenCalledWith(['/vehicles', 7]));
  });

  it('loads the vehicle in edit mode and updates it', async () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const vehicle = {
      id: 3,
      vin: 'TMBJJ7NE8L0000003',
      make: 'Skoda',
      model: 'Octavia',
      productionYear: 2026,
      plannedHandoverDate: null,
    };
    fixture.componentRef.setInput('id', '3');
    fixture.detectChanges();
    http.expectOne({ method: 'GET', url: '/api/vehicles/3' }).flush(vehicle);
    await fixture.whenStable();
    fill('text', 2, 'Superb');

    await submit();
    const request = http.expectOne({ method: 'PUT', url: '/api/vehicles/3' });
    expect(request.request.body).toEqual({
      vin: 'TMBJJ7NE8L0000003',
      make: 'Skoda',
      model: 'Superb',
      productionYear: 2026,
      plannedHandoverDate: null,
    });
    request.flush({ ...vehicle, model: 'Superb' });

    await vi.waitFor(() => expect(navigate).toHaveBeenCalledWith(['/vehicles', 3]));
  });

  it('shows field errors returned by the backend as ProblemDetail', async () => {
    fillValidVehicle();

    await submit();
    http.expectOne({ method: 'POST', url: '/api/vehicles' }).flush(
      {
        title: 'Bad Request',
        status: 400,
        detail: 'Validation failed',
        errors: [{ field: 'productionYear', message: 'must be between 1990 and next year' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );

    await vi.waitFor(() => expect(errors()).toEqual(['must be between 1990 and next year']));
  });

  it('shows the ProblemDetail detail when the backend reports a conflict', async () => {
    fillValidVehicle();

    await submit();
    http.expectOne({ method: 'POST', url: '/api/vehicles' }).flush(
      {
        title: 'Conflict',
        status: 409,
        detail: 'Vehicle with VIN WVWZZZ1KZAW000001 already exists',
      },
      { status: 409, statusText: 'Conflict' },
    );

    await vi.waitFor(() =>
      expect(errors()).toEqual(['Vehicle with VIN WVWZZZ1KZAW000001 already exists']),
    );
  });
});
