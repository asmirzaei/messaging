create table if not exists "user"
(
    id                  varchar(36) not null constraint user_pkey primary key,
    nick_name           varchar(50) not null unique
);

create table if not exists message
(
    id                  varchar(36) not null constraint message_pkey primary key,
    message             text not null,
    sender_user_id      varchar(36) not null constraint message_sender_user_id_fkey     references "user" (id),
    receiver_user_id    varchar(36) not null constraint message_receiver_user_id_fkey   references "user" (id),
    received_date_time  timestamp(6) not null
);
create index if not exists message_sender_user_id_index on message (sender_user_id);
create index if not exists message_receiver_user_id_index on message (receiver_user_id);
