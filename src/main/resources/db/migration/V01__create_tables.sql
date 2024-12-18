create table tb_application (
    id_application serial not null,
    ds_name varchar(255) not null,
    ds_token varchar(255) not null,
    ds_user varchar(255) not null,
    ds_password varchar(255) not null,
    dt_create timestamp not null,
    dt_update timestamp
);
alter table tb_application add constraint tb_application_pk primary key (id_application);
create unique index tb_application_ak01 on tb_application (ds_name);
create unique index tb_application_ak02 on tb_application (ds_user);
create unique index tb_application_ak03 on tb_application (ds_token);

create table tb_profile (
    id_profile serial not null,
    id_application int not null,
    ds_name varchar(255) not null,
    ds_group varchar(255) not null,
    dt_create timestamp not null,
    dt_update timestamp
);
alter table tb_profile add constraint tb_profile_pk primary key (id_profile);
alter table tb_profile add constraint tb_profile_application foreign key (id_application) references tb_application (id_application);
create unique index tb_profile_ak01 on tb_profile (id_application, ds_name);

create table tb_resource (
    id_resource serial not null,
    ds_name varchar(255) not null,
    dt_create timestamp not null,
    dt_update timestamp
);
alter table tb_resource add constraint tb_resource_pk primary key (id_resource);
create unique index tb_resource_ak01 on tb_resource (ds_name);

create table tb_action (
    id_action serial not null,
    ds_name varchar(255) not null,
    dt_create timestamp not null,
    dt_update timestamp
);
alter table tb_action add constraint tb_action_pk primary key (id_action);
create unique index tb_action_ak01 on tb_action (ds_name);

create table tb_profile_resource_action (
    id_profile_resource_action serial not null,
    id_profile int not null,
    id_resource int not null,
    id_action int not null
);
alter table tb_profile_resource_action add constraint tb_profile_resource_action_pk primary key (id_profile_resource_action);
alter table tb_profile_resource_action add constraint tb_profile_resource_action_profile foreign key (id_profile) references tb_profile (id_profile);
alter table tb_profile_resource_action add constraint tb_profile_resource_action_resource foreign key (id_resource) references tb_resource (id_resource);
alter table tb_profile_resource_action add constraint tb_profile_resource_action_action foreign key (id_action) references tb_action (id_action);