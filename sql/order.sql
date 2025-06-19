-- 订单相关
CREATE TABLE public.t_bus_order_head (
                                         order_id serial NOT NULL,
                                         order_no varchar(255) NULL,
                                         order_status varchar(255) NULL,
                                         bill_no varchar(200) NULL,
                                         bill_date timestamp(6) NULL,
                                         bill_created_time timestamp(6) NULL,
                                         planner varchar(255) NULL,
                                         bill_type varchar(255) NULL,
                                         document_status varchar(255) NULL,
                                         bill_plan_qty numeric(25, 2) NULL DEFAULT 0,
                                         prd_org varchar(255) NULL,
                                         b_material_id int4 NULL,
                                         b_material_number varchar(255) NULL,
                                         b_material_name varchar(255) NULL,
                                         material_id int4 NULL,
                                         material_number varchar(255) NULL,
                                         material_name varchar(255) NULL,
                                         add_guo_qty numeric(25, 2) NULL DEFAULT 0,
                                         add_unit_weight numeric(25, 2) NULL DEFAULT 0,
                                         add_sum_weight numeric(25, 2) NULL DEFAULT 0,
                                         created_name varchar(255) NULL,
                                         created_time timestamp(6) NULL,
                                         updated_name varchar NULL,
                                         updated_time timestamp(6) NULL,
                                         current_process int4 NULL,
                                         class_id int4 NULL,
                                         craft_id int4 NULL,
                                         craft_desc varchar(255) NULL,
                                         body_lot varchar(255) NULL,
                                         body_prd_dept varchar(255) NULL,
                                         body_material_id int4 NULL,
                                         body_material_name varchar(255) NULL,
                                         body_material_number varchar(255) NULL,
                                         body_material_specification varchar(255) NULL,
                                         body_plan_prd_qty numeric(25, 2) NOT NULL DEFAULT 0,
                                         body_unit varchar(255) NULL,
                                         body_plan_start_date date NULL,
                                         body_plan_finish_date date NULL,
                                         erp_mo_fid int4 NULL,
                                         erp_mo_entry_id int4 NULL,
                                         body_product_type varchar(255) NULL,
                                         body_one_pot_qty numeric(25, 2) NULL,
                                         body_pot_qty int4 NULL,
                                         body_class_group varchar(255) NULL,
                                         CONSTRAINT t_bus_order_head_pkey3 PRIMARY KEY (order_id)
);
CREATE TABLE public.t_bus_order_head_ppbom (
                                               order_id int4 NOT NULL,
                                               order_ppbom_id int4 NULL,
                                               CONSTRAINT t_bus_order_body_ppbom_copy1_pkey PRIMARY KEY (order_id)
);
CREATE TABLE public.t_bus_order_ppbom (
                                          order_ppbom_id serial NOT NULL,
                                          order_id int4 NOT NULL,
                                          material_id int4 NULL,
                                          material_name varchar(255) NULL,
                                          material_number varchar(255) NULL,
                                          material_specification varchar(255) NULL,
                                          unit varchar(255) NULL,
                                          must_qty float8 NOT NULL DEFAULT 0,
                                          erp_ppbom_id int4 NULL,
                                          erp_ppbom_entry_id int4 NULL,
                                          ppbom_bill_no varchar(255) NULL,
                                          mo_bill_no varchar(255) NULL,
                                          CONSTRAINT t_bus_order_ppbom_pkey PRIMARY KEY (order_ppbom_id)
);
CREATE TABLE public.t_bus_order_ppbom_lk (
                                             order_id int4 NOT NULL,
                                             order_ppbom_id int4 NULL,
                                             CONSTRAINT t_bus_order_body_ppbom_pkey PRIMARY KEY (order_id)
);
CREATE UNIQUE INDEX uk_k0cupn394rdmfljej1cqvjf0v ON public.t_bus_order_ppbom_lk USING btree (order_ppbom_id);
CREATE TABLE public.t_bus_order_process (
                                            order_process_id SERIAL NOT NULL ,
                                            order_no varchar NULL,
                                            receive_time timestamp(6) NULL,
                                            finish_time timestamp(6) NULL,
                                            process_id int4 NULL,
                                            process_seq int4 NULL,
                                            elapsed_time float4 NULL,
                                            process_status varchar(255) NULL,
                                            suspend_reason varchar(255) NULL,
                                            class_id int4 NULL,
                                            person_id int4 NULL,
                                            CONSTRAINT t_bus_order_process_pkey PRIMARY KEY (order_process_id)
);
CREATE TABLE public.t_bus_order_process_history (
                                                    order_process_history_id SERIAL NOT NULL,
                                                    order_no varchar NULL,
                                                    bus_type varchar(255) NULL,
                                                    process_id int4 NULL,
                                                    process_number varchar(255) NULL,
                                                    process_name varchar(255) NULL,
                                                    class_id int4 NULL,
                                                    person_id int4 NULL,
                                                    device_id int4 NULL,
                                                    device_person_id int4 NULL,
                                                    record_type varchar(255) NULL,
                                                    record_unit varchar(255) NULL,
                                                    record_qty float4 NULL,
                                                    material_id int4 NULL,
                                                    material_number varchar(255) NULL,
                                                    material_name varchar(255) NULL,
                                                    report_time timestamp(6) NULL,
                                                    report_status varchar(255) NULL,
                                                    CONSTRAINT t_bus_order_process_history_pkey PRIMARY KEY (order_process_history_id)
);
CREATE TABLE public.t_bus_order_process_lk (
                                               order_id int4 NOT NULL DEFAULT 0,
                                               order_process_id int4 NOT NULL DEFAULT 0
);
CREATE TABLE public.t_bus_order_process_record (
                                                   order_process_record_id SERIAL NOT NULL,
                                                   order_process_id int4 NULL,
                                                   order_no varchar NULL,
                                                   bus_type varchar(255) NULL,
                                                   process_id int4 NULL,
                                                   process_number varchar(255) NULL,
                                                   process_name varchar(255) NULL,
                                                   class_id int4 NULL,
                                                   person_id int4 NULL,
                                                   device_id int4 NULL,
                                                   device_person_id int4 NULL,
                                                   record_type varchar(255) NULL,
                                                   record_unit varchar(255) NULL,
                                                   record_qty float4 NULL,
                                                   material_id int4 NULL,
                                                   material_number varchar(255) NULL,
                                                   material_name varchar(255) NULL,
                                                   CONSTRAINT t_bus_order_process_record_pkey PRIMARY KEY (order_process_record_id)
);
-- CREATE TABLE public.t_bus_order_process_record_lk (
--                                                       order_process_id int4 NOT NULL DEFAULT 0,
--                                                       order_process_record_id int4 NOT NULL DEFAULT 0
-- );
-- CREATE UNIQUE INDEX uk_8gc0kv2yoo6y0jsmt7a0nx6o5 ON public.t_bus_order_process_record_lk USING btree (order_process_record_id);

-- 2022/5/7新增字段
ALTER TABLE public.t_bus_order_head ADD mid_mo_modify_date timestamp NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_modify_date IS 'erp修改日期';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_seq int2 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_seq IS '明细行号';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_first_material_name varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_first_material_name IS 'A料-物料名称';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_first_material_number varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_first_material_number IS 'A料-物料编码';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_first_material_proportion float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_first_material_proportion IS 'A料-比例';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_second_material_proportion float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_second_material_proportion IS 'B料-比例';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_material_positive_error float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_material_positive_error IS '正误差';
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_material_negative_error float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_material_negative_error IS '差误差';
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_material_standard float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_material_standard IS '标准值';


ALTER TABLE IF EXISTS public.t_bus_order_process DROP COLUMN IF EXISTS old_order_process_id;

ALTER TABLE IF EXISTS public.t_bus_order_process
    ADD COLUMN old_order_process_id integer;
CREATE TABLE t_sync_order_log(
                                 id SERIAL NOT NULL,
                                 bill_no VARCHAR(255),
                                 sync_time timestamp,
                                 sync_status VARCHAR(1),
                                 sync_type varchar NULL,
                                 PRIMARY KEY (id)
);

COMMENT ON TABLE t_sync_order_log IS '订单同步日志表';
COMMENT ON COLUMN t_sync_order_log.id IS 'id';
COMMENT ON COLUMN t_sync_order_log.bill_no IS '单据编号';
COMMENT ON COLUMN t_sync_order_log.sync_time IS '同步时间0：成功 1：失败';
COMMENT ON COLUMN t_sync_order_log.sync_status IS '同步状态';
ALTER TABLE public.t_sync_order_log ADD sync_content varchar NULL;
COMMENT ON COLUMN public.t_sync_order_log.sync_content IS '内容描述';
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN order_id DROP NOT NULL;



--2022-05-09 增加产能单位，产能数量，二级类目编码
alter TABLE t_bus_order_process_record add record_type_l2 varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_record"."record_type_l2" IS '二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装';
alter TABLE t_bus_order_process_record add capacity_unit varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_record"."capacity_unit" IS '产能单位';
alter TABLE t_bus_order_process_record add capacity_qty float4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_record"."capacity_qty" IS '产能数量';

alter TABLE t_bus_order_process_history add record_type_l2 varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."record_type_l2" IS '二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装';
alter TABLE t_bus_order_process_history add capacity_unit varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."capacity_unit" IS '产能单位';
alter TABLE t_bus_order_process_history add capacity_qty float4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."capacity_qty" IS '产能数量';


INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (54, 'RECORDTYPEL20000', '二级类目类型', '1', '废膜', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (55, 'RECORDTYPEL20000', '二级类目类型', '2', '剩余膜', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (56, 'RECORDTYPEL20000', '二级类目类型', '3', '袋装', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (57, 'RECORDTYPEL20000', '二级类目类型', '4', '桶装', '1', NULL, NULL, NULL, NULL, 0);

--订单表头增加处理人
alter TABLE t_bus_order_head add current_person_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_head"."current_person_id" IS '处理人（操作人）';

--修改长日期
alter TABLE t_bus_order_head alter column body_plan_start_date timestamp(6) NULL;
alter TABLE t_bus_order_head alter column body_plan_finish_date timestamp(6) NULL;

alter TABLE t_bus_order_process_record add iot_collection_last_time timestamp(6) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_record"."capacity_qty" IS 'IOT最后取数时间';
alter TABLE t_bus_order_process_history add iot_collection_last_time timestamp(6) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."capacity_qty" IS 'IOT最后取数时间';

--2022/5/23 添加早晚班对应视图
CREATE OR REPLACE VIEW public.v_scheduling
AS SELECT '00:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '01:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '02:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '03:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '04:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '05:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '06:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '07:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '08:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '09:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '10:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '11:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '12:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '13:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '14:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '15:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '16:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '17:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '18:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '19:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '20:00'::text AS my_date,
                          'SCHEDULING0001'::text AS code
   UNION ALL
   SELECT '21:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '22:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code
   UNION ALL
   SELECT '23:00'::text AS my_date,
                          'SCHEDULING0002'::text AS code;


-- 订单物料表
CREATE TABLE t_sync_material(
                                id serial NOT NULL,
                                material_code VARCHAR(100) NOT NULL,
                                material_name VARCHAR(100),
                                material_unit VARCHAR(50),
                                group_code VARCHAR(255),
                                material_model VARCHAR(255),
                                br VARCHAR(255),
                                material_status VARCHAR(1),
                                created_time DATE,
                                created_name VARCHAR(255),
                                updated_time DATE,
                                updated_name VARCHAR(255),
                                PRIMARY KEY (id)
);

COMMENT ON TABLE t_sync_material IS '物料基础信息表';
COMMENT ON COLUMN t_sync_material.id IS 'id';
COMMENT ON COLUMN t_sync_material.material_code IS '物料编码';
COMMENT ON COLUMN t_sync_material.material_name IS '物料名称';
COMMENT ON COLUMN t_sync_material.material_unit IS '物料单位';
COMMENT ON COLUMN t_sync_material.group_code IS '分组编码';
COMMENT ON COLUMN t_sync_material.material_model IS '物料规格型号';
COMMENT ON COLUMN t_sync_material.br IS '备注';
COMMENT ON COLUMN t_sync_material.material_status IS '状态 0：启用 1：废弃';
COMMENT ON COLUMN t_sync_material.created_time IS '创建时间';
COMMENT ON COLUMN t_sync_material.created_name IS '创建人';
COMMENT ON COLUMN t_sync_material.updated_time IS '修改时间';
COMMENT ON COLUMN t_sync_material.updated_name IS '修改人';

--增加盘点类型 2022-5-31
alter TABLE t_bus_order_process_record add record_type_pd varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_record"."record_type_pd" IS '盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点';

alter TABLE t_bus_order_process_history add record_type_pd varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."record_type_pd" IS '盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点';


INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (102, 'STOCKTAKING0000', '盘点类型', 'STOCKTAKING0001', '交接班盘点', '1', '2022-05-31 17:52:13.739', 'www@qq.com', '2022-05-31 17:52:13.739', 'www@qq.com', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (103, 'STOCKTAKING0000', '盘点类型', 'STOCKTAKING0002', '订单完工盘点', '1', '2022-05-31 17:52:30.331', 'www@qq.com', '2022-05-31 17:52:30.331', 'www@qq.com', 0);


alter TABLE t_bus_order_process_history add order_process_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_process_id" IS '工序执行表ID';

alter TABLE t_bus_order_head add old_order_status varchar(255) NULL;
COMMENT ON COLUMN "public"."t_bus_order_head"."old_order_status" IS '原订单状态';

-- 2022-06-22
CREATE TABLE t_bus_order_update(
                                   id serial NOT NULL,
                                   created_time timestamp,
                                   order_id INTEGER,
                                   update_type VARCHAR(1),
                                   ppbom_ids VARCHAR(255),
                                   ppbom_json VARCHAR(3000),
                                   order_json VARCHAR(3000),
                                   PRIMARY KEY (id)
);

COMMENT ON TABLE t_bus_order_update IS '订单变更记录表';
COMMENT ON COLUMN t_bus_order_update.id IS 'id';
COMMENT ON COLUMN t_bus_order_update.created_time IS '变更时间';
COMMENT ON COLUMN t_bus_order_update.order_id IS '订单id';
COMMENT ON COLUMN t_bus_order_update.update_type IS '变更类型 0:新增物料 1:删除物料2:变更订单';
COMMENT ON COLUMN t_bus_order_update.ppbom_ids IS '变更的物料id集合，多个,隔开';
COMMENT ON COLUMN t_bus_order_update.ppbom_json IS '变更物料修改json';
COMMENT ON COLUMN t_bus_order_update.order_json IS '变更订单修改json';

-- 2022-06-07改动
ALTER TABLE public.t_bus_order_head ALTER COLUMN bill_plan_qty TYPE float4 USING bill_plan_qty::float4;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_guo_qty TYPE float4 USING add_guo_qty::float4;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_unit_weight TYPE float4 USING add_unit_weight::float4;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_sum_weight TYPE float4 USING add_sum_weight::float4;
ALTER TABLE public.t_bus_order_head ALTER COLUMN body_one_pot_qty TYPE float4 USING body_one_pot_qty::float4;
ALTER TABLE public.t_bus_order_head ALTER COLUMN body_plan_prd_qty TYPE float4 USING body_plan_prd_qty::float4;


COMMENT ON TABLE t_bus_order_head IS '订单信息主表';
COMMENT ON TABLE t_bus_order_ppbom IS '订单用料清单表';
COMMENT ON TABLE t_bus_order_process IS '订单工序执行表';
COMMENT ON TABLE t_bus_order_process_record IS '订单报工/盘点结果表';
COMMENT ON TABLE t_bus_order_process_history IS '订单报工/盘点历史记录表';

-- 2022-06-29 报工盘点增加订单用料清单ID
alter TABLE t_bus_order_process_history add order_ppbom_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_ppbom_id" IS '订单用料清单ID';
alter TABLE t_bus_order_process_record add order_ppbom_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_ppbom_id" IS '订单用料清单ID';

-- 2022-07-08 历史记录表增加报工结果表id，方便删除处理
alter TABLE t_bus_order_process_history add order_process_record_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_process_record_id" IS '订单报工/盘点结果表ID';

-- 2022/7/4
ALTER TABLE public.t_bus_order_head ADD is_deleted varchar NULL DEFAULT 1;
COMMENT ON COLUMN public.t_bus_order_head.is_deleted IS '是否删除 0：删除 1：非删除';

-- 2022-07-04
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_first_material_max_value float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_first_material_max_value IS '每锅A料添加最大值';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_second_material_max_value float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_second_material_max_value IS '每锅B料添加最大值';

-- 2022-07-04 字典增加二级类目类型、投入二级品（一级类目）
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (112, 'GROUPCODE0000', '字典分类', 'RECORDL20000', '投入二级品（一级类目）', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (76, 'GROUPCODE0000', '字典分类', 'RECORDTYPEL20000', '二级类目类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);

INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (109, 'RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20001', 'A料', '1', '2022-07-04 16:18:43.959', 'tenant@thingsboard.org', '2022-07-04 16:31:24.801', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (111, 'RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20003', '废料', '1', '2022-07-04 16:19:18.982', 'tenant@thingsboard.org', '2022-07-04 16:31:36.587', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (110, 'RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20002', 'B料', '1', '2022-07-04 16:18:59.097', 'tenant@thingsboard.org', '2022-07-04 16:31:31.903', 'tenant@thingsboard.org', 0);

INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (113, 'RECORDL20000', '投入二级品（一级类目）', 'RECORDL20001', 'A料-本订单', '1', '2022-07-04 16:28:57.022', 'tenant@thingsboard.org', '2022-07-04 16:29:47.744', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (116, 'RECORDL20000', '投入二级品（一级类目）', 'RECORDL20004', 'B料-其他订单', '1', '2022-07-04 16:29:40.094', 'tenant@thingsboard.org', '2022-07-04 16:30:08.022', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (114, 'RECORDL20000', '投入二级品（一级类目）', 'RECORDL20002', 'A料-其他订单', '1', '2022-07-04 16:29:08.035', 'tenant@thingsboard.org', '2022-07-04 16:29:54.973', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc"("code_id", "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES (115, 'RECORDL20000', '投入二级品（一级类目）', 'RECORDL20003', 'B料-本订单', '1', '2022-07-04 16:29:27.335', 'tenant@thingsboard.org', '2022-07-04 16:30:01.037', 'tenant@thingsboard.org', 0);


-- 2022-7-6
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_mes_unit VARCHAR(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_mes_unit IS 'mes单位';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_devept_unit VARCHAR(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_devept_unit IS '开发单位';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_mes_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_mes_qty IS 'mes数量';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_devept_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_devept_qty IS '开发数量';

-- 2022-07-07
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_bom_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_bom_id IS 'bomId';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_bom_number varchar(100) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_bom_number IS 'bom编号';

-- 2022-07-11
ALTER TABLE public.t_bus_order_ppbom add mid_ppbom_entry_is_into int4 NOT NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_is_into IS '是否需要投入';
-- ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN must_qty TYPE float4 USING must_qty::float4;
ALTER TABLE public.t_bus_order_process_record ADD import_pot float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_record.import_pot IS '投入锅数';
ALTER TABLE public.t_bus_order_process_record ADD export_pot float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_record.export_pot IS '产出锅数';
ALTER TABLE public.t_bus_order_process_record ADD export_pot_min float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_record.export_pot_min IS '产出斗数';


CREATE TABLE t_bus_order_process_pot(
                                        id SERIAL NOT NULL,
                                        same_flag INTEGER,
                                        order_process_id INTEGER,
                                        device_id INTEGER,
                                        device_person_Id INTEGER,
                                        order_ppbom_id INTEGER,
                                        created_time timestamp(6),
                                        created_name VARCHAR(255),
                                        PRIMARY KEY (id)
);

COMMENT ON TABLE t_bus_order_process_pot IS '报工流水表';
COMMENT ON COLUMN t_bus_order_process_pot.id IS 'id';
COMMENT ON COLUMN t_bus_order_process_pot.same_flag IS '同一锅标识';
COMMENT ON COLUMN t_bus_order_process_pot.order_process_id IS '工序执行表id';
COMMENT ON COLUMN t_bus_order_process_pot.device_id IS '机台号';
COMMENT ON COLUMN t_bus_order_process_pot.device_person_Id IS '机排手';
COMMENT ON COLUMN t_bus_order_process_pot.order_ppbom_id IS '订单用料清单ID';
COMMENT ON COLUMN t_bus_order_process_pot.created_time IS '处理时间';
COMMENT ON COLUMN t_bus_order_process_pot.created_name IS '处理人';
ALTER TABLE public.t_bus_order_head alter must_qty float4 NOT NULL DEFAULT 0,

ALTER TABLE public.t_bus_order_head ADD order_finish_date timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_finish_date IS '完工时间';
ALTER TABLE public.t_bus_order_head ADD order_pending_date timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_pending_date IS '挂起时间';
ALTER TABLE public.t_bus_order_head ADD order_pending_desc varchar(100) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_pending_desc IS '挂起备注';

-- 2022/7/14
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_item_type int2 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_item_type IS '子项类型';
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_use_rate float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_use_rate IS '使用比例';

ALTER TABLE public.t_bus_order_ppbom add mid_ppbom_entry_input_process varchar(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_input_process IS '投入工序';

-- 2022/8/4
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_replace_group int4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_replace_group IS '替换料组';
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_bom_number varchar NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_bom_number IS 'bom编码';

-- 2022/8/23
ALTER TABLE public.t_bus_order_head ALTER COLUMN bill_plan_qty TYPE numeric(19, 9) USING bill_plan_qty::numeric;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_guo_qty TYPE numeric(19, 9) USING add_guo_qty::numeric;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_unit_weight TYPE numeric(19, 9) USING add_unit_weight::numeric;
ALTER TABLE public.t_bus_order_head ALTER COLUMN body_plan_prd_qty TYPE numeric(19, 9) USING body_plan_prd_qty::numeric;
ALTER TABLE public.t_bus_order_head ALTER COLUMN add_sum_weight TYPE numeric(19, 9) USING add_sum_weight::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_material_positive_error TYPE numeric(19, 9) USING mid_ppbom_entry_material_positive_error::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_material_negative_error TYPE numeric(19, 9) USING mid_ppbom_entry_material_negative_error::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_material_standard TYPE numeric(19, 9) USING mid_ppbom_entry_material_standard::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_use_rate TYPE numeric(19, 9) USING mid_ppbom_entry_use_rate::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_denominator TYPE numeric(19, 9) USING mid_ppbom_entry_denominator::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_weigh_devept_qty TYPE numeric(19, 9) USING mid_ppbom_entry_weigh_devept_qty::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN mid_ppbom_entry_weigh_mes_qty TYPE numeric(19, 9) USING mid_ppbom_entry_weigh_mes_qty::numeric;
ALTER TABLE public.t_bus_order_ppbom ALTER COLUMN must_qty TYPE numeric(19, 9) USING must_qty::numeric;

-- 2022/9/20
ALTER TABLE public.t_sys_message_order ADD order_process_id int4 NULL;
COMMENT ON COLUMN public.t_sys_message_order.order_process_id IS '工序执行表ID';
ALTER TABLE public.t_sys_message_order ADD remark varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_message_order.remark IS '备注';

ALTER TABLE public.t_bus_order_process ADD old_process_status varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_process.old_process_status IS '原工序状态';

