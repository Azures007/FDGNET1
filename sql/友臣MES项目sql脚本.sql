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
                                   nc_cdeptid varchar(255) NULL, -- 部门ID
                                   nc_cdeptname varchar(255) NULL, -- 部门名称
                                   CONSTRAINT t_sys_user_detail_pkey PRIMARY KEY (user_detail_id)
);
COMMENT ON TABLE public.t_sys_user_detail IS '用户详细信息表';

COMMENT ON COLUMN public.t_sys_user_detail.user_detail_id IS '用户信息表ID';
COMMENT ON COLUMN public.t_sys_user_detail.user_id IS '用户ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_pk_org IS '基地ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_org_name IS '基地名称';
COMMENT ON COLUMN public.t_sys_user_detail.nc_cwkid IS '产线ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_vwkname IS '产线名称';
COMMENT ON COLUMN public.t_sys_user_detail.nc_cdeptid IS '部门ID';
COMMENT ON COLUMN public.t_sys_user_detail.nc_cdeptname IS '部门名称';

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
ALTER TABLE public.t_sys_process_info ADD t_sys_process_info varchar NULL;
COMMENT ON COLUMN public.t_sys_process_info.t_sys_process_info IS '报工类型';


