import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  CloseEpisodeRequest,
  ClinicalEpisodeRequest,
  ClinicalEpisodeResponse,
} from '../../models/clinical/clinical.interface';

@Injectable({ providedIn: 'root' })
export class ClinicalEpisodeService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.clinicalEpisode;
  }

  /** Lista global paginada con filtros (pantalla Episodios Clínicos - Flujo 2) */
  getAll(queryParams: any): Observable<Paginator<ClinicalEpisodeResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + '/list';
    return this.http.get<ApiResponse<Paginator<ClinicalEpisodeResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Episodios de un paciente específico (acordeón perfil - Flujo 1) */
  getByPatient(patientId: number, queryParams: any): Observable<Paginator<ClinicalEpisodeResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + `/patient/${patientId}`;
    return this.http.get<ApiResponse<Paginator<ClinicalEpisodeResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<ClinicalEpisodeResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<ClinicalEpisodeResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: ClinicalEpisodeRequest): Observable<ClinicalEpisodeResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<ClinicalEpisodeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  close(id: number, body: CloseEpisodeRequest): Observable<ClinicalEpisodeResponse> {
    const url = this.base + `/${id}/close`;
    return this.http.patch<ApiResponse<ClinicalEpisodeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  reactivatePatient(patientId: number, body: ClinicalEpisodeRequest): Observable<ClinicalEpisodeResponse> {
    const url = this.base + `/patient/${patientId}/reactivate`;
    return this.http.post<ApiResponse<ClinicalEpisodeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

