package org.thingsboard.server.dao.mes.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.mes.sys.TSysDemo;
import org.thingsboard.server.dao.mes.dto.TSysDemoDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.sql.mes.demo.TSysDemoRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 样例服务实现类
 * @author: hhh
 * @date: 2025/8/13 15:00
 * @description: 实现样例的增删改查功能
 */
@Slf4j
@Service("tSysDemoService")
public class TSysDemoServiceImpl implements TSysDemoService {

    @Autowired
    private TSysDemoRepository tSysDemoRepository;

    @Override
    public PageVo<TSysDemo> tSysDemoList(Integer current, Integer size, TSysDemoDto tSysDemoDto) {
        log.info("查询样例列表，当前页：{}，每页大小：{}，查询条件：{}", current, size, tSysDemoDto);

        // 创建分页请求，按ID降序排列
        PageRequest pageRequest = PageRequest.of(current, size, Sort.by(Sort.Direction.DESC, "demoId"));

        // 创建查询条件
        Specification<TSysDemo> specification = createSpecification(tSysDemoDto);

        // 执行分页查询
        Page<TSysDemo> pageResult = tSysDemoRepository.findAll(specification, pageRequest);

        // 构造返回结果
        PageVo<TSysDemo> pageVo = new PageVo<>(pageResult);
        pageVo.setCurrent(current);
        pageVo.setSize(size);

        log.info("查询样例列表完成，总数量：{}", pageVo.getTotal());
        return pageVo;
    }

    @Override
    public TSysDemo getTSysDemoById(Integer demoId) {
        log.info("根据ID查询样例，demoId：{}", demoId);

        if (demoId == null || demoId <= 0) {
            throw new RuntimeException("样例ID不能为空或无效");
        }

        TSysDemo tSysDemo = tSysDemoRepository.findById(demoId)
                .orElseThrow(() -> new RuntimeException("样例不存在，demoId：" + demoId));

        log.info("查询样例成功：{}", tSysDemo);
        return tSysDemo;
    }

    /**
     * 新增或更新样例（demoId为空或<=0时为新增，否则为更新）
     * @param tSysDemo 样例对象
     */
    @Override
    @Transactional
    public void saveOrUpdateTSysDemo(TSysDemo tSysDemo) {
        if (tSysDemo.getDemoId() == null || tSysDemo.getDemoId() <= 0) {
            // 新增
            log.info("保存样例：{}", tSysDemo);

            // 数据验证
            validateTSysDemo(tSysDemo);

            // 设置创建时间和创建人
            Date now = new Date();
            tSysDemo.setCreatedTime(now);
            tSysDemo.setUpdatedTime(now);

            // 设置默认启用状态
            if (tSysDemo.getEnabled() == null) {
                tSysDemo.setEnabled(1);
            }

            // 保存样例
            TSysDemo savedDemo = tSysDemoRepository.save(tSysDemo);
            log.info("样例保存成功，ID：{}", savedDemo.getDemoId());
        } else {
            // 更新
            log.info("更新样例：{}", tSysDemo);

            // 检查样例是否存在
            TSysDemo existingDemo = tSysDemoRepository.findById(tSysDemo.getDemoId())
                    .orElseThrow(() -> new RuntimeException("样例不存在，demoId：" + tSysDemo.getDemoId()));

            // 数据验证
            validateTSysDemo(tSysDemo);

            // 保留原有的创建时间和创建人
            tSysDemo.setCreatedTime(existingDemo.getCreatedTime());
            tSysDemo.setCreatedUser(existingDemo.getCreatedUser());

            // 设置更新时间
            tSysDemo.setUpdatedTime(new Date());

            // 更新样例
            tSysDemoRepository.save(tSysDemo);
            log.info("样例更新成功，ID：{}", tSysDemo.getDemoId());
        }
    }

    @Override
    @Transactional
    public void deleteTSysDemo(Integer demoId) {
        log.info("删除样例，demoId：{}", demoId);

        if (demoId == null || demoId <= 0) {
            throw new RuntimeException("样例ID不能为空或无效");
        }

        // 检查样例是否存在
        if (!tSysDemoRepository.existsById(demoId)) {
            throw new RuntimeException("样例不存在，demoId：" + demoId);
        }

        // 删除样例
        tSysDemoRepository.deleteById(demoId);
        log.info("样例删除成功，ID：{}", demoId);
    }

    /**
     * 创建查询条件
     * @param tSysDemoDto 查询条件DTO
     * @return 查询规格
     */
    private Specification<TSysDemo> createSpecification(TSysDemoDto tSysDemoDto) {
        return (Root<TSysDemo> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 根据样例名称查询
            if (tSysDemoDto != null && StringUtils.isNotBlank(tSysDemoDto.getDemoName())) {
                predicates.add(cb.like(root.get("demoName"), "%" + tSysDemoDto.getDemoName() + "%"));
            }

            // 根据样例编码查询
            if (tSysDemoDto != null && StringUtils.isNotBlank(tSysDemoDto.getDemoNumber())) {
                predicates.add(cb.like(root.get("demoNumber"), "%" + tSysDemoDto.getDemoNumber() + "%"));
            }

            // 根据启用状态查询
            if (tSysDemoDto != null && tSysDemoDto.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), tSysDemoDto.getEnabled()));
            }

            // 根据备注查询
            if (tSysDemoDto != null && StringUtils.isNotBlank(tSysDemoDto.getRemark())) {
                predicates.add(cb.like(root.get("remark"), "%" + tSysDemoDto.getRemark() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 验证样例数据
     * @param tSysDemo 样例对象
     */
    private void validateTSysDemo(TSysDemo tSysDemo) {
        if (tSysDemo == null) {
            throw new RuntimeException("样例对象不能为空");
        }

        if (StringUtils.isBlank(tSysDemo.getDemoName())) {
            throw new RuntimeException("样例名称不能为空");
        }

        if (StringUtils.isBlank(tSysDemo.getDemoNumber())) {
            throw new RuntimeException("样例编码不能为空");
        }

        // 检查样例编码是否重复（新增时）
        if (tSysDemo.getDemoId() == null) {
            if (tSysDemoRepository.existsByDemoNumber(tSysDemo.getDemoNumber())) {
                throw new RuntimeException("样例编码已存在：" + tSysDemo.getDemoNumber());
            }
        } else {
            // 更新时检查编码是否与其他样例重复
            Optional<TSysDemo> existingDemo = tSysDemoRepository.findByDemoNumberAndDemoIdNot(tSysDemo.getDemoNumber(), tSysDemo.getDemoId());
            if (existingDemo.isPresent()) {
                throw new RuntimeException("样例编码已存在：" + tSysDemo.getDemoNumber());
            }
        }
    }
}
