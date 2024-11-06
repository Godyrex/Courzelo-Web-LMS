import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ModuleRequest} from '../../../../shared/models/institution/ModuleRequest';
import {InstitutionResponse} from '../../../../shared/models/institution/InstitutionResponse';

@Component({
  selector: 'app-add-module',
  templateUrl: './add-module.component.html',
  styleUrls: ['./add-module.component.scss']
})
export class AddModuleComponent {
  @Output() moduleAdded = new EventEmitter<void>();
  @Input() programID: string;
  @Input() institution: InstitutionResponse;
  addModuleForm: FormGroup;
  moduleRequest: ModuleRequest;
  currentInstitution: InstitutionResponse;

  constructor(
      private fb: FormBuilder,
      private moduleService: ModuleService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) {
    this.addModuleForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      skills: [[]],
      duration: ['', Validators.required],
      credit: [0, Validators.required],
      scoreToPass: [0, Validators.required],
      semester: [null],
      moduleParts: this.fb.array([])

    });
  }
  get moduleParts(): FormArray {
    return this.addModuleForm.get('moduleParts') as FormArray;
  }

  addModulePart(): void {
    this.moduleParts.push(this.fb.group({
      name: ['', Validators.required],
      value: [0, Validators.required]
    }));
  }

  removeModulePart(index: number): void {
    this.moduleParts.removeAt(index);
  }
  onSubmit() {
    if (this.addModuleForm.valid) {
      console.log(this.addModuleForm.value);
      const formValue = this.addModuleForm.value;
      const modulePartsMap = {};

      formValue.moduleParts.forEach(part => {
        modulePartsMap[part.name] = part.value;
      });

      this.moduleRequest = {
        ...formValue,
        moduleParts: modulePartsMap,
        program: this.programID
      };
        console.log(this.moduleRequest);
      this.moduleService.createModule(this.moduleRequest).subscribe(
          () => {
            this.toastr.success('Module added successfully');
            this.moduleAdded.emit();
          },
          error => {
            if (error.error) {
                this.toastr.error(error.error);
            } else {
                this.toastr.error('Failed to add module');
            }
          }
      );
    }
  }
}
