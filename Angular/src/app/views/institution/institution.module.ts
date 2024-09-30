import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {InstitutionRoutingModule} from './institution-routing.module';
import {UsersComponent} from './users/users.component';
import {EditComponent} from './edit/edit.component';
import {HomeComponent} from './home/home.component';
import {NgxPaginationModule} from 'ngx-pagination';
import {NgxDatatableModule} from '@swimlane/ngx-datatable';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedComponentsModule} from '../../shared/components/shared-components.module';
import {NgbDatepickerModule, NgbTabsetModule} from '@ng-bootstrap/ng-bootstrap';
import {CourseComponent, TimeRemainingPipe} from './course/course.component';
import { ClassComponent } from './class/class.component';
import { InvitationsComponent } from './invitations/invitations.component';
import {TagInputModule} from 'ngx-chips';
import {ToolsModule} from '../tools/tools.module';
import { ProgramsComponent } from './programs/programs.component';
import { AddProgramComponent } from './programs/add-program/add-program.component';
import { EditProgramComponent } from './programs/edit-program/edit-program.component';
import { ModulesComponent } from './modules/modules.component';
import { AddModuleComponent } from './modules/add-module/add-module.component';
import { EditModuleComponent } from './modules/edit-module/edit-module.component';
import { AddClassComponent } from './class/add-class/add-class.component';
import { EditClassComponent } from './class/edit-class/edit-class.component';
import { ViewCoursesComponent } from './class/view-courses/view-courses.component';
import { ViewStudentsComponent } from './class/view-students/view-students.component';


@NgModule({
  declarations: [
    UsersComponent,
    EditComponent,
    HomeComponent,
    CourseComponent,
    ClassComponent,
    InvitationsComponent,
    ProgramsComponent,
    AddProgramComponent,
    EditProgramComponent,
    ModulesComponent,
    AddModuleComponent,
    EditModuleComponent,
    AddClassComponent,
    EditClassComponent,
    ViewCoursesComponent,
    ViewStudentsComponent
  ],
    imports: [
        CommonModule,
        InstitutionRoutingModule,
        NgxPaginationModule,
        NgxDatatableModule,
        FormsModule,
        ReactiveFormsModule,
        SharedComponentsModule,
        NgbTabsetModule,
        NgbDatepickerModule,
        TimeRemainingPipe,
        TagInputModule,
        ToolsModule
    ]
})
export class InstitutionModule { }
