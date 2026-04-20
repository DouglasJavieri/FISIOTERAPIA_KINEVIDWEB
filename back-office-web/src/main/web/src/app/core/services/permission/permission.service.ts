import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangePermissionStatusRequest,
  PermissionPageResponse,
  PermissionRequest,
  PermissionResponse
} from "../../models/permission/permission.interface";

@Injectable({ providedIn: 'root' })
export class PermissionService {


  constructor(private http: HttpClient) {}

  getAll(queryParams: any): Observable<Paginator<PermissionPageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = environment.apiUrl + environment.endpoints.permission + `/list`;
    return this.http.get<ApiResponse<Paginator<PermissionPageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getAllForSelect(): Observable<PermissionPageResponse[]> {
    return this.getAll({ page: 0, size: 500, sortBy: 'name', sortDir: 'ASC', status: 'ACTIVE' })
      .pipe(map(paginator => paginator.content));
  }

  getById(id: number): Observable<PermissionResponse> {
    const url = environment.apiUrl + environment.endpoints.permission + `/${id}`;
    return this.http.get<ApiResponse<PermissionResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: PermissionRequest): Observable<PermissionResponse> {
    const url = environment.apiUrl + environment.endpoints.permission + `/create`;
    return this.http.post<ApiResponse<PermissionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: PermissionRequest): Observable<PermissionResponse> {
    const url = environment.apiUrl + environment.endpoints.permission + `/update/${id}`;
    return this.http.put<ApiResponse<PermissionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangePermissionStatusRequest): Observable<PermissionResponse> {
    const url = environment.apiUrl + environment.endpoints.permission + `/${id}/status`;
    return this.http.patch<ApiResponse<PermissionResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = environment.apiUrl + environment.endpoints.permission + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}


