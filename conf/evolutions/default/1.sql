# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table email_model (
  id                            bigint not null,
  email                         varchar(255),
  usuario_id                    bigint,
  constraint pk_email_model primary key (id)
);
create sequence email_model_seq;

create table password_model (
  id                            bigint not null,
  contrasenia                   varchar(255),
  constraint pk_password_model primary key (id)
);
create sequence password_model_seq;

create table tags_model (
  id                            bigint not null,
  tag                           varchar(255),
  constraint pk_tags_model primary key (id)
);
create sequence tags_model_seq;

create table tarea_model (
  id                            bigint not null,
  titulo                        varchar(255),
  descripcion                   varchar(255),
  fechacreacion                 timestamp,
  fechafin                      timestamp,
  constraint pk_tarea_model primary key (id)
);
create sequence tarea_model_seq;

create table tarea_model_tags_model (
  tarea_model_id                bigint not null,
  tags_model_id                 bigint not null,
  constraint pk_tarea_model_tags_model primary key (tarea_model_id,tags_model_id)
);

create table usuario_model (
  id                            bigint not null,
  username                      varchar(255),
  nombre                        varchar(255),
  edad                          integer,
  telefono                      varchar(255),
  pass_id                       bigint,
  token                         varchar(255),
  constraint uq_usuario_model_pass_id unique (pass_id),
  constraint pk_usuario_model primary key (id)
);
create sequence usuario_model_seq;

create table usuario_model_tarea_model (
  usuario_model_id              bigint not null,
  tarea_model_id                bigint not null,
  constraint pk_usuario_model_tarea_model primary key (usuario_model_id,tarea_model_id)
);

alter table email_model add constraint fk_email_model_usuario_id foreign key (usuario_id) references usuario_model (id) on delete restrict on update restrict;
create index ix_email_model_usuario_id on email_model (usuario_id);

alter table tarea_model_tags_model add constraint fk_tarea_model_tags_model_tarea_model foreign key (tarea_model_id) references tarea_model (id) on delete restrict on update restrict;
create index ix_tarea_model_tags_model_tarea_model on tarea_model_tags_model (tarea_model_id);

alter table tarea_model_tags_model add constraint fk_tarea_model_tags_model_tags_model foreign key (tags_model_id) references tags_model (id) on delete restrict on update restrict;
create index ix_tarea_model_tags_model_tags_model on tarea_model_tags_model (tags_model_id);

alter table usuario_model add constraint fk_usuario_model_pass_id foreign key (pass_id) references password_model (id) on delete restrict on update restrict;

alter table usuario_model_tarea_model add constraint fk_usuario_model_tarea_model_usuario_model foreign key (usuario_model_id) references usuario_model (id) on delete restrict on update restrict;
create index ix_usuario_model_tarea_model_usuario_model on usuario_model_tarea_model (usuario_model_id);

alter table usuario_model_tarea_model add constraint fk_usuario_model_tarea_model_tarea_model foreign key (tarea_model_id) references tarea_model (id) on delete restrict on update restrict;
create index ix_usuario_model_tarea_model_tarea_model on usuario_model_tarea_model (tarea_model_id);


# --- !Downs

alter table email_model drop constraint if exists fk_email_model_usuario_id;
drop index if exists ix_email_model_usuario_id;

alter table tarea_model_tags_model drop constraint if exists fk_tarea_model_tags_model_tarea_model;
drop index if exists ix_tarea_model_tags_model_tarea_model;

alter table tarea_model_tags_model drop constraint if exists fk_tarea_model_tags_model_tags_model;
drop index if exists ix_tarea_model_tags_model_tags_model;

alter table usuario_model drop constraint if exists fk_usuario_model_pass_id;

alter table usuario_model_tarea_model drop constraint if exists fk_usuario_model_tarea_model_usuario_model;
drop index if exists ix_usuario_model_tarea_model_usuario_model;

alter table usuario_model_tarea_model drop constraint if exists fk_usuario_model_tarea_model_tarea_model;
drop index if exists ix_usuario_model_tarea_model_tarea_model;

drop table if exists email_model;
drop sequence if exists email_model_seq;

drop table if exists password_model;
drop sequence if exists password_model_seq;

drop table if exists tags_model;
drop sequence if exists tags_model_seq;

drop table if exists tarea_model;
drop sequence if exists tarea_model_seq;

drop table if exists tarea_model_tags_model;

drop table if exists usuario_model;
drop sequence if exists usuario_model_seq;

drop table if exists usuario_model_tarea_model;

