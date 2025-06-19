import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastDialogComponent } from './toast-dialog.component';

describe('ToastDialogComponent', () => {
  let component: ToastDialogComponent;
  let fixture: ComponentFixture<ToastDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ToastDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ToastDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
