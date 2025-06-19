
-- ----------------------------
-- Table structure for t_sys_code_dsc
-- 字典管理
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_sys_code_dsc";
CREATE TABLE "public"."t_sys_code_dsc" (
  "code_id" int4 NOT NULL DEFAULT nextval('t_sys_code_dsc_code_id_seq'::regclass),
  "code_cl_id" varchar(255) COLLATE "pg_catalog"."default",
  "code_cl_dsc" varchar(255) COLLATE "pg_catalog"."default",
  "code_value" varchar(255) COLLATE "pg_catalog"."default",
  "code_dsc" varchar(255) COLLATE "pg_catalog"."default",
  "enabled_st" varchar(2) COLLATE "pg_catalog"."default" DEFAULT 0,
  "crt_time" timestamp(6),
  "crt_user" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6),
  "update_user" varchar(255) COLLATE "pg_catalog"."default",
  "is_group" int4
)
;
COMMENT ON COLUMN "public"."t_sys_code_dsc"."code_cl_id" IS '参数分类编码';
COMMENT ON COLUMN "public"."t_sys_code_dsc"."code_cl_dsc" IS '参数分类描述';
COMMENT ON COLUMN "public"."t_sys_code_dsc"."code_value" IS '参数编码';
COMMENT ON COLUMN "public"."t_sys_code_dsc"."code_dsc" IS '参数描述';
COMMENT ON COLUMN "public"."t_sys_code_dsc"."enabled_st" IS '启停状态';
COMMENT ON COLUMN "public"."t_sys_code_dsc"."is_group" IS '是否字典组 0：否 1：是';
COMMENT ON TABLE "public"."t_sys_code_dsc" IS '代码描述公用表';

-- ----------------------------
-- Records of t_sys_code_dsc
-- ----------------------------
INSERT INTO "public"."t_sys_code_dsc" VALUES (51, 'PROCESSTYPES0000', '订单工序类型', '1', '正常订单', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."t_sys_code_dsc" VALUES (52, 'PROCESSTYPES0000', '订单工序类型', '2', '移交订单', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."t_sys_code_dsc" VALUES (53, 'PROCESSSTATUS0000', '工序状态', '4', '移交中', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO "public"."t_sys_code_dsc" VALUES (41, 'GROUPCODE0000', '字典分类', 'CJ0000', '车间', '1', '2022-04-26 11:33:23.353', 'tenant@thingsboard.org', '2022-04-26 11:33:23.353', 'tenant@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (32, 'GROUPCODE0000', '字典分类', 'JOB0000', '岗位', '1', NULL, NULL, '2022-04-24 16:27:35.646', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (1, 'JOB0000', '岗位', 'JOB0001', '组长', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (2, 'JOB0000', '岗位', 'JOB0002', '普通工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (5, 'SCHEDULING0000', '排班', 'SCHEDULING0002', '晚班(21:00~9:00）', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (6, 'SCHEDULING0000', '排班', 'SCHEDULING0001', '早班(9:00~21:00）', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (7, 'ORDERSTATUS0000', '订单状态', '0', '未开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (8, 'BUSTYPE0000', '业务类型', 'BG', '报工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (9, 'BUSTYPE0000', '业务类型', 'PD', '盘点', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (10, 'RECORDTYPE0000', '类目类型', '1', '原辅料', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (11, 'ORDERSTATUS0000', '订单状态', '1', '已开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (33, 'GROUPCODE0000', '字典分类', 'ENABLEDST0000', '启用状态', '1', NULL, NULL, '2022-04-24 16:29:24.623', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (35, 'GROUPCODE0000', '字典分类', 'ORDERST0000', '订单状态', '1', NULL, NULL, '2022-04-24 15:06:35.339', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (12, 'ORDERSTATUS0000', '订单状态', '2', '暂停', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (13, 'ORDERSTATUS0000', '订单状态', '3', '已完工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (14, 'DOCUMENTSTATUS0000', '单据状态', 'A', '创建', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (15, 'DOCUMENTSTATUS0000', '单据状态', 'C', '审核', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (16, 'DOCUMENTSTATUS0000', '单据状态', 'D', '反审核', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (17, 'DOCUMENTSTATUS0000', '单据状态', 'B', '提交', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (18, 'DOCUMENTSTATUS0000', '单据状态', 'Z', '暂存', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (20, 'REPORTSTATUS0000', '报工状态', '0', '作废', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (22, 'REPORTSTATUS0000', '报工状态', '1', '正常', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (26, 'PROCESSSTATUS0000', '工序状态', '0', '未开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (27, 'PROCESSSTATUS0000', '工序状态', '1', '已开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (28, 'PROCESSSTATUS0000', '工序状态', '2', '暂停', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (29, 'PROCESSSTATUS0000', '工序状态', '3', '已完工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (30, 'RECORDTYPE0000', '类目类型', '2', '二级品数量', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (31, 'RECORDTYPE0000', '类目类型', '3', '产后数量', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (49, 'CJ0000', '车间', 'CJ001', '车间1', '1', '2022-04-26 17:16:19.87', 'tenant@thingsboard.org', '2022-05-10 09:06:02.006', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (54, 'RECORDTYPEL20000', '二级类目类型', '1', '废膜', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (55, 'RECORDTYPEL20000', '二级类目类型', '2', '剩余膜', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (56, 'RECORDTYPEL20000', '二级类目类型', '3', '袋装', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (57, 'RECORDTYPEL20000', '二级类目类型', '4', '桶装', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (58, 'SUSPENDREASON0000', '暂停原因', '1', '机械故障维修中', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (59, 'SUSPENDREASON0000', '暂停原因', '2', '其他原因', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (37, 'JOB0000', '岗位', 'JOB0004', '机排手', '0', '2022-04-24 16:49:47.442', 'customer@thingsboard.org', '2022-04-24 16:49:47.442', 'customer@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (39, 'JOB0000', '岗位', 'JOB0005', '质检员', '1', '2022-04-24 17:07:36.2', 'customer@thingsboard.org', '2022-04-24 17:50:34.302', 'customer@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (50, 'CJ0000', '车间', 'CJ002', '车间2', '0', '2022-04-26 17:25:51.756', 'tenant@thingsboard.org', '2022-05-10 08:36:09.238', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (68, 'ENABLEDST0000', '启用状态', 'QIYONG', '启用', '1', '2022-05-11 08:43:24.414', 'tenant@thingsboard.org', '2022-05-11 08:43:24.414', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (36, 'CJ0000', '车间', 'CJ004', '车间4', '0', '2022-05-10 10:07:43.4', 'tenant@thingsboard.org', '2022-05-10 10:07:43.4', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (34, 'CJ0000', '车间', 'CJ003', '车间3', '1', '2022-05-10 10:06:51.45', 'tenant@thingsboard.org', '2022-05-10 10:07:49.816', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (38, 'CJ0000', '车间', 'CJ005', 'CJ005', '1', '2022-05-10 10:08:15.431', 'tenant@thingsboard.org', '2022-05-10 10:08:15.431', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (60, 'JOB0000', '岗位', 'JOB005', '测试新增', '1', '2022-05-10 10:58:19.554', 'tenant@thingsboard.org', '2022-05-10 10:58:19.554', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (61, 'CJ0000', '车间', 'CJ00222', '车间', '1', '2022-05-10 11:01:38.311', 'tenant@thingsboard.org', '2022-05-10 11:01:38.311', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (65, 'GROUPCODE0000', '字典', 'CUSTOMWC0000', '自定义报工类目', '1', NULL, NULL, NULL, NULL, 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (64, 'CJ0000', '车间', 'CJ0003', '车间0003', '0', '2022-05-10 16:24:49.865', 'tenant@thingsboard.org', '2022-05-11 08:41:30.416', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (69, 'ENABLEDST0000', '启用状态', 'JINYONG', '禁用', '1', '2022-05-11 08:43:43.601', 'tenant@thingsboard.org', '2022-05-11 08:43:43.601', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (3, 'JOB0000', '岗位', 'ZRYH', '主任一号', '0', '2022-05-10 08:37:38.937', 'tenant@thingsboard.org', '2022-05-11 08:42:14.894', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (71, 'ORDERST0000', '订单状态', 'WEIWANGONG', '未完工', '1', '2022-05-11 09:18:37.157', 'tenant@thingsboard.org', '2022-05-11 09:18:37.157', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (67, 'JOB0000', '岗位', 'ZRWH005', '主任五号', '1', '2022-05-11 08:42:40.771', 'tenant@thingsboard.org', '2022-05-11 08:42:50.446', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (74, 'RECORDTYPE0000', '类目类型', '4', '自定义报工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (66, 'CUSTOMWC0000', '自定义报工类目', 'CUSTOMWC0001', '返工订单-二级品', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (73, 'ORDERST0000', '订单状态', 'TINGGONG', '停工中', '1', '2022-05-11 09:20:34.112', 'tenant@thingsboard.org', '2022-05-16 09:20:14.6', 'tenant@thingsboard.org', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (24, 'UNIT0000', '单位', 'JIAN', '件', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (70, 'ORDERST0000', '订单状态', 'YIKAIGONG', '已开工', '1', '2022-05-11 09:18:16.117', 'tenant@thingsboard.org', '2022-05-14 13:58:40.575', 'zengjinjiang', 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (23, 'UNIT0000', '单位', 'ZHI', '支', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (19, 'UNIT0000', '单位', 'KG', '千克', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (21, 'UNIT0000', '单位', 'G', '克', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (25, 'UNIT0000', '单位', 'XIANG', '箱', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO "public"."t_sys_code_dsc" VALUES (81, 'GROUPCODE0000', '字典分类', 'DOCUMENTSTATUS0000', '单据状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (79, 'GROUPCODE0000', '字典分类', 'PROCESSTYPES0000', '订单工序类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (78, 'GROUPCODE0000', '字典分类', 'ORDERSTATUS0000', '订单状态1', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (77, 'GROUPCODE0000', '字典分类', 'BUSTYPE0000', '业务类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (76, 'GROUPCODE0000', '字典分类', 'RECORDTYPEL20000', '二级类目类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (75, 'GROUPCODE0000', '字典分类', 'RECORDTYPE0000', '类目类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (4, 'GROUPCODE0000', '字典分类', 'SCHEDULING0000', '排班', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (80, 'GROUPCODE0000', '字典分类', 'REPORTSTATUS0000', '报工状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (84, 'GROUPCODE0000', '字典分类', 'UNIT0000', '单位', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (83, 'GROUPCODE0000', '字典分类', 'SUSPENDREASON0000', '暂停原因', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO "public"."t_sys_code_dsc" VALUES (82, 'GROUPCODE0000', '字典分类', 'PROCESSSTATUS0000', '工序状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);

-- ----------------------------
-- Primary Key structure for table t_sys_code_dsc
-- ----------------------------
ALTER TABLE "public"."t_sys_code_dsc" ADD CONSTRAINT "t_sys_code_dsc_pkey" PRIMARY KEY ("code_id");
