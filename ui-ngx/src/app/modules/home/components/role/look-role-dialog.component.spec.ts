import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LookRoleDialogComponent } from './look-role-dialog.component';

describe('LookRoleDialogComponent', () => {
  let component: LookRoleDialogComponent;
  let fixture: ComponentFixture<LookRoleDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LookRoleDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LookRoleDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
