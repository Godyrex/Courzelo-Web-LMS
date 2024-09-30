import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupResponse} from '../../../../shared/models/institution/GroupResponse';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {ToastrService} from 'ngx-toastr';
import {GroupService} from '../../../../shared/services/institution/group.service';
import {UserService} from '../../../../shared/services/user/user.service';

@Component({
  selector: 'app-view-students',
  templateUrl: './view-students.component.html',
  styleUrls: ['./view-students.component.scss']
})
export class ViewStudentsComponent implements OnInit {
  @Input() group: GroupResponse;
  @Output() close = new EventEmitter<void>();
  modules: ModuleResponse[] = [];
  studentsWithImages: { email: string, image: string }[] = [];
  constructor(
      private userService: UserService,
      private toastr: ToastrService
  ) {
  }
  ngOnInit(): void {
    if (this.group.students != null) {
      this.group.students.forEach(email => {
        this.userService.getProfileImageBlobUrl(email).subscribe(
            image => {
              const imageUrl = image ? URL.createObjectURL(image) : null;
              this.studentsWithImages.push({ email, image: imageUrl });
            },
            error => {
              this.studentsWithImages.push({ email, image: null });
            }
        );
      });
    }
  }
  onClose() {
    this.close.emit();
  }
}
