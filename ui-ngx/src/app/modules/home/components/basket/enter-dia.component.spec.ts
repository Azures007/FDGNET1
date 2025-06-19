import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterDiaComponent } from './enter-dia.component';

describe('EnterDiaComponent', () => {
  let component: EnterDiaComponent;
  let fixture: ComponentFixture<EnterDiaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EnterDiaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EnterDiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
