

import { Component, OnInit } from '@angular/core';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'tb-bi-manage',
  templateUrl: './manage.html',
  styleUrls: ['./manage.scss']
})
export class ManageComponent implements OnInit {

  constructor(private dictionaryService: DictionaryService, private sanitizer: DomSanitizer) {

  }
  iframeUrl: SafeResourceUrl = '';
  ngOnInit() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'BI0000',
      enabledSt: 1
    }
    this.dictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.iframeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(res.data?.list[0]?.codeValue + 'productionData');
    })
  }

}
