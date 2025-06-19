-- 料筐表

CREATE TABLE t_sys_charging_basket(
    charging_basket_id SERIAL NOT NULL,
    code VARCHAR(15),
    br VARCHAR(255),
    charging_basket_size VARCHAR(50),
    charging_basket_status VARCHAR(1),
    max_bearing VARCHAR(50),
    created_name VARCHAR(100),
    created_time DATE,
    updated_name VARCHAR(100),
    updated_time DATE,
    PRIMARY KEY (charging_basket_id)
);

COMMENT ON TABLE t_sys_charging_basket IS '料筐表';
COMMENT ON COLUMN t_sys_charging_basket.charging_basket_id IS 'id';
COMMENT ON COLUMN t_sys_charging_basket.code IS '料筐编码';
COMMENT ON COLUMN t_sys_charging_basket.br IS '备注';
COMMENT ON COLUMN t_sys_charging_basket.charging_basket_size IS '料筐尺寸';
COMMENT ON COLUMN t_sys_charging_basket.charging_basket_status IS '状态0：作废 1：启用';
COMMENT ON COLUMN t_sys_charging_basket.max_bearing IS '最大承重';
COMMENT ON COLUMN t_sys_charging_basket.created_name IS '创建人';
COMMENT ON COLUMN t_sys_charging_basket.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_charging_basket.updated_name IS '修改人';
COMMENT ON COLUMN t_sys_charging_basket.updated_time IS '修改时间';
ALTER TABLE public.t_sys_charging_basket ADD weight float4 NULL;
COMMENT ON COLUMN public.t_sys_charging_basket.weight IS '料框重量';

