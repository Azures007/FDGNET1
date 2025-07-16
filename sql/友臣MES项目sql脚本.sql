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
                                               id serial4 NOT NULL,
                                               inspection_item varchar(255) NULL,
                                               key_process varchar(255) NULL,
                                               monitoring_method varchar(255) NULL,
                                               material_id int4 NULL,
                                               product_name varchar(255) NULL,
                                               standard text NULL,
                                               remarks text NULL,
                                               is_enabled varchar(255) NULL,
                                               create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                               create_user varchar(255) NULL,
                                               update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                               update_user varchar(255) NULL,
                                               key_process_name varchar(255) NULL,
                                               monitoring_method_name varchar(255) NULL,
                                               CONSTRAINT t_sys_quality_category_pkey PRIMARY KEY (id)
);


-- public.t_sys_quality_category_config definition

-- Drop table

-- DROP TABLE public.t_sys_quality_category_config;

CREATE TABLE public.t_sys_quality_category_config (
                                                      id serial4 NOT NULL,
                                                      category_id int4 NULL,
                                                      material_id int4 NULL,
                                                      field_name varchar(255) NULL,
                                                      is_enabled varchar(255) NULL,
                                                      field_type varchar(255) NULL,
                                                      parameter_range varchar(255) NULL,
                                                      dropdown_fields varchar(255) NULL,
                                                      unit varchar(255) NULL,
                                                      is_required varchar(255) NULL,
                                                      create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                      create_user varchar(255) NULL,
                                                      update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                      update_user varchar(255) NULL,
                                                      material_name varchar(255) NULL,
                                                      CONSTRAINT t_sys_quality_category_config_pkey PRIMARY KEY (id)
);


-- public.t_sys_quality_plan definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan;

CREATE TABLE public.t_sys_quality_plan (
                                           id serial4 NOT NULL,
                                           plan_name varchar(255) NULL,
                                           production_line_id int4 NULL,
                                           remarks varchar(255) NULL,
                                           is_enabled varchar(255) NULL,
                                           create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                           create_user varchar(255) NULL,
                                           update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                           update_user varchar(255) NULL,
                                           production_line_name varchar(255) NULL,
                                           CONSTRAINT t_sys_quality_plan_pkey PRIMARY KEY (id)
);


-- public.t_sys_quality_plan_config definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan_config;

CREATE TABLE public.t_sys_quality_plan_config (
                                                  id serial4 NOT NULL,
                                                  plan_id int4 NULL,
                                                  category_id int4 NULL,
                                                  config_data text NULL,
                                                  create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                  create_user varchar(255) NULL,
                                                  update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                  update_user varchar(255) NULL,
                                                  inspection_item varchar(255) NULL,
                                                  key_process varchar(255) NULL,
                                                  key_process_name varchar(255) NULL,
                                                  monitoring_method varchar(255) NULL,
                                                  monitoring_method_name varchar(255) NULL,
                                                  CONSTRAINT t_sys_quality_plan_config_pkey PRIMARY KEY (id)
);


-- public.t_sys_quality_plan_judgment definition

-- Drop table

-- DROP TABLE public.t_sys_quality_plan_judgment;

CREATE TABLE public.t_sys_quality_plan_judgment (
                                                    id serial4 NOT NULL,
                                                    plan_id int4 NULL,
                                                    field_name varchar(255) NULL,
                                                    is_enabled varchar(255) NULL,
                                                    field_type varchar(255) NULL,
                                                    dropdown_fields varchar(255) NULL,
                                                    unit varchar(255) NULL,
                                                    is_required varchar(255) NULL,
                                                    create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                    create_user varchar(255) NULL,
                                                    update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
                                                    update_user varchar(255) NULL,
                                                    CONSTRAINT t_sys_quality_plan_judgment_pkey PRIMARY KEY (id)
);


ALTER TABLE public.t_sys_quality_plan ALTER COLUMN production_line_id TYPE varchar(255) USING production_line_id::varchar(255);


ALTER TABLE public.t_sys_quality_plan_config ADD standard text NULL;
