package io.roach.stock.util;

import java.sql.Types;

import org.hibernate.dialect.CockroachDialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class CockroachDBDialect extends CockroachDialect {
    public CockroachDBDialect() {
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        return super.getForUpdateNowaitString(); // No FOR SHARE in 1SR
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDBIdentityColumnSupport();
    }

    public static class CockroachDBIdentityColumnSupport extends IdentityColumnSupportImpl {
        @Override
        public boolean supportsIdentityColumns() {
            return true;
        }

        @Override
        public String getIdentitySelectString(String table, String column, int type) {
            switch (type) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.NUMERIC:
                    return "select unique_rowid()";
                default:
                    return "select gen_random_uuid()";
            }
        }

        @Override
        public String getIdentityColumnString(int type) {
            switch (type) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.NUMERIC:
                    return "not null default unique_rowid()";
                default:
                    return "not null default gen_random_uuid()";
            }
        }

        @Override
        public String getIdentityInsertString() {
            return "unordered_unique_rowid()";
        }
    }
}
