begin transaction;

insert into vote_price (votes, coins, created_by)
values
(1, 3, 'system'),
(10, 25, 'system'),
(100 ,200, 'system'),
(1000 ,1250, 'system'),
(10000, 5000, 'system');

commit;
