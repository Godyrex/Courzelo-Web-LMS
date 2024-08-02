import { BrowserModule } from '@angular/platform-browser';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './shared/inmemory-db/inmemory-db.service';


import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { AddProjectComponent } from './shared/components/Project/Admin/add-project/add-project.component';
import { DashboardProjectComponent } from './shared/components/Project/Admin/dashboard-project/dashboard-project.component';
import { ViewdetailsComponent } from './shared/components/Project/Admin/viewdetails/viewdetails.component';
import { PdfComponent } from './shared/components/Project/User/pdf/pdf.component';
import { ProjectComponent } from './shared/components/Project/User/project/project.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ProjectDetailsComponent } from './shared/components/Project/User/projectdetails/projectdetails.component';
import { ProgressDashboardComponent } from './shared/components/Project/User/progress-dashboard/progress-dashboard.component';

import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {Interceptor} from './shared/services/user/Interceptor';
import { ProjectCalendarComponent } from './shared/components/Project/User/project-calendar/project-calendar.component';
import { ColorPickerModule } from 'ngx-color-picker';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CalendarRoutingModule } from './views/calendar/calendar-routing.module';
import { CalendarFormProjectComponent } from './shared/components/Project/User/calendar-form-project/calendar-form-project.component';
import { NgbDatepickerModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PublicationComponent } from './shared/components/Project/User/publication/publication.component';
import { AddpublicationComponent } from './shared/components/Project/User/addpublication/addpublication.component';
import { SharedDirectivesModule } from './shared/directives/shared-directives.module';
import { SharedPipesModule } from './shared/pipes/shared-pipes.module';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { ChatRoutingModule } from './views/chat/chat-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    AddProjectComponent,
    DashboardProjectComponent,
    ViewdetailsComponent ,
    PdfComponent ,
    ProjectComponent,
    ProjectDetailsComponent,
    ProgressDashboardComponent,
   ProjectCalendarComponent ,
   CalendarFormProjectComponent,
   PublicationComponent,
   AddpublicationComponent,
 


   
  ],
  imports: [
    BrowserModule,
    SharedModule,
    HttpClientModule,
    BrowserAnimationsModule,
    InMemoryWebApiModule.forRoot(InMemoryDataService, { passThruUnknownUrl: true }),
    AppRoutingModule ,
    FormsModule,
    CommonModule,
    ReactiveFormsModule ,
    BrowserAnimationsModule,
   DragDropModule ,
   RouterModule.forRoot([]),
   ColorPickerModule,
   CalendarModule.forRoot({
     provide: DateAdapter,
     useFactory: adapterFactory
   }),
   CalendarRoutingModule ,
   ColorPickerModule ,
   NgbDatepickerModule,
   NgbModule,
   SharedDirectivesModule,
   SharedPipesModule,
   PerfectScrollbarModule,
   ChatRoutingModule,

  ],
  providers: [
    DatePipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA] 




})
export class AppModule { }
