import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountResetDiaComponent } from './account-reset-dia.component';

describe('AccountResetDiaComponent', () => {
  let component: AccountResetDiaComponent;
  let fixture: ComponentFixture<AccountResetDiaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountResetDiaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountResetDiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
