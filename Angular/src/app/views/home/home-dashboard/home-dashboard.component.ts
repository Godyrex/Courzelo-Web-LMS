import {Component, OnInit} from '@angular/core';
import {SessionStorageService} from '../../../shared/services/user/session-storage.service';

@Component({
  selector: 'app-home-dashboard',
  templateUrl: './home-dashboard.component.html',
  styleUrls: ['./home-dashboard.component.scss']
})
export class HomeDashboardComponent implements OnInit {
  constructor(
      private sessionstorage: SessionStorageService,

  ) {
  }
  currentUser = this.sessionstorage.getUserFromSession();
  userName = 'John Doe';
  courses = [
    { name: 'Math 101', teacher: 'Mrs. Smith', progress: 75, nextDue: 'Oct 10', link: '/course/1' },
    { name: 'Physics 202', teacher: 'Mr. Johnson', progress: 50, nextDue: 'Oct 15', link: '/course/2' },
  ];
  student = { program: 'Bachelor in Computer Science', group: 'Group A', advisor: 'Dr. Emily White' };
  announcements = ['New Semester Registration Open', 'Library Hours Updated'];

  ngOnInit(): void {
      throw new Error('Method not implemented.');
  }
}
