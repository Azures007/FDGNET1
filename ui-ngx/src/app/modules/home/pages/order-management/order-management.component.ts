
import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';


@Component({
  selector: 'tb-order-management',
  templateUrl: './order-management.component.html',
  styleUrls: []
})
export class OrderManagementComponent implements OnInit {

  constructor(protected store: Store<AppState>) {

  }

  ngOnInit() {

  }

}
