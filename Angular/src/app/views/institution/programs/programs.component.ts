import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {InstitutionResponse} from '../../../shared/models/institution/InstitutionResponse';
import {InstitutionService} from '../../../shared/services/institution/institution.service';
import {ResponseHandlerService} from '../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ProgramResponse} from '../../../shared/models/institution/ProgramResponse';
import {PaginatedProgramsResponse} from '../../../shared/models/institution/PaginatedProgramsResponse';
import {ProgramService} from '../../../shared/services/institution/program.service';
import {AddProgramComponent} from './add-program/add-program.component';
import {debounceTime} from 'rxjs/operators';
import {EditProgramComponent} from './edit-program/edit-program.component';

@Component({
  selector: 'app-programs',
  templateUrl: './programs.component.html',
  styleUrls: ['./programs.component.scss']
})
export class ProgramsComponent implements OnInit {
  loadingPrograms = false;
  institutionID: string;
  _currentPage = 1;
  totalPages = 0;
  totalItems = 0;
  itemsPerPage = 10;
  searchControl: FormControl = new FormControl();
  currentInstitution: InstitutionResponse;
  currentProgram: ProgramResponse;
  paginatedPrograms: PaginatedProgramsResponse;
  constructor(
      private institutionService: InstitutionService,
      private programService: ProgramService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService,
      private route: ActivatedRoute,
      private modalService: NgbModal,
      private router: Router
  ) { }

  ngOnInit(): void {
    this.institutionID = this.route.snapshot.paramMap.get('institutionID');
    this.institutionService.getInstitutionByID(this.institutionID).subscribe(
        response => {
          this.currentInstitution = response;
        }
    );
    this.getPrograms(this.currentPage, this.itemsPerPage, null);
    this.searchControl.valueChanges
        .pipe(debounceTime(200))
        .subscribe(value => {
          this.getPrograms(1, this.itemsPerPage, value);
        });
  }
  get currentPage(): number {
    return this._currentPage;
  }
  set currentPage(value: number) {
    this._currentPage = value;
    if (this.searchControl.value == null) {
      this.getPrograms(this._currentPage, this.itemsPerPage, null);
    } else {
      this.getPrograms(this._currentPage, this.itemsPerPage, this.searchControl.value);
    }
  }
  getPrograms(page: number, sizePerPage: number, keyword?: string): void {
    this.loadingPrograms = true;
    this.programService.getPrograms(page - 1, sizePerPage, this.institutionID, keyword).subscribe(
        response => {
          this.paginatedPrograms = response;
          this.totalPages = response.totalPages;
          this.totalItems = response.totalItems;
          this.loadingPrograms = false;
        },
        error => {
          this.handleResponse.handleError(error);
          this.loadingPrograms = false;
        }
    );
  }
  deleteProgram(id: string): void {
    this.loadingPrograms = true;
    this.programService.deleteProgram(id).subscribe(
        response => {
          this.toastr.success('Program deleted successfully');
          this.paginatedPrograms.programs = this.paginatedPrograms.programs.filter(p => p.id !== id);
          this.loadingPrograms = false;
        },
        error => {
          this.handleResponse.handleError(error);
          this.loadingPrograms = false;
        }
    );
  }
  openAddProgramModal() {
    const modalRef = this.modalService.open(AddProgramComponent);
    modalRef.componentInstance.programAdded.subscribe(() => {
      this.getPrograms(this.currentPage, this.itemsPerPage, null);
      modalRef.close();
    });
  }
  openEditProgramModal(program: ProgramResponse) {
    const modalRef = this.modalService.open(EditProgramComponent);
    modalRef.componentInstance.program = program;
    modalRef.componentInstance.programUpdated.subscribe((updatedProgram: ProgramResponse) => {
      if (updatedProgram != null) {
        const index = this.paginatedPrograms.programs.findIndex(p => p.id === updatedProgram.id);
        if (index !== -1) {
          this.paginatedPrograms.programs[index] = updatedProgram;
        }
      }
      modalRef.close();
    }, (reason) => {
        console.log('Err!', reason);
        modalRef.close();
        }
    );
  }
  modalConfirmFunction(content: any, program: ProgramResponse) {
    this.currentProgram = program;
    this.modalService.open(content, { ariaLabelledBy: 'confirm Program' })
        .result.then((result) => {
      if (result === 'Ok') {
        this.deleteProgram(this.currentProgram.id);
        this.currentProgram = null;
      }
    }, (reason) => {
      console.log('Err!', reason);
    });
  }
  viewModules(program: ProgramResponse): void {
    this.router.navigate(['/institution', this.currentInstitution.id, 'programs', program.id, 'modules'], {
      state: { program }
    });
  }
}
