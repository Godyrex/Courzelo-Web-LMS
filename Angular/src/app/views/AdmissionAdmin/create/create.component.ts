import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SessionStorageService } from 'src/app/shared/services/user/session-storage.service';
import { InscriptionService } from './../../../shared/services/admission/inscription.service';
import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/shared/services/user/user.service';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import { UserResponse } from 'src/app/shared/models/user/UserResponse';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {

saveUser() {
throw new Error('Method not implemented.');
}
  inscriptionForm: FormGroup;
  connectedUser:UserResponse;
  countries = [];
  maxDate: NgbDateStruct;
  selectedFile: File | null = null;
  uploadMessage: string | null = null;

constructor(
  private inscriptionService:InscriptionService,
  private sessionStorageService: SessionStorageService,
  private formBuilder: FormBuilder,
  private userService: UserService


){}

ngOnInit(): void {
  this.createForm();
  this.connectedUser = this.sessionStorageService.getUserFromSession();
  this.userService.getCountries().subscribe(
    countries => {
      this.countries = countries;
      console.log(this.countries);
    }
);
}

shouldShowError(controlName: string, errorName: string): boolean {
  const control = this.inscriptionForm.get(controlName);
  return control && control.errors && control.errors[errorName] && (control.dirty || control.touched);
}
createForm(){
this.inscriptionForm = this.formBuilder.group({
  email: ['', [Validators.required, Validators.email]],
  name: ['', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]],
  lastname: ['', [Validators.required, Validators.maxLength(20), Validators.minLength(3)]],
  birthDate: [[Validators.required]],
  gender: ['', [Validators.required]],
  country: ['', [Validators.required]],
  password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(8)]],
});
}


onFileSelected(event: any): void {
  this.selectedFile = event.target.files[0];
}

onUpload(): void {
  
  if (this.selectedFile) {
    this.inscriptionService.uploadFiles(this.selectedFile,this.connectedUser.education.institutionID).subscribe(
      response => {
        this.uploadMessage = 'Upload successful!';
      },
      error => {
        console.error('Upload error:', error);
        this.uploadMessage = `Upload failed: ${error.message || error}`;
      }
    );
  } else {
    this.uploadMessage = 'No file selected.';
  }
}
}
