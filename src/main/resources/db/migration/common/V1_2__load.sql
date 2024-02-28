insert into limits
values ('buy', 0.95),
       ('sell', 1.05);

insert into account (id, name, balance, currency, account_type)
values ('b1f78043-5c2d-40d8-b2fb-ded812f460dd', 'trader_a', 1000000000, 'USD', 'system'),
       ('9db22e1d-d802-418e-991b-d6da1795bccb', 'trader_b', 1000000000, 'USD', 'system'),
       ('91e0daa0-b843-46f7-99d8-c3cc747e375f', 'trader_c', 1000000000, 'USD', 'system'),
       ('aac59922-6e5c-4dc2-82e5-1dc04308ba7b', 'trader_d', 1000000000, 'USD', 'system'),
       ('9018bb48-969d-474f-9e63-be03457bbbd3', 'trader_e', 1000000000, 'USD', 'system'),
       ('3786c84e-2add-4cba-89ec-5c38b1619f01', 'trader_f', 1000000000, 'USD', 'system'),
       ('76d3b46c-3692-4bc5-a7fc-43ee4ec04de2', 'trader_g', 1000000000, 'USD', 'system'),
       ('fdf73cf1-f0cc-4d65-82f2-a6a602e99a88', 'trader_h', 1000000000, 'USD', 'system'),
       ('57903f18-2768-4c05-b092-68b7f78d78c2', 'trader_i', 1000000000, 'USD', 'system'),
       ('1aecfd8d-3911-407f-8529-1fe644740f30', 'trader_j', 1000000000, 'USD', 'system');

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       'b1f78043-5c2d-40d8-b2fb-ded812f460dd'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '9db22e1d-d802-418e-991b-d6da1795bccb'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '91e0daa0-b843-46f7-99d8-c3cc747e375f'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       'aac59922-6e5c-4dc2-82e5-1dc04308ba7b'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '9018bb48-969d-474f-9e63-be03457bbbd3'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '3786c84e-2add-4cba-89ec-5c38b1619f01'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '76d3b46c-3692-4bc5-a7fc-43ee4ec04de2'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       'fdf73cf1-f0cc-4d65-82f2-a6a602e99a88'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '57903f18-2768-4c05-b092-68b7f78d78c2'
from generate_series(1, 50) as n;

insert into account (name, balance, currency, account_type, parent_id)
select md5(random()::text),
       5000.00 + random() * 15000.00,
       'USD',
       'trading',
       '1aecfd8d-3911-407f-8529-1fe644740f30'
from generate_series(1, 50) as n;

-- ###########################################################

insert into portfolio (account_id)
select id
from account;

-- ###########################################################

insert into product (buy_price, sell_price, currency, reference)
select 1.50 + random() * 2.50,
       2.75 + random() * 3.75,
       'USD',
       concat('product-', n::text)
from generate_series(1, 150) as n;
