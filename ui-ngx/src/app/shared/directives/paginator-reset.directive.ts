import { Directive, ElementRef, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';

@Directive({
  selector: 'mat-paginator'
})
export class PaginatorResetDirective implements OnChanges {
  @Input() length: number;
  @Input() pageIndex: number;
  @Input() pageSize: number;

  constructor(private paginator: MatPaginator, private el: ElementRef) {}

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.hasOwnProperty('length')) {
      this.checkAndResetPageIndex();
    }
  }
  ngAfterViewInit(): void {
    const labelDom = this.el.nativeElement.querySelector('.mat-paginator-page-size-label') as HTMLElement;
    labelDom.innerHTML = '条数/页';
  }

  private checkAndResetPageIndex(): void {
    if (this.paginator.pageIndex > 0) {
      const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
      if (startIndex >= this.paginator.length) {
        this.paginator.pageIndex = 0;
        this.paginator.page.emit({
          pageIndex: 0,
          pageSize: this.paginator.pageSize,
          length: this.paginator.length
        });
      }
    }
  }
}
