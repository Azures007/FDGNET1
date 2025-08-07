package org.thingsboard.server.dao.mes.role;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.mes.sys.TSysRole;
import org.thingsboard.server.common.data.mes.sys.TSysRoleUser;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.ListRoleDto;
import org.thingsboard.server.dao.sql.mes.order.OrderPPBomRepository;
import org.thingsboard.server.dao.sql.mes.role.RoleRepository;
import org.thingsboard.server.dao.sql.mes.role.RoleUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.role
 * @date 2022/4/7 10:09
 * @Description:
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleUserRepository roleUserRepository;

    @Override
    public Page<TSysRole> listRole(Integer current, Integer size, ListRoleDto listRoleDto) {
        listRoleDto.setEnabled(StringUtils.isBlank(listRoleDto.getEnabled()) ? null : listRoleDto.getEnabled());
        List<Sort.Order> orders=new ArrayList<>();
        Sort.Order order1=new Sort.Order(Sort.Direction.DESC,"createdTime");
        Sort.Order order2=new Sort.Order(Sort.Direction.DESC,"roleId");
        orders.add(order1);
        orders.add(order2);
        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(current, size, sort);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("roleName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("roleCode", ExampleMatcher.GenericPropertyMatchers.contains());
        TSysRole tSysRole = new TSysRole();
        BeanUtils.copyProperties(listRoleDto, tSysRole);
        Example<TSysRole> example = Example.of(tSysRole, matcher);
        Page<TSysRole> tSysRolePage = roleRepository.findAll(example, pageable);
        return tSysRolePage;
    }

    @Override
    public void saveRole(TSysRole role) {
        saveRoleVerify(role);
        roleRepository.saveAndFlush(role);
    }

    @Override
    public void deleteRole(Integer roleId) {
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new RuntimeException("删除的角色不存在");
        }
        roleRepository.deleteById(roleId);
    }

    @Override
    public void saveRoleUser(TSysRoleUser tSysRoleUser) {
        roleUserRepository.saveAndFlush(tSysRoleUser);
    }

    @Override
    public TSysRole getById(Integer roleId) {
        TSysRole one = roleRepository.getByRoleId(roleId);
        return one;
    }

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Override
    public void test() {
        List<TBusOrderPPBom> all = orderPPBomRepository.findAll();
        String tBusOrderPPBom1;
        for (TBusOrderPPBom tBusOrderPPBom : all) {
            tBusOrderPPBom1=tBusOrderPPBom.getMaterialName();
            tBusOrderPPBom.setMaterialName(tBusOrderPPBom.getMaterialNumber());
            tBusOrderPPBom.setMaterialNumber(tBusOrderPPBom1);
            orderPPBomRepository.saveAndFlush(tBusOrderPPBom);
        }

    }

    @Override
    public TSysRole getByUserId(String useId) {
        return roleRepository.getByUserId(useId);
    }


    //保存角色数据有效性验证
    private void saveRoleVerify(TSysRole role) {
        if (role.getRoleId() == null) {
            //新增
            if(StringUtils.isBlank(role.getRoleCode())){
                role.setRoleCode(GlobalConstant.getRoleCode(false));
            }
            if (StringUtils.isBlank(role.getRoleName())) {
                throw new RuntimeException("角色名称不能为空");
            } else {
                TSysRole tSysRoleVerifyName = new TSysRole();
                tSysRoleVerifyName.setRoleName(role.getRoleName());
                Example<TSysRole> example = Example.of(tSysRoleVerifyName);
                Optional<TSysRole> one = roleRepository.findOne(example);
                if (one.isPresent()) {
                    throw new RuntimeException("已存在的角色名称，请重新设置！");
                }
            }
            while (true){
                if (StringUtils.isNoneBlank(role.getRoleCode())) {
                    TSysRole tSysRoleVerifyCode = new TSysRole();
                    tSysRoleVerifyCode.setRoleCode(role.getRoleCode());
                    Example<TSysRole> example = Example.of(tSysRoleVerifyCode);
                    if (roleRepository.findOne(example).isPresent()) {
                       role.setRoleCode(GlobalConstant.getRoleCode(true));
                    }else {
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(role.getByFactory())) {
                role.setByFactory("0");
            }
            if (StringUtils.isBlank(role.getByGroup())) {
                role.setByGroup("0");
            }
            if(StringUtils.isBlank(role.getEnabled())){
                role.setEnabled(GlobalConstant.enableTrue);
            }
            role.setCreatedTime(role.getUpdatedTime());
            role.setCreatedName(role.getUpdatedName());
        } else {
            //修改
        }
    }
}
