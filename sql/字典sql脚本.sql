-- 2022-09-25 开发过程新增的字典需要把插入行的脚本提交到此文件，包含字典分类和字典项

INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'CJ0000', '车间', '1', '2022-04-26 11:33:23.353', 'tenant@thingsboard.org', '2022-04-26 11:33:23.353', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'jian', '件', '1', NULL, NULL, '2022-05-25 17:04:11.746', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ002', '车间2', '1', '2022-04-26 17:25:51.756', 'tenant@thingsboard.org', '2022-05-10 08:36:09.238', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ENABLEDST0000', '启用状态', 'QIYONG', '启用', '1', '2022-05-11 08:43:24.414', 'tenant@thingsboard.org', '2022-05-11 08:43:24.414', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ004', '车间4', '1', '2022-05-10 10:07:43.400', 'tenant@thingsboard.org', '2022-05-10 10:07:43.400', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ003', '车间3', '1', '2022-05-10 10:06:51.450', 'tenant@thingsboard.org', '2022-05-10 10:07:49.816', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ005', 'CJ005', '1', '2022-05-10 10:08:15.431', 'tenant@thingsboard.org', '2022-05-10 10:08:15.431', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ00222', '车间', '1', '2022-05-10 11:01:38.311', 'tenant@thingsboard.org', '2022-05-10 11:01:38.311', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典', 'CUSTOMWC0000', '自定义报工类目', '1', NULL, NULL, NULL, NULL, 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ0003', '车间0003', '1', '2022-05-10 16:24:49.865', 'tenant@thingsboard.org', '2022-05-11 08:41:30.416', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ENABLEDST0000', '启用状态', 'JINYONG', '禁用', '1', '2022-05-11 08:43:43.601', 'tenant@thingsboard.org', '2022-05-11 08:43:43.601', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERST0000', '订单状态', 'WEIWANGONG', '未完工', '1', '2022-05-11 09:18:37.157', 'tenant@thingsboard.org', '2022-05-11 09:18:37.157', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '4', '自定义报工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'UNIT0000', '单位', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'SUSPENDREASON0000', '暂停原因', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'PROCESSSTATUS0000', '工序状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'li', '粒', '1', '2022-05-25 17:04:58.091', 'hhh@qq.com', '2022-05-25 17:04:58.091', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'zhang', '张', '1', '2022-05-25 17:05:13.825', 'hhh@qq.com', '2022-05-25 17:05:13.825', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'kuai', '块', '1', '2022-05-25 17:05:29.469', 'hhh@qq.com', '2022-05-25 17:05:29.469', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'tao', '套', '1', '2022-05-25 17:05:40.670', 'hhh@qq.com', '2022-05-25 17:05:40.670', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'ping', '瓶', '1', '2022-05-25 17:05:54.019', 'hhh@qq.com', '2022-05-25 17:05:54.019', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'juan', '卷', '1', '2022-05-25 17:06:06.404', 'hhh@qq.com', '2022-05-25 17:06:06.404', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'ge', '个', '1', '2022-05-25 17:06:17.002', 'hhh@qq.com', '2022-05-25 17:06:17.002', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ABRASIVES0000', '模具', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SUSPENDREASON0000', '暂停原因', '2', '电路原因', '1', NULL, NULL, '2022-05-30 14:25:17.927', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SUSPENDREASON0000', '暂停原因', '3', '环境原因', '1', '2022-05-30 14:25:28.879', 'hhh@qq.com', '2022-05-30 14:25:28.879', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'xiang', '箱', '1', NULL, NULL, '2022-05-30 14:45:56.574', 'gm001', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('STOCKTAKING0000', '盘点类型', 'STOCKTAKING0001', '交接班盘点', '1', '2022-05-31 17:52:13.739', 'www@qq.com', '2022-05-31 17:52:13.739', 'www@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'STOCKTAKING0000', '盘点类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ABRASIVES0000', '模具', '4*8', '102克/袋*30袋', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ABRASIVES0000', '模具', '4*8', '2.5千克/袋*2袋', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ABRASIVES0000', '模具', '4*7', '20克/个*24个/桶*12桶/箱', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ABRASIVES0000', '模具', '4*11', '10克/只*20只/盒*20/盒/箱', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('BIND0000', '扫码类型', 'BIND0001', '报工扫码', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('BIND0000', '扫码类型', 'BIND0002', '接单扫码', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERSTATUS0000', '订单状态1', '4', '已挂起', '1', '2022-06-16 15:41:59.834', 'hhh@qq.com', '2022-06-16 15:41:59.834', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'JOB0000', '岗位', '1', NULL, NULL, '2022-04-24 16:27:35.646', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERSTATUS0000', '订单状态', '0', '未开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('BUSTYPE0000', '业务类型', 'BG', '报工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('BUSTYPE0000', '业务类型', 'PD', '盘点', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '1', '原辅料', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERSTATUS0000', '订单状态', '1', '已开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ENABLEDST0000', '启用状态', '1', NULL, NULL, '2022-04-24 16:29:24.623', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ORDERST0000', '订单状态', '1', NULL, NULL, '2022-04-24 15:06:35.339', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERSTATUS0000', '订单状态', '2', '暂停', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERSTATUS0000', '订单状态', '3', '已完工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('DOCUMENTSTATUS0000', '单据状态', 'A', '创建', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'REPORTSTATUS0000', '报工状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('DOCUMENTSTATUS0000', '单据状态', 'C', '审核', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('DOCUMENTSTATUS0000', '单据状态', 'D', '反审核', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('DOCUMENTSTATUS0000', '单据状态', 'B', '提交', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('DOCUMENTSTATUS0000', '单据状态', 'Z', '暂存', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('REPORTSTATUS0000', '报工状态', '0', '作废', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('REPORTSTATUS0000', '报工状态', '1', '正常', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '0', '未开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '1', '已开工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '2', '暂停', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '3', '已完工', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '2', '二级品数量', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '3', '产后数量', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', '1', '废膜', '1', NULL, NULL, '2022-09-01 09:08:05.385', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', '3', '袋装', '1', NULL, NULL, '2022-09-01 09:08:08.354', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CJ0000', '车间', 'CJ001', '二厂蟹柳车间', '1', '2022-04-26 17:16:19.870', 'tenant@thingsboard.org', '2022-09-04 15:29:09.405', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', '2', '剩余膜', '1', NULL, NULL, '2022-09-01 09:08:12.912', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', '4', '桶装', '1', NULL, NULL, '2022-09-01 09:08:01.545', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('STOCKTAKING0000', '盘点类型', 'STOCKTAKING0002', '订单完工盘点', '1', '2022-05-31 17:52:30.331', 'www@qq.com', '2022-06-06 19:01:06.937', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMWC0000', '自定义报工类目', 'CUSTOMWC0001', '返工订单-二级品', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERST0000', '订单状态', 'TINGGONG', '停工中', '1', '2022-05-11 09:20:34.112', 'tenant@thingsboard.org', '2022-05-16 09:20:14.600', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ORDERST0000', '订单状态', 'YIKAIGONG', '已开工', '1', '2022-05-11 09:18:16.117', 'tenant@thingsboard.org', '2022-05-14 13:58:40.575', 'zengjinjiang', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'kg', '千克', '1', NULL, NULL, '2022-05-25 17:04:33.320', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'g', '克', '1', NULL, NULL, '2022-05-25 17:04:37.704', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSTYPES0000', '订单工序类型', '1', '正常订单', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'DOCUMENTSTATUS0000', '单据状态', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'PROCESSTYPES0000', '订单工序类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ORDERSTATUS0000', '订单状态1', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'BUSTYPE0000', '业务类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'RECORDTYPEL20000', '二级类目类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'RECORDTYPE0000', '类目类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'SCHEDULING0000', '排班', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SUSPENDREASON0000', '暂停原因', '1', '机器设备原因', '1', NULL, NULL, '2022-05-30 14:25:08.162', 'hhh@qq.com', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'zhi', '支', '1', NULL, NULL, '2022-05-30 14:45:49.968', 'gm001', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSTYPES0000', '订单工序类型', '2', '移交订单', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '4', '移交中', '1', NULL, NULL, NULL, NULL, 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW001', '厂长', '1', '2022-07-07 11:42:24.664', 'tenant@thingsboard.org', '2022-07-07 11:42:24.664', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW002', '车间主任', '1', '2022-07-07 11:43:10.134', 'tenant@thingsboard.org', '2022-07-07 11:43:10.134', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW003', '车间副主任', '1', '2022-07-07 11:43:28.765', 'tenant@thingsboard.org', '2022-07-07 11:43:28.765', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW004', '无菌组班长', '1', '2022-07-07 11:44:13.317', 'tenant@thingsboard.org', '2022-07-07 11:44:13.317', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW005', '包装组组长', '1', '2022-07-07 11:44:33.905', 'tenant@thingsboard.org', '2022-07-07 11:44:33.905', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW006', '蟹柳线组长', '1', '2022-07-07 11:44:52.569', 'tenant@thingsboard.org', '2022-07-07 11:44:52.569', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW007', '扒皮组组长', '1', '2022-07-07 11:45:30.913', 'tenant@thingsboard.org', '2022-07-07 11:45:30.913', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW023', '无菌教练员', '1', '2022-07-07 11:53:28.342', 'tenant@thingsboard.org', '2022-07-07 11:53:28.342', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW024', '无菌杀菌机手', '1', '2022-07-07 11:53:44.461', 'tenant@thingsboard.org', '2022-07-07 11:53:44.461', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW025', '无菌摆肠人员', '1', '2022-07-07 11:54:00.358', 'tenant@thingsboard.org', '2022-07-07 11:54:00.358', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW026', '无菌教练机手', '1', '2022-07-07 11:54:16.925', 'tenant@thingsboard.org', '2022-07-07 11:54:16.925', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW027', '无菌扒皮人员', '1', '2022-07-07 11:54:47.956', 'tenant@thingsboard.org', '2022-07-07 11:54:47.956', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW028', '无菌杀菌人员', '1', '2022-07-07 11:55:05.574', 'tenant@thingsboard.org', '2022-07-07 11:55:05.574', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW029', '无菌接筐人员', '1', '2022-07-07 11:55:45.323', 'tenant@thingsboard.org', '2022-07-07 11:55:45.323', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW030', '挑拣人员', '1', '2022-07-07 11:56:18.422', 'tenant@thingsboard.org', '2022-07-07 11:56:18.422', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW031', '开箱机机手', '1', '2022-07-07 11:56:34.570', 'tenant@thingsboard.org', '2022-07-07 11:56:34.570', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW032', '下货人员', '1', '2022-07-07 11:56:49.839', 'tenant@thingsboard.org', '2022-07-07 11:56:49.839', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW008', '无菌组组长', '1', '2022-07-07 11:45:55.591', 'tenant@thingsboard.org', '2022-07-07 11:45:55.591', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW009', '包装组班长', '1', '2022-07-07 11:46:29.557', 'tenant@thingsboard.org', '2022-07-07 11:46:29.557', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW010', '解冻/切片机手', '1', '2022-07-07 11:46:48.322', 'tenant@thingsboard.org', '2022-07-07 11:46:48.322', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW011', '蟹柳线斩拌机手组长', '1', '2022-07-07 11:48:41.921', 'tenant@thingsboard.org', '2022-07-07 11:48:41.921', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW012', '蟹柳线复称人员', '1', '2022-07-07 11:49:09.771', 'tenant@thingsboard.org', '2022-07-07 11:49:09.771', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW013', '蟹柳线加料人员', '1', '2022-07-07 11:49:32.835', 'tenant@thingsboard.org', '2022-07-07 11:49:32.835', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW014', '蟹柳线切片机手', '1', '2022-07-07 11:49:54.735', 'tenant@thingsboard.org', '2022-07-07 11:49:54.735', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW015', '蟹柳线蟹柳机手', '1', '2022-07-07 11:50:10.583', 'tenant@thingsboard.org', '2022-07-07 11:50:10.583', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW016', '蟹柳线配料人员', '1', '2022-07-07 11:50:28.768', 'tenant@thingsboard.org', '2022-07-07 11:50:28.768', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW017', '蟹柳线斩拌机手', '1', '2022-07-07 11:50:42.668', 'tenant@thingsboard.org', '2022-07-07 11:50:42.668', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW018', '蟹柳后备斩拌机手', '1', '2022-07-07 11:51:03.266', 'tenant@thingsboard.org', '2022-07-07 11:51:03.266', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW019', '蟹柳线教练机手', '1', '2022-07-07 11:51:27.634', 'tenant@thingsboard.org', '2022-07-07 11:51:27.634', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW020', '无菌拉伸膜组长', '1', '2022-07-07 11:52:09.463', 'tenant@thingsboard.org', '2022-07-07 11:52:09.463', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW021', '无菌拌料机手', '1', '2022-07-07 11:52:57.662', 'tenant@thingsboard.org', '2022-07-07 11:52:57.662', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW022', '无菌拉伸膜机手', '1', '2022-07-07 11:53:13.043', 'tenant@thingsboard.org', '2022-07-07 11:53:13.043', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW033', '装箱人员', '1', '2022-07-07 11:57:03.552', 'tenant@thingsboard.org', '2022-07-07 11:57:03.552', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW034', '散装机机手', '1', '2022-07-07 11:57:18.036', 'tenant@thingsboard.org', '2022-07-07 11:57:18.036', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW035', '风干机手', '1', '2022-07-07 11:57:31.385', 'tenant@thingsboard.org', '2022-07-07 11:57:31.385', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW036', '蟹柳线成本统计', '1', '2022-07-07 11:57:57.319', 'tenant@thingsboard.org', '2022-07-07 11:57:57.319', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULING0000', '排班', 'SCHEDULING0002', '晚班(18:00~6:00）', '1', NULL, NULL, '2022-09-02 15:32:45.625', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW037', '无菌间成本统计', '1', '2022-07-07 11:58:17.217', 'tenant@thingsboard.org', '2022-07-07 11:58:17.217', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW038', '包装间成本统计', '1', '2022-07-07 11:58:33.382', 'tenant@thingsboard.org', '2022-07-07 11:58:33.382', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW039', '成品入库员', '1', '2022-07-07 11:59:00.017', 'tenant@thingsboard.org', '2022-07-07 11:59:00.017', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW040', '成本管控经理', '1', '2022-07-07 11:59:15.797', 'tenant@thingsboard.org', '2022-07-07 11:59:15.797', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW041', '成本核算组长', '1', '2022-07-07 11:59:30.266', 'tenant@thingsboard.org', '2022-07-07 11:59:30.266', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW042', '成本核算员', '1', '2022-07-07 11:59:51.478', 'tenant@thingsboard.org', '2022-07-07 11:59:51.478', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW043', '技术员', '1', '2022-07-07 12:00:09.546', 'tenant@thingsboard.org', '2022-07-07 12:00:09.546', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW044', 'IE工程师', '1', '2022-07-07 12:00:25.777', 'tenant@thingsboard.org', '2022-07-07 12:00:25.777', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW045', '信息管理员', '1', '2022-07-07 12:00:43.844', 'tenant@thingsboard.org', '2022-07-07 12:00:43.844', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW046', '开发工程师', '1', '2022-07-07 12:01:20.200', 'tenant@thingsboard.org', '2022-07-07 12:01:20.200', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW047', '蟹柳线班长', '1', '2022-07-07 12:03:25.774', 'tenant@thingsboard.org', '2022-07-07 12:03:25.774', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW048', '应用系统工程师', '1', '2022-07-07 12:48:11.081', 'tenant@thingsboard.org', '2022-07-07 12:48:11.081', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW049', '项目经理', '1', '2022-07-07 12:48:58.424', 'tenant@thingsboard.org', '2022-07-07 12:48:58.424', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', '050', '应用系统部经理', '1', '2022-07-07 12:49:42.137', 'tenant@thingsboard.org', '2022-07-07 12:49:42.137', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('JOB0000', '岗位', 'GW051', '采购专员', '1', '2022-07-07 12:50:20.104', 'tenant@thingsboard.org', '2022-07-07 12:50:20.104', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'RECORDL20000', '投入二级品（一级类目）', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20001', 'A料', '1', '2022-07-04 16:18:43.959', 'tenant@thingsboard.org', '2022-07-04 16:31:24.801', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20003', '废料', '1', '2022-07-04 16:19:18.982', 'tenant@thingsboard.org', '2022-07-04 16:31:36.587', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', 'RECORDTYPEL20002', 'B料', '1', '2022-07-04 16:18:59.097', 'tenant@thingsboard.org', '2022-07-04 16:31:31.903', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDL20000', '投入二级品（一级类目）', 'RECORDL20001', 'A料-本订单', '1', '2022-07-04 16:28:57.022', 'tenant@thingsboard.org', '2022-07-04 16:29:47.744', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDL20000', '投入二级品（一级类目）', 'RECORDL20004', 'B料-其他订单', '1', '2022-07-04 16:29:40.094', 'tenant@thingsboard.org', '2022-07-04 16:30:08.022', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDL20000', '投入二级品（一级类目）', 'RECORDL20002', 'A料-其他订单', '1', '2022-07-04 16:29:08.035', 'tenant@thingsboard.org', '2022-07-04 16:29:54.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDL20000', '投入二级品（一级类目）', 'RECORDL20003', 'B料-本订单', '1', '2022-07-04 16:29:27.335', 'tenant@thingsboard.org', '2022-07-04 16:30:01.037', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '5', '投入前道数量', '1', '2022-07-13 14:47:03.729', 'tenant@thingsboard.org', '2022-07-13 14:47:03.729', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPE0000', '类目类型', '6', 'AB料投入', '1', '2022-07-14 09:28:11.937', 'tenant@thingsboard.org', '2022-07-14 09:28:11.937', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'OTHER0000', '其他', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ERPPROCESS0000', 'ERP工序标识', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'LSM', '拉伸膜', '1', '2022-07-18 09:41:58.993', 'tenant@thingsboard.org', '2022-07-18 09:41:58.993', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'ZB', '斩拌', '1', '2022-07-18 09:40:18.853', 'tenant@thingsboard.org', '2022-07-18 09:40:18.853', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'XL', '蟹柳', '1', '2022-07-18 09:40:35.711', 'tenant@thingsboard.org', '2022-07-18 09:40:35.711', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'BP', '剥皮', '1', '2022-07-18 09:41:32.777', 'tenant@thingsboard.org', '2022-07-18 09:41:32.777', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'BL', '拌料', '1', '2022-07-18 09:41:44.278', 'tenant@thingsboard.org', '2022-07-18 09:41:44.278', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'BZ', '包装', '1', '2022-07-18 09:42:14.376', 'tenant@thingsboard.org', '2022-07-18 09:42:14.376', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '0.88', '系数比例A', '1', '2022-08-09 10:24:22.446', 'tenant@thingsboard.org', '2022-08-09 10:24:42.612', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('RECORDTYPEL20000', '二级类目类型', '5', '使用膜', '1', '2022-09-01 09:07:55.855', 'tenant@thingsboard.org', '2022-09-01 09:07:55.855', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'REPORTYPE0000', '报工类型', '1', NULL, NULL, '2022-04-24 16:27:09.615', 'customer@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('REPORTYPE0000', '报工类型', 'REPORTYPE0002', '尾料', '1', '2022-08-08 16:36:30.806', 'tenant@thingsboard.org', '2022-08-08 16:38:15.031', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('REPORTYPE0000', '报工类型', 'REPORTYPE0001', '正常', '1', '2022-08-08 16:36:00.871', 'tenant@thingsboard.org', '2022-08-08 16:38:11.161', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'tong', '桶', '1', '2022-09-01 17:50:13.207', 'tenant@thingsboard.org', '2022-09-01 17:50:13.207', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'dai', '袋', '1', '2022-09-01 17:50:24.857', 'tenant@thingsboard.org', '2022-09-01 17:50:24.857', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'APPVERSION0000', 'app版本', '1', '2022-09-01 18:27:09.615', 'tenant@thingsboard.org', '2022-09-01 17:50:35.147', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'SCHEDULINGTIME0000', '排班时间', '1', '2022-09-02 11:28:27.278', '13489956595', '2022-09-02 11:28:27.278', '13489956595', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMWC0000', '自定义报工类目', 'CUSTOMWC0003', '乳化浆-产出', '0', '2022-09-01 08:54:42.626', 'tenant@thingsboard.org', '2022-09-03 20:37:01.580', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '1', 'A参数', '1', '2022-07-16 16:27:30.002', 'tenant@thingsboard.org', '2022-09-02 22:27:37.383', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('APPVERSION0000', 'app版本', '{"key":"remark","value":"紧急更新 解决ab料产出bug"}', '更新内容', '1', '2022-09-01 18:27:09.615', 'tenant@thingsboard.org', '2022-09-04 14:33:33.050', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMWC0000', '自定义报工类目', 'CUSTOMWC0002', '乳化浆-投入', '0', '2022-09-01 08:54:04.803', 'tenant@thingsboard.org', '2022-09-04 14:19:46.224', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '0', 'A参数是否启用', '1', '2022-07-16 16:30:01.480', 'tenant@thingsboard.org', '2022-09-04 11:51:29.709', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('APPVERSION0000', 'app版本', '{"key":"vrsnCode","value":"14"}', '版本号(value为整数)', '1', '2022-09-01 18:27:09.615', 'tenant@thingsboard.org', '2022-09-25 17:48:15.631', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('UNIT0000', '单位', 'he', '盒', '1', '2022-09-01 17:50:35.147', 'tenant@thingsboard.org', '2022-09-08 19:30:17.952', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '010205000004', '鱼肉蛋白粉', '1', '2022-09-25 15:50:49.512', 'tenant@thingsboard.org', '2022-09-25 15:50:49.512', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '010203002000', '蛋白粉', '1', '2022-09-25 15:51:03.462', 'tenant@thingsboard.org', '2022-09-25 15:51:03.462', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '020204462100', '空白卷膜（散装5kg蟹柳专用）', '1', '2022-09-25 15:51:17.743', 'tenant@thingsboard.org', '2022-09-25 15:51:17.743', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('STOCKTAKING0000', '盘点类型', 'STOCKTAKING0003', '中途完工盘点', '1', '2022-09-25 16:57:47.876', 'tenant@thingsboard.org', '2022-09-25 16:57:47.876', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSSTATUS0000', '工序状态', '5', '移交驳回', '1', '2022-09-25 16:59:51.888', 'tenant@thingsboard.org', '2022-09-25 16:59:51.888', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('APPVERSION0000', 'app版本', '{"key":"upgFile","value":"https://lc-mall.obs.cn-north-4.myhuaweicloud.com/apk/licheng_M_v1.1.3_release_20220925_official_0.apk"}', '更新地址', '1', '2022-09-01 18:27:09.615', 'tenant@thingsboard.org', '2022-09-25 17:47:56.982', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULING0000', '排班', 'SCHEDULING0001', '早班(6:00~18:00）', '1', NULL, NULL, '2022-09-02 15:32:34.717', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULINGTIME0000', '排班时间', '6:00:00', '早班上班', '1', '2022-09-02 14:31:56.461', '13489956595', '2022-09-02 15:33:25.626', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULINGTIME0000', '排班时间', '18:00:00', '早班下班', '1', '2022-09-02 14:45:00.675', '13489956595', '2022-09-02 15:33:34.739', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULINGTIME0000', '排班时间', '18:00:01', '晚班上班', '1', '2022-09-02 14:47:18.849', '13489956595', '2022-09-02 15:34:45.467', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('SCHEDULINGTIME0000', '排班时间', '6:00:01', '晚班下班', '1', '2022-09-02 14:48:29.605', '13489956595', '2022-09-02 15:35:01.546', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('MATERIALSYNCHEADER', '物料同步表头', '03', '03', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('MATERIALSYNCHEADER', '物料同步表头', '05', '05', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('MATERIALSYNCHEADER', '物料同步表头', '05', '05', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'MEMBRANE0000', '拉伸膜参数', '1', '2022-09-02 21:23:13.124', 'tenant@thingsboard.org', '2022-09-02 21:23:13.124', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMWC0000', '自定义报工类目', 'CUSTOMWC0004', '次品浆-投入', '1', '2022-09-03 20:35:46.882', 'tenant@thingsboard.org', '2022-09-03 20:35:46.882', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ABRASIVES0000', '模具', '4*8', '92克/袋*30袋', '1', '2022-09-04 14:23:52.499', 'tenant@thingsboard.org', '2022-09-04 14:24:07.176', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('MEMBRANE0000', '拉伸膜参数', '0.5', '跑动一次使用膜长', '1', '2022-09-02 21:23:35.273', 'tenant@thingsboard.org', '2022-09-05 16:46:56.841', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'RHJ', '乳化浆', '1', '2022-09-19 15:10:23.054', 'tenant@thingsboard.org', '2022-09-19 15:10:23.054', 'tenant@thingsboard.org', 0);
--INSERT INTO public.t_sys_code_dsc
--(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
--VALUES('RECORDL20000', '投入二级品（一级类目）', 'RECORDL20005', '次品浆', '1', '2022-09-25 15:48:30.154', 'tenant@thingsboard.org', '2022-09-25 15:48:30.154', 'tenant@thingsboard.org', 0);
--INSERT INTO public.t_sys_code_dsc
-- (code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
-- VALUES('RECORDL20000', '投入二级品（一级类目）', 'TECORDL20006', '冰水', '1', '2022-09-25 15:48:45.259', 'tenant@thingsboard.org', '2022-09-25 15:48:45.259', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'CUSTOMPD0000', '自定义盘点类目', '1', '2022-09-25 15:49:44.145', 'tenant@thingsboard.org', '2022-09-25 15:49:44.145', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '020201035026', '82手撕仿蟹肉棒膜', '1', '2022-09-25 15:50:08.082', 'tenant@thingsboard.org', '2022-09-25 15:50:08.082', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '020201035025', '70手撕仿蟹肉棒膜', '1', '2022-09-25 15:50:23.288', 'tenant@thingsboard.org', '2022-09-25 15:50:23.288', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('CUSTOMPD0000', '自定义盘点类目', '010202006000', '大豆分离蛋白', '1', '2022-09-25 15:50:37.195', 'tenant@thingsboard.org', '2022-09-25 15:50:37.195', 'tenant@thingsboard.org', 0);


-- 2022-10-12 ERP工序标识
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'RHJ', '乳化浆', '1', '2022-10-10 10:50:23.810', 'tenant@thingsboard.org', '2022-10-10 10:50:23.810', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPPROCESS0000', 'ERP工序标识', 'XLSS', '蟹柳色素', '1', '2022-10-10 10:50:37.018', 'tenant@thingsboard.org', '2022-10-10 10:50:37.018', 'tenant@thingsboard.org', 0);

-- 2022-10-12 新增投入二级品（一级类目）
INSERT INTO "public"."t_sys_code_dsc" ("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES( 'RECORDL20000', '投入二级品（一级类目）', 'B_RECORDL20001', 'A料-本订单', '1', '2022-10-08 11:09:45.971', 'tenant@thingsboard.org', '2022-10-08 11:35:33.261', 'tenant@thingsboard.org', 0 );
INSERT INTO "public"."t_sys_code_dsc" ( "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES('RECORDL20000', '投入二级品（一级类目）', 'B_RECORDL20006', '冰水', '1', '2022-09-08 14:13:45.446', 'gm001', '2022-10-08 11:35:41.642', 'tenant@thingsboard.org', 0 );
INSERT INTO "public"."t_sys_code_dsc" ( "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES('RECORDL20000', '投入二级品（一级类目）', 'B_RECORDL20002', 'A料-其他订单', '1', '2022-10-08 11:20:45.862', 'tenant@thingsboard.org', '2022-10-08 11:35:48.497', 'tenant@thingsboard.org', 0 );
INSERT INTO "public"."t_sys_code_dsc" ( "code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES ('RECORDL20000', '投入二级品（一级类目）', 'B_RECORDL20003', 'B料-本订单', '1', '2022-10-08 11:21:05.45', 'tenant@thingsboard.org', '2022-10-08 11:35:53.999', 'tenant@thingsboard.org', 0 );
INSERT INTO "public"."t_sys_code_dsc" ("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES('RECORDL20000', '投入二级品（一级类目）', 'C_RECORDL20005', '次品浆', '1', '2022-10-08 11:21:43.387', 'tenant@thingsboard.org', '2022-10-08 11:35:59.669', 'tenant@thingsboard.org', 0 );
INSERT INTO "public"."t_sys_code_dsc" ("code_cl_id", "code_cl_dsc", "code_value", "code_dsc", "enabled_st", "crt_time", "crt_user", "update_time", "update_user", "is_group" )
VALUES('RECORDL20000', '投入二级品（一级类目）', 'B_RECORDL20004', 'B料-其他订单', '1', '2022-10-08 11:24:04.197', 'tenant@thingsboard.org', '2022-10-08 11:36:06.145', 'tenant@thingsboard.org', 0 );

-- 2022-10-20
INSERT INTO public.t_sys_code_dsc
( code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PICKINGTYPE00', '领料结果/记录表类型', '1', '退料', '1', NULL, NULL, NULL, NULL, 0),('PICKINGTYPE00', '领料结果/记录表类型', '0', '领料', '1', NULL, NULL, NULL, NULL, 0);

INSERT INTO public.t_sys_code_dsc
( code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PICKINGTYPE01', '领料工单类型', '0', '专项领料', '1', NULL, NULL, NULL, NULL, 0),
('PICKINGTYPE01', '领料工单类型', '1', '共耗领料', '1', NULL, NULL, NULL, NULL, 0),
('PICKINGTYPE01', '领料工单类型', '2', '补料', '1', NULL, NULL, NULL, NULL, 0),
('PICKINGTYPE01', '领料工单类型', '3', '退料', '1', NULL, NULL, NULL, NULL, 0);


INSERT INTO public.t_sys_code_dsc
( code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PICKINGSTATUS00', '料单工单状态', '0', '进行中', '1', NULL, NULL, NULL, NULL, 0),
('PICKINGSTATUS00', '料单工单状态', '1', '待提交', '1', NULL, NULL, NULL, NULL, 0),
('PICKINGSTATUS00', '料单工单状态', '2', '已完工', '1', NULL, NULL, NULL, NULL, 0)


-- 2022-10-24 原料投入预警比例、原料投入超额比例、拌料原料投入超额比例
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '110', '原料投入超额最大比例', '1', '2022-10-20 16:29:47.491', 'tenant@thingsboard.org', '2022-10-24 11:40:33.786', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '100', '原料投入预警最小比例', '1', '2022-10-20 16:29:22.230', 'tenant@thingsboard.org', '2022-10-24 11:40:45.493', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '110', '原料投入预警最大比例', '1', '2022-10-24 11:40:57.144', 'tenant@thingsboard.org', '2022-10-24 11:40:57.144', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('OTHER0000', '其他', '110', '拌料原料投入超额最大比例', '1', '2022-10-20 19:00:30.080', 'tenant@thingsboard.org', '2022-10-24 11:41:13.024', 'tenant@thingsboard.org', 0);

-- 定时消息提醒 2022-11-22
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'PROCESSENDMESSAGE', '定时消息提醒', '1', '2022-11-17 16:31:59.059', 'tenant@thingsboard.org', '2022-11-17 16:31:59.059', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('PROCESSENDMESSAGE', '定时消息提醒', '48', '工序结束超时提醒', '1', '2022-11-17 16:32:38.452', 'tenant@thingsboard.org', '2022-11-17 16:32:38.452', 'tenant@thingsboard.org', 0);

-- ERP班别 2022-11-22
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'ERPCLASS0000', 'ERP班别', '1', '2022-11-18 17:30:50.355', 'tenant@thingsboard.org', '2022-11-18 17:30:50.355', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPCLASS0000', 'ERP班别', 'BZ017', '甲班', '1', '2022-11-18 17:32:09.305', 'tenant@thingsboard.org', '2022-11-18 17:32:09.305', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('ERPCLASS0000', 'ERP班别', 'BZ018', '乙班', '1', '2022-11-18 17:32:36.202', 'tenant@thingsboard.org', '2022-11-18 17:32:36.202', 'tenant@thingsboard.org', 0);

-- 物料同步表头 2023-08-08
INSERT INTO public.t_sys_code_dsc
(code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES('GROUPCODE0000', '字典分类', 'MATERIALSYNCHEADER', '物料同步表头', '1', '2022-11-18 17:30:50.355', 'tenant@thingsboard.org', '2022-11-18 17:30:50.355', 'tenant@thingsboard.org', 1);


--  新增质检相关字典
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(242, 'GROUPCODE0000', '字典分类', 'QCCF0000', '品质类目配置表检查项字段', '1', '2025-07-04 11:23:19.484', 'tenant@thingsboard.org', '2025-07-04 11:23:19.484', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(243, 'QCCF0000', '品质类目配置表检查项字段', 'QCCF0001', '每锅用量标准(kg)', '1', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(244, 'QCCF0000', '品质类目配置表检查项字段', 'QCCF0002', '每锅用量(kg)', '1', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(245, 'QCCF0000', '品质类目配置表检查项字段', 'QCCF0003', '原料批次', '1', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(247, 'GROUPCODE0000', '字典分类', 'QPJ0000', '品质方案判定信息字段', '1', '2025-07-04 16:07:36.917', 'tenant@thingsboard.org', '2025-07-04 16:07:36.917', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(249, 'QPJ0000', '品质方案判定信息字段', 'QPJ0002', '实际值（首检）', '1', '2025-07-04 16:08:40.700', 'tenant@thingsboard.org', '2025-07-04 16:08:40.700', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(258, 'GJGX0000', '关键工序（质检）', 'GJGX0004', '面皮搅拌', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-10 08:25:28.360', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(252, 'QPJ0000', '品质方案判定信息字段', 'QPJ0005', '实际值(末值)', '1', '2025-07-04 16:10:21.503', 'tenant@thingsboard.org', '2025-07-04 16:10:21.503', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(248, 'QPJ0000', '品质方案判定信息字段', 'QPJ0001', '检测时间（首检）', '1', '2025-07-04 16:08:14.455', 'tenant@thingsboard.org', '2025-07-04 16:10:55.610', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(250, 'QPJ0000', '品质方案判定信息字段', 'QPJ0003', '判定（首检）', '1', '2025-07-04 16:09:28.066', 'tenant@thingsboard.org', '2025-07-04 16:11:01.226', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(251, 'QPJ0000', '品质方案判定信息字段', 'QPJ0004', '检测时间(末值)', '1', '2025-07-04 16:09:51.725', 'tenant@thingsboard.org', '2025-07-04 16:11:11.572', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(253, 'QPJ0000', '品质方案判定信息字段', 'QPJ0006', '判定(末值)', '1', '2025-07-04 16:10:36.293', 'tenant@thingsboard.org', '2025-07-04 16:11:15.424', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(254, 'GROUPCODE0000', '字典分类', 'GJGX0000', '关键工序（质检）', '1', '2025-07-07 15:43:52.531', 'tenant@thingsboard.org', '2025-07-07 15:43:52.531', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(255, 'GJGX0000', '关键工序（质检）', 'GJGX0001', '产前检验', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(259, 'GJGX0000', '关键工序（质检）', 'GJGX0005', '排出后料温测量', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(260, 'GJGX0000', '关键工序（质检）', 'GJGX0006', '品温搅拌', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(261, 'GJGX0000', '关键工序（质检）', 'GJGX0007', '搅拌速度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(262, 'GJGX0000', '关键工序（质检）', 'GJGX0008', '检查时间', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(263, 'GJGX0000', '关键工序（质检）', 'GJGX0009', '第一张、第四张、使用杀菌水', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(264, 'GJGX0000', '关键工序（质检）', 'GJGX0010', '成型速度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(265, 'GJGX0000', '关键工序（质检）', 'GJGX0011', '探针直径', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(266, 'GJGX0000', '关键工序（质检）', 'GJGX0012', '烘烤温度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(267, 'GJGX0000', '关键工序（质检）', 'GJGX0013', '烘烤时间', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(268, 'GJGX0000', '关键工序（质检）', 'GJGX0014', '烘烤效果', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(269, 'GJGX0000', '关键工序（质检）', 'GJGX0015', '水冷前盖量温度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(270, 'GJGX0000', '关键工序（质检）', 'GJGX0016', '水冷时间', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(271, 'GJGX0000', '关键工序（质检）', 'GJGX0017', '检瓶外观', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(272, 'GJGX0000', '关键工序（质检）', 'GJGX0018', '内容器温度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(273, 'GJGX0000', '关键工序（质检）', 'GJGX0019', '物料及外袋日期打印', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(274, 'GJGX0000', '关键工序（质检）', 'GJGX0020', '净重', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(246, 'QCCF0000', '品质类目配置表检查项字段', 'QCCF0004', '是否超保质期', '1', '2025-07-04 15:07:43.400', 'tenant@thingsboard.org', '2025-07-09 15:17:37.123', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(275, 'GJGX0000', '关键工序（质检）', 'GJGX0021', '加量水比例', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(276, 'GJGX0000', '关键工序（质检）', 'GJGX0022', '硫磺水比例', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(277, 'GJGX0000', '关键工序（质检）', 'GJGX0023', '外气温度', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(279, 'GJGX0000', '关键工序（质检）', 'GJGX0025', '压差', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(281, 'GROUPCODE0000', '字典分类', 'JKFF0000', '监控方法（质检）', '1', '2025-07-07 16:03:39.347', 'tenant@thingsboard.org', '2025-07-07 16:03:39.347', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(282, 'JKFF0000', '监控方法（质检）', 'JKFF0001', '电子台秤', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(283, 'JKFF0000', '监控方法（质检）', 'JKFF0002', '钢尺', '1', '2025-07-07 16:04:34.326', 'tenant@thingsboard.org', '2025-07-07 16:04:34.326', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(284, 'JKFF0000', '监控方法（质检）', 'JKFF0003', '红外线测温仪', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(285, 'JKFF0000', '监控方法（质检）', 'JKFF0004', '机台控制屏幕', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(286, 'JKFF0000', '监控方法（质检）', 'JKFF0005', '计时器', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(287, 'JKFF0000', '监控方法（质检）', 'JKFF0006', '目测', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(288, 'JKFF0000', '监控方法（质检）', 'JKFF0007', '目测/品尝', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(289, 'JKFF0000', '监控方法（质检）', 'JKFF0008', '目视/记录', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(290, 'JKFF0000', '监控方法（质检）', 'JKFF0009', '设定值', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(291, 'JKFF0000', '监控方法（质检）', 'JKFF0010', '温度计', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(292, 'JKFF0000', '监控方法（质检）', 'JKFF0011', '温湿度计', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(293, 'JKFF0000', '监控方法（质检）', 'JKFF0012', '压差计', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(294, 'JKFF0000', '监控方法（质检）', 'JKFF0013', '针式温度计', '1', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', '2025-07-07 16:04:08.973', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(295, 'GROUPCODE0000', '字典分类', 'ZDLX0000', '字段类型', '1', '2025-07-08 10:22:04.616', 'tenant@thingsboard.org', '2025-07-08 10:22:04.616', 'tenant@thingsboard.org', 1);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(296, 'ZDLX0000', '字段类型', 'ZDLX0001', '文本', '1', '2025-07-08 10:22:38.748', 'tenant@thingsboard.org', '2025-07-08 10:22:38.748', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(298, 'ZDLX0000', '字段类型', 'ZDLX0003', '数值', '1', '2025-07-08 10:23:14.177', 'tenant@thingsboard.org', '2025-07-08 10:23:14.177', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(297, 'ZDLX0000', '字段类型', 'ZDLX0002', '日期', '1', '2025-07-08 10:22:56.553', 'tenant@thingsboard.org', '2025-07-08 10:23:29.901', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(299, 'ZDLX0000', '字段类型', 'ZDLX0004', '下拉框', '1', '2025-07-08 10:23:54.000', 'tenant@thingsboard.org', '2025-07-08 10:23:54.000', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(280, 'GJGX0000', '关键工序（质检）', 'GJGX0026', '残氧', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-10 08:21:37.922', 'tenant@thingsboard.org', 0);
INSERT INTO public.t_sys_code_dsc
(code_id, code_cl_id, code_cl_dsc, code_value, code_dsc, enabled_st, crt_time, crt_user, update_time, update_user, is_group)
VALUES(278, 'GJGX0000', '关键工序（质检）', 'GJGX0024', '外箱喷码', '1', '2025-07-07 15:46:33.439', 'tenant@thingsboard.org', '2025-07-10 08:22:11.523', 'tenant@thingsboard.org', 0);
