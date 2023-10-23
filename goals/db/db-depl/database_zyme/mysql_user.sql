create user if not exists 'zyme' identified by 'zyme';
grant select, insert, update, delete, execute on ${db.database}.* to '${db.zyme.user}'@'%';
grant all on jrs.* to '${db.zyme.user}'@'%';