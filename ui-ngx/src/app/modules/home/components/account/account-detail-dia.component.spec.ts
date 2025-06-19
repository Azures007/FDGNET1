import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountDetailDiaComponent } from './account-detail-dia.component';

describe('AccountDetailDiaComponent', () => {
  let component: AccountDetailDiaComponent;
  let fixture: ComponentFixture<AccountDetailDiaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountDetailDiaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailDiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
