
import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';


@Component({
  selector: 'tb-technological-process',
  templateUrl: './technological-process.html',
  styleUrls: []
})
export class TechnologicalProcessComponent implements OnInit {

  constructor(protected store: Store<AppState>) {

  }

  ngOnInit() {

  }

}
