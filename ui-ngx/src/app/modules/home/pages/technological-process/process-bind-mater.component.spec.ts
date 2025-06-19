import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessBindMaterComponent } from './process-bind-mater.component';

describe('ProcessBindMaterComponent', () => {
  let component: ProcessBindMaterComponent;
  let fixture: ComponentFixture<ProcessBindMaterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessBindMaterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessBindMaterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
