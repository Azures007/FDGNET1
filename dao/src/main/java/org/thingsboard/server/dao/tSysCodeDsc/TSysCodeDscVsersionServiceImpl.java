package org.thingsboard.server.dao.tSysCodeDsc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDscVersion;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscVersionRepository;

import java.util.*;

@Service
@Slf4j
public class TSysCodeDscVsersionServiceImpl implements TSysCodeDscVersionService {
    @Autowired
    TSysCodeDscVersionRepository tSysCodeDscVersionRepository;


//    @Override
//    public List<TSysCodeDscVersion> tSysCodeDscGroupList() {
//        List<TSysCodeDscVersion> tSysCodeDscList = tSysCodeDscVersionRepository.findByCodeClId("GROUPCODE0000");
//        return tSysCodeDscList;
//    }

//    @Override
//    public void saveTSysCodeDscVersion(TSysCodeDscVersion tSysCodeDscVersion) {
//
//
//
//        if (tSysCodeDscVersion.getCodeId() == null) {
//            //新增
//            tSysCodeDscVersion.setCrtUser(tSysCodeDscVersion.getUpdateUser());
//            tSysCodeDscVersion.setCrtTime(tSysCodeDscVersion.getUpdateTime());
//            if (StringUtils.isBlank(tSysCodeDscVersion.getEnabledSt())) {
//                tSysCodeDscVersion.setEnabledSt("0");
//            }
//
//        }
//        tSysCodeDscVersionRepository.saveAndFlush(tSysCodeDscVersion);
//    }

//    @Transactional
//    @Override
//    public void saveCodeCl(TSysCodeDsc tSysCodeDsc) {
//        if ("".equals(tSysCodeDsc.getCodeValue()) || tSysCodeDsc.getCodeValue() == null) {
//            throw new RuntimeException("编码不能为空值或空字符串");
//        }
//        //同一分组下，字典编码不能重复
//        TSysCodeDsc findTSysCodeDsc = this.getCodeByCodeClAndCodeVale("GROUPCODE0000", tSysCodeDsc.getCodeValue());
//        if (findTSysCodeDsc != null) {
//            if (findTSysCodeDsc.getCodeId().intValue() == tSysCodeDsc.getCodeId().intValue()) {
//                throw new RuntimeException("同一分组下，字典编码不能重复");
//            }
//        }
//        //是否字典分类
//        tSysCodeDsc.setCodeClDsc("字典分类");
//        tSysCodeDsc.setCodeClId("GROUPCODE0000");
//        tSysCodeDsc.setIsGroup(1);
//        this.saveTSysCodeDsc(tSysCodeDsc);
//        //字典变动更新缓存
//        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
//        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
//        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
//        GlobalConstant.setCodeDscTable(content);
//    }

//    @Transactional
//    @Override
//    public void saveCode(TSysCodeDsc tSysCodeDsc) {
//        if (tSysCodeDsc.getCodeClDsc() == null || tSysCodeDsc.getCodeClId() == null) {
//            if (StringUtils.isBlank(tSysCodeDsc.getCodeClDsc()) || StringUtils.isBlank(tSysCodeDsc.getCodeClId())) {
//                throw new RuntimeException("字典分组编码和字典分组描述不能为空");
//            }
//        }
//        //同一分组下，字典编码不能重复
//        TSysCodeDsc findTSysCodeDsc = this.getCodeByCodeClAndCodeDsc(tSysCodeDsc.getCodeClId(), tSysCodeDsc.getCodeDsc());
//        if (findTSysCodeDsc != null) {
//            if (findTSysCodeDsc.getCodeId().intValue() != tSysCodeDsc.getCodeId().intValue()) {
//                throw new RuntimeException("同一分组下，字典编码不能重复");
//            }
//        }
//        tSysCodeDsc.setIsGroup(0);
//        this.saveTSysCodeDsc(tSysCodeDsc);
//        //字典变动更新缓存
//        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
//        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
//        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
//        GlobalConstant.setCodeDscTable(content);
//    }

//    @Override
//    public void deleteTSysCodeDsc(Integer codeId) {
//        tSysCodeDscRepository.deleteById(codeId);
//    }
//
//
//    @Transactional
//    @Override
//    public void deleteCode(Integer codeId) {
//        TSysCodeDsc tSysCodeDsc = this.getCodeById(codeId);
//        if (null == tSysCodeDsc) {
//            throw new RuntimeException("字典不存在");
//        }
//        if (tSysCodeDsc.getIsGroup().intValue() == 1) {
//            var list = tSysCodeDscRepository.findByCodeClId(tSysCodeDsc.getCodeValue());
//            if (null != list && list.size() > 0) {
//                throw new RuntimeException("删除字典分类时，请先删除字典分类中的字典项");
//            }
//        }
//        this.deleteTSysCodeDsc(codeId);
//        //字典变动更新缓存
//        TSysCodeDscDto tSysCodeDscDto=new TSysCodeDscDto();
//        tSysCodeDscDto.setEnabledSt(GlobalConstant.enableTrue);
//        List<TSysCodeDsc> content = this.tSysCodeDscList(0, Integer.MAX_VALUE, tSysCodeDscDto).getContent();
//        GlobalConstant.setCodeDscTable(content);
//    }

//    @Override
//    public Page<TSysCodeDsc> tSysCodeDscList(Integer current, Integer size, TSysCodeDscDto tSysCodeDscDto) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
//        Pageable pageable = PageRequest.of(current, size, sort);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("codeDsc", ExampleMatcher.GenericPropertyMatchers.contains())
//                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("is_group", ExampleMatcher.GenericPropertyMatchers.exact());
//        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
//        BeanUtils.copyProperties(tSysCodeDscDto, tSysCodeDsc);
//        tSysCodeDsc.setIsGroup(0);
//        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
//        Page<TSysCodeDsc> tSysCodeDscPage = tSysCodeDscRepository.findAll(example, pageable);
//        return tSysCodeDscPage;
//    }

    @Override
    public void saveBatch(Integer relId,List<TSysCodeDsc> tSysCodeDscList) {
//        //生成UUID，一份保存的字典版本号是一致的
//        UUID uuid = UUID.randomUUID();
//        String compactUuid = uuid.toString().replace("-", "");
//
//        //todo 做重复校验，如果重复引用重复的版本号
//
//
//        //保存整个合集同时要生成UUID的版本编号，
//        List<TSysCodeDscVersion> codeDscVersionList = new ArrayList<>();
//        for (TSysCodeDsc tSysCodeDsc:
//        tSysCodeDscList) {
//            TSysCodeDscVersion codeDscVersion = new TSysCodeDscVersion();
//            BeanUtils.copyProperties(tSysCodeDsc,codeDscVersion);
//            codeDscVersion.setCodeId(null);
//            codeDscVersion.setVersionNo(compactUuid);
//            codeDscVersion.setVersionCrtTime(new Date());
//
//            codeDscVersionList.add(codeDscVersion);
//        }
//        //todo 同时建立版本号关系表




    }

    @Override
    public Page<TSysCodeDsc> getCodeByCodeCl(Integer current, Integer size, String codeClId,String versionNo, String enabledSt) {
        Sort sort = Sort.by(Sort.Direction.DESC, "crtTime");
        Pageable pageable = PageRequest.of(current, size, sort);
        TSysCodeDscVersion tSysCodeDscVersion = new TSysCodeDscVersion();
        tSysCodeDscVersion.setCodeClId(codeClId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
        if (StringUtils.isNotBlank(enabledSt)) {
            tSysCodeDscVersion.setEnabledSt(enabledSt);
            matcher.withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<TSysCodeDscVersion> example = Example.of(tSysCodeDscVersion, matcher);
        Page<TSysCodeDscVersion> tSysCodeDscVersionList = tSysCodeDscVersionRepository.findAll(example, pageable);


        List<TSysCodeDscVersion> codeVersionList = tSysCodeDscVersionList.getContent();
        List<TSysCodeDsc> tSysCodeDscList = new ArrayList<>();
        for (TSysCodeDscVersion codeDscVersion:
                codeVersionList) {
            TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();

            BeanUtils.copyProperties(codeDscVersion,tSysCodeDsc);
            tSysCodeDscList.add(tSysCodeDsc);
        }


        return new PageImpl<TSysCodeDsc>(tSysCodeDscList, tSysCodeDscVersionList.getPageable(), tSysCodeDscVersionList.getTotalElements());
    }

//    @Override
//    public TSysCodeDsc getCodeById(Integer codeId) {
//        return tSysCodeDscRepository.findById(codeId).orElse(null);
//    }
//
//
//    @Override
//    public TSysCodeDsc getCodeByCodeClAndCodeVale(String codeClId, String codeValue) {
//
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("codeValue", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
//        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
//        tSysCodeDsc.setCodeClId(codeClId);
//        tSysCodeDsc.setCodeValue(codeValue);
////        BeanUtils.copyProperties(tSysCodeDscDto,tSysCodeDsc);
//        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
//        Optional<TSysCodeDsc> o = tSysCodeDscRepository.findOne(example);
//        TSysCodeDsc codeDsc = null;
//        if (!o.isEmpty()) {
//            codeDsc = o.get();
//        }
//
//        return codeDsc;
//    }

//    @Override
//    public TSysCodeDsc getCodeByCodeClAndCodeDsc(String codeClId, String codeDscStr) {
//
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("codeDsc", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact());
//        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
//        tSysCodeDsc.setCodeClId(codeClId);
//        tSysCodeDsc.setCodeValue(codeDscStr);
////        BeanUtils.copyProperties(tSysCodeDscDto,tSysCodeDsc);
//        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
//        Optional<TSysCodeDsc> o = tSysCodeDscRepository.findOne(example);
//        TSysCodeDsc codeDsc = null;
//        if (!o.isEmpty()) {
//            codeDsc = o.get();
//        }
//
//        return codeDsc;
//    }

    @Override
    public List<TSysCodeDscVersion> getCodeByCodeClId(String codeClId) {
        return tSysCodeDscVersionRepository.findByCodeClIdAndEnabledSt(codeClId, GlobalConstant.enableTrue);
    }




}
