create table test
(
    id         bigint auto_increment,
    title      varchar(50) null,
    created_at datetime default current_timestamp not null,
    updated_at datetime default current_timestamp null,
    constraint test_pk
        primary key (id)
) comment 'test table';

