-- ----------------------------
-- Table structure for t_bus_order_bind_code
-- ----------------------------
CREATE TABLE "public"."t_bus_order_bind_code" (
"bind_code_id" int4 NOT NULL DEFAULT nextval('t_bus_order_bind_code_id_seq'::regclass),
"order_process_record_id" int4,
"order_process_id" int4,
"bind_code_type" varchar(255) COLLATE "pg_catalog"."default",
"order_no" varchar(32) COLLATE "pg_catalog"."default",
"process_id" int4,
"classs_id" int4,
"person_id" int4,
"bind_code_number" varchar(255) COLLATE "pg_catalog"."default",
"prev_order_no" varchar(255) COLLATE "pg_catalog"."default",
"prev_order_process_record_id" int4,
"prev_order_process_id" int4,
"bind_code_status" int4,
"created_time" timestamp(6),
"order_process_history_id" int4,
"prev_order_process_history_id" int4
)
;
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."bind_code_id" IS '扫码绑定信息表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."order_process_record_id" IS '订单报工结果表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."order_process_id" IS '订单工序执行表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."bind_code_type" IS '扫码类型，包括：报工扫码、接单扫码';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."order_no" IS '本工序订单号';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."process_id" IS '本工序id';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."classs_id" IS '本工序班组id';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."person_id" IS '本工序处理人';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."bind_code_number" IS '绑定料框编码';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."prev_order_no" IS '上道订单订单号';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."prev_order_process_record_id" IS '上道订单报工结果表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."prev_order_process_id" IS '上道订单工序执行表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."bind_code_status" IS '绑定状态：1.已绑定，2.已解绑';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."order_process_history_id" IS '订单报工历史记录表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."prev_order_process_history_id" IS '上道订单报工历史记录表ID';

-- ----------------------------
-- Primary Key structure for table t_bus_order_bind_code
-- ----------------------------
ALTER TABLE "public"."t_bus_order_bind_code" ADD CONSTRAINT "t_bus_order_bind_code_pkey" PRIMARY KEY ("bind_code_id");

COMMENT ON TABLE t_bus_order_bind_code IS '扫码绑定信息表';