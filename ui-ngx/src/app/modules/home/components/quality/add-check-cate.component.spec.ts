import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCheckCateComponent } from './add-check-cate.component';

describe('AddCheckCateComponent', () => {
  let component: AddCheckCateComponent;
  let fixture: ComponentFixture<AddCheckCateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddCheckCateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCheckCateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
