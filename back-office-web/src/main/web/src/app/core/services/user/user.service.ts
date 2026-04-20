import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  ChangeUserStatusRequest,
  UserPageResponse,
  UserRequest,
  UserResponse,
  UserUpdateRequest,
} from '../../models/user/user.interface';

@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private http: HttpClient) {}

  getAll(queryParams: any): Observable<Paginator<UserPageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = environment.apiUrl + environment.endpoints.user + `/list`;
    return this.http.get<ApiResponse<Paginator<UserPageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<UserResponse> {
    const url = environment.apiUrl + environment.endpoints.user + `/${id}`;
    return this.http.get<ApiResponse<UserResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: UserRequest): Observable<UserResponse> {
    const url = environment.apiUrl + environment.endpoints.user + `/create`;
    return this.http.post<ApiResponse<UserResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: UserUpdateRequest): Observable<UserResponse> {
    const url = environment.apiUrl + environment.endpoints.user + `/update/${id}`;
    return this.http.put<ApiResponse<UserResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangeUserStatusRequest): Observable<UserResponse> {
    const url = environment.apiUrl + environment.endpoints.user + `/${id}/status`;
    return this.http.patch<ApiResponse<UserResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = environment.apiUrl + environment.endpoints.user + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

