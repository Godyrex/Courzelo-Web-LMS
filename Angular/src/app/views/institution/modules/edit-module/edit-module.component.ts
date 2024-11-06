import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {InstitutionResponse} from '../../../../shared/models/institution/InstitutionResponse';
import {ModuleRequest} from '../../../../shared/models/institution/ModuleRequest';

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
  moduleRequest: ModuleRequest;

  constructor(
      private fb: FormBuilder,
      private moduleService: ModuleService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // Convert moduleParts object into an array of [key, value] pairs
    const moduleParts = this.module.moduleParts
        ? Object.entries(this.module.moduleParts) // Converts { "TP": 40, "Cour": 80 } to [["TP", 40], ["Cour", 80]]
        : [];

    this.editModuleForm = this.fb.group({
      name: [this.module.name, Validators.required],
      description: [this.module.description, Validators.required],
      skills: [this.module.skills],
      duration: [this.module.duration, Validators.required],
      credit: [this.module.credit, Validators.required],
      scoreToPass: [this.module.scoreToPass, Validators.required],
      semester: [
        this.module.semester === 'FIRST_SEMESTER'
            ? 'FIRST_SEMESTER'
            : this.module.semester === 'SECOND_SEMESTER'
                ? 'SECOND_SEMESTER'
                : null
      ],
      moduleParts: this.fb.array(
          moduleParts.map(([key, value]) =>
              this.fb.group({
                name: [key, Validators.required],   // "TP" or "Cour"
                value: [value, Validators.required] // 40 or 80
              })
          )
      ),
      isFinished: [this.module.isFinished || false]
    });
  }


  get moduleParts(): FormArray {
    return this.editModuleForm.get('moduleParts') as FormArray;
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
    if (this.editModuleForm.valid) {
      console.log(this.editModuleForm.value);
      const formValue = this.editModuleForm.value;
      const modulePartsMap = {};

      formValue.moduleParts.forEach(part => {
        modulePartsMap[part.name] = part.value;
      });

      this.moduleRequest = {
        ...formValue,
        moduleParts: modulePartsMap,
      };
      console.log(this.moduleRequest);
      this.moduleService.updateModule(this.module.id, this.moduleRequest).subscribe(
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
