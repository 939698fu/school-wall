create table user
(
    id              bigint auto_increment comment '用户ID'
        primary key,
    username        varchar(50)                            not null comment '用户名（登录用）',
    password        varchar(200)                           not null comment '密码（加密存储，格式：salt$hash）',
    open_id         varchar(100) default ''                null comment '微信OpenID',
    nickname        varchar(50)                            not null comment '用户昵称',
    avatar          varchar(500) default '?'               null comment '头像URL',
    school          varchar(100) default ''                null comment '学校名称',
    bio             varchar(100) default ''                null comment '个人简介',
    post_count      int          default 0                 null comment '帖子数',
    like_count      int          default 0                 null comment '获赞数',
    collect_count   int          default 0                 null comment '收藏数',
    create_time     datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    follower_count  int          default 0                 null,
    following_count int          default 0                 null,
    constraint username
        unique (username)
)
    comment '用户表';

create table follow_record
(
    id          bigint auto_increment comment '记录ID'
        primary key,
    user_id     bigint                             not null comment '关注者ID',
    followed_id bigint                             not null comment '被关注者ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime                           null,
    constraint uk_user_followed
        unique (user_id, followed_id),
    constraint follow_record_ibfk_1
        foreign key (user_id) references user (id)
            on delete cascade,
    constraint follow_record_ibfk_2
        foreign key (followed_id) references user (id)
            on delete cascade
)
    comment '关注记录表';

create index idx_followed_id
    on follow_record (followed_id);

create table message
(
    id          bigint auto_increment comment '消息ID'
        primary key,
    from_id     bigint                                not null comment '发送者ID',
    to_id       bigint                                not null comment '接收者ID',
    content     varchar(1000)                         not null comment '消息内容',
    type        varchar(20) default 'text'            null comment '消息类型(text/image)',
    is_read     tinyint(1)  default 0                 null comment '是否已读(0-未读,1-已读)',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime                              null,
    file_url    varchar(255)                          null,
    constraint message_ibfk_1
        foreign key (from_id) references user (id)
            on delete cascade,
    constraint message_ibfk_2
        foreign key (to_id) references user (id)
            on delete cascade
)
    comment '消息表';

create index idx_create_time
    on message (create_time);

create index idx_from_id
    on message (from_id);

create index idx_is_read
    on message (is_read);

create index idx_to_id
    on message (to_id);

create table post
(
    id          bigint auto_increment comment '帖子ID'
        primary key,
    user_id     bigint                                not null comment '作者ID',
    title       varchar(50)                           not null comment '帖子标题',
    content     text                                  not null comment '帖子内容',
    images      text                                  null comment '图片URL列表，JSON格式',
    tag         varchar(20) default '校园生活'        null comment '话题标签',
    tag_color   varchar(20) default 'gray'            null comment '标签颜色',
    is_anon     tinyint(1)  default 0                 null comment '是否匿名(0-否,1-是)',
    is_private  tinyint(1)  default 0                 null comment '是否仅自己可见(0-否,1-是)',
    likes       int         default 0                 null comment '点赞数',
    comments    int         default 0                 null comment '评论数',
    create_time datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint post_ibfk_1
        foreign key (user_id) references user (id)
            on delete cascade
)
    comment '帖子表';

create table collect_record
(
    id          bigint auto_increment comment '记录ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    post_id     bigint                             not null comment '帖子ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    udate_time  datetime                           null,
    update_time datetime                           null,
    constraint uk_user_post
        unique (user_id, post_id),
    constraint collect_record_ibfk_1
        foreign key (user_id) references user (id)
            on delete cascade,
    constraint collect_record_ibfk_2
        foreign key (post_id) references post (id)
            on delete cascade
)
    comment '收藏记录表';

create index idx_post_id
    on collect_record (post_id);

create table comment
(
    id          bigint auto_increment comment '评论ID'
        primary key,
    post_id     bigint                             not null comment '帖子ID',
    user_id     bigint                             not null comment '评论者ID',
    content     varchar(200)                       not null comment '评论内容',
    likes       int      default 0                 null comment '点赞数',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint comment_ibfk_1
        foreign key (post_id) references post (id)
            on delete cascade,
    constraint comment_ibfk_2
        foreign key (user_id) references user (id)
            on delete cascade
)
    comment '评论表';

create index idx_create_time
    on comment (create_time);

create index idx_post_id
    on comment (post_id);

create index idx_user_id
    on comment (user_id);

create table like_record
(
    id          bigint auto_increment comment '记录ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    post_id     bigint                             null comment '帖子ID',
    comment_id  bigint                             null comment '评论ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime                           null,
    constraint uk_user_comment
        unique (user_id, comment_id),
    constraint uk_user_post
        unique (user_id, post_id),
    constraint like_record_ibfk_1
        foreign key (user_id) references user (id)
            on delete cascade,
    constraint like_record_ibfk_2
        foreign key (post_id) references post (id)
            on delete cascade,
    constraint like_record_ibfk_3
        foreign key (comment_id) references comment (id)
            on delete cascade
)
    comment '点赞记录表';

create index idx_comment_id
    on like_record (comment_id);

create index idx_post_id
    on like_record (post_id);

create index idx_create_time
    on post (create_time);

create index idx_is_anon
    on post (is_anon);

create index idx_likes
    on post (likes);

create index idx_tag
    on post (tag);

create index idx_user_id
    on post (user_id);

create index idx_create_time
    on user (create_time);

create index idx_open_id
    on user (open_id);

create index idx_username
    on user (username);

create definer = root@localhost view comment_detail_view as
select `c`.`id`          AS `id`,
       `c`.`post_id`     AS `post_id`,
       `c`.`user_id`     AS `user_id`,
       `c`.`content`     AS `content`,
       `c`.`likes`       AS `likes`,
       `c`.`create_time` AS `create_time`,
       `c`.`update_time` AS `update_time`,
       `u`.`nickname`    AS `author_nickname`,
       `u`.`avatar`      AS `author_avatar`
from (`school_wall`.`comment` `c` left join `school_wall`.`user` `u` on ((`c`.`user_id` = `u`.`id`)));

-- comment on column comment_detail_view.id not supported: 评论ID

-- comment on column comment_detail_view.post_id not supported: 帖子ID

-- comment on column comment_detail_view.user_id not supported: 评论者ID

-- comment on column comment_detail_view.content not supported: 评论内容

-- comment on column comment_detail_view.likes not supported: 点赞数

-- comment on column comment_detail_view.create_time not supported: 创建时间

-- comment on column comment_detail_view.update_time not supported: 更新时间

-- comment on column comment_detail_view.author_nickname not supported: 用户昵称

-- comment on column comment_detail_view.author_avatar not supported: 头像URL

create definer = root@localhost view post_detail_view as
select `p`.`id`          AS `id`,
       `p`.`user_id`     AS `user_id`,
       `p`.`title`       AS `title`,
       `p`.`content`     AS `content`,
       `p`.`images`      AS `images`,
       `p`.`tag`         AS `tag`,
       `p`.`tag_color`   AS `tag_color`,
       `p`.`is_anon`     AS `is_anon`,
       `p`.`is_private`  AS `is_private`,
       `p`.`likes`       AS `likes`,
       `p`.`comments`    AS `comments`,
       `p`.`create_time` AS `create_time`,
       `p`.`update_time` AS `update_time`,
       `u`.`nickname`    AS `author_nickname`,
       `u`.`avatar`      AS `author_avatar`
from (`school_wall`.`post` `p` left join `school_wall`.`user` `u` on ((`p`.`user_id` = `u`.`id`)));

-- comment on column post_detail_view.id not supported: 帖子ID

-- comment on column post_detail_view.user_id not supported: 作者ID

-- comment on column post_detail_view.title not supported: 帖子标题

-- comment on column post_detail_view.content not supported: 帖子内容

-- comment on column post_detail_view.images not supported: 图片URL列表，JSON格式

-- comment on column post_detail_view.tag not supported: 话题标签

-- comment on column post_detail_view.tag_color not supported: 标签颜色

-- comment on column post_detail_view.is_anon not supported: 是否匿名(0-否,1-是)

-- comment on column post_detail_view.is_private not supported: 是否仅自己可见(0-否,1-是)

-- comment on column post_detail_view.likes not supported: 点赞数

-- comment on column post_detail_view.comments not supported: 评论数

-- comment on column post_detail_view.create_time not supported: 创建时间

-- comment on column post_detail_view.update_time not supported: 更新时间

-- comment on column post_detail_view.author_nickname not supported: 用户昵称

-- comment on column post_detail_view.author_avatar not supported: 头像URL

