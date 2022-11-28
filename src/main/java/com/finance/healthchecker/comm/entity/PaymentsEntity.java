package com.finance.healthchecker.comm.entity;

public class PaymentsEntity {
	
	String imp_uid;
	String pg_tid;
	String cancel_pg_tid;
	String created;
	String cancelled_at;
	String paid_at;
	String failed_at;
	String status;
	String pay_method;
	String pg_provider;
	String merchant_uid;
	String fail_reason;
	String cancel_reason;
	String apply_num;
	String pg_id;
	String vbank_num;
	String currency;
	String buyer_email;
	Long amount;
	Double d_amount;
	Long cancel_amount;
	Long org_amount;
	Long payment_id;
	Long pay_cnt;

	public String getBuyer_email() {
		return buyer_email;
	}

	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getPay_cnt() {
		return pay_cnt;
	}

	public void setPay_cnt(Long pay_cnt) {
		this.pay_cnt = pay_cnt;
	}

	public Long getOrg_amount() {
		return org_amount;
	}

	public void setOrg_amount(Long org_amount) {
		this.org_amount = org_amount;
	}

	public Double getD_amount() {
		return d_amount;
	}

	public void setD_amount(Double d_amount) {
		this.d_amount = d_amount;
	}

	public String getVbank_num() {
		return vbank_num;
	}

	public void setVbank_num(String vbank_num) {
		this.vbank_num = vbank_num;
	}

	public String getPg_id() {
		return pg_id;
	}

	public void setPg_id(String pg_id) {
		this.pg_id = pg_id;
	}

	public String getMerchant_uid() {
		return merchant_uid;
	}

	public void setMerchant_uid(String merchant_uid) {
		this.merchant_uid = merchant_uid;
	}

	public String getImp_uid() {
		return imp_uid;
	}

	public void setImp_uid(String imp_uid) {
		this.imp_uid = imp_uid;
	}

	public String getPg_tid() {
		return pg_tid;
	}

	public void setPg_tid(String pg_tid) {
		this.pg_tid = pg_tid;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCancelled_at() {
		return cancelled_at;
	}

	public void setCancelled_at(String cancelled_at) {
		this.cancelled_at = cancelled_at;
	}

	public String getPaid_at() {
		return paid_at;
	}

	public void setPaid_at(String paid_at) {
		this.paid_at = paid_at;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public String getPg_provider() {
		return pg_provider;
	}

	public void setPg_provider(String pg_provider) {
		this.pg_provider = pg_provider;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getCancel_amount() {
		return cancel_amount;
	}

	public void setCancel_amount(Long cancel_amount) {
		this.cancel_amount = cancel_amount;
	}

	public Long getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(Long id) {
		this.payment_id = id;
	}

	public String getFailed_at() {
		return failed_at;
	}

	public void setFailed_at(String failed_at) {
		this.failed_at = failed_at;
	}

	public String getFail_reason() {
		return fail_reason;
	}

	public void setFail_reason(String fail_reason) {
		this.fail_reason = fail_reason;
	}

	public String getCancel_reason() {
		return cancel_reason;
	}

	public void setCancel_reason(String cancel_reason) {
		this.cancel_reason = cancel_reason;
	}

	public String getApply_num() {
		return apply_num;
	}

	public void setApply_num(String apply_num) {
		this.apply_num = apply_num;
	}

	public String getCancel_pg_tid() {
		return cancel_pg_tid;
	}

	public void setCancel_pg_tid(String cancel_pg_tid) {
		this.cancel_pg_tid = cancel_pg_tid;
	}


}
