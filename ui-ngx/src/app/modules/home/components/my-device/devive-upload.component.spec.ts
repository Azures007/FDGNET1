import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviveUploadComponent } from './devive-upload.component';

describe('DeviveUploadComponent', () => {
  let component: DeviveUploadComponent;
  let fixture: ComponentFixture<DeviveUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeviveUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeviveUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
