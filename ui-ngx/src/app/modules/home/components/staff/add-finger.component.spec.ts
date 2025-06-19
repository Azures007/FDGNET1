import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFingerComponent } from './add-finger.component';

describe('AddFingerComponent', () => {
  let component: AddFingerComponent;
  let fixture: ComponentFixture<AddFingerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddFingerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddFingerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
