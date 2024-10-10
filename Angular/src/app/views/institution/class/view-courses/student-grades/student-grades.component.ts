import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {GradeService} from '../../../../../shared/services/institution/grade.service';
import {GroupResponse} from '../../../../../shared/models/institution/GroupResponse';
import {GroupService} from '../../../../../shared/services/institution/group.service';
import {ModuleService} from '../../../../../shared/services/institution/module.service';
import {ModuleResponse} from '../../../../../shared/models/institution/ModuleResponse';
import {StudentGrades} from '../../../../../shared/models/institution/StudentGrades';

@Component({
  selector: 'app-student-grades',
  templateUrl: './student-grades.component.html',
  styleUrls: ['./student-grades.component.scss']
})
export class StudentGradesComponent implements OnInit {
  studentGrades: StudentGrades[] = [];
  @Input() groupResponse: GroupResponse;
  @Input() moduleResponse: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  loading = false;

  constructor(
      private toastr: ToastrService,
      private gradeService: GradeService,
      private groupService: GroupService,
      private moduleService: ModuleService,
  ) {
  }

  ngOnInit(): void {
    this.loadStudentGrades();
  }
  loadStudentGrades(): void {
    this.loading = true;
    if (!this.groupResponse || !this.moduleResponse || !this.groupResponse.students || !this.moduleResponse.assessments) {
        this.toastr.error('No students or assessments found');
        this.loading = false;
        return;
    }
    this.groupResponse.students.forEach(student => {
        const studentGrade: StudentGrades = {
            studentEmail: student,
            image: '',
            grades: {}
        };
        this.moduleResponse.assessments.forEach(assessment => {
            studentGrade.grades[assessment.name] = 0;
        });
        this.studentGrades.push(studentGrade);
    });
    this.gradeService.getGradesByGroupAndModule(this.groupResponse.id, this.moduleResponse.id).subscribe(
        grades => {
            this.studentGrades.forEach(studentGrade => {
            this.moduleResponse.assessments.forEach(assessment => {
                const grade = grades.find(g => g.studentEmail === studentGrade.studentEmail && g.name === assessment.name);
                if (grade) {
                    studentGrade.grades[assessment.name] = grade.grade;
            }});
            });
            this.loading = false;
        },
        error => {
            if (error.error) {
                this.toastr.error(error.error);
            } else {
                this.toastr.error('Failed to fetch grades');
            }
            this.loading = false;
        }
    );
  }
  onSave(): void {
    this.loading = true;
    const gradeRequests = [];
    this.studentGrades.forEach(studentGrade => {
        Object.keys(studentGrade.grades).forEach(assessmentName => {
            const gradeRequest = {
                groupID: this.groupResponse.id,
                moduleID: this.moduleResponse.id,
                studentEmail: studentGrade.studentEmail,
                name: assessmentName,
                grade: studentGrade.grades[assessmentName]
            };
            gradeRequests.push(gradeRequest);
        });
    });
    this.gradeService.addGrades(gradeRequests).subscribe(
        () => {
            this.toastr.success('Grades saved successfully');
            this.loading = false;
        },
        error => {
            if (error.error) {
                this.toastr.error(error.error);
            } else {
                this.toastr.error('Failed to save grades');
            }
            this.loading = false;
        }
    );
  }

  onClose(): void {
    this.close.emit();
  }
}
