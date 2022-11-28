package com.finance.healthchecker.comm.util;

import com.finance.healthchecker.comm.db.DBConnection;
import com.finance.healthchecker.comm.entity.PaymentsEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class DataControl {

	protected Connection conn;
	protected DBConnection connection = DBConnection.getInstance();

	public DataControl() {
	}

	public  void ReleaseConnection() {
		connection.freeConnection("mysql",conn);
	}


	public ArrayList getPaymentsData(String user_id, String pg_id, String openDttm) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");

		//System.out.println(user_id+":"+pg_provider+":"+pg_id);

		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

		String today = SDF.format(calendar.getTime());

		query.append("\n select pg_id, pg_provider, pay_method, status, currency, count(*), sum(amount)  ")
			 .append("\n from payments p  ")
			 .append("\n where p.user_id = " + user_id)
		     .append("\n and   p.sandbox = 0 ")
			 .append("\n and   p.status in ('paid','failed','cancelled') ")
			 .append("\n and   p.created >= '"+openDttm.substring(0,8)+"000000' ")
		 	 .append("\n and   p.created <= now() ");
		if("ALL".equals(pg_id)==false){
			query.append("\n and p.pg_id = '"+pg_id+"' ");
		}
		query.append("\n group by pg_id, pg_provider, pay_method, status, currency ");

		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setPg_id(rs.getString(1));
				entity.setPg_provider(rs.getString(2));
				entity.setPay_method(rs.getString(3));
				entity.setStatus(rs.getString(4));
				entity.setCurrency(rs.getString(5));
				entity.setPay_cnt(rs.getLong(6));
				entity.setAmount(rs.getLong(7));


				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

	public ArrayList getVbankPaymentsData(String user_id, String pg_provider, String pg_id, String search_date) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");

		query.append("\n select id, imp_uid, merchant_uid, pg_provider, pay_method, TRUNCATE(amount,0), '01', p.paid_at, cancelled_at, failed_at, fail_reason, cancel_reason, pg_tid, '', vbank_num, p.amount ")
				.append("\n from payments p  ")
				.append("\n where p.user_id = " + user_id)
				.append("\n and   p.pg_provider = '"+pg_provider+"'")
				.append("\n and   p.pg_id = '"+pg_id+"'")
				.append("\n and   p.amount > 0 ")
				.append("\n and   p.status = 'paid' ")
				.append("\n and   p.pay_method = 'vbank' ")
				.append("\n and   p.paid_at >= '"+search_date+"000000' and p.paid_at <= '"+search_date+"235959' ")
				.append("\n union all ")
				.append("\n select p.id, p.imp_uid, p.merchant_uid, p.pg_provider, p.pay_method, TRUNCATE(pc.cancel_amount,0), if(pc.cancel_amount = p.amount, '03', '04'), p.paid_at, pc.applied, p.failed_at, p.fail_reason, p.cancel_reason, p.pg_tid, pc.pg_tid, p.vbank_num, p.amount  ")
				.append("\n from payment_cancels pc, payments p   ")
				.append("\n where pc.user_id = p.user_id " )
				.append("\n and   pc.payment_id = p.id ")
				.append("\n and   p.user_id = " + user_id)
				.append("\n and   p.pg_provider = '"+pg_provider+"'")
				.append("\n and   p.pg_id = '"+pg_id+"'")
				.append("\n and   p.status = 'cancelled' ")
				.append("\n and   p.pay_method = 'vbank' ")
				.append("\n and   pc.cancel_amount > 0 ") // exlude delte vbank account
				.append("\n and   pc.created >= date_add('"+search_date+"000000',interval-1 month) and pc.created <= '"+search_date+"235959'  ")
				.append("\n and   pc.applied >= '"+search_date+"000000' and pc.applied <= '"+search_date+"235959' ");


		//LogTrace.debug("card.comm.data.DataControl.getPayData()",query.toString());

		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setPayment_id(rs.getLong(1));
				entity.setImp_uid(rs.getString(2));
				entity.setMerchant_uid(rs.getString(3));
				entity.setPg_provider(rs.getString(4));
				entity.setPay_method(rs.getString(5));
				entity.setAmount(rs.getLong(6));
				entity.setStatus(rs.getString(7));
				entity.setPaid_at(rs.getString(8));
				entity.setCancelled_at(rs.getString(9));
				entity.setFailed_at(rs.getString(10));
				entity.setFail_reason(rs.getString(11));
				entity.setCancel_reason(rs.getString(12));
				entity.setPg_tid(rs.getString(13));
				entity.setCancel_pg_tid(rs.getString(14));
				entity.setVbank_num(rs.getString(15));

				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

	public ArrayList getCancelData(String imp_uid, String pg_tid, long amount) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");
		//System.out.println(imp_uid+":"+pg_tid+":"+amount);

		query.append("\n select p.imp_uid, p.merchant_uid, p.amount, p.pg_tid, pc.cancel_amount, pc.pg_tid ")
			 .append("\n from payments p, payment_cancels pc " )
			 .append("\n where p.id = pc.payment_id ")
			 .append("\n and p.imp_uid = '"+imp_uid+"' ")
			 .append("\n and pc.cancel_amount = "+amount+" ")
			 .append("\n and pc.pg_tid like '%"+pg_tid+"' ");


		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setImp_uid(rs.getString(1));
				entity.setMerchant_uid(rs.getString(2));
				entity.setAmount(rs.getLong(3));
				entity.setPg_tid(rs.getString(4));
				entity.setD_amount(rs.getDouble(5));
				entity.setCancel_pg_tid(rs.getString(6));

				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

	public ArrayList getCancelDataByUserId(String user_id, String pg_tid, double amount, String cancelled_at) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");
		//System.out.println(imp_uid+":"+pg_tid+":"+amount);

		query.append("\n select p.imp_uid, p.merchant_uid, p.amount, p.pg_tid, pc.cancel_amount, pc.pg_tid ")
				.append("\n from payments p, payment_cancels pc " )
				.append("\n where p.id = pc.payment_id ")
				.append("\n and p.user_id = " + user_id)
				.append("\n and pc.cancel_amount = "+amount+" ")
				.append("\n and p.pg_tid = '"+pg_tid+"' ")
				.append("\n and p.cancelled_at > '"+cancelled_at+"' ");


		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setImp_uid(rs.getString(1));
				entity.setMerchant_uid(rs.getString(2));
				entity.setAmount(rs.getLong(3));
				entity.setPg_tid(rs.getString(4));
				entity.setD_amount(rs.getDouble(5));
				entity.setCancel_pg_tid(rs.getString(6));

				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

	public ArrayList getFailData(String user_id, String pg_id, String openDttm) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");

		query.append("\n select fail_reason, count(*) ")
				.append("\n from payments p " )
				.append("\n where p.user_id = " + user_id)
				.append("\n and   p.sandbox = 0")
				.append("\n and   p.created >= '"+openDttm.substring(0,8)+"000000' ")
				.append("\n and   p.created <= now() ")
		        .append("\n and   p.status = 'failed' ");
		if("ALL".equals(pg_id)==false){
			query.append("\n and p.pg_id = '"+pg_id+"' ");
		}
		query.append("\n group by fail_reason ");


		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setFail_reason(rs.getString(1));
				entity.setPay_cnt(rs.getLong(2));

				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

	public ArrayList getFirstPaidData(String user_id, String pg_id ,String openDttm) throws Exception {

		ArrayList retList = new ArrayList();

		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		this.conn = connection.getConnection("mysql");

		query.append("\n select imp_uid, amount, currency, buyer_email, paid_at ")
			 .append("\n from payments p " )
			 .append("\n where p.user_id = " + user_id)
			 .append("\n and   p.sandbox = 0")
			 .append("\n and   p.amount = 0 ")
			 .append("\n and   p.status = 'paid' ")
			 .append("\n and   p.created >= '"+openDttm.substring(0,8)+"000000' ")
			 .append("\n and   p.created <= now() ");
		if("ALL".equals(pg_id)==false){
			query.append("\n and p.pg_id = '"+pg_id+"' ");
		}
		query.append("\n order by created  ");


		try {
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery(query.toString());

			while(rs.next()){

				PaymentsEntity entity = new PaymentsEntity();
				entity.setImp_uid(rs.getString(1));
				entity.setAmount(rs.getLong(2));
				entity.setCurrency(rs.getString(3));
				entity.setBuyer_email(rs.getString(4));
				entity.setPaid_at(rs.getString(5));

				retList.add(entity);
			}

		} catch (Exception e) {
			//LogTrace.trace("finance.portone.comm.util.DataControl.getPaymentsData",e.toString());
			e.printStackTrace();
		} finally {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			this.ReleaseConnection();
		}
		return retList;
	}

}
