package com.shs.framework.core;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import com.shs.framework.dao.BaseDAO;
import com.shs.framework.dao.DbManager;
import com.shs.framework.dao.BaseDAO.ConnectionOperator;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @version 0.1
 * @author chyxion
 * @describe: Ext抽象Store，供grid等查询使用
 * @date created: Dec 6, 2012 10:56:00 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 */
public abstract class ExtStore {
	private BaseDAO dao;
	/**
	 * 当前拥有的数据
	 */
	private JSONArray data;
	/**
	 * 附加数据
	 */
	private Object extraData;
	/**
	 * 总数据，不是data.length()，grid分页时候用
	 */
	private Object total;
	/**
	 * 查询SQL
	 */
    private StringBuffer sbSQL;
    /**
     * 查询传入值
     */
    private List<Object> values = new LinkedList<Object>();
    /**
     * 升序
     */
    protected final String ASC = "asc";
    /**
     * 降序
     */
    protected final String DESC = "desc";
    /**
     * 排序列
     */
    private String orderCol;
    /**
     * 排序方向
     */
    private String direction = ASC;
    /**
     * 参数对象
     */
    protected final Params params;
    /**
     * 查询的JSON属性小写
     */
    private boolean lowerCase = DbManager.LOWERCASE;
    /**
     * 数据库连接
     */
    protected Connection dbConnection;
    /**
     * 条件
     */
	private StringBuffer sbCondition = new StringBuffer();
    /**
     * store 数据
     * @return JSONArray
     */
	public JSONArray getData() {
		return data;
	}
	/**
	 * store总数，分页使用
	 * @return 总数
	 */
	public Object getTotal() {
		return total;
	}
	/**
	 * 构造函数，供继承使用
	 * @param params
	 * @throws Exception
	 */
    protected ExtStore(BaseDAO dao, final Params params) {
    	this.dao = dao;
        this.params = params;
        this.dao.execute(new ConnectionOperator() {
            @Override
            public void run() throws Exception {
                ExtStore.this.dbConnection = dbConnection;
                ExtStore.this.run();
                if (sbCondition.length() > 0) {
                	sbSQL.append(sbCondition);
                }
                data = findJSONArrayPage(
                		ExtStore.this.lowerCase,
                		getOrderCol(), 
                		direction, 
                		params.getInt("start", 0), 
                		params.getInt("limit", 0), 
                		sbSQL.toString(),
                		values.toArray());
                total = total();
            }
        });
    }
    protected String getOrderCol() {
    	if (orderCol == null) {
    		throw new RuntimeException("ExtStore未指定order by列，请调用orderBy方法！");
    	}
		return orderCol;
	}
	public ExtStore setSQL(String strSQL) {
    	return setSQL(new StringBuffer(strSQL));
    }
    public ExtStore setSQL(StringBuffer sbSQL) {
    	this.sbSQL = sbSQL;
    	return this;
    }
    public ExtStore orderBy(String oc) {
    	orderCol = oc;
    	return this;
    }
    public ExtStore orderBy(String oc, String dir) {
    	this.direction = dir;
    	orderCol = oc;
    	return this;
    }
    protected void setDirection(String od) {
    	direction = od;
    }
    public ExtStore lowerCase() {
    	lowerCase = true;
    	return this;
    }
    public ExtStore upperCase() {
    	lowerCase = false;
    	return this;
    }
    public ExtStore asc() {
    	direction = ASC;
    	return this;
    }
    public ExtStore desc() {
    	direction = DESC;
    	return this;
    }
    /**
     * 总数，分页时候改写用，
     * @throws Exception
     */
    private Object total() throws Exception {
    	String strSQL = sbSQL.toString();
    	strSQL = "select count(1) " + strSQL.substring(StringUtils.indexOfIgnoreCase(strSQL, " from "));
    	return dao.findObj(dbConnection, strSQL, values.toArray());
    };
    /**
     * 生成查询SQL
     * @throws Exception
     */
    protected abstract void run() throws Exception;
    /**
     * 添加查询传入值
     * @param v
     */
    public ExtStore addValue(Object v) {
    	values.add(v);
    	return this;
	}
    /**
     * 增加and查询条件，
     * @param column 列名
     * @param op 操作符，如: =, <=, <>
     * @param param 参数名
     * @return
     */
    public ExtStore and(String column, String op, String param) {
    	
    	String v = params.get(param);
		if (!StringUtils.isEmpty(v)) {
			sbCondition.append(" and ")
			.append(column)
			.append(" ")
			.append(op)
			.append(" ? ");
			values.add(v);
		} 
    	return this;
	}
    public ExtStore where(String column, String op, String param) {
    	String v = params.get(param);
		if (!StringUtils.isEmpty(v)) {
			sbCondition.append(" where ")
			.append(column)
			.append(" ")
			.append(op)
			.append(" ? ");
			values.add(v);
		} 
    	return this;
    }
    /**
     * or条件
     * @param column 列名
     * @param op 操作符，如：=, >=, <>
     * @param param 参数名
     * @return
     */
    public ExtStore or(String column, String op, String param) {
    	String v = params.get(param);
		if (!StringUtils.isEmpty(v)) {
			sbCondition.append(" or ")
			.append(column)
			.append(" ")
			.append(op)
			.append(" ? ");
			values.add(v);
		} 
    	return this;
	}
    @Override
    public String toString() {
		try {
			// 如果提供总数，输出总数
			return new JSONObject()
						.put("success", true)
						.put("data", data)
						.put("total", total).toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
	public void setExtraData(Object extraData) {
		this.extraData = extraData;
	}
	public Object getExtraData() {
		return extraData;
	}
}
