

import { Component, OnInit } from '@angular/core';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';

@Component({
  selector: 'tb-bi-manage',
  templateUrl: './manage.html',
  styleUrls: ['./manage.scss']
})
export class ManageComponent implements OnInit {

  constructor(private dictionaryService: DictionaryService, private sanitizer: DomSanitizer, private router: Router) {

  }
  iframeUrl: SafeResourceUrl = '';
  prefixMap = {
    '/bi/manage': 'productionData',
    '/bi/package': 'packageData',
    '/bi/oven': 'ovenData'
  }
  ngOnInit() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'BI0000',
      enabledSt: 1
    }
    this.dictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.iframeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(res.data?.list[0]?.codeValue + this.prefixMap[this.router.url]);
    })
  }

}
