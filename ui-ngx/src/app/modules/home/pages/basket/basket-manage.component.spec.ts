import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasketManageComponent } from './basket-manage.component';

describe('BasketManageComponent', () => {
  let component: BasketManageComponent;
  let fixture: ComponentFixture<BasketManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BasketManageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BasketManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
