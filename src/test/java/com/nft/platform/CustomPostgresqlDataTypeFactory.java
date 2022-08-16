package com.nft.platform;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.TypeCastException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * Class required for DBUnit normal working with json and jsonb types.
 * Need to specify this class in dataTypeFactoryClass property of DBUnit
 */
public class CustomPostgresqlDataTypeFactory extends PostgresqlDataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlTypeName.equals("jsonb") || sqlTypeName.equals("json")) {
            return new JsonbDataType();
        } else {
            return super.createDataType(sqlType, sqlTypeName);
        }
    }

    public static class JsonbDataType extends AbstractDataType {

        public JsonbDataType() {
            super("json", Types.OTHER, String.class, false);
        }

        @Override
        public Object typeCast(Object obj) throws TypeCastException {
            return obj.toString();
        }

        @Override
        public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException {
            return resultSet.getString(column);
        }

        @Override
        public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException, TypeCastException {
            final PGobject jsonObj = new PGobject();
            jsonObj.setType("json");
            jsonObj.setValue(value == null ? null : value.toString());

            statement.setObject(column, jsonObj);
        }
    }
}

