import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountBindComponent } from './account-bind.component';

describe('AccountBindComponent', () => {
  let component: AccountBindComponent;
  let fixture: ComponentFixture<AccountBindComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountBindComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountBindComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
