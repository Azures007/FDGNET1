
import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';


@Component({
  selector: 'tb-quality',
  templateUrl: './quality.component.html',
  styleUrls: []
})
export class QualityComponent implements OnInit {

  constructor(protected store: Store<AppState>) {

  }

  ngOnInit() {

  }

}
