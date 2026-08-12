import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class ReportService {

  constructor(private http: HttpClient) {}

  reportSheetPayrollPdf(): Observable<any> {
    const url = environment.apiUrl + environment.endpoints.report + '/report-user';

    const headers = new HttpHeaders({
      'Accept': 'application/json'
    });

    return this.http.get(url, {
      headers,
      observe: 'response',
      responseType: 'json'
    });
  }
}
