import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassLeaderComponent } from './class-leader.component';

describe('ClassLeaderComponent', () => {
  let component: ClassLeaderComponent;
  let fixture: ComponentFixture<ClassLeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassLeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassLeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
