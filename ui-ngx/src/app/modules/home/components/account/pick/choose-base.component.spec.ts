import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseBaseComponent } from './choose-base.component';

describe('ChooseBaseComponent', () => {
  let component: ChooseBaseComponent;
  let fixture: ComponentFixture<ChooseBaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseBaseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
