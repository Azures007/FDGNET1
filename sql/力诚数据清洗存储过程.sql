-- public.t_tmp_bs_device_production_hours_01 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_01;

CREATE TABLE public.t_tmp_bs_device_production_hours_01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	max_ts int8 NULL
);


-- public.t_tmp_bs_device_production_hours_02 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_02;

CREATE TABLE public.t_tmp_bs_device_production_hours_02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	max_ts int8 NULL,
	production_num text NULL
);


-- public.t_tmp_bs_device_production_hours_03 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_03;

CREATE TABLE public.t_tmp_bs_device_production_hours_03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	knife_speed text NULL,
	pot_speed text NULL,
	poured_pot_number text NULL,
	processing_time text NULL,
	b_poured_pot_number text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_04 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_04;

CREATE TABLE public.t_tmp_bs_device_production_hours_04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	coiling_num text NULL,
	b_coiling_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_05 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_05;

CREATE TABLE public.t_tmp_bs_device_production_hours_05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL,
	b_production_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_06 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_06;

CREATE TABLE public.t_tmp_bs_device_production_hours_06 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL,
	b_production_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_07 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_07;

CREATE TABLE public.t_tmp_bs_device_production_hours_07 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL,
	b_production_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_08 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_08;

CREATE TABLE public.t_tmp_bs_device_production_hours_08 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	pot_num text NULL,
	b_pot_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_production_hours_09 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_production_hours_09;

CREATE TABLE public.t_tmp_bs_device_production_hours_09 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	run_num text NULL,
	b_run_num text NULL,
	flag int4 NULL
);


-- public.t_tmp_bs_device_status_hours_01 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_status_hours_01;

CREATE TABLE public.t_tmp_bs_device_status_hours_01 (
	times text NULL
);


-- public.t_tmp_bs_device_status_hours_02 definition

-- Drop table

-- DROP TABLE public.t_tmp_bs_device_status_hours_02;

CREATE TABLE public.t_tmp_bs_device_status_hours_02 (
	"name" varchar NULL,
	entity_id varchar NULL,
	ts int8 NULL,
	report_date varchar NULL,
	report_time varchar NULL,
	status varchar NULL
);


-- public.t_tmp_cln_coilingmachine_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_coilingmachine_t;

CREATE TABLE public.t_tmp_cln_coilingmachine_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	coiling_num text NULL
);


-- public.t_tmp_cln_crabline01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline01;

CREATE TABLE public.t_tmp_cln_crabline01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_crabline02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline02;

CREATE TABLE public.t_tmp_cln_crabline02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_crabline03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline03;

CREATE TABLE public.t_tmp_cln_crabline03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_production_num text NULL
);


-- public.t_tmp_cln_crabline04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline04;

CREATE TABLE public.t_tmp_cln_crabline04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);


-- public.t_tmp_cln_crabline05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline05;

CREATE TABLE public.t_tmp_cln_crabline05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	production_num text NULL
);


-- public.t_tmp_cln_crabline_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t;

CREATE TABLE public.t_tmp_cln_crabline_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL,
	test_production_num text NULL,
	test_time text NULL
);


-- public.t_tmp_cln_crabline_t01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t01;

CREATE TABLE public.t_tmp_cln_crabline_t01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_crabline_t02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t02;

CREATE TABLE public.t_tmp_cln_crabline_t02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_crabline_t03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t03;

CREATE TABLE public.t_tmp_cln_crabline_t03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_production_num text NULL
);


-- public.t_tmp_cln_crabline_t04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t04;

CREATE TABLE public.t_tmp_cln_crabline_t04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);


-- public.t_tmp_cln_crabline_t05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_crabline_t05;

CREATE TABLE public.t_tmp_cln_crabline_t05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	production_num text NULL
);


-- public.t_tmp_cln_cuttingmachine01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine01;

CREATE TABLE public.t_tmp_cln_cuttingmachine01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_cuttingmachine02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine02;

CREATE TABLE public.t_tmp_cln_cuttingmachine02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_cuttingmachine03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine03;

CREATE TABLE public.t_tmp_cln_cuttingmachine03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_production_num text NULL
);


-- public.t_tmp_cln_cuttingmachine04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine04;

CREATE TABLE public.t_tmp_cln_cuttingmachine04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);


-- public.t_tmp_cln_cuttingmachine05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine05;

CREATE TABLE public.t_tmp_cln_cuttingmachine05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	production_num text NULL
);


-- public.t_tmp_cln_cuttingmachine_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL,
	test_production_num text NULL,
	test_time text NULL
);


-- public.t_tmp_cln_cuttingmachine_t01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t01;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_cuttingmachine_t02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t02;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_cuttingmachine_t03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t03;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_production_num text NULL
);


-- public.t_tmp_cln_cuttingmachine_t04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t04;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);


-- public.t_tmp_cln_cuttingmachine_t05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_cuttingmachine_t05;

CREATE TABLE public.t_tmp_cln_cuttingmachine_t05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	production_num text NULL
);


-- public.t_tmp_cln_device_status_second_01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_device_status_second_01;

CREATE TABLE public.t_tmp_cln_device_status_second_01 (
	"name" varchar(255) NULL,
	max_ts int8 NULL
);


-- public.t_tmp_cln_device_status_second_02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_device_status_second_02;

CREATE TABLE public.t_tmp_cln_device_status_second_02 (
	"name" varchar NULL,
	entity_id varchar NULL,
	ts int8 NULL,
	report_date varchar NULL,
	report_time varchar NULL,
	status varchar NULL
);


-- public.t_tmp_cln_mixingpot_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_mixingpot_t;

CREATE TABLE public.t_tmp_cln_mixingpot_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	knife_speed text NULL,
	pot_speed text NULL,
	poured_pot_number text NULL,
	processing_time text NULL
);


-- public.t_tmp_cln_packline_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_packline_t;

CREATE TABLE public.t_tmp_cln_packline_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	production_num text NULL
);


-- public.t_tmp_cln_sterilizepot_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_sterilizepot_t;

CREATE TABLE public.t_tmp_cln_sterilizepot_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	pot_num text NULL,
	start_time text NULL,
	end_time text NULL,
	alarm_time text NULL
);


-- public.t_tmp_cln_stretchmachine01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine01;

CREATE TABLE public.t_tmp_cln_stretchmachine01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_stretchmachine02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine02;

CREATE TABLE public.t_tmp_cln_stretchmachine02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_stretchmachine03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine03;

CREATE TABLE public.t_tmp_cln_stretchmachine03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	molding_time text NULL
);


-- public.t_tmp_cln_stretchmachine04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine04;

CREATE TABLE public.t_tmp_cln_stretchmachine04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	sealing_time text NULL
);


-- public.t_tmp_cln_stretchmachine05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine05;

CREATE TABLE public.t_tmp_cln_stretchmachine05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	vacuum_time text NULL
);


-- public.t_tmp_cln_stretchmachine06 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine06;

CREATE TABLE public.t_tmp_cln_stretchmachine06 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	run_num text NULL
);


-- public.t_tmp_cln_stretchmachine07 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine07;

CREATE TABLE public.t_tmp_cln_stretchmachine07 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_run_num text NULL
);


-- public.t_tmp_cln_stretchmachine08 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine08;

CREATE TABLE public.t_tmp_cln_stretchmachine08 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);


-- public.t_tmp_cln_stretchmachine_t definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t;

CREATE TABLE public.t_tmp_cln_stretchmachine_t (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	report_date text NULL,
	report_time text NULL,
	status text NULL,
	molding_time text NULL,
	sealing_time text NULL,
	vacuum_time text NULL,
	run_num text NULL,
	test_run_num text NULL,
	test_time text NULL
);


-- public.t_tmp_cln_stretchmachine_t01 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t01;

CREATE TABLE public.t_tmp_cln_stretchmachine_t01 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL
);


-- public.t_tmp_cln_stretchmachine_t02 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t02;

CREATE TABLE public.t_tmp_cln_stretchmachine_t02 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	status text NULL
);


-- public.t_tmp_cln_stretchmachine_t03 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t03;

CREATE TABLE public.t_tmp_cln_stretchmachine_t03 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	molding_time text NULL
);


-- public.t_tmp_cln_stretchmachine_t04 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t04;

CREATE TABLE public.t_tmp_cln_stretchmachine_t04 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	sealing_time text NULL
);


-- public.t_tmp_cln_stretchmachine_t05 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t05;

CREATE TABLE public.t_tmp_cln_stretchmachine_t05 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	vacuum_time text NULL
);


-- public.t_tmp_cln_stretchmachine_t06 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t06;

CREATE TABLE public.t_tmp_cln_stretchmachine_t06 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	run_num text NULL
);


-- public.t_tmp_cln_stretchmachine_t07 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t07;

CREATE TABLE public.t_tmp_cln_stretchmachine_t07 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_run_num text NULL
);


-- public.t_tmp_cln_stretchmachine_t08 definition

-- Drop table

-- DROP TABLE public.t_tmp_cln_stretchmachine_t08;

CREATE TABLE public.t_tmp_cln_stretchmachine_t08 (
	"name" varchar(255) NULL,
	entity_id uuid NULL,
	ts int8 NULL,
	test_time text NULL
);



CREATE OR REPLACE PROCEDURE public.pc_all_job_running(IN i_run_date character varying)
 LANGUAGE plpgsql
AS $procedure$
DECLARE
v_run_date character varying;
v_sql text;
-- o_ret_code integer default 0;
o_ret_note1 character varying;
v_start_time timestamp;
v_begin_time timestamp;
v_prc character varying;
job_group record;
job_list record;
begin

  v_prc:='pc_all_job_running';
  v_begin_time:=clock_timestamp();
  --v_run_date :=to_char(sysdate-1,'yyyy-mm-dd');
  v_run_date :=i_run_date;


    for job_group in (select group_id
                        from t_mid_run_job_group a
                       where a.enabled_st = '1'
                       order by a.job_group_sn) loop
            for job_list in (select *
                               from t_mid_run_job_list a
                              where a.enabled_st = '1'
                                and a.group_id = job_group.group_id
                              order by a.job_sn) loop
					declare
					o_ret_code integer default 0;
					o_ret_note character varying default 'not';
                      begin
                            --每日跑数 JFRQ0001
                            if (job_list.job_frq='JFRQ0001') then
                              v_sql := ' call ' || job_list.job_name || '('||chr(39) ||chr(39)  || '::character varying,'  ||chr(39)  || v_run_date||chr(39)  || '::character varying,' ||chr(39) || v_run_date||chr(39) ||'::character varying,'||0|| '::integer,'||chr(39) ||chr(39)  || '::character varying); ';

                              --执行跑数脚本
                              v_start_time:=clock_timestamp();
                               execute  v_sql into o_ret_code,o_ret_note;
                               o_ret_note:='运行时长:'||round(extract(epoch from (clock_timestamp()-v_start_time))::numeric)||'秒>>>'||o_ret_note;
                              --错误则回滚
                              if o_ret_code=-1 then
--                                 rollback;
                                call pc_insert_run_job_log('1'::character varying,job_list.job_name::character varying,v_sql::character varying,o_ret_code::character varying,o_ret_note::character varying);
                                --exit;
                              end if;
                              call pc_insert_run_job_log('1'::character varying,job_list.job_name::character varying,v_sql::character varying,o_ret_code::character varying,o_ret_note::character varying);
                            end if;
                      exception
                        when others then
                          call pc_insert_run_job_log('2'::character varying,job_list.job_name::character varying,v_sql::character varying,SQLSTATE::character varying,SQLERRM::character varying);
                      end;
            end loop;
--              commit;
    end loop;

--   commit;

      --更新作业状态
      update t_mid_run_job_list t1
         set (run_date, job_st) =
             (select job_date, error_type
                from (select t1.job_id,
                             coalesce(t2.job_date, to_char(now(), 'yyyy-mm-dd')) job_date,
                             coalesce(t2.error_type, '0') error_type
                        from t_mid_run_job_list t1
                        left join (select a.job_id,
                                         a.job_date,
                                         min(a.error_type) error_type
                                    from t_mid_run_job_log a
                                   where a.job_date =
                                         to_char(now(), 'yyyy-mm-dd')
                                   group by a.job_id, a.job_date) t2
                          on t1.job_id = t2.job_id) t2
               where t1.job_id = t2.job_id)
       where exists (select 1
                from (select t1.job_id,
                             coalesce(t2.job_date, to_char(now(), 'yyyy-mm-dd')) job_date,
                             coalesce(t2.error_type, '0') error_type
                        from t_mid_run_job_list t1
                        left join (select a.job_id,
                                         a.job_date,
                                         min(a.error_type) error_type
                                    from t_mid_run_job_log a
                                   where a.job_date =
                                         to_char(now(), 'yyyy-mm-dd')
                                   group by a.job_id, a.job_date) t2
                          on t1.job_id = t2.job_id) t2
               where t1.job_id = t2.job_id);

  o_ret_note1:='运行时长:'||round(extract(epoch from (clock_timestamp()-v_begin_time))::numeric)||'秒>>>';
  call pc_insert_run_job_log('1'::character varying,v_prc::character varying,''::character varying,1::character varying,o_ret_note1::character varying);
--   commit;

exception
  when others then
--     rollback;
    --插入日志,执行完毕
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
--     commit;
end;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_bs_device_production_hours(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$
DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
I_START_TIME character varying;
I_END_TIME character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;
--time_list record;

BEGIN
V_PRC := 'pc_bs_device_production_hours';
--V_JOB_START_TIME := now();
o_ret_code := 0;

-- 当天0点至24点
select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

select TO_CHAR(to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_START_TIME;
select TO_CHAR(to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_END_TIME;


-- 清空临时表
truncate table t_tmp_bs_device_production_hours_01;
truncate table t_tmp_bs_device_production_hours_02;
-- 斩拌锅临时表
truncate table t_tmp_bs_device_production_hours_03;
-- 倒盘机临时表
truncate table t_tmp_bs_device_production_hours_04;
-- 蟹柳线临时表
truncate table t_tmp_bs_device_production_hours_05;
--剥皮机临时表
truncate table t_tmp_bs_device_production_hours_06;
--包装线临时表
truncate table t_tmp_bs_device_production_hours_07;
--杀菌罐临时表
truncate table t_tmp_bs_device_production_hours_08;
--拉伸膜机临时表
truncate table t_tmp_bs_device_production_hours_09;


-- 删除清洗的区间字段
delete from t_bs_device_production_hours where ts>=I_START_TS and ts<=I_END_TS;

-- 获取所有设备跑数之前最新上报产量的时间戳
insert into t_tmp_bs_device_production_hours_01(
"name",entity_id,max_ts
)
select t.name,t.entity_id,max(t.ts) max_ts
from t_cln_mixingpot t where t.report_time < I_START_TIME
and poured_pot_number is not null
group by t.name,t.entity_id
union all
select t1.name,t1.entity_id,max(t1.ts) max_ts
from t_cln_coilingmachine t1 where t1.report_time < I_START_TIME
and t1.coiling_num is not null
group by t1.name,t1.entity_id
union all
select t2.name,t2.entity_id,max(t2.ts) max_ts
from t_cln_crabline t2 where t2.report_time < I_START_TIME
and t2.production_num is not null
group by t2.name,t2.entity_id
union all
select t3.name,t3.entity_id,max(t3.ts) max_ts
from t_cln_cuttingmachine t3 where t3.report_time < I_START_TIME
and t3.production_num is not null
group by t3.name,t3.entity_id
union all
select t4.name,t4.entity_id,max(t4.ts) max_ts
from t_cln_packline t4 where t4.report_time < I_START_TIME
and t4.production_num is not null
group by t4.name,t4.entity_id
union all
select t5.name,t5.entity_id,max(t5.ts) max_ts
from t_cln_sterilizepot t5 where t5.report_time < I_START_TIME
and t5.pot_num is not null
group by t5.name,t5.entity_id
union all
select t6.name,t6.entity_id,max(t6.ts) max_ts
from t_cln_stretchmachine t6 where t6.report_time < I_START_TIME
and t6.run_num is not null
group by t6.name,t6.entity_id;

-- 获取所有设备跑数之前最新的产量数据
insert into t_tmp_bs_device_production_hours_02(
"name",entity_id,max_ts,production_num
)
select t."name",t.entity_id,t.max_ts,t1.coiling_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_coilingmachine t1
on t."name" =t1."name"  and t.entity_id =t1.entity_id and t.max_ts =t1.ts
union all
select t."name",t.entity_id,t.max_ts,t2.production_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_crabline t2
on t."name" =t2."name"  and t.entity_id =t2.entity_id and t.max_ts =t2.ts
union all
select t."name",t.entity_id,t.max_ts,t3.production_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_cuttingmachine t3
on t."name" =t3."name"  and t.entity_id =t3.entity_id and t.max_ts =t3.ts
union all
select t."name",t.entity_id,t.max_ts,t4.poured_pot_number as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_mixingpot t4
on t."name" =t4."name"  and t.entity_id =t4.entity_id and t.max_ts =t4.ts
union all
select t."name",t.entity_id,t.max_ts,t5.production_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_packline t5
on t."name" =t5."name"  and t.entity_id =t5.entity_id and t.max_ts =t5.ts
union all
select t."name",t.entity_id,t.max_ts,t6.pot_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_sterilizepot t6
on t."name" =t6."name"  and t.entity_id =t6.entity_id and t.max_ts =t6.ts
union all
select t."name",t.entity_id,t.max_ts,t7.run_num as production_num
from t_tmp_bs_device_production_hours_01 t
join t_cln_stretchmachine t7
on t."name" =t7."name"  and t.entity_id =t7.entity_id and t.max_ts =t7.ts ;



-- ================================================== 查询斩拌锅产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_03(
"name",entity_id,ts,report_date,report_time,status,knife_speed,pot_speed,poured_pot_number,processing_time,b_poured_pot_number,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,knife_speed,pot_speed,poured_pot_number,processing_time,b_poured_pot_number,flag
from (select t1.*,CASE
                     WHEN t1.b_poured_pot_number::bigint = t1.poured_pot_number::bigint THEN
                      0
                     WHEN t1.b_poured_pot_number::bigint > t1.poured_pot_number::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.poured_pot_number, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_poured_pot_number  -- 上一条锅数
from t_cln_mixingpot t where t.poured_pot_number is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_03 a set b_poured_pot_number=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_poured_pot_number is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(poured_pot_number as integer)-cast(b_poured_pot_number as integer) as production_num
from t_tmp_bs_device_production_hours_03;


-- ================================================== 查询导盘机产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_04(
"name",entity_id,ts,report_date,report_time,status,coiling_num,b_coiling_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,coiling_num,b_coiling_num,flag
from (select t1.*,CASE
                     WHEN t1.b_coiling_num::bigint = t1.coiling_num::bigint THEN
                      0
                     WHEN t1.b_coiling_num::bigint > t1.coiling_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.coiling_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_coiling_num  -- 上一条锅数
from t_cln_coilingmachine t where t.coiling_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_04 a set b_coiling_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_coiling_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(coiling_num as integer)-cast(b_coiling_num as integer) as production_num
from t_tmp_bs_device_production_hours_04;



-- ================================================== 查询蟹柳线产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_05(
"name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
from (select t1.*,CASE
                     WHEN t1.b_production_num::bigint = t1.production_num::bigint THEN
                      0
                     WHEN t1.b_production_num::bigint > t1.production_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.production_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_production_num  -- 上一条锅数
from t_cln_crabline t where t.production_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_05 a set b_production_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_production_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(production_num as integer)-cast(b_production_num as integer) as production_num
from t_tmp_bs_device_production_hours_05;




-- ================================================== 查询剥皮机产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_06(
"name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
from (select t1.*,CASE
                     WHEN t1.b_production_num::bigint = t1.production_num::bigint THEN
                      0
                     WHEN t1.b_production_num::bigint > t1.production_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.production_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_production_num  -- 上一条锅数
from t_cln_cuttingmachine t where t.production_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_06 a set b_production_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_production_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(production_num as integer)-cast(b_production_num as integer) as production_num
from t_tmp_bs_device_production_hours_06;



-- ================================================== 查询包装线产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_07(
"name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,production_num,b_production_num,flag
from (select t1.*,CASE
                     WHEN t1.b_production_num::bigint = t1.production_num::bigint THEN
                      0
                     WHEN t1.b_production_num::bigint > t1.production_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.production_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_production_num  -- 上一条锅数
from t_cln_packline t where t.production_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_07 a set b_production_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_production_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(production_num as integer)-cast(b_production_num as integer) as production_num
from t_tmp_bs_device_production_hours_07;





-- ================================================== 查询杀菌罐产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_08(
"name",entity_id,ts,report_date,report_time,status,pot_num,b_pot_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,pot_num,b_pot_num,flag
from (select t1.*,CASE
                     WHEN t1.b_pot_num::bigint = t1.pot_num::bigint THEN
                      0
                     WHEN t1.b_pot_num::bigint > t1.pot_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.pot_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_pot_num  -- 上一条锅数
from t_cln_sterilizepot t where t.pot_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_08 a set b_pot_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_pot_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(pot_num as integer)-cast(b_pot_num as integer) as pot_num
from t_tmp_bs_device_production_hours_08;




-- ================================================== 查询拉伸膜机产量数据 =====================================================
insert into t_tmp_bs_device_production_hours_09(
"name",entity_id,ts,report_date,report_time,status,run_num,b_run_num,flag
)
-- 防止设备数据重置时上报数据处于计算区间,根据前一条数据的锅数对数据进行标记，锅数无变化标伤为0，前一条锅数大于当前锅数说明设备数据重置归零标记为2，锅数正常增加标记为1
select "name",entity_id,ts,report_date,report_time,status,run_num,b_run_num,flag
from (select t1.*,CASE
                     WHEN t1.b_run_num::bigint = t1.run_num::bigint THEN
                      0
                     WHEN t1.b_run_num::bigint > t1.run_num::bigint then
                      2
                     ELSE
                      1
                   END flag from (
select *,
lag (t.run_num, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_run_num  -- 上一条锅数
from t_cln_stretchmachine t where t.run_num is not null
 and t.report_time >=I_START_TIME and t.report_time   <=I_END_TIME -- 时间区间条件
) t1) t2
where t2.flag in (1,2);

-- 更新斩拌锅，修补0点数据
update t_tmp_bs_device_production_hours_09 a set b_run_num=b.production_num
from t_tmp_bs_device_production_hours_02 b
where a.b_run_num is null
and a.entity_id=b.entity_id;

-- 插入结果表
insert into t_bs_device_production_hours(
"name",entity_id,ts,report_date,report_time,production_num
)
select "name",entity_id,ts,report_date,report_time,
cast(run_num as integer)-cast(b_run_num as integer) as run_num
from t_tmp_bs_device_production_hours_09;





  o_ret_code := 1;
  o_ret_note := '成功';
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_bs_device_status_hours(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$
DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
I_START_TIME character varying;
I_END_TIME character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;
time_list record;

BEGIN
V_PRC := 'pc_bs_device_status_hours';
--V_JOB_START_TIME := now();
o_ret_code := 0;

-- 当天0点至24点
select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

select TO_CHAR(to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_START_TIME;
select TO_CHAR(to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_END_TIME;


-- 清空临时表
truncate table t_tmp_bs_device_status_hours_01;
truncate table t_tmp_bs_device_status_hours_02;
-- 删除清洗的区间字段
delete from t_bs_device_status_hours where ts>=I_START_TS and ts<=I_END_TS;


-- 获取当天每个整点时间
insert into t_tmp_bs_device_status_hours_01(times)
select TO_CHAR(generate_series,'yyyy-mm-dd hh24:mi:ss:MS') times
from generate_series(to_timestamp (i_start_date || ' 01:00:00', 'yyyy-mm-dd hh24:mi:ss'),to_timestamp (i_start_date || ' 23:00:00', 'yyyy-mm-dd hh24:mi:ss'), '1 hour');


-- 获取当天的设备状态数据写入临时表
insert into t_tmp_bs_device_status_hours_02(
"name",entity_id,ts,report_date,report_time,status
)
select t."name",
t.entity_id ,
t.ts ,
t.report_date ,
t.report_time ,
t.status
from t_cln_device_status_second t
where t.report_time >=I_START_TIME
and t.report_time <=I_END_TIME;



-- 在每日的每个整点插入锚点，方便整点查询
for time_list in (select times from t_tmp_bs_device_status_hours_01) loop
		declare

                      begin
	                      	insert into t_tmp_bs_device_status_hours_02(
	                      	"name",entity_id,ts,report_date,report_time,status
	                      	)
	                      	select t1."name",
								t1.entity_id,
								extract(epoch from (to_timestamp (time_list.times, 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 as ts,
								to_char(to_timestamp (time_list.times, 'yyyy-mm-dd hh24:mi:ss:MS'), 'yyyy-mm-dd') as report_date,
								to_char(to_timestamp (time_list.times, 'yyyy-mm-dd hh24:mi:ss:MS'), 'yyyy-mm-dd hh24:mi:ss') as report_time,
								t1.status
							from (
								select "name",entity_id,ts,report_date,report_time,status,
								row_number() OVER (PARTITION BY t.entity_id ORDER BY report_time desc) rn
								from t_tmp_bs_device_status_hours_02 t
								where t.report_time<=time_list.times
							) t1 where t1.rn=1;
                      end;
    end loop;

-- 插入结果表
insert into t_bs_device_status_hours(
name,
entity_id,
ts,
report_date,
report_time,
status,
l_report_time,
l_status,
b_report_time,
b_status,
status_seconds
)
select
t3.name,
t3.entity_id,
t3.ts,
t3.report_date,
t3.report_time,
t3.status,
t3.l_report_time,
t3.l_status,
t3.b_report_time,
t3.b_status,
(select round(extract(epoch FROM (to_timestamp (t3.l_report_time, 'yyyy-mm-dd hh24:mi:ss') - to_timestamp (t3.report_time, 'yyyy-mm-dd hh24:mi:ss') ))::numeric)) status_seconds-- 取本条上报时间和下一条相差的秒数
from (
select t2.name,
t2.entity_id,
t2.ts,
t2.report_date,
t2.report_time,
t2.status,
lead (t2.report_time,1, I_END_TIME) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) l_report_time, -- 下一条上报时间
lead (t2.status, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) l_status,-- 下一条状态
lag (t2.report_time, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) b_report_time,-- 上一条上报时间
lag (t2.status, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) b_status  -- 上一条状态
from t_tmp_bs_device_status_hours_02 t2
) t3;

-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功';
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_coilingmachine(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_coilingmachine';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
-- truncate table t_tmp_cln_ts_kv_01;
-- 删除清洗的区间字段
delete from t_cln_coilingmachine where report_date=i_start_date;

-- 插入表
insert into t_cln_coilingmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
coiling_num
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.coiling_num
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as coiling_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='倒盘次数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.coiling_num
order by ts;
-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_coilingmachine_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_coilingmachine_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_coilingmachine_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    execute  'TRUNCATE TABLE t_tmp_cln_coilingmachine_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
--truncate table t_tmp_cln_coilingmachine_t;


-- 将增量数据插入临时表
insert into t_tmp_cln_coilingmachine_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
coiling_num
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.coiling_num
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_coilingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as coiling_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'coilingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='倒盘次数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.coiling_num
order by ts;

-- 插入目标表
insert into t_cln_coilingmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
coiling_num
)
select name,
entity_id,
ts,
report_date,
report_time,
status,
coiling_num
from t_tmp_cln_coilingmachine_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_coilingmachine a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);



--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;


-- COMMIT;

  -- o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    -- o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
    --PC_INSERT_RUN_JOB_LOG('2', V_PRC, '', -1, O_RET_NOTE);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_crabline(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_crabline';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
truncate table t_tmp_cln_crabline01;
truncate table t_tmp_cln_crabline02;
truncate table t_tmp_cln_crabline03;
truncate table t_tmp_cln_crabline04;
truncate table t_tmp_cln_crabline05;
-- 删除清洗的区间字段
delete from t_cln_crabline where report_date=i_start_date;

-- 查询时间戳主表
insert into t_tmp_cln_crabline01(
name,entity_id,ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts;

-- 插入运行状态临时表
insert into t_tmp_cln_crabline02(
name,entity_id,ts,status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态';


insert into t_tmp_cln_crabline03(
name,entity_id,ts,test_production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机模式生产支数';

insert into t_tmp_cln_crabline04(
name,entity_id,ts,test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间';

insert into t_tmp_cln_crabline05(
name,entity_id ,ts,production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式生产支数';


-- 插入结果表
insert into t_cln_crabline(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
e.production_num,
c.test_production_num,
d.test_time
from
t_tmp_cln_crabline01 a
left join t_tmp_cln_crabline02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_crabline03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_crabline04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_crabline05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.test_production_num,
d.test_time,
e.production_num
order by ts;


-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_crabline_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_crabline_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_crabline_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_crabline_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
truncate table t_tmp_cln_crabline_t01;
truncate table t_tmp_cln_crabline_t02;
truncate table t_tmp_cln_crabline_t03;
truncate table t_tmp_cln_crabline_t04;
truncate table t_tmp_cln_crabline_t05;



-- 查询时间戳主表
insert into t_tmp_cln_crabline_t01(
name,entity_id,ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts;

-- 插入运行状态临时表
insert into t_tmp_cln_crabline_t02(
name,entity_id,ts,status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态'
and not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);


insert into t_tmp_cln_crabline_t03(
name,entity_id,ts,test_production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机模式生产支数'
and not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_crabline_t04(
name,entity_id,ts,test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间'
and not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_crabline_t05(
name,entity_id ,ts,production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'crabLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式生产支数'
and not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);


-- 合并临时表
insert into t_tmp_cln_crabline_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
e.production_num,
c.test_production_num,
d.test_time
from
t_tmp_cln_crabline_t01 a
left join t_tmp_cln_crabline_t02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_crabline_t03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_crabline_t04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_crabline_t05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
where not exists (select 1 from t_tmp_cln_crabline_t i where i.entity_id=a.entity_id and i.ts=a.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by
a.name,a.entity_id,a.ts,
b.status,
c.test_production_num,
d.test_time,
e.production_num
order by ts;



-- 插入目标表
insert into t_cln_crabline(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
from t_tmp_cln_crabline_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_crabline a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);



--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_cuttingmachine(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$

DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_cuttingmachine';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
truncate table t_tmp_cln_cuttingmachine01;
truncate table t_tmp_cln_cuttingmachine02;
truncate table t_tmp_cln_cuttingmachine03;
truncate table t_tmp_cln_cuttingmachine04;
truncate table t_tmp_cln_cuttingmachine05;
-- 删除清洗的区间字段
delete from t_cln_cuttingmachine where report_date=i_start_date;

-- 插入时间戳主表
insert into t_tmp_cln_cuttingmachine01(
name,entity_id,ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts;

insert into t_tmp_cln_cuttingmachine02(
name,entity_id,ts,status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态';

insert into t_tmp_cln_cuttingmachine03(
name,entity_id,ts,test_production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机模式生产支数';

insert into t_tmp_cln_cuttingmachine04(
name,entity_id,ts,test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间';

insert into t_tmp_cln_cuttingmachine05(
name,entity_id,ts,production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式生产支数';

-- 插入结果表
insert into t_cln_cuttingmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
e.production_num,
c.test_production_num,
d.test_time
from
t_tmp_cln_cuttingmachine01 a
left join t_tmp_cln_cuttingmachine02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_cuttingmachine03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_cuttingmachine04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_cuttingmachine05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.test_production_num,
d.test_time,
e.production_num
order by ts;

-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_cuttingmachine_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_cuttingmachine_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_cuttingmachine_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_cuttingmachine_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;






-- 清空临时表
truncate table t_tmp_cln_cuttingmachine_t01;
truncate table t_tmp_cln_cuttingmachine_t02;
truncate table t_tmp_cln_cuttingmachine_t03;
truncate table t_tmp_cln_cuttingmachine_t04;
truncate table t_tmp_cln_cuttingmachine_t05;


-- 插入时间戳主表
insert into t_tmp_cln_cuttingmachine_t01(
name,entity_id,ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts;

insert into t_tmp_cln_cuttingmachine_t02(
name,entity_id,ts,status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态'
and not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_cuttingmachine_t03(
name,entity_id,ts,test_production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机模式生产支数'
and not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_cuttingmachine_t04(
name,entity_id,ts,test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间'
and not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_cuttingmachine_t05(
name,entity_id,ts,production_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'cuttingMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式生产支数'
and not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);



-- 合并临时表
insert into t_tmp_cln_cuttingmachine_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
e.production_num,
c.test_production_num,
d.test_time
from
t_tmp_cln_cuttingmachine_t01 a
left join t_tmp_cln_cuttingmachine_t02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_cuttingmachine_t03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_cuttingmachine_t04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_cuttingmachine_t05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
where not exists (select 1 from t_tmp_cln_cuttingmachine_t i where i.entity_id=a.entity_id and i.ts=a.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by
a.name,a.entity_id,a.ts,
b.status,
c.test_production_num,
d.test_time,
e.production_num
order by ts;



-- 插入结果表
insert into t_cln_cuttingmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
)
select
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num,
test_production_num,
test_time
from t_tmp_cln_cuttingmachine_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_cuttingmachine a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);





--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_device_status(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$
DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_device_status';
--V_JOB_START_TIME := now();
o_ret_code := 0;


-- 当天0点至24点
select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
-- truncate table t_tmp_cln_device_status_01;
-- 删除清洗的区间字段
delete from t_cln_device_status where ts>=I_START_TS and ts<=I_END_TS;


-- 插入结果表
insert into t_cln_device_status(
name,
entity_id,
ts,
report_date,
report_time,
status
)
select
t."name" ,
t.entity_id ,
t.ts ,
t.report_date ,
t.report_time ,
t.status
from t_cln_coilingmachine t
where t.ts >=I_START_TS
and t.ts<=I_END_TS
and t.status is not null
union all
select
t1."name" ,
t1.entity_id ,
t1.ts ,
t1.report_date ,
t1.report_time ,
t1.status
from t_cln_crabline t1
where t1.ts >=I_START_TS
and t1.ts<=I_END_TS
and t1.status is not null
union all
select
t2."name" ,
t2.entity_id ,
t2.ts ,
t2.report_date ,
t2.report_time ,
t2.status
from t_cln_cuttingmachine t2
where t2.ts >=I_START_TS
and t2.ts<=I_END_TS
and t2.status is not null
union all
select
t3."name" ,
t3.entity_id ,
t3.ts ,
t3.report_date ,
t3.report_time ,
t3.status
from t_cln_mixingpot t3
where t3.ts >=I_START_TS
and t3.ts<=I_END_TS
and t3.status is not null
union all
select
t4."name" ,
t4.entity_id ,
t4.ts ,
t4.report_date ,
t4.report_time ,
t4.status
from t_cln_packline t4
where t4.ts >=I_START_TS
and t4.ts<=I_END_TS
and t4.status is not null
union all
select
t5."name" ,
t5.entity_id ,
t5.ts ,
t5.report_date ,
t5.report_time ,
t5.status
from t_cln_sterilizepot t5
where t5.ts >=I_START_TS
and t5.ts<=I_END_TS
and t5.status is not null
union all
select
t6."name" ,
t6.entity_id ,
t6.ts ,
t6.report_date ,
t6.report_time ,
t6.status
from t_cln_stretchmachine t6
where t6.ts >=I_START_TS
and t6.ts<=I_END_TS
and t6.status is not null;

-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_device_status_second(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$
DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
I_START_TIME character varying;
I_END_TIME character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_device_status_second';
--V_JOB_START_TIME := now();
o_ret_code := 0;

-- 当天0点至24点
select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

select TO_CHAR(to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_START_TIME;
select TO_CHAR(to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS'),'yyyy-mm-dd hh24:mi:ss') into I_END_TIME;


-- 清空临时表
truncate table t_tmp_cln_device_status_second_01;
truncate table t_tmp_cln_device_status_second_02;
-- 删除清洗的区间字段
delete from t_cln_device_status_second where ts>=I_START_TS and ts<=I_END_TS;



-- 查询跑数之前最新的设备状态
insert into t_tmp_cln_device_status_second_01(
name,max_ts
)
SELECT t2."name" ,
max(ts) max_ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t1."key"='运行状态'
and t.ts<I_START_TS
group by t2."name";

insert into t_tmp_cln_device_status_second_02(
name,entity_id,ts,report_date,report_time,status
)
select
name,entity_id,ts,report_date,report_time,status
from t_cln_device_status t
where exists (select 1 from t_tmp_cln_device_status_second_01 t1 where t1."name"=t."name" and t.ts=t1.max_ts);

-- 补齐每日0点至第一条数据的空白
INSERT INTO public.t_cln_device_status ("name", entity_id, ts, report_date, report_time, status)
select "name", entity_id, I_START_TS, i_start_date, I_START_TIME, status from t_tmp_cln_device_status_second_02;

-- 插入表
insert into t_cln_device_status_second(
name,
entity_id,
ts,
report_date,
report_time,
status,
l_report_time,
l_status,
b_report_time,
b_status,
status_seconds
)
select
t3.name,
t3.entity_id,
t3.ts,
t3.report_date,
t3.report_time,
t3.status,
t3.l_report_time,
t3.l_status,
t3.b_report_time,
t3.b_status,
(select round(extract(epoch FROM (to_timestamp (t3.l_report_time, 'yyyy-mm-dd hh24:mi:ss') - to_timestamp (t3.report_time, 'yyyy-mm-dd hh24:mi:ss') ))::numeric)) status_seconds-- 取本条上报时间和下一条相差的秒数
from (
select t2.name,
t2.entity_id,
t2.ts,
t2.report_date,
t2.report_time,
t2.status,
lead (t2.report_time,1, I_END_TIME) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) l_report_time, -- 下一条上报时间
lead (t2.status, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) l_status,-- 下一条状态
lag (t2.report_time, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) b_report_time,-- 上一条上报时间
lag (t2.status, 1, null) over(partition by t2.entity_id order by t2.entity_id,t2.ts ) b_status  -- 上一条上报时间
from (select t1.*,
CASE
                     WHEN t1.b_status = t1.status THEN
                      1
                     ELSE
                      0
                   END flag
from (select
t.name,
t.entity_id,
t.ts,
t.report_date,
t.report_time,
t.status,
--lead (t.report_time,1,'2999-12-31 00:00:00') over(partition by t.report_date,t.entity_id order by t.entity_id,t.ts ) l_report_time, -- 下一条上报时间
--lead (t.status, 1, null) over(partition by t.report_date,t.entity_id order by t.entity_id,t.ts ) l_status,-- 下一条状态
--lag (t.report_time, 1, '2999-12-31 00:00:00') over(partition by t.report_date,t.entity_id order by t.entity_id,t.ts ) b_report_time,-- 上一条上报时间
lag (t.status, 1, null) over(partition by t.entity_id order by t.entity_id,t.ts ) b_status  -- 上一条上报时间
from t_cln_device_status t where t.ts >=I_START_TS and t.ts <=I_END_TS) t1) t2 where t2.flag=0
) t3;

-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功';
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_mixingpot(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$

DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_mixingpot';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
-- truncate table t_tmp_cln_ts_kv_01;
-- 删除清洗的区间字段
delete from t_cln_mixingpot where report_date=i_start_date;

-- 插入表
insert into t_cln_mixingpot(
name,
entity_id,
ts,
report_date,
report_time,
status,
knife_speed,
pot_speed,
poured_pot_number,
processing_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.knife_speed,
d.pot_speed,
e.poured_pot_number,
f.processing_time
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as knife_speed
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='刀转速') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as pot_speed
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='锅转速') d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as poured_pot_number
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='倒锅次数') e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as processing_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每锅加工时间') f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.knife_speed,
d.pot_speed,
e.poured_pot_number,
f.processing_time
order by ts;
-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_mixingpot_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_mixingpot_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_mixingpot_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_mixingpot_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;






-- 插入表
insert into t_tmp_cln_mixingpot_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
knife_speed,
pot_speed,
poured_pot_number,
processing_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.knife_speed,
d.pot_speed,
e.poured_pot_number,
f.processing_time
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_mixingpot_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as knife_speed
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='刀转速') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as pot_speed
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='锅转速') d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as poured_pot_number
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='倒锅次数') e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as processing_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'mixingPot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每锅加工时间') f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.knife_speed,
d.pot_speed,
e.poured_pot_number,
f.processing_time
order by ts;



-- 插入结果表
insert into t_cln_mixingpot(
name,
entity_id,
ts,
report_date,
report_time,
status,
knife_speed,
pot_speed,
poured_pot_number,
processing_time
)
select
name,
entity_id,
ts,
report_date,
report_time,
status,
knife_speed,
pot_speed,
poured_pot_number,
processing_time
from t_tmp_cln_mixingpot_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_mixingpot a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);





--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_packline(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$

DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_packline';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
-- truncate table t_tmp_cln_ts_kv_01;
-- 删除清洗的区间字段
delete from t_cln_packline where report_date=i_start_date;

-- 插入表
insert into t_cln_packline(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.production_num
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='生产件数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.production_num
order by ts;
-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_packline_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_packline_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_packline_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_packline_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;



-- 插入表
insert into t_tmp_cln_packline_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.production_num
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_packline_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as production_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'packLine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='生产件数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.production_num
order by ts;



-- 插入结果表
insert into t_cln_packline(
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num
)
select
name,
entity_id,
ts,
report_date,
report_time,
status,
production_num
from t_tmp_cln_packline_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_packline a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);





--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_sterilizepot(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$

DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_sterilizepot';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
-- truncate table t_tmp_cln_ts_kv_01;
-- 删除清洗的区间字段
delete from t_cln_sterilizepot where report_date=i_start_date;

-- 插入表
insert into t_cln_sterilizepot(
name,
entity_id,
ts,
report_date,
report_time,
status,
pot_num,
start_time,
end_time,
alarm_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.pot_num,
d.start_time,
e.end_time,
f.alarm_time
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as pot_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='罐数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as start_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每一罐杀菌起始点') d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as end_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每一罐杀菌结束点') e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as alarm_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='杀菌告警时间') f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.pot_num,
d.start_time,
e.end_time,
f.alarm_time
order by ts;


-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_sterilizepot_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_sterilizepot_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_sterilizepot_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_sterilizepot_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;







-- 插入表
insert into t_tmp_cln_sterilizepot_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
pot_num,
start_time,
end_time,
alarm_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.pot_num,
d.start_time,
e.end_time,
f.alarm_time
from
(select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_sterilizepot_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts) a
left join (select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态') b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as pot_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='罐数') c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as start_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每一罐杀菌起始点') d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as end_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='每一罐杀菌结束点') e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join (select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as alarm_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'sterilizePot%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='杀菌告警时间') f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.pot_num,
d.start_time,
e.end_time,
f.alarm_time
order by ts;



-- 插入结果表
insert into t_cln_sterilizepot(
name,
entity_id,
ts,
report_date,
report_time,
status,
pot_num,
start_time,
end_time,
alarm_time
)
select
name,
entity_id,
ts,
report_date,
report_time,
status,
pot_num,
start_time,
end_time,
alarm_time
from t_tmp_cln_sterilizepot_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_sterilizepot a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);





--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_stretchmachine(IN i_corp_id character varying, IN i_start_date character varying, IN i_end_date character varying, INOUT o_ret_code integer, INOUT o_ret_note character varying)
 LANGUAGE plpgsql
AS $procedure$

DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_stretchmachine';
--V_JOB_START_TIME := now();
o_ret_code := 0;

select extract(epoch from (to_timestamp (i_start_date || ' 00:00:00:000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_start_date || ' 23:59:59:999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;

-- 清空临时表
truncate table t_tmp_cln_stretchmachine01;
truncate table t_tmp_cln_stretchmachine02;
truncate table t_tmp_cln_stretchmachine03;
truncate table t_tmp_cln_stretchmachine04;
truncate table t_tmp_cln_stretchmachine05;
truncate table t_tmp_cln_stretchmachine06;
truncate table t_tmp_cln_stretchmachine07;
truncate table t_tmp_cln_stretchmachine08;
-- 删除清洗的区间字段
delete from t_cln_stretchmachine where report_date=i_start_date;

-- 获取时间表做主表
insert into t_tmp_cln_stretchmachine01(
name,
entity_id,
ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
group by t2."name",t.entity_id,t.ts;

-- 获取拉伸膜机状态
insert into t_tmp_cln_stretchmachine02(
name,entity_id,ts,
status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态';

-- 获取成型时间
insert into t_tmp_cln_stretchmachine03(
name,entity_id,ts,
molding_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as molding_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='成型时间';

-- 获取封口时间
insert into t_tmp_cln_stretchmachine04(
name,entity_id,ts,
sealing_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as sealing_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='封口时间';

insert into t_tmp_cln_stretchmachine05(
name,entity_id,ts,
vacuum_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as vacuum_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='真空时间';

insert into t_tmp_cln_stretchmachine06(
name,entity_id,ts,
run_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as run_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式跑动次数';

insert into t_tmp_cln_stretchmachine07(
name,entity_id,ts,
test_run_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_run_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调试模式跑动次数';

insert into t_tmp_cln_stretchmachine08(
name,entity_id,ts,
test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间';

-- 插入结果表
insert into t_cln_stretchmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
molding_time,
sealing_time,
vacuum_time,
run_num,
test_run_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.molding_time,
d.sealing_time,
e.vacuum_time,
f.run_num,
g.test_run_num,
h.test_time
from
t_tmp_cln_stretchmachine01 a
left join t_tmp_cln_stretchmachine02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_stretchmachine03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_stretchmachine04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_stretchmachine05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join t_tmp_cln_stretchmachine06 f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
left join t_tmp_cln_stretchmachine07 g
on g.name=a.name
and a.entity_id=g.entity_id
and a.ts =g.ts
left join t_tmp_cln_stretchmachine08 h
on h.name=a.name
and a.entity_id=h.entity_id
and a.ts =h.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.molding_time,
d.sealing_time,
e.vacuum_time,
f.run_num,
g.test_run_num,
h.test_time
order by ts;

-- COMMIT;

  o_ret_code := 1;
  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_code := -1;
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_cln_stretchmachine_t(IN i_run_time character varying)
 LANGUAGE plpgsql
AS $procedure$


DECLARE
V_PRC character varying;
V_JOB_START_TIME timestamp;
I_START_TS bigint;
I_END_TS bigint;
O_RET_NOTE character varying;
V_START_TIME     character varying;
V_TMP_MAX_TIME   character varying;
--O_RET_CODE_VALUE integer DEFAULT 0;

BEGIN
V_PRC := 'pc_cln_stretchmachine_t';
V_JOB_START_TIME := clock_timestamp();
--o_ret_code := 0;

--凌晨0点到6点不运行
  IF SUBSTR(i_run_time, 12, 2) >= '00' AND
     SUBSTR(i_run_time, 12, 2) <= '05' THEN
    RETURN;
  END IF;


  --开始时间取60分钟前
  V_START_TIME := TO_CHAR(to_timestamp (i_run_time, 'yyyy-mm-dd hh24:mi:ss')-interval '1 hours','yyyy-mm-dd hh24:mi:ss');

  --获取临时表最大时间
  SELECT MAX(report_time)
    INTO V_TMP_MAX_TIME
    FROM t_tmp_cln_stretchmachine_t
   WHERE report_time >= SUBSTR(i_run_time, 1, 10) || ' 00:00:00'
     AND report_time <= i_run_time;

  --隔天后就删除昨天数据，并获取开始时间
  IF V_TMP_MAX_TIME IS NULL OR
     SUBSTR(V_TMP_MAX_TIME, 1, 10) <> SUBSTR(i_run_time, 1, 10) THEN
    EXECUTE 'TRUNCATE TABLE t_tmp_cln_stretchmachine_t';
    V_START_TIME := SUBSTR(i_run_time, 1, 10) || ' 00:00:00';
  END IF;




 -- 将当天的数据抓取至临时表
-- 计算开始和结束时间的时间戳
select extract(epoch from (to_timestamp (V_START_TIME || ':000', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_START_TS;
select extract(epoch from (to_timestamp (i_run_time || ':999', 'yyyy-mm-dd hh24:mi:ss:MS')))*1000 into I_END_TS;



-- 清空临时表
truncate table t_tmp_cln_stretchmachine_t01;
truncate table t_tmp_cln_stretchmachine_t02;
truncate table t_tmp_cln_stretchmachine_t03;
truncate table t_tmp_cln_stretchmachine_t04;
truncate table t_tmp_cln_stretchmachine_t05;
truncate table t_tmp_cln_stretchmachine_t06;
truncate table t_tmp_cln_stretchmachine_t07;
truncate table t_tmp_cln_stretchmachine_t08;


-- 获取时间表做主表
insert into t_tmp_cln_stretchmachine_t01(
name,
entity_id,
ts
)
select
t2."name",t.entity_id,t.ts
from ts_kv t
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS)
group by t2."name",t.entity_id,t.ts;

-- 获取拉伸膜机状态
insert into t_tmp_cln_stretchmachine_t02(
name,entity_id,ts,
status
)
select
t2."name",t.entity_id,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as status
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='运行状态'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

-- 获取成型时间
insert into t_tmp_cln_stretchmachine_t03(
name,entity_id,ts,
molding_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as molding_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='成型时间'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

-- 获取封口时间
insert into t_tmp_cln_stretchmachine_t04(
name,entity_id,ts,
sealing_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as sealing_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='封口时间'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_stretchmachine_t05(
name,entity_id,ts,
vacuum_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as vacuum_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='真空时间'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_stretchmachine_t06(
name,entity_id,ts,
run_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as run_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='正常模式跑动次数'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_stretchmachine_t07(
name,entity_id,ts,
test_run_num
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_run_num
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调试模式跑动次数'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

insert into t_tmp_cln_stretchmachine_t08(
name,entity_id,ts,
test_time
)
select
t2."name",t.entity_id ,t.ts,
case
        when t.long_v is not null then ''||t.long_v
        when t.dbl_v is not null then ''||t.dbl_v
		when t.str_v is not null then ''||t.str_v
        when t.bool_v is not null then ''||t.bool_v
        when t.json_v is not null then ''||t.json_v
        when t.json_v is null then null end as test_time
from ts_kv t
left join ts_kv_dictionary t1
on t."key" =t1.key_id
left join device t2
on t.entity_id =t2.id
where t2.name like 'stretchMachine%'
and ts>=I_START_TS
and ts<=I_END_TS
and t1."key"='调机时间'
and not exists (select 1 from t_tmp_cln_stretchmachine_t i where i.entity_id=t.entity_id and i.ts=t.ts
				and i.ts>=I_START_TS
				and i.ts<=I_END_TS);

-- 合并临时表
insert into t_tmp_cln_stretchmachine_t(
name,
entity_id,
ts,
report_date,
report_time,
status,
molding_time,
sealing_time,
vacuum_time,
run_num,
test_run_num,
test_time
)
select a.name,a.entity_id,a.ts,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd') AS report_date,
to_char(to_timestamp(a.ts/1000), 'yyyy-mm-dd hh24:mi:ss') AS report_time,
b.status,
c.molding_time,
d.sealing_time,
e.vacuum_time,
f.run_num,
g.test_run_num,
h.test_time
from
t_tmp_cln_stretchmachine_t01 a
left join t_tmp_cln_stretchmachine_t02 b
on b.name=a.name
and a.entity_id=b.entity_id
and a.ts =b.ts
left join t_tmp_cln_stretchmachine_t03 c
on c.name=a.name
and a.entity_id=c.entity_id
and a.ts =c.ts
left join t_tmp_cln_stretchmachine_t04 d
on d.name=a.name
and a.entity_id=d.entity_id
and a.ts =d.ts
left join t_tmp_cln_stretchmachine_t05 e
on e.name=a.name
and a.entity_id=e.entity_id
and a.ts =e.ts
left join t_tmp_cln_stretchmachine_t06 f
on f.name=a.name
and a.entity_id=f.entity_id
and a.ts =f.ts
left join t_tmp_cln_stretchmachine_t07 g
on g.name=a.name
and a.entity_id=g.entity_id
and a.ts =g.ts
left join t_tmp_cln_stretchmachine_t08 h
on h.name=a.name
and a.entity_id=h.entity_id
and a.ts =h.ts
group by
a.name,a.entity_id,a.ts,
b.status,
c.molding_time,
d.sealing_time,
e.vacuum_time,
f.run_num,
g.test_run_num,
h.test_time
order by ts;



-- 插入结果表
insert into t_cln_stretchmachine(
name,
entity_id,
ts,
report_date,
report_time,
status,
molding_time,
sealing_time,
vacuum_time,
run_num,
test_run_num,
test_time
)
select
name,
entity_id,
ts,
report_date,
report_time,
status,
molding_time,
sealing_time,
vacuum_time,
run_num,
test_run_num,
test_time
from t_tmp_cln_stretchmachine_t t
where t.report_time > V_START_TIME
AND t.report_time <= i_run_time
and not exists (select 1
		from t_cln_stretchmachine a
		where a.report_time>=V_START_TIME
		and a.report_time <=i_run_time
		and a.entity_id=t.entity_id
		and a.ts = t.ts);





--跑批时间过长再打印日志
  IF round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) > 30 THEN
    O_RET_NOTE := '运行时长:' ||
                  round(extract(epoch from (clock_timestamp()-V_JOB_START_TIME))::numeric) ||
                  '秒>>>' || ',时间范围:' || V_START_TIME || ' ' || i_run_time;
   call pc_insert_run_job_log('1'::character varying,V_PRC::character varying,''::character varying,1::character varying,O_RET_NOTE::character varying);
  END IF;



-- COMMIT;

  o_ret_note := '成功:'||I_START_TS||'-'||I_END_TS;
EXCEPTION
  WHEN OTHERS THEN
    o_ret_note := V_PRC || ' 执行错误:' || SQLERRM;
    call pc_insert_run_job_log('2'::character varying,v_prc::character varying,''::character varying,SQLSTATE::character varying,SQLERRM::character varying);
END;
$procedure$
;

CREATE OR REPLACE PROCEDURE public.pc_insert_run_job_log(IN i_type character varying, IN i_name character varying, IN i_cmd character varying, IN i_errtype character varying, IN i_errmsg character varying)
 LANGUAGE plpgsql
AS $procedure$
declare

v_job_id   uuid;
  v_log_id   uuid;
  v_date character varying;
  v_time character varying;
  v_count integer;
begin
    --生成随机32位主键
	select uuid_generate_v4() into v_log_id;
	select to_char(clock_timestamp(),'yyyy-mm-dd') into v_date;
    select to_char(clock_timestamp(),'hh24:mi:ss') into v_time;

    --获取作业ID
    select count(1) into v_count
    from t_mid_run_job_list a
    where a.job_name=i_name;

    if v_count>0 then
      select a.job_id into v_job_id
      from t_mid_run_job_list a
      where a.job_name=i_name;
    end if;

    insert into t_mid_run_job_log
      (log_id,log_type,job_date,job_time,job_name,job_cmd,error_type,error_msg,job_id)
    values
      (v_log_id, i_type,v_date,v_time,i_name,i_cmd, substr(i_errtype,1,10),substr(i_errmsg,1,2999),v_job_id);

end;
$procedure$
;
