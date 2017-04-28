package com.yourong.common.pageable;

public class SQLServiceDialect extends Dialect{
	@Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }
    
    public String getCountString(String sql) {
		sql = sql.toLowerCase();
		if (sql.matches("top"))
			return "select count(1) from (" + sql + ") tmp_count";
		else {
			int orderIndex = sql.indexOf("order");
			return "select count(1) from (" + sql.substring(0, orderIndex)
					+ ") tmp_count";
		}
	}

   
    @Override
    public String getLimitString(String querySqlString, int offset,String offsetPlaceholder, int limit, String limitPlaceholder) {
        StringBuffer pagingBuilder = new StringBuffer();
        String orderby = getOrderByPart(querySqlString);
        String distinctStr = "";

        String loweredString = querySqlString.toLowerCase();
        String sqlPartString = querySqlString;
        if (loweredString.trim().startsWith("select")) {
            int index = 6;
            if (loweredString.startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);

        // if no ORDER BY is specified use fake ORDER BY field to avoid errors
        if (orderby == null || orderby.length() == 0) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }

        StringBuffer result = new StringBuffer();
        result.append("WITH query AS (SELECT ")
                .append(distinctStr)
                .append("TOP 100 PERCENT ")
                .append(" ROW_NUMBER() OVER (")
                .append(orderby)
                .append(") as __row_number__, ")
                .append(pagingBuilder)
                .append(") SELECT * FROM query WHERE __row_number__ BETWEEN ")
                .append(offset+1).append(" AND ").append(offset+limit)
                .append(" ORDER BY __row_number__");

        return result.toString();
    }

    static String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.indexOf("order by");
        if (orderByIndex != -1) {
            // if we find a new "order by" then we need to ignore
            // the previous one since it was probably used for a subquery
            return sql.substring(orderByIndex);
        } else {
            return "";
        }
    }
}
