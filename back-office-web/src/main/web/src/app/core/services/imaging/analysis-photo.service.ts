import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import {
  AnalysisPhotoResponse,
  AnnotationsUpdateRequest,
} from '../../models/imaging/imaging.interface';

@Injectable({ providedIn: 'root' })
export class AnalysisPhotoService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.footAnalysis;
  }

  /**
   * Sube una foto al análisis.
   * Usa FormData porque el backend espera multipart/form-data.
   */
  upload(footAnalysisId: number, file: File, photoOrder: number): Observable<AnalysisPhotoResponse> {
    const url = this.base + `/${footAnalysisId}/photos`;
    const formData = new FormData();
    formData.append('file', file);
    formData.append('photoOrder', photoOrder.toString());
    return this.http.post<ApiResponse<AnalysisPhotoResponse>>(url, formData)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getByAnalysisId(footAnalysisId: number): Observable<AnalysisPhotoResponse[]> {
    const url = this.base + `/${footAnalysisId}/photos`;
    return this.http.get<ApiResponse<AnalysisPhotoResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<AnalysisPhotoResponse> {
    const url = this.base + `/photos/${id}`;
    return this.http.get<ApiResponse<AnalysisPhotoResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  updateAnnotations(id: number, body: AnnotationsUpdateRequest): Observable<AnalysisPhotoResponse> {
    const url = this.base + `/photos/${id}/annotations`;
    return this.http.put<ApiResponse<AnalysisPhotoResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  toggleSelection(id: number): Observable<AnalysisPhotoResponse> {
    const url = this.base + `/photos/${id}/select`;
    return this.http.patch<ApiResponse<AnalysisPhotoResponse>>(url, {})
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/photos/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}
