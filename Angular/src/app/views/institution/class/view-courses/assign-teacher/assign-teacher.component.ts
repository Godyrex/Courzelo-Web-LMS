import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {ModuleResponse} from '../../../../../shared/models/institution/ModuleResponse';
import {CourseService} from '../../../../../shared/services/institution/course.service';
import {UserResponse} from '../../../../../shared/models/user/UserResponse';
import {Observable} from 'rxjs';
import {InstitutionService} from '../../../../../shared/services/institution/institution.service';
import {TeacherResponse} from '../../../../../shared/models/institution/TeacherResponse';
import {map, startWith} from 'rxjs/operators';

@Component({
  selector: 'app-assign-teacher',
  templateUrl: './assign-teacher.component.html',
  styleUrls: ['./assign-teacher.component.scss']
})
export class AssignTeacherComponent implements OnInit {
  @Input() module: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  assignTeacherForm: FormGroup = this.fb.group({
    assignOption: ['select', Validators.required],
    email: ['', [Validators.required, Validators.email]],
  });
  teachers: TeacherResponse[] = [];

  constructor(
      private fb: FormBuilder,
      private courseService: CourseService,
      private institutionService: InstitutionService,
      private toastr: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.fitlerTeachers();
    }
  onOptionChange(option: string) {
    if (option === 'select') {
      this.assignTeacherForm.get('email').setValidators([Validators.required]);
    } else {
      this.assignTeacherForm.get('email').setValidators([Validators.required, Validators.email]);
    }
    this.assignTeacherForm.get('email').updateValueAndValidity();
  }
  fitlerTeachers() {
    this.institutionService.getInstitutionFilteredTeachers(this.module.institutionID, this.module.skills).subscribe(
        (teachers) => {
          this.teachers = teachers;
          console.log(teachers);
        },
        error => {
          if (error.error) {
            this.toastr.error(error.error);
          } else {
            this.toastr.error('Failed to get teachers');
          }
        });
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
