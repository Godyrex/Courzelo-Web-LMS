import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupResponse} from '../../../../shared/models/institution/GroupResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-view-courses',
  templateUrl: './view-courses.component.html',
  styleUrls: ['./view-courses.component.scss']
})
export class ViewCoursesComponent implements OnInit {

  @Input() program: string;
  @Input() group: GroupResponse;
  @Output() close = new EventEmitter<void>();
  modules: ModuleResponse[] = [];
  constructor(
      private moduleService: ModuleService,
      private toastr: ToastrService
  ) {
  }
  ngOnInit(): void {
    console.log(this.program);
    this.moduleService.getModules(0, 100, this.program, null).subscribe(
        modules => {
          console.log(modules);
          this.modules = modules.modules;
        },
        error => {
          this.toastr.error('Failed to fetch modules');
        }
    );
  }
    onClose() {
        this.close.emit();
    }

}
