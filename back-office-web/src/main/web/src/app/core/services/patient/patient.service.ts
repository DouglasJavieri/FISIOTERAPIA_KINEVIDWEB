import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangePatientStatusRequest,
  PatientPageResponse,
  PatientRequest,
  PatientResponse,
  PatientUpdateRequest,
} from '../../models/patient/patient.interface';

@Injectable({ providedIn: 'root' })
export class PatientService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.patient;
  }

  getAll(queryParams: any): Observable<Paginator<PatientPageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + '/list';
    return this.http.get<ApiResponse<Paginator<PatientPageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<PatientResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<PatientResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getActiveList(): Observable<PatientResponse[]> {
    const url = this.base + '/active-list';
    return this.http.get<ApiResponse<PatientResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: PatientRequest): Observable<PatientResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<PatientResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: PatientUpdateRequest): Observable<PatientResponse> {
    const url = this.base + `/update/${id}`;
    return this.http.put<ApiResponse<PatientResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangePatientStatusRequest): Observable<PatientResponse> {
    const url = this.base + `/${id}/status`;
    return this.http.patch<ApiResponse<PatientResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

