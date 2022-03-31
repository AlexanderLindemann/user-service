insert into poe (code, name, group_name, comment, points_reward, coins_reward,
points_reward_sub, coins_reward_sub, usd_price, coins_price, free_amount_on_period, free_amount_on_period_sub, created_by)
values
('VOTE', 'Vote', 'IMPACT', null, 3, null,
2, null, null, null, 1, 5, 'script'),
('QUIZ', 'Take the Quiz', 'IMPACT', null, 10, 50,
5, 5, null, null, 1, 1, 'script'),
('CHALLENGE', 'Challenge Participation', 'IMPACT', null, 1, 1,
1, 1, null, null, 1, 1, 'script'),
('REGISTRATION', '1st Time App Entry/Registration', 'ONBOARDING', 'Only once, for Registration process', 50, 50,
null, null, null, null, null, null, 'script'),
('LIKE', 'Inner Like', 'COMMON_AND_SOCIAL', 'Like on news or post in-app', 1, 1,
null, null, null, null, null, null, 'script'),
('PROFILE_COMPLETE', 'Full Profile Completion', 'ONBOARDING', 'Once', 20, 50,
null, null, null, null, null, null, 'script'),
('PERIOD_ENTRY', '1st Time of a Period App Entry', 'COMMON_AND_SOCIAL', 'Once a period of FanToken Reward calculation', 5, 10,
5, 5, null, null, null, null, 'script'),
('WATCH_VIDEO', 'Watch Video', 'COMMON_AND_SOCIAL', 'View more than 20 sec', 3, 3,
null, null, null, null, null, null, 'script');
