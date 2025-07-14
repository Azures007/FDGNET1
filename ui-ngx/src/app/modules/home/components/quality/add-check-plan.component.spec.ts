import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCheckPlanComponent } from './add-check-plan.component';


describe('AddCheckPlanComponent', () => {
  let component: AddCheckPlanComponent;
  let fixture: ComponentFixture<AddCheckPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddCheckPlanComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCheckPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
