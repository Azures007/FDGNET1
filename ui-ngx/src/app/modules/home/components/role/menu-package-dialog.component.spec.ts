import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuPackageDialogComponent } from './menu-package-dialog.component';

describe('MenuPackageDialogComponent', () => {
  let component: MenuPackageDialogComponent;
  let fixture: ComponentFixture<MenuPackageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuPackageDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuPackageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
