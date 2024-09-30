import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ModuleRequest} from '../../../../shared/models/institution/ModuleRequest';

@Component({
  selector: 'app-add-module',
  templateUrl: './add-module.component.html',
  styleUrls: ['./add-module.component.scss']
})
export class AddModuleComponent {
  @Output() moduleAdded = new EventEmitter<void>();
  @Input() programID: string;
  addModuleForm: FormGroup;
  moduleRequest: ModuleRequest;

  constructor(
      private fb: FormBuilder,
      private moduleService: ModuleService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) {
    this.addModuleForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      duration: ['', Validators.required],
      credit: [0, Validators.required],
    });
  }

  onSubmit() {
    if (this.addModuleForm.valid) {
      this.moduleRequest = this.addModuleForm.value;
        this.moduleRequest.program = this.programID;
        console.log(this.moduleRequest);
      this.moduleService.createModule(this.moduleRequest).subscribe(
          () => {
            this.toastr.success('Program added successfully');
            this.moduleAdded.emit();
          },
          error => {
            this.handleResponse.handleError(error);
          }
      );
    }
  }
}
