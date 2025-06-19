-- 班别 字典、人员相关sql
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
	CONSTRAINT t_sys_class_pkey PRIMARY KEY (class_id)
);


CREATE TABLE public.t_sys_class_personnel_rel (
	class_personnel_id int8 NOT NULL,
	class_id int8 NULL,
	personnel_id int8 NULL,
	crt_user varchar(255) NULL,
	crt_time timestamp NULL,
	update_time varchar(255) NULL,
	update_user timestamp NULL,
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

CREATE TABLE public.t_sys_personnel_info (
	personnel_id bigserial NOT NULL,
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
	CONSTRAINT t_sys_personnel_info_pkey PRIMARY KEY (personnel_id)
);
