
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';


@Component({
    selector: 'app-Tree',
    templateUrl: './app-tree.component.html',
    styleUrls: ['./app-tree.component.scss']
})
export class AppTreeComponent implements OnInit {

    DefaultConfig: any;

    @Input() TreeData: any;
    @Input() menuName: string;
    @Input() menuId: string;
    @Input() isFlag: string;
    @Input() select: any;
    @Input() tsysMenus: string;

    @Output() NodeClickEvent = new EventEmitter<any>();

    constructor(

    ) { }

    ngOnInit() {
        this.DefaultConfig = {
            menuName: this.menuName || "Name",
            menuId: this.menuId || "Code",
            tsysMenus: this.tsysMenus || "Member",
            select: this.select ||"select",
            Expland: "Expland",
            isFlag: this.isFlag || "isFlag",
        }
        console.log(this.select)
    }

    ExplandClick(event: any) {
        if (event[this.DefaultConfig.tsysMenus] && event[this.DefaultConfig.tsysMenus].length > 0) {
            event[this.DefaultConfig.Expland] = !(event[this.DefaultConfig.Expland] || false);
        }
    }

    SelectClick(event: any) {
        if (event.isFlag == '0') {
            event.isFlag = '1'
        } else {
            event.isFlag = '0'
        }
        let flag = !event[this.DefaultConfig.select];
        event[this.DefaultConfig.select] = flag;

        this.ChildrenAll(event, event.isFlag, flag);

        this.NodeClickEvent.emit(event);
    }

    //判断是否有子元素 
    ChildrenAll(event, isFlag, flag) {
        if (event.tsysMenus && event.tsysMenus.length > 0) {
            for (let i = 0; i < event.tsysMenus.length; i++) {
                event.tsysMenus[i].isFlag = isFlag;
                event.tsysMenus[i][this.DefaultConfig.select] = flag;
                this.ChildrenAll(event.tsysMenus[i], isFlag, flag);
            }
        }
    }


    NodeClickEventEmit(event: any) {
        this.NodeClickEvent.emit(event);
    }
} 