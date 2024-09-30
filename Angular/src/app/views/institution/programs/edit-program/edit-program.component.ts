import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ProgramResponse} from '../../../../shared/models/institution/ProgramResponse';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProgramService} from '../../../../shared/services/institution/program.service';
import {ResponseHandlerService} from '../../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-edit-program',
  templateUrl: './edit-program.component.html',
  styleUrls: ['./edit-program.component.scss']
})
export class EditProgramComponent implements OnInit {
  @Input() program: ProgramResponse;
  @Output() programUpdated = new EventEmitter<void>();
  editProgramForm: FormGroup;

  constructor(
      private fb: FormBuilder,
      private programService: ProgramService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.editProgramForm = this.fb.group({
      name: [this.program.name, Validators.required],
      description: [this.program.description, Validators.required],
      credits: [this.program.credits, Validators.required],
      duration: [this.program.duration]
    });
  }

  onSubmit() {
    if (this.editProgramForm.valid) {
      this.programService.updateProgram(this.program.id, this.editProgramForm.value).subscribe(
          () => {
            this.toastr.success('Program updated successfully');
            this.programUpdated.emit({ ...this.program, ...this.editProgramForm.value });
          },
          error => {
            this.handleResponse.handleError(error);
          }
      );
    }
  }
}
