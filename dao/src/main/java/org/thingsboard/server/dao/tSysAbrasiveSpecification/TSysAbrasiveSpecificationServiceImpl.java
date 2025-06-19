package org.thingsboard.server.dao.tSysAbrasiveSpecification;

import jnr.ffi.annotations.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.LichengConstants;
import org.thingsboard.server.common.data.MidDept;
import org.thingsboard.server.common.data.MidOrg;
import org.thingsboard.server.common.data.TSysAbrasiveSpecification;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.tSysAbrasiveSpecification.TSysAbrasiveSpecificationDto;
import org.thingsboard.server.dao.midDetp.MidDeptService;
import org.thingsboard.server.dao.sql.licheng.MidDeptRepository;
import org.thingsboard.server.dao.sql.licheng.MidOrgRepository;
import org.thingsboard.server.dao.sql.tSysAbrasiveSpecification.TSysAbrasiveSpecificationRepository;
import org.thingsboard.server.dao.vo.PageVo;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TSysAbrasiveSpecificationServiceImpl implements TSysAbrasiveSpecificationService {

    @Autowired
    TSysAbrasiveSpecificationRepository tSysAbrasiveSpecificationRepository;

    @Autowired
    MidOrgRepository midOrgRepository;

    @Autowired
    MidDeptRepository midDeptRepository;

    @Override
    public PageVo<TSysAbrasiveSpecification> getList(Integer current, Integer size, TSysAbrasiveSpecificationDto tSysAbrasiveSpecificationDto) {

        //排序
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createdTime"));
        // 封装成Pageable 便可分页查询
        Pageable pageable = PageRequest.of(current, size, Sort.by(orders));
        Page<TSysAbrasiveSpecification> tSysAbrasiveSpecificationPage = tSysAbrasiveSpecificationRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            //磨具规格
            if (!StringUtils.isEmpty(tSysAbrasiveSpecificationDto.getAbrasiveSpecificationNo())) {
                predicates.add(criteriaBuilder.like(root.get("abrasiveSpecificationNo").as(String.class), "%" + tSysAbrasiveSpecificationDto.getAbrasiveSpecificationNo() + "%"));
            }
            //状态
            if (!StringUtils.isEmpty(tSysAbrasiveSpecificationDto.getAbrasiveSpecificationStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("abrasiveSpecificationStatus").as(String.class), tSysAbrasiveSpecificationDto.getAbrasiveSpecificationStatus()));
            }
            //生产组织id
            if (!StringUtils.isEmpty(tSysAbrasiveSpecificationDto.getKdOrgId())) {
                predicates.add(criteriaBuilder.equal(root.get("kdOrgId"), tSysAbrasiveSpecificationDto.getKdOrgId()));
            }
            //生产车间id
            if (!StringUtils.isEmpty(tSysAbrasiveSpecificationDto.getKdDeptId())) {
                predicates.add(criteriaBuilder.equal(root.get("kdWorkshopId"), tSysAbrasiveSpecificationDto.getKdDeptId()));
            }
            //是否删除 0：非删除 1：删除
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), LichengConstants.IS_DELETED_0));
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
        ////查找分页数据
        PageVo<TSysAbrasiveSpecification> pageVo = new PageVo(tSysAbrasiveSpecificationPage);
        return pageVo;
    }

    @Override
    public TSysAbrasiveSpecification getDetail(Integer abrasiveSpecificationId) {
        TSysAbrasiveSpecification tSysAbrasiveSpecification = tSysAbrasiveSpecificationRepository.findById(abrasiveSpecificationId).orElse(null);
        return tSysAbrasiveSpecification;
    }

    @Override
    public void save(TSysAbrasiveSpecification tSysAbrasiveSpecification) {
        if (tSysAbrasiveSpecification.getAbrasiveSpecificationId() == null) {
            //新增
            tSysAbrasiveSpecification.setCreatedName(tSysAbrasiveSpecification.getUpdatedName());
            tSysAbrasiveSpecification.setCreatedTime(tSysAbrasiveSpecification.getUpdatedTime());
        }
        MidOrg byKdOrgId = midOrgRepository.findByKdOrgId(tSysAbrasiveSpecification.getKdOrgId());
        if (byKdOrgId != null) {
            tSysAbrasiveSpecification.setKdOrgName(byKdOrgId.getKdOrgName());
            tSysAbrasiveSpecification.setKdOrgNumber(byKdOrgId.getKdOrgNum());
        } else {
            tSysAbrasiveSpecification.setKdOrgName("");
            tSysAbrasiveSpecification.setKdOrgNumber("");
        }

        MidDept midDept = midDeptRepository.findByKdDeptId(tSysAbrasiveSpecification.getKdWorkshopId());
        if (midDept != null) {
            tSysAbrasiveSpecification.setKdWorkshopName(midDept.getKdDeptName());
            tSysAbrasiveSpecification.setKdWorkshopNumber(midDept.getKdDeptNum());
        } else {
            tSysAbrasiveSpecification.setKdWorkshopName("");
            tSysAbrasiveSpecification.setKdWorkshopNumber("");
        }
        //业务版本1
        //是否删除:0非删除
        tSysAbrasiveSpecification.setIsDeleted(LichengConstants.IS_DELETED_0);
        try {
            tSysAbrasiveSpecificationRepository.saveAndFlush(tSysAbrasiveSpecification);
        } catch (Exception e) {
            throw new RuntimeException("保存或修改失败,请刷新后重试");
        }

    }

    @Override
    public void delete(Integer abrasiveSpecificationId, Integer version, String updatedName) {
        TSysAbrasiveSpecification tSysAbrasiveSpecification = tSysAbrasiveSpecificationRepository.findByAbrasiveSpecificationIdAndVersion(abrasiveSpecificationId, version);
        if (tSysAbrasiveSpecification != null) {
            tSysAbrasiveSpecification.setUpdatedName(updatedName);
            tSysAbrasiveSpecification.setUpdatedTime(new Date());
            //是否删除 0:非删除, 1:删除
            tSysAbrasiveSpecification.setIsDeleted(LichengConstants.IS_DELETED_1);
            tSysAbrasiveSpecificationRepository.saveAndFlush(tSysAbrasiveSpecification);
        }

    }

    @Override
    public void setPickingStatus(Integer abrasiveSpecificationId, String abrasiveSpecificationStatus, String updatedName) {
        TSysAbrasiveSpecification tSysAbrasiveSpecification = tSysAbrasiveSpecificationRepository.findById(abrasiveSpecificationId).orElse(null);
        if (tSysAbrasiveSpecification != null) {
            tSysAbrasiveSpecification.setUpdatedName(updatedName);
            tSysAbrasiveSpecification.setUpdatedTime(new Date());
            //状态:0:禁用, 1:启用'
            tSysAbrasiveSpecification.setAbrasiveSpecificationStatus(abrasiveSpecificationStatus);
            tSysAbrasiveSpecificationRepository.saveAndFlush(tSysAbrasiveSpecification);
        }
    }

    @Override
    public List<TSysAbrasiveSpecification> findAll() {
        TSysAbrasiveSpecification tSysAbrasiveSpecification=new TSysAbrasiveSpecification();
        tSysAbrasiveSpecification.setIsDeleted(GlobalConstant.enableFalse);
        tSysAbrasiveSpecification.setAbrasiveSpecificationStatus("1");
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("isDeleted",ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("abrasiveSpecificationStatus", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TSysAbrasiveSpecification> example=Example.of(tSysAbrasiveSpecification,matcher);
        return tSysAbrasiveSpecificationRepository.findAll(example);
    }


}
