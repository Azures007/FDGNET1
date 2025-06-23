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
