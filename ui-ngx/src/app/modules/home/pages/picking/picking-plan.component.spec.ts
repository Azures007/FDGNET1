import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingPlanComponent } from './picking-plan.component';

describe('PickingPlanComponent', () => {
  let component: PickingPlanComponent;
  let fixture: ComponentFixture<PickingPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingPlanComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
