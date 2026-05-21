import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import {
  FootprintAnalysisRequest,
  FootprintAnalysisResponse,
} from '../../models/imaging/imaging.interface';

@Injectable({ providedIn: 'root' })
export class FootprintAnalysisService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.footAnalysis;
  }

  /**
   * Upsert — crea o actualiza la Valoración de Hernández Corvo.
   * Solo puede existir UNA valoración activa por análisis.
   */
  saveOrUpdate(footAnalysisId: number, body: FootprintAnalysisRequest): Observable<FootprintAnalysisResponse> {
    const url = this.base + `/${footAnalysisId}/footprint`;
    return this.http.post<ApiResponse<FootprintAnalysisResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getByFootAnalysisId(footAnalysisId: number): Observable<FootprintAnalysisResponse> {
    const url = this.base + `/${footAnalysisId}/footprint`;
    return this.http.get<ApiResponse<FootprintAnalysisResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<FootprintAnalysisResponse> {
    const url = this.base + `/footprint/${id}`;
    return this.http.get<ApiResponse<FootprintAnalysisResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/footprint/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}
