import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { Paginator } from '../../../shared/models/paginator.model';
import { buildHttpParams, mapResponseApi } from '../../../shared/utils/http.util';
import {
  AssignUserToEmployeeRequest,
  ChangeEmployeeStatusRequest,
  EmployeePageResponse,
  EmployeeRequest,
  EmployeeResponse,
  EmployeeUpdateRequest,
} from '../../models/employee/employee.interface';
import { UserResponse } from '../../models/user/user.interface';

@Injectable({ providedIn: 'root' })
export class EmployeeService {

  constructor(private http: HttpClient) {}

  private get base(): string {
    return environment.apiUrl + environment.endpoints.employee;
  }

  getAll(queryParams: any): Observable<Paginator<EmployeePageResponse>> {
    const params = buildHttpParams(queryParams);
    const url = this.base + '/list';
    return this.http.get<ApiResponse<Paginator<EmployeePageResponse>>>(url, { params })
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getById(id: number): Observable<EmployeeResponse> {
    const url = this.base + `/${id}`;
    return this.http.get<ApiResponse<EmployeeResponse>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  create(body: EmployeeRequest): Observable<EmployeeResponse> {
    const url = this.base + '/create';
    return this.http.post<ApiResponse<EmployeeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  update(id: number, body: EmployeeUpdateRequest): Observable<EmployeeResponse> {
    const url = this.base + `/update/${id}`;
    return this.http.put<ApiResponse<EmployeeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  changeStatus(id: number, body: ChangeEmployeeStatusRequest): Observable<EmployeeResponse> {
    const url = this.base + `/${id}/status`;
    return this.http.patch<ApiResponse<EmployeeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  delete(id: number): Observable<boolean> {
    const url = this.base + `/delete/${id}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  assignUser(employeeId: number, body: AssignUserToEmployeeRequest): Observable<EmployeeResponse> {
    const url = this.base + `/${employeeId}/assign-user`;
    return this.http.patch<ApiResponse<EmployeeResponse>>(url, body)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  removeUser(employeeId: number): Observable<EmployeeResponse> {
    const url = this.base + `/${employeeId}/remove-user`;
    return this.http.patch<ApiResponse<EmployeeResponse>>(url, {})
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  getUsersAvailable(): Observable<UserResponse[]> {
    const url = this.base + '/users-available';
    return this.http.get<ApiResponse<UserResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }
}

