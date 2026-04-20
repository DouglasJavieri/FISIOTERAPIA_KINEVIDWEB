import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangeMedicalServiceStatusRequest,
  MedicalServicePageResponse,
  MedicalServiceRequest,
  MedicalServiceResponse,
  MedicalServiceUpdateRequest,
} from '../../models/medical-service/medical-service.interface';

@Injectable({ providedIn: 'root' })
export class MedicalServiceService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.medicalService;
  }

  getAll(queryParams: any): Observable<Paginator<MedicalServicePageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + '/list';
    return this.http.get<ApiResponse<Paginator<MedicalServicePageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<MedicalServiceResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<MedicalServiceResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getActiveList(): Observable<MedicalServiceResponse[]> {
    const url = this.base + '/active-list';
    return this.http.get<ApiResponse<MedicalServiceResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: MedicalServiceRequest): Observable<MedicalServiceResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<MedicalServiceResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: MedicalServiceUpdateRequest): Observable<MedicalServiceResponse> {
    const url = this.base + `/update/${id}`;
    return this.http.put<ApiResponse<MedicalServiceResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangeMedicalServiceStatusRequest): Observable<MedicalServiceResponse> {
    const url = this.base + `/${id}/status`;
    return this.http.patch<ApiResponse<MedicalServiceResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

