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

-- 2022-5-12
INSERT INTO public."t_sys_role"
(role_id, role_name, role_code, role_explain, created_name, enabled, created_time)
VALUES(99999, '厂长', 'JSBM-99999', '默认角色', '', '0', '2022-05-12');

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
