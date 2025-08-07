package org.thingsboard.server.dao.dailyreport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.mes.dto.DailyReportDto;
import org.thingsboard.server.dao.mes.dto.SysQualityReportCategoryDto;
import org.thingsboard.server.dao.mes.dto.TSysClassDto;
import org.thingsboard.server.dao.mes.dto.TSysQualityReportPlanVo;
import org.thingsboard.server.dao.sql.mes.DailyReport.DailyReportEntryRepository;
import org.thingsboard.server.dao.sql.mes.DailyReport.DailyReportItemRepository;
import org.thingsboard.server.dao.sql.mes.DailyReport.DailyReportRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityReport.TSysQualityReportPlanRelRepository;
import org.thingsboard.server.dao.sql.mes.tSysQualityReport.TSysQualityReportPlanRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.mes.tSysClass.TSysClassService;
import org.thingsboard.server.dao.mes.tSysPersonnelInfo.TSysPersonnelInfoService;
import org.thingsboard.server.dao.mes.tSysQualityReportCategory.TSysQualityReportCategoryService;
import org.thingsboard.server.dao.mes.vo.DailyReportVo;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class DailyReportServiceImpl implements DailyReportService{
    @Autowired
    TSysClassService tSysClassService;

    @Autowired
    TSysPersonnelInfoService tSysPersonnelInfoService;

    @Autowired
    DailyReportRepository dailyReportRepository;

    @Autowired
    DailyReportEntryRepository dailyReportEntryRepository;

    @Autowired
    TSysQualityReportPlanRepository reportPlanRepository;

    @Autowired
    TSysQualityReportPlanRelRepository reportPlanRelRepository;

    @Autowired
    TSysQualityReportCategoryService sysQualityReportCategoryService;

    @Autowired
    DailyReportItemRepository dailyReportItemRepository;;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public PageVo<DailyReportVo> selectShopPerson(String id, Integer current, Integer size) {
        TSysPersonnelInfo currentUser = tSysPersonnelInfoRepository.findAllByUserId(id);
        if(currentUser==null)
            return null;
        TSysClassDto tSysClassDto = new TSysClassDto();
        tSysClassDto.setName(currentUser.getClassName());
        Page<TSysClass> classList = tSysClassService.tSysClassList(current, size, tSysClassDto);
        List<DailyReportVo> ShopPersonMapList = new ArrayList<>();
        DailyReportVo ShopPersonMap = new DailyReportVo();
        for (TSysClass c : classList) {
            if (ShopPersonMapList.size() > 0) {
                break;
            }
            ShopPersonMap.setShopManagerId(c.getWorkshopDirectorId() + "");
            ShopPersonMap.setShopManagerName(c.getWorkshopDirector());
            ShopPersonMapList.add(ShopPersonMap);
        }
        PageVo<DailyReportVo> pageVo = new PageVo<>();
        pageVo.setList(ShopPersonMapList);
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        return pageVo;
    }

    @Override
    public String getBillNo(DailyReportVo dailyReportVo) throws SQLException {
        if (dailyReportVo.getBillNo().equals("")) {
            String billNo = dailyGetBillNo("D");
            //Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            //Statement statement = conn.createStatement();
        /*statement.execute("INSERT INTO daily_report_vo (bill_no,material_code,material_name,solut_id,solut_name,shop_manager_id,shop_manager_name) VALUES " +
                "(" + dailyReportVo.getBillNo() + "," + dailyReportVo.getMaterialName() + "," + dailyReportVo.getMaterialCode() + "," + dailyReportVo.getSolutId() + "," + dailyReportVo.getSolutName() + "," + dailyReportVo.getShopManagerId() + "," + dailyReportVo.getShopManagerName() + ")");
        dailyReport.updateByBillNo(dailyReportVo);*/
            if (billNo == null) {
                return "";
            }
            return billNo;
        } else {
            return dailyReportVo.getBillNo();
        }
    }


    @Override
    public DailyReportVo  getDailyDetail(DailyReportVo dailyReportVo) {
        TSysQualityReportPlanVo saveDto = new TSysQualityReportPlanVo();
        TSysQualityReportPlan plan = reportPlanRepository.findById(Integer.parseInt(dailyReportVo.getSolutId())).orElse(null);
        BeanUtils.copyProperties(plan, saveDto);
        List<TSysQualityReportPlanRel> items = reportPlanRelRepository.findByPlanId(Integer.parseInt(dailyReportVo.getSolutId()), Sort.by(Sort.Direction.ASC, "id"));
        List<SysQualityReportCategoryDto> dtoList=new ArrayList<>();
        for (TSysQualityReportPlanRel item : items) {
            SysQualityReportCategoryDto dto=sysQualityReportCategoryService.categoryDetail(item.getCategoryId());
            dtoList.add(dto);
        }
        saveDto.setItemList(dtoList);

        List<DailyReportDto> dailyReportDto=new ArrayList<>();
        for (SysQualityReportCategoryDto item : dtoList) {
            List<DailyReportItem> itemList=new ArrayList<>();
            DailyReportDto dto=new DailyReportDto();
            dto.setId(item.getId());
            dto.setFrequency(item.getFrequency());
            dto.setFrequencyValue(item.getFrequencyValue());
            dto.setImportantItem(item.getImportantItem());
            List<TSysQualityReportItem> tSysQualityReportItems=item.getItemList();
            for (TSysQualityReportItem item1 : tSysQualityReportItems) {
                DailyReportItem dailyReportItem=new DailyReportItem();
                dailyReportItem.setId(item1.getId());
                dailyReportItem.setFieldName(item1.getFieldName());
                dailyReportItem.setFieldTypeId(item1.getFieldType());
                dailyReportItem.setSpiltValue(item1.getDropdownFields());
                itemList.add(dailyReportItem);
            }
            dto.setItemList(itemList);
            dailyReportDto.add(dto);
        }
        dailyReportVo.setItemList(dailyReportDto);
        return dailyReportVo;
    }

    @Override
    @Transactional
    public void saveDaily(DailyReportVo dailyReportVo) {
        DailyReportHead dailyReportHead = new DailyReportHead();
        BeanUtils.copyProperties(dailyReportVo, dailyReportHead);
        if (dailyReportHead.getId() == null ||dailyReportHead.getId()==0) {
            dailyReportHead.setCreatedName(dailyReportHead.getCreatedName());
            dailyReportHead.setCreatedTime(dailyReportHead.getUpdatedTime());
        } else {
            if (dailyReportRepository.findById(dailyReportHead.getId()).isEmpty()) {
                DailyReportHead info = dailyReportRepository.findById(dailyReportHead.getId()).get();
                dailyReportHead.setCreatedTime(info.getCreatedTime());
                dailyReportHead.setCreatedName(dailyReportHead.getCreatedName());
            }
        }
        dailyReportHead.setEnabled(1);
        if(dailyReportVo.getSaveStaus()) {
            dailyReportHead.setSaveStaus(true);
        }
        else {
            dailyReportHead.setSaveStaus(false);
        }
        dailyReportHead = dailyReportRepository.saveAndFlush(dailyReportHead);
        //插入明细
        List<DailyReportDto> items = dailyReportVo.getItemList();
        List<DailyReportEntry> rels = new ArrayList<>();
        for (DailyReportDto item : items) {
            DailyReportEntry rel = new DailyReportEntry();
            BeanUtils.copyProperties(item,rel);
            rel.setDailyreportId(dailyReportHead.getId());
            rel.setId(0);
            rel=dailyReportEntryRepository.saveAndFlush(rel);
            List<DailyReportItem> itemList=item.getItemList();
            for (DailyReportItem item1 : itemList) {
                item1.setDailyreportEntryId(rel.getId());
                item1.setId(0);
                dailyReportItemRepository.saveAndFlush(item1);
            }
            rels.add(rel);
        }
    }

    @Override
    public DailyReportVo DailyDetail(Integer id) {
        DailyReportVo saveVo = new DailyReportVo();
        DailyReportHead plan = dailyReportRepository.findById(id).orElse(null);
        BeanUtils.copyProperties(plan, saveVo);
        List<DailyReportEntry> items =dailyReportEntryRepository.findByDailyreportId(saveVo.getId());
        List<DailyReportDto> Dtoitem=new ArrayList<>();
        for (DailyReportEntry item : items) {
            DailyReportDto rel = new DailyReportDto();
            BeanUtils.copyProperties(item,rel);
            List<DailyReportItem> itemList=dailyReportItemRepository.findAllByDailyreportEntryId(item.getId());
            rel.setItemList(itemList);
            Dtoitem.add(rel);
        }
        saveVo.setItemList(Dtoitem);
        return saveVo;
    }

    @Override
    public PageVo<DailyReportVo> getDailyList(Integer current, Integer size, LocalDate startTime, LocalDate endTime) {
        List<DailyReportHead> plan = dailyReportRepository.findAllByCreatedTimeBetween(startTime,endTime);
        List<DailyReportVo> saveVos = new ArrayList<>();
        for (DailyReportHead item : plan) {
            DailyReportVo saveVo = new DailyReportVo();
            BeanUtils.copyProperties(item,saveVo);
            saveVos.add(saveVo);
        }
        PageVo<DailyReportVo> pageVo = new PageVo<>();
        pageVo.setList(saveVos);
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        return pageVo;
    }

    private String dailyGetBillNo(String prefix) {
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String billNo="";
        String billNoParam = prefix + date + "%";
        String obj = dailyReportRepository.getHaveBillNo(billNoParam);
        if (obj != null) {
            billNo = prefix + date + padRight(String.valueOf(Integer.parseInt(obj.substring(obj.length() - 3)) + 1), 3, '0');
        } else {
            billNo = prefix + date + "001";
        }
        return billNo;
    }
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
}
