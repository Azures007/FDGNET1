import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountAddDiaComponent } from './account-add-dia.component';

describe('AccountAddDiaComponent', () => {
  let component: AccountAddDiaComponent;
  let fixture: ComponentFixture<AccountAddDiaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountAddDiaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountAddDiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
