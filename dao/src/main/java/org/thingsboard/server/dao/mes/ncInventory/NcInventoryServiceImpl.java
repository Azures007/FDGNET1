package org.thingsboard.server.dao.mes.ncInventory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.mes.vo.PageVo;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NcInventoryServiceImpl implements NcInventoryService {
    @Autowired
    private NcInventoryRepository repository;

    @Autowired
    protected UserService userService;

    @Override
    public void saveOrUpdateBatchByBillId(List<NcInventory> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 直接saveAll，JPA会根据主键billId自动新增或更新
        repository.saveAll(list);
    }
    @Override
    public PageVo<NcInventory> queryInventory(String userId,String warehouseName, String materialName, String spec,Integer current, Integer size) {
        //List<String> cwkids =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(userId,pkOrg);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billId"));
        Sort sort1 = Sort.by(orders);
        Pageable pageable = PageRequest.of(current, size, sort1);
        Page<NcInventory> inventoryPage = repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            //仓库名称
            if (!StringUtils.isEmpty(warehouseName)) {
                predicates.add(criteriaBuilder.like(root.get("warehouseName"), "%" + warehouseName + "%"));
            }
            //物料名称
            if (!StringUtils.isEmpty(materialName)) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + materialName + "%"));
            }
            //规格
            if (!StringUtils.isEmpty(spec)) {
                predicates.add(criteriaBuilder.like(root.get("spec"), "%" + spec + "%"));
            }
            if(ncWarehouses!=null && !ncWarehouses.isEmpty()){
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("warehouseId").in(warehouseIds));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
        PageVo<NcInventory> pageVo = new PageVo<>(inventoryPage);
        return pageVo;
    }
}
