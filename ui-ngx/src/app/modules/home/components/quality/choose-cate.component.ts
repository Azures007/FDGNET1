import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-choose-cate',
  templateUrl: './choose-cate.component.html',
  styleUrls: ['./dialog.scss', './choose-cate.component.scss']
})
export class ChooseCateComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseCateComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private qualityService: QualityService,
    private dictionaryService: DictionaryService,
    private utils: Utils,
  ) { }
  fieldNames = [];
  dataSource = [];
  total = 0;
  searchFormGroup = this.fb.group({
    current: 0,
    size: 10,
    inspectionItem: '',
    keyProcessName: '',
    productName: ''
  });
  pageSizeOptions: number[] = [10, 50, 100, 200, 500];
  ngOnInit(): void {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'QCCF0000',
      // enabledSt: 1
    }
    this.dictionaryService.fetchGetTypeTableList({
      ...par,
    }).subscribe(res => {
      this.fieldNames = res.data.list;
    })
    this.getTableData();
  }
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        inspectionItem: this.searchFormGroup.value.inspectionItem,
        keyProcessName: this.searchFormGroup.value.keyProcessName,
        productName: this.searchFormGroup.value.productName,
        isEnabled: '1',
      }
    }
    this.qualityService.fetchGetQualityCategoryList(par).subscribe(res => {
      const temp = res.data.list.map(item => {
        const disabled = this.injectData.configs.findIndex(i => i.categoryId === item.categoryId) !== -1;
        return {
          ...item,
          isChecked: false,
          configData: JSON.parse(item.configData),
          disabled,
        }
      })
      console.log(temp);
      this.dataSource = temp;
      this.total = res.data.total;
    })
  }
  getFieldValue(item) {
    const arr = [];
    item.configData.forEach(subItem => {
      const arr1 = [];
      arr1.push(subItem.productName);
      this.fieldNames.forEach(fieldItem => {
        const itemTemp = subItem.fieldList.find(item1 => item1.fieldName == fieldItem.codeValue);
        if (fieldItem.codeValue == itemTemp.fieldName) {
          arr1.push(itemTemp.isEnabled == '1' ? '已启用' : '未启用');
        }

      })
      arr.push(arr1);
    })
    return arr;
  }
  getArray() {
    return new Array(this.fieldNames.length + 1);
  }
  getFrequency(value) {
    let label = '';
    this.injectData.frequencyList?.forEach(item => {
      if (item.codeValue === value) {
        label = item.codeDsc;
      }
    })
    return label;
  }
  handleChoose(item: any) {
    this.dialogRef.close(item);
  }

  //关闭弹窗
  addDialogClose(isSubmit: boolean) {
    if(isSubmit) {
      const list = this.dataSource.filter(item => item.isChecked);
      if(!list.length) {
        this.utils.showMessage('请选择要提交的项目', 'error');
        return;
      }
      this.dialogRef.close(list);
    } else {
      this.dialogRef.close();
    }
  }
  reset() {
    this.searchFormGroup.controls['inspectionItem'].setValue('');
    this.searchFormGroup.controls['keyProcessName'].setValue('');
    this.searchFormGroup.controls['productName'].setValue('');
    this.getTableData();
  }
  submit() {

  }
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
}
