import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeOrgComponent } from './change-org.component';

describe('ChooseBaseComponent', () => {
  let component: ChangeOrgComponent;
  let fixture: ComponentFixture<ChangeOrgComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeOrgComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeOrgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
