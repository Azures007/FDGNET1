import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DicTypeAddComponent } from './dic-type-add.component';

describe('DicTypeAddComponent', () => {
  let component: DicTypeAddComponent;
  let fixture: ComponentFixture<DicTypeAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DicTypeAddComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DicTypeAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
