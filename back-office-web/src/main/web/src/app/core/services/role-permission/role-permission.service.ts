import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/models/api-response';
import { mapResponseApi } from '../../../shared/utils/http.util';
import { RolePermissionRequest, RolePermissionResponse } from '../../models/role-permission/role-permission.interface';
import { PermissionResponse } from '../../models/permission/permission.interface';

@Injectable({ providedIn: 'root' })
export class RolePermissionService {

  constructor(private http: HttpClient) {}

  /** Obtiene los permisos asignados a un rol. */
  getPermissionsByRoleId(roleId: number): Observable<PermissionResponse[]> {
    const url = environment.apiUrl + environment.endpoints.rolePermission + `/${roleId}/permissions`;
    return this.http.get<ApiResponse<PermissionResponse[]>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Asigna un permiso a un rol. */
  assign(request: RolePermissionRequest): Observable<RolePermissionResponse> {
    const url = environment.apiUrl + environment.endpoints.rolePermission + `/assign`;
    return this.http.post<ApiResponse<RolePermissionResponse>>(url, request)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Remueve un permiso de un rol. */
  remove(roleId: number, permissionId: number): Observable<boolean> {
    const url = environment.apiUrl + environment.endpoints.rolePermission + `/remove/${roleId}/${permissionId}`;
    return this.http.delete<ApiResponse<boolean>>(url)
      .pipe(map(resp => mapResponseApi(url, resp)));
  }

  /** Asigna múltiples permisos al rol en paralelo. */
  assignMany(roleId: number, permissionIds: number[]): Observable<RolePermissionResponse[]> {
    if (permissionIds.length === 0) return of([]);
    return forkJoin(
      permissionIds.map(permissionId => this.assign({ roleId, permissionId }))
    );
  }

  /** Remueve múltiples permisos del rol en paralelo. */
  removeMany(roleId: number, permissionIds: number[]): Observable<boolean[]> {
    if (permissionIds.length === 0) return of([]);
    return forkJoin(
      permissionIds.map(permissionId => this.remove(roleId, permissionId))
    );
  }

  syncPermissions(roleId: number, newPermIds: number[], currentPermIds: number[]): Observable<any> {
    const currentSet = new Set(currentPermIds);
    const newSet     = new Set(newPermIds);
    const toAdd      = newPermIds.filter(id => !currentSet.has(id));
    const toRemove   = currentPermIds.filter(id => !newSet.has(id));

    const ops: Observable<any>[] = [];
    if (toAdd.length > 0)    ops.push(this.assignMany(roleId, toAdd));
    if (toRemove.length > 0) ops.push(this.removeMany(roleId, toRemove));

    return ops.length > 0 ? forkJoin(ops) : of(null);
  }
}

