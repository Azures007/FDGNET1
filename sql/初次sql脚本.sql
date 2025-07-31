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
	mid_material_id serial NOT NULL,
	kd_material_doc_status varchar(5) NULL,
	kd_material_disable_status varchar(5) NULL,
	kd_material_unit_id int4 NULL,
	kd_material_unit_name varchar NULL,
	kd_material_unit_number varchar NULL,
	kd_material_use_org_id int4 NULL,
	kd_material_use_org_number varchar NULL,
	kd_material_use_org_name varchar NULL,
	kd_material_group varchar NULL
);

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
	mid_mo_entry_bill_id int4 NULL,
	CONSTRAINT mid_mo_entry_pkey PRIMARY KEY (mid_mo_entry_id)
);

CREATE TABLE public.mid_mo_entry_status (
	mid_mo_entry_status_id serial NOT NULL,
	mid_mo_entry_status_name varchar(20) NULL,
	mid_mo_entry_status_value int2 NULL,
	mid_mo_entry_status_pending_name varchar(20) NULL,
	mid_mo_entry_status_pending_value int2 NULL,
	gmt_create timestamp(6) NULL,
	gmt_modified timestamp(6) NULL,
	is_delete int2 NULL,
	mid_mo_entry_id int4 NULL,
	mid_mo_bill_number varchar(32) NULL,
	mid_mo_entry_seq int2 NULL,
	mid_mo_entry_pending_case text NULL,
	CONSTRAINT mid_mo_entry_status_pkey PRIMARY KEY (mid_mo_entry_status_id)
);

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
	mid_ppbom_bill_id int4 NULL,
	CONSTRAINT mid_ppbom_pkey PRIMARY KEY (mid_ppbom_id)
);

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
	mid_ppbom_entry_bill_id int4 NULL,
	CONSTRAINT mid_ppbom_entry_pkey PRIMARY KEY (mid_ppbom_entry_id)
);

CREATE TABLE public.t_bus_order_bind_code (
    bind_code_id serial NOT NULL,
	order_process_record_id int4 NULL,
	order_process_id int4 NULL,
	bind_code_type varchar(255) NULL,
	order_no varchar(32) NULL,
	process_id int4 NULL,
	classs_id int4 NULL,
	person_id int4 NULL,
	bind_code_number varchar(255) NULL,
	prev_order_no varchar(255) NULL,
	prev_order_process_record_id int4 NULL,
	prev_order_process_id int4 NULL,
	bind_code_status int4 NULL,
	created_time timestamp(6) NULL,
	CONSTRAINT t_bus_order_bind_code_pkey PRIMARY KEY (bind_code_id)
);

alter TABLE t_bus_order_bind_code add"order_process_history_id" int4;
alter TABLE t_bus_order_bind_code add "prev_order_process_history_id" int4;
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."order_process_history_id" IS '订单报工历史记录表ID';
COMMENT ON COLUMN "public"."t_bus_order_bind_code"."prev_order_process_history_id" IS '上道订单报工历史记录表ID';

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
	body_plan_start_date timestamp(0) NULL,
	body_plan_finish_date timestamp(0) NULL,
	erp_mo_fid int4 NULL,
	erp_mo_entry_id int4 NULL,
	body_product_type varchar(255) NULL,
	body_one_pot_qty numeric(25, 2) NULL,
	body_pot_qty int4 NULL,
	body_class_group varchar(255) NULL,
	mid_mo_modify_date timestamp(6) NULL,
	mid_mo_entry_seq int2 NULL,
	mid_mo_entry_first_material_name varchar NULL,
	mid_mo_entry_first_material_number varchar NULL,
	mid_mo_entry_first_material_proportion float4 NULL,
	mid_mo_entry_second_material_proportion float4 NULL,
	current_person_id int4 NULL,
	old_order_status varchar(255) NULL,
	CONSTRAINT t_bus_order_head_pkey3 PRIMARY KEY (order_id),
	CONSTRAINT fk79q94wvcyr5nc0vndlj48q0tt FOREIGN KEY (class_id) REFERENCES public.t_sys_class(class_id),
	CONSTRAINT fkjjt0p4eashkgk8cal4qhjksaw FOREIGN KEY (craft_id) REFERENCES public.t_sys_craft_info(craft_id),
	CONSTRAINT fkq7msvrh5cvfxw1pfen8ufgak4 FOREIGN KEY (current_process) REFERENCES public.t_sys_process_info(process_id)
);

CREATE TABLE public.t_bus_order_ppbom (
	order_ppbom_id serial NOT NULL,
	order_id int4 NULL,
	material_id int4 NULL,
	material_name varchar(255) NULL,
	material_number varchar(255) NULL,
	material_specification varchar(255) NULL,
	unit varchar(255) NULL,
	must_qty numeric(25, 2) NOT NULL DEFAULT 0,
	erp_ppbom_id int4 NULL,
	erp_ppbom_entry_id int4 NULL,
	ppbom_bill_no varchar(255) NULL,
	mo_bill_no varchar(255) NULL,
	mid_ppbom_entry_material_positive_error float4 NULL,
	mid_ppbom_entry_material_negative_error float4 NULL,
	mid_ppbom_entry_material_standard float4 NULL,
	CONSTRAINT t_bus_order_ppbom_pkey PRIMARY KEY (order_ppbom_id)
);


CREATE TABLE public.t_bus_order_ppbom_lk (
	order_id int4 NULL,
	order_ppbom_id int4 NULL,
	CONSTRAINT uk_k0cupn394rdmfljej1cqvjf0v UNIQUE (order_ppbom_id),
	CONSTRAINT fk2n4r2n1lhgpnobn3mj3f2w89p FOREIGN KEY (order_id) REFERENCES public.t_bus_order_head(order_id),
	CONSTRAINT fkk9ok7s9awopp6tx94ii0xps3l FOREIGN KEY (order_ppbom_id) REFERENCES public.t_bus_order_ppbom(order_ppbom_id)
);

CREATE TABLE public.t_bus_order_process (
	order_process_id serial NOT NULL,
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
	"type" varchar(255) NULL,
	old_order_process_id int4 NULL,
	CONSTRAINT t_bus_order_process_pkey PRIMARY KEY (order_process_id),
	CONSTRAINT fkihjmhf13fgqx2uxvjb32tqv1i FOREIGN KEY (class_id) REFERENCES public.t_sys_class(class_id),
	CONSTRAINT fkqtqhswpiujung0va03gh6ut5y FOREIGN KEY (process_id) REFERENCES public.t_sys_process_info(process_id),
	CONSTRAINT fkrhr8dfvqklojtg0q4f7jawnfj FOREIGN KEY (person_id) REFERENCES public.t_sys_personnel_info(personnel_id)
);
CREATE TABLE public.t_bus_order_process_history (
	order_process_history_id serial NOT NULL,
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
	body_lot varchar(255) NULL,
	stock_count int4 NULL DEFAULT 1,
	record_type_l2 varchar(255) NULL,
	capacity_unit varchar(255) NULL,
	capacity_qty float4 NULL,
	iot_collection_last_time timestamp(6) NULL,
	record_type_pd varchar(255) NULL,
	order_process_id int4 NULL,
	CONSTRAINT t_bus_order_process_history_pkey PRIMARY KEY (order_process_history_id),
	CONSTRAINT fkamvs0vwawrqdt3isln5coandh FOREIGN KEY (class_id) REFERENCES public.t_sys_class(class_id),
	CONSTRAINT fke7s4lp0wh21bfwgy2eytrfw9t FOREIGN KEY (device_id) REFERENCES public.t_sys_device(device_id),
	CONSTRAINT fkeb561d94ubaroaqv6f61vsgg3 FOREIGN KEY (person_id) REFERENCES public.t_sys_personnel_info(personnel_id),
	CONSTRAINT fkj5y987o8v32r6iq9skhfus791 FOREIGN KEY (process_id) REFERENCES public.t_sys_process_info(process_id),
	CONSTRAINT fkoujc04mn9oxpi8k1bbqxy2y26 FOREIGN KEY (device_person_id) REFERENCES public.t_sys_personnel_info(personnel_id)
);
CREATE TABLE public.t_bus_order_process_lk (
	order_id int4 NOT NULL DEFAULT 0,
	order_process_id int4 NOT NULL DEFAULT 0,
	CONSTRAINT uk_60vtw3nnn92bmx5a2rli3w330 UNIQUE (order_process_id),
	CONSTRAINT fk53j7e612t31ikosg1ujfejiuw FOREIGN KEY (order_id) REFERENCES public.t_bus_order_head(order_id),
	CONSTRAINT fkcl1a699456d3agbyley138vdm FOREIGN KEY (order_process_id) REFERENCES public.t_bus_order_process(order_process_id)
);


CREATE TABLE public.t_bus_order_process_record (
	order_process_record_id serial NOT NULL,
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
	body_lot varchar(255) NULL,
	report_time timestamp(6) NULL,
	record_type_l2 varchar(255) NULL,
	capacity_unit varchar(255) NULL,
	capacity_qty float4 NULL,
	iot_collection_last_time timestamp(6) NULL,
	record_type_pd varchar(255) NULL,
	CONSTRAINT t_bus_order_process_record_pkey PRIMARY KEY (order_process_record_id),
	CONSTRAINT fkl091cmoiip50ygm5wm4rhbn71 FOREIGN KEY (device_id) REFERENCES public.t_sys_device(device_id),
	CONSTRAINT fkn92el27ojt02xyn3dxcqw7jxs FOREIGN KEY (class_id) REFERENCES public.t_sys_class(class_id),
	CONSTRAINT fkpvgb8aqwat916inhvl4jruga6 FOREIGN KEY (device_person_id) REFERENCES public.t_sys_personnel_info(personnel_id),
	CONSTRAINT fkt4djfbl7ygk9js58pxp3190kd FOREIGN KEY (person_id) REFERENCES public.t_sys_personnel_info(personnel_id),
	CONSTRAINT fkthfjlfcew5hgqohua8xfy5kiw FOREIGN KEY (process_id) REFERENCES public.t_sys_process_info(process_id)
);

CREATE TABLE public.t_sync_material (
	id serial NOT NULL,
	material_code varchar(100) NOT NULL,
	material_name varchar(100) NULL,
	material_unit varchar(50) NULL,
	group_code varchar(255) NULL,
	material_model varchar(255) NULL,
	br varchar(255) NULL,
	material_status varchar(1) NULL,
	created_time date NULL,
	created_name varchar(255) NULL,
	updated_time date NULL,
	updated_name varchar(255) NULL,
	CONSTRAINT t_sync_material_pkey PRIMARY KEY (id)
);
CREATE TABLE public.t_sync_order_log (
	id serial NOT NULL,
	bill_no varchar(255) NULL,
	sync_time timestamp NULL,
	sync_status varchar(1) NULL,
	sync_content varchar NULL,
	sync_type varchar NULL,
	CONSTRAINT t_sync_order_log_pkey PRIMARY KEY (id)
);

CREATE TABLE public.t_sys_charging_basket (
	charging_basket_id serial NOT NULL,
	code varchar(15) NULL,
	br varchar(255) NULL,
	charging_basket_size varchar(50) NULL,
	charging_basket_status varchar(1) NULL,
	max_bearing varchar(50) NULL,
	created_name varchar(100) NULL,
	created_time date NULL,
	updated_name varchar(100) NULL,
	updated_time date NULL,
	weight float4 NULL,
	CONSTRAINT t_sys_charging_basket_pkey PRIMARY KEY (charging_basket_id)
);


CREATE TABLE public.t_sys_class (
	"name" varchar(255) NULL,
	process varchar(255) NULL,
	group_leader varchar(255) NULL,
	scheduling varchar(255) NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	class_id serial NOT NULL,
	enabled_st varchar NULL DEFAULT 0,
	team_num varchar NULL,
	group_leader_id varchar(255) NULL,
	scheduling_code_dsc varchar(255) NULL,
	CONSTRAINT t_sys_class_pkey PRIMARY KEY (class_id)
);

CREATE TABLE public.t_sys_class_group_leader_rel (
	class_group_leader_id serial NOT NULL,
	class_id int4 NULL,
	personnel_id int4 NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	CONSTRAINT t_sys_class_group_leader_rel_pkey PRIMARY KEY (class_group_leader_id)
);


CREATE TABLE public.t_sys_class_personnel_rel (
	class_personnel_id serial NOT NULL,
	class_id int4 NULL,
	personnel_id int4 NULL,
	crt_user varchar(255) NULL,
	crt_time timestamp NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	CONSTRAINT t_sys_class_personnel_rel_pkey PRIMARY KEY (class_personnel_id)
);

CREATE TABLE public.t_sys_code_dsc (
	code_id serial NOT NULL,
	code_cl_id varchar(255) NULL,
	code_cl_dsc varchar(255) NULL,
	code_value varchar(255) NULL,
	code_dsc varchar(255) NULL,
	enabled_st varchar(2) NULL DEFAULT 0,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	is_group int4 NULL,
	CONSTRAINT t_sys_code_dsc_pkey PRIMARY KEY (code_id)
);

CREATE TABLE public.t_sys_craft_info (
	craft_id serial NOT NULL,
	craft_detail varchar(255) NULL,
	craft_name varchar(255) NULL,
	craft_number varchar(255) NULL,
	created_time timestamp NULL,
	created_user varchar(255) NULL,
	effective_time timestamp NULL,
	enabled int4 NULL,
	failure_time timestamp NULL,
	updated_time timestamp NULL,
	updated_user varchar(255) NULL,
	CONSTRAINT t_sys_craft_info_pkey PRIMARY KEY (craft_id)
);

CREATE TABLE public.t_sys_craft_material_rel (
	craft_material_id serial NOT NULL,
	craft_id int4 NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	material_code varchar(100) NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	CONSTRAINT t_sys_craft_material_rel_pkey PRIMARY KEY (craft_material_id)
);


CREATE TABLE public.t_sys_craft_process_rel (
	craft_process_id serial NOT NULL,
	craft_id int4 NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	process_id int4 NULL,
	sort int4 NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	is_reporting_bind_code int4 NULL,
	is_receiving_bind_code int4 NULL,
	is_receiving_unbind_code int4 NULL,
	CONSTRAINT t_sys_craft_process_rel_pkey PRIMARY KEY (craft_process_id)
);


CREATE TABLE public.t_sys_device (
	device_id serial NOT NULL,
	created_time timestamp NULL,
	created_user varchar(255) NULL,
	device_name varchar(255) NULL,
	device_number varchar(255) NULL,
	disabled_time timestamp NULL,
	disabled_user varchar(255) NULL,
	enable_time timestamp NULL,
	enable_user varchar(255) NULL,
	enabled varchar(2) NULL DEFAULT 0,
	status int4 NULL,
	updated_user varchar(255) NULL,
	updated_time timestamp NULL,
	workshop varchar(255) NULL,
	note varchar(1000) NULL,
	workshop_no varchar NULL,
	CONSTRAINT t_sys_device_pkey PRIMARY KEY (device_id)
);

CREATE TABLE public.t_sys_log (
	id serial NOT NULL,
	methods varchar(50) NULL,
	way varchar(100) NULL,
	in_param varchar(2000) NULL,
	ip varchar(20) NULL,
	created_name varchar(50) NULL,
	created_time timestamp NULL,
	created_username varchar(50) NULL,
	user_id varchar(100) NULL,
	CONSTRAINT t_sys_log_pkey PRIMARY KEY (id)
);

CREATE TABLE public.t_sys_menu (
	menu_id serial NOT NULL,
	menu_name varchar(50) NOT NULL,
	menu_explain varchar(255) NULL,
	"path" varchar(50) NOT NULL,
	flag varchar(1) NULL DEFAULT 0,
	parent_id int4 NOT NULL DEFAULT 0,
	enabled varchar(1) NOT NULL DEFAULT 0,
	created_time date NULL,
	created_name varchar(50) NULL,
	updated_time date NULL,
	updated_name varchar(50) NULL,
	menu_type varchar NULL,
	CONSTRAINT t_sys_menu_pkey PRIMARY KEY (menu_id)
);

CREATE TABLE public.t_sys_message_order (
	id serial NOT NULL,
	order_id int4 NOT NULL,
	order_type int4 NULL,
	order_no varchar(100) NULL,
	created_time timestamp NULL,
	product_standard varchar(255) NULL,
	bill_plan_qty varchar(255) NULL,
	bot_message varchar(255) NULL,
	mes_type varchar(255) NULL,
	status_type varchar(255) NULL,
	is_read varchar(255) NULL,
	mes_time timestamp NULL,
	user_id varchar(255) NULL,
	CONSTRAINT t_sys_message_order_pkey PRIMARY KEY (id)
);

CREATE TABLE public.t_sys_personnel_info (
	personnel_id serial NOT NULL,
	"name" varchar(255) NULL,
	sex varchar(5) NULL,
	phone varchar(64) NULL,
	station varchar(255) NULL,
	enabled_st varchar(2) NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	class_name varchar(255) NULL,
	user_id varchar(255) NULL,
	user_email varchar(255) NULL,
	CONSTRAINT t_sys_personnel_info_pkey PRIMARY KEY (personnel_id)
);


CREATE TABLE public.t_sys_process_class_rel (
	process_class_id serial NOT NULL,
	class_id int4 NULL,
	crt_time timestamp NULL,
	crt_user varchar(255) NULL,
	process_id int4 NULL,
	update_time timestamp NULL,
	update_user varchar(255) NULL,
	CONSTRAINT t_sys_process_class_rel_pkey PRIMARY KEY (process_class_id)
);

CREATE TABLE public.t_sys_process_info (
	process_id serial NOT NULL,
	created_time timestamp NULL,
	created_user varchar(255) NULL,
	enabled int4 NULL,
	process_detail varchar(255) NULL,
	process_name varchar(255) NULL,
	process_number varchar(255) NULL,
	updated_time timestamp NULL,
	updated_user varchar(255) NULL,
	CONSTRAINT t_sys_process_info_pkey PRIMARY KEY (process_id)
);

CREATE TABLE public.t_sys_role (
	role_id serial NOT NULL,
	role_name varchar(200) NOT NULL,
	role_code varchar(50) NULL,
	role_explain varchar(255) NULL,
	enabled varchar(1) NOT NULL DEFAULT 0,
	created_time date NULL,
	created_name varchar(200) NULL,
	updated_time date NULL,
	updated_name varchar(200) NULL,
	by_factory varchar NULL DEFAULT 1,
	by_group varchar NULL DEFAULT 1,
	CONSTRAINT t_sys_role_pkey PRIMARY KEY (role_id)
);

CREATE TABLE public.t_sys_role_menu (
	role_menu_id serial NOT NULL,
	role_id int4 NOT NULL,
	menu_id int4 NOT NULL,
	enabled varchar(1) NOT NULL DEFAULT 0,
	created_name varchar(50) NULL,
	created_time date NULL,
	updated_name varchar(50) NULL,
	updated_time date NULL,
	CONSTRAINT t_sys_role_menu_pkey PRIMARY KEY (role_menu_id)
);

CREATE TABLE public.t_sys_role_user (
	role_user_id serial NOT NULL,
	role_id int4 NULL,
	user_id varchar NOT NULL,
	created_time date NULL,
	created_name varchar(50) NULL,
	updated_time date NULL,
	updated_name varchar(50) NULL,
	user_status varchar(1) NOT NULL DEFAULT 0,
	last_time timestamp NULL,
	CONSTRAINT t_sys_role_user_pkey PRIMARY KEY (role_user_id)
);

CREATE TABLE public.t_sys_user_menu (
	user_menu_id varchar(32) NOT NULL,
	user_id varchar(32) NOT NULL,
	menu_id varchar(32) NOT NULL,
	enabled varchar(1) NOT NULL DEFAULT 0,
	created_name varchar(20) NULL,
	created_time date NULL,
	updated_name varchar(20) NULL,
	updated_time date NULL,
	CONSTRAINT t_sys_user_menu_pkey PRIMARY KEY (user_menu_id)
);

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

-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- VALUES ('首页', 'home', '/home', 0, 0
-- 		, 0, 'link'),
-- 	('角色管理', 'domain', '/role', 0, 0
-- 		, 0, 'link'),
-- 	('账号管理', 'domain', '/account', 0, 0
-- 		, 0, 'link'),
-- 	('字典管理', 'domain', '/dictionary', 0, 0
-- 		, 0, 'link'),
-- 	('设备管理', 'domain', '/mydevice', 0, 0
-- 		, 0, 'link'),
-- 	('人员管理', 'domain', '/staff', 0, 0
-- 		, 0, 'link'),
-- 	('班别管理', 'domain', '/class', 0, 0
-- 		, 0, 'link'),
-- 	('规则链库', 'settings_ethernet', '/ruleChains', 0, 0
-- 		, 0, 'link'),
-- 	('订单管理', 'bookmarks', '/order', 0, 0
-- 		, 0, 'toggle'),
-- 	('工艺路线管理', 'bookmarks', '/technological', 0, 0
-- 		, 0, 'toggle'),
-- 	('资产', 'domain', '/assets', 0, 0
-- 		, 0, 'link'),
-- 	('设备', 'devices_other', '/devices', 0, 0
-- 		, 0, 'link'),
-- 	('设备配置', 'mdi:alpha-d-box', '/deviceProfiles', 0, 0
-- 		, 0, 'link'),
-- 	('OTA升级', 'memory', '/otaUpdates', 0, 0
-- 		, 0, 'link'),
-- 	('实体视图', 'view_quilt', '/entityViews', 0, 0
-- 		, 0, 'link'),
-- 	('Edge instances', 'router', '/edgeInstances', 0, 0
-- 		, 0, 'link'),
-- 	('Edge management', 'settings_input_antenna', '/edgeManagement', 0, 0
-- 		, 0, 'toggle'),
-- 	('部件库', 'now_widgets', '/widgets-bundles', 0, 0
-- 		, 0, 'link'),
-- 	('仪表板库', 'dashboards', '/dashboards', 0, 0
-- 		, 0, 'link'),
-- 	('审计日志', 'domain', '/assets', 0, 0
-- 		, 0, 'link'),
-- 	('Api 使用统计', 'insert_chart', '/usage', 0, 0
-- 		, 0, 'link'),
-- 	('系统设置', 'settings', '/settings', 0, 0
-- 		, 0, 'toggle');
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- 	values('物料','material','/material','0','0','0','link')
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select '订单列表' as menu_name ,'bookmarks' as menu_explain,'/order/list' as "path",0 as flag,(select menu_id from t_sys_menu where path='/order') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select '工艺路线' as menu_name ,'bookmarks' as menu_explain,'/technological/processRoute' as "path",0 as flag,(select menu_id from t_sys_menu where path='/technological') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select '工序管理' as menu_name ,'bookmarks' as menu_explain,'/technological/processManage' as "path",0 as flag,(select menu_id from t_sys_menu where path='/technological') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select 'Rule Chain templates' as menu_name ,'settings_ethernet' as menu_explain,'/edgeManagement/ruleChains' as "path",0 as flag,(select menu_id from t_sys_menu where path='/edgeManagement') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select '首页设置' as menu_name ,'settings_applications' as menu_explain,'/settings/home' as "path",0 as flag,(select menu_id from t_sys_menu where path='/settings') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public.t_sys_menu (menu_name, menu_explain, "path", flag, parent_id
-- 	, enabled, menu_type)
-- select '资源库' as menu_name ,'folder' as menu_explain,'/settings/resources-library' as "path",0 as flag,(select menu_id from t_sys_menu where path='/settings') as parent_id,0 as enabled,'link' as menu_type
--
-- INSERT INTO public."t_sys_role"
-- (role_name, role_code, role_explain, created_name, enabled, created_time)
-- VALUES('厂长', 'JSBM-99999', '默认角色', '', '0', '2022-05-12');


COMMENT ON TABLE t_bus_order_head IS '订单信息主表';
COMMENT ON TABLE t_bus_order_ppbom IS '订单用料清单表';
COMMENT ON TABLE t_bus_order_process IS '订单工序执行表';
COMMENT ON TABLE t_bus_order_process_record IS '订单报工/盘点结果表';
COMMENT ON TABLE t_bus_order_process_history IS '订单报工/盘点历史记录表';
COMMENT ON TABLE t_bus_order_bind_code IS '扫码绑定信息表';

-- 2022-06-29 报工盘点增加订单用料清单ID
alter TABLE t_bus_order_process_history add order_ppbom_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_ppbom_id" IS '订单用料清单ID';
alter TABLE t_bus_order_process_record add order_ppbom_id int4 NULL;
COMMENT ON COLUMN "public"."t_bus_order_process_history"."order_ppbom_id" IS '订单用料清单ID';

-- 2022-07-04
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_first_material_max_value float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_first_material_max_value IS '每锅A料添加最大值';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_second_material_max_value float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_second_material_max_value IS '每锅B料添加最大值';


-- 2022-7-6 订单
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_mes_unit VARCHAR(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_mes_unit IS 'mes单位';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_devept_unit VARCHAR(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_devept_unit IS '开发单位';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_mes_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_mes_qty IS 'mes数量';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_weigh_devept_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_weigh_devept_qty IS '开发数量';


-- 2022-07-06 物料
ALTER TABLE public.mid_material ADD kd_material_cut_weight float4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_cut_weight IS '切断后重量';

ALTER TABLE public.mid_material ADD kd_material_peel_weight float4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_peel_weight IS '扒皮后重量';

ALTER TABLE public.mid_material ADD kd_material_mixture_weight float4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_mixture_weight IS '拌料后重量';

ALTER TABLE public.mid_material ADD kd_material_stretch_weight float4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_stretch_weight IS '拉伸膜后重量';

ALTER TABLE public.mid_material ADD kd_material_is_peel int4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_is_peel IS '是否去皮 true去皮 false 不去皮';

ALTER TABLE public.mid_material ADD kd_material_workshop_id int4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_workshop_id IS '所属车间-id';

ALTER TABLE public.mid_material ADD kd_material_workshop_name varchar(255) NULL;
COMMENT ON COLUMN public.mid_material.kd_material_workshop_name IS '所属车间-名称';

ALTER TABLE public.mid_material ADD kd_material_workshop_number varchar(255) NULL;
COMMENT ON COLUMN public.mid_material.kd_material_workshop_number IS '所属车间-编码';


-- 2022-07-07
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_bom_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_bom_id IS 'bomId';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_bom_number varchar(100) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_bom_number IS 'bom编号';
-- 2022-07-07 设备
ALTER TABLE public.t_sys_device ADD belong_process_id int4 NULL;
COMMENT ON COLUMN public.t_sys_device.belong_process_id IS '所属工序id';
ALTER TABLE public.t_sys_device ADD kd_org_id int4 NULL;
COMMENT ON COLUMN public.t_sys_device.kd_org_id IS '生产组织id';
ALTER TABLE public.t_sys_device ADD kd_dept_id int4 NULL;
COMMENT ON COLUMN public.t_sys_device.kd_dept_id IS '生产车间id';

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

-- 2022-07-12 班别增加班别编码
ALTER TABLE public.t_sys_class ADD class_number VARCHAR(255);
COMMENT ON COLUMN public.t_sys_class.class_number IS '班别编码';

ALTER TABLE public.t_sys_class ADD belong_process_id int4 NULL;
COMMENT ON COLUMN public.t_sys_class.belong_process_id IS '所属工序id';
ALTER TABLE public.t_sys_class ADD kd_org_id int4 NULL;
COMMENT ON COLUMN public.t_sys_class.kd_org_id IS '生产组织id';
ALTER TABLE public.t_sys_class ADD kd_dept_id int4 NULL;
COMMENT ON COLUMN public.t_sys_class.kd_dept_id IS '生产车间id';

--工艺路线
ALTER TABLE public.t_sys_craft_info ADD kd_org_id int4 NULL;
COMMENT ON COLUMN public.t_sys_craft_info.kd_org_id IS '生产组织id';
ALTER TABLE public.t_sys_craft_info ADD kd_dept_id int4 NULL;
COMMENT ON COLUMN public.t_sys_craft_info.kd_dept_id IS '生产车间id';

-- -- 2022-07-13 扫码串联改造
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group")
--  VALUES('RECORDTYPE0000', '类目类型', '5', '投入前道数量', '1', '2022-07-13 14:47:03.729', 'hhh@qq.com', '2022-07-13 14:47:03.729', 'hhh@qq.com', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group")
--  VALUES ('RECORDTYPE0000', '类目类型', '6', 'AB料投入', '1', '2022-07-14 09:28:11.937', 'hhh@qq.com', '2022-07-14 09:28:11.937', 'hhh@qq.com', 0);

ALTER TABLE public.t_bus_order_head ADD order_finish_date timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_finish_date IS '完工时间';
ALTER TABLE public.t_bus_order_head ADD order_pending_date timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_pending_date IS '挂起时间';
ALTER TABLE public.t_bus_order_head ADD order_pending_desc varchar(100) NULL;
COMMENT ON COLUMN public.t_bus_order_head.order_pending_desc IS '挂起备注';

ALTER TABLE public.t_bus_order_ppbom add mid_ppbom_entry_item_type int2 NOT NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_item_type IS '子项类型';
ALTER TABLE public.t_bus_order_ppbom add mid_ppbom_entry_use_rate float4 NOT NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_use_rate IS '使用比例';

ALTER TABLE public.t_bus_order_ppbom add mid_ppbom_entry_input_process varchar(20) NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_input_process IS '投入工序';

-- --2022-07-18
-- --A参数
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('GROUPCODE0000', '字典分类', 'OTHER0000', '其他', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('OTHER0000', '其他', '0.9', 'A参数', '1', '2022-07-16 16:27:30.002', 'tenant@thingsboard.org', '2022-07-16 16:27:30.002', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('OTHER0000', '其他', '1', 'A参数是否启用', '1', '2022-07-16 16:30:01.48', 'tenant@thingsboard.org', '2022-07-16 16:30:01.48', 'tenant@thingsboard.org', 0);
--
--
-- --ERP工序标识
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('GROUPCODE0000', '字典分类', 'ERPPROCESS0000', 'ERP工序标识', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
--
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'LSM', '拉伸膜', '1', '2022-07-18 09:41:58.993', 'tenant@thingsboard.org', '2022-07-18 09:41:58.993', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'ZB', '斩拌', '1', '2022-07-18 09:40:18.853', 'tenant@thingsboard.org', '2022-07-18 09:40:18.853', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'XL', '蟹柳', '1', '2022-07-18 09:40:35.711', 'tenant@thingsboard.org', '2022-07-18 09:40:35.711', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'BP', '剥皮', '1', '2022-07-18 09:41:32.777', 'tenant@thingsboard.org', '2022-07-18 09:41:32.777', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'BL', '拌料', '1', '2022-07-18 09:41:44.278', 'tenant@thingsboard.org', '2022-07-18 09:41:44.278', 'tenant@thingsboard.org', 0);
-- INSERT INTO "public"."t_sys_code_dsc"("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group") VALUES ('ERPPROCESS0000', 'ERP工序标识', 'BZ', '包装', '1', '2022-07-18 09:42:14.376', 'tenant@thingsboard.org', '2022-07-18 09:42:14.376', 'tenant@thingsboard.org', 0);

-- 2022-07-19
ALTER TABLE public.t_sync_material add kd_material_id int4 NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_id IS '金蝶的物料ID';

--2022-07-26 小廖
alter table t_bus_order_process add hand_over_person_id integer;
comment on column t_bus_order_process.hand_over_person_id is '移交人';

alter table t_bus_order_process add hand_over_time timestamp(6);
comment on column t_bus_order_process.hand_over_time is '移交时间';

ALTER TABLE public.t_bus_order_process_record ADD device_group_id varchar COLLATE "pg_catalog"."default" DEFAULT ''::character varying;
COMMENT ON COLUMN public.t_bus_order_process_record.device_group_id IS '多选机台号分组标识id';
ALTER TABLE public.t_bus_order_process_record ADD device_person_group_id varchar COLLATE "pg_catalog"."default" DEFAULT ''::character varying;
COMMENT ON COLUMN public.t_bus_order_process_record.device_person_group_id IS '多选操作员分组标识id';

ALTER TABLE public.t_bus_order_process_history ADD device_group_id varchar COLLATE "pg_catalog"."default" DEFAULT ''::character varying;
COMMENT ON COLUMN public.t_bus_order_process_history.device_group_id IS '多选机台号分组标识id';
ALTER TABLE public.t_bus_order_process_history ADD device_person_group_id varchar COLLATE "pg_catalog"."default" DEFAULT ''::character varying;
COMMENT ON COLUMN public.t_bus_order_process_history.device_person_group_id IS '多选操作员分组标识id';

-- 2022-08-09
ALTER TABLE public.t_bus_order_process_record ADD record_type_bg varchar(20) NULL;
COMMENT ON COLUMN public.t_bus_order_process_record.record_type_bg IS '报工类型:REPORTYPE0001=正常，REPORTYPE0002=尾料';
ALTER TABLE public.t_bus_order_process_history ADD record_type_bg varchar(20) NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.record_type_bg IS '报工类型:REPORTYPE0001=正常，REPORTYPE0002=尾料';


-- 2022-08-12
ALTER TABLE public.t_bus_order_process_record ADD record_manual_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_record.record_manual_qty IS '报工数量（手工输入）';
ALTER TABLE public.t_bus_order_process_history ADD record_manual_qty float4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.record_manual_qty IS '报工数量（手工输入）';

-- 2022-08-16（废弃）
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_sale_order_no varchar(50) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_sale_order_no IS '需求订单';

-- 2022-08-22
ALTER TABLE public.t_bus_order_head ADD mid_mo_sale_order_no varchar(50) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_sale_order_no IS '需求订单';

ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_denominator float4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_denominator IS '分母';

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

-- 2022-08-23
ALTER TABLE public.t_bus_order_process_history ADD order_process_history_parent_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.order_process_history_parent_id IS '报工盘点历史记录表父级id';

-- ALTER TABLE public.t_bus_order_ppbom ADD is_main_ppbom_material int2 NULL;
-- COMMENT ON COLUMN public.t_bus_order_ppbom.is_main_ppbom_material IS '是否主用料';

ALTER TABLE public.t_bus_order_process_history ADD import_pot float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_history.import_pot IS '投入锅数';
ALTER TABLE public.t_bus_order_process_history ADD export_pot float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_history.export_pot IS '产出锅数';
ALTER TABLE public.t_bus_order_process_history ADD export_pot_min float4 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_history.export_pot_min IS '产出斗数';

ALTER TABLE public.t_bus_order_process_record ADD import_pot_group int2 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_record.import_pot_group IS '投入组合锅数';
ALTER TABLE public.t_bus_order_process_history ADD import_pot_group int2 NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_process_history.import_pot_group IS '投入组合锅数';

ALTER TABLE public.t_bus_order_head ADD mid_mo_team_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_team_id IS '班组-ID';
ALTER TABLE public.t_bus_order_head ADD mid_mo_team_name varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_team_name IS '班组-名字';


ALTER TABLE public.t_bus_order_process_record ADD lsm_material_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_record.lsm_material_id IS '拉伸膜物料ID';
ALTER TABLE public.t_bus_order_process_history ADD lsm_material_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.lsm_material_id IS '拉伸膜物料ID';

-- 订单报工机台号附表
CREATE TABLE public.t_bus_order_process_device_rel (
   id serial NOT NULL,
   device_id int4 NULL,
   device_group_id varchar NULL,
   crt_user varchar(255) NULL,
   crt_time timestamp NULL,
   update_user varchar(255) NULL,
   update_time timestamp NULL,
   order_process_id int4 NULL,
   CONSTRAINT t_bus_order_process_record_person_rel_pkey PRIMARY KEY (id)
);

COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."device_id" IS '机台号id';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."device_group_id" IS '机台号分组标识id';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."crt_user" IS '创建人';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."crt_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_bus_order_process_device_rel"."order_process_id" IS '工序执行表id';
COMMENT ON TABLE "public"."t_bus_order_process_device_rel" IS '订单报工机台号附表';

-- 订单报工操作员附表
CREATE TABLE public.t_bus_order_process_person_rel (
   id serial NOT NULL,
   device_person_id int4 NULL,
   device_person_group_id varchar NULL,
   crt_user varchar(255) NULL,
   crt_time timestamp NULL,
   update_user varchar(255) NULL,
   update_time timestamp NULL,
   order_process_id int4 NULL,
   CONSTRAINT t_bus_order_process_record_person_ref_pkey PRIMARY KEY (id)
);

COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."device_person_id" IS '操作员id';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."device_person_group_id" IS '操作员分组标识id';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."crt_user" IS '创建人';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."crt_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."update_user" IS '更新人';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_bus_order_process_person_rel"."order_process_id" IS '工序执行表id';
COMMENT ON TABLE "public"."t_bus_order_process_person_rel" IS '订单报工操作员附表';

ALTER TABLE public.mid_material ADD kd_material_props_id varchar NULL;
COMMENT ON COLUMN public.mid_material.kd_material_props_id IS '物料属性';

-- 2022/9/7
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_handle_group int4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_handle_group IS '操作分组';
ALTER TABLE public.t_bus_order_ppbom ADD mid_ppbom_entry_handle_sort int4 NULL;
COMMENT ON COLUMN public.t_bus_order_ppbom.mid_ppbom_entry_handle_sort IS '操作顺序';

-- 2022/9/8
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_ice_water float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_ice_water IS '冰水最大值';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_emulsion float4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_emulsion IS '次品乳化浆最大值';


-- 2022-09-15
ALTER TABLE public.mid_material ADD kd_material_each_piece_num int4 NULL;
COMMENT ON COLUMN public.mid_material.kd_material_each_piece_num IS '每件支数';

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

-- 2022-09-22 工序执行表增加盘点类型和盘点时间
ALTER TABLE public.t_bus_order_process ADD old_record_type_pd VARCHAR(50) NULL;
COMMENT ON COLUMN public.t_bus_order_process.old_record_type_pd IS '移交前的盘点类型';
ALTER TABLE public.t_bus_order_process ADD old_record_type_pd_time timestamp NULL;
COMMENT ON COLUMN public.t_bus_order_process.old_record_type_pd_time IS '移交前的盘点时间';
ALTER TABLE public.t_bus_order_process ADD record_type_pd VARCHAR(50) NULL;
COMMENT ON COLUMN public.t_bus_order_process.record_type_pd IS '移交前的盘点类型';
ALTER TABLE public.t_bus_order_process ADD record_type_pd_time timestamp NULL;
COMMENT ON COLUMN public.t_bus_order_process.record_type_pd_time IS '移交前的盘点时间';

ALTER TABLE public.t_bus_order_process ADD old_hand_over_person_id integer;
COMMENT ON COLUMN public.t_bus_order_process.old_hand_over_person_id IS '移交前的移交人';
ALTER TABLE public.t_bus_order_process ADD old_hand_over_time timestamp NULL;
COMMENT ON COLUMN public.t_bus_order_process.old_hand_over_time IS '移交前的移交时间';


ALTER TABLE public.t_bus_order_process_record ADD iot_collection_start_time timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_process_record.iot_collection_start_time IS 'IOT开始时间';
ALTER TABLE public.t_bus_order_process_history ADD iot_collection_start_time timestamp(6) NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.iot_collection_start_time IS 'IOT开始时间';

-- 2022/9/20
ALTER TABLE public.t_sys_message_order ADD order_process_id int4 NULL;
COMMENT ON COLUMN public.t_sys_message_order.order_process_id IS '工序执行表ID';
ALTER TABLE public.t_sys_message_order ADD remark varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_message_order.remark IS '备注';
-- 2022/9/29
ALTER TABLE public.t_sys_message_order ADD execute_process_status varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_message_order.execute_process_status IS '执行工序状态,包括：0=未开工、1=已开工、2=暂停、3=已完工、4=移交中、5=移交驳回';

ALTER TABLE public.t_bus_order_process ADD old_process_status varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_process.old_process_status IS '原工序状态';

-- 2022/7/4
ALTER TABLE public.t_bus_order_head ADD is_deleted varchar NULL DEFAULT 1;
COMMENT ON COLUMN public.t_bus_order_head.is_deleted IS '是否删除 0：删除 1：非删除';

-- 2022/10/8
ALTER TABLE public.t_sys_log ALTER in_param TYPE VARCHAR(2000);

-- 2022-10-10
ALTER TABLE public.t_sys_process_info ADD erp_process_number varchar(20) NULL;
COMMENT ON COLUMN public.t_sys_process_info.erp_process_number IS 'ERP工序标识';


-- 2022-10-20
CREATE TABLE t_sys_picking(
    picking_id serial NOT NULL,
    picking_no VARCHAR(64),
    picking_type VARCHAR(1),
    picking_status VARCHAR(1),
    source_picking_no VARCHAR(64),
    order_no VARCHAR(64),
    bill_no VARCHAR(64),
    body_lot VARCHAR(64),
    body_material_number VARCHAR(255),
    body_material_name VARCHAR(255),
    process_id INTEGER,
    class_id INTEGER,
    person_id INTEGER,
    order_process_id INTEGER,
    created_id INTEGER,
    created_name VARCHAR(255),
    created_time timestamp,
    PRIMARY KEY (picking_id)
);

COMMENT ON TABLE t_sys_picking IS '料单工单表';
COMMENT ON COLUMN t_sys_picking.picking_id IS 'id';
COMMENT ON COLUMN t_sys_picking.picking_no IS '料单号';
COMMENT ON COLUMN t_sys_picking.picking_type IS '料单工单类型0：专项领料，1：共耗领料，2：补料 3：退料';
COMMENT ON COLUMN t_sys_picking.picking_status IS '料筐工单状态：0:进行中1：待提交2：已完工';
COMMENT ON COLUMN t_sys_picking.source_picking_no IS '源料单号';
COMMENT ON COLUMN t_sys_picking.order_no IS '订单号';
COMMENT ON COLUMN t_sys_picking.bill_no IS '单据编号';
COMMENT ON COLUMN t_sys_picking.body_lot IS '批号';
COMMENT ON COLUMN t_sys_picking.body_material_number IS '物料编码';
COMMENT ON COLUMN t_sys_picking.body_material_name IS '物料名称';
COMMENT ON COLUMN t_sys_picking.process_id IS '工序id';
COMMENT ON COLUMN t_sys_picking.class_id IS '班别id';
COMMENT ON COLUMN t_sys_picking.person_id IS '操作员id';
COMMENT ON COLUMN t_sys_picking.order_process_id IS '工序执行表id';
COMMENT ON COLUMN t_sys_picking.created_id IS '申请人';
COMMENT ON COLUMN t_sys_picking.created_name IS '申请人姓名';
COMMENT ON COLUMN t_sys_picking.created_time IS '申请时间';

CREATE TABLE t_sys_picking_record(
    picking_record_id serial NOT NULL,
    picking_no VARCHAR(64),
    picking_record_type VARCHAR(32),
    material_name VARCHAR(255),
    material_number VARCHAR(255),
    material_specification VARCHAR(255),
    picking_number DECIMAL(24,2),
    picking_unit VARCHAR(255),
    in_number DECIMAL(24,2),
    no_number DECIMAL(24,2),
    must_qty DECIMAL(19,9),
    picking_id INTEGER,
    created_name VARCHAR(255),
    created_time timestamp,
    PRIMARY KEY (picking_record_id)
);

COMMENT ON TABLE t_sys_picking_record IS '料单物料结果表';
COMMENT ON COLUMN t_sys_picking_record.picking_record_id IS 'id';
COMMENT ON COLUMN t_sys_picking_record.picking_no IS '料单号';
COMMENT ON COLUMN t_sys_picking_record.picking_record_type IS '类型 0:领料 1:退料';
COMMENT ON COLUMN t_sys_picking_record.material_name IS '物料名称';
COMMENT ON COLUMN t_sys_picking_record.material_number IS '物料编码';
COMMENT ON COLUMN t_sys_picking_record.material_specification IS '规格型号';
COMMENT ON COLUMN t_sys_picking_record.picking_number IS '申领数量';
COMMENT ON COLUMN t_sys_picking_record.picking_unit IS '单位';
COMMENT ON COLUMN t_sys_picking_record.in_number IS '已领取数量';
COMMENT ON COLUMN t_sys_picking_record.no_number IS '未领取数量';
COMMENT ON COLUMN t_sys_picking_record.must_qty IS '应发数量';
COMMENT ON COLUMN t_sys_picking_record.picking_id IS '领料工单表id';
COMMENT ON COLUMN t_sys_picking_record.created_name IS '创建人';
COMMENT ON COLUMN t_sys_picking_record.created_time IS '创建时间';

CREATE TABLE t_sys_picking_history(
    picking_history_id serial NOT NULL,
    picking_record_id INTEGER,
    picking_history_type VARCHAR(1),
    material_name VARCHAR(255),
    material_number VARCHAR(255),
    material_specification VARCHAR(255),
    picking_number DECIMAL(24,2),
    picking_unit VARCHAR(255),
    picking_time timestamp,
    updated_id INTEGER,
    updated_class_id INTEGER,
    created_name VARCHAR(255),
    created_time timestamp,
    PRIMARY KEY (picking_history_id)
);

COMMENT ON TABLE t_sys_picking_history IS '料单物料记录表';
COMMENT ON COLUMN t_sys_picking_history.picking_history_id IS 'id';
COMMENT ON COLUMN t_sys_picking_history.picking_record_id IS '领料结果表id';
COMMENT ON COLUMN t_sys_picking_history.picking_history_type IS '类型 0：领料 1：退料';
COMMENT ON COLUMN t_sys_picking_history.material_name IS '物料名称';
COMMENT ON COLUMN t_sys_picking_history.material_number IS '物料编码';
COMMENT ON COLUMN t_sys_picking_history.material_specification IS '规格型号';
COMMENT ON COLUMN t_sys_picking_history.picking_number IS '领料/退料数量';
COMMENT ON COLUMN t_sys_picking_history.picking_unit IS '单位';
COMMENT ON COLUMN t_sys_picking_history.picking_time IS '领料/退料确认时间';
COMMENT ON COLUMN t_sys_picking_history.updated_id IS '领料/退料人';
COMMENT ON COLUMN t_sys_picking_history.updated_class_id IS '领料/退料班组id';
COMMENT ON COLUMN t_sys_picking_history.created_name IS '处理人';
COMMENT ON COLUMN t_sys_picking_history.created_time IS '处理时间';

-- 2022-10-24
ALTER TABLE public.t_sys_process_info ADD by_set_import varchar NULL DEFAULT 0;
COMMENT ON COLUMN public.t_sys_process_info.by_set_import IS '投入设置属性 0：禁用 1：启用';

-- 2022-10-27 料框
ALTER TABLE public.t_sys_charging_basket ADD unit varchar(50) NULL;
COMMENT ON COLUMN public.t_sys_charging_basket.unit IS '料框单位';

-- 2022-10-28
ALTER TABLE public.t_sync_material ADD kd_material_workshop_id int4 NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_workshop_id IS '所属车间-id';
ALTER TABLE public.t_sync_material ADD kd_material_workshop_name varchar(255) NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_workshop_name IS '所属车间-名称';
ALTER TABLE public.t_sync_material ADD kd_material_workshop_number varchar(255) NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_workshop_number IS '所属车间-编码';
ALTER TABLE public.t_sync_material ADD kd_material_use_org_id int4 NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_use_org_id IS '使用组织id';
ALTER TABLE public.t_sync_material ADD kd_material_use_org_number varchar NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_use_org_number IS '使用组织编码';
ALTER TABLE public.t_sync_material ADD kd_material_use_org_name varchar NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_use_org_name IS '使用组织名称';

-- 2022-11-04
ALTER TABLE public.t_sys_process_info ADD fingerprint_authentication varchar NULL DEFAULT 0;
COMMENT ON COLUMN public.t_sys_process_info.fingerprint_authentication IS '指纹认证 0：禁用 1：启用';

ALTER TABLE public.t_bus_order_head ADD order_matching varchar NULL DEFAULT 0;
COMMENT ON COLUMN public.t_bus_order_head.order_matching IS '匹配工艺路线: 0: 不匹配, 1:匹配';
-- 2022-11-16
ALTER TABLE public.t_sys_craft_material_rel ADD material_id int4 NULL;
COMMENT ON COLUMN public.t_sys_craft_material_rel.material_id IS '物料id';

-- 2022-11-21
ALTER TABLE public.t_sys_process_info ADD by_set_export varchar NULL;
COMMENT ON COLUMN public.t_sys_process_info.by_set_export IS '产出控制 0：禁用 1：启用';

-- 2022-11-18
ALTER TABLE public.t_bus_order_head ADD mid_mo_team_number varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_team_number IS '班组-编码';


ALTER TABLE public.t_sys_craft_info ADD prev_craft_id int4;
COMMENT ON COLUMN public.t_sys_craft_info.prev_craft_id IS '前道工艺路线';

-- 2022-11-23
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_team_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_team_id IS '明细-班组-ID';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_team_name varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_team_name IS '明细-班组-名称';
ALTER TABLE public.t_bus_order_head ADD mid_mo_entry_team_number varchar(255) NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_entry_team_number IS '明细-班组-编码';

-- 2022/11/30
ALTER TABLE public.t_sys_log ALTER methods TYPE VARCHAR(500);

COMMENT ON COLUMN public.t_sys_craft_material_rel.material_id IS '存金蝶物料id';
-- 2022-12-1
ALTER TABLE public.t_sys_menu ADD skip_url varchar(3000) NULL;
COMMENT ON COLUMN public.t_sys_menu.skip_url IS '跳转url，只对标识为url时有效';

ALTER TABLE public.t_sys_class ADD class_team_number varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_class.class_team_number IS 'ERP班别编码';

-- 2022-12-08
CREATE OR REPLACE FUNCTION "public"."up_timestamp"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
begin
    new.updated_time= current_timestamp;
return new;
end
$BODY$
LANGUAGE plpgsql VOLATILE
  COST 100
--磨具规格
CREATE TABLE public.t_sys_abrasive_specification (
                                                     abrasive_specification_id serial4 NOT NULL,
                                                     abrasive_specification_no varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     single_gram_qty numeric(18, 3) NOT NULL DEFAULT 0,
                                                     math_by_weight varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     math_by_qty varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     abrasive_specification_remark varchar(255) NOT NULL DEFAULT ''::character varying,
                                                     kd_org_id int4 NOT NULL DEFAULT 0,
                                                     kd_org_number varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     kd_org_name varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     kd_workshop_id int4 NOT NULL DEFAULT 0,
                                                     kd_workshop_number varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     kd_workshop_name varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     abrasive_specification_status varchar(1) NOT NULL DEFAULT '0'::character varying,
                                                     abrasive_specification_seq int4 NOT NULL DEFAULT 0,
                                                     "version" int2 NOT NULL DEFAULT 1,
                                                     is_deleted varchar NOT NULL DEFAULT '0'::character varying,
                                                     created_time timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
                                                     created_name varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     updated_time timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
                                                     updated_name varchar(64) NOT NULL DEFAULT ''::character varying,
                                                     CONSTRAINT t_sys_abrasive_specification_pkey PRIMARY KEY (abrasive_specification_id)
);

COMMENT ON TABLE  public.t_sys_abrasive_specification IS '磨具规格';
COMMENT ON COLUMN public.t_sys_abrasive_specification.abrasive_specification_id IS '磨具规格id';
COMMENT ON COLUMN public.t_sys_abrasive_specification.abrasive_specification_no IS '磨具规格';
COMMENT ON COLUMN public.t_sys_abrasive_specification.single_gram_qty IS '单支克重';
COMMENT ON COLUMN public.t_sys_abrasive_specification.math_by_weight IS '转重量公式';
COMMENT ON COLUMN public.t_sys_abrasive_specification.math_by_qty IS '转数量公式';
COMMENT ON COLUMN public.t_sys_abrasive_specification.abrasive_specification_remark IS '磨具描述';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_org_id IS '生产组织id';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_org_number IS '生产组织编码';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_org_name IS '生产组织名称';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_workshop_id IS '生产车间Id';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_workshop_number IS '生产车间编码';
COMMENT ON COLUMN public.t_sys_abrasive_specification.kd_workshop_name IS '生产车间名称';
COMMENT ON COLUMN public.t_sys_abrasive_specification.abrasive_specification_status IS '状态:0:禁用, 1:启用';
COMMENT ON COLUMN public.t_sys_abrasive_specification.abrasive_specification_seq IS '排序';
COMMENT ON COLUMN public.t_sys_abrasive_specification.version IS '数据更新版本';
COMMENT ON COLUMN public.t_sys_abrasive_specification.is_deleted IS '是否删除 0:非删除, 1:删除';
COMMENT ON COLUMN public.t_sys_abrasive_specification.created_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_abrasive_specification.created_name IS '创建人';
COMMENT ON COLUMN public.t_sys_abrasive_specification.updated_time IS '修改时间';
COMMENT ON COLUMN public.t_sys_abrasive_specification.updated_name IS '修改人';

-- 创建更新修改时间对应触发器
create trigger updated_t_sys_abrasive_specification before
    update
    on
        public.t_sys_abrasive_specification for each row execute function up_timestamp();

-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(13, '15g*8支*40袋', 0.015, '次*(4*8)*0.015kg', '次*(4*8)*1支', '4*8（跑一次32支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 32, 0, '0', '2022-12-08 17:55:41.020', 'zengjinjiang', '2022-12-08 17:55:41.020', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(14, '（9支）95克*40袋', 0.015, '次*(4*11)*0.015kg', '次*(4*11)*1支', '4*11（跑一次44支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 44, 0, '0', '2022-12-08 17:56:30.242', 'zengjinjiang', '2022-12-08 17:56:30.242', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(15, '2.64kg(88克*30袋)', 0.011, '次*(4*5)*0.0108kg', '次*(4*5)*1支', '4*5（跑一次20支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 20, 0, '0', '2022-12-08 17:57:58.921', 'zengjinjiang', '2022-12-08 17:57:58.921', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(16, '2.76kg(92克*30袋）', 0.018, '次*(4*8)*0.018kg', '次*(4*8)*1支', '4*8（跑一次32支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 32, 0, '0', '2022-12-08 17:58:50.000', 'zengjinjiang', '2022-12-08 17:58:50.000', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(17, '20g*10支*30盒', 0.020, '次*(4*7)*0.02kg', '次*(4*7)*1支', '4*7（跑一次28支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 28, 0, '0', '2022-12-08 17:59:28.271', 'zengjinjiang', '2022-12-08 17:59:28.271', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(18, '5kg（25克*20支）', 0.025, '次*(4*7)*0.025kg', '次*(4*7)*1支', '4*7（跑一次28支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 28, 0, '0', '2022-12-08 18:00:13.127', 'zengjinjiang', '2022-12-08 18:00:13.127', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(5, '1', 1.000, '1', '1', '1', 248194, '2', '山东工厂', 582518, 'BM000235', '制桶车间（山东）', '0', 1, 1, '1', '2022-12-06 18:39:23.602', '144590@qq.com', '2022-12-07 15:20:58.878', '144590@qq.com');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(7, '10克*20支*20盒', 0.010, '次*(4*11)*0.01kg', '次*(4*11)*1支', '4*11（跑一次44支）', 100017, '10103', '晋江二厂', 170985, 'BM000076', '二厂卤蛋车间', '0', 1, 2, '1', '2022-12-07 18:24:22.134', '144590@qq.com', '2022-12-08 15:53:39.744', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(4, 'test01', 1.000, '1', '1', '1', 248194, '2', '山东工厂', 614341, 'BM000259', '卤蛋车间（山东）', '0', 1, 2, '1', '2022-12-06 18:34:22.435', 'test', '2022-12-08 15:53:43.749', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(6, 'test025', 1.000, '1', '1', '1', 248194, '2', '山东工厂', 614341, 'BM000259', '卤蛋车间（山东）', '0', 1, 7, '1', '2022-12-07 14:46:47.000', 'test', '2022-12-08 15:53:50.782', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(9, '20克*24支*12桶', 0.020, '次*(4*7)*0.02kg', '次*(4*7)*1支', '4*7（跑一次28支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 28, 0, '0', '2022-12-08 15:55:49.620', 'zengjinjiang', '2022-12-08 15:55:49.620', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(8, '20克*100支*1盒', 0.331, '次*(22*33)*0.331kg', '次*(4*7)*1支', '41*76（跑一次1000支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '0', 1, 3, '1', '2022-12-08 10:20:02.951', 'zengjinjiang', '2022-12-08 16:00:34.615', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(10, '102克/袋*30袋', 0.020, '次*(4*8)*0.02kg', '次*(4*8)*1支', '4*8（跑一次32支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 32, 8, '0', '2022-12-08 16:00:05.552', 'zengjinjiang', '2022-12-08 17:31:19.856', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(11, '10克*20支*20盒', 0.010, '次*(4*11)*0.01kg', '次*(4*11)*1支', '4*11（跑一次44支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 44, 0, '0', '2022-12-08 17:54:16.518', 'zengjinjiang', '2022-12-08 17:54:16.518', 'zengjinjiang');
-- INSERT INTO public.t_sys_abrasive_specification (abrasive_specification_id, abrasive_specification_no, single_gram_qty, math_by_weight, math_by_qty, abrasive_specification_remark, kd_org_id, kd_org_number, kd_org_name, kd_workshop_id, kd_workshop_number, kd_workshop_name, abrasive_specification_status, abrasive_specification_seq, "version", is_deleted, created_time, created_name, updated_time, updated_name) VALUES(12, '2.5kg*2袋', 0.018, '次*(4*8)*0.018kg', '次*(4*8)*1支', '4*8（跑一次32支）', 100017, '10103', '晋江二厂', 210239, 'BM000189', '二厂蟹柳车间', '1', 32, 0, '0', '2022-12-08 17:55:02.427', 'zengjinjiang', '2022-12-08 17:55:02.427', 'zengjinjiang');

-- 2022-12-12
ALTER TABLE public.t_sys_menu ADD "sort" int NULL;
COMMENT ON COLUMN public.t_sys_menu."sort" IS '排序';
ALTER TABLE public.t_sys_menu ADD br varchar NULL;
COMMENT ON COLUMN public.t_sys_menu.br IS '备注';

-- 2023-04-27
COMMENT ON COLUMN public.mid_material.kd_material_stretch_weight IS '拉伸膜后重量、单支克重';

ALTER TABLE public.t_sync_material ADD kd_material_stretch_weight float4 NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_stretch_weight IS '拉伸膜后重量、单支克重';

ALTER TABLE public.t_sync_material ADD kd_material_each_piece_num int4 NULL;
COMMENT ON COLUMN public.t_sync_material.kd_material_each_piece_num IS '每件支数';

-- 2023-6-14
DROP TABLE IF EXISTS t_sys_device_iot;
CREATE TABLE t_sys_device_iot(
    id serial NOT NULL,
    device_id INTEGER NOT NULL,
    iot_time timestamp NOT NULL,
    device_code VARCHAR(255),
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_sys_device_iot IS '设备iot时间表';
COMMENT ON COLUMN t_sys_device_iot.id IS 'id';
COMMENT ON COLUMN t_sys_device_iot.device_id IS '设备id';
COMMENT ON COLUMN t_sys_device_iot.iot_time IS '最后采集时间';
COMMENT ON COLUMN t_sys_device_iot.device_code IS '设备代码';

CREATE TABLE t_sys_device_iot_history(
    id serial NOT NULL,
    order_no VARCHAR(255),
    order_process_id INTEGER,
    process_code VARCHAR(255),
    device_id INTEGER,
    device_code VARCHAR(255),
    created_time timestamp NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_sys_device_iot_history IS '设备iot时间历史记录表';
COMMENT ON COLUMN t_sys_device_iot_history.id IS 'id';
COMMENT ON COLUMN t_sys_device_iot_history.order_no IS '订单号';
COMMENT ON COLUMN t_sys_device_iot_history.order_process_id IS '工序执行id';
COMMENT ON COLUMN t_sys_device_iot_history.process_code IS '工序编码';
COMMENT ON COLUMN t_sys_device_iot_history.device_id IS '设备id';
COMMENT ON COLUMN t_sys_device_iot_history.device_code IS '设备编码';
COMMENT ON COLUMN t_sys_device_iot_history.created_time IS '创建时间';


-- 2023-7-10
ALTER TABLE public.t_bus_order_process_history ADD all_import_pot int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.all_import_pot IS '积累投入锅数';
ALTER TABLE public.t_sys_device_iot ADD record_type varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_device_iot.record_type IS '类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量、自定义报工';

-- 2023-8-24

ALTER TABLE public.t_sys_device_iot_history ADD record_type varchar(255) NULL;
COMMENT ON COLUMN public.t_sys_device_iot_history.record_type IS '类目类型（类目编码）:数据字典维护，内容包括：原辅料，二级品数量、产后数量、自定义报工';

-- 2023-8-30
ALTER TABLE public.t_bus_order_process_history ADD iot_qty int4 NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.iot_qty IS '设备采集数量';
ALTER TABLE public.t_bus_order_process_history ADD iot_math varchar NULL;
COMMENT ON COLUMN public.t_bus_order_process_history.iot_math IS '计算公式';
COMMENT ON COLUMN public.t_sys_device_iot_history.device_id IS '此字段存放本次采集数量';
-- 2024-6-17
ALTER TABLE public.t_bus_order_update ALTER COLUMN order_json TYPE varchar(30000) USING order_json::varchar;


-- 客户，客户类型、订单备注
ALTER TABLE public.t_bus_order_head ADD mid_mo_customer_id int4 NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_customer_id IS '客户id';

ALTER TABLE public.t_bus_order_head ADD mid_mo_customer_name varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_customer_name IS '客户名称';

ALTER TABLE public.t_bus_order_head ADD mid_mo_customer_number varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_customer_number IS '客户编码';

ALTER TABLE public.t_bus_order_head ADD mid_mo_customer_type varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_customer_type IS '客户类型';

ALTER TABLE public.t_bus_order_head ADD mid_mo_desc varchar NULL;
COMMENT ON COLUMN public.t_bus_order_head.mid_mo_desc IS '订单备注';

-- 2025-7-9
CREATE TABLE t_sys_pd_record(
    pd_record_id serial NOT NULL,
    pd_time timestamp NOT NULL,
    material_number VARCHAR(255),
    material_name VARCHAR(255),
    material_specifications VARCHAR(255),
    pd_unit VARCHAR(255),
    pd_unit_str VARCHAR(255),
    pd_qty NUMERIC(24,6),
    pd_created_name VARCHAR(255),
    pd_created_id VARCHAR(255),
    pd_workshop_name VARCHAR(255),
    pd_workshop_number VARCHAR(255),
    pd_workshop_leader_name VARCHAR(255),
    pd_workshop_leader_id VARCHAR(255),
    pd_class_name VARCHAR(255),
    pd_class_number VARCHAR(255),
    by_deteled VARCHAR(1),
    created_time timestamp NOT NULL,
    created_name VARCHAR(255) NOT NULL,
    by_fp VARCHAR(1) NOT NULL,
    pd_type VARCHAR(1) NOT NULL,
    pd_br VARCHAR(9999),
    PRIMARY KEY (pd_record_id)
);

COMMENT ON TABLE t_sys_pd_record IS '友臣盘点记录表';
COMMENT ON COLUMN t_sys_pd_record.pd_record_id IS 'id';
COMMENT ON COLUMN t_sys_pd_record.pd_time IS '盘点时间';
COMMENT ON COLUMN t_sys_pd_record.material_number IS '材料编码';
COMMENT ON COLUMN t_sys_pd_record.material_name IS '材料名称';
COMMENT ON COLUMN t_sys_pd_record.material_specifications IS '材料规格';
COMMENT ON COLUMN t_sys_pd_record.pd_unit IS '单位';
COMMENT ON COLUMN t_sys_pd_record.pd_unit_str IS '单位名称';
COMMENT ON COLUMN t_sys_pd_record.pd_qty IS '盘点数量';
COMMENT ON COLUMN t_sys_pd_record.pd_created_name IS '盘点人姓名';
COMMENT ON COLUMN t_sys_pd_record.pd_created_id IS '盘点人id';
COMMENT ON COLUMN t_sys_pd_record.pd_workshop_name IS '盘点车间名称';
COMMENT ON COLUMN t_sys_pd_record.pd_workshop_number IS '盘点车间编码';
COMMENT ON COLUMN t_sys_pd_record.pd_workshop_leader_name IS '盘点车间主任名称';
COMMENT ON COLUMN t_sys_pd_record.pd_workshop_leader_id IS '盘点车间主任id';
COMMENT ON COLUMN t_sys_pd_record.pd_class_name IS '盘点班组名称';
COMMENT ON COLUMN t_sys_pd_record.pd_class_number IS '盘点班组编码';
COMMENT ON COLUMN t_sys_pd_record.by_deteled IS '是否删除 0：否 1：是';
COMMENT ON COLUMN t_sys_pd_record.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_pd_record.created_name IS '创建人（用户名）';
COMMENT ON COLUMN t_sys_pd_record.by_fp IS '是否已复盘 0:否 1：是';
COMMENT ON COLUMN t_sys_pd_record.pd_type IS '盘点类型 0：盘点 1：复盘';
COMMENT ON COLUMN t_sys_pd_record.pd_br IS '备注';

CREATE INDEX idx_pd_type ON t_sys_pd_record(pd_type);
CREATE INDEX idx_pd_time ON t_sys_pd_record(pd_time);

ALTER TABLE public.t_sys_pd_record ADD re_pd_record_id int4 NULL;
COMMENT ON COLUMN public.t_sys_pd_record.re_pd_record_id IS ' 原盘点记录id';

-- 2025-7-10
ALTER TABLE public.t_sys_pd_record ADD pd_time_str varchar NULL;
COMMENT ON COLUMN public.t_sys_pd_record.pd_time_str IS '盘点日期（格式yyyy-MM-dd）';

ALTER TABLE public.t_sys_pd_record RENAME COLUMN by_deteled TO by_deleted;

-- 2025-7-31
CREATE TABLE t_sys_pd_record_split(
    pd_record_split_id serial NOT NULL,
    pd_time timestamp NOT NULL,
    material_number VARCHAR(255),
    material_name VARCHAR(255),
    material_specifications VARCHAR(255),
    pd_unit VARCHAR(255),
    pd_unit_str VARCHAR(255),
    pd_qty NUMERIC(24,6),
    pd_created_name VARCHAR(255),
    pd_created_id VARCHAR(255),
    pd_workshop_name VARCHAR(255),
    pd_workshop_number VARCHAR(255),
    pd_workshop_leader_name VARCHAR(255),
    pd_workshop_leader_id VARCHAR(255),
    pd_class_name VARCHAR(255),
    pd_class_number VARCHAR(255),
    by_deteled VARCHAR(1),
    created_time timestamp NOT NULL,
    created_name VARCHAR(255) NOT NULL,
    by_fp VARCHAR(1),
    pd_type VARCHAR(1),
    pd_br VARCHAR(9999),
    re_pd_record_id INTEGER,
    pd_time_str VARCHAR(255),
    PRIMARY KEY (pd_record_split_id)
);

COMMENT ON TABLE t_sys_pd_record_split IS '友臣盘点拆分记录表';
COMMENT ON COLUMN t_sys_pd_record_split.pd_record_split_id IS 'id';
COMMENT ON COLUMN t_sys_pd_record_split.pd_time IS '盘点时间';
COMMENT ON COLUMN t_sys_pd_record_split.material_number IS '材料编码';
COMMENT ON COLUMN t_sys_pd_record_split.material_name IS '材料名称';
COMMENT ON COLUMN t_sys_pd_record_split.material_specifications IS '材料规格';
COMMENT ON COLUMN t_sys_pd_record_split.pd_unit IS '单位';
COMMENT ON COLUMN t_sys_pd_record_split.pd_unit_str IS '单位名称';
COMMENT ON COLUMN t_sys_pd_record_split.pd_qty IS '盘点数量';
COMMENT ON COLUMN t_sys_pd_record_split.pd_created_name IS '盘点人姓名';
COMMENT ON COLUMN t_sys_pd_record_split.pd_created_id IS '盘点人id';
COMMENT ON COLUMN t_sys_pd_record_split.pd_workshop_name IS '盘点车间名称';
COMMENT ON COLUMN t_sys_pd_record_split.pd_workshop_number IS '盘点车间编码';
COMMENT ON COLUMN t_sys_pd_record_split.pd_workshop_leader_name IS '盘点车间主任名称';
COMMENT ON COLUMN t_sys_pd_record_split.pd_workshop_leader_id IS '盘点车间主任id';
COMMENT ON COLUMN t_sys_pd_record_split.pd_class_name IS '盘点班组名称';
COMMENT ON COLUMN t_sys_pd_record_split.pd_class_number IS '盘点班组编码';
COMMENT ON COLUMN t_sys_pd_record_split.by_deteled IS '是否删除 0：否 1：是';
COMMENT ON COLUMN t_sys_pd_record_split.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_pd_record_split.created_name IS '创建人（用户名）';
COMMENT ON COLUMN t_sys_pd_record_split.by_fp IS '是否已复盘 0:否 1：是';
COMMENT ON COLUMN t_sys_pd_record_split.pd_type IS '盘点类型 0：盘点 1：复盘';
COMMENT ON COLUMN t_sys_pd_record_split.pd_br IS '备注';
COMMENT ON COLUMN t_sys_pd_record_split.re_pd_record_id IS '原盘点记录id';
COMMENT ON COLUMN t_sys_pd_record_split.pd_time_str IS '盘点日期（格式yyyy-MM-dd）';
