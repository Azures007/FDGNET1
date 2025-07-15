import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseCheckCateComponent } from './choose-check-cate.component';

describe('ChooseCheckCateComponent', () => {
  let component: ChooseCheckCateComponent;
  let fixture: ComponentFixture<ChooseCheckCateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseCheckCateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseCheckCateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
