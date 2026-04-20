import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangeRoleStatusRequest,
  RolePageResponse,
  RoleRequest,
  RoleResponse,
} from '../../models/roles/role.interface';

@Injectable({ providedIn: 'root' })
export class RoleService {

  constructor(private http: HttpClient) {}

  getAll(queryParams: any): Observable<Paginator<RolePageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = environment.apiUrl + environment.endpoints.role + `/list`;
    return this.http.get<ApiResponse<Paginator<RolePageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<RoleResponse> {
    const url = environment.apiUrl + environment.endpoints.role + `/${id}`;
    return this.http.get<ApiResponse<RoleResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: RoleRequest): Observable<RoleResponse> {
    const url = environment.apiUrl + environment.endpoints.role + `/create`;
    return this.http.post<ApiResponse<RoleResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: RoleRequest): Observable<RoleResponse> {
    const url = environment.apiUrl + environment.endpoints.role + `/update/${id}`;
    return this.http.put<ApiResponse<RoleResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangeRoleStatusRequest): Observable<RoleResponse> {
    const url = environment.apiUrl + environment.endpoints.role + `/${id}/status`;
    return this.http.patch<ApiResponse<RoleResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = environment.apiUrl + environment.endpoints.role + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

