import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ModuleResponse} from '../../../../shared/models/institution/ModuleResponse';

@Component({
  selector: 'app-view-module-parts',
  templateUrl: './view-module-parts.component.html',
  styleUrls: ['./view-module-parts.component.scss']
})
export class ViewModulePartsComponent implements OnInit{
  ngOnInit(): void {
    console.log(this.moduleResponse);
  }
  @Input() moduleResponse: ModuleResponse;
  @Output() close = new EventEmitter<void>();
  onClose(): void {
    this.close.emit();
  }

  protected readonly Object = Object;
}
