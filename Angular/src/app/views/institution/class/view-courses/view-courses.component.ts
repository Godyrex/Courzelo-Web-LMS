import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupResponse} from '../../../../shared/models/institution/GroupResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ToastrService} from 'ngx-toastr';
import {CourseService} from '../../../../shared/services/institution/course.service';
import {CourseRequest} from '../../../../shared/models/institution/CourseRequest';
import {GroupService} from '../../../../shared/services/institution/group.service';

@Component({
  selector: 'app-view-courses',
  templateUrl: './view-courses.component.html',
  styleUrls: ['./view-courses.component.scss']
})
export class ViewCoursesComponent implements OnInit {

  @Input() program: string;
  @Input() group: GroupResponse;
  @Output() close = new EventEmitter<void>();
  loading = false;
    showFullDescription: { [key: string]: boolean } = {};
    modules: ModuleResponse[] = [];
  constructor(
      private moduleService: ModuleService,
      private toastr: ToastrService,
      private courseService: CourseService,
      private groupService: GroupService,
  ) {
  }
  ngOnInit(): void {
    console.log(this.program);
  this.fetchModules();
  }
    fetchModules() {
      this.loading = true;
        this.moduleService.getModules(0, 500, this.program, null).subscribe(
            modules => {
                console.log(modules);
                this.modules = modules.modules;
                this.modules.forEach(module => {
                    module.courseCreated = false; // Initialize as false
                    for (const course of this.group.courses) {
                        if (course.module === module.id) {
                            console.log(course);
                            module.courseCreated = true;
                            module.courseID = course.courseID;
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
    onClose() {
        this.close.emit();
    }
    deleteCourse(id: string) {
        this.loading = true;
        this.courseService.deleteCourse(id).subscribe(
        () => {
            this.toastr.success('Course deleted successfully');
            this.groupService.getGroup(this.group.id).subscribe(
                group => {
                    this.group = group;
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

}
