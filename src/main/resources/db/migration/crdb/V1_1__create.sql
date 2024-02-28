--
-- Roach Trading schema for CockroachDB
--

-- drop table if exists account cascade;
-- drop table if exists booking_order cascade;
-- drop table if exists booking_order_item cascade;
-- drop table if exists portfolio cascade;
-- drop table if exists portfolio_item cascade;
-- drop table if exists product cascade;

create table limits
(
    name  varchar(64)    not null,
    value numeric(19, 2) not null,

    primary key (name)
);

-- drop sequence if exists account_seq;
create sequence if not exists account_seq
    start 1 increment by 1 cache 64;

create table account
(
    id               uuid           not null default gen_random_uuid(),
    name             varchar(64)    not null,
    balance          numeric(19, 2) not null,
    currency         varchar(3)     not null,
    account_type     varchar(15)    not null,
    created_at       timestamptz    null default clock_timestamp(),
    last_modified_at timestamptz,
    parent_id        uuid           null,

    primary key (id)
);

create unique index uidx_account_name
    on account (name);

alter table account
    add constraint check_account_type_value check (account_type in ('trading', 'system'));

alter table account
    add constraint check_positive_balance check (balance >= 0);

create table portfolio
(
    account_id  uuid not null,
    description varchar(255),

    primary key (account_id)
);

create table portfolio_item
(
    account_id uuid not null,
    product_id uuid not null,
    quantity   int4 not null,
    item_pos   int  not null,

    primary key (account_id, product_id, item_pos)
);

create table booking_order
(
    id             uuid           not null default gen_random_uuid(),
    approved_at    timestamptz,
    order_type     varchar(10),
    placed_at      timestamptz    not null,
    quantity       int4           not null,
    reference      varchar(255)   not null,
    total_amount   numeric(19, 2) not null,
    total_currency varchar(3)     not null,
    account_id     uuid,
    product_id     uuid           not null,

    primary key (id)
);

create unique index on booking_order (reference)
    storing (approved_at, order_type, placed_at, quantity, total_amount, total_currency, account_id, product_id);

create unique index uidx_order_placed_at
    on booking_order (placed_at, id) storing (total_amount);

alter table booking_order
    add constraint check_order_type_value check (order_type in ('BUY', 'SELL'));

create table booking_order_item
(
    account_id        uuid           not null,
    order_id          uuid           not null,
    transfer_amount   numeric(19, 2) not null,
    transfer_currency varchar(3)     not null,
    running_balance   numeric(19, 2) not null,
    running_currency  varchar(3)     not null,

    primary key (order_id, account_id)
);

create table product
(
    id               uuid           not null default gen_random_uuid(),
    foreign_id       uuid           null,
    buy_price        numeric(19, 2) not null,
    sell_price       numeric(19, 2) not null,
    spread           numeric(19, 2) AS (buy_price - sell_price) stored,
    currency         varchar(3)     not null,
    reference        varchar(128)   not null,
    last_modified_at timestamptz,

    primary key (id)
);

create unique index uidx_product_fid on product (foreign_id)
    storing (buy_price,sell_price,currency);

create unique index uidx_product_ref on product (reference)
    using hash with bucket_count =8
    storing (buy_price,sell_price,currency);

--
-- Foreign keys
--

alter table if exists account
    add constraint fk_account_to_parent
        foreign key (parent_id)
            references account (id);

alter table if exists booking_order
    add constraint fk_booking_order_to_account
        foreign key (account_id)
            references account;

alter table if exists booking_order
    add constraint fk_booking_order_to_product
        foreign key (product_id)
            references product;

alter table if exists booking_order_item
    add constraint fk_order_item_to_account
        foreign key (account_id)
            references account;

alter table if exists booking_order_item
    add constraint fk_order_item_to_order
        foreign key (order_id)
            references booking_order;

alter table if exists portfolio
    add constraint fk_portfolio_to_account
        foreign key (account_id)
            references account;

alter table if exists portfolio_item
    add constraint fk_portfolio_item_to_portfolio
        foreign key (account_id)
            references portfolio;

alter table if exists portfolio_item
    add constraint fk_portfolio_item_to_product
        foreign key (product_id)
            references product;

