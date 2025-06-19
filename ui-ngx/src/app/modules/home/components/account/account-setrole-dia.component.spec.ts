import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountSetroleDiaComponent } from './account-setrole-dia.component';

describe('AccountSetroleDiaComponent', () => {
  let component: AccountSetroleDiaComponent;
  let fixture: ComponentFixture<AccountSetroleDiaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountSetroleDiaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountSetroleDiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
