import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DicAddComponent } from './dic-add.component';

describe('DicAddComponent', () => {
  let component: DicAddComponent;
  let fixture: ComponentFixture<DicAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DicAddComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DicAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
