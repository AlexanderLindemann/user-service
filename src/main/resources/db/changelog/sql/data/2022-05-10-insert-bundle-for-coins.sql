begin transaction;

insert into bundle_for_coins (type, bundle_size, coins, image_url, created_by)
values
('VOTE', 1, 3,'', 'system'),
('VOTE', 10, 25,'', 'system'),
('VOTE', 100 ,200,'', 'system'),
('VOTE', 1000 ,1250,'', 'system'),
('VOTE', 10000, 5000,'', 'system'),
('WHEEL_SPIN', 1, 3,'', 'system'),
('WHEEL_SPIN', 10, 25,'', 'system'),
('WHEEL_SPIN', 100 ,200,'', 'system'),
('WHEEL_SPIN', 1000 ,1250,'', 'system'),
('WHEEL_SPIN', 10000, 5000,'', 'system'),
('NFT_VOTE', 1, 3,'', 'system'),
('NFT_VOTE', 10, 25,'', 'system'),
('NFT_VOTE', 100 ,200,'', 'system'),
('NFT_VOTE', 1000 ,1250,'', 'system'),
('NFT_VOTE', 10000, 5000,'', 'system');

commit;
