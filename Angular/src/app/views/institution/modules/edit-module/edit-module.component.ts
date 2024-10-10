import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {InstitutionResponse} from '../../../../shared/models/institution/InstitutionResponse';

@Component({
  selector: 'app-edit-module',
  templateUrl: './edit-module.component.html',
  styleUrls: ['./edit-module.component.scss']
})
export class EditModuleComponent implements OnInit {
  @Input() module: ModuleResponse;
  @Input() institution: InstitutionResponse;
  @Output() moduleUpdated = new EventEmitter<void>();
  editModuleForm: FormGroup;

  constructor(
      private fb: FormBuilder,
      private moduleService: ModuleService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.editModuleForm = this.fb.group({
      name: [this.module.name, Validators.required],
      description: [this.module.description, Validators.required],
      skills: [this.module.skills],
      duration: [this.module.duration, Validators.required],
      credit: [this.module.credit, Validators.required],
      semester: [
        this.module.semester === 'FIRST_SEMESTER'
            ? 'FIRST_SEMESTER'
            : this.module.semester === 'SECOND_SEMESTER'
                ? 'SECOND_SEMESTER'
                : null
      ],
    });
  }

  onSubmit() {
    if (this.editModuleForm.valid) {
      console.log(this.editModuleForm.value);
      this.moduleService.updateModule(this.module.id, this.editModuleForm.value).subscribe(
          () => {
            this.toastr.success('Module updated successfully');
            this.moduleUpdated.emit({ ...this.module, ...this.editModuleForm.value });
          },
          error => {
              if (error.error) {
                this.toastr.error(error.error);
              } else {
                this.toastr.error('Failed to update module');
              }

          }
      );
    }
  }
}
