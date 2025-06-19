import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickingSumComponent } from './picking-sum.component';

describe('PickingSumComponent', () => {
  let component: PickingSumComponent;
  let fixture: ComponentFixture<PickingSumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PickingSumComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PickingSumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
