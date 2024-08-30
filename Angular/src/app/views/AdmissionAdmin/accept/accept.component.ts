import { InscriptionService } from './../../../shared/services/admission/inscription.service';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { UserResponse } from 'src/app/shared/models/user/UserResponse';
import { SessionStorageService } from 'src/app/shared/services/user/session-storage.service';

@Component({
  selector: 'app-accept',
  templateUrl: './accept.component.html',
  styleUrls: ['./accept.component.scss']
})
export class AcceptComponent implements OnInit{

  acceptForm!: FormGroup;
  connectedUser:UserResponse;


  constructor(public dialogRef: MatDialogRef<AcceptComponent>,
    private fb: FormBuilder,
    private sessionStorageService: SessionStorageService,
    private InscriptionService:InscriptionService,
      ){}
  ngOnInit(): void {
    this.connectedUser = this.sessionStorageService.getUserFromSession();
    this.createForm();
  }
  createForm() {
    this.acceptForm = this.fb.group({
      acceptedLimit: ['', [Validators.required]],
      waitingLimit: ['', [Validators.required]],
    });  
  }

  onSubmit() {
    const waitingLimit = this.acceptForm.get('waitingLimit')?.value;
    const acceptedLimit = this.acceptForm.get('acceptedLimit')?.value;
    const id = this.connectedUser.education.institutionID;
    this.InscriptionService.addInstitutionUserRole(id,acceptedLimit,waitingLimit).subscribe((res)=>{
      console.log(res);
    })

    console.log("lol")
  }
  onClose(){
    this.dialogRef.close();
  }
}
