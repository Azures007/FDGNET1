-- 为 t_bus_order_head 表添加新字段
ALTER TABLE t_bus_order_head
    ADD COLUMN nc_cpmohid VARCHAR(255),
ADD COLUMN nc_cmoid VARCHAR(255),
ADD COLUMN nc_cdeptid VARCHAR(255),
ADD COLUMN nc_vwkname VARCHAR(255),
ADD COLUMN nc_cwkid VARCHAR(255),
ADD COLUMN nc_pk_material VARCHAR(255);

COMMENT ON COLUMN t_bus_order_head.nc_cpmohid IS 'NC生产订单id';
COMMENT ON COLUMN t_bus_order_head.nc_cmoid IS 'NC订单明细id';
COMMENT ON COLUMN t_bus_order_head.nc_cdeptid IS 'NC生产部门id';
COMMENT ON COLUMN t_bus_order_head.nc_vwkname IS 'NC生产线';
COMMENT ON COLUMN t_bus_order_head.nc_cwkid IS 'NC生产线id';
COMMENT ON COLUMN t_bus_order_head.nc_pk_material IS 'NC物料id';

ALTER TABLE t_bus_order_ppbom
    ADD COLUMN nc_cmoid VARCHAR(255),
ADD COLUMN nc_pk_material VARCHAR(255);

COMMENT ON COLUMN t_bus_order_ppbom.nc_cmoid IS 'NC订单明细id';
COMMENT ON COLUMN t_bus_order_ppbom.nc_pk_material IS 'NC物料id';

ALTER TABLE "public"."t_bus_order_ppbom"
    ALTER COLUMN "mid_ppbom_entry_is_into" DROP NOT NULL,
ALTER COLUMN "mid_ppbom_entry_item_type" DROP NOT NULL,
  ALTER COLUMN "mid_ppbom_entry_use_rate" DROP NOT NULL;


-- t_sync_material表添加新字段
ALTER TABLE t_sync_material
    ADD COLUMN nc_material_id VARCHAR(50),
ADD COLUMN nc_material_category VARCHAR(100),
ADD COLUMN nc_material_main_category VARCHAR(100),
ADD COLUMN nc_material_classification VARCHAR(100),
ADD COLUMN nc_material_quality_num INTEGER,
ADD COLUMN nc_material_quality_unit VARCHAR(2),
ADD COLUMN nc_material_status VARCHAR(20);

-- 可选：添加字段注释
COMMENT ON COLUMN t_sync_material.nc_material_id IS 'NC物料ID';
COMMENT ON COLUMN t_sync_material.nc_material_category IS '物料分类';
COMMENT ON COLUMN t_sync_material.nc_material_main_category IS '物料大类';
COMMENT ON COLUMN t_sync_material.nc_material_classification IS '材料分类';
COMMENT ON COLUMN t_sync_material.nc_material_quality_num IS '保质期(月)';
COMMENT ON COLUMN t_sync_material.nc_material_quality_unit IS '保质期单位(0=年，1=月，2=日)';
COMMENT ON COLUMN t_sync_material.nc_material_status IS 'NC状态';

-- 创建t_sys_organization基地表
CREATE TABLE t_sys_organization (
                                    id SERIAL PRIMARY KEY,
                                    nc_pk_org VARCHAR(255),
                                    nc_org_name VARCHAR(255)
);
COMMENT ON COLUMN t_sys_organization.nc_pk_org IS 'NC基地id';
COMMENT ON COLUMN t_sys_organization.nc_org_name IS 'NC基地名称';
COMMENT ON TABLE "public"."t_sys_organization" IS '基地';

-- 创建生产线表
CREATE TABLE t_sys_workline (
                                id SERIAL PRIMARY KEY,
                                nc_cwkid VARCHAR(255),
                                nc_vwkname VARCHAR(255),
                                nc_vwkcode VARCHAR(255),
                                nc_pk_org VARCHAR(255),
                                nc_org_name VARCHAR(255),
                                nc_cdeptid VARCHAR(255),
                                nc_cdeptname VARCHAR(255)
);
COMMENT ON TABLE t_sys_workline IS '生产线';
COMMENT ON COLUMN t_sys_workline.nc_cwkid IS '产线id';
COMMENT ON COLUMN t_sys_workline.nc_vwkname IS '产线名称';
COMMENT ON COLUMN t_sys_workline.nc_vwkcode IS '产线编码';
COMMENT ON COLUMN t_sys_workline.nc_pk_org IS '基地id';
COMMENT ON COLUMN t_sys_workline.nc_org_name IS '基地名称';
COMMENT ON COLUMN t_sys_workline.nc_cdeptid IS '部门id';
COMMENT ON COLUMN t_sys_workline.nc_cdeptname IS '部门名称';

-- 创建生产部门表
CREATE TABLE t_sys_department (
                                  id SERIAL PRIMARY KEY,
                                  nc_cdeptid VARCHAR(255),
                                  nc_cdeptname VARCHAR(255),
                                  nc_cdeptcode VARCHAR(255),
                                  nc_dept_parent_code VARCHAR(255),
                                  nc_pk_org VARCHAR(255),
                                  nc_org_name VARCHAR(255)
);
COMMENT ON TABLE t_sys_department IS '生产部门';
COMMENT ON COLUMN t_sys_department.nc_cdeptid IS '部门id';
COMMENT ON COLUMN t_sys_department.nc_cdeptname IS '部门名称';
COMMENT ON COLUMN t_sys_department.nc_cdeptcode IS '部门编码';
COMMENT ON COLUMN t_sys_department.nc_dept_parent_code IS '部门编码';
COMMENT ON COLUMN t_sys_department.nc_pk_org IS '基地id';
COMMENT ON COLUMN t_sys_department.nc_org_name IS '基地名称';

-- 创建仓库表
CREATE TABLE t_sys_warehouse (
                                 id SERIAL PRIMARY KEY,
                                 nc_pk_stordoc VARCHAR(255),
                                 nc_pk_org VARCHAR(255),
                                 nc_name VARCHAR(255),
                                 nc_code VARCHAR(255)
);
COMMENT ON TABLE t_sys_warehouse IS '仓库';
COMMENT ON COLUMN t_sys_warehouse.nc_pk_stordoc IS '仓库pk';
COMMENT ON COLUMN t_sys_warehouse.nc_pk_org IS '基地pk';
COMMENT ON COLUMN t_sys_warehouse.nc_name IS '仓库名称';
COMMENT ON COLUMN t_sys_warehouse.nc_code IS '仓库编码';

-- 2025-06-24 物料BOM附表
CREATE TABLE t_sync_material_bom (
     id SERIAL PRIMARY KEY,
     parent_id INTEGER NOT NULL,
     material_id INTEGER NOT NULL,
     ratio DOUBLE PRECISION
);

COMMENT ON TABLE t_sync_material_bom IS '物料BOM附表';
COMMENT ON COLUMN t_sync_material_bom.id IS '物料BOM附表ID';
COMMENT ON COLUMN t_sync_material_bom.parent_id IS '父级物料ID';
COMMENT ON COLUMN t_sync_material_bom.material_id IS '物料ID';
COMMENT ON COLUMN t_sync_material_bom.ratio IS '比例';

ALTER TABLE t_sync_material_bom
ALTER COLUMN ratio TYPE NUMERIC(20,10)
USING ratio::NUMERIC(20,10)

ALTER TABLE t_sync_material_bom ADD COLUMN material_name VARCHAR(100);
COMMENT ON COLUMN t_sync_material_bom.material_name IS '物料名称';

-- 接单时间
ALTER TABLE "public"."t_bus_order_head"
    ADD COLUMN "nc_receive_time" timestamp;

COMMENT ON COLUMN "public"."t_bus_order_head"."nc_receive_time" IS '接单时间';


-- 更新菜单默认可用为1 2025-01-02
update t_sys_menu set enabled = '1' where enabled = '0';


-- 2025-06-30 用户详细信息表
CREATE TABLE t_sys_user_detail (
                                   user_detail_id serial4 NOT NULL, -- 用户信息表ID
                                   user_id varchar(255) NOT NULL, -- 用户ID
                                   nc_pk_org varchar(255) NULL, -- 基地ID
                                   nc_org_name varchar(255) NULL, -- 基地名称
                                   nc_cwkid varchar(255) NULL, -- 产线ID
                                   nc_vwkname varchar(255) NULL, -- 产线名称
--                                    nc_cdeptid varchar(255) NULL, -- 部门ID
--                                    nc_cdeptname varchar(255) NULL, -- 部门名称
                                   CONSTRAINT t_sys_user_detail_pkey PRIMARY KEY (user_detail_id)
);
COMMENT ON TABLE public.t_sys_user_detail IS '用户详细信息表';

COMMENT ON COLUMN public.t_sys_user_detail.user_detail_id IS '用户信息表ID';
COMMENT ON COLUMN public.t_sys_user_detail.user_id IS '用户ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_pk_org IS '基地ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_org_name IS '基地名称';
COMMENT ON COLUMN public.t_sys_user_detail.nc_cwkid IS '产线ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_vwkname IS '产线名称';
-- COMMENT ON COLUMN public.t_sys_user_detail.nc_cdeptid IS '部门ID';
-- COMMENT ON COLUMN public.t_sys_user_detail.nc_cdeptname IS '部门名称';

ALTER TABLE t_bus_order_head
    ADD COLUMN nc_note VARCHAR(255);
COMMENT ON COLUMN public.t_bus_order_head.nc_note IS '备注';

ALTER TABLE t_bus_order_ppbom ADD COLUMN nc_material_model VARCHAR(255);
COMMENT ON COLUMN public.t_bus_order_ppbom.nc_material_model IS '型号';

ALTER TABLE t_sys_department ADD COLUMN is_delete VARCHAR(1) DEFAULT '0';
COMMENT  ON COLUMN t_sys_department.is_delete IS '0:未删除 1:已删除';
ALTER TABLE t_sys_warehouse ADD COLUMN is_delete VARCHAR(1) DEFAULT '0';
COMMENT  ON COLUMN t_sys_warehouse.is_delete IS '0:未删除 1:已删除';
ALTER TABLE t_sys_workline ADD COLUMN is_delete VARCHAR(1) DEFAULT '0';
COMMENT  ON COLUMN t_sys_workline.is_delete IS '0:未删除 1:已删除';
ALTER TABLE t_sys_organization ADD COLUMN is_delete VARCHAR(1) DEFAULT '0';
COMMENT  ON COLUMN t_sys_organization.is_delete IS '0:未删除 1:已删除';
ALTER TABLE t_bus_order_head ALTER COLUMN is_deleted SET DEFAULT '0';

ALTER TABLE "public"."t_sys_workline" RENAME COLUMN "is_delete" TO "nc_status";

ALTER TABLE "public"."t_sys_workline"
ALTER COLUMN "nc_status" TYPE varchar(16) COLLATE "pg_catalog"."default";

COMMENT ON COLUMN "public"."t_sys_workline"."nc_status" IS '状态：生效;失效';

ALTER TABLE "public"."t_sys_organization" RENAME COLUMN "is_delete" TO "nc_status";

ALTER TABLE "public"."t_sys_organization"
ALTER COLUMN "nc_status" TYPE varchar(16) COLLATE "pg_catalog"."default";

COMMENT ON COLUMN "public"."t_sys_organization"."nc_status" IS '状态：生效;失效';

ALTER TABLE "public"."t_sys_warehouse" RENAME COLUMN "is_delete" TO "nc_status";

ALTER TABLE "public"."t_sys_warehouse"
ALTER COLUMN "nc_status" TYPE varchar(16) COLLATE "pg_catalog"."default";

COMMENT ON COLUMN "public"."t_sys_warehouse"."nc_status" IS '状态：生效;失效';

ALTER TABLE "public"."t_sys_department" RENAME COLUMN "is_delete" TO "nc_status";

ALTER TABLE "public"."t_sys_department"
ALTER COLUMN "nc_status" TYPE varchar(16) COLLATE "pg_catalog"."default";

COMMENT ON COLUMN "public"."t_sys_department"."nc_status" IS '状态：生效;失效';

-- 2025-07-01 班别 增加车间主任
ALTER TABLE t_sys_class ADD COLUMN workshop_director_id int4;
COMMENT ON COLUMN t_sys_class.workshop_director_id IS '车间主任id';

ALTER TABLE t_sys_class ADD COLUMN workshop_director varchar(255);
COMMENT ON COLUMN t_sys_class.workshop_director IS '车间主任';


-- 2025-7-16  质检类目和质检方案表
-- public.t_sys_quality_category definition

-- Drop table

-- DROP TABLE public.t_sys_quality_category;

CREATE TABLE public.t_sys_quality_category (
                                               id serial4 NOT NULL, -- 质检类目ID，主键，自增
                                               inspection_item varchar(255) NULL, -- 检查项目名称
                                               key_process varchar(255) NULL, -- 关键工序,字典
                                               monitoring_method varchar(255) NULL, -- 监控方法，字典值
                                               material_id int4 NULL, -- 关联的物料ID
                                               product_name varchar(255) NULL, -- 产品名称
                                               standard text NULL, -- 质检标准，大文本类型
                                               remarks text NULL, -- 备注信息
                                               is_enabled varchar(255) NULL, -- 状态，字典值
                                               create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间，自动记录
                                               create_user varchar(255) NULL, -- 创建人
                                               update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间，自动记录
                                               update_user varchar(255) NULL, -- 更新人
                                               key_process_name varchar(255) NULL, -- 关键工序名称
                                               monitoring_method_name varchar(255) NULL, -- 监控方法名称
                                               CONSTRAINT t_sys_quality_category_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_category IS '质检类目主表，存储质检类目的基本信息';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_category.id IS '质检类目ID，主键，自增';
COMMENT ON COLUMN public.t_sys_quality_category.inspection_item IS '检查项目名称';
COMMENT ON COLUMN public.t_sys_quality_category.key_process IS '关键工序,字典';
COMMENT ON COLUMN public.t_sys_quality_category.monitoring_method IS '监控方法，字典值';
COMMENT ON COLUMN public.t_sys_quality_category.material_id IS '关联的物料ID';
COMMENT ON COLUMN public.t_sys_quality_category.product_name IS '产品名称';
COMMENT ON COLUMN public.t_sys_quality_category.standard IS '质检标准，大文本类型';
COMMENT ON COLUMN public.t_sys_quality_category.remarks IS '备注信息';
COMMENT ON COLUMN public.t_sys_quality_category.is_enabled IS '状态，字典值';
COMMENT ON COLUMN public.t_sys_quality_category.create_time IS '创建时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_category.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_category.update_time IS '更新时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_category.update_user IS '更新人';
COMMENT ON COLUMN public.t_sys_quality_category.key_process_name IS '关键工序名称';
COMMENT ON COLUMN public.t_sys_quality_category.monitoring_method_name IS '监控方法名称';


-- public.t_sys_quality_category_config definition

-- Drop table

-- DROP TABLE public.t_sys_quality_category_config;

CREATE TABLE public.t_sys_quality_category_config (
                                                      id serial4 NOT NULL, -- 配置信息ID，主键，自增
                                                      category_id int4 NULL, -- 关联的质检类目ID
                                                      material_id int4 NULL, -- 关联的物料ID
                                                      field_name varchar(255) NULL, -- 字段名称，字典值，物料管理弹窗选择后自动带出
                                                      is_enabled varchar(255) NULL, -- 是否启用，字典值：是/否
                                                      field_type varchar(255) NULL, -- 字段类型，字典值：文本/日期/数值/下拉框
                                                      parameter_range varchar(255) NULL, -- 参数范围，文本类型
                                                      dropdown_fields varchar(255) NULL, -- 下拉框字段，多个字段用逗号间隔
                                                      unit varchar(255) NULL, -- 单位，文本类型
                                                      is_required varchar(255) NULL, -- 是否必填，字典值：是/否
                                                      create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间，自动记录
                                                      create_user varchar(255) NULL, -- 创建人
                                                      update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间，自动记录
                                                      update_user varchar(255) NULL, -- 更新人
                                                      material_name varchar(255) NULL, -- 品名
                                                      CONSTRAINT t_sys_quality_category_config_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_category_config IS '质检类目配置信息表，存储质检类目的字段配置信息';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_category_config.id IS '配置信息ID，主键，自增';
COMMENT ON COLUMN public.t_sys_quality_category_config.category_id IS '关联的质检类目ID';
COMMENT ON COLUMN public.t_sys_quality_category_config.material_id IS '关联的物料ID';
COMMENT ON COLUMN public.t_sys_quality_category_config.field_name IS '字段名称，字典值，物料管理弹窗选择后自动带出';
COMMENT ON COLUMN public.t_sys_quality_category_config.is_enabled IS '是否启用，字典值：是/否';
COMMENT ON COLUMN public.t_sys_quality_category_config.field_type IS '字段类型，字典值：文本/日期/数值/下拉框';
COMMENT ON COLUMN public.t_sys_quality_category_config.parameter_range IS '参数范围，文本类型';
COMMENT ON COLUMN public.t_sys_quality_category_config.dropdown_fields IS '下拉框字段，多个字段用逗号间隔';
COMMENT ON COLUMN public.t_sys_quality_category_config.unit IS '单位，文本类型';
COMMENT ON COLUMN public.t_sys_quality_category_config.is_required IS '是否必填，字典值：是/否';
COMMENT ON COLUMN public.t_sys_quality_category_config.create_time IS '创建时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_category_config.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_category_config.update_time IS '更新时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_category_config.update_user IS '更新人';
COMMENT ON COLUMN public.t_sys_quality_category_config.material_name IS '品名';

-- public.t_sys_quality_plan definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan;

CREATE TABLE public.t_sys_quality_plan (
                                           id serial4 NOT NULL, -- 质检方案ID，主键，自增
                                           plan_name varchar(255) NULL, -- 方案名称
                                           production_line_id varchar(255) NULL, -- 生产线ID
                                           remarks varchar(255) NULL, -- 备注信息
                                           is_enabled varchar(255) NULL, -- 启停状态
                                           create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间，自动记录
                                           create_user varchar(255) NULL, -- 创建人
                                           update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间，自动记录
                                           update_user varchar(255) NULL, -- 更新人
                                           production_line_name varchar(255) NULL, -- 生产线名称
                                           CONSTRAINT t_sys_quality_plan_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_plan IS '质检方案主表，存储质检方案的基本信息';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_plan.id IS '质检方案ID，主键，自增';
COMMENT ON COLUMN public.t_sys_quality_plan.plan_name IS '方案名称';
COMMENT ON COLUMN public.t_sys_quality_plan.production_line_id IS '生产线ID';
COMMENT ON COLUMN public.t_sys_quality_plan.remarks IS '备注信息';
COMMENT ON COLUMN public.t_sys_quality_plan.is_enabled IS '启停状态';
COMMENT ON COLUMN public.t_sys_quality_plan.create_time IS '创建时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_plan.update_time IS '更新时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan.update_user IS '更新人';
COMMENT ON COLUMN public.t_sys_quality_plan.production_line_name IS '生产线名称';


-- public.t_sys_quality_plan_config definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan_config;

CREATE TABLE public.t_sys_quality_plan_config (
                                                  id serial4 NOT NULL, -- 配置信息ID，主键，自增
                                                  plan_id int4 NULL, -- 关联的质检方案ID
                                                  category_id int4 NULL, -- 关联的质检类目ID
                                                  config_data text NULL, -- 配置数据，JSONB类型，存储动态配置信息
                                                  create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间，自动记录
                                                  create_user varchar(255) NULL, -- 创建人
                                                  update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间，自动记录
                                                  update_user varchar(255) NULL, -- 更新人
                                                  inspection_item varchar(255) NULL, -- 检查项目
                                                  key_process varchar(255) NULL, -- 关键工序id
                                                  key_process_name varchar(255) NULL, -- 关键工序名称
                                                  monitoring_method varchar(255) NULL, -- 监控方法id
                                                  monitoring_method_name varchar(255) NULL, -- 监控方法名称
                                                  standard text NULL,
                                                  CONSTRAINT t_sys_quality_plan_config_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_plan_config IS '质检方案配置信息表，存储质检方案的配置信息，包括关联的质检类目';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_plan_config.id IS '配置信息ID，主键，自增';
COMMENT ON COLUMN public.t_sys_quality_plan_config.plan_id IS '关联的质检方案ID';
COMMENT ON COLUMN public.t_sys_quality_plan_config.category_id IS '关联的质检类目ID';
COMMENT ON COLUMN public.t_sys_quality_plan_config.config_data IS '配置数据，JSONB类型，存储动态配置信息';
COMMENT ON COLUMN public.t_sys_quality_plan_config.create_time IS '创建时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan_config.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_plan_config.update_time IS '更新时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan_config.update_user IS '更新人';
COMMENT ON COLUMN public.t_sys_quality_plan_config.inspection_item IS '检查项目';
COMMENT ON COLUMN public.t_sys_quality_plan_config.key_process IS '关键工序id';
COMMENT ON COLUMN public.t_sys_quality_plan_config.key_process_name IS '关键工序名称';
COMMENT ON COLUMN public.t_sys_quality_plan_config.monitoring_method IS '监控方法id';
COMMENT ON COLUMN public.t_sys_quality_plan_config.monitoring_method_name IS '监控方法名称';


-- public.t_sys_quality_plan_judgment definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan_judgment;

CREATE TABLE public.t_sys_quality_plan_judgment (
                                                    id serial4 NOT NULL, -- 判定信息ID，主键，自增
                                                    plan_id int4 NULL, -- 关联的质检方案ID
                                                    field_name varchar(255) NULL, -- 字段名称，字典值
                                                    is_enabled varchar(255) NULL, -- 是否启用，字典值：是/否
                                                    field_type varchar(255) NULL, -- 字段类型，字典值：文本/日期/数值/下拉框
                                                    dropdown_fields varchar(255) NULL, -- 下拉框字段，多个字段用逗号间隔
                                                    unit varchar(255) NULL, -- 单位，文本类型
                                                    is_required varchar(255) NULL, -- 是否必填，字典值：是/否
                                                    create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间，自动记录
                                                    create_user varchar(255) NULL, -- 创建人
                                                    update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间，自动记录
                                                    update_user varchar(255) NULL, -- 更新人
                                                    CONSTRAINT t_sys_quality_plan_judgment_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_plan_judgment IS '质检方案判定信息表，存储质检方案的判定字段信息';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_plan_judgment.id IS '判定信息ID，主键，自增';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.plan_id IS '关联的质检方案ID';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.field_name IS '字段名称，字典值';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.is_enabled IS '是否启用，字典值：是/否';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.field_type IS '字段类型，字典值：文本/日期/数值/下拉框';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.dropdown_fields IS '下拉框字段，多个字段用逗号间隔';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.unit IS '单位，文本类型';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.is_required IS '是否必填，字典值：是/否';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.create_time IS '创建时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.update_time IS '更新时间，自动记录';
COMMENT ON COLUMN public.t_sys_quality_plan_judgment.update_user IS '更新人';




-- public.t_sys_quality_ctrl definition

-- Drop table

-- DROP TABLE public.t_sys_quality_ctrl;

CREATE TABLE public.t_sys_quality_ctrl (
                                           id serial4 NOT NULL,
                                           quality_ctrl_no varchar(255) NULL, -- 质检控制编号
                                           material_id int4 NULL, -- 物料ID
                                           material_name varchar(255) NULL, -- 物料名称
                                           plan_id int4 NULL, -- 方案ID
                                           plan_name varchar(255) NULL, -- 方案名称
                                           production_line_id varchar(255) NULL, -- 生产线ID
                                           production_line_name varchar(255) NULL, -- 生产线名称
                                           remarks varchar(255) NULL, -- 备注
                                           create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                           create_user varchar(255) NULL,
                                           update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                           update_user varchar(255) NULL,
                                           inspection_date timestamp NULL, -- 质检时间
                                           status varchar(10) NULL, -- 状态
                                           CONSTRAINT t_sys_quality_ctrl_pkey PRIMARY KEY (id)
);

-- Column comments

COMMENT ON TABLE t_sys_quality_ctrl IS '质检控制表';

COMMENT ON COLUMN public.t_sys_quality_ctrl.quality_ctrl_no IS '质检控制编号';
COMMENT ON COLUMN public.t_sys_quality_ctrl.material_id IS '物料ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl.material_name IS '物料名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl.plan_id IS '方案ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl.plan_name IS '方案名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl.production_line_id IS '生产线ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl.production_line_name IS '生产线名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl.remarks IS '备注';
COMMENT ON COLUMN public.t_sys_quality_ctrl.inspection_date IS '质检时间';
COMMENT ON COLUMN public.t_sys_quality_ctrl.status IS '状态';


-- public.t_sys_quality_ctrl_detail definition

-- Drop table

-- DROP TABLE public.t_sys_quality_ctrl_detail;

CREATE TABLE public.t_sys_quality_ctrl_detail (
                                                  id int4 DEFAULT nextval('t_sys_quality_ctrl_detil_id_seq'::regclass) NOT NULL,
                                                  ctrl_id int4 NULL, -- 主表ID
                                                  category_id int4 NULL, -- 分类ID
                                                  inspection_item varchar(255) NULL, -- 检查项目
                                                  key_process varchar(255) NULL, -- 关键工序
                                                  key_process_name varchar(255) NULL, -- 关键工序名称
                                                  monitoring_method varchar(255) NULL, -- 监控方法
                                                  monitoring_method_name varchar(255) NULL, -- 监控方法名称
                                                  standard text NULL, -- 质检标准
                                                  judgment_data text NULL, -- 判定配置数据存储动态配置信息
                                                  config_data text NULL, -- 分类配置数据存储动态配置信息
                                                  create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                  create_user varchar(255) NULL,
                                                  update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                  update_user varchar(255) NULL,
                                                  CONSTRAINT t_sys_quality_ctrl_detil_pkey PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.ctrl_id IS '主表ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.category_id IS '分类ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.inspection_item IS '检查项目';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.key_process IS '关键工序';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.key_process_name IS '关键工序名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.monitoring_method IS '监控方法';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.monitoring_method_name IS '监控方法名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.standard IS '质检标准';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.judgment_data IS '判定配置数据存储动态配置信息';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.config_data IS '分类配置数据存储动态配置信息';

-- 2025-07-23 班别、工艺路线、设备 增加基地id
ALTER TABLE t_sys_class ADD COLUMN pk_org VARCHAR(255);
COMMENT ON COLUMN t_sys_class.pk_org IS '基地id';

ALTER TABLE t_sys_craft_info ADD COLUMN pk_org VARCHAR(255);
COMMENT ON COLUMN t_sys_craft_info.pk_org IS '基地id';

ALTER TABLE t_sys_device ADD COLUMN pk_org VARCHAR(255);
COMMENT ON COLUMN t_sys_device.pk_org IS '基地id';

ALTER TABLE t_bus_order_process_history
    ADD COLUMN all_import_pot int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.all_import_pot IS '积累投入锅数';

ALTER TABLE public.t_bus_order_process_history ADD iot_math varchar NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.iot_math IS '计算公式';

ALTER TABLE public.t_bus_order_process_history ADD iot_qty int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.iot_qty IS '设备采集数量';

create table t_bus_user_current_org_line(
                                            id serial4 NOT NULL,
                                            user_id varchar(255) NULL,
                                            org varchar(255) NULL,
                                            workline varchar(255) NULL,
                                            CONSTRAINT t_bus_user_current_org_line_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_bus_user_current_org_line IS '记录用户登录的基地产线';
COMMENT ON COLUMN public.t_bus_user_current_org_line.user_id IS '用户ID';
COMMENT ON COLUMN public.t_bus_user_current_org_line.org IS '基地id';
COMMENT ON COLUMN public.t_bus_user_current_org_line.workline IS '产线id';

-- 2025-07-31 30355 【后台】工序管理
ALTER TABLE public.t_sys_process_info ADD report_type varchar NULL;
COMMENT ON COLUMN public.t_sys_process_info.report_type IS '报工类型';

create table t_bus_inventory_inout(
                                      id serial4 NOT NULL,
                                      order_process_history_id INT4,
                                      bill_id varchar(255) NOT NULL,
                                      qty FLOAT4,
                                      CONSTRAINT t_bus_inventory_inout_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_bus_inventory_inout IS '库存出入记录表';
COMMENT ON COLUMN public.t_bus_inventory_inout.order_process_history_id IS '报工记录表行ID';
COMMENT ON COLUMN public.t_bus_inventory_inout.bill_id IS '库存ID';
COMMENT ON COLUMN public.t_bus_inventory_inout.qty IS '变化数量';
ALTER TABLE "public"."t_bus_order_head"
    ALTER COLUMN "body_pot_qty" SET DEFAULT 0;


-- 创建净含量范围管理表
CREATE TABLE t_sys_net_content_range (
                                         id SERIAL PRIMARY KEY,
                                         material_code VARCHAR(255),
                                         material_name VARCHAR(255),
                                         material_model VARCHAR(255),
                                         material_id INTEGER,
                                         lower_limit NUMERIC(10,2),
                                         upper_limit NUMERIC(10,2),
                                         status VARCHAR(20),
                                         create_user VARCHAR(255),
                                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加表注释
COMMENT ON TABLE t_sys_net_content_range IS '净含量范围管理表';

-- 添加字段注释
COMMENT ON COLUMN t_sys_net_content_range.id IS '主键ID';
COMMENT ON COLUMN t_sys_net_content_range.material_code IS '产品编码';
COMMENT ON COLUMN t_sys_net_content_range.material_name IS '产品名称';
COMMENT ON COLUMN t_sys_net_content_range.material_model IS '产品规格';
COMMENT ON COLUMN t_sys_net_content_range.material_id IS '产品ID';
COMMENT ON COLUMN t_sys_net_content_range.lower_limit IS '下限值(g)';
COMMENT ON COLUMN t_sys_net_content_range.upper_limit IS '上限值(g)';
COMMENT ON COLUMN t_sys_net_content_range.status IS '状态（启用/禁用）';
COMMENT ON COLUMN t_sys_net_content_range.create_user IS '创建人';
COMMENT ON COLUMN t_sys_net_content_range.create_time IS '创建时间';


-- 添加表注释
COMMENT ON TABLE t_sys_craft_info IS '工艺路线表';

COMMENT ON COLUMN t_sys_craft_info.craft_id IS '工艺路线ID';
COMMENT ON COLUMN t_sys_craft_info.craft_name IS '工艺路线名称';
COMMENT ON COLUMN t_sys_craft_info.craft_number IS '工艺路线编号';
COMMENT ON COLUMN t_sys_craft_info.effective_time IS '生效时间';
COMMENT ON COLUMN t_sys_craft_info.failure_time IS '失效时间';
COMMENT ON COLUMN t_sys_craft_info.craft_detail IS '工艺说明';
COMMENT ON COLUMN t_sys_craft_info.enabled IS '是否启用 0：禁用 1：启用';
COMMENT ON COLUMN t_sys_craft_info.created_user IS '创建人';
COMMENT ON COLUMN t_sys_craft_info.created_time IS '创建日期';
COMMENT ON COLUMN t_sys_craft_info.updated_time IS '修改日期';
COMMENT ON COLUMN t_sys_craft_info.updated_user IS '修改人';


COMMENT ON TABLE t_sys_personnel_info IS '人员类';

COMMENT ON COLUMN t_sys_personnel_info.personnel_id IS '人员ID';
COMMENT ON COLUMN t_sys_personnel_info.name IS '姓名';
COMMENT ON COLUMN t_sys_personnel_info.sex IS '性别';
COMMENT ON COLUMN t_sys_personnel_info.phone IS '手机号';
COMMENT ON COLUMN t_sys_personnel_info.station IS '岗位';
COMMENT ON COLUMN t_sys_personnel_info.class_name IS '班组';
COMMENT ON COLUMN t_sys_personnel_info.user_id IS '绑定的账号用户ID';
COMMENT ON COLUMN t_sys_personnel_info.user_email IS '绑定用户的账号';
COMMENT ON COLUMN t_sys_personnel_info.enabled_st IS '是否可用 0：禁用 1：启用';
COMMENT ON COLUMN t_sys_personnel_info.crt_user IS '创建人';
COMMENT ON COLUMN t_sys_personnel_info.crt_time IS '创建日期';
COMMENT ON COLUMN t_sys_personnel_info.update_time IS '修改日期';
COMMENT ON COLUMN t_sys_personnel_info.update_user IS '修改人';


-- public.t_sys_quality_ctrl_detail definition

-- Drop table

-- DROP TABLE t_sys_quality_ctrl_detail;

CREATE TABLE t_sys_quality_ctrl_detail (
                                           id int4 NOT NULL DEFAULT nextval('t_sys_quality_ctrl_detil_id_seq'::regclass),
                                           ctrl_id int4 NULL, -- 主表ID
                                           category_id int4 NULL, -- 分类ID
                                           inspection_item varchar(255) NULL, -- 检查项目
                                           key_process varchar(255) NULL, -- 关键工序
                                           key_process_name varchar(255) NULL, -- 关键工序名称
                                           monitoring_method varchar(255) NULL, -- 监控方法
                                           monitoring_method_name varchar(255) NULL, -- 监控方法名称
                                           standard text NULL, -- 质检标准
                                           judgment_data text NULL, -- 判定配置数据存储动态配置信息
                                           config_data text NULL, -- 分类配置数据存储动态配置信息
                                           create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP, -- 创建时间
                                           create_user varchar(255) NULL, -- 创建人
                                           update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP, -- 更新时间
                                           update_user varchar(255) NULL, -- 更新人
                                           machine_data text NULL, -- 机台配置数据存储动态配置信息
                                           schedule_data text NULL, -- 班时配置数据存储动态配置信息
                                           CONSTRAINT t_sys_quality_ctrl_detil_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_ctrl_detail IS '质检控制明细表';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.ctrl_id IS '主表ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.category_id IS '分类ID';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.inspection_item IS '检查项目';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.key_process IS '关键工序';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.key_process_name IS '关键工序名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.monitoring_method IS '监控方法';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.monitoring_method_name IS '监控方法名称';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.standard IS '质检标准';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.judgment_data IS '判定配置数据存储动态配置信息';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.config_data IS '分类配置数据存储动态配置信息';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.create_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.create_user IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.update_time IS '更新时间';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.update_user IS '更新人';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.machine_data IS '机台配置数据存储动态配置信息';
COMMENT ON COLUMN public.t_sys_quality_ctrl_detail.schedule_data IS '班时配置数据存储动态配置信息';


-- public.t_sys_quality_report_category definition

-- Drop table

-- DROP TABLE t_sys_quality_report_category;

CREATE TABLE t_sys_quality_report_category (
                                               id serial4 NOT NULL, -- 主键ID
                                               frequency varchar NOT NULL, -- 频次，字典
                                               important_item text NOT NULL, -- 重点项目
                                               enabled int4 NOT NULL DEFAULT 0, -- 启用/禁用
                                               created_name varchar(255) NULL, -- 创建人
                                               created_time date NULL, -- 创建时间
                                               updated_name varchar(255) NULL, -- 修改人
                                               updated_time date NULL, -- 修改日期
                                               remark varchar(255) NULL, -- 备注
                                               frequency_value varchar NULL, -- 频次，字典值
                                               CONSTRAINT t_sys_quality_report_pkey PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_report_category.id IS '主键ID';
COMMENT ON COLUMN public.t_sys_quality_report_category.frequency IS '频次，字典';
COMMENT ON COLUMN public.t_sys_quality_report_category.important_item IS '重点项目';
COMMENT ON COLUMN public.t_sys_quality_report_category.enabled IS '启用/禁用';
COMMENT ON COLUMN public.t_sys_quality_report_category.created_name IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_report_category.created_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_quality_report_category.updated_name IS '修改人';
COMMENT ON COLUMN public.t_sys_quality_report_category.updated_time IS '修改日期';
COMMENT ON COLUMN public.t_sys_quality_report_category.remark IS '备注';
COMMENT ON COLUMN public.t_sys_quality_report_category.frequency_value IS '频次，字典值';


-- public.t_sys_quality_report_item definition

-- Drop table

-- DROP TABLE t_sys_quality_report_item;

CREATE TABLE t_sys_quality_report_item (
                                           id serial4 NOT NULL, -- 主键ID
                                           category_id int4 NULL, -- 关联的日报类目表ID
                                           field_name varchar(255) NULL, -- 达成（异常）情况描述
                                           field_type varchar(255) NULL, -- 达成（异常）情况类型(下拉，或文本)
                                           dropdown_fields varchar(255) NULL, -- 下拉框选项
                                           required varchar(255) NULL, -- 是否必填
                                           enabled varchar(255) NULL, -- 启用/禁用
                                           created_name varchar(255) NULL, -- 创建人
                                           created_time date NULL, -- 创建时间
                                           updated_name varchar(255) NULL, -- 修改人
                                           updated_time date NULL, -- 修改日期
                                           CONSTRAINT t_sys_quality_report_item_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_report_item IS '质量日报项目表';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_report_item.id IS '主键ID';
COMMENT ON COLUMN public.t_sys_quality_report_item.category_id IS '关联的日报类目表ID';
COMMENT ON COLUMN public.t_sys_quality_report_item.field_name IS '达成（异常）情况描述';
COMMENT ON COLUMN public.t_sys_quality_report_item.field_type IS '达成（异常）情况类型(下拉，或文本)';
COMMENT ON COLUMN public.t_sys_quality_report_item.dropdown_fields IS '下拉框选项';
COMMENT ON COLUMN public.t_sys_quality_report_item.required IS '是否必填';
COMMENT ON COLUMN public.t_sys_quality_report_item.enabled IS '启用/禁用';
COMMENT ON COLUMN public.t_sys_quality_report_item.created_name IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_report_item.created_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_quality_report_item.updated_name IS '修改人';
COMMENT ON COLUMN public.t_sys_quality_report_item.updated_time IS '修改日期';


-- public.t_sys_quality_report_plan definition

-- Drop table

-- DROP TABLE t_sys_quality_report_plan;

CREATE TABLE t_sys_quality_report_plan (
                                           id serial4 NOT NULL, -- 主键ID
                                           product_name varchar(255) NULL, -- 品名
                                           prod_dept_id varchar(255) NULL, -- 生产部门ID
                                           prod_line_id varchar(255) NULL, -- 生产线ID
                                           remark varchar(255) NULL, -- 备注
                                           created_name varchar(255) NULL, -- 创建人
                                           created_time date NULL, -- 创建时间
                                           updated_name varchar(255) NULL, -- 修改人
                                           updated_time date NULL, -- 修改日期
                                           prod_dept_name varchar NULL, -- 生产部门名称
                                           prod_line_name varchar NULL, -- 生产线名称
                                           enabled int4 NULL, -- 启用/禁用
                                           CONSTRAINT t_sys_quality_report_plan_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_report_plan IS '质检日报方案表';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_report_plan.id IS '主键ID';
COMMENT ON COLUMN public.t_sys_quality_report_plan.product_name IS '品名';
COMMENT ON COLUMN public.t_sys_quality_report_plan.prod_dept_id IS '生产部门ID';
COMMENT ON COLUMN public.t_sys_quality_report_plan.prod_line_id IS '生产线ID';
COMMENT ON COLUMN public.t_sys_quality_report_plan.remark IS '备注';
COMMENT ON COLUMN public.t_sys_quality_report_plan.created_name IS '创建人';
COMMENT ON COLUMN public.t_sys_quality_report_plan.created_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_quality_report_plan.updated_name IS '修改人';
COMMENT ON COLUMN public.t_sys_quality_report_plan.updated_time IS '修改日期';
COMMENT ON COLUMN public.t_sys_quality_report_plan.prod_dept_name IS '生产部门名称';
COMMENT ON COLUMN public.t_sys_quality_report_plan.prod_line_name IS '生产线名称';
COMMENT ON COLUMN public.t_sys_quality_report_plan.enabled IS '启用/禁用';


-- public.t_sys_quality_report_plan_rel definition

-- Drop table

-- DROP TABLE t_sys_quality_report_plan_rel;

CREATE TABLE t_sys_quality_report_plan_rel (
                                               id serial4 NOT NULL, -- 主键ID
                                               plan_id int4 NOT NULL, -- 质检日报方案ID
                                               category_id int4 NOT NULL, -- 质检日报类目ID
                                               CONSTRAINT t_sys_quality_report_plan_rel_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_sys_quality_report_plan_rel IS '质检日报方案与类目关系表';

-- Column comments

COMMENT ON COLUMN public.t_sys_quality_report_plan_rel.id IS '主键ID';
COMMENT ON COLUMN public.t_sys_quality_report_plan_rel.plan_id IS '质检日报方案ID';
COMMENT ON COLUMN public.t_sys_quality_report_plan_rel.category_id IS '质检日报类目ID';

-- 2025-09-11
ALTER TABLE public.t_sys_pd_record ADD pd_workshop_nc_id varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_pd_record.pd_workshop_nc_id IS '盘点车间ncid';
ALTER TABLE public.t_sys_pd_record_split ADD pd_workshop_nc_id varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_pd_record_split.pd_workshop_nc_id IS '盘点车间ncid';


-- 订单报工记录表 修改数量类型 2025-10-08
ALTER TABLE "public"."t_bus_order_process_record"
  ALTER COLUMN "record_qty" TYPE numeric(24,6) USING "record_qty"::numeric(24,6),
  ALTER COLUMN "record_manual_qty" TYPE numeric(24,6) USING "record_manual_qty"::numeric(24,6);

ALTER TABLE "public"."t_bus_order_process_history"
  ALTER COLUMN "record_qty" TYPE numeric(24,6) USING "record_qty"::numeric(24,6),
  ALTER COLUMN "record_manual_qty" TYPE numeric(24,6) USING "record_manual_qty"::numeric(24,6);



-- 每日报表表体信息 2025-10-14
CREATE TABLE t_bus_daily_report_entry (
                                          id serial4 NOT NULL, -- 表id
                                          dailyreport_id int4 NULL, -- 连接表头id
                                          fseq varchar(500) NULL, -- 序号
                                          frequency varchar(500) NULL, -- 频次id
                                          frequency_value varchar(500) NULL, -- 频次값
                                          important_item varchar(500) NULL, -- 重点项目
                                          CONSTRAINT daily_report_dto_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_bus_daily_report_entry IS '每日报表表体信息';

COMMENT ON COLUMN public.t_bus_daily_report_entry.id IS '表id';
COMMENT ON COLUMN public.t_bus_daily_report_entry.dailyreport_id IS '连接表头id';
COMMENT ON COLUMN public.t_bus_daily_report_entry.fseq IS '序号';
COMMENT ON COLUMN public.t_bus_daily_report_entry.frequency IS '频次id';
COMMENT ON COLUMN public.t_bus_daily_report_entry.frequency_value IS '频次값';
COMMENT ON COLUMN public.t_bus_daily_report_entry.important_item IS '重点项目';

-- 每日报表表体关联得配置管理信息数据
CREATE TABLE t_bus_daily_report_entry_item (
                                               id serial4 NOT NULL, -- 表id
                                               dailyreport_entry_id int4 NULL, -- 关联的每日报表明细id
                                               field_name varchar(500) NULL, -- 达成（异常）清况描述"
                                               status varchar(500) NULL, -- 达成情况
                                               field_type_id varchar(500) NULL, -- 达成清况类型id
                                               field_type_value varchar(500) NULL, -- 达成情况类型값
                                               spilt_value varchar(500) NULL, -- 下拉列表对应字段值
                                               required varchar(255) NULL, -- 是否必填
                                               CONSTRAINT "t_bus_daily_report_entry_Item_pkey" PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_bus_daily_report_entry_item IS '每日报表表体关联得配置管理信息数据';

COMMENT ON COLUMN public.t_bus_daily_report_entry_item.dailyreport_entry_id IS '关联的每日报表明细id';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.field_name IS '达成（异常）清况描述"';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.status IS '达成情况';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.field_type_id IS '达成清况类型id';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.field_type_value IS '达成情况类型값';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.spilt_value IS '下拉列表对应字段值';
COMMENT ON COLUMN public.t_bus_daily_report_entry_item.required IS '是否必填';

-- 每日报表表头信息
CREATE TABLE t_bus_daily_report_head (
                                         id serial4 NOT NULL, -- 表id
                                         bill_no varchar(500) NULL, -- 单据编号
                                         material_code varchar(500) NULL, -- 产品编码
                                         material_name varchar(500) NULL, -- 产品名称
                                         solut_id varchar(500) NULL, -- 方案id
                                         solut_name varchar(500) NULL, -- 方案名称
                                         shop_manager_id varchar(500) NULL, -- 车间主任id
                                         shop_manager_name varchar(500) NULL, -- 车间主任名称
                                         created_name varchar(500) NULL, -- 创建人
                                         created_time date NULL, -- 创建时间
                                         updated_name varchar(500) NULL, -- 修改人
                                         updated_time date NULL, -- 修改日期
                                         enabled int4 NULL, -- 启用/禁用
                                         prod_line_id varchar(500) NULL, -- 生产线id
                                         prod_line_name varchar(500) NULL, -- 生产线名称
                                         status varchar NULL, -- 保存还是提交状态/保存是ture,提交是false
                                         CONSTRAINT daily_report_vo_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.t_bus_daily_report_head IS '每日报表表头信息';

COMMENT ON COLUMN public.t_bus_daily_report_head.id IS '表id';
COMMENT ON COLUMN public.t_bus_daily_report_head.bill_no IS '单据编号';
COMMENT ON COLUMN public.t_bus_daily_report_head.material_code IS '产品编码';
COMMENT ON COLUMN public.t_bus_daily_report_head.material_name IS '产品名称';
COMMENT ON COLUMN public.t_bus_daily_report_head.solut_id IS '方案id';
COMMENT ON COLUMN public.t_bus_daily_report_head.solut_name IS '方案名称';
COMMENT ON COLUMN public.t_bus_daily_report_head.shop_manager_id IS '车间主任id';
COMMENT ON COLUMN public.t_bus_daily_report_head.shop_manager_name IS '车间主任名称';
COMMENT ON COLUMN public.t_bus_daily_report_head.created_name IS '创建人';
COMMENT ON COLUMN public.t_bus_daily_report_head.created_time IS '创建时间';
COMMENT ON COLUMN public.t_bus_daily_report_head.updated_name IS '修改人';
COMMENT ON COLUMN public.t_bus_daily_report_head.updated_time IS '修改日期';
COMMENT ON COLUMN public.t_bus_daily_report_head.enabled IS '启用/禁用';
COMMENT ON COLUMN public.t_bus_daily_report_head.prod_line_id IS '生产线id';
COMMENT ON COLUMN public.t_bus_daily_report_head.prod_line_name IS '生产线名称';
COMMENT ON COLUMN public.t_bus_daily_report_head.status IS '保存还是提交状态/保存是ture,提交是false';

-- 2025-10-15
ALTER TABLE public.t_sys_recipe_input ALTER COLUMN standard_input TYPE numeric(16, 6) USING standard_input::numeric(16, 6);

-- 2025-10-28
ALTER TABLE t_sys_recipe_input ADD COLUMN semi_finished_product_name VARCHAR(255);
COMMENT ON COLUMN t_sys_recipe_input.semi_finished_product_name IS '半成品';
ALTER TABLE t_sys_recipe_input ADD COLUMN semi_finished_product_code VARCHAR(255);
COMMENT ON COLUMN t_sys_recipe_input.semi_finished_product_code IS '半成品编码';

-- 2025-11-10
ALTER TABLE t_sys_recipe_input ADD COLUMN plan_input_ratio NUMERIC(10,6);
COMMENT ON COLUMN t_sys_recipe_input.plan_input_ratio IS '计划投入比例';

ALTER TABLE t_sys_recipe_input ADD COLUMN pot_calculation_basis VARCHAR(1);
COMMENT ON COLUMN t_sys_recipe_input.pot_calculation_basis IS '锅数计算基准（复选框）';

-- 2025-11-01
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_seq_text varchar(10) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_seq_text IS '订单明细行号';

-- 2025-11-10 班别 增加产线id
ALTER TABLE t_sys_class ADD COLUMN nc_cwkid VARCHAR(255);
COMMENT ON COLUMN t_sys_class.nc_cwkid IS '产线id';

-- 2025-11-18
ALTER TABLE t_sys_recipe_input ADD COLUMN display_default_value VARCHAR(1);
COMMENT ON COLUMN t_sys_recipe_input.display_default_value IS '显示默认值（复选框）';

-- 2025-11-19
ALTER TABLE t_bus_order_process ADD COLUMN order_id int4;
COMMENT ON COLUMN t_bus_order_process.order_id IS '订单id';
ALTER TABLE t_bus_order_process ADD COLUMN nc_cmoid VARCHAR(255);
COMMENT ON COLUMN t_bus_order_process.nc_cmoid IS '订单明细id';

-- 2025-11-20
ALTER TABLE t_bus_inventory ADD COLUMN material_type_pd VARCHAR(64);
COMMENT ON COLUMN t_bus_inventory.material_type_pd IS '盘点物料分类';
