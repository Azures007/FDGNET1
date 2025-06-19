-- 订单表
CREATE TABLE public.mid_mo (
	mid_mo_id serial NOT NULL,
	mid_mo_bill_id int4 NULL,
	mid_mo_bill_number varchar(30) NULL,
	mid_mo_date timestamp(0) NULL,
	mid_mo_create_date timestamp(0) NULL,
	mid_mo_modify_date timestamp(0) NULL,
	mid_mol_type_id varchar(150) NULL,
	mid_mo_type_name varchar(255) NULL,
	mid_mo_org_id int4 NULL,
	mid_mo_org_name varchar(20) NULL,
	gmt_create timestamp(0) NULL,
	gmt_modified timestamp(0) NULL,
	is_delete int2 NULL,
	uuid varchar(50) NULL,
	error_message text NULL,
	error_time timestamp NULL,
	CONSTRAINT mid_mo_pkey PRIMARY KEY (mid_mo_id)
);

-- 订单详情表
CREATE TABLE public.mid_mo_entry (
	mid_mo_entry_id serial NOT NULL,
	mid_mo_id int4 NULL,
	mid_mo_entry_seq int2 NULL,
	mid_mo_entry_each_pot_weight float4 NULL,
	mid_mo_entry_pot_num int4 NULL,
	mid_mo_entry_first_material_name varchar(20) NULL,
	mid_mo_entry_first_material_number varchar(30) NULL,
	mid_mo_entry_first_material_proportion float4 NULL DEFAULT 0,
	mid_mo_entry_second_material_name varchar(20) NULL,
	mid_mo_entry_second_material_number varchar(30) NULL,
	mid_mo_entry_second_material_proportion float4 NULL,
	mid_mo_entry_team_id int4 NULL,
	mid_mo_entry_team_name varchar(20) NULL,
	mid_mo_entry_lot_id int4 NULL,
	mid_mo_entry_lot_value varchar(40) NULL,
	mid_mo_entry_work_shop_id int4 NULL,
	mid_mo_entry_work_shop_name varchar(20) NULL,
	mid_mo_entry_material_id int4 NULL,
	mid_mo_entry_material_name varchar(30) NULL,
	mid_mo_entry_material_number varchar(30) NULL,
	mid_mo_entry_material_spec varchar(100) NULL,
	mid_mo_entry_plan_num float4 NULL,
	mid_mo_entry_unit_id int4 NULL,
	mid_mo_entry_unit_name varchar(40) NULL,
	mid_mo_entry_unit_number varchar(40) NULL,
	mid_mo_entry_plan_start_date timestamp(0) NULL,
	mid_mo_entry_plan_finish_date timestamp(0) NULL,
	mid_mo_entry_sale_order_number varchar(40) NULL,
	gmt_create timestamp(0) NULL,
	gmt_modified timestamp(0) NULL,
	is_delete int2 NULL,
	uuid varchar(50) NULL,
	CONSTRAINT mid_mo_entry_pkey PRIMARY KEY (mid_mo_entry_id)
);

-- 生产用料清单表
CREATE TABLE public.mid_ppbom (
	mid_ppbom_id serial NOT NULL,
	mid_mo_entry_id int4 NULL,
	mid_ppbom_bill_number varchar(50) NULL,
	mid_mo_bill_number varchar(40) NULL,
	mid_mo_seq int4 NULL,
	mid_ppbom_material_name varchar(40) NULL,
	mid_ppbom_material_number varchar(40) NULL,
	mid_ppbom_unit_name varchar(40) NULL,
	mid_ppbom_unit_number varchar(40) NULL,
	gmt_create timestamp(0) NULL,
	gmt_modified timestamp(0) NULL,
	is_delete int2 NULL,
	mid_mo_entry_create_date timestamp NULL,
	mid_mo_entry_modify_date timestamp NULL,
	uuid varchar(50) NULL,
	CONSTRAINT mid_ppbom_pkey PRIMARY KEY (mid_ppbom_id)
);

-- 生产用料清单明细表
CREATE TABLE public.mid_ppbom_entry (
	mid_ppbom_entry_id serial NOT NULL,
	mid_ppbom_id int4 NULL,
	mid_ppbom_entry_material_name varchar(40) NULL,
	mid_ppbom_entry_material_number varchar(40) NULL,
	mid_ppbom_entry_unit_name varchar(40) NULL,
	mid_ppbom_entry_unit_number varchar(40) NULL,
	mid_ppbom_entry_material_positive_error float4 NULL,
	mid_ppbom_entry_material_negative_error float4 NULL,
	mid_ppbom_entry_material_standard float4 NULL,
	gmt_create timestamp NULL,
	gmt_modified timestamp NULL,
	is_delete int2 NULL,
	uuid varchar(50) NULL,
	CONSTRAINT mid_ppbom_entry_pkey PRIMARY KEY (mid_ppbom_entry_id)
);

-- 成品物料表
-- DROP TABLE public.mid_material;

CREATE TABLE public.mid_material (
	kd_material_id int4 NULL,
	kd_material_number varchar(50) NULL,
	kd_material_name varchar(100) NULL,
	kd_material_spec varchar(255) NULL,
	kd_material_modify_date_time timestamp(0) NULL,
	gmt_create timestamp(0) NULL,
	gmt_modified timestamp(0) NULL,
	is_delete int2 NULL,
	kd_material_modify_create_time timestamp(0) NULL,
	kd_material_membrane_thickness float4 NULL,
	kd_material_membrane_width float4 NULL,
	kd_material_membrane_density float4 NULL,
	kd_material_per_weight float4 NULL,
	kd_material_net_weight float4 NULL,
	mid_material_id int4 NULL DEFAULT nextval('mid_materia_mid_material_id_seq'::regclass),
	kd_material_doc_status varchar(5) NULL,
	kd_material_disable_status varchar(5) NULL,
	kd_material_unit_id int4 NULL,
	kd_material_unit_name varchar(40) NULL,
	kd_material_unit_number varchar(40) NULL
);

ALTER TABLE public.mid_mo_entry ADD mid_mo_entry_bill_id int4 NULL;
COMMENT ON COLUMN mid_mo_entry.mid_mo_entry_bill_id IS '生产明细Id';

ALTER TABLE public.mid_ppbom ADD mid_ppbom_bill_id int4 NULL;
COMMENT ON COLUMN public.mid_ppbom.mid_ppbom_bill_id IS '金蝶用料清单主体Id';

ALTER TABLE public.mid_ppbom_entry ADD mid_ppbom_entry_bill_id int4 NULL;
COMMENT ON COLUMN public.mid_ppbom_entry.mid_ppbom_entry_bill_id IS '金蝶用料清单明细Id';

-- 2022/5/7
ALTER TABLE public.mid_mo ADD mid_mo_bill_status varchar(5) NULL;
COMMENT ON COLUMN public.mid_mo.mid_mo_bill_status IS '单据状态';
ALTER TABLE public.mid_mo_entry ADD mid_mo_entry_service_status varchar(20) NULL;
COMMENT ON COLUMN public.mid_mo_entry.mid_mo_entry_service_status IS '业务状态';
ALTER TABLE public.mid_ppbom ADD mid_ppbom_material_id int4 NULL;
COMMENT ON COLUMN public.mid_ppbom.mid_ppbom_material_id IS '物料-单位-Id';
ALTER TABLE public.mid_ppbom_entry ADD mid_ppbom_entry_must_qty float8 NULL;
COMMENT ON COLUMN public.mid_ppbom_entry.mid_ppbom_entry_must_qty IS '应发数量';
ALTER TABLE public.mid_ppbom_entry ADD mid_ppbom_entry_material_id int4 NULL;
COMMENT ON COLUMN public.mid_ppbom_entry.mid_ppbom_entry_material_id IS '物料ID';

-- 2022/6/2
ALTER TABLE public.mid_material ADD kd_material_use_org_id int4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_use_org_id IS '使用组织id';
ALTER TABLE public.mid_material ADD kd_material_use_org_number varchar NULL;
COMMENT ON COLUMN public.mid_material.kd_material_use_org_number IS '使用组织编码';
ALTER TABLE public.mid_material ADD kd_material_use_org_name varchar NULL;
COMMENT ON COLUMN public.mid_material.kd_material_use_org_name IS '使用组织名称';

-- 2022/6/9
ALTER TABLE public.mid_material ADD kd_material_group varchar NULL;
COMMENT ON COLUMN public.mid_material.kd_material_group IS '物料编号前4位';

-- 2022/7/6
CREATE TABLE public.mid_org (
	mid_org_id serial NOT NULL ,
	kd_org_id int4 NULL,
	kd_org_name varchar(100) NULL,
	kd_org_num varchar(100) NULL,
	gmt_create timestamp NULL,
	gmt_modified timestamp NULL,
	is_delete int2 NULL,
	CONSTRAINT mid_org_pkey PRIMARY KEY (mid_org_id)
);

CREATE TABLE public.mid_dept (
	mid_dept_id serial NOT NULL,
	kd_dept_id int4 NULL,
	kd_dept_name varchar(100) NULL,
	kd_dept_num varchar(100) NULL,
	kd_org_num varchar(100) NULL,
	gmt_create timestamp NULL,
	gmt_modified timestamp NULL,
	is_delete int2 NULL,
	CONSTRAINT mid_dept_pkey PRIMARY KEY (mid_dept_id)
);

-- 2022/8/5
ALTER TABLE public.mid_material ADD kd_material_props_id varchar NULL;
COMMENT ON COLUMN public.mid_material.kd_material_props_id IS '物料属性';



