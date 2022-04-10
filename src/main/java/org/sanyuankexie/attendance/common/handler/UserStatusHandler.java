package org.sanyuankexie.attendance.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.sanyuankexie.attendance.common.DTO.UserStatusEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(UserStatusEnum.class)
@MappedJdbcTypes(JdbcType.INTEGER)
public class UserStatusHandler  extends BaseTypeHandler<UserStatusEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, UserStatusEnum userStatusEnum, JdbcType jdbcType) throws SQLException {
         preparedStatement.setInt(i,userStatusEnum.getStatus());
    }
    @Override
    public UserStatusEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        return UserStatusEnum.getStatus(Integer.parseInt(string));
    }
    @Override
    public UserStatusEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return UserStatusEnum.getStatus(resultSet.getInt(i));
    }
    @Override
    public UserStatusEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return UserStatusEnum.getStatus(callableStatement.getInt(i));
    }
}
