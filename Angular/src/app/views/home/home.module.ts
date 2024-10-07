import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { HomeDashboardComponent } from './home-dashboard/home-dashboard.component';
import {SharedComponentsModule} from "../../shared/components/shared-components.module";


@NgModule({
  declarations: [
    HomeDashboardComponent,
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
      SharedComponentsModule
  ]
})
export class HomeModule { }
