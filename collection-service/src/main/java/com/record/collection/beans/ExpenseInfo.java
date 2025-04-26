package com.record.collection.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="EXPENSE_INFO")
public class ExpenseInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private String id;
	@Column(name = "EXPENSE_NAME")
	private String expenseName;
	@Column(name = "EXPENSE_GROUP")
	private String expenseGroup;
	@Column(name = "EXPENSE_TYPE")
	private String expenseType;
	@Column(name = "PAYMENT_METHOD")
	private String paymentMethod;
	@Column(name = "REIMBURSEMENT_REQUIRED")
	private boolean reimbursementRequired;
	@Column(name = "REIMBURSEMENT_METHOD")
	private String reimbursementMethod;
	@Column(name = "REIMBURSED")
	private boolean reimbursed;
	@Column(name = "REPORTED_BY")
	private String reportedBy;
	@Column(name = "STATUS")
	private String status;
	@Column(name="APPROVED_BY")
	private String approvedBy;
	@Column(name = "IMAGE_REFERENCE_PATH")
	private String imageReferencePath;
	@Column(name = "AMOUNT")
	private double amount;
	@Column(name = "EXPENSE_YEAR")
	private int expenseYear;
	@Column(name = "EXPENSE_DATE")
	private Date expenseDate;
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@Column(name = "DESCRIPTION")
	private String description;
	@ManyToOne(optional=false)
	@JoinColumn(name="CREATED_BY", referencedColumnName="USERNAME")
	private Users createdBy;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExpenseName() {
		return expenseName;
	}
	public void setExpenseName(String expenseName) {
		this.expenseName = expenseName;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getExpenseGroup() {
		return expenseGroup;
	}
	public void setExpenseGroup(String expenseGroup) {
		this.expenseGroup = expenseGroup;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public String getReportedBy() {
		return reportedBy;
	}
	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public String getImageReferencePath() {
		return imageReferencePath;
	}
	public void setImageReferencePath(String imageReferencePath) {
		this.imageReferencePath = imageReferencePath;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getExpenseYear() {
		return expenseYear;
	}
	public void setExpenseYear(int expenseYear) {
		this.expenseYear = expenseYear;
	}
	public Date getExpenseDate() {
		return expenseDate;
	}
	public void setExpenseDate(Date expenseDate) {
		this.expenseDate = expenseDate;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Users getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}
	public boolean isReimbursementRequired() {
		return reimbursementRequired;
	}
	public void setReimbursementRequired(boolean reimbursementRequired) {
		this.reimbursementRequired = reimbursementRequired;
	}
	public String getReimbursementMethod() {
		return reimbursementMethod;
	}
	public void setReimbursementMethod(String reimbursementMethod) {
		this.reimbursementMethod = reimbursementMethod;
	}
	public boolean isReimbursed() {
		return reimbursed;
	}
	public void setReimbursed(boolean reimbursed) {
		this.reimbursed = reimbursed;
	}
}
