import {Component, OnInit} from '@angular/core';
import {SessionStorageService} from '../../../shared/services/user/session-storage.service';
import {CourseService} from '../../../shared/services/institution/course.service';
import {ProgramService} from '../../../shared/services/institution/program.service';
import {ToastrService} from 'ngx-toastr';
import {CourseResponse} from '../../../shared/models/institution/CourseResponse';
import {ModuleService} from '../../../shared/services/institution/module.service';
import { UserService } from 'src/app/shared/services/user/user.service';
import {DomSanitizer} from '@angular/platform-browser';
import {ProgramResponse} from '../../../shared/models/institution/ProgramResponse';
import {GroupService} from '../../../shared/services/institution/group.service';
import {GroupResponse} from '../../../shared/models/institution/GroupResponse';
import {ViewStudentsComponent} from "../../institution/class/view-students/view-students.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-home-dashboard',
  templateUrl: './home-dashboard.component.html',
  styleUrls: ['./home-dashboard.component.scss']
})
export class HomeDashboardComponent implements OnInit {
  constructor(
      private sessionstorage: SessionStorageService,
      private courseService: CourseService,
      private programService: ProgramService,
      private groupService: GroupService,
      private moduleService: ModuleService,
      private toastr: ToastrService,
      private userService: UserService,
      private sanitizer: DomSanitizer,
      private modalService: NgbModal
  ) {
  }
  currentUser = this.sessionstorage.getUserFromSession();
  myProgram: ProgramResponse;
  myGroup: GroupResponse;
  loading = false;
  courses: CourseResponse[] = [];
  student = { program: 'Bachelor in Computer Science', group: 'Group A', advisor: 'Dr. Emily White' };
  announcements = ['New Semester Registration Open', 'Library Hours Updated'];

  ngOnInit(): void {
      this.fetchCourses();
      this.fetchProgram();
      this.fetchGroup();
  }
  fetchCourses(): void {
    this.loading = true;
    this.courseService.getMyCourses().subscribe((courses) => {
      this.courses = courses;
      this.courses.forEach((course) => {
        this.moduleService.getModule(course.module).subscribe((module) => {
          course.name = module.name;
          course.description = module.description;
          course.credit = module.credit;
        });
        if (course.teacher) {
          this.userService.getProfileImageBlobUrl(course.teacher).subscribe((blob: Blob) => {
            const objectURL = URL.createObjectURL(blob);
            course.imageSrc = this.sanitizer.bypassSecurityTrustUrl(objectURL);
          });
        }

      });
      console.log(this.courses);
        this.loading = false;
    }, (error) => {
      if (error.error) {
        this.toastr.error(error.error, 'Error');
      } else {
        this.toastr.error('An error occurred', 'Error');
      }
      this.loading = false;
        });
  }
  fetchProgram(): void {
    this.programService.getMyProgram().subscribe((program) => {
        this.myProgram = program;
    });
  }
  fetchGroup(): void {
    this.groupService.getMyGroup().subscribe((group) => {
        this.myGroup = group;
    });
  }
  openViewStudentsModal(group: GroupResponse) {
    const modalRef = this.modalService.open(ViewStudentsComponent, { size : 'lg' , backdrop: false});
    modalRef.componentInstance.group = group;
    modalRef.componentInstance.close.subscribe(() => {
      modalRef.close();
    });
  }
}
