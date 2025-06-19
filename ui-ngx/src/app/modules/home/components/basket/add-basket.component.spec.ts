import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddBasketComponent } from './add-basket.component';

describe('AddBasketComponent', () => {
  let component: AddBasketComponent;
  let fixture: ComponentFixture<AddBasketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddBasketComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddBasketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
