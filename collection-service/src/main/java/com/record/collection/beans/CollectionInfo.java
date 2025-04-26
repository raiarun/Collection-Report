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
@Table(name="COLLECTION_INFO")
public class CollectionInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private String id;
	@Column(name = "CONTRIBUTION_TYPE")
	private String contributionType;
	@ManyToOne(optional=false)
	@JoinColumn(name = "CONTRIBUTOR", referencedColumnName="CONTRIBUTOR_NAME")
	private Contributor contributor;
	@Column(name = "COLLECTION_TYPE")
	private String collectionType;
	@Column(name = "FUND_PAYMENT_METHOD")
	private String fundPaymentMethod;
	@Column(name = "AMOUNT")
	private double amount;
	@Column(name = "COLLECTION_YEAR")
	private int collectionYear;
	@Column(name = "COLLECTION_MONTH")
	private String collectionMonth;
	@Column(name = "COLLECTION_DATE")
	private Date collectionDateTime;
	@Column(name = "BUDGET_CATEGORY_TYPE")
	private String budgetCategoryType;	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@Column(name = "NOTE")
	private String note;
	@Column(name = "REPORTED_BY")
	private String reportedBy;
	@ManyToOne(optional=false)
	@JoinColumn(name="CREATED_BY", referencedColumnName="USERNAME")
	private Users createdBy;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContributionType() {
		return contributionType;
	}

	public void setContributionType(String contributionType) {
		this.contributionType = contributionType;
	}

	public Contributor getContributor() {
		return contributor;
	}

	public void setContributor(Contributor contributor) {
		this.contributor = contributor;
	}

	public String getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}
	
	public String getFundPaymentMethod() {
		return fundPaymentMethod;
	}

	public void setFundPaymentMethod(String fundPaymentMethod) {
		this.fundPaymentMethod = fundPaymentMethod;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getCollectionYear() {
		return collectionYear;
	}

	public void setCollectionYear(int collectionYear) {
		this.collectionYear = collectionYear;
	}

	public String getCollectionMonth() {
		return collectionMonth;
	}

	public void setCollectionMonth(String collectionMonth) {
		this.collectionMonth = collectionMonth;
	}

	public Date getCollectionDateTime() {
		return collectionDateTime;
	}

	public void setCollectionDateTime(Date collectionDateTime) {
		this.collectionDateTime = collectionDateTime;
	}

	public String getBudgetCategoryType() {
		return budgetCategoryType;
	}

	public void setBudgetCategoryType(String budgetCategoryType) {
		this.budgetCategoryType = budgetCategoryType;
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
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}

	public Users getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "CollectionInfo [id=" + id + ", contributionType=" + contributionType + ", collectionType="
				+ collectionType + ", amount=" + amount + ", collectionYear=" + collectionYear + ", collectionMonth="
				+ collectionMonth + ", collectionDateTime=" + collectionDateTime + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", note=" + note + ", reportedBy=" + reportedBy + "]";
	}

}
