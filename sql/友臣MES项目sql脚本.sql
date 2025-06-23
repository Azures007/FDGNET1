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