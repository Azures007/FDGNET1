/**
    样例表，无业务用途，仅用于帮助开发参考 2025-08-13 hhh
 */
CREATE TABLE t_sys_demo (
                            demo_id SERIAL PRIMARY KEY,
                            demo_name VARCHAR(255),
                            demo_number VARCHAR(255),
                            remark VARCHAR(512),
                            created_time TIMESTAMP,
                            created_user VARCHAR(255),
                            updated_time TIMESTAMP,
                            updated_user VARCHAR(255),
                            enabled int4
);

COMMENT ON TABLE t_sys_demo IS '样例';

COMMENT ON COLUMN t_sys_demo.demo_id IS '样例ID';
COMMENT ON COLUMN t_sys_demo.demo_name IS '样例名称';
COMMENT ON COLUMN t_sys_demo.demo_number IS '样例编号';
COMMENT ON COLUMN t_sys_demo.remark IS '备注';
COMMENT ON COLUMN t_sys_demo.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_demo.created_user IS '创建人';
COMMENT ON COLUMN t_sys_demo.updated_time IS '更新时间';
COMMENT ON COLUMN t_sys_demo.updated_user IS '更新人';
COMMENT ON COLUMN t_sys_demo.enabled IS '是否启用';

-- 生成一些样例数据
INSERT INTO t_sys_demo(demo_name, demo_number, remark, created_time, created_user, updated_time, updated_user, enabled)
VALUES ('样例1', 'Demo1', '样例1', '2025-01-01 00:00:00', 'system', '2025-01-01 00:00:00', 'system', 1);
INSERT INTO t_sys_demo(demo_name, demo_number, remark, created_time, created_user, updated_time, updated_user, enabled)
VALUES ('样例2', 'Demo2', '样例2', '2025-01-01 00:00:00', 'system', '2025-01-01 00:00:00', 'system', 1);
INSERT INTO t_sys_demo(demo_name, demo_number, remark, created_time, created_user, updated_time, updated_user, enabled)
VALUES ('样例3', 'Demo3', '样例3', '2025-01-01 00:00:00', 'system', '2025-01-01 00:00:00', 'system', 1);
INSERT INTO t_sys_demo(demo_name, demo_number, remark, created_time, created_user, updated_time, updated_user, enabled)
VALUES ('样例4', 'Demo4', '样例4', '2025-01-01 00:00:00', 'system', '2025-01-01 00:00:00', 'system', 0);


