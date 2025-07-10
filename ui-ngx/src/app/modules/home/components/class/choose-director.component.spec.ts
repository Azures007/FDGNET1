import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseDirectorComponent } from './choose-director.component';

describe('ChooseDirectorComponent', () => {
  let component: ChooseDirectorComponent;
  let fixture: ComponentFixture<ChooseDirectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChooseDirectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseDirectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
