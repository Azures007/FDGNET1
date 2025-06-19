import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "myPipe",
})
export class GetMatchStrPipe implements PipeTransform {
    transform(value: any, args?: any[]): any {
        if (value) {
            let str = ''
            switch (value) {
                case -1:
                    str = "不匹配";
                    return str;
                case 0:
                    str = "0";
                    return str;
                case 1:
                    str = "匹配";
                    return str;
            }
        }
    }
}