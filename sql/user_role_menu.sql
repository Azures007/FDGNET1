--  用户 角色 菜单相关接口
DROP TABLE IF EXISTS t_sys_role;
CREATE TABLE t_sys_role(
    role_id SERIAL NOT NULL,
    role_name VARCHAR(20) NOT NULL,
    role_code VARCHAR(50),
    role_explain VARCHAR(255),
    enabled VARCHAR(1) NOT NULL DEFAULT  0,
    created_time DATE,
    created_name VARCHAR(20),
    updated_time DATE,
    updated_name VARCHAR(20),
    PRIMARY KEY (role_id)
);

COMMENT ON TABLE t_sys_role IS '角色表';
COMMENT ON COLUMN t_sys_role.role_id IS 'id';
COMMENT ON COLUMN t_sys_role.role_name IS '角色名';
COMMENT ON COLUMN t_sys_role.role_code IS '角色代码';
COMMENT ON COLUMN t_sys_role.role_explain IS '角色说明';
COMMENT ON COLUMN t_sys_role.enabled IS '是否可用;0：可用 1：不可用';
COMMENT ON COLUMN t_sys_role.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_role.created_name IS '创建人';
COMMENT ON COLUMN t_sys_role.updated_time IS '修改时间';
COMMENT ON COLUMN t_sys_role.updated_name IS '修改人';

DROP TABLE IF EXISTS t_sys_menu;
CREATE TABLE t_sys_menu(
    menu_id SERIAL NOT NULL,
    menu_name VARCHAR(50) NOT NULL,
    menu_explain VARCHAR(255),
    path VARCHAR(50) NOT NULL,
    flag VARCHAR(1) DEFAULT  0,
    parent_id INTEGER NOT NULL DEFAULT  0,
    enabled VARCHAR(1) NOT NULL DEFAULT  0,
    created_time DATE,
    created_name VARCHAR(50),
    updated_time DATE,
    updated_name VARCHAR(50),
    PRIMARY KEY (menu_id)
);

COMMENT ON TABLE t_sys_menu IS '菜单表';
COMMENT ON COLUMN t_sys_menu.menu_id IS 'id';
COMMENT ON COLUMN t_sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN t_sys_menu.menu_explain IS '菜单说明';
COMMENT ON COLUMN t_sys_menu.path IS '菜单路径';
COMMENT ON COLUMN t_sys_menu.flag IS '标识 0:菜单 1:功能按钮;0:菜单 1:功能按钮';
COMMENT ON COLUMN t_sys_menu.parent_id IS '父级菜单id;根菜单为0';
COMMENT ON COLUMN t_sys_menu.enabled IS '是否可用;0：可用 1：不可用';
COMMENT ON COLUMN t_sys_menu.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_menu.created_name IS '创建人';
COMMENT ON COLUMN t_sys_menu.updated_time IS '修改时间';
COMMENT ON COLUMN t_sys_menu.updated_name IS '修改人';

DROP TABLE IF EXISTS t_sys_role_menu;
CREATE TABLE t_sys_role_menu(
    role_menu_id SERIAL NOT NULL,
    role_id INTEGER NOT NULL,
    menu_id INTEGER NOT NULL,
    enabled VARCHAR(1) NOT NULL DEFAULT  0,
    created_name VARCHAR(50),
    created_time DATE,
    updated_name VARCHAR(50),
    updated_time DATE,
    PRIMARY KEY (role_menu_id)
);

COMMENT ON TABLE t_sys_role_menu IS '';
COMMENT ON COLUMN t_sys_role_menu.role_menu_id IS 'id';
COMMENT ON COLUMN t_sys_role_menu.role_id IS '角色id';
COMMENT ON COLUMN t_sys_role_menu.menu_id IS '菜单id';
COMMENT ON COLUMN t_sys_role_menu.enabled IS '是否可用;0：可用 1：不可用';
COMMENT ON COLUMN t_sys_role_menu.created_name IS '创建人';
COMMENT ON COLUMN t_sys_role_menu.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_role_menu.updated_name IS '修改人';
COMMENT ON COLUMN t_sys_role_menu.updated_time IS '修改时间';

DROP TABLE IF EXISTS t_sys_user_menu;
CREATE TABLE t_sys_user_menu(
    user_menu_id VARCHAR(32) NOT NULL,
    user_id VARCHAR(32) NOT NULL,
    menu_id VARCHAR(32) NOT NULL,
    enabled VARCHAR(1) NOT NULL DEFAULT  0,
    created_name VARCHAR(20),
    created_time DATE,
    updated_name VARCHAR(20),
    updated_time DATE,
    PRIMARY KEY (user_menu_id)
);

COMMENT ON TABLE t_sys_user_menu IS '';
COMMENT ON COLUMN t_sys_user_menu.user_menu_id IS 'id';
COMMENT ON COLUMN t_sys_user_menu.user_id IS '用户id';
COMMENT ON COLUMN t_sys_user_menu.menu_id IS '菜单id';
COMMENT ON COLUMN t_sys_user_menu.enabled IS '是否可用;0：可用 1：不可用';
COMMENT ON COLUMN t_sys_user_menu.created_name IS '创建人';
COMMENT ON COLUMN t_sys_user_menu.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_user_menu.updated_name IS '修改人';
COMMENT ON COLUMN t_sys_user_menu.updated_time IS '修改时间';

CREATE TABLE t_sys_role_user(
    role_user_id SERIAL NOT NULL,
    role_id INTEGER,
    user_id VARCHAR(255) NOT NULL,
    created_time DATE,
    created_name VARCHAR(20),
    updated_time DATE,
    updated_name VARCHAR(20),
    PRIMARY KEY (role_user_id)
);

COMMENT ON TABLE t_sys_role_user IS '角色用户关系表';
COMMENT ON COLUMN t_sys_role_user.role_user_id IS 'id';
COMMENT ON COLUMN t_sys_role_user.role_id IS '角色id';
COMMENT ON COLUMN t_sys_role_user.user_id IS '用户id';
COMMENT ON COLUMN t_sys_role_user.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_role_user.created_name IS '创建人';
COMMENT ON COLUMN t_sys_role_user.updated_time IS '修改时间';
COMMENT ON COLUMN t_sys_role_user.updated_name IS '修改人';
ALTER TABLE public.t_sys_role_user ADD user_status varchar(1) NOT NULL DEFAULT 0;
COMMENT ON COLUMN public.t_sys_role_user.user_status IS '用户状态 0:可用 1：不可用';
ALTER TABLE public.t_sys_role_user ADD last_time timestamp NULL;
COMMENT ON COLUMN public.t_sys_role_user.last_time IS '上次登陆时间';

-- 2022/5/11修改
ALTER TABLE public.t_sys_menu ADD menu_type varchar NULL;
COMMENT ON COLUMN public.t_sys_menu.menu_type IS '菜单类型';
INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
VALUES ('首页', 'home', '/home', 0, 0
		, 0, 'link'),
	('角色管理', 'domain', '/role', 0, 0
		, 0, 'link'),
	('账号管理', 'domain', '/account', 0, 0
		, 0, 'link'),
	('字典管理', 'domain', '/dictionary', 0, 0
		, 0, 'link'),
	('设备管理', 'domain', '/mydevice', 0, 0
		, 0, 'link'),
	('人员管理', 'domain', '/staff', 0, 0
		, 0, 'link'),
	('班别管理', 'domain', '/class', 0, 0
		, 0, 'link'),
	('规则链库', 'settings_ethernet', '/ruleChains', 0, 0
		, 0, 'link'),
	('订单管理', 'bookmarks', '/order', 0, 0
		, 0, 'toggle'),
	('工艺路线管理', 'bookmarks', '/technological', 0, 0
		, 0, 'toggle'),
	('资产', 'domain', '/assets', 0, 0
		, 0, 'link'),
	('设备', 'devices_other', '/devices', 0, 0
		, 0, 'link'),
	('设备配置', 'mdi:alpha-d-box', '/deviceProfiles', 0, 0
		, 0, 'link'),
	('OTA升级', 'memory', '/otaUpdates', 0, 0
		, 0, 'link'),
	('实体视图', 'view_quilt', '/entityViews', 0, 0
		, 0, 'link'),
	('Edge instances', 'router', '/edgeInstances', 0, 0
		, 0, 'link'),
	('Edge management', 'settings_input_antenna', '/edgeManagement', 0, 0
		, 0, 'toggle'),
	('部件库', 'now_widgets', '/widgets-bundles', 0, 0
		, 0, 'link'),
	('仪表板库', 'dashboards', '/dashboards', 0, 0
		, 0, 'link'),
	('审计日志', 'domain', '/assets', 0, 0
		, 0, 'link'),
	('Api 使用统计', 'insert_chart', '/usage', 0, 0
		, 0, 'link'),
	('系统设置', 'settings', '/settings', 0, 0
		, 0, 'toggle');

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
	values('物料','material','/material','0','0','0','link')

	INSERT INTO public.t_sys_menu
( menu_name, menu_explain, "path", flag, parent_id, enabled, created_time, created_name, updated_time, updated_name, menu_type)
VALUES( '料筐管理', 'domain', '/basketManage', '0', 0, '0', NULL, NULL, NULL, NULL, 'link');

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select '订单列表' as menu_name ,'bookmarks' as menu_explain,'/order/list' as "path",0 as flag,(select menu_id from t_sys_menu where path='/order') as parent_id,0 as enabled,'link' as menu_type

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select '工艺路线' as menu_name ,'bookmarks' as menu_explain,'/technological/processRoute' as "path",0 as flag,(select menu_id from t_sys_menu where path='/technological') as parent_id,0 as enabled,'link' as menu_type

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select '工序管理' as menu_name ,'bookmarks' as menu_explain,'/technological/processManage' as "path",0 as flag,(select menu_id from t_sys_menu where path='/technological') as parent_id,0 as enabled,'link' as menu_type

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select 'Rule Chain templates' as menu_name ,'settings_ethernet' as menu_explain,'/edgeManagement/ruleChains' as "path",0 as flag,(select menu_id from t_sys_menu where path='/edgeManagement') as parent_id,0 as enabled,'link' as menu_type

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select '首页设置' as menu_name ,'settings_applications' as menu_explain,'/settings/home' as "path",0 as flag,(select menu_id from t_sys_menu where path='/settings') as parent_id,0 as enabled,'link' as menu_type

INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
	, enabled, menu_type)
select '资源库' as menu_name ,'folder' as menu_explain,'/settings/resources-library' as "path",0 as flag,(select menu_id from t_sys_menu where path='/settings') as parent_id,0 as enabled,'link' as menu_type

-- 2022-5-12
INSERT INTO public."t_sys_role"
(role_id, role_name, role_code, role_explain, created_name, enabled, created_time)
VALUES(99999, '厂长', 'JSBM-99999', '默认角色', '', '0', '2022-05-12');

-- 2022-07-05

-- 角色按钮部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增角色' as menu_name ,'' as menu_explain ,'/role/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/role/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/role/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/role/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/role/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/role/delete' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '菜单配置' as menu_name ,'' as menu_explain ,'/role/menu' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '数据权限' as menu_name ,'' as menu_explain ,'/role/data' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/role'	;

-- 账号按钮部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增账号' as menu_name ,'' as menu_explain ,'/account/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/account/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/account/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/account/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/account/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/account/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '重置密码' as menu_name ,'' as menu_explain ,'/account/reset' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '角色配置' as menu_name ,'' as menu_explain ,'/account/setrole' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/account'	;

-- 设备管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增设备' as menu_name ,'' as menu_explain ,'/mydevice/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/mydevice/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/mydevice/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/mydevice/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/mydevice/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/mydevice/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导入' as menu_name ,'' as menu_explain ,'/mydevice/up' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导出' as menu_name ,'' as menu_explain ,'/mydevice/down' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/mydevice'	;

-- 人员管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增人员' as menu_name ,'' as menu_explain ,'/staff/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/staff/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/staff/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/staff/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/staff/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/staff/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导入' as menu_name ,'' as menu_explain ,'/staff/up' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导出' as menu_name ,'' as menu_explain ,'/staff/down' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/staff'	;

-- 班别管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增班别' as menu_name ,'' as menu_explain ,'/class/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/class/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/class/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/class/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/class/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/class/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '组员管理' as menu_name ,'' as menu_explain ,'/class/member' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导出' as menu_name ,'' as menu_explain ,'/class/down' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/class';

-- 订单管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '接单开工' as menu_name ,'' as menu_explain ,'/order/list/start' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/order/list';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导出' as menu_name ,'' as menu_explain ,'/order/list/up' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/order/list'	;
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '批量接单' as menu_name ,'' as menu_explain ,'/order/list/moreStart' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/order/list'	;

-- 工艺路线部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增工艺路线' as menu_name ,'' as menu_explain ,'/technological/processRoute/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '产品绑定' as menu_name ,'' as menu_explain ,'/technological/processRoute/bind' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/technological/processRoute/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/technological/processRoute/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/technological/processRoute/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/technological/processRoute/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processRoute';

-- 工序管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增工序' as menu_name ,'' as menu_explain ,'/technological/processManage/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '工序设置' as menu_name ,'' as menu_explain ,'/technological/processManage/set' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/technological/processManage/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/technological/processManage/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/technological/processManage/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/technological/processManage/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/technological/processManage';

-- 物料管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增物料' as menu_name ,'' as menu_explain ,'/material/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '同步' as menu_name ,'' as menu_explain ,'/material/sync' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/material/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/material/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/material/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/material/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/material';

-- 料筐管理部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增料框' as menu_name ,'' as menu_explain ,'/basketManage/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导入料框' as menu_name ,'' as menu_explain ,'/basketManage/up' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/basketManage/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/basketManage/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/basketManage/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/basketManage/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '导出二维码' as menu_name ,'' as menu_explain ,'/basketManage/down' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/basketManage';

-- 字典模块部分
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '新增字典' as menu_name ,'' as menu_explain ,'/dictionary/add' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '禁用' as menu_name ,'' as menu_explain ,'/dictionary/disable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '启用' as menu_name ,'' as menu_explain ,'/dictionary/enable' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '编辑' as menu_name ,'' as menu_explain ,'/dictionary/edit' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '删除' as menu_name ,'' as menu_explain ,'/dictionary/del' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';
insert into t_sys_menu (menu_name,menu_explain,path,flag,parent_id,enabled)
	select '查看' as menu_name ,'' as menu_explain ,'/dictionary/detail' as path,1 as  flag ,menu_id ,0 enabled from t_sys_menu where "path" ='/dictionary';

-- 2022/9/21
CREATE TABLE t_sys_user_devices(
    id SERIAL NOT NULL,
    user_id VARCHAR(255),
    devices_key text,
    devices_type VARCHAR(1),
    enabled VARCHAR(1),
    created_time timestamp(6),
    created_name VARCHAR(255),
    updated_time timestamp(6),
    update_name VARCHAR(255),
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_sys_user_devices IS '用户设备信息表';
COMMENT ON COLUMN t_sys_user_devices.id IS 'id';
COMMENT ON COLUMN t_sys_user_devices.user_id IS '用户id';
COMMENT ON COLUMN t_sys_user_devices.devices_key IS '设备key';
COMMENT ON COLUMN t_sys_user_devices.devices_type IS '设备类型 1：指纹';
COMMENT ON COLUMN t_sys_user_devices.enabled IS '是否可用 0：不可用 1：可用';
COMMENT ON COLUMN t_sys_user_devices.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_user_devices.created_name IS '创建人';
COMMENT ON COLUMN t_sys_user_devices.updated_time IS '更新时间';
COMMENT ON COLUMN t_sys_user_devices.update_name IS '更新人';
ALTER TABLE public.t_sys_user_devices ADD personnel_id int NULL;
COMMENT ON COLUMN public.t_sys_user_devices.personnel_id IS '人员id';
