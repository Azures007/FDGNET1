import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: 'button'
})
export class TableBtnStyleResetDirective {
  constructor(private el: ElementRef) { }
  ngAfterViewInit() {
    const $btmDom = $(this.el.nativeElement);
    if ($btmDom.closest('td').length) {
      $btmDom.css('font-size', '12px');
    }
  }
}
