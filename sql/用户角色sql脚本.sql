
-- 1.获取tenant_profile表name='Default' id
-- 2.执行下面语句 令tenant_profile_id值等于上面获取的id
INSERT INTO public.tenant
(id, created_time, additional_info, tenant_profile_id, address, address2, city, country, email, phone, region, search_text, state, title, zip)
VALUES('dc623fb0-baeb-11ec-af69-872632e755c8'::uuid, 1649828271915, '{"description":"","homeDashboardId":null,"homeDashboardHideToolbar":true}',
       '6504aea0-3d7f-11ed-8949-d5ccaf7d57c3'::uuid, NULL, NULL, NULL, NULL, NULL, NULL, 'Global', '力诚', NULL, '力诚', NULL);
-- 3.获取tb_user表tenant_id字段id
-- 4.执行下面语句，更改 tenant_id为上面获取的id
INSERT INTO public.customer
(id, created_time, additional_info, address, address2, city, country, email, phone, search_text, state, tenant_id, title, zip)
VALUES('263874d0-bc5c-11ec-b9ff-d18292d42636'::uuid, 1649986450589, '{"description":"","homeDashboardId":null,"homeDashboardHideToolbar":true}', NULL, NULL, NULL, NULL, '123457@qq.com', '1564741214', '1457', NULL,
       '67adfcb0-3d7f-11ed-8949-d5ccaf7d57c3--tenant的id'::uuid, '1457', NULL);

-- select * from tb_user
-- 插入默认的角色用户
INSERT INTO "public"."t_sys_role_user"
("role_user_id", "role_id", "user_id", "created_time", "created_name", "updated_time", "updated_name", "user_status", "last_time")
VALUES (2, 3, '67adfcb0-3d7f-11ed-8949-d5ccaf7d57c3--tenant的id', '2022-08-23', 'tenant@thingsboard.org', NULL, NULL, '1', NULL);
INSERT INTO "public"."t_sys_role_user"("role_user_id", "role_id", "user_id", "created_time", "created_name", "updated_time", "updated_name", "user_status", "last_time")
VALUES (1, 99999, '64aa80b0-3d7f-11ed-8949-d5ccaf7d57c3--sysadmin的id', NULL, NULL, '2022-07-04', 'tenant@thingsboard.org', '1', NULL);
