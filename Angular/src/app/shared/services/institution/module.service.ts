import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ModuleRequest} from '../../models/institution/ModuleRequest';
import {PaginatedModulesResponse} from '../../models/institution/PaginatedModulesResponse';
import {ModuleResponse} from '../../models/institution/ModuleResponse';

@Injectable({
  providedIn: 'root'
})
export class ModuleService {
  private baseUrl = 'http://localhost:8080/api/v1/module';

  constructor(private http: HttpClient) { }

  createModule(moduleRequest: ModuleRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/create`, moduleRequest);
  }

  updateModule(id: string, moduleRequest: ModuleRequest): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, moduleRequest);
  }

  deleteModule(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getModules(page: number, sizePerPage: number, programID: string, keyword?: string): Observable<PaginatedModulesResponse> {
    let params = new HttpParams()
        .set('page', page.toString())
        .set('sizePerPage', sizePerPage.toString())
        .set('programID', programID);
    if (keyword) {
      params = params.set('keyword', keyword);
    }
    return this.http.get<PaginatedModulesResponse>(`${this.baseUrl}/`, { params });
  }

  getModule(id: string): Observable<ModuleResponse> {
    return this.http.get<ModuleResponse>(`${this.baseUrl}/${id}`);
  }
}
