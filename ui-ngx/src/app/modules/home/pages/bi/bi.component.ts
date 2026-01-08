
import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';


@Component({
  selector: 'tb-bi',
  templateUrl: './bi.component.html',
  styleUrls: []
})
export class BiComponent implements OnInit {

  constructor(protected store: Store<AppState>) {

  }

  ngOnInit() {

  }

}
