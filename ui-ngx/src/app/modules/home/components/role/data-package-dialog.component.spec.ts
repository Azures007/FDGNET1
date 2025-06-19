import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataPackageDialogComponent } from './data-package-dialog.component';

describe('DataPackageDialogComponent', () => {
  let component: DataPackageDialogComponent;
  let fixture: ComponentFixture<DataPackageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataPackageDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataPackageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
