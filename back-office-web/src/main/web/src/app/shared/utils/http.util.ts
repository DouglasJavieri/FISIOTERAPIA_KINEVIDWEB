import { HttpParams }    from '@angular/common/http';
import { ErrorResponse } from '../models/error-response';
import { ApiResponse }   from '../models/api-response';
import { Constants }     from './constants';

export function defaultHttpError(path: string, code: string, message: string): ErrorResponse {
  return {
    code,
    message,
    path,
    status: 0,
    timestamp: new Date().toISOString(),
  };
}

export const mapResponseApi = <T>(url: string, resp: ApiResponse<T>): T => {
  if (resp.code !== Constants.OK_CODE) {
    throw defaultHttpError(url, resp.code, resp.message);
  }
  return resp.data;
};

export const buildHttpParams = (queryParams: Record<string, any>): HttpParams => {
  let params = new HttpParams();
  Object.entries(queryParams).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      params = params.set(key, String(value));
    }
  });
  return params;
};


