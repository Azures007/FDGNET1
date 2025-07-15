import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseCateComponent } from './choose-cate.component';

describe('ChooseCateComponent', () => {
  let component: ChooseCateComponent;
  let fixture: ComponentFixture<ChooseCateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseCateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseCateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
