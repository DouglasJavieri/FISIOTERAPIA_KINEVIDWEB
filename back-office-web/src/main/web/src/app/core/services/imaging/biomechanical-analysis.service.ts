import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import {
  BiomechanicalAnalysisRequest,
  BiomechanicalAnalysisResponse,
} from '../../models/imaging/imaging.interface';

@Injectable({ providedIn: 'root' })
export class BiomechanicalAnalysisService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.footAnalysis;
  }

  /**
   * Upsert — crea o actualiza el análisis biomecánico de un pie (LEFT o RIGHT).
   */
  saveOrUpdate(footAnalysisId: number, body: BiomechanicalAnalysisRequest): Observable<BiomechanicalAnalysisResponse> {
    const url = this.base + `/${footAnalysisId}/biomechanical`;
    return this.http.post<ApiResponse<BiomechanicalAnalysisResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getByFootAnalysisId(footAnalysisId: number): Observable<BiomechanicalAnalysisResponse[]> {
    const url = this.base + `/${footAnalysisId}/biomechanical`;
    return this.http.get<ApiResponse<BiomechanicalAnalysisResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<BiomechanicalAnalysisResponse> {
    const url = this.base + `/biomechanical/${id}`;
    return this.http.get<ApiResponse<BiomechanicalAnalysisResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/biomechanical/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}
