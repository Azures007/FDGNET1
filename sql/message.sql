-- 消息sql
CREATE TABLE t_sys_message_order(
    id serial NOT NULL,
    order_id INTEGER NOT NULL,
    order_type INTEGER,
    order_no VARCHAR(100),
    created_time timestamp,
    product_standard VARCHAR(255),
    bill_plan_qty VARCHAR(255),
    bot_message VARCHAR(255),
    mes_type VARCHAR(255),
    status_type VARCHAR(255),
    is_read VARCHAR(255),
    mes_time timestamp,
    user_id VARCHAR(255),
    PRIMARY KEY (id)
);

COMMENT ON TABLE t_sys_message_order IS '订单消息表';
COMMENT ON COLUMN t_sys_message_order.id IS '消息id;自增';
COMMENT ON COLUMN t_sys_message_order.order_id IS '订单id;订单id';
COMMENT ON COLUMN t_sys_message_order.order_type IS '订单类型;0=未开工、1=已开工、2=暂停、3=已完工';
COMMENT ON COLUMN t_sys_message_order.order_no IS '订单号';
COMMENT ON COLUMN t_sys_message_order.created_time IS '下单时间(转成 2022-12-05 12:12:12格式)';
COMMENT ON COLUMN t_sys_message_order.product_standard IS '产品规格';
COMMENT ON COLUMN t_sys_message_order.bill_plan_qty IS '预期产量';
COMMENT ON COLUMN t_sys_message_order.bot_message IS '移交信息';
COMMENT ON COLUMN t_sys_message_order.mes_type IS '消息类型 0：移交订单消息 1：订单变更消息';
COMMENT ON COLUMN t_sys_message_order.status_type IS '状态类型 0:移交 1：普通';
COMMENT ON COLUMN t_sys_message_order.is_read IS '是否已读 0:已读 1：未读';
COMMENT ON COLUMN t_sys_message_order.mes_time IS '消息时间';
COMMENT ON COLUMN t_sys_message_order.user_id IS '用户id';

-- 2022/7/13
ALTER TABLE public.t_sys_message_order ADD update_strs varchar NULL;
COMMENT ON COLUMN public.t_sys_message_order.update_strs IS '变更信息详情';
