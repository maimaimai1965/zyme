
/* This file is generated from Table. Please do not change this file manually !!!
Table location:
https://docs.google.com/spreadsheets/d/1wLUZ4ZV9PyWvqZZf2bICAJqQ7oBalZSn8G5B8eOnxRY/edit?usp=sharing
*/

SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';

/* aa_profile */
insert into aa_profile (profile_id,profile_cd,pswd_life_time, pswd_grace_time, pswd_reuse_time,pswd_reuse_max, login_attemp_max,pswd_lock_time, pswd_len_min, pswd_alphanum,pswd_case) values (1,'Default System Profile','0','0','0','0','0','0',6,'N','N');
insert into aa_profile (profile_id,profile_cd,pswd_life_time, pswd_grace_time, pswd_reuse_time,pswd_reuse_max, login_attemp_max,pswd_lock_time, pswd_len_min, pswd_alphanum,pswd_case) values (2,'Default User Profile','37','3','148','7','5','15',6,'Y','Y');
insert into aa_profile (profile_id,profile_cd,pswd_life_time, pswd_grace_time, pswd_reuse_time,pswd_reuse_max, login_attemp_max,pswd_lock_time, pswd_len_min, pswd_alphanum,pswd_case) values (3,'Default Partner Profile','0','0','0','0','0','0',6,'N','N');

/* aa_role */
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1000,'ROLE_EXEC_SYS','Scheduler Service (Служебный-планировщик)','S',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1001,'ROLE_ACTUATOR','Actuator Service (Служебная информация о сервисах)','S',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1100,'ROLE_LOGIN_ANY_IP','Login from any IP','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1101,'ROLE_LOGIN_OWN_IP','Login from own IP','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1200,'ROLE_MANAGE_USER','Manage Users (Управление пользователями)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1201,'ROLE_CHANGE_USER_STATE','Change Users state (Изменение состояния пользователя)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1300,'ROLE_MANAGE_PARTNER','Manage Partners (Управление партнерами)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1301,'ROLE_MANAGE_PARTNER_AGENT','Manage Partner Agents (Управление агентами партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1302,'ROLE_MANAGE_PARTNER_TMP_LIMIT','Manage Partner Temp Credit Limits (Управление временными кредитными лимитами партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1303,'ROLE_VIEW_PARTNER','View Partners (Просмотр партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1304,'ROLE_SETUP_PARTNER_TMP_LIMIT','Setup Partner Temp Credit Limits (Установка временных кредитных лимитов партнеров)','G','External API');
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1305,'ROLE_REFILL_PARTNER_QUOTA','Refill Partner Quotas (Пополнение квоты партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1306,'ROLE_REFILL_PARTNER_QUOTA_SUBMIT','Request for Partner Quota refill (Запрос на пополнение квоты партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1307,'ROLE_MANAGE_PARTNER_LIMIT','Manage Partner Credit Limits (Управление кредитными лимитами партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1308,'ROLE_VIEW_PARTNER_REFILL','View partner quota refills (Просмотр пополнений квот партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1309,'ROLE_VIEW_PARTNER_QUOTA','View partner quota (Просмотр квот партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1310,'ROLE_MANAGE_PARTNER_SITE','Manage Partner FTP Sites (Управление FTP сайтами партнеров)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1311,'ROLE_MANAGE_TRADE_POINTS','Manage Trade points (Управление торговыми точками)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1312,'ROLE_VIEW_TRADE_POINTS','View Trade points and history (Просмотр торговых точек и их истории)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1313,'ROLE_REFILL_PARTNER_QUOTA_APPR','Approve request for partner quota refill (Подтверждение пополнения квоты партнеров)','G','Access to refill quota functionality');
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1400,'ROLE_CREATE_TRANS','Create TopUp payment (Пополнение счета абонента)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1401,'ROLE_CANCEL_OWN_TRANS','Cancel own TopUp payment (Отмена своего пополнения счета абонента)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1402,'ROLE_CANCEL_ANY_TRANS','Cancel any TopUp payment (Отмена любого пополнения счета абонента)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1403,'ROLE_VIEW_OWN_TRANS','View own TopUp payments, for Partner (Просмотр своих пополнений счетов абонентов, для Партнера)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1404,'ROLE_VIEW_ANY_TRANS','View TopUp payments of any one partner (Просмотр пополнений счетов абонентов любым одним партнером)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1405,'ROLE_VIEW_ALL_TRANS','View TopUp payments of all partners, UI (Просмотр пополнений счетов абонентов всеми партнерами в UI)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1406,'ROLE_VIEW_SUBS_TRANS','View transactions of subscriber (Просмотр транзакций абонента)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1407,'ROLE_MANAGE_MANUAL_PAYMENT','Create manual payments (Создание ручных платежей)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1408,'ROLE_VIEW_MANUAL_PAYMENT','View manual payments (Просмотр ручных платежей)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1409,'ROLE_REPAIR_TRANS','Send to Reprocess errored TopUp payments (Отправка на повторную обработку ошибочных платежей пополнения)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1500,'ROLE_VIEW_ANY_AUDIT_LOG','View audit log (Просмотр журнала аудита)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1501,'ROLE_CHANGE_SYS_PARAM','Change system parameters and directories (Изменения параметров системы и справочников)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1502,'ROLE_MANAGE_MSG_QUEUE','Manage message queues (Управление очередями сообщений)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1503,'ROLE_VIEW_MSG','View message history (Просмотр истории сообщений)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1504,'ROLE_MANAGE_MSG_TMPL','Manage SMS/email templates (Управление SMS/email шаблонами)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1505,'ROLE_VIEW_SUBS_LIST','View Subscriber lists (Просмотр списков абонентов)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1506,'ROLE_MANAGE_SUBS_NOTIF_BL','Manage Subscriber notification Black list (Упраление черным списком уведомлений абонентов)','G','External API');
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1507,'ROLE_MANAGE_SUBS_MFS_LIST','Manage Subscriber MFS lists (Управление MFS списками абонентов)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1508,'ROLE_MANAGE_SUBS_TOPUP_LIST','Manage Subscriber Topup lists (Управление Topup списками абонентов)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1600,'ROLE_USER','JRS Report User (Пользователь системы отчётов)','G','JRS system role');
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1601,'ROLE_ADMINISTRATOR','JRS Report Admin (Администратор системы отчётов)','G','JRS system role');
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1602,'ROLE_NODE_PGW_REPORTS','Bussines Reports User (Пользователь бизнес отчётов)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1603,'ROLE_NODE_ADMIN_REPORTS','Admin Reports User (Пользователь административных отчётов)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1700,'ROLE_MANAGE_JOB','Manage Jobs (Управление периодическими задачами)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1701,'ROLE_RUN_JOB','Start/Stop Jobs (Запуск/остановка периодических задач)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1702,'ROLE_VIEW_JOB','View Jobs (Просмотр периодических задач)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1800,'ROLE_MANAGE_MFS_SERVICE','Manage MFS services (Управление сервисами MFS)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1801,'ROLE_APPROVE_MFS_SERVICE_1','Approve MFS services, Bussiness (Утверждение сервисов MFS, бизнес-пользователь)','G',null);
insert into aa_role (role_id,role_cd,role_name,role_type,note) values (1802,'ROLE_APPROVE_MFS_SERVICE_2','Approve MFS services, AML (Утверждение сервисов MFS, AML)','G',null);

/* aa_user */
insert into aa_user (user_id,profile_id,uname, user_type, password,state, auth_type,short_name, full_name, created_dt,updated_dt,last_pswd_dt,expiration_dt,locked_dt,login_attemp, sms_no, email, note) values (3,1,'exec1','S','c4ca4238a0b923820dcc509a6f75849b','E','I','Executer','Scheduled Task Executor',current_timestamp,null,current_timestamp,null,null,0,null, null, null);
insert into aa_user (user_id,profile_id,uname, user_type, password,state, auth_type,short_name, full_name, created_dt,updated_dt,last_pswd_dt,expiration_dt,locked_dt,login_attemp, sms_no, email, note) values (7,1,'es_aplilayer','E','a7a422697bf9b6da9ddd281b65de8d49','E','I','API-LAYER','History Transactions Service API User',current_timestamp,null,current_timestamp,null,null,0,null, null, null);
insert into aa_user (user_id,profile_id,uname, user_type, password,state, auth_type,short_name, full_name, created_dt,updated_dt,last_pswd_dt,expiration_dt,locked_dt,login_attemp, sms_no, email, note) values (11,1,'asup2tpgw','E','933a45c73f645c3c054957be2e9e49e9','E','I','ASUP','Partner account quota refill API User',current_timestamp,null,current_timestamp,null,null,0,null, null, 'Заявка ОІМ 588667 від 12.10.2022');
insert into aa_user (user_id,profile_id,uname, user_type, password,state, auth_type,short_name, full_name, created_dt,updated_dt,last_pswd_dt,expiration_dt,locked_dt,login_attemp, sms_no, email, note) values (12,1,'actuator','S','c4ca4238a0b923820dcc509a6f75849b','E','I','Actuator','Actuator User',current_timestamp,null,current_timestamp,null,null,0,null, null, null);

/* aa_user_role*/
insert into aa_user_role (user_id, role_id) values (3, 1000);
insert into aa_user_role (user_id, role_id) values (11, 1305);
insert into aa_user_role (user_id, role_id) values (12, 1001);

/* aa_group */
insert into aa_group (group_id,group_cd,group_name,note) values (4,'GROUP_CSR_TEMP_LIMIT','CSR Tmp limit manager',null);
insert into aa_group (group_id,group_cd,group_name,note) values (100,'TPGW_Partners','TPGW_Partners',null);
insert into aa_group (group_id,group_cd,group_name,note) values (101,'TPGW_Billing_Gateway_Administrator','TPGW Billing Gateway Administrators',null);
insert into aa_group (group_id,group_cd,group_name,note) values (102,'TPGW_Topup_Power_Manager','TPGW Topup Power Managers',null);
insert into aa_group (group_id,group_cd,group_name,note) values (103,'TPGW_Topup_Manager','TPGW_APP_Topup_Manager',null);
insert into aa_group (group_id,group_cd,group_name,note) values (104,'TPGW_MFS_Manager','TPGW_APP_MFS_Manager',null);
insert into aa_group (group_id,group_cd,group_name,note) values (105,'TPGW_External_System_CSR_Agent','TPGW_External_System_CSR_Agent',null);
insert into aa_group (group_id,group_cd,group_name,note) values (106,'TPGW_External_System_Limit_Management','TPGW_External_System_Limit_Management',null);
insert into aa_group (group_id,group_cd,group_name,note) values (200,'TPGW_TPGW_Vendor','TPGW_APP_TPGW_Vendor',null);

/* aa_group_member */
insert into aa_group_member (group_id, user_id) values (105, 7);
insert into aa_group_member (group_id, user_id) values (106, 7);

/* aa_group_role */
insert into aa_group_role (group_id, role_id) values (4, 1304);
insert into aa_group_role (group_id, role_id) values (100, 1400);
insert into aa_group_role (group_id, role_id) values (100, 1401);
insert into aa_group_role (group_id, role_id) values (100, 1403);
insert into aa_group_role (group_id, role_id) values (101, 1100);
insert into aa_group_role (group_id, role_id) values (101, 1200);
insert into aa_group_role (group_id, role_id) values (101, 1201);
insert into aa_group_role (group_id, role_id) values (101, 1300);
insert into aa_group_role (group_id, role_id) values (101, 1301);
insert into aa_group_role (group_id, role_id) values (101, 1302);
insert into aa_group_role (group_id, role_id) values (101, 1305);
insert into aa_group_role (group_id, role_id) values (101, 1313);
insert into aa_group_role (group_id, role_id) values (101, 1307);
insert into aa_group_role (group_id, role_id) values (101, 1311);
insert into aa_group_role (group_id, role_id) values (101, 1402);
insert into aa_group_role (group_id, role_id) values (101, 1405);
insert into aa_group_role (group_id, role_id) values (101, 1409);
insert into aa_group_role (group_id, role_id) values (101, 1500);
insert into aa_group_role (group_id, role_id) values (101, 1501);
insert into aa_group_role (group_id, role_id) values (101, 1502);
insert into aa_group_role (group_id, role_id) values (101, 1503);
insert into aa_group_role (group_id, role_id) values (101, 1504);
insert into aa_group_role (group_id, role_id) values (101, 1505);
insert into aa_group_role (group_id, role_id) values (101, 1507);
insert into aa_group_role (group_id, role_id) values (101, 1508);
insert into aa_group_role (group_id, role_id) values (101, 1310);
insert into aa_group_role (group_id, role_id) values (101, 1600);
insert into aa_group_role (group_id, role_id) values (101, 1603);
insert into aa_group_role (group_id, role_id) values (101, 1602);
insert into aa_group_role (group_id, role_id) values (101, 1700);
insert into aa_group_role (group_id, role_id) values (101, 1800);
insert into aa_group_role (group_id, role_id) values (102, 1302);
insert into aa_group_role (group_id, role_id) values (102, 1307);
insert into aa_group_role (group_id, role_id) values (102, 1308);
insert into aa_group_role (group_id, role_id) values (102, 1311);
insert into aa_group_role (group_id, role_id) values (102, 1312);
insert into aa_group_role (group_id, role_id) values (102, 1313);
insert into aa_group_role (group_id, role_id) values (102, 1405);
insert into aa_group_role (group_id, role_id) values (102, 1600);
insert into aa_group_role (group_id, role_id) values (102, 1602);
insert into aa_group_role (group_id, role_id) values (103, 1308);
insert into aa_group_role (group_id, role_id) values (103, 1311);
insert into aa_group_role (group_id, role_id) values (103, 1306);
insert into aa_group_role (group_id, role_id) values (104, 1800);
insert into aa_group_role (group_id, role_id) values (105, 1404);
insert into aa_group_role (group_id, role_id) values (106, 1304);
insert into aa_group_role (group_id, role_id) values (200, 1100);
insert into aa_group_role (group_id, role_id) values (200, 1200);
insert into aa_group_role (group_id, role_id) values (200, 1201);
insert into aa_group_role (group_id, role_id) values (200, 1300);
insert into aa_group_role (group_id, role_id) values (200, 1301);
insert into aa_group_role (group_id, role_id) values (200, 1302);
insert into aa_group_role (group_id, role_id) values (200, 1305);
insert into aa_group_role (group_id, role_id) values (200, 1313);
insert into aa_group_role (group_id, role_id) values (200, 1307);
insert into aa_group_role (group_id, role_id) values (200, 1311);
insert into aa_group_role (group_id, role_id) values (200, 1402);
insert into aa_group_role (group_id, role_id) values (200, 1405);
insert into aa_group_role (group_id, role_id) values (200, 1409);
insert into aa_group_role (group_id, role_id) values (200, 1500);
insert into aa_group_role (group_id, role_id) values (200, 1501);
insert into aa_group_role (group_id, role_id) values (200, 1502);
insert into aa_group_role (group_id, role_id) values (200, 1503);
insert into aa_group_role (group_id, role_id) values (200, 1504);
insert into aa_group_role (group_id, role_id) values (200, 1505);
insert into aa_group_role (group_id, role_id) values (200, 1507);
insert into aa_group_role (group_id, role_id) values (200, 1508);
insert into aa_group_role (group_id, role_id) values (200, 1310);
insert into aa_group_role (group_id, role_id) values (200, 1600);
insert into aa_group_role (group_id, role_id) values (200, 1603);
insert into aa_group_role (group_id, role_id) values (200, 1602);
insert into aa_group_role (group_id, role_id) values (200, 1700);
insert into aa_group_role (group_id, role_id) values (200, 1800);

