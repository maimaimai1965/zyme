create user if not exists 'zyme02' identified by 'zyme02';
grant select, insert, update, delete, execute on ${db.database}.* to '${db.zyme.user}'@'%';
grant all on jrs.* to '${db.zyme.user}'@'%';