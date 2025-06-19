import {  HttpClient } from "@angular/common/http";


export function myPost(url, data) {
    console.log(url, 'my')
    console.log(HttpClient.post)
    // return  HttpClient.post(url,data)
}
