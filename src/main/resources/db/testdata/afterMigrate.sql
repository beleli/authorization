insert into tb_application (
    ds_name,
    ds_token,
    ds_user,
    ds_password,
    dt_create
)
select 'blitech-authorization', 'a39dc1b5-53ad-4a99-ae10-0e5572fe173b', 'authorization-user', '$2a$10$.IwnX1InDcGjL54Xk9jdzuCW.BPddZ3LywmGJ39xh/QMftXKi1uoW', now()
where not exists(select ds_name from tb_application where ds_name = 'blitech-authorization');

insert into tb_profile (
    id_application,
    ds_name,
    ds_group,
    dt_create
)
select 1, 'admin', 'adGroup_blitech-authorization_admin', now()
where not exists(select ds_name from tb_profile where ds_name = 'admin');

insert into tb_resource (
    ds_name,
    dt_create
)
select 'applications', now()
where not exists(select ds_name from tb_resource where ds_name = 'applications');

insert into tb_resource (
    ds_name,
    dt_create
)
select 'actions', now()
where not exists(select ds_name from tb_resource where ds_name = 'actions');

insert into tb_resource (
    ds_name,
    dt_create
)
select 'profiles', now()
where not exists(select ds_name from tb_resource where ds_name = 'profiles');

insert into tb_resource (
    ds_name,
    dt_create
)
select 'resources', now()
where not exists(select ds_name from tb_resource where ds_name = 'resources');

insert into tb_action (
    ds_name,
    dt_create
)
select 'read', now()
where not exists(select ds_name from tb_action where ds_name = 'read');

insert into tb_action (
    ds_name,
    dt_create
)
select 'write', now()
where not exists(select ds_name from tb_action where ds_name = 'write');

insert into tb_profile (
    id_application,
    ds_name,
    ds_group,
    dt_create
)
select 1, 'admin', 'adGroup_blitech-authorization_admin', now()
where not exists(select ds_name from tb_profile where ds_name = 'admin');

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 1, 1
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 1 and id_action = 1);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 1, 2
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 1 and id_action = 2);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 2, 1
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 2 and id_action = 1);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 2, 2
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 2 and id_action = 2);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 3, 1
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 3 and id_action = 1);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 3, 2
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 3 and id_action = 2);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 4, 1
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 4 and id_action = 1);

insert into tb_profile_resource_action (
    id_profile,
    id_resource,
    id_action
)
select 1, 4, 2
where not exists(select id_profile_resource_action from tb_profile_resource_action where id_profile = 1 and id_resource = 4 and id_action = 2);