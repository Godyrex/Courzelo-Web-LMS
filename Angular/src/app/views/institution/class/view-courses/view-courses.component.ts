import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupResponse} from '../../../../shared/models/institution/GroupResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ToastrService} from 'ngx-toastr';
import {CourseService} from '../../../../shared/services/institution/course.service';
import {CourseRequest} from '../../../../shared/models/institution/CourseRequest';
import {GroupService} from '../../../../shared/services/institution/group.service';
import {ViewStudentsComponent} from '../../../../shared/components/view-students/view-students.component';
import {AssignTeacherComponent} from './assign-teacher/assign-teacher.component';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {StudentGradesComponent} from '../../../../shared/components/student-grades/student-grades.component';

@Component({
  selector: 'app-view-courses',
  templateUrl: './view-courses.component.html',
  styleUrls: ['./view-courses.component.scss']
})
export class ViewCoursesComponent implements OnInit {

  @Input() program: string;
  @Input() group: GroupResponse;
  @Input() module: ModuleResponse;
    currentCourse: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  loading = false;
    showFullDescription: { [key: string]: boolean } = {};
    modules: ModuleResponse[] = [];
  constructor(
      private moduleService: ModuleService,
      private toastr: ToastrService,
      private courseService: CourseService,
      private groupService: GroupService,
      private modalService: NgbModal,
  ) {
  }
  ngOnInit(): void {
  this.fetchModules();
  }
    fetchModules() {
      this.loading = true;
        this.moduleService.getModules(0, 500, this.program, null).subscribe(
            modules => {
                this.modules = modules.modules;
                this.modules.forEach(module => {
                    module.courseCreated = false; // Initialize as false
                    for (const course of this.group.courses) {
                        if (course.module === module.id) {
                            module.courseCreated = true;
                            module.courseID = course.courseID;
                            module.courseTeacher = course.teacher;
                            console.log('modules after checking courses :' + modules);
                            break;
                        }
                    }
                });
                this.loading = false;
            },
            error => {
                this.toastr.error('Failed to fetch modules');
                this.loading = false;
            }
        );
    }
    toggleDescription(moduleId: string) {
        this.showFullDescription[moduleId] = !this.showFullDescription[moduleId];
    }
    openDeleteCourseModal(module: ModuleResponse, content: NgbModalRef) {
        this.currentCourse = module;
        this.modalService.open(content, { ariaLabelledBy: 'modal-title-course' , backdrop: false }).result.then(
            result => {
                if (result === 'Ok') {
                    this.deleteCourse(module.courseID);
                }
            },
            reason => {}
        );
    }
    onClose() {
        this.close.emit();
    }
    deleteCourse(id: string) {
        this.loading = true;
        this.courseService.deleteCourse(id).subscribe(
        () => {
            this.toastr.success('Course deleted successfully');
            this.currentCourse = null;
            this.modules = this.modules.filter(m => m.courseID !== id);
            this.toastr.info('Refresh your page to see changes');
            this.loading = false;
            },
        error => {
            if (error.error) {
                this.toastr.error(error.error);
            } else {
                this.toastr.error('Failed to delete course');
            }
            this.loading = false;
        });
    }
    createCourse(moduleResponse: ModuleResponse) {
     const courseRequest: CourseRequest = {
            module: moduleResponse.id,
            group: this.group.id,
     };
     this.loading = true;
   this.courseService.addCourse(this.group.institutionID, courseRequest).subscribe(
         course => {
              this.toastr.success('Course created successfully');
              console.log(this.group);
              this.groupService.getGroup(this.group.id).subscribe(
                    group => {
                        console.log(group);
                        this.group = group;
                        console.log(this.group);
                        this.fetchModules();
                    },
                    error => {
                        if (error.error) {
                            this.toastr.error(error.error);
                        } else {
                            this.toastr.error('Failed to fetch group');
                        }
                        this.loading = false;
                    }
                );
         },
         error => {
             if (error.error) {
                    this.toastr.error(error.error);
             } else {
                 this.toastr.error('Failed to create course');
             }
             this.loading = false;
         }
   );
    }
    openStudentsGradesModal(moduleResponse: ModuleResponse) {
        const modalRef = this.modalService.open(StudentGradesComponent, {size : 'lg', backdrop: false});
        modalRef.componentInstance.groupResponse = this.group;
        modalRef.componentInstance.moduleResponse = moduleResponse;
        modalRef.componentInstance.mode = 'admin';
        modalRef.componentInstance.close.subscribe(() => {
            modalRef.close();
        });
    }
    openAsssignTeacherModal(module: ModuleResponse) {
        const modalRef = this.modalService.open(AssignTeacherComponent, { size : 'sm' , backdrop: false});
        modalRef.componentInstance.module = module;
        modalRef.componentInstance.close.subscribe(() => {
            modalRef.close();
        });
    }
}
