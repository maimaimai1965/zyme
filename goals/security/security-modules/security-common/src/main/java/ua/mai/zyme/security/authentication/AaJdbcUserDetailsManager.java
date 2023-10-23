package ua.mai.zyme.security.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

public class AaJdbcUserDetailsManager extends JdbcUserDetailsManager {

//    public static final String AA_CREATE_USER_SQL = "insert into users (username, password, enabled) values (?,?,?)";
//    public static final String AA_DELETE_USER_SQL = "delete from users where username = ?";
//    public static final String AA_UPDATE_USER_SQL = "update users set password = ?, enabled = ? where username = ?";
//    public static final String AA_INSERT_AUTHORITY_SQL = "insert into authorities (username, authority) values (?,?)";
//    public static final String AA_DELETE_USER_AUTHORITIES_SQL = "delete from authorities where username = ?";
//    public static final String AA_USER_EXISTS_SQL = "select username from users where username = ?";
//    public static final String AA_CHANGE_PASSWORD_SQL = "update users set password = ? where username = ?";
//    public static final String AA_FIND_GROUPS_SQL = "select group_name from groups";
//    public static final String AA_FIND_USERS_IN_GROUP_SQL = "select username from group_members gm, groups g where gm.group_id = g.id and g.group_name = ?";
//    public static final String AA_INSERT_GROUP_SQL = "insert into groups (group_name) values (?)";
//    public static final String AA_FIND_GROUP_ID_SQL = "select id from groups where group_name = ?";
//    public static final String AA_INSERT_GROUP_AUTHORITY_SQL = "insert into group_authorities (group_id, authority) values (?,?)";
//    public static final String AA_DELETE_GROUP_SQL = "delete from groups where id = ?";
//    public static final String AA_DELETE_GROUP_AUTHORITIES_SQL = "delete from group_authorities where group_id = ?";
//    public static final String AA_DELETE_GROUP_MEMBERS_SQL = "delete from group_members where group_id = ?";
//    public static final String AA_RENAME_GROUP_SQL = "update groups set group_name = ? where group_name = ?";
//    public static final String AA_INSERT_GROUP_MEMBER_SQL = "insert into group_members (group_id, username) values (?,?)";
//    public static final String AA_DELETE_GROUP_MEMBER_SQL = "delete from group_members where group_id = ? and username = ?";
//    public static final String AA_GROUP_AUTHORITIES_QUERY_SQL = "select g.id, g.group_name, ga.authority from groups g, group_authorities ga where g.group_name = ? and g.id = ga.group_id ";
//    public static final String AA_DELETE_GROUP_AUTHORITY_SQL = "delete from group_authorities where group_id = ? and authority = ?";
    protected final Log logger = LogFactory.getLog(this.getClass());

    private final String usersByUsernameQuery =
            "select uname as username, password, 1 as enabled from aa_user where uname = ?";
    private final String authoritiesByUserQuery =
        """
            select u.uname as username, r.role_cd as authority
            from aa_user u
            join aa_user_role as ur  on ur.user_id = u.user_id
            join aa_role as r  on r.role_id = ur.role_id
            where u.uname = ?
        """;
    private String groupAuthoritiesByUsernameQuery =
        """
            select g.group_id, g.group_cd, r.role_cd as authority
            from aa_user u
            join aa_group_member as gm  on gm.user_id = u.user_id
            join aa_group_role   as gr  on gr.group_id = gm.group_id
            join aa_group        as g   on g.group_id = gm.group_id
            join aa_role         as r   on r.role_id = gr.role_id
            where u.uname = ?
        """;

//    private String createUserSql = "insert into users (username, password, enabled) values (?,?,?)";
//    private String deleteUserSql = "delete from users where username = ?";
//    private String updateUserSql = "update users set password = ?, enabled = ? where username = ?";
//    private String createAuthoritySql = "insert into authorities (username, authority) values (?,?)";
//    private String deleteUserAuthoritiesSql = "delete from authorities where username = ?";
//    private String userExistsSql = "select username from users where username = ?";
//    private String changePasswordSql = "update users set password = ? where username = ?";
//    private String findAllGroupsSql = "select group_name from groups";
//    private String findUsersInGroupSql = "select username from group_members gm, groups g where gm.group_id = g.id and g.group_name = ?";
//    private String insertGroupSql = "insert into groups (group_name) values (?)";
//    private String findGroupIdSql = "select id from groups where group_name = ?";
//    private String insertGroupAuthoritySql = "insert into group_authorities (group_id, authority) values (?,?)";
//    private String deleteGroupSql = "delete from groups where id = ?";
//    private String deleteGroupAuthoritiesSql = "delete from group_authorities where group_id = ?";
//    private String deleteGroupMembersSql = "delete from group_members where group_id = ?";
//    private String renameGroupSql = "update groups set group_name = ? where group_name = ?";
//    private String insertGroupMemberSql = "insert into group_members (group_id, username) values (?,?)";
//    private String deleteGroupMemberSql = "delete from group_members where group_id = ? and username = ?";
//    private String groupAuthoritiesSql = "select g.id, g.group_name, ga.authority from groups g, group_authorities ga where g.group_name = ? and g.id = ga.group_id ";
//    private String deleteGroupAuthoritySql = "delete from group_authorities where group_id = ? and authority = ?";

    public AaJdbcUserDetailsManager(DataSource dataSource) {
        super(dataSource);
        setEnableGroups(true);
        setUsersByUsernameQuery(usersByUsernameQuery);
        setAuthoritiesByUsernameQuery(authoritiesByUserQuery);
        setGroupAuthoritiesByUsernameQuery(groupAuthoritiesByUsernameQuery);
    }

}
