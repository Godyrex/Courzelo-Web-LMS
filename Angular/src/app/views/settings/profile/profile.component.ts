import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ProfileInformationRequest } from '../../../shared/models/user/requests/ProfileInformationRequest';
import { UserService } from '../../../shared/services/user/user.service';
import { SessionStorageService } from '../../../shared/services/user/session-storage.service';
import { UserResponse } from '../../../shared/models/user/UserResponse';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { ResponseHandlerService } from '../../../shared/services/user/response-handler.service';
import { StatusMessageResponse } from 'src/app/shared/models/user/StatusMessageResponse';
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  skills: string[] = [];
  user: UserResponse;


  constructor(
    private formBuilder: FormBuilder,
    private toastr: ToastrService,
    private userService: UserService,
    private sessionStorageService: SessionStorageService,
    private responseHandlerService: ResponseHandlerService
  ) {
    const today = new Date();
    const minAge = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());

    this.maxDate = {
      year: minAge.getFullYear(),
      month: minAge.getMonth() + 1,
      day: minAge.getDate()
    };
  }
  loading: boolean;
  connectedUser: UserResponse;
  maxDate: NgbDateStruct;
  informationForm = this.formBuilder.group({
    name: [' ', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]],
    lastname: ['', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]],
    bio: ['', [Validators.maxLength(300), Validators.minLength(20)]],
    title: ['', [Validators.minLength(3), Validators.maxLength(20)]],
    birthDate: [],
    gender: [''],
    country: ['']
  }
  );
  profileInfromationRequest: ProfileInformationRequest = {};
  selectedFileName: string;
  selectedFileUrl: string | ArrayBuffer;
  birthDate: NgbDateStruct;
  file: any;
  countries = [];
  ngOnInit() {
      this.getUserSkills();
      this.userService.getCountries().subscribe(
            countries => {
                this.countries = countries;
                console.log(this.countries);
            }
        );
   this.connectedUser = this.sessionStorageService.getUserFromSession();
   console.log(this.connectedUser);
      this.initializeFormWithUserData();

    const date = new Date(this.connectedUser.profile.birthDate);
    this.birthDate = {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      day: date.getDate()
    };
  }
  initializeFormWithUserData() {
    if (this.connectedUser) {
      const date = new Date(this.connectedUser.profile.birthDate);
      this.birthDate = {
        year: date.getFullYear(),
        month: date.getMonth() + 1,
        day: date.getDate()
      };
      this.informationForm.patchValue({
        name: this.connectedUser.profile.name,
        lastname: this.connectedUser.profile.lastname,
        bio: this.connectedUser.profile.bio,
        title: this.connectedUser.profile.title,
        gender: this.connectedUser.profile.gender,
        country: this.connectedUser.profile.country
      });
    }
  }

  updateUserProfile() {
    this.userService.updateUserProfile(this.profileInfromationRequest).subscribe(
      res => {
        this.userService.getUserProfile().subscribe(
          user => {
            this.sessionStorageService.setUser(user.user);
          }
        );
        this.loading = false;
        this.toastr.success(res.message, 'Success!', { progressBar: true });
      },
      error => {
        this.loading = false;
        this.responseHandlerService.handleError(error);
      }
    );
  }
  updateProfileInformation() {
    this.loading = true;
    if (this.informationForm.valid) {
      this.profileInfromationRequest = this.informationForm.getRawValue();
      if (this.informationForm.controls['birthDate'].value) {
        this.profileInfromationRequest.birthDate = `${this.informationForm.controls['birthDate'].value.year}-${this.informationForm.controls['birthDate'].value.month.toString().padStart(2, '0')}-${this.informationForm.controls['birthDate'].value.day.toString().padStart(2, '0')}`;
      }

      this.updateUserProfile();
    } else {
      this.loading = false;
      this.toastr.error('Form is invalid', 'Error!', { progressBar: true });
    }
  }
  onFileSelected(event) {
    if (event.target.files.length > 0) {
      this.file = event.target.files[0];
      const reader = new FileReader();
      this.selectedFileName = this.file.name;
      reader.onload = (e) => this.selectedFileUrl = reader.result;
      reader.readAsDataURL(this.file);
    }
  }
  uploadProfilePicture() {
    this.loading = true;
    if (!this.file) {
      this.toastr.error('No file selected', 'Error!', { progressBar: true });
      this.loading = false;
      return;
    }
    this.userService.uploadProfileImage(this.file).subscribe(
      res => {
        this.userService.getUserProfile().subscribe(
          user => {
            this.sessionStorageService.setUser(user.user);
          }
        );
        this.loading = false;
        this.toastr.success(res.message, 'Success!', { progressBar: true });
      },
      error => {
        this.loading = false;
        this.responseHandlerService.handleError(error);
      }
    );
  }

  getUserSkills() {
    this.user = this.sessionStorageService.getUser();
    console.log("user Email  " + this.user.email);

    this.userService.getUserSkills(this.user.email).subscribe(
      (res: any) => {
        this.skills = res; // Assuming the API returns the skills directly
        this.toastr.success('Skills retrieved successfully', 'Success!', { progressBar: true });
        console.log("skills on getting " + this.skills);
      },
      (error) => {
        this.handleErrorResponse(error);
      }
    );
  }


  addSkill(event: any) {
    const skill = event.value;
    console.log("user Email  " + this.user.email);
    this.userService.addSkillToUser(this.user.email, skill).subscribe(
      (response: StatusMessageResponse) => {
        this.toastr.success(response.message, 'Success!', { progressBar: true });
        console.log("adding skill  " + skill);

      },
      (error) => {
        this.handleErrorResponse(error);
      }
    );
  }
  removeSkill(event: any) {
    console.log(event);

    const skill = event.tag;

    this.user = this.sessionStorageService.getUser();

    this.userService.deleteUserSkill(this.user.email, event).subscribe(
      (response: StatusMessageResponse) => {
        this.toastr.success(response.message, 'Success!', { progressBar: true });
        this.getUserSkills();
      },
      (error) => {
        this.handleErrorResponse(error);
      }
    );


  }

  handleErrorResponse(error) {
    console.error(error);
    let errorMessage = 'An unexpected error occurred';
    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    }
    switch (error.status) {
      case 409:
        this.toastr.error(errorMessage, 'Error!', { progressBar: true });
        break;
      case 400:
        this.toastr.error(errorMessage, 'Error!', { progressBar: true });
        break;
      case 401:
        break;
      default:
        this.toastr.error(errorMessage, 'Error!', { progressBar: true });
    }
  }
}
