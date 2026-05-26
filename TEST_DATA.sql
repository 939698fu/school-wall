-- 校园微墙测试数据脚本
-- 用途：补齐帖子图片、个人信息、收藏/关注、私信图片与未读会话，便于前后端联调
-- 默认联调账号：
--   username: chengzi
--   password: 123456

USE `school_wall`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `message`;
TRUNCATE TABLE `follow_record`;
TRUNCATE TABLE `collect_record`;
TRUNCATE TABLE `like_record`;
TRUNCATE TABLE `comment`;
TRUNCATE TABLE `post`;
TRUNCATE TABLE `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- 当前后端的密码格式为 salt$base64(sha256(password + salt))
SET @pwd = 'c2Nob29sd2FsbF9zYWx0XzIwMjQ=$Nq9s3E7I+RYTDgG7hW4ZWmbNQNYUC0H626rPWwd03Z8=';

INSERT INTO `user`
(`id`, `username`, `password`, `open_id`, `nickname`, `avatar`, `school`, `bio`,
 `post_count`, `like_count`, `collect_count`, `follower_count`, `following_count`, `create_time`, `update_time`)
VALUES
(1, 'chengzi', @pwd, 'wx_openid_001', '橙子不甜', '🍊', '某某大学', '喜欢吃喝玩乐，常在校园墙摸鱼。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 120 DAY), NOW()),
(2, 'shuxue', @pwd, 'wx_openid_002', '数学学长', '🦁', '某某大学', '数学系大三，看到题就想证明。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 110 DAY), NOW()),
(3, 'xiaojuzi', @pwd, 'wx_openid_003', '小橘子同学', '🐱', '某某大学', '努力学习的大一新生。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 100 DAY), NOW()),
(4, 'treehole', @pwd, 'wx_openid_004', '树洞来信', '🐼', '某某大学', '把没说出口的话交给风。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 95 DAY), NOW()),
(5, 'studyhub', @pwd, 'wx_openid_005', '学习委员本委', '📖', '某某大学', '专注整理资料，也负责分享。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 90 DAY), NOW()),
(6, 'foodie', @pwd, 'wx_openid_006', '美食探索者', '🍜', '某某大学', '正在逐个攻克食堂和小吃街。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 88 DAY), NOW()),
(7, 'cloudy', @pwd, 'wx_openid_007', '小云同学', '☁️', '某某大学', '热爱记录校园里的小确幸。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 80 DAY), NOW()),
(8, 'ballboy', @pwd, 'wx_openid_008', '运动少年', '⚽', '某某大学', '每周都在找人踢球。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 75 DAY), NOW()),
(9, 'musicman', @pwd, 'wx_openid_009', '音乐达人', '🎵', '某某大学', '吉他社常驻选手。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 70 DAY), NOW()),
(10, 'coder', @pwd, 'wx_openid_010', '程序猿小王', '💻', '某某大学', '白天写代码，晚上 debug。', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 65 DAY), NOW());

INSERT INTO `post`
(`id`, `user_id`, `title`, `content`, `images`, `tag`, `tag_color`, `is_anon`, `is_private`, `likes`, `comments`, `create_time`, `update_time`)
VALUES
(1, 1, '北区食堂新出的麻辣香锅真的绝了！！',
 '今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口，随手点了一份结果完全惊艳到我了。食材新鲜、锅底够香、价格也很实惠，强烈推荐大家去试试！',
 JSON_ARRAY(
   'https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=900&q=80',
   'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=900&q=80'
 ),
 '美食', 'orange', 1, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(2, 3, '有没有大神可以帮我看一下这道数分题？',
 '这道题我做了两个小时还没搞明白，最后一步总是卡住。题目是设 f(x) 在 [a,b] 上连续，在 (a,b) 上可导，且 f(a)=f(b)=0，证明存在 ξ∈(a,b) 使得 f''(ξ)+f(ξ)=0。求带带！',
 JSON_ARRAY('https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=900&q=80'),
 '学习', 'blue', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 4, '想向图书馆二楼靠窗那个男生表白',
 '每次去图书馆都能看到你，黑色卫衣，总是戴着耳机认真看书。如果你刚好看到这条帖子，可以私信我吗。',
 JSON_ARRAY('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=900&q=80'),
 '表白', 'pink', 1, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(4, 5, '分享一个超好用的备考资料整理网站',
 '期末季到了，分享给大家一个整理好的备考资源站，里面有历年真题和重点笔记，纯白嫖。想找资料的同学可以先收藏这条。',
 JSON_ARRAY(
   'https://images.unsplash.com/photo-1513258496099-48168024aec0?auto=format&fit=crop&w=900&q=80',
   'https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=900&q=80'
 ),
 '资源', 'green', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(5, 7, '春日限定！樱花树下的图书馆',
 '今天路过图书馆门口，发现樱花都开了，粉色花瓣飘下来的时候真的很治愈，随手拍了几张分享给大家。',
 JSON_ARRAY(
   'https://images.unsplash.com/photo-1522383225653-ed111181a951?auto=format&fit=crop&w=900&q=80',
   'https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=900&q=80',
   'https://images.unsplash.com/photo-1527061011665-3652c757a4d4?auto=format&fit=crop&w=900&q=80'
 ),
 '校园生活', 'pink', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 8, '周末足球友谊赛报名啦！',
 '周六下午三点，足球场有一场友谊赛，欢迎喜欢足球的同学一起来玩！不论水平，重在参与，有意向的可以私信我报名。',
 NULL,
 '运动', 'green', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 30 HOUR), DATE_SUB(NOW(), INTERVAL 30 HOUR)),
(7, 10, 'Python 学习路线分享',
 '整理了一份 Python 学习路线图，从入门到进阶，包含推荐书籍、在线课程和实战项目，分享给想学 Python 的同学。',
 JSON_ARRAY('https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=900&q=80'),
 '学习', 'blue', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(8, 1, '图书馆自习座位被人占座了该怎么办',
 '今天去图书馆看到一排座位全用书包占着，但是人都不在。我等了快一个小时了，这种情况可以向谁反馈？',
 NULL,
 '校园生活', 'gray', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 40 HOUR), DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(9, 9, '周五晚吉他社演出，欢迎来听！',
 '本周五晚上七点，大学生活动中心小剧场，吉他社专场演出，有弹唱、指弹和原创歌曲，欢迎来捧场。',
 JSON_ARRAY('https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&w=900&q=80'),
 '校园生活', 'green', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 6, '南门小吃街的烤冷面 YYDS！',
 '南门口那家烤冷面真的绝了，加蛋加肠才 8 块钱，酱料酸甜刚好，每次路过都要排队，但真的值得。',
 JSON_ARRAY('https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=900&q=80'),
 '美食', 'orange', 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(11, 4, '深夜 emo 一下...',
 '最近压力有点大，课程越来越难，感觉自己什么都学不会。有时候真的会怀疑自己是不是不适合现在的节奏。',
 NULL,
 '校园生活', 'gray', 1, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(12, 1, '只给自己看的发帖草稿',
 '这是一条仅自己可见的帖子，用来验证前端不会把私密贴展示给其他人。',
 NULL,
 '校园生活', 'gray', 0, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 20 MINUTE), DATE_SUB(NOW(), INTERVAL 20 MINUTE));

INSERT INTO `comment`
(`id`, `post_id`, `user_id`, `content`, `likes`, `create_time`, `update_time`)
VALUES
(1, 1, 3, '已经约好舍友明天中午去！感谢博主强推！🔥🔥', 0, DATE_SUB(NOW(), INTERVAL 170 MINUTE), NOW()),
(2, 1, 6, '那家我也去吃了，土豆片真的很绝。', 0, DATE_SUB(NOW(), INTERVAL 160 MINUTE), NOW()),
(3, 1, 1, '@美食探索者 你下次试试加宽粉，真的会更香。', 0, DATE_SUB(NOW(), INTERVAL 150 MINUTE), NOW()),
(4, 2, 2, '考虑辅助函数 g(x)=e^x·f(x)，然后对 g 用罗尔定理即可。', 0, DATE_SUB(NOW(), INTERVAL 115 MINUTE), NOW()),
(5, 2, 3, '啊啊啊懂了懂了！太感谢了！', 0, DATE_SUB(NOW(), INTERVAL 110 MINUTE), NOW()),
(6, 2, 10, '这题我也卡住了，感谢学长。', 0, DATE_SUB(NOW(), INTERVAL 105 MINUTE), NOW()),
(7, 3, 1, '图书馆二楼靠窗男生们：是我！都是我！', 0, DATE_SUB(NOW(), INTERVAL 280 MINUTE), NOW()),
(8, 3, 7, '勇敢一点！冲！', 0, DATE_SUB(NOW(), INTERVAL 270 MINUTE), NOW()),
(9, 4, 10, '这个我也在用，资料整理得很清楚。', 0, DATE_SUB(NOW(), INTERVAL 460 MINUTE), NOW()),
(10, 4, 3, '正好在复习，先收藏了。', 0, DATE_SUB(NOW(), INTERVAL 440 MINUTE), NOW()),
(11, 5, 1, '明天中午准备去打卡拍照了。', 0, DATE_SUB(NOW(), INTERVAL 1300 MINUTE), NOW()),
(12, 6, 1, '算我一个！我带水过去。', 0, DATE_SUB(NOW(), INTERVAL 1700 MINUTE), NOW()),
(13, 7, 5, '这份路线图很完整，适合新手入门。', 0, DATE_SUB(NOW(), INTERVAL 2800 MINUTE), NOW()),
(14, 8, 5, '我们学校规定占座超过 30 分钟可以联系管理员处理。', 0, DATE_SUB(NOW(), INTERVAL 2200 MINUTE), NOW()),
(15, 9, 7, '上次去听过，现场氛围真的很好。', 0, DATE_SUB(NOW(), INTERVAL 4300 MINUTE), NOW()),
(16, 10, 1, '这家我愿称之为小吃街 MVP。', 0, DATE_SUB(NOW(), INTERVAL 5600 MINUTE), NOW()),
(17, 11, 3, '抱抱你，最近真的很多人都在焦虑。', 0, DATE_SUB(NOW(), INTERVAL 540 MINUTE), NOW()),
(18, 11, 2, '别急，先把节奏放慢一点也没关系。', 0, DATE_SUB(NOW(), INTERVAL 500 MINUTE), NOW());

INSERT INTO `like_record`
(`id`, `user_id`, `post_id`, `comment_id`, `create_time`, `update_time`)
VALUES
(1, 3, 1, NULL, DATE_SUB(NOW(), INTERVAL 175 MINUTE), NOW()),
(2, 6, 1, NULL, DATE_SUB(NOW(), INTERVAL 174 MINUTE), NOW()),
(3, 7, 1, NULL, DATE_SUB(NOW(), INTERVAL 173 MINUTE), NOW()),
(4, 2, 2, NULL, DATE_SUB(NOW(), INTERVAL 118 MINUTE), NOW()),
(5, 5, 2, NULL, DATE_SUB(NOW(), INTERVAL 117 MINUTE), NOW()),
(6, 1, 2, NULL, DATE_SUB(NOW(), INTERVAL 116 MINUTE), NOW()),
(7, 1, 3, NULL, DATE_SUB(NOW(), INTERVAL 290 MINUTE), NOW()),
(8, 7, 3, NULL, DATE_SUB(NOW(), INTERVAL 289 MINUTE), NOW()),
(9, 3, 4, NULL, DATE_SUB(NOW(), INTERVAL 470 MINUTE), NOW()),
(10, 10, 4, NULL, DATE_SUB(NOW(), INTERVAL 465 MINUTE), NOW()),
(11, 1, 5, NULL, DATE_SUB(NOW(), INTERVAL 1400 MINUTE), NOW()),
(12, 8, 6, NULL, DATE_SUB(NOW(), INTERVAL 1750 MINUTE), NOW()),
(13, 5, 7, NULL, DATE_SUB(NOW(), INTERVAL 2850 MINUTE), NOW()),
(14, 1, 8, NULL, DATE_SUB(NOW(), INTERVAL 2250 MINUTE), NOW()),
(15, 7, 9, NULL, DATE_SUB(NOW(), INTERVAL 4350 MINUTE), NOW()),
(16, 1, 10, NULL, DATE_SUB(NOW(), INTERVAL 5650 MINUTE), NOW()),
(17, 3, NULL, 4, DATE_SUB(NOW(), INTERVAL 114 MINUTE), NOW()),
(18, 10, NULL, 4, DATE_SUB(NOW(), INTERVAL 113 MINUTE), NOW()),
(19, 1, NULL, 7, DATE_SUB(NOW(), INTERVAL 279 MINUTE), NOW()),
(20, 2, NULL, 17, DATE_SUB(NOW(), INTERVAL 535 MINUTE), NOW());

INSERT INTO `collect_record`
(`id`, `user_id`, `post_id`, `create_time`, `update_time`)
VALUES
(1, 1, 4, DATE_SUB(NOW(), INTERVAL 455 MINUTE), NOW()),
(2, 1, 7, DATE_SUB(NOW(), INTERVAL 2805 MINUTE), NOW()),
(3, 1, 10, DATE_SUB(NOW(), INTERVAL 5605 MINUTE), NOW()),
(4, 3, 1, DATE_SUB(NOW(), INTERVAL 169 MINUTE), NOW()),
(5, 3, 4, DATE_SUB(NOW(), INTERVAL 450 MINUTE), NOW()),
(6, 5, 2, DATE_SUB(NOW(), INTERVAL 112 MINUTE), NOW()),
(7, 5, 7, DATE_SUB(NOW(), INTERVAL 2790 MINUTE), NOW()),
(8, 6, 1, DATE_SUB(NOW(), INTERVAL 168 MINUTE), NOW()),
(9, 7, 5, DATE_SUB(NOW(), INTERVAL 1290 MINUTE), NOW()),
(10, 10, 2, DATE_SUB(NOW(), INTERVAL 109 MINUTE), NOW());

INSERT INTO `follow_record`
(`id`, `user_id`, `followed_id`, `create_time`, `update_time`)
VALUES
(1, 1, 2, DATE_SUB(NOW(), INTERVAL 60 DAY), NOW()),
(2, 1, 5, DATE_SUB(NOW(), INTERVAL 52 DAY), NOW()),
(3, 1, 6, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW()),
(4, 3, 2, DATE_SUB(NOW(), INTERVAL 35 DAY), NOW()),
(5, 3, 5, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW()),
(6, 5, 2, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
(7, 5, 10, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW()),
(8, 6, 1, DATE_SUB(NOW(), INTERVAL 25 DAY), NOW()),
(9, 7, 1, DATE_SUB(NOW(), INTERVAL 20 DAY), NOW()),
(10, 8, 1, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
(11, 8, 2, DATE_SUB(NOW(), INTERVAL 18 DAY), NOW()),
(12, 9, 7, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
(13, 10, 5, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW());

INSERT INTO `message`
(`id`, `from_id`, `to_id`, `content`, `type`, `file_url`, `is_read`, `create_time`, `update_time`)
VALUES
(1, 2, 1, '你好，我看了你发的那道数分题。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 95 MINUTE), NOW()),
(2, 1, 2, '太好了，我最后一步总卡住。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 90 MINUTE), NOW()),
(3, 2, 1, '你先试试构造 g(x)=e^x·f(x)，再用罗尔定理。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 86 MINUTE), NOW()),
(4, 1, 2, '懂了懂了！那你有空可以帮我看看另外两道吗？', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 82 MINUTE), NOW()),
(5, 2, 1, '可以，明天下午图书馆见！', 'text', NULL, 0, DATE_SUB(NOW(), INTERVAL 78 MINUTE), NOW()),
(6, 1, 2, '', 'image', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=900&q=80', 0, DATE_SUB(NOW(), INTERVAL 75 MINUTE), NOW()),
(7, 6, 1, '北区食堂的麻辣香锅真的有那么好吃吗？', 'text', NULL, 0, DATE_SUB(NOW(), INTERVAL 35 MINUTE), NOW()),
(8, 1, 6, '真的可以冲，我刚又去吃了一次。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 32 MINUTE), NOW()),
(9, 6, 1, '', 'image', 'https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=900&q=80', 0, DATE_SUB(NOW(), INTERVAL 28 MINUTE), NOW()),
(10, 5, 1, '你好，我想把下周志愿活动的海报也发到校园墙，可以吗？', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
(11, 1, 5, '可以呀，你把文案和图片发我。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 290 MINUTE), NOW()),
(12, 5, 1, '', 'image', 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=900&q=80', 1, DATE_SUB(NOW(), INTERVAL 280 MINUTE), NOW()),
(13, 8, 1, '周末足球赛记得来啊！', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 26 HOUR), NOW()),
(14, 1, 8, '一定到，我还会带一个室友。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 25 HOUR), NOW()),
(15, 7, 1, '你拍的樱花照片太好看了，能发我原图吗？', 'text', NULL, 0, DATE_SUB(NOW(), INTERVAL 7 HOUR), NOW()),
(16, 1, 7, '可以呀，我晚点整理完发你。', 'text', NULL, 1, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW());

UPDATE `post` p
SET `comments` = (
  SELECT COUNT(*) FROM `comment` c WHERE c.post_id = p.id
);

UPDATE `post` p
SET `likes` = (
  SELECT COUNT(*) FROM `like_record` lr WHERE lr.post_id = p.id
);

UPDATE `comment` c
SET `likes` = (
  SELECT COUNT(*) FROM `like_record` lr WHERE lr.comment_id = c.id
);

UPDATE `user` u
SET
  `post_count` = (SELECT COUNT(*) FROM `post` p WHERE p.user_id = u.id),
  `like_count` = (SELECT COALESCE(SUM(p.likes), 0) FROM `post` p WHERE p.user_id = u.id),
  `collect_count` = (SELECT COUNT(*) FROM `collect_record` cr WHERE cr.user_id = u.id),
  `follower_count` = (SELECT COUNT(*) FROM `follow_record` fr WHERE fr.followed_id = u.id),
  `following_count` = (SELECT COUNT(*) FROM `follow_record` fr WHERE fr.user_id = u.id);

SELECT 'TEST_DATA.sql 导入完成' AS result;
