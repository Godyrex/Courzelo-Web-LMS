import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ProgramResponse} from '../../../shared/models/institution/ProgramResponse';
import {ResponseHandlerService} from '../../../shared/services/user/response-handler.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {debounceTime} from 'rxjs/operators';
import {ModuleResponse} from '../../../shared/models/institution/ModuleResponse';
import {PaginatedModulesResponse} from '../../../shared/models/institution/PaginatedModulesResponse';
import {ModuleService} from '../../../shared/services/institution/module.service';
import {AddModuleComponent} from './add-module/add-module.component';
import {EditModuleComponent} from './edit-module/edit-module.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-modules',
  templateUrl: './modules.component.html',
  styleUrls: ['./modules.component.scss']
})
export class ModulesComponent implements OnInit {
  loadingModules = false;
  institutionID: string;
  _currentPage = 1;
  totalPages = 0;
  totalItems = 0;
  itemsPerPage = 10;
  searchControl: FormControl = new FormControl();
  @Input() currentProgram: ProgramResponse;
  currentModule: ModuleResponse;
  paginatedModules: PaginatedModulesResponse;
  constructor(
      private moduleService: ModuleService,
      private handleResponse: ResponseHandlerService,
      private toastr: ToastrService,
      private modalService: NgbModal,
        private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.currentProgram = history.state.program;
      this.getModules(this.currentPage, this.itemsPerPage, this.currentProgram.id, null);
    });
    this.searchControl.valueChanges
        .pipe(debounceTime(200))
        .subscribe(value => {
          this.getModules(1, this.itemsPerPage, this.currentProgram.id, value);
        });
  }
  get currentPage(): number {
    return this._currentPage;
  }
  set currentPage(value: number) {
    this._currentPage = value;
    if (this.searchControl.value == null) {
      this.getModules(this._currentPage, this.itemsPerPage, this.currentProgram.id, null);
    } else {
      this.getModules(this._currentPage, this.itemsPerPage, this.currentProgram.id, this.searchControl.value);
    }
  }
  getModules(page: number, sizePerPage: number, programID: string, keyword?: string): void {
    this.loadingModules = true;
    this.moduleService.getModules(page - 1, sizePerPage, programID, keyword).subscribe(
        response => {
          this.paginatedModules = response;
          this.totalPages = response.totalPages;
          this.totalItems = response.totalItems;
          this.loadingModules = false;
        },
        error => {
          this.handleResponse.handleError(error);
          this.loadingModules = false;
        }
    );
  }
  deleteModule(id: string): void {
    this.loadingModules = true;
    this.moduleService.deleteModule(id).subscribe(
        response => {
          this.toastr.success('Program deleted successfully');
          this.paginatedModules.modules = this.paginatedModules.modules.filter(p => p.id !== id);
          this.loadingModules = false;
        },
        error => {
          this.handleResponse.handleError(error);
          this.loadingModules = false;
        }
    );
  }
  openAddModuleModal() {
    const modalRef = this.modalService.open(AddModuleComponent, {backdrop: false});
    modalRef.componentInstance.programID = this.currentProgram.id;
    modalRef.componentInstance.moduleAdded.subscribe(() => {
      this.getModules(this.currentPage, this.itemsPerPage, this.currentProgram.id, null);
      modalRef.close();
    });
  }
  openEditModuleModal(module: ModuleResponse) {
    const modalRef = this.modalService.open(EditModuleComponent, {backdrop: false});
    modalRef.componentInstance.module = module;
    modalRef.componentInstance.moduleUpdated.subscribe((updatedModule: ModuleResponse) => {
          if (updatedModule != null) {
            const index = this.paginatedModules.modules.findIndex(p => p.id === updatedModule.id);
            if (index !== -1) {
              this.paginatedModules.modules[index] = updatedModule;
            }
          }
          modalRef.close();
        }, (reason) => {
          console.log('Err!', reason);
          modalRef.close();
        }
    );
  }
  modalConfirmFunction(content: any, module: ModuleResponse) {
    this.currentModule = module;
    this.modalService.open(content, { ariaLabelledBy: 'confirm Module', backdrop: false })
        .result.then((result) => {
      if (result === 'Ok') {
        this.deleteModule(this.currentModule.id);
        this.currentModule = null;
      }
    }, (reason) => {
      console.log('Err!', reason);
    });
  }
}
