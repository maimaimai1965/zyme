create user if not exists ${db.schema.user} identified by ${db.schema.pass};
grant select, insert, update, delete, execute on ${db.schema}.* to '${db.schema.user}'@'%';
# grant all on jrs.* to '${db.schema.user}'@'%';