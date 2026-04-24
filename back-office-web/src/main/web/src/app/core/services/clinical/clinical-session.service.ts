import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangeSessionStatusRequest,
  ClinicalSessionRequest,
  ClinicalSessionResponse,
  ClinicalSessionUpdateRequest,
} from '../../models/clinical/clinical.interface';

@Injectable({ providedIn: 'root' })
export class ClinicalSessionService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.clinicalSession;
  }

  getByEpisode(episodeId: number, queryParams: any): Observable<Paginator<ClinicalSessionResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + `/episode/${episodeId}`;
    return this.http.get<ApiResponse<Paginator<ClinicalSessionResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<ClinicalSessionResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<ClinicalSessionResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: ClinicalSessionRequest): Observable<ClinicalSessionResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<ClinicalSessionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: ClinicalSessionUpdateRequest): Observable<ClinicalSessionResponse> {
    const url = this.base + `/update/${id}`;
    return this.http.put<ApiResponse<ClinicalSessionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangeSessionStatusRequest): Observable<ClinicalSessionResponse> {
    const url = this.base + `/${id}/status`;
    return this.http.patch<ApiResponse<ClinicalSessionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

