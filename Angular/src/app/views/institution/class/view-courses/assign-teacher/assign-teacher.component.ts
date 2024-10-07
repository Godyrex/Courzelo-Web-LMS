import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {ModuleResponse} from '../../../../../shared/models/institution/ModuleResponse';
import {CourseService} from '../../../../../shared/services/institution/course.service';

@Component({
  selector: 'app-assign-teacher',
  templateUrl: './assign-teacher.component.html',
  styleUrls: ['./assign-teacher.component.scss']
})
export class AssignTeacherComponent {
  @Input() module: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  assignTeacherForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  constructor(
      private fb: FormBuilder,
      private courseService: CourseService,
      private toastr: ToastrService
  ) {
  }

  onSubmit() {
    if (this.assignTeacherForm.valid) {
      console.log(this.assignTeacherForm.value);
      console.log(this.module.courseID);
      this.courseService.setTeacher(this.module.courseID, this.assignTeacherForm.value.email).subscribe(
          () => {
            this.toastr.success('Teacher assigned successfully');
          },
          error => {
            if (error.error) {
              this.toastr.error(error.error);
            } else {
              this.toastr.error('Failed to assign teacher');
            }
          });
    }
  }
  onClose() {
    this.close.emit();
  }
}
