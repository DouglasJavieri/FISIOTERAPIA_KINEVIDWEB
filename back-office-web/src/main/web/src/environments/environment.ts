// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  /**
   * Rutas de los recursos del backend.
   * Uso: environment.apiUrl + environment.endpoints.permission + '/list'
   *   → http://localhost:8080/api/permission/list
   */
  endpoints: {
    auth: '/auth',
    user: '/user',
    role: '/role',
    permission: '/permission',
    userRole: '/user-role',
    rolePermission: '/role-permission',
    employee: '/employee',
    patient: '/patient',
    medicalService: '/medical-service',
    clinicalEpisode: '/clinical-episode',
    clinicalSession: '/clinical-session',
    footAnalysis:    '/foot-analysis',
  }
};

