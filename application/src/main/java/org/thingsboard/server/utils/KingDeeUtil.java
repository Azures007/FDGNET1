package org.thingsboard.server.utils;

import com.alibaba.fastjson.JSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thingsboard.server.vo.KingdeeInterfaceParticipate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 金蝶接口调用工具类
 * 参考金蝶地址：https://openapi.open.kingdee.com/ApiCenterDoc
 */
public class KingDeeUtil {
    public static String POST_K3CloudURL;

    // Cookie 值
    private static String CookieVal = null;

    private static Map map = new HashMap();

    static {
        map.put("Save",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc");
        map.put("View",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.View.common.kdsvc");
        map.put("Submit",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Submit.common.kdsvc");
        map.put("Audit",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc");
        map.put("UnAudit",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UnAudit.common.kdsvc");
        map.put("StatusConvert",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.StatusConvert.common.kdsvc");
        map.put("ExcuteOperation",
                "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExcuteOperation.common.kdsvc");
    }

    // HttpURLConnection
    private static HttpURLConnection initUrlConn(String url, JSONArray paras)
            throws IOException, JSONException {
        URL postUrl = new URL(POST_K3CloudURL.concat(url));
        System.out.println(" 请求地址 postUrl====== "+postUrl);

        HttpURLConnection connection = (HttpURLConnection) postUrl
                .openConnection();
        if (CookieVal != null) {
            connection.setRequestProperty("Cookie", CookieVal);
        }
        if (!connection.getDoOutput()) {
            connection.setDoOutput(true);
        }
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "application/json");
        DataOutputStream out = new DataOutputStream(
                connection.getOutputStream());

        UUID uuid = UUID.randomUUID();
        int hashCode = uuid.toString().hashCode();

        JSONObject jObj = new JSONObject();

        jObj.put("format", 1);
        jObj.put("useragent", "ApiClient");
        jObj.put("rid", hashCode);
        jObj.put("parameters", chinaToUnicode(paras.toString()));
        jObj.put("timestamp", new Date().toString());
        jObj.put("v", "1.0");

        out.writeBytes(jObj.toString());
        out.flush();
        out.close();

        return connection;
    }

    /**
     * 金蝶用户登录接口
     * @param ipAppress 金蝶ip地址
     * @param dbId      账套id
     * @param user      用户账号
     * @param pwd       用户密码
     * @param lang      系统语言
     * @return          是否登录成功
     * @throws Exception    /
     */
    public static boolean login(String ipAppress, String dbId, String user, String pwd, int lang)
            throws IOException, JSONException {

        boolean bResult = false;

        POST_K3CloudURL = ipAppress;
        String sUrl = "Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";

        JSONArray jParas = new JSONArray();
        jParas.put(dbId);// 帐套Id
        jParas.put(user);// 用户名
        jParas.put(pwd);// 密码
        jParas.put(lang);// 语言

        HttpURLConnection connection = initUrlConn(sUrl, jParas);
        // 获取Cookie
        String key = null;
        for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase("Set-Cookie")) {
                String tempCookieVal = connection.getHeaderField(i);
                if (tempCookieVal.startsWith("kdservice-sessionid")) {
                    CookieVal = tempCookieVal;
                    break;
                }
            }
        }
        System.out.println(CookieVal);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String line;
        System.out.println(" ============================= ");
        System.out.println(" Contents of post request ");
        System.out.println(" ============================= ");
        while ((line = reader.readLine()) != null) {
            String sResult = new String(line.getBytes(), "utf-8");
            System.out.println(sResult);
            bResult = line.contains("\"LoginResultType\":1");
        }
        System.out.println(" ============================= ");
        System.out.println(" Contents of post request ends ");
        System.out.println(" ============================= ");
        reader.close();

        connection.disconnect();

        return bResult;
    }

//    public static  boolean GenStkInstock(JSONArray jara) throws Exception{
//        return invokeCust("GenStkInstock",jara);
//    }
//
//    public static  boolean GenTransfer(JSONArray jara) throws Exception{
//        return invokeCust("GenTransfer",jara);
//    }

    // Save
    public static boolean Save(String formId, String content)  throws IOException, JSONException {
        return invoke("Save", formId, content);
    }

    // View
    public static boolean View(String formId, String content)  throws IOException, JSONException {
        return invoke("View", formId, content);
    }

    // Submit
    public static boolean Submit(String formId, String content)  throws IOException, JSONException {
        return invoke("Submit", formId, content);
    }

    // Audit
    public static boolean Audit(String formId, String content)  throws IOException, JSONException {
        return invoke("Audit", formId, content);
    }

    // UnAudit
    public static boolean UnAudit(String formId, String content)  throws IOException, JSONException {
        return invoke("UnAudit", formId, content);
    }

    // StatusConvert
    public static boolean StatusConvert(String formId, String content)
             throws IOException, JSONException {
        return invoke("StatusConvert", formId, content);
    }

    // StatusConvert
    public static boolean ExcuteOperation(String formId,String opNumber, String content)
            throws IOException, JSONException {
        return invoke("ExcuteOperation", formId, opNumber, content);
    }


    public static boolean invokeCust(String deal,JSONArray jParas)  throws Exception{
        System.out.println("连接金蝶系统");
        String sUrl = map.get(deal).toString();
       // jParas = new JSONArray();
        HttpURLConnection connectionInvoke = initUrlConn(sUrl, jParas);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connectionInvoke.getInputStream()));

        String line;
        System.out.println(" ============================= ");
        System.out.println(deal + "接口开始读取" + jParas
                + "请求 ");
        String sResult = "";
        while ((line = reader.readLine()) != null) {
            sResult = new String(line.getBytes(), "utf-8");
            System.out.println(sResult);
        }
        System.out.println(" 请求读取结束 ");
        System.out.println("=============================");
        reader.close();
        connectionInvoke.disconnect();
        Map map = (Map) JSON.parse(sResult);
        if(map.get("code").equals(200)){
            return true;
        }else{
            throw new RuntimeException(map.get("message").toString());
        }
    }

    private static boolean invoke(String deal, String formId, String content)
            throws IOException, JSONException {
        System.out.println("连接金蝶系统");
        String sUrl = map.get(deal).toString();
        JSONArray jParas = new JSONArray();
        jParas.put(formId);
        jParas.put(content);

        HttpURLConnection connectionInvoke = initUrlConn(sUrl, jParas);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connectionInvoke.getInputStream()));

        String line;
        System.out.println(" ============================= ");
        System.out.println(deal + "接口开始读取" + content
                + "请求 ");
        String sResult = "";
        while ((line = reader.readLine()) != null) {
            sResult = new String(line.getBytes(), "utf-8");
            System.out.println(sResult);
        }
        System.out.println(" 请求读取结束 ");
        System.out.println("=============================");
        reader.close();

        connectionInvoke.disconnect();
        Map map = (Map) JSON.parse(sResult);
        Map map2 = (Map) map.get("Result");
        String json = map2.get("ResponseStatus").toString();
        KingdeeInterfaceParticipate kdip = null;
        if (isNullString(json)) {
            kdip = JSON.parseObject(json, KingdeeInterfaceParticipate.class);
        }
        if (deal.equals("Audit") && (kdip.getErrorCode() == 500 || !kdip.isIsSuccess()) || sResult.contains("response_error")) {
            return false;
//            Invoke(deal, formId, content);
        }
        return true;
    }

    private static boolean invoke(String deal, String formId, String opNumber, String content)
            throws IOException, JSONException {
        System.out.println("连接金蝶系统");
        String sUrl = map.get(deal).toString();
        JSONArray jParas = new JSONArray();
        jParas.put(formId);
        jParas.put(opNumber);
        jParas.put(content);

        HttpURLConnection connectionInvoke = initUrlConn(sUrl, jParas);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connectionInvoke.getInputStream()));

        String line;
        System.out.println(" ============================= ");
        System.out.println(deal + "接口开始读取" + content
                + "请求 ");
        String sResult = "";
        while ((line = reader.readLine()) != null) {
            sResult = new String(line.getBytes(), "utf-8");
            System.out.println(sResult);
        }
        System.out.println(" 请求读取结束 ");
        System.out.println("=============================");
        reader.close();

        connectionInvoke.disconnect();
        Map map = (Map) JSON.parse(sResult);
        Map map2 = (Map) map.get("Result");
        String json = map2.get("ResponseStatus").toString();
        KingdeeInterfaceParticipate kdip = null;
        if (isNullString(json)) {
            kdip = JSON.parseObject(json, KingdeeInterfaceParticipate.class);
        }
        if (deal.equals("Audit") && (kdip.getErrorCode() == 500 || !kdip.isIsSuccess()) || sResult.contains("response_error") || deal.equals("ExcuteOperation") && kdip.getErrors().size() > 0) {
            return false;
//            Invoke(deal, formId, content);
        }
        return true;
    }

    /**
     * 非空判定
     * @param obj   要判定的字符串
     * @return  不为空不为null返回true
     */
    public static Boolean isNullString(String obj){
        //排除空集合json字符串
        String obje = obj != null ?obj.replace("[","").replace("]","") : null;
        if (obje != null && !"".equals(obje.trim()) && !"null".equals(obje)){
            return true;
        }
        return false;
    }


    /**
     * 把中文转成Unicode码
     *
     * @param str   要改变的字符串
     * @return  /
     */
    public static String chinaToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            // 汉字范围 \u4e00-\u9fa5 (中文)
            if (chr1 >= 19968 && chr1 <= 171941) {
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
}
