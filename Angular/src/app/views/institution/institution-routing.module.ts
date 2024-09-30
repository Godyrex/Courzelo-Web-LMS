import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UsersComponent} from './users/users.component';
import {EditComponent} from './edit/edit.component';
import {HomeComponent} from './home/home.component';
import {AuthGuard} from '../../shared/services/auth-guard.service';
import {CourseComponent} from './course/course.component';
import {ClassComponent} from './class/class.component';
import {InvitationsComponent} from './invitations/invitations.component';
import {ProgramsComponent} from './programs/programs.component';
import {ModulesComponent} from './modules/modules.component';

const routes: Routes = [
  {
    path: ':institutionID/users',
    component: UsersComponent,
    canActivate: [AuthGuard],
    data: {
        roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID/invitations',
    component: InvitationsComponent,
    canActivate: [AuthGuard],
    data: {
      roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID/programs',
    component: ProgramsComponent,
    canActivate: [AuthGuard],
    data: {
      roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID/programs/:programID/modules',
    component: ModulesComponent,
    canActivate: [AuthGuard],
    data: {
      roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID/classes',
    component: ClassComponent,
    canActivate: [AuthGuard],
    data: {
      roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID/edit',
    component: EditComponent,
    canActivate: [AuthGuard],
    data: {
      roles: ['ADMIN']
    }
  },
  {
    path: ':institutionID',
    component: HomeComponent
  },
  {
    path: 'course/:courseID',
    component: CourseComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InstitutionRoutingModule { }
