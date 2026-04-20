import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import { UserRoleRequest, UserRoleResponse } from '../../models/user-role/user-role.interface';
import { RoleResponse } from '../../models/roles/role.interface';

@Injectable({ providedIn: 'root' })
export class UserRoleService {

  constructor(private http: HttpClient) {}

  /** Obtiene los roles asignados a un usuario. */
  getRolesByUserId(userId: number): Observable<RoleResponse[]> {
    const url = environment.apiUrl + environment.endpoints.userRole + `/${userId}/roles`;
    return this.http.get<ApiResponse<RoleResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Asigna un rol a un usuario. */
  assign(request: UserRoleRequest): Observable<UserRoleResponse> {
    const url = environment.apiUrl + environment.endpoints.userRole + `/assign`;
    return this.http.post<ApiResponse<UserRoleResponse>>(url, request)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Remueve un rol de un usuario. */
  remove(userId: number, roleId: number): Observable<boolean> {
    const url = environment.apiUrl + environment.endpoints.userRole + `/remove/${userId}/${roleId}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /**
   * Sincroniza el rol del usuario: si tenía un rol diferente, lo remueve y asigna el nuevo.
   * Si el rol no cambió, no hace nada.
   */
  syncRole(userId: number, newRoleId: number | null, currentRoleId: number | null): Observable<any> {
    if (newRoleId === currentRoleId) return of(null);

    if (currentRoleId !== null && newRoleId !== null) {
      // Cambió el rol: remover viejo y asignar nuevo
      return this.remove(userId, currentRoleId).pipe(
        switchMap(() => this.assign({ userId, roleId: newRoleId }))
      );
    }
    if (currentRoleId !== null && newRoleId === null) {
      // Se quitó el rol
      return this.remove(userId, currentRoleId);
    }
    if (newRoleId !== null) {
      // Se asignó un rol por primera vez
      return this.assign({ userId, roleId: newRoleId });
    }
    return of(null);
  }
}

