-- mes权限部分
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(1, '顶级菜单1', '123', '/path', '0', 0, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(2, '顶级菜单2', '123', '/path2', '0', 0, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(3, '顶级菜单3', '123', '/path3', '0', 0, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(4, '顶级菜单4', '123', '/path4', '0', 0, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(5, '子集菜单4', '123', '/path/path1', '0', 1, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(6, '子集菜单1', '123', '/path/path2', '0', 1, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(7, '子集菜单3', '123', '/path/path3', '0', 1, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(8, '子集菜单4', '123', '/path/path12', '0', 7, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(9, '子集菜单5', '123', '/path/path122', '0', 8, '0', NULL, NULL, NULL, NULL, NULL);
-- INSERT INTO public.t_sys_menu
-- (menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
-- VALUES(10, '子集菜单8', '123', '/path/path1122', '0', 5, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(11, '首页', 'home', '/home', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(13, '账号管理', 'domain', '/account', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(14, '字典管理', 'domain', '/dictionary', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(15, '设备管理', 'domain', '/mydevice', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(16, '人员管理', 'domain', '/staff', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(17, '班别管理', 'domain', '/class', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(18, '规则链库', 'settings_ethernet', '/ruleChains', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(19, '订单管理', 'bookmarks', '/order', '0', 0, '0', NULL, NULL, NULL, NULL, 'toggle');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(20, '工艺路线管理', 'bookmarks', '/technological', '0', 0, '0', NULL, NULL, NULL, NULL, 'toggle');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(21, '资产', 'domain', '/assets', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(22, '设备', 'devices_other', '/devices', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(23, '设备配置', 'mdi:alpha-d-box', '/deviceProfiles', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(24, 'OTA升级', 'memory', '/otaUpdates', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(25, '实体视图', 'view_quilt', '/entityViews', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(26, 'Edge instances', 'router', '/edgeInstances', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(27, 'Edge management', 'settings_input_antenna', '/edgeManagement', '0', 0, '0', NULL, NULL, NULL, NULL, 'toggle');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(28, '部件库', 'now_widgets', '/widgets-bundles', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(29, '仪表板库', 'dashboards', '/dashboards', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(30, '审计日志', 'domain', '/assets', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(31, 'Api 使用统计', 'insert_chart', '/usage', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(32, '系统设置', 'settings', '/settings', '0', 0, '0', NULL, NULL, NULL, NULL, 'toggle');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(33, '订单列表', 'bookmarks', '/order/list', '0', 19, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(34, '工艺路线', 'bookmarks', '/technological/processRoute', '0', 20, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(35, '工序管理', 'bookmarks', '/technological/processManage', '0', 20, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(36, 'Rule Chain templates', 'settings_ethernet', '/edgeManagement/ruleChains', '0', 27, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(37, '首页设置', 'settings_applications', '/settings/home', '0', 32, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(38, '资源库', 'folder', '/settings/resources-library', '0', 32, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(12, '角色管理', 'domain', '/role', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(39, '物料管理', 'domain', '/material', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(41, '编辑', '', '/role/edit', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(42, '禁用', '', '/role/disable', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(43, '启用', '', '/role/enable', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(44, '查看', '', '/role/detail', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(45, '删除', '', '/role/delete', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(46, '菜单配置', '', '/role/menu', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(47, '数据权限', '', '/role/data', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(48, '新增账号', '', '/account/add', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(49, '编辑', '', '/account/edit', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(50, '禁用', '', '/account/disable', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(51, '启用', '', '/account/enable', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(52, '查看', '', '/account/detail', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(53, '删除', '', '/account/del', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(54, '重置密码', '', '/account/reset', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(55, '角色配置', '', '/account/setrole', '1', 13, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(56, '新增设备', '', '/mydevice/add', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(57, '编辑', '', '/mydevice/edit', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(58, '禁用', '', '/mydevice/disable', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(59, '启用', '', '/mydevice/enable', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(60, '查看', '', '/mydevice/detail', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(61, '删除', '', '/mydevice/del', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(62, '导入', '', '/mydevice/up', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(63, '导出', '', '/mydevice/down', '1', 15, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(64, '新增人员', '', '/staff/add', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(65, '编辑', '', '/staff/edit', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(66, '禁用', '', '/staff/disable', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(67, '启用', '', '/staff/enable', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(68, '查看', '', '/staff/detail', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(69, '删除', '', '/staff/del', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(70, '导入', '', '/staff/up', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(71, '导出', '', '/staff/down', '1', 16, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(72, '新增班别', '', '/class/add', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(73, '编辑', '', '/class/edit', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(74, '禁用', '', '/class/disable', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(75, '启用', '', '/class/enable', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(76, '查看', '', '/class/detail', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(77, '删除', '', '/class/del', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(78, '组员管理', '', '/class/member', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(79, '接单开工', '', '/order/list/start', '1', 33, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(80, '导出', '', '/order/list/up', '1', 33, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(81, '新增工艺路线', '', '/technological/processRoute/add', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(82, '产品绑定', '', '/technological/processRoute/bind', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(83, '禁用', '', '/technological/processRoute/disable', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(84, '启用', '', '/technological/processRoute/enable', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(85, '编辑', '', '/technological/processRoute/edit', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(86, '删除', '', '/technological/processRoute/del', '1', 34, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(87, '新增工序', '', '/technological/processManage/add', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(88, '工序设置', '', '/technological/processManage/set', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(89, '禁用', '', '/technological/processManage/disable', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(90, '启用', '', '/technological/processManage/enable', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(91, '编辑', '', '/technological/processManage/edit', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(92, '删除', '', '/technological/processManage/del', '1', 35, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(93, '新增物料', '', '/material/add', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(94, '同步', '', '/material/sync', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(40, '新增角色', '', '/role/add', '1', 12, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(95, '禁用', '', '/material/disable', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(96, '启用', '', '/material/enable', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(97, '编辑', '', '/material/edit', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(98, '删除', '', '/material/del', '1', 39, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(99, '料筐管理', 'domain', '/basketManage', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(100, '新增料框', '', '/basketManage/add', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(101, '导入料框', '', '/basketManage/up', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(102, '禁用', '', '/basketManage/disable', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(103, '启用', '', '/basketManage/enable', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(104, '编辑', '', '/basketManage/edit', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(105, '删除', '', '/basketManage/del', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(106, '导出二维码', '', '/basketManage/down', '1', 99, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(107, '新增字典', '', '/dictionary/add', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(108, '禁用', '', '/dictionary/disable', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(109, '启用', '', '/dictionary/enable', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(110, '编辑', '', '/dictionary/edit', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(111, '删除', '', '/dictionary/del', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(112, '查看', '', '/dictionary/detail', '1', 14, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(113, '导出', '', '/class/down', '1', 17, '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(114, '批量接单', '', '/order/list/moreStart', '1', 33, '0', NULL, NULL, NULL, NULL, NULL);

-- 2022/9/28
--app父级菜单
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(10000, 'APP菜单', 'app菜单部分', '/appMenu', '0', 0, '0', NULL, NULL, NULL, NULL, NULL);

--任务列表部分
INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(11111, '今日任务', 'app菜单部分', '/appMenu/getTodayTask', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(111110, '查看详情', 'app菜单部分', '/appMenu/getTodayTask/show', '1', 11111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(12111, '待生产任务', 'app菜单部分', '/appMenu/getWaitTaskList', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(121110, '查看详情', 'app菜单部分', '/appMenu/getWaitTaskList/show', '1', 12111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(121111, '接单开工', 'app菜单部分', '/appMenu/getWaitTaskList/orderToStart', '1', 12111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(13111, '生产中任务', 'app菜单部分', '/appMenu/getStartTaskList', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131115, '查看详情', 'app菜单部分', '/appMenu/getStartTaskList/show', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131110, '工序报工', 'app菜单部分', '/appMenu/getStartTaskList/processBG', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131111, '工序盘点', 'app菜单部分', '/appMenu/getStartTaskList/processPD', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131112, '工序暂停', 'app菜单部分', '/appMenu/getStartTaskList/processZT', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131113, '工序恢复', 'app菜单部分', '/appMenu/getStartTaskList/processHF', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(131114, '工序结束', 'app菜单部分', '/appMenu/getStartTaskList/processJS', '1', 13111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(14111, '未生产任务', 'app菜单部分', '/appMenu/getUnStartTaskList', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(141110, '查看详情', 'app菜单部分', '/appMenu/getUnStartTaskList/show', '1', 14111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(15111, '已完工任务', 'app菜单部分', '/appMenu/getFinishTaskList', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(151110, '查看详情', 'app菜单部分', '/appMenu/getFinishTaskList/show', '1', 15111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(151111, '工序撤回', 'app菜单部分', '/appMenu/getFinishTaskList/reloadProcess', '1', 15111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(151112, '工序盘点', 'app菜单部分', '/appMenu/getFinishTaskList/processPD', '1', 15111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(16111, '移交待生产任务列表', 'app菜单部分', '/appMenu/getWaithandOverVerify', '1', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(161110, '查看详情', 'app菜单部分', '/appMenu/getWaithandOverVerify/show', '1', 16111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(161111, '接受移交', 'app菜单部分', '/appMenu/getWaithandOverVerify/getYJ', '1', 16111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(161112, '驳回移交', 'app菜单部分', '/appMenu/getWaithandOverVerify/pushYJ', '1', 16111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(17111, '移交生产中任务列表', 'app菜单部分', '/appMenu/gethandOverTask', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171110, '查看详情', 'app菜单部分', '/appMenu/gethandOverTask/show', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171111, '工序报工', 'app菜单部分', '/appMenu/gethandOverTask/processBG', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171113, '工序结束', 'app菜单部分', '/appMenu/gethandOverTask/processJS', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171112, '工序盘点', 'app菜单部分', '/appMenu/gethandOverTask/processPD', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171114, '工序暂停', 'app菜单部分', '/appMenu/gethandOverTask/processZT', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(171115, '工序恢复', 'app菜单部分', '/appMenu/gethandOverTask/processHF', '1', 17111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(18111, '明日订单任务', 'app菜单部分', '/appMenu/getNextDayTaskList', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(181110, '查看详情', 'app菜单部分', '/appMenu/getNextDayTaskList/show', '1', 18111, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(19000, '消息中心', 'app菜单部分', '/appMenu/message', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(191111, '消息列表--订单详情', 'app菜单部分', '/appMenu/message/show', '0', 19000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(191112, '消息列表--订单列表', 'app菜单部分', '/appMenu/message/listMessage', '0', 19000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(20000, '工序报工', 'app菜单部分', '/appMenu/processBG', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200000, '标识单打印', 'app菜单部分', '/appMenu/processBG/workPrint', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200001, '自定义报工', 'app菜单部分', '/appMenu/processBG/optionalBG', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200002, '报工记录', 'app菜单部分', '/appMenu/processBG/listBG', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200003, '报工记录删除', 'app菜单部分', '/appMenu/processBG/deleteListBG', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200004, '工序结束', 'app菜单部分', '/appMenu/processBG/processJS', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200005, '提交', 'app菜单部分', '/appMenu/processBG/submit', '1', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200006, '原辅料投入', 'app菜单部分', '/appMenu/processBG/rawMaterialImport', '0', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200007, 'AB料投入', 'app菜单部分', '/appMenu/processBG/ABImport', '0', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200008, 'AB料产出', 'app菜单部分', '/appMenu/processBG/ABExport', '0', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(200009, '合格品产出', 'app菜单部分', '/appMenu/processBG/qualifiedExport', '0', 20000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(21000, '工序盘点', 'app菜单部分', '/appMenu/processPD', '0', 10000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210000, '移交', 'app菜单部分', '/appMenu/processPD/handover', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210001, '移交记录', 'app菜单部分', '/appMenu/processPD/listHandover', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210002, '盘点记录', 'app菜单部分', '/appMenu/processPD/listPD', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210003, '盘点记录重新盘点', 'app菜单部分', '/appMenu/processPD/listPDReload', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210004, '盘点记录删除', 'app菜单部分', '/appMenu/processPD/deleteListPD', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(210005, '提交', 'app菜单部分', '/appMenu/processPD/submit', '1', 21000, '0', NULL, NULL, NULL, NULL, NULL);

-- 2022-12-7
INSERT INTO public.t_sys_menu
( menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type,skip_url)
VALUES('MES订单投入产出汇总表(一级)', 'domain', '/appMenu/dataMsa/mesSum1', '2', 22000, '0', NULL, NULL, NULL, NULL, 'link','http://59.61.227.161:8081/decision/view/report?viewlet=%252Flc%252F%25E4%25BF%25A1%25E6%2581%25AF%25E9%2583%25A8%252FMES%25E5%25B7%25A5%25E5%25BA%258F%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E7%25B3%25BB%25E5%2588%2597%25E6%258A%25A5%25E8%25A1%25A8%252FMES%25E8%25AE%25A2%25E5%258D%2595%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E6%25B1%2587%25E6%2580%25BB%25E8%25A1%25A8%2528%25E4%25B8%2580%25E7%25BA%25A7%2529.cpt&op=view&__parameters__=%257B%2522__pi__%2522%253Atrue%257D&_=1670382187810');
INSERT INTO public.t_sys_menu
( menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type,skip_url)
VALUES('MES订单投入产出明细表(二级)', 'domain', '/appMenu/dataMsa/mesPlan2', '2', 22000, '0', NULL, NULL, NULL, NULL, 'link','http://59.61.227.161:8081/decision/view/report?viewlet=%252Flc%252F%25E4%25BF%25A1%25E6%2581%25AF%25E9%2583%25A8%252FMES%25E5%25B7%25A5%25E5%25BA%258F%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E7%25B3%25BB%25E5%2588%2597%25E6%258A%25A5%25E8%25A1%25A8%252FMES%25E8%25AE%25A2%25E5%258D%2595%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E6%2598%258E%25E7%25BB%2586%25E8%25A1%25A8%2528%25E4%25BA%258C%25E7%25BA%25A7%2529.cpt&op=view&__parameters__=%257B%2522__pi__%2522%253Atrue%257D&_=1670382666284');
INSERT INTO public.t_sys_menu
( menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type,skip_url)
VALUES('MES订单AB料投入产出明细表(二级)', 'domain', '/appMenu/dataMsa/mesABPlan2', '2', 22000, '0', NULL, NULL, NULL, NULL, 'link','http://59.61.227.161:8081/decision/view/report?viewlet=%252Flc%252F%25E4%25BF%25A1%25E6%2581%25AF%25E9%2583%25A8%252FMES%25E5%25B7%25A5%25E5%25BA%258F%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E7%25B3%25BB%25E5%2588%2597%25E6%258A%25A5%25E8%25A1%25A8%252FMES%25E8%25AE%25A2%25E5%258D%2595AB%25E6%2596%2599%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E6%2598%258E%25E7%25BB%2586%25E8%25A1%25A8%2528%25E4%25BA%258C%25E7%25BA%25A7%2529.cpt&op=view&__parameters__=%257B%2522__pi__%2522%253Atrue%257D&_=1670382671880');
INSERT INTO public.t_sys_menu
( menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type,skip_url)
VALUES('人员投入产出报表', 'domain', '/appMenu/dataMsa/userexport', '2', 22000, '0', NULL, NULL, NULL, NULL, 'link','http://59.61.227.161:8081/decision/view/report?viewlet=%252Flc%252F%25E4%25BF%25A1%25E6%2581%25AF%25E9%2583%25A8%252FMES%25E5%25B7%25A5%25E5%25BA%258F%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E7%25B3%25BB%25E5%2588%2597%25E6%258A%25A5%25E8%25A1%25A8%252F%25E4%25BA%25BA%25E5%2591%2598%25E6%258A%2595%25E5%2585%25A5%25E4%25BA%25A7%25E5%2587%25BA%25E6%258A%25A5%25E8%25A1%25A8.cpt&op=view&__parameters__=%257B%2522__pi__%2522%253Atrue%257D&_=1670382687096');

INSERT INTO public.t_sys_menu
(menu_id, menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES(4020, '磨具管理', 'domain', '/mySpec', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');



