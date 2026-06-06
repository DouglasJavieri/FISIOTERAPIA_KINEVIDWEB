import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import {
  FootAnalysisRequest,
  FootAnalysisResponse,
  FootAnalysisUpdateRequest,
} from '../../models/imaging/imaging.interface';

@Injectable({ providedIn: 'root' })
export class FootAnalysisService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.footAnalysis;
  }

  create(body: FootAnalysisRequest): Observable<FootAnalysisResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<FootAnalysisResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<FootAnalysisResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<FootAnalysisResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getBySessionId(sessionId: number): Observable<FootAnalysisResponse> {
    const url = this.base + `/session/${sessionId}`;
    return this.http.get<ApiResponse<FootAnalysisResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: FootAnalysisUpdateRequest): Observable<FootAnalysisResponse> {
    const url = this.base + `/update/${id}`;
    return this.http.put<ApiResponse<FootAnalysisResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  saveFull(body: any): Observable<FootAnalysisResponse> {
    const url = this.base + '/save-full';
    return this.http.post<ApiResponse<FootAnalysisResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  downloadReport(id: number): Observable<Blob> {
    const url = this.base + `/${id}/report/download`;
    return this.http.get(url, { responseType: 'blob' });
  }
}
