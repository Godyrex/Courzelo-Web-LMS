import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewModulePartsComponent } from './view-module-parts.component';

describe('ViewModulePartsComponent', () => {
  let component: ViewModulePartsComponent;
  let fixture: ComponentFixture<ViewModulePartsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewModulePartsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewModulePartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
