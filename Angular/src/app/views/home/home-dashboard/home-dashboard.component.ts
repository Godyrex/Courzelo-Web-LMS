import {Component, OnInit} from '@angular/core';
import {SessionStorageService} from '../../../shared/services/user/session-storage.service';
import {CourseService} from '../../../shared/services/institution/course.service';
import {ProgramService} from '../../../shared/services/institution/program.service';
import {ToastrService} from 'ngx-toastr';
import {CourseResponse} from '../../../shared/models/institution/CourseResponse';
import {ModuleService} from '../../../shared/services/institution/module.service';
import { UserService } from 'src/app/shared/services/user/user.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {ProgramResponse} from '../../../shared/models/institution/ProgramResponse';
import {GroupService} from '../../../shared/services/institution/group.service';
import {GroupResponse} from '../../../shared/models/institution/GroupResponse';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ViewStudentsComponent} from '../../../shared/components/view-students/view-students.component';
import {StudentGradesComponent} from '../../../shared/components/student-grades/student-grades.component';
import {MyGradesComponent} from './my-grades/my-grades.component';
import {InstitutionService} from '../../../shared/services/institution/institution.service';
import {InstitutionResponse} from '../../../shared/models/institution/InstitutionResponse';

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
      private institutionService: InstitutionService,
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
  myInstitution: InstitutionResponse;
  sanitizedWebsiteUrl: SafeUrl;
  loading = false;
  courses: CourseResponse[] = [];
  student = { program: 'Bachelor in Computer Science', group: 'Group A', advisor: 'Dr. Emily White' };
  announcements = ['New Semester Registration Open', 'Library Hours Updated'];

  ngOnInit(): void {
      this.fetchCourses();
      this.fetchProgram();
      this.fetchGroup();
      this.fetchInstitution();
  }
  openStudentsGradesModal(groupID: string, moduleID: string) {
    const modalRef = this.modalService.open(StudentGradesComponent, {size : 'lg', backdrop: false});
    modalRef.componentInstance.groupID = groupID;
    modalRef.componentInstance.moduleID = moduleID;
    modalRef.componentInstance.mode = 'teacher';
    modalRef.componentInstance.close.subscribe(() => {
      modalRef.close();
    });
  }
  openMyGradesModal() {
    const modalRef = this.modalService.open(MyGradesComponent, {size : 'lg', backdrop: false});
    modalRef.componentInstance.close.subscribe(() => {
      modalRef.close();
    });
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
  fetchInstitution(): void {
    this.institutionService.getInstitutionByID(this.currentUser?.education?.institutionID).subscribe((institution) => {
      this.myInstitution = institution;
      this.sanitizedWebsiteUrl = this.sanitizer.bypassSecurityTrustUrl(institution.website);
    });
  }
  fetchGroup(): void {
    this.groupService.getMyGroup().subscribe((group) => {
        this.myGroup = group;
    });
  }
  downloadExcel() {
    this.programService.downloadExcel(this.myProgram.id).subscribe(
        response => {
          const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = this.myProgram.name + '.xlsx';
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error => {
          if (error.error) {
            this.toastr.error(error.error, 'Error');
          } else {
            this.toastr.error('An error occurred', 'Error');
          }
        }
    );
  }
  openViewStudentsModal(group: GroupResponse) {
    const modalRef = this.modalService.open(ViewStudentsComponent, { size : 'lg' , backdrop: false});
    modalRef.componentInstance.group = group;
    modalRef.componentInstance.close.subscribe(() => {
      modalRef.close();
    });
  }
}
