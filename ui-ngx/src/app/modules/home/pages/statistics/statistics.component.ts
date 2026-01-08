
import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';


@Component({
  selector: 'tb-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: []
})
export class StatisticsComponent implements OnInit {

  constructor(protected store: Store<AppState>) {

  }

  ngOnInit() {

  }

}
