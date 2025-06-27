package org.thingsboard.server.dao.menu;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.SetRoleMenuDto;
import org.thingsboard.server.dao.sql.menu.MenuRepository;
import org.thingsboard.server.dao.sql.menu.MenuRoleRepository;
import org.thingsboard.server.dao.sql.role.RoleUserRepository;
import org.thingsboard.server.dao.vo.MenuListVo;
import org.thingsboard.server.dao.vo.PageVo;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.menu
 * @date 2022/4/11 17:03
 * @Description:
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuRoleRepository menuRoleRepository;

    @Autowired
    RoleUserRepository roleUserRepository;

    @Override
    public void update(TSysMenu tSysMenu) {
        if (tSysMenu.getMenuId() == null) {
            //新增
            tSysMenu.setCreatedName(tSysMenu.getUpdatedName());
            tSysMenu.setCreatedTime(tSysMenu.getUpdatedTime());
            if (tSysMenu.getParentId() == null) {
                tSysMenu.setParentId(0);
            }
            if (StringUtils.isBlank(tSysMenu.getEnabled())) {
                tSysMenu.setEnabled(GlobalConstant.enableTrue);
            }
            if (StringUtils.isBlank(tSysMenu.getFlag())) {
                tSysMenu.setFlag("0");
            }
        }
        menuRepository.saveAndFlush(tSysMenu);
    }

    @Override
    public MenuListVo list(int isTree, Integer roleId) {
        MenuListVo menuListVo = new MenuListVo();
        List<Sort.Order> orders=new ArrayList<>();
        Sort.Order sort1=new Sort.Order(Sort.Direction.ASC,"sort");
        Sort.Order sort2=new Sort.Order(Sort.Direction.ASC,"menuId");
        orders.add(sort1);
        orders.add(sort2);
        Sort sort = Sort.by(orders);
        List<TSysMenu> menuList = menuRepository.findAll(sort);
        if (isTree == 0) {
        } else {
            List<TSysMenu> tSysMenus = menuRepository.listMenuByRoleId(roleId);
            if (menuList.size() == tSysMenus.size()) {
                menuListVo.setIsFlag("0");
                menuListVo.setSelect(true);
            }
            for (TSysMenu tSysMenu : menuList) {
                tSysMenu.setIsFlag("1");
                tSysMenu.setSelect(false);
                for (TSysMenu sysMenu : tSysMenus) {
                    if (tSysMenu.getMenuId().intValue() == sysMenu.getMenuId().intValue()) {
                        tSysMenu.setIsFlag("0");
                        tSysMenu.setSelect(true);
                        break;
                    }
                }
            }
            //树型显示
            // 存放顶级菜单节点
            List<TSysMenu> tSysMenusByRoot = new ArrayList<>();
            for (TSysMenu tSysMenu : menuList) {
                if (tSysMenu.getParentId() == 0) {
                    tSysMenusByRoot.add(tSysMenu);
                }
            }
            //将集合转成树
            menuList = listToTree(menuList, tSysMenusByRoot);
        }
        menuListVo.setTsysMenus(menuList);
        return menuListVo;
    }

    @Override
    public List<Integer> listRoleMenu(Integer roleId) {
        List<Integer> integers = menuRoleRepository.listIdByRoleId(roleId);
        return integers;
    }

    @Transactional
    @Override
    public void setRoleMenu(SetRoleMenuDto setRoleMenuDto) {
        Integer roleId = setRoleMenuDto.getRoleId();
        menuRoleRepository.deleteByRoleId(roleId);
        List<TSysRoleMenu> tSysRoleMenus = new ArrayList<>();
        List<Integer> menuIds = setRoleMenuDto.getMenuIds();
        TSysRoleMenu roleMenu;
        String name = setRoleMenuDto.getCreatedName();
        Date time = setRoleMenuDto.getCreatedTime();
        for (Integer menuId : menuIds) {
            roleMenu = new TSysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenu.setCreatedName(name);
            roleMenu.setUpdatedName(name);
            roleMenu.setEnabled("0");
            roleMenu.setCreatedTime(time);
            roleMenu.setUpdatedTime(time);
            tSysRoleMenus.add(roleMenu);
        }
        menuRoleRepository.saveAll(tSysRoleMenus);
    }

    @Override
    public List<TSysMenu> getMyMenu(String userId) {
        Integer roleId = roleUserRepository.getByUserId(userId).getRoleId();
        if(roleId==null){
            throw new RuntimeException("当前用户未绑定角色，请绑定角色在操作！！！");
        }
        List<TSysMenu> tSysMenus = menuRepository.listMenuByRoleId(roleId);
        //树型显示
        // 存放顶级菜单节点
        List<TSysMenu> tSysMenusByRoot = new ArrayList<>();
        for (TSysMenu tSysMenu : tSysMenus) {
            if (tSysMenu.getParentId() == 0) {
                tSysMenusByRoot.add(tSysMenu);
            }
        }
        //将集合转成树
        tSysMenus = listToTree(tSysMenus, tSysMenusByRoot);
        return tSysMenus;
    }

    @Override
    public List<String> listPath(String userId) {
        TSysRoleUser byUserId = roleUserRepository.getByUserId(userId);
        List<String> strings = menuRepository.listPath(byUserId.getRoleId());
        return strings;
    }

    @Override
    public MenuListVo getMenuListVoByTree() {
        MenuListVo menuListVo = new MenuListVo();
        List<Sort.Order> orders=new ArrayList<>();
        Sort.Order sort1=new Sort.Order(Sort.Direction.ASC,"sort");
        Sort.Order sort2=new Sort.Order(Sort.Direction.ASC,"menuId");
        orders.add(sort1);
        orders.add(sort2);
        Sort sort = Sort.by(orders);
        List<TSysMenu> menuList = menuRepository.findAll(sort);
        //树型显示
        // 存放顶级菜单节点
        List<TSysMenu> tSysMenusByRoot = new ArrayList<>();
        for (TSysMenu tSysMenu : menuList) {
            if (tSysMenu.getParentId() == 0) {
                tSysMenusByRoot.add(tSysMenu);
            }
        }
        //将集合转成树
        menuList = listToTree(menuList, tSysMenusByRoot);
        menuListVo.setTsysMenus(menuList);
        return menuListVo;
    }

    /**
     * 分页菜单列表
     * @param current
     * @param size
     * @return
     */
    @Override
    public PageVo<TSysMenu> pageMenu(Integer current, Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"createdTime");
        PageRequest pageRequest= PageRequest.of(current,size,sort);
        Page<TSysMenu> all = menuRepository.findAll(pageRequest);
        PageVo<TSysMenu> tSysMenuPageVo=new PageVo<>(all);
        return tSysMenuPageVo;
    }

    @Override
    public PageVo<TSysMenu> pageMenu(Integer current, Integer size, TSysMenu tSysMenu) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "menuId"));
        Sort sort1 = Sort.by(orders);
        Pageable pageable = PageRequest.of(current, size, sort1);
        Page<TSysMenu> tSysMenuPage = menuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (!StringUtils.isEmpty(tSysMenu.getMenuName())) {
                predicates.add(criteriaBuilder.like(root.get("menuName"), "%" + tSysMenu.getMenuName() + "%"));
            }
            if (tSysMenu.getParentId() >= 0) {
                predicates.add(criteriaBuilder.equal(root.get("parentId"), tSysMenu.getParentId()));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
        PageVo<TSysMenu> pageVo = new PageVo<>(tSysMenuPage);
        return pageVo;
    }

    @Override
    public List<TSysMenu> listMenuByUser(String userId) {
        TSysRoleUser byUserId = roleUserRepository.getByUserId(userId);
        List<String> flags=new ArrayList<>();
        flags.add("2");
        List<TSysMenu> tSysMenus= menuRepository.listMenuByUser(byUserId.getRoleId(),flags);
        return tSysMenus;
    }

    @Override
    public TSysMenu getById(Integer menuId) {
        return menuRepository.findById(menuId).orElse(new TSysMenu());
    }

    @Override
    public void delete(Integer menuId) {
        menuRepository.deleteById(menuId);
    }

    /**
     * 将集合转成树,深度优先算法
     *
     * @param menuList：所有数据
     * @param tSysMenusByRoot：存放根节点数据
     * @return
     */
    private List<TSysMenu> listToTree(List<TSysMenu> menuList, List<TSysMenu> tSysMenusByRoot) {
        //标记
        boolean[] flagArr = new boolean[menuList.size()];
        for (TSysMenu tSysMenu : tSysMenusByRoot) {
            treeGo(menuList, flagArr, tSysMenu.getMenuId().intValue(), tSysMenu);
        }
        return tSysMenusByRoot;
    }

    //递归
    private void treeGo(List<TSysMenu> menuList, boolean[] flagArr, int currendId, TSysMenu tSysMenu) {
        if (!isChild(menuList, tSysMenu)) {
            //没有子节点，递归结束
            return;
        }
        List<TSysMenu> tSysMenus = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getParentId().intValue() == currendId && !flagArr[i]) {
                //找到子节点
                tSysMenus.add(menuList.get(i));
                flagArr[i] = true;
                treeGo(menuList, flagArr, menuList.get(i).getMenuId().intValue(), menuList.get(i));
            }
        }
        tSysMenu.setTSysMenus(tSysMenus);
    }

    /**
     * 判断是否有子节点
     *
     */
    private boolean isChild(List<TSysMenu> menuList, TSysMenu tSysMenu) {
        for (TSysMenu sysMenu : menuList) {
            if (sysMenu.getParentId().intValue() == tSysMenu.getMenuId().intValue()) {
                return true;
            }
        }
        return false;
    }
}
