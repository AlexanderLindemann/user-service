BEGIN TRANSACTION;

INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('a97a72f5-21f9-4597-b805-a3368cb0612e', 'PERIOD_ENTRY', '1st Time of a Period App Entry', 'COMMON_AND_SOCIAL',
        'Once a period of FanToken Reward calculation', 5, 10, 5, 5, NULL, NULL, NULL, NULL,
        '2022-04-03 01:27:07.316981', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('2d1cd574-6556-4d66-9649-0b516e81a9c4', 'WATCH_VIDEO', 'Watch Video', 'COMMON_AND_SOCIAL',
        'View more than 20 sec', 3, 3, NULL, NULL, NULL, NULL, NULL, NULL, '2022-04-03 01:27:07.316981', NULL, 'script',
        NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('13fcd2c8-9489-4a1e-b281-b75197d68e11', 'QUIZ', 'Take the Quiz', 'IMPACT', 'comment POE', 10, 50, 5, 5, NULL,
        NULL, 1, 1, '2022-04-03 01:27:07.316981', '2022-04-07 13:50:57.630079', 'script',
        'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('85c78525-7e6f-4d58-892c-09bb4af40ecf', 'PROFILE_COMPLETE', 'Full Profile Completion', 'ONBOARDING', 'Once', 20,
        50, 1, 1, NULL, NULL, NULL, NULL, '2022-04-03 01:27:07.316981', '2022-04-19 12:05:14.249455', 'script',
        'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('4b1971c4-a3e0-4a84-b819-1bb8208c394a', 'CHALLENGE', 'Challenge Participation', 'IMPACT', NULL, 22, 10, 10, 10,
        NULL, NULL, 10, 10, '2022-04-03 01:27:07.316981', '2022-04-19 14:40:00.184541', 'script',
        'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('5b9311a0-8d6e-4d57-a86e-a7c4b8d307cc', 'LIKE', 'Inner Like', 'COMMON_AND_SOCIAL',
        'Like on news or post in-app', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2022-04-03 01:27:07.316981',
        '2022-06-07 17:29:05.111197', 'script', 'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('6d46ca56-f7e8-49b1-b883-91879dc500d9', 'VOTE', 'Vote', 'IMPACT', NULL, 5, NULL, NULL, 3, NULL, NULL, 2, 6,
        '2022-04-18 10:46:05.899966', '2022-04-19 11:58:33.177406', 'script', 'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('278e7442-2fc7-40a5-b966-cec83de4d727', 'SHARE', 'Share', 'COMMON_AND_SOCIAL', NULL, 3, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, '2022-05-14 17:30:39.204331', NULL, 'script', 'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('b25ddf9b-67f7-4aeb-b32a-0d9f7177e117', 'WHEEL_ROLLED', 'Wheel rolled', 'GAMIFICATION', NULL, 3, NULL, NULL, 2,
        NULL, NULL, 1, 3, '2022-05-14 17:30:39.204331', '2022-06-02 18:48:03.152528', 'script',
        'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('8c6561af-cd14-4106-87d7-b409e086ff18', 'REGISTRATION', '1st Time App Entry/Registration', 'ONBOARDING',
        'Only once, for Registration process', 50, 50, NULL, NULL, NULL, NULL, NULL, NULL, '2022-04-03 01:27:07.316981',
        '2022-05-03 08:38:24.46405', 'script', 'b9e50e84-37d9-4b5d-a364-0b70b2273b56');
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('a5897900-b2ba-4a4b-a424-4fcae65f5550', 'PRENFT_VOTE', 'Vote on NFT', 'IMPACT', NULL, 1, NULL, NULL, 2, NULL,
        NULL, NULL, NULL, '2022-08-03 08:09:40.797287', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('91bc8ee8-4e9d-489c-bb5d-05f8de37a4ec', 'PRENFT_OFFER', 'Offer preNFT (once a period)', 'PRENFT', NULL, 20,
        NULL, NULL, 40, NULL, NULL, NULL, NULL, '2022-08-03 08:09:40.797287', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('5563fb5c-d9bf-413a-af94-1da57a168be9', 'PRENFT_MODERATED', 'Offered preNFT has been moderated', 'PRENFT', NULL,
        1, NULL, NULL, 2, NULL, NULL, NULL, NULL, '2022-08-03 08:09:40.797287', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('a54a3808-fe69-4f13-9c78-9bf072c30848', 'PRENFT_VOTED_MORE_THAN',
        'Offered preNFT had been voted by more than 25 people', 'PRENFT', NULL, 100, NULL, NULL, 120, NULL, NULL, NULL,
        NULL, '2022-08-03 08:09:40.797287', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.poe (id, code, name, group_name, comment, points_reward, coins_reward, coins_reward_sub,
                        points_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub,
                        created_at, updated_at, created_by, updated_by)
VALUES ('63c5551e-1bc4-4f1b-85be-9a5015b0653b', 'PRENFT_REALISED', 'NFT had been minted from offered preNFT', 'PRENFT',
        NULL, 300, NULL, NULL, 350, NULL, NULL, NULL, NULL, '2022-08-03 08:09:40.797287', NULL, 'script', NULL)
ON CONFLICT (id) DO NOTHING;
COMMIT;