DROP TABLE IF EXISTS t_sys_log;
CREATE TABLE t_sys_log(
    id serial NOT NULL,
    methods VARCHAR(50),
    way VARCHAR(100),
    in_param VARCHAR(2000),
    ip VARCHAR(20),
    user_id VARCHAR(100),
    created_name VARCHAR(50),
    created_time timestamp,
    created_username VARCHAR(50),
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_sys_log IS '全局日志表';
COMMENT ON COLUMN t_sys_log.id IS 'id';
COMMENT ON COLUMN t_sys_log.methods IS '功能';
COMMENT ON COLUMN t_sys_log.way IS '方式';
COMMENT ON COLUMN t_sys_log.in_param IS '入参';
COMMENT ON COLUMN t_sys_log.ip IS 'ip地址';
COMMENT ON COLUMN t_sys_log.created_name IS '创建人姓名';
COMMENT ON COLUMN t_sys_log.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_log.created_username IS '创建人';
