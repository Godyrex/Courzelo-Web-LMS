import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';

@Component({
  selector: 'app-edit-module',
  templateUrl: './edit-module.component.html',
  styleUrls: ['./edit-module.component.scss']
})
export class EditModuleComponent implements OnInit {
  @Input() module: ModuleResponse;
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
      duration: [this.module.duration, Validators.required],
      credit: [this.module.credit, Validators.required]
    });
  }

  onSubmit() {
    if (this.editModuleForm.valid) {
      console.log(this.editModuleForm.value);
      this.moduleService.updateModule(this.module.id, this.editModuleForm.value).subscribe(
          () => {
            this.toastr.success('Program updated successfully');
            this.moduleUpdated.emit({ ...this.module, ...this.editModuleForm.value });
          },
          error => {
            this.handleResponse.handleError(error);
          }
      );
    }
  }
}
