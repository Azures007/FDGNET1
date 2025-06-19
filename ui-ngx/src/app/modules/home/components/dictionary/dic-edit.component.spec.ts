import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DicEditComponent } from './dic-edit.component';

describe('DicEditComponent', () => {
  let component: DicEditComponent;
  let fixture: ComponentFixture<DicEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DicEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DicEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
