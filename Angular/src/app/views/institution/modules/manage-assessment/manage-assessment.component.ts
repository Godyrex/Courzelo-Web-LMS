import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';
import {AssessmentRequest} from '../../../../shared/models/institution/AssessmentRequest';
import {ToastrService} from 'ngx-toastr';
import {ModuleService} from '../../../../shared/services/institution/module.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-manage-assessment',
  templateUrl: './manage-assessment.component.html',
  styleUrls: ['./manage-assessment.component.scss']
})
export class ManageAssessmentComponent implements OnInit {
  loading = false;
  isEditMode = false;
  isAddMode = false;
  @Input() moduleResponse: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  currentAssessment: AssessmentRequest;
  constructor(
      private toastr: ToastrService,
      private moduleService: ModuleService,
      private modalService: NgbModal
  ) {
  }

  ngOnInit(): void {
    if (this.moduleResponse.assessments != null && this.moduleResponse.assessments.length > 0) {
        this.moduleResponse.assessments = this.moduleResponse.assessments.map(assessment => {
            return {
            ...assessment,
            weight: assessment.weight * 100
            };
        });
    }
  }
  onClose(): void {
    if (this.moduleResponse.assessments != null && this.moduleResponse.assessments.length > 0) {
        this.moduleResponse.assessments = this.moduleResponse.assessments.map(assessment => {
            return {
            ...assessment,
            weight: assessment.weight / 100
            };
        });
    }
    this.close.emit();
  }
  calculateAssessmentWeight(): number {
    let total = 0;
    if (this.moduleResponse.assessments) {
        this.moduleResponse.assessments.forEach(assessment => {
            total += assessment.weight;
        });
    }
    return total;
  }
  addNewAssessment(): void {
    this.isAddMode = true;
    this.currentAssessment = {oldName: '', name: '', weight: null };
  }

  editAssessment(assessment: any): void {
    this.isEditMode = true;
    this.currentAssessment = { ...assessment };
    this.currentAssessment.oldName = assessment.name;
  }

  cancelEdit(): void {
    this.isEditMode = false;
    this.isAddMode = false;
    this.currentAssessment = {oldName: '', name: '', weight: null };
  }
    modalConfirmFunction(content: any, assessment: AssessmentRequest) {
        this.currentAssessment = assessment;
        this.modalService.open(content, { ariaLabelledBy: 'confirm Module', backdrop: false })
            .result.then((result) => {
            if (result === 'Ok') {
                this.deleteAssessment(assessment.name);
                this.currentAssessment = {oldName: '', name: '', weight: null };
            }
        }, (reason) => {
            console.log('Err!', reason);
        });
    }
  onSubmitAssessment(): void {
    if (this.isEditMode) {
      console.log('Edit mode');
      // Update existing assessment
      const index = this.moduleResponse.assessments.findIndex(a => a.name === this.currentAssessment.oldName);
      if (index > -1) {
        if (this.currentAssessment.name === this.currentAssessment.oldName ||
            this.moduleResponse.assessments.findIndex(a => a.name === this.currentAssessment.name) === -1) {
          {
            const assessmentCopy = {...this.currentAssessment};
            this.moduleService.updateAssessment(this.moduleResponse.id, this.currentAssessment).subscribe(
                response => {
                  this.moduleResponse.assessments[index] = {...assessmentCopy};
                  this.toastr.success('Assessment updated successfully');
                },
                error => {
                  if (error.error) {
                    this.toastr.error(error.error);
                  } else {
                    this.toastr.error('Failed to update assessment');
                  }
                }
            );
          }
        } else {
            this.toastr.error('Assessment with the same name already exists');
        }
      }
    } else if (this.isAddMode) {
      console.log(this.moduleResponse);
      if (!this.moduleResponse.assessments) {
        this.moduleResponse.assessments = [];
      }
      if (this.moduleResponse.assessments.findIndex(a => a.name === this.currentAssessment.name) > -1) {
        this.toastr.error('Assessment with the same name already exists');
      } else {
        console.log(this.currentAssessment);
        const assessmentCopy = { ...this.currentAssessment }; // Create a copy
        this.moduleService.createAssessment(this.moduleResponse.id, assessmentCopy).subscribe(
            response => {
              console.log(assessmentCopy);
              this.moduleResponse.assessments.push(assessmentCopy);
              this.toastr.success('Assessment created successfully');
              console.log(this.moduleResponse);
            },
            error => {
              if (error.error) {
                this.toastr.error(error.error);
              } else {
                this.toastr.error('Failed to create assessment');
              }
            }
        );
      }
    }

    // Reset the form and exit add/edit mode
    this.cancelEdit();
  }

  deleteAssessment(name: string): void {
    this.moduleService.deleteAssessment(this.moduleResponse.id, name).subscribe(
        response => {
          this.moduleResponse.assessments = this.moduleResponse.assessments.filter(a => a.name !== name);
          this.toastr.success('Assessment deleted successfully');
        },
        error => {
          if (error.error) {
            this.toastr.error(error.error);
          } else {
            this.toastr.error('Failed to delete assessment');
          }
        }
    );
  }
}
