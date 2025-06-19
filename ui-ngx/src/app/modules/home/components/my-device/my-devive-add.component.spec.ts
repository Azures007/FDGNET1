import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyDeviveAddComponent } from './my-devive-add.component';

describe('MyDeviveAddComponent', () => {
  let component: MyDeviveAddComponent;
  let fixture: ComponentFixture<MyDeviveAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyDeviveAddComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyDeviveAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
