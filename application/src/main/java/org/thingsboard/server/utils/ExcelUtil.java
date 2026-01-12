package org.thingsboard.server.utils;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.TableStyle;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Excel工具类
 *
 **/
public class ExcelUtil {

    /**
     * 读取 Excel(多个 sheet)
     *
     * @param excel 文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        if (reader == null) {
            return null;
        }

        for (Sheet sheet : reader.getSheets()) {
            if (rowModel != null) {
                sheet.setClazz(rowModel.getClass());
            }
            reader.read(sheet);
        }

        return excelListener.getDatas();
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel 文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @param sheetNo sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo) {
        return readExcel(excel, rowModel, sheetNo, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel 文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @param sheetNo sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo, int headLineNum) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);

        if (reader == null) {
            return null;
        }

        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));

        return excelListener.getDatas();
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     *
     * @param response HttpServletResponse
     * @param list 数据 list，每个元素为一个 BaseRowModel
     * @param fileName 导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object 映射实体类，Excel 模型
     */
    public static void writeExcel(HttpServletResponse response, List<? extends BaseRowModel> list, String fileName,
                                  String sheetName, BaseRowModel object) {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);

        TableStyle tableStyle = new TableStyle();
        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        Font font = new Font();
        font.setFontHeightInPoints((short) 9);
        tableStyle.setTableHeadFont(font);
        tableStyle.setTableContentFont(font);
        sheet.setTableStyle(tableStyle);

        writer.write(list, sheet);
        writer.finish();
    }

    /**
     * 【多Sheet导出】核心方法 - 带表头，每个Sheet可自定义数据和映射实体
     *
     * @param response      HttpServletResponse 响应对象
     * @param fileName      导出的文件名（例如：用户数据报表）
     * @param sheetDataList 多个sheet的数据集集合
     */
    public static void writeExcelWithMultiSheet(HttpServletResponse response, String fileName,
                                                List<ExcelSheetData> sheetDataList) {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        // 复用你原有的表格样式，全局通用，如需单独改某个sheet样式可在循环内重写
        TableStyle tableStyle = new TableStyle();
        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        Font font = new Font();
        font.setFontHeightInPoints((short) 9);
        tableStyle.setTableHeadFont(font);
        tableStyle.setTableContentFont(font);

        // 遍历所有sheet数据，逐个写入，sheet序号从1开始自增
        int sheetNo = 1;
        for (ExcelSheetData sheetData : sheetDataList) {
            // 每个sheet创建独立的Sheet对象，核心：sheetNo自增，不能重复
            Sheet sheet = new Sheet(sheetNo, 0, sheetData.getModelClass());
            sheet.setSheetName(sheetData.getSheetName());
            sheet.setTableStyle(tableStyle);
            // 写入当前sheet的数据
            writer.write(sheetData.getDataList(), sheet);
            sheetNo++;
        }
        // 必须最后执行finish，刷新流+关闭资源，否则Excel文件损坏
        writer.finish();
    }

    /**
     * 辅助内部类：封装「单个Sheet的所有信息」
     * 承载：当前sheet的名称、数据列表、Excel映射实体类class
     */
    public static class ExcelSheetData {
        // 当前sheet的名称
        private String sheetName;
        // 当前sheet的数据集合
        private List<? extends BaseRowModel> dataList;
        // 当前sheet对应的Excel映射实体类
        private Class<? extends BaseRowModel> modelClass;

        // 全参构造器，方便快速创建对象
        public ExcelSheetData(String sheetName, List<? extends BaseRowModel> dataList,
                              Class<? extends BaseRowModel> modelClass) {
            this.sheetName = sheetName;
            this.dataList = dataList;
            this.modelClass = modelClass;
        }

        // getter/setter 必须有
        public String getSheetName() { return sheetName; }
        public void setSheetName(String sheetName) { this.sheetName = sheetName; }
        public List<? extends BaseRowModel> getDataList() { return dataList; }
        public void setDataList(List<? extends BaseRowModel> dataList) { this.dataList = dataList; }
        public Class<? extends BaseRowModel> getModelClass() { return modelClass; }
        public void setModelClass(Class<? extends BaseRowModel> modelClass) { this.modelClass = modelClass; }
    }


    public static void writeExcel(HttpServletResponse response, List<? extends BaseRowModel> list, List<? extends BaseRowModel> list2, String fileName,
                                  String sheetName, String sheetName2, BaseRowModel object, BaseRowModel object2) {

        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        if (list != null && list.size() > 0) {
            Sheet sheet = new Sheet(1, 0, object.getClass());
            sheet.setSheetName(sheetName);

            TableStyle tableStyle = new TableStyle();
            tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
            Font font = new Font();
            font.setFontHeightInPoints((short) 9);
            tableStyle.setTableHeadFont(font);
            tableStyle.setTableContentFont(font);
            sheet.setTableStyle(tableStyle);

            writer.write(list, sheet);
        }
        //页签2
        if (list2 != null && list2.size() > 0) {
            Sheet sheet = new Sheet(2, 0, object2.getClass());
            sheet.setSheetName(sheetName2);

            TableStyle tableStyle = new TableStyle();
            tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
            Font font = new Font();
            font.setFontHeightInPoints((short) 9);
            tableStyle.setTableHeadFont(font);
            tableStyle.setTableContentFont(font);
            sheet.setTableStyle(tableStyle);

            writer.write(list2, sheet);
        }

        writer.finish();
    }


    /**
     * 导出 Excel ：多个 sheet，带表头
     *
     * @param response HttpServletResponse
     * @param list 数据 list，每个元素为一个 BaseRowModel
     * @param fileName 导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object 映射实体类，Excel 模型
     */
    public static ExcelWriterFactory writeExcelWithSheets(HttpServletResponse response,
                                                          List<? extends BaseRowModel> list, String fileName,
                                                          String sheetName, BaseRowModel object) {
        ExcelWriterFactory writer = new ExcelWriterFactory(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);
        sheet.setTableStyle(getTableStyle());
        writer.write(list, sheet);

        return writer;
    }

    /**
     * 导出融资还款情况表
     *
     * @param response
     * @param list
     * @param fileName
     * @param sheetName
     * @param object
     */
    public static void writeFinanceRepayment(HttpServletResponse response, List<? extends BaseRowModel> list,
                                             String fileName, String sheetName, BaseRowModel object) {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);
        sheet.setTableStyle(getTableStyle());
        writer.write(list, sheet);

        for (int i = 1; i <= list.size(); i += 4) {
            writer.merge(i, i + 3, 0, 0);
            writer.merge(i, i + 3, 1, 1);
        }

        writer.finish();
    }

    /**
     * 导出文件时为Writer生成OutputStream
     */
    public static OutputStream getOutputStream(String fileName, HttpServletResponse response) {
        //创建本地文件
        fileName = fileName + ".xls";

        try {
            //String utf8Filename = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            return response.getOutputStream();
        } catch (Exception e) {

            throw new RuntimeException("导出异常！");
        }
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel 需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     */
    private static ExcelReader getReader(MultipartFile excel, ExcelListener excelListener) {
        String filename = excel.getOriginalFilename();

        if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
            throw new RuntimeException("文件格式错误！");
        }
        InputStream inputStream;

        try {
            inputStream = new BufferedInputStream(excel.getInputStream());

            return new ExcelReader(inputStream, null, excelListener, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 资金收支导出 Excel ：一个 sheet，带表头
     *
     * @param response HttpServletResponse
     * @param list 数据 list，每个元素为一个 BaseRowModel
     * @param fileName 导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param object 映射实体类，Excel 模型
     */
    public static void exportFundBudgetExcel(HttpServletResponse response, List<? extends BaseRowModel> list,
                                             String fileName, String sheetName, BaseRowModel object) throws IOException {
        ExcelWriter writer = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
        Sheet sheet = new Sheet(1, 0, object.getClass());
        sheet.setSheetName(sheetName);
        sheet.setTableStyle(getTableStyle());

        writer.write(list, sheet);
        writer.merge(2, 3, 0, 0);
        writer.merge(4, 13, 0, 0);
        writer.merge(14, 14, 0, 1);
        writer.finish();
    }

    /**
     * 读取Excel表格数据，封装成实体
     *
     * @param inputStream
     * @param clazz
     * @param sheetNo
     * @param headLineMun
     * @return
     */
    public static Object readExcel(InputStream inputStream, Class<? extends BaseRowModel> clazz, Integer sheetNo,
                                   Integer headLineMun) {
        if (null == inputStream) {

            throw new NullPointerException("the inputStream is null!");
        }

        ExcelListener listener = new ExcelListener();
        ExcelReader reader = new ExcelReader(inputStream, valueOf(inputStream), null, listener);
        reader.read(new Sheet(sheetNo, headLineMun, clazz));

        return listener.getDatas();
    }

    /**
     * 根据输入流，判断为xls还是xlsx，该方法原本存在于easyexcel 1.1.0 的ExcelTypeEnum中。
     */
    public static ExcelTypeEnum valueOf(InputStream inputStream) {
        try {
            FileMagic fileMagic = FileMagic.valueOf(inputStream);

            if (FileMagic.OLE2.equals(fileMagic)) {
                return ExcelTypeEnum.XLS;
            }

            if (FileMagic.OOXML.equals(fileMagic)) {
                return ExcelTypeEnum.XLSX;
            }

            throw new RuntimeException("excelTypeEnum can not null");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置全局样式
     *
     * @return
     */
    private static TableStyle getTableStyle() {
        TableStyle tableStyle = new TableStyle();

        tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);
        Font font = new Font();
        font.setBold(true);
        font.setFontHeightInPoints((short) 9);
        tableStyle.setTableHeadFont(font);
        Font fontContent = new Font();
        fontContent.setFontHeightInPoints((short) 9);
        tableStyle.setTableContentFont(fontContent);

        return tableStyle;
    }
}
