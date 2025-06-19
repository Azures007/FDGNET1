import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmitWorkReportComponent } from './submit-work-report.component';

describe('SubmitWorkReportComponent', () => {
  let component: SubmitWorkReportComponent;
  let fixture: ComponentFixture<SubmitWorkReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubmitWorkReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmitWorkReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
