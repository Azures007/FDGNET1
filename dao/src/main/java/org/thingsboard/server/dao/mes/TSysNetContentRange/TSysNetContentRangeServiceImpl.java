package org.thingsboard.server.dao.mes.TSysNetContentRange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.sys.TSysNetContentRange;
import org.thingsboard.server.dao.mes.dto.TSysNetContentRangeDto;
import org.thingsboard.server.dao.mes.vo.TSysNetContentRangeVo;
import org.thingsboard.server.dao.sql.mes.TSysNetContentRange.TSysNetContentRangeRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TSysNetContentRangeServiceImpl implements TSysNetContentRangeService {

    @Autowired
    private TSysNetContentRangeRepository tSysNetContentRangeRepository;

    @Override
    public Page<TSysNetContentRangeVo> tSysNetContentRangeList(String toString, Integer current, Integer size, String sortField, String sortOrder, TSysNetContentRangeDto tSysNetContentRangeDto) {
        // 默认按创建时间升序排序
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, "createTime");
        }
        PageRequest pageable = PageRequest.of(current , size, sort);

        Specification<TSysNetContentRange> specification = (Root<TSysNetContentRange> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 根据产品编码查询
            if (tSysNetContentRangeDto.getMaterialCode() != null && !tSysNetContentRangeDto.getMaterialCode().isEmpty()) {
                predicates.add(cb.like(root.get("materialCode"), "%" + tSysNetContentRangeDto.getMaterialCode() + "%"));
            }

            // 根据产品名称查询
            if (tSysNetContentRangeDto.getMaterialName() != null && !tSysNetContentRangeDto.getMaterialName().isEmpty()) {
                predicates.add(cb.like(root.get("materialName"), "%" + tSysNetContentRangeDto.getMaterialName() + "%"));
            }

            // 根据产品规格查询
            if (tSysNetContentRangeDto.getMaterialModel() != null && !tSysNetContentRangeDto.getMaterialModel().isEmpty()) {
                predicates.add(cb.like(root.get("materialModel"), "%" + tSysNetContentRangeDto.getMaterialModel() + "%"));
            }

            // 根据状态查询
            if (tSysNetContentRangeDto.getStatus() != null && !tSysNetContentRangeDto.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), tSysNetContentRangeDto.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TSysNetContentRange> netContentRangePage = tSysNetContentRangeRepository.findAll(specification, pageable);
        List<TSysNetContentRangeVo> voList = new ArrayList<>();
        for (TSysNetContentRange record : netContentRangePage.getContent()) {
            TSysNetContentRangeVo vo = new TSysNetContentRangeVo();
            BeanUtils.copyProperties(record, vo);
            voList.add(vo);
        }
        return new PageImpl<>(voList, pageable, netContentRangePage.getTotalElements());
    }

    @Override
    public TSysNetContentRange saveNetContentRange(TSysNetContentRange tSysNetContentRange) {
        // 校验下限值要小于或等于上限值
        if (tSysNetContentRange.getLowerLimit() != null && tSysNetContentRange.getUpperLimit() != null) {
            if (tSysNetContentRange.getLowerLimit() > tSysNetContentRange.getUpperLimit()) {
                throw new RuntimeException("下限值不能大于上限值");
            }
        }

        // 如果ID为空，则为新增，需要检查material_id是否已存在
        if (tSysNetContentRange.getId() == null) {
            // 检查是否存在相同的material_id
            if (tSysNetContentRange.getMaterialId() != null) {
                Specification<TSysNetContentRange> specification = (Root<TSysNetContentRange> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                    return cb.equal(root.get("materialId"), tSysNetContentRange.getMaterialId());
                };
                long count = tSysNetContentRangeRepository.count(specification);
                if (count > 0) {
                    throw new RuntimeException("该物品已经添加");
                }
            }
            tSysNetContentRange.setCreateTime(new Date());
        } else {
            // 如果ID不为空，则为编辑，需要保留原有的创建人和创建时间
            TSysNetContentRange existingRecord = tSysNetContentRangeRepository.findById(tSysNetContentRange.getId()).orElse(null);
            if (existingRecord != null) {
                // 保留原有的创建人和创建时间
                tSysNetContentRange.setCreateUser(existingRecord.getCreateUser());
                tSysNetContentRange.setCreateTime(existingRecord.getCreateTime());
            }
        }
        // 保存到数据库
        return tSysNetContentRangeRepository.save(tSysNetContentRange);
    }

    @Override
    public void deleteNetContentRangeById(Integer id) {
        tSysNetContentRangeRepository.deleteById(id);
    }

    @Override
    public TSysNetContentRange getNetContentRangeById(Integer id) {
        return tSysNetContentRangeRepository.findById(id).orElse(null);
    }

    @Override
    public TSysNetContentRange updateNetContentRangeStatus(Integer id, String status) {
        TSysNetContentRange tSysNetContentRange = tSysNetContentRangeRepository.findById(id).orElse(null);
        if (tSysNetContentRange != null) {
            tSysNetContentRange.setStatus(status);
            return tSysNetContentRangeRepository.save(tSysNetContentRange);
        }
        return null;
    }
    
    @Override
    public TSysNetContentRange getNetContentRangeByMaterialCode(String materialCode) {
        Specification<TSysNetContentRange> specification = (Root<TSysNetContentRange> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("materialCode"), materialCode));
            predicates.add(cb.equal(root.get("status"), "1"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<TSysNetContentRange> results = tSysNetContentRangeRepository.findAll(specification);
        return results.isEmpty() ? null : results.get(0);
    }
}
