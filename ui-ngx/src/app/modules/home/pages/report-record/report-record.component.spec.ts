import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryMgtComponent } from './inventory-mgt.component';

describe('InventoryMgtComponent', () => {
  let component: InventoryMgtComponent;
  let fixture: ComponentFixture<InventoryMgtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InventoryMgtComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryMgtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
